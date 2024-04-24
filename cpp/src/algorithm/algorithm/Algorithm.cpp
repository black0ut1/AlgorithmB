#include "Algorithm.h"
#include "../../data/priority_queue/PriorityQueue.h"
#include "../../data/queue/Queue.h"

#include <utility>
#include <limits>
#include <queue>
#include <cmath>
#include <algorithm>
#include <iostream>

Algorithm::Algorithm(Network &map, const Matrix &odMatrix, double relativeGap, int iterations)
		: map(map), odMatrix(odMatrix), relativeGap(relativeGap),
		  iterations(iterations), bushes(map.zones, Bush((int) map.getArcs().size())) {}

std::vector<Network::Arc> Algorithm::start() {
	map.updateCosts();
	for (int root = 0; root < map.zones; ++root)
		createBush(root);

	auto &arcs = map.getArcs();
	for (const auto &bush: bushes)
		for (auto &arc: arcs)
			arc.addFlow(bush.getArcFlow(arc.index));
	map.updateCosts();


	std::cout << "===================================" << std::endl;
	std::cout << "Static traffic assignment algorithm B - ";
	std::cout << "Sequential version" << std::endl;
	std::cout << "Max. iterations: " << iterations << std::endl;
	if (relativeGap != 0)
		std::cout << "Relative gap: " << relativeGap << std::endl;
	std::cout << "Starting objective function: " << map.objectiveFunction() << std::endl;
	std::cout << "===================================" << std::endl;


	double currentRelativeGap = std::numeric_limits<double>::infinity();
	double maxLB = -std::numeric_limits<double>::infinity();
	int currentIteration = 0;
	while ((currentIteration < iterations && relativeGap == 0) ||
		   (currentIteration < iterations && currentRelativeGap > relativeGap)) {
		std::cout << "Iteration " << currentIteration << std::endl;

		for (auto &bush: bushes) {
			improveBush(bush);

			const auto [minTree, maxTree] = getTrees(bush);

			for (int node = 0; node < map.nodes; ++node) {
				int lca = LCA(minTree, maxTree, node);
				if (lca == -1)
					continue;

				double deltaX = findFlowDelta(minTree, maxTree, bush, node, lca);
				if (deltaX == 0)
					continue;

				shiftFlows(minTree, maxTree, bush, node, lca, deltaX);
			}

			removeUnusedArcs(bush, minTree);
			map.updateCosts();
		}

		double of = map.objectiveFunction();
		std::cout << "Objective function: " << of << std::endl;

		if (relativeGap != 0) {
			double gap = map.gap(odMatrix);
			double lb = of + gap;
			if (lb > maxLB)
				maxLB = lb;

			currentRelativeGap = -gap / std::abs(maxLB);

			std::cout << "Gap: " << gap << std::endl;
			std::cout << "Relative gap: " << currentRelativeGap << std::endl;
		}

		std::cout << "-----------------------------------" << std::endl;
		currentIteration++;
	}

	return map.getArcs();
}

void Algorithm::createBush(int root) {
	bushes[root].root = root;

	auto [distances, minTree] = map.minTree(root);

	for (const auto &arc: map.getArcs()) {
		if (distances[arc.startNode] < distances[arc.endNode])
			bushes[root].addArc(arc.index);
	}

	for (int node = 0; node < map.zones; ++node) {
		double trips = odMatrix(root, node);
		if (trips == 0)
			continue;

		auto arc = minTree[node];
		while (arc != nullptr) {
			bushes[root].addFlow(arc->index, trips);

			arc = minTree[arc->startNode];
		}
	}
}

void Algorithm::improveBush(Bush &bush) {
	std::vector<double> maxDistance = getMaxDistance(bush);

	for (const auto &arc: map.getArcs()) {
		if (std::isinf(maxDistance[arc.startNode]) || std::isinf(maxDistance[arc.endNode]))
			continue;

		if (maxDistance[arc.startNode] < maxDistance[arc.endNode])
			bush.addArc(arc.index);
	}
}

std::vector<double> Algorithm::getMaxDistance(const Bush &bush) {
	auto indegree = Algorithm::indegree(bush);

	std::vector<double> distance(map.nodes, -std::numeric_limits<double>::infinity());
	distance[bush.root] = 0;

	Queue q(map.nodes);
	q.enqueue(bush.root);
	while (!q.isEmpty()) {
		int startNode = q.dequeue();

		for (const auto &arc: map.neighborsOf(startNode)) {
			if (!bush.arcExists(arc.index))
				continue;

			double newDistance = distance[startNode] + arc.getCost();
			if (distance[arc.endNode] < newDistance)
				distance[arc.endNode] = newDistance;

			indegree[arc.endNode]--;
			if (indegree[arc.endNode] == 0)
				q.enqueue(arc.endNode);
		}
	}

	return distance;
}

