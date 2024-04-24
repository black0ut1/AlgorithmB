#include <stdexcept>
#include <limits>
#include "Network.h"
#include "../priority_queue/PriorityQueue.h"

Network::Network(const std::vector<std::vector<Arc>> &adjacencyList, int zones)
		: nodes((int) adjacencyList.size()), zones(zones), indices(nodes + 1) {
	int sum = 0;
	for (const auto &item: adjacencyList)
		sum += (int) item.size();
	arcs.reserve(sum);

	int offset = 0;
	for (int startNode = 0; startNode < adjacencyList.size(); ++startNode) {
		indices[startNode] = offset;

		auto neighbors = adjacencyList[startNode];
		for (int i = 0; i < neighbors.size(); ++i) {
			arcs.emplace_back(neighbors[i], offset + i);
		}

		offset += (int) neighbors.size();
	}

	indices[indices.size() - 1] = offset;
}

std::span<Network::Arc> Network::neighborsOf(int node) {
	return {arcs.begin() + indices[node], arcs.begin() + indices[node + 1]};
}

Network::Arc &Network::getArc(int from, int to) {
	auto neighbors = neighborsOf(from);

	for (const auto &arc: neighbors)
		if (arc.endNode == to)
			return const_cast<Arc &>(arc);

	throw std::runtime_error("Arc does not exist");
}

void Network::updateCosts() {
	for (auto &arc: arcs)
		arc.cost = arc.BPR(0);
}

double Network::objectiveFunction() {
	double sum = 0;
	for (const auto &arc: arcs) {
		sum += arc.BPRintegral();
	}
	return sum;
}

double Network::gap(const Matrix &odm) {
	std::vector<double> aonFlow(arcs.size());

	for (int startZone = 0; startZone < zones; ++startZone) {
		auto [_, minTree] = Network::minTree(startZone);

		for (int endZonde = 0; endZonde < zones; ++endZonde) {
			double trips = odm(startZone, endZonde);
			if (trips == 0)
				continue;

			auto arc = minTree[endZonde];
			while (arc != nullptr) {
				aonFlow[arc->index] += trips;

				arc = minTree[arc->startNode];
			}
		}
	}

	double gap = 0;
	for (const auto &arc: arcs)
		gap += arc.cost * (aonFlow[arc.index] - arc.currentFlow);

	return gap;
}

double Network::gap(const Matrix &odm, BS::thread_pool &threadPool) {

	std::vector<std::atomic<double>> aonFlow(arcs.size());

	for (int startZone = 0; startZone < zones; ++startZone) {
		threadPool.detach_task([this, startZone, &odm, &aonFlow]() {
			auto minTree = Network::minTree(startZone).second;

			for (int endZone = 0; endZone < zones; ++endZone) {
				double trips = odm(startZone, endZone);
				if (trips == 0)
					continue;

				const Arc *arc = minTree[endZone];
				while (arc != nullptr) {
					aonFlow[arc->index].fetch_add(trips);

					arc = minTree[arc->startNode];
				}
			}
		});
	}
	threadPool.wait();

	double gap = 0;
	for (const auto &arc: arcs) {
		gap += arc.cost * (aonFlow[arc.index].load() - arc.currentFlow);
	}

	return gap;
}

std::pair<std::vector<double>, std::vector<const Network::Arc *>> Network::minTree(int root) {
	std::vector<double> distance(nodes, std::numeric_limits<double>::infinity());
	distance[root] = 0;

	std::vector<const Arc *> previous(nodes);

	PriorityQueue pq(nodes);
	std::vector<char> mark(nodes);


	pq.add(root, 0);
	while (!pq.isEmpty()) {
		int fromVertex = pq.popMin();
		mark[fromVertex] = 2;

		auto neighbors = neighborsOf(fromVertex);
		for (const auto &arc: neighbors) {
			int toVertex = arc.endNode;
			if (mark[toVertex] == 2)
				continue;

			double newDistance = distance[fromVertex] + arc.cost;
			if (mark[toVertex] == 0) {
				mark[toVertex] = 1;
				distance[toVertex] = newDistance;
				previous[toVertex] = &arc;
				pq.add(toVertex, newDistance);
			} else if (mark[toVertex] == 1 && newDistance < distance[toVertex]) {
				distance[toVertex] = newDistance;
				previous[toVertex] = &arc;
				pq.decreasePriority(toVertex, newDistance);
			}
		}
	}

	return {distance, previous};
}

std::vector<Network::Arc> &Network::getArcs() {
	return arcs;
}

////////////////////////////////

Network::Arc::Arc(int startNode, int endNode, double capacity, double freeFlow)
		: startNode(startNode), endNode(endNode), capacity(capacity),
		  freeFlow(freeFlow), index(-1) {}

Network::Arc::Arc(const Arc &copy, int index)
		: startNode(copy.startNode), endNode(copy.endNode), capacity(copy.capacity),
		  freeFlow(copy.freeFlow), currentFlow(copy.currentFlow), index(index) {}

void Network::Arc::addFlow(double delta) {
	currentFlow += delta;
}

double Network::Arc::getCurrentFlow() const {
	return currentFlow;
}

double Network::Arc::getCost() const {
	return cost;
}

double Network::Arc::BPR(double delta) const {
	double ratio = (currentFlow + delta) / capacity;
	double pwr = ratio * ratio * ratio * ratio;
	return freeFlow * (1 + .15 * pwr);
}

double Network::Arc::BPRderivative(double delta) const {
	double ratio = (currentFlow + delta) / capacity;
	double pwr = ratio * ratio * ratio;
	return .6 * freeFlow * pwr / capacity;
}

double Network::Arc::BPRintegral() const {
	double ratio = currentFlow / capacity;
	double pwr = ratio * ratio * ratio * ratio;
	return freeFlow * currentFlow * (1 + .03 * pwr);
}

Network::Arc::Arc(int startNode, int endNode, double capacity, double freeFlow, double flow, int index)
		: startNode(startNode), endNode(endNode), capacity(capacity),
		  freeFlow(freeFlow), index(index), currentFlow(flow) {}
