package cz.zcu.pperncka.algorithm;

import cz.zcu.pperncka.data.*;

import java.util.*;

/** Class representing sequential Algorithm B with its input and inner state. */
public class Algorithm {
	
	/** Maximum number of Newton method iterations when moving flows. */
	protected static final int NEWTON_MAX_ITERATIONS = 100;
	
	/**
	 * If difference of two successive Newton method values
	 * is smaller than this number, the method terminates.
	 */
	protected static final double NEWTON_EPSILON = 1e-10;
	
	/** Road network. */
	protected final Network map;
	
	/** Origin-destination matrix. */
	protected final Matrix odMatrix;
	
	/** Minimum relative gap. */
	protected final double relativeGap;
	
	/** Max iterations. */
	protected final int iterations;
	
	/**
	 * Array of bushes, bush on index i is rooted in node i.
	 * Bush is an acyclic factor of {@code map}.
	 */
	protected final Bush[] bushes;
	
	/**
	 * Creates new instance representing Algorithm B with given input.
	 * @param map         road network to which the traffic will be assigned
	 * @param odMatrix    OD matrix of flows that will be assigned
	 * @param relativeGap min value of relative gap, if during any
	 *                    iteration will be the value of rel. gap
	 *                    lower than this, algorithm terminates
	 * @param iterations  max number of iterations of the algorithm
	 */
	public Algorithm(Network map, Matrix odMatrix, double relativeGap, int iterations) {
		this.map = map;
		this.odMatrix = odMatrix;
		this.relativeGap = relativeGap;
		this.iterations = iterations;
		this.bushes = new Bush[map.zones];
	}
	