std::pair<std::vector<Network::Arc *>, std::vector<Network::Arc *>>
Algorithm::getTrees(const Bush &bush) {
	auto indegree = Algorithm::indegree(bush);

	std::vector<double> minDistance(map.nodes, std::numeric_limits<double>::infinity());
	minDistance[bush.root] = 0;
	std::vector<Network::Arc *> minTree(map.nodes);

	std::vector<double> maxDistance(map.nodes, -std::numeric_limits<double>::infinity());
	maxDistance[bush.root] = 0;
	std::vector<Network::Arc *> maxTree(map.nodes);

	Queue q(map.nodes);
	q.enqueue(bush.root);
	while (!q.isEmpty()) {
		int startNode = q.dequeue();

		for (auto &arc: map.neighborsOf(startNode)) {
			if (!bush.arcExists(arc.index))
				continue;

			double newDistance = minDistance[startNode] + arc.getCost();
			if (minDistance[arc.endNode] > newDistance) {
				minDistance[arc.endNode] = newDistance;
				minTree[arc.endNode] = &arc;
			}

			newDistance = maxDistance[startNode] + arc.getCost();
			if (maxDistance[arc.endNode] < newDistance && bush.getArcFlow(arc.index) != 0) {
				maxDistance[arc.endNode] = newDistance;
				maxTree[arc.endNode] = &arc;
			}

			indegree[arc.endNode]--;
			if (indegree[arc.endNode] == 0)
				q.enqueue(arc.endNode);
		}
	}

	return {minTree, maxTree};
}

std::vector<int> Algorithm::indegree(const Bush &bush) {
	std::vector<int> indegree(map.nodes);

	for (const auto &arc: map.getArcs()) {
		if (!bush.arcExists(arc.index))
			continue;
		indegree[arc.endNode]++;
	}

	return indegree;
}

int Algorithm::LCA(const std::vector<Network::Arc *> &minTree,
				   const std::vector<Network::Arc *> &maxTree, int node) {
	auto minTreePrev = minTree[node];
	auto maxTreePrev = maxTree[node];

	if (maxTreePrev == nullptr || minTreePrev == nullptr)
		return -1;
	if (minTreePrev->startNode == maxTreePrev->startNode)
		return -1;

	std::vector<int> flags;
	flags.reserve(100);
	while (minTreePrev != nullptr) {
		flags.push_back(minTreePrev->startNode);
		minTreePrev = minTree[minTreePrev->startNode];
	}

	while (true) {
		if (maxTreePrev == nullptr)
			return -1;
		if (std::find(flags.begin(), flags.end(), maxTreePrev->startNode) != flags.end())
			return maxTreePrev->startNode;
		maxTreePrev = maxTree[maxTreePrev->startNode];
	}
}

double Algorithm::findFlowDelta(const std::vector<Network::Arc *> &minTree,
								const std::vector<Network::Arc *> &maxTree,
								const Bush &bush, int node, int lca) {

	double maxDeltaX = std::numeric_limits<double>::infinity();
	auto arc = maxTree[node];
	while (arc != nullptr && arc->endNode != lca) {
		if (maxDeltaX > bush.getArcFlow(arc->index))
			maxDeltaX = bush.getArcFlow(arc->index);

		arc = maxTree[arc->startNode];
	}

	if (maxDeltaX == 0)
		return 0;

	double deltaX = 0;
	for (int i = 0; i < NEWTON_MAX_ITERATIONS; ++i) {

		double minPathFlow = 0;
		double minPathFlowDerivative = 0;
		arc = minTree[node];
		while (arc != nullptr && arc->endNode != lca) {
			minPathFlow += arc->BPR(deltaX);
			minPathFlowDerivative += arc->BPRderivative(deltaX);

			arc = minTree[arc->startNode];
		}

		double maxPathFlow = 0;
		double maxPathFlowDerivative = 0;
		arc = maxTree[node];
		while (arc != nullptr && arc->endNode != lca) {
			maxPathFlow += arc->BPR(-deltaX);
			maxPathFlowDerivative += arc->BPRderivative(-deltaX);

			arc = maxTree[arc->startNode];
		}

		double newDeltaX = deltaX + (maxPathFlow - minPathFlow) /
									(maxPathFlowDerivative + minPathFlowDerivative);

		if (std::abs(deltaX - newDeltaX) < NEWTON_EPSILON) {
			deltaX = std::min(std::max(newDeltaX, 0.0), maxDeltaX);
			break;
		} else
			deltaX = newDeltaX;
	}

	return std::min(std::max(deltaX, 0.0), maxDeltaX);
}

void Algorithm::shiftFlows(const std::vector<Network::Arc *> &minTree,
						   const std::vector<Network::Arc *> &maxTree,
						   Bush &bush, int node, int lca, double deltaX) {
	auto arc = minTree[node];
	while (arc != nullptr && arc->endNode != lca) {
		arc->addFlow(deltaX);
		bush.addFlow(arc->index, deltaX);

		arc = minTree[arc->startNode];
	}

	arc = maxTree[node];
	while (arc != nullptr && arc->endNode != lca) {
		arc->addFlow(-deltaX);
		bush.addFlow(arc->index, -deltaX);

		arc = maxTree[arc->startNode];
	}
}

void Algorithm::removeUnusedArcs(Bush &bush, const std::vector<Network::Arc *> &minTree) {
	for (int i = 0; i < map.getArcs().size(); ++i) {
		if (bush.getArcFlow(i) <= 0)
			bush.removeArc(i);
	}

	for (auto arc: minTree) {
		if (arc != nullptr)
			bush.addArc(arc->index);
	}
}
