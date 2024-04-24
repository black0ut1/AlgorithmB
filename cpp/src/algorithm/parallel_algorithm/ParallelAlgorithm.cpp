#include <iostream>
#include "ParallelAlgorithm.h"

ParallelAlgorithm::ParallelAlgorithm(Network &map, const Matrix &odMatrix,
									 double epsilon, int iterations, int threads)
		: Algorithm(map, odMatrix, epsilon, iterations), threads(threads),
		  threadPool(threads) {}

std::vector<Network::Arc> ParallelAlgorithm::start() {
	map.updateCosts();
	for (int i = 0; i < map.zones; ++i) {
		threadPool.detach_task([i, this]() { createBush(i); });
	}
	threadPool.wait();


	auto &arcs = map.getArcs();
	for (const auto &bush: bushes)
		for (auto &arc: arcs)
			arc.addFlow(bush.getArcFlow(arc.index));
	map.updateCosts();


	std::cout << "===================================" << std::endl;
	std::cout << "Static traffic assignment algorithm B - ";
	std::cout << "Parallel version (" << threads << " threads)" << std::endl;
	std::cout << "Max. iterations: " << iterations << std::endl;
	if (relativeGap != 0)
		std::cout << "Relative gap: " << relativeGap << std::endl;
	std::cout << "Starting objective function: " << map.objectiveFunction() << std::endl;
	std::cout << "===================================" << std::endl;


	double currentRelativeGap = std::numeric_limits<double>::infinity();
	double maxLB = -std::numeric_limits<double>::infinity();
	int currentIteration = 0;

	while (currentIteration < iterations && (relativeGap == 0 || currentRelativeGap > relativeGap)) {
		std::cout << "Iteration " << currentIteration << std::endl;

		for (int i = 0; i < bushes.size(); i += threads) {

			std::vector<std::pair<std::vector<Network::Arc *>, std::vector<Network::Arc *>>>
					trees(threads);
			std::vector<std::vector<int>> lcas(threads, std::vector<int>(map.nodes));

			for (int j = 0; j < threads; ++j) {
				if (i + j == bushes.size())
					break;

				threadPool.detach_task([i, j, this, &trees, &lcas]() {
					improveBush(bushes[i + j]);
					trees[j] = getTrees(bushes[i + j]);

					for (int node = 0; node < map.nodes; ++node)
						lcas[j][node] = LCA(trees[j].first, trees[j].second, node);
				});
			}
			threadPool.wait();


			for (int j = 0; j < threads; ++j) {
				if (i + j == bushes.size())
					break;

				auto [minTree, maxTree] = trees[j];
				for (int node = 0; node < map.nodes; ++node) {
					int lca = lcas[j][node];
					if (lca == -1)
						continue;

					double deltaX = findFlowDelta(minTree, maxTree, bushes[i + j], node, lca);
					if (deltaX == 0)
						continue;

					shiftFlows(minTree, maxTree, bushes[i + j], node, lca, deltaX);
				}
			}


			for (int j = 0; j < threads; ++j) {
				if (i + j == bushes.size())
					break;

				threadPool.detach_task([i, j, this, &trees]() {
					removeUnusedArcs(bushes[i + j], trees[j].first);
				});
			}
			threadPool.wait();

			map.updateCosts();
		}

		double of = map.objectiveFunction();
		std::cout << "Objective function: " << of << std::endl;

		if (relativeGap != 0) {
			double gap = map.gap(odMatrix, threadPool);
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