	/**
	 * Starts the Algorithm B.
	 * @return array of arcs with assigned flows
	 */
	public Network.Arc[] start() {
		map.updateCosts();
		for (int root = 0; root < bushes.length; root++)
			bushes[root] = createBush(root);
		
		
		Network.Arc[] mapArcs = map.getArcs();
		for (Bush bush : bushes)
			for (Network.Arc mapArc : mapArcs)
				mapArc.addFlow(bush.getArcFlow(mapArc.index));
		map.updateCosts();
		
		
		System.out.println("===================================");
		System.out.print("Static traffic assignment algorithm B - ");
		System.out.println("Sequential version");
		System.out.println("Max. iterations: " + iterations);
		if (relativeGap != 0)
			System.out.printf(Locale.ROOT, "Relative gap: %.15f%n", relativeGap);
		System.out.printf(Locale.ROOT,
				"Starting objective function: %.15f%n", map.objectiveFunction());
		System.out.println("===================================");
		
		
		double currentRelativeGap = Double.POSITIVE_INFINITY;
		double maxLb = Double.NEGATIVE_INFINITY;
		int currentIteration = 0;
		
		while ((currentIteration < iterations && relativeGap == 0) ||
				(currentIteration < iterations && currentRelativeGap > relativeGap)) {
			System.out.println("Iteration " + currentIteration);
			
			for (Bush bush : bushes) {
				improveBush(bush);
				
				var trees = getTrees(bush);
				var minTree = trees.first();
				var maxTree = trees.second();
				
				for (int node = 0; node < map.nodes; node++) {
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
			System.out.printf(Locale.ROOT, "Objective function: %.15f%n", of);
			
			if (relativeGap != 0) {
				double gap = map.gap(odMatrix);
				double lb = of + gap;
				if (lb > maxLb)
					maxLb = lb;
				
				currentRelativeGap = -gap / Math.abs(maxLb);
				
				System.out.printf(Locale.ROOT, "Gap: %.15f%n", gap);
				System.out.printf(Locale.ROOT, "Relative gap: %.15f%n", currentRelativeGap);
			}
			
			System.out.println("-----------------------------------");
			currentIteration++;
		}
		
		return map.getArcs();
	}
	
	/**
	 * Creates a bush rooted in given node. Bush is created as a
	 * factor of {@code map} with only those arcs which start node
	 * is closer (w.r.t. cost) to root than end node.
	 * @param root root of the bush
	 * @return bush - acyclic factor of {@code map}
	 */
	protected Bush createBush(int root) {
		var pair = map.minTree(root);
		double[] distances = pair.first();
		Network.Arc[] previous = pair.second();
		
		Bush bush = new Bush(map.getArcs().length, root);
		
		for (Network.Arc arc : map.getArcs())
			if (distances[arc.startNode] < distances[arc.endNode])
				bush.addArc(arc.index);
		
		for (int node = 0; node < map.zones; node++) {
			double trips = odMatrix.get(root, node);
			if (trips == 0)
				continue;
			
			Network.Arc arc = previous[node];
			while (arc != null) {
				bush.addFlow(arc.index, trips);
				
				arc = previous[arc.startNode];
			}
		}
		
		return bush;
	}
	
	/**
	 * Improves bush by adding arcs to it. Arc is added if maximum
	 * distance in bush of its start node is smaller than maximum
	 * distance in bush of its end node.
	 * @param bush bush to be improved
	 */
	protected void improveBush(Bush bush) {
		double[] maxDistance = getMaxDistance(bush);
		
		for (Network.Arc arc : map.getArcs()) {
			if (maxDistance[arc.startNode] == Double.NEGATIVE_INFINITY
					|| maxDistance[arc.endNode] == Double.NEGATIVE_INFINITY)
				continue;
			
			if (maxDistance[arc.startNode] < maxDistance[arc.endNode]) {
				bush.addArc(arc.index);
			}
		}
	}
	
	/**
	 * Return maximum distances (w.r.t. cost) in bush for each node.
	 * @param bush bush in which maximum distances are found
	 * @return array of maximum distances for each node
	 */
	protected double[] getMaxDistance(Bush bush) {
		// spočtení vstupní stupně vrcholů, kvůli udržení topologického seřazení
		int[] indegree = indegree(bush);
		
		
		// strom maximálních cest
		double[] maxTreeDistance = new double[map.nodes];
		Arrays.fill(maxTreeDistance, Double.NEGATIVE_INFINITY);
		maxTreeDistance[bush.root] = 0;
		
		
		IntQueue queue = new IntQueue(map.nodes);
		queue.enqueue(bush.root);
		while (!queue.isEmpty()) {
			int startNode = queue.dequeue();
			
			for (Network.Arc arc : map.neighborsOf(startNode)) {
				if (!bush.arcExists(arc.index))
					continue;
				
				double newDistance = maxTreeDistance[startNode] + arc.getCost();
				if (maxTreeDistance[arc.endNode] < newDistance) {
					maxTreeDistance[arc.endNode] = newDistance;
				}
				
				indegree[arc.endNode]--;
				if (indegree[arc.endNode] == 0)
					queue.enqueue(arc.endNode);
			}
		}
		
		return maxTreeDistance;
	}
	
	/**
	 * Returns trees of max and min paths to each node in the bush.
	 * Distances are computed w.r.t cost.
	 * @param bush bush in which trees are found
	 * @return pair (min tree, max tree)
	 */
	protected Pair<Network.Arc[], Network.Arc[]> getTrees(Bush bush) {
		// spočtení vstupní stupně vrcholů, kvůli udržení topologického seřazení
		int[] indegree = indegree(bush);
		
		
		// strom minimálních cest
		double[] minTreeDistance = new double[map.nodes];
		Arrays.fill(minTreeDistance, Double.POSITIVE_INFINITY);
		minTreeDistance[bush.root] = 0;
		
		Network.Arc[] minTreePrevious = new Network.Arc[map.nodes];
		
		// strom maximálních cest
		double[] maxTreeDistance = new double[map.nodes];
		Arrays.fill(maxTreeDistance, Double.NEGATIVE_INFINITY);
		maxTreeDistance[bush.root] = 0;
		
		Network.Arc[] maxTreePrevious = new Network.Arc[map.nodes];
		
		
		IntQueue queue = new IntQueue(map.nodes);
		queue.enqueue(bush.root);
		while (!queue.isEmpty()) {
			int startNode = queue.dequeue();
			
			for (Network.Arc arc : map.neighborsOf(startNode)) {
				if (!bush.arcExists(arc.index))
					continue;
				
				double newDistance = minTreeDistance[startNode] + arc.getCost();
				if (minTreeDistance[arc.endNode] > newDistance) {
					minTreeDistance[arc.endNode] = newDistance;
					minTreePrevious[arc.endNode] = arc;
				}
				
				newDistance = maxTreeDistance[startNode] + arc.getCost();
				if (maxTreeDistance[arc.endNode] < newDistance && bush.getArcFlow(arc.index) != 0) {
					maxTreeDistance[arc.endNode] = newDistance; // ^^ hrana musí být aktivní
					maxTreePrevious[arc.endNode] = arc;
				}
				
				indegree[arc.endNode]--;
				if (indegree[arc.endNode] == 0)
					queue.enqueue(arc.endNode);
			}
		}
		
		return new Pair<>(minTreePrevious, maxTreePrevious);
	}
	
	/**
	 * Computes the indegree of each node in the bush.
	 * @param bush bush
	 * @return array of indegrees for each node
	 */
	protected int[] indegree(Bush bush) {
		int[] indegree = new int[map.nodes];
		
		for (Network.Arc arc : map.getArcs()) {
			if (!bush.arcExists(arc.index))
				continue;
			indegree[arc.endNode]++;
		}
		
		return indegree;
	}
	
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
	protected int LCA(Network.Arc[] minTree, Network.Arc[] maxTree, int node) {
		Network.Arc minTreePrev = minTree[node];
		Network.Arc maxTreePrev = maxTree[node];
		
		if (maxTreePrev == null || minTreePrev == null)
			return -1;
		if (minTreePrev.startNode == maxTreePrev.startNode)
			return -1;
		
		IntSet flags = new IntSet(100, 2);
		while (minTreePrev != null) {
			flags.add(minTreePrev.startNode);
			minTreePrev = minTree[minTreePrev.startNode];
		}
		
		while (true) {
			if (maxTreePrev == null)
				return -1;
			if (flags.contains(maxTreePrev.startNode))
				return maxTreePrev.startNode;
			maxTreePrev = maxTree[maxTreePrev.startNode];
		}
	}
	
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
	protected double findFlowDelta(Network.Arc[] minTree, Network.Arc[] maxTree,
								   Bush bush, int node, int lca) {
		
		double maxDeltaX = Double.POSITIVE_INFINITY;
		Network.Arc arc = maxTree[node];
		while (arc != null && arc.endNode != lca) {
			if (maxDeltaX > bush.getArcFlow(arc.index))
				maxDeltaX = bush.getArcFlow(arc.index);
			
			arc = maxTree[arc.startNode];
		}
		
		if (maxDeltaX == 0)
			return 0;
		
		// případ 0 < deltaX < μ
		double deltaX = 0;
		for (int i = 0; i < NEWTON_MAX_ITERATIONS; i++) {
			
			double minPathFlow = 0;
			double minPathFlowDerivative = 0;
			arc = minTree[node];
			while (arc != null && arc.endNode != lca) {
				minPathFlow += arc.BPR(deltaX);
				minPathFlowDerivative += arc.BPRderivative(deltaX);
				
				arc = minTree[arc.startNode];
			}
			
			
			double maxPathFlow = 0;
			double maxPathFlowDerivative = 0;
			arc = maxTree[node];
			while (arc != null && arc.endNode != lca) {
				maxPathFlow += arc.BPR(-deltaX);
				maxPathFlowDerivative += arc.BPRderivative(-deltaX);
				
				arc = maxTree[arc.startNode];
			}
			
			double newDeltaX = deltaX + (maxPathFlow - minPathFlow) /
					(maxPathFlowDerivative + minPathFlowDerivative);
			
			if (Math.abs(deltaX - newDeltaX) < NEWTON_EPSILON) {
				deltaX = Math.min(Math.max(newDeltaX, 0), maxDeltaX);
				break;
			} else
				deltaX = newDeltaX;
		}
		
		return Math.min(Math.max(deltaX, 0), maxDeltaX);
	}
	
	/**
	 * Shifts flow from max path segment to min path segment.
	 * @param minTree tree of min paths
	 * @param maxTree tree of max paths
	 * @param bush bush
	 * @param node node, at which segments end
	 * @param lca node, at which segments start
	 * @param deltaX flow to be shifted
	 */
	protected void shiftFlows(Network.Arc[] minTree, Network.Arc[] maxTree,
							  Bush bush, int node, int lca, double deltaX) {
		
		Network.Arc arc = minTree[node];
		while (arc != null && arc.endNode != lca) {
			arc.addFlow(deltaX);
			bush.addFlow(arc.index, deltaX);
			
			arc = minTree[arc.startNode];
		}
		
		arc = maxTree[node];
		while (arc != null && arc.endNode != lca) {
			arc.addFlow(-deltaX);
			bush.addFlow(arc.index, -deltaX);
			
			arc = maxTree[arc.startNode];
		}
	}
	
	
	/**
	 * Removes arcs from bush that have zero flow on
	 * the bush. Arcs in min tree are spared.
	 * @param bush bush
	 * @param minTree tree of min paths
	 */
	protected void removeUnusedArcs(Bush bush, Network.Arc[] minTree) {
		
		for (int i = 0; i < map.getArcs().length; i++) {
			if (bush.getArcFlow(i) <= 0)
				bush.removeArc(i);
		}
		
		for (Network.Arc arc : minTree) {
			if (arc != null)
				bush.addArc(arc.index);
		}
	}
}
