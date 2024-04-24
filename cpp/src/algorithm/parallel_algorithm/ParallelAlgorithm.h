#ifndef BP_CPP_PARALLELALGORITHM_H
#define BP_CPP_PARALLELALGORITHM_H

#include "../algorithm/Algorithm.h"
#include "thread-pool/BS_thread_pool.hpp"

/** Class representing parallel Algorithm B with its input and inner state. */
class ParallelAlgorithm : public Algorithm {
protected:
	/** Number of worker threads. */
	const int threads;

	/** Thread pool for executing tasks. */
	BS::thread_pool threadPool;

public:
	/**
	 * Creates new instance representing parallel Algorithm B with given input.
	 * @param map         road network to which the traffic will be assigned
	 * @param odMatrix    OD matrix of flows that will be assigned
	 * @param relativeGap min value of relative gap, if during any
	 *                    iteration will be the value of rel. gap
	 *                    lower than this, algorithm terminates
	 * @param iterations  max number of iterations of the algorithm
	 * @param threads     number of worker threads
	 */
	ParallelAlgorithm(Network &map, const Matrix &odMatrix,
					  double epsilon, int iterations, int threads);

	std::vector<Network::Arc> start() override;
};

#endif
