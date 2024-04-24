#ifndef BP_CPP_NETWORK_H
#define BP_CPP_NETWORK_H

#include <vector>
#include <span>
#include "../matrix/Matrix.h"
#include "thread-pool/BS_thread_pool.hpp"

/**
 * Class representing static oriented graph/road network using
 * Compressed Sparse Row implementation. Nodes are represented
 * by the set @c {0,...,nodes-1} . First @c zones nodes
 * are zones.
 */
class Network {
public:
	/** Number of nodes in the network. */
	const int nodes;

	/** Number of traffic zones in the network */
	const int zones;

	class Arc;

private:
	/**
	 * Vector of offsets to array @c arcs . On index i in
	 * this array is the index to array @c arcs , on which
	 * begin arcs starting in node i.
	 */
	std::vector<int> indices;

	/** Vector of arcs in the network. */
	std::vector<Arc> arcs;

public:
	/**
	 * Constructor creating network from adjacency list.
	 * @param adjacencyList adjacency list representation of graph
	 * @param zones         number of zones
	 */
	Network(const std::vector<std::vector<Arc>> &adjacencyList, int zones);

	/**
	 * Returns arcs starting in given node.
	 * @param node starting node
	 * @return array view into array @c arcs representing
	 * subarray of arcs starting in @c node
	 */
	std::span<Network::Arc> neighborsOf(int node);

	/** For testing purposes. */
	Arc &getArc(int from, int to);

	/** Updates cost of all arcs according to their current flows. */
	void updateCosts();

	/**
	 * Calculates the objective function of this network
	 * w.r.t. current flows.
	 * @return value of objective function w.r.t. current flows
	 */
	double objectiveFunction();

	/**
	 * Calculates the gap metric w.r.t. current flows.
	 * @param odm OD matrix needed for AON assignment part
	 * @return value of gap w.r.t. current flows
	 */
	double gap(const Matrix &odm);

	/**
	 * Same as ::gap(Matrix) but parallelized.
	 * @param odm        OD matrix needed for AON assignment part
	 * @param threadPool thread pool for executing tasks
	 * @return value of gap w.r.t. current flows, slightly undeterministic
	 */
	double gap(const Matrix &odm, BS::thread_pool &threadPool);

	/**
	 * Returns array of all arcs.
	 * @return array of all arcs
	 */
	std::vector<Arc> &getArcs();

	/**
	 * Using Dijkstra, this method returns the tree of min paths
	 * and according distances to each node from given root node.
	 * Distances are calculated from arc costs.
	 * @param root node in which the algorithm starts
	 * @return pair (min. distances, min. tree)
	 */
	std::pair<std::vector<double>, std::vector<const Network::Arc *>> minTree(int root);

	/** Class representing an arc in the network. */
	class Arc {
	public:
		/** Node in which this arc starts. */
		const int startNode;

		/** Node in which this arc ends. */
		const int endNode;

		/** Index of this arc in the array @c arcs . */
		const int index;

		/** Capacity of this arc. */
		const double capacity;

		/** Free flow time of this arc. */
		const double freeFlow;

	private:
		/** Current flow on this arc in the network. */
		double currentFlow{};

		/** Cost of this arc determined by BPR function. */
		double cost{};

		friend class Network;

	public:
		/**
		 * Creates new arc with given parameters and zero flow.
		 * @param startNode node in which this arc starts
		 * @param endNode   node in which this arc ends
		 * @param capacity  capacity of this arc
		 * @param freeFlow  free flow time of this arc
		 */
		Arc(int startNode, int endNode, double capacity, double freeFlow);

		/** For testing purposes. */
		Arc(int startNode, int endNode, double capacity, double freeFlow, double flow, int index);

		/**
		 * Creates a copy of given arc with initialized index.
		 * @param copy  arc to be copied
		 * @param index index to array {@code arcs} in which will
		 *              this arc reside
		 */
		Arc(const Arc &copy, int index);

		/**
		 * Adds flow to current flow of this arc.
		 * @param delta flow to be added
		 */
		void addFlow(double delta);

		/**
		 * Returns current flow.
		 * @return current flow of this arc
		 */
		double getCurrentFlow() const;

		/**
		 * Returns cost of this arc. The cost does not
		 * need to be w.r.t. current flow unless
		 * ::updateCosts() is called.
		 * @return cost of this arc
		 */
		double getCost() const;

		/**
		 * Returns the value of BPR function (or cost) of this
		 * arc w.r.t current flow plus given delta.
		 * @param delta value that will be added to current flow to compute cost
		 * @return cost of this arc
		 */
		double BPR(double delta) const;

		/**
		 * Returns the value of derivative of BPR function
		 * of this arc w.r.t current flow plus given delta.
		 * @param delta value that will be added to current flow
		 *              to compute cost derivative
		 * @return cost derivative of this arc
		 */
		double BPRderivative(double delta) const;

		/**
		 * Returns the value of integral of BPR function
		 * w.r.t. current flow on the arc.
		 * @return the value of integral of BPR function
		 */
		double BPRintegral() const;
	};
};

#endif
