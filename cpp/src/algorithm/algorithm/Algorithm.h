#ifndef BP_CPP_ALGORITHM_H
#define BP_CPP_ALGORITHM_H

#include "../../data/network/Network.h"
#include "../../data/bush/Bush.h"

/** Class representing sequential Algorithm B with its input and inner state. */
class Algorithm {
protected:
	/** Maximum number of Newton method iterations when moving flows. */
	static constexpr int NEWTON_MAX_ITERATIONS = 100;

	/**
	 * If difference of two successive Newton method values
	 * is smaller than this number, the method terminates.
	 */
	static constexpr double NEWTON_EPSILON = 1e-10;

	/** Road network. */
	Network &map;

	/** Origin-destination matrix. */
	const Matrix &odMatrix;

	/** Minimum relative gap. */
	const double relativeGap;

	/** Max iterations. */
	const int iterations;

	/**
	 * Array of bushes, bush on index i is rooted in node i.
	 * Bush is an acyclic factor of {@code map}.
	 */
	std::vector<Bush> bushes;

public:
	/**
	 * Creates new instance representing Algorithm B with given input.
	 * @param map         road network to which the traffic will be assigned
	 * @param odMatrix    OD matrix of flows that will be assigned
	 * @param relativeGap min value of relative gap, if during any
	 *                    iteration will be the value of rel. gap
	 *                    lower than this, algorithm terminates
	 * @param iterations  max number of iterations of the algorithm
	 */
	Algorithm(Network &map, const Matrix &odMatrix, double relativeGap, int iterations);

	/**
	 * Starts the Algorithm B.
	 * @return vector of arcs with assigned flows
	 */
	virtual std::vector<Network::Arc> start();

	/** Default virtual destructor. */
	virtual ~Algorithm() = default;

protected:
	/**
	 * Creates a bush rooted in given node. Bush is created as a
	 * factor of {@code map} with only those arcs which start node
	 * is closer (w.r.t. cost) to root than end node.
	 * @param root root of the bush
	 * @return bush - acyclic factor of {@code map}
	 */
	void createBush(int root);

	/**
	 * Improves bush by adding arcs to it. Arc is added if maximum
	 * distance in bush of its start node is smaller than maximum
	 * distance in bush of its end node.
	 * @param bush bush to be improved
	 */
	void improveBush(Bush &bush);

	/**
	 * Return maximum distances (w.r.t. cost) in bush for each node.
	 * @param bush bush in which maximum distances are found
	 * @return array of maximum distances for each node
	 */
	std::vector<double> getMaxDistance(const Bush &bush);

	/**
	 * Returns trees of max and min paths to each node in the bush.
	 * Distances are computed w.r.t cost.
	 * @param bush bush in which trees are found
	 * @return pair (min tree, max tree)
	 */
	std::pair<std::vector<Network::Arc *>, std::vector<Network::Arc *>>
	getTrees(const Bush &bush);

	/**
	 * Computes the indegree of each node in the bush.
	 * @param bush bush
	 * @return array of indegrees for each node
	 */
	std::vector<int> indegree(const Bush &bush);

	/**
	 * Finds segments of min and max path that start at the
	 * same node (which is returned) end at given node and
	 * have no shared nodes or arcs in between. Similar to
	 * Lowest Common Ancestor procedure.
	 * @param minTree tree of min paths
	 * @param maxTree tree of max paths
	 * @param node node, at which segments end
	 * @return node, at which segments start
	 */
	static int LCA(const std::vector<Network::Arc *> &minTree,
				   const std::vector<Network::Arc *> &maxTree, int node);

	/**
	 * Finds the flow that will be shifted from max path segment
	 * to min path segment so that those segments are in equilibrium.
	 * @param minTree tree of min paths
	 * @param maxTree tree of max paths
	 * @param bush bush
	 * @param node node, at which segments end
	 * @param lca node, at which segments start
	 * @return flow to be shifted
	 */
	static double findFlowDelta(const std::vector<Network::Arc *> &minTree,
								const std::vector<Network::Arc *> &maxTree,
								const Bush &bush, int node, int lca);

	/**
	 * Shifts flow from max path segment to min path segment.
	 * @param minTree tree of min paths
	 * @param maxTree tree of max paths
	 * @param bush bush
	 * @param node node, at which segments end
	 * @param lca node, at which segments start
	 * @param deltaX flow to be shifted
	 */
	static void shiftFlows(const std::vector<Network::Arc *> &minTree,
						   const std::vector<Network::Arc *> &maxTree,
						   Bush &bush, int node, int lca, double deltaX);

	/**
	 * Removes arcs from bush that have zero flow on
	 * the bush. Arcs in min tree are spared.
	 * @param bush bush
	 * @param minTree tree of min paths
	 */
	void removeUnusedArcs(Bush &bush, const std::vector<Network::Arc *> &minTree);
};

#endif
