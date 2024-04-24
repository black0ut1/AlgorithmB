#ifndef BP_CPP_BUSH_H
#define BP_CPP_BUSH_H

#include <vector>

/**
 * Class representing a bush constructed from a @c Network .
 * Bush is an acyclic subgraph with only one root.
 */
class Bush {
public:
	/** Root of this bush. */
	int root{};

private:

	/**
	 * Array indicating which arcs of @c arcs
	 * array of @c Network are in this bush.
	 */
	std::vector<char> arcFlags;

	/** Vector representing flow on this bush for each arc on this bush */
	std::vector<double> flows;

public:
	/**
	 * Constructor creating bush with given root and
	 * supporting given number of edges.
	 * @param arcs number of edges in original network
	 */
	explicit Bush(int arcs);

	/**
	 * Adds arc to the bush.
	 * @param arcIndex index of added arc
	 */
	void addArc(int arcIndex);

	/**
	 * Removes arc from the bush.
	 * @param arcIndex index of removed arc
	 */
	void removeArc(int arcIndex);

	/**
	 * Returns if arc exists in this bush.
	 * @param arcIndex index of arc
	 * @return true, if arc exists in the bush, false otherwise
	 */
	bool arcExists(int arcIndex) const;

	/**
	 * Returns flow on the bush of arc.
	 * @param arcIndex index of arc
	 * @return flow on the bush
	 */
	double getArcFlow(int arcIndex) const;

	/**
	 * Adds to a flow on the bush of arc.
	 * @param arcIndex index of arc
	 * @param flow     flow to be added
	 */
	void addFlow(int arcIndex, double delta);
};

#endif