package cz.zcu.pperncka.data;


import com.google.common.util.concurrent.AtomicDoubleArray;
import cz.zcu.pperncka.Utils;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * Class representing static oriented graph/road network using
 * Compressed Sparse Row implementation. Nodes are represented
 * by the set {@code {0,...,nodes-1}}. First {@code zones} nodes
 * are zones.
 */
public class Network {
	
	/**
	 * Array of offsets to array {@code arcs}. On index i in
	 * this array is the index to array {@code arcs}, on which
	 * begin arcs starting in node i.
	 */
	private final int[] indices;
	
	/** Array of arcs in the network. */
	private final Arc[] arcs;
	
	/** Number of nodes in the network. */
	public final int nodes;
	
	/** Number of traffic zones in the network */
	public final int zones;
	
	/**
	 * Constructor creating network from adjacency list.
	 * @param adjacencyList adjacency list representation of graph
	 * @param zones         number of zones
	 */
	public Network(List<Arc>[] adjacencyList, int zones) {
		int numOfArcs = 0;
		for (List<Arc> arcVector : adjacencyList)
			numOfArcs += arcVector.size();
		
		this.nodes = adjacencyList.length;
		this.zones = zones;
		this.indices = new int[this.nodes + 1];
		this.arcs = new Arc[numOfArcs];
		
		int offset = 0;
		for (int startNode = 0; startNode < adjacencyList.length; startNode++) {
			indices[startNode] = offset;
			
			var neighbors = adjacencyList[startNode];
			for (int i = 0; i < neighbors.size(); i++) {
				arcs[offset + i] = new Arc(neighbors.get(i), offset + i);
			}
			
			offset += neighbors.size();
		}
		indices[indices.length - 1] = offset;
	}
	
	/**
	 * Returns arcs starting in given node.
	 * @param node starting node
	 * @return array view into array {@code arcs} representing
	 * subarray of arcs starting in {@code node}
	 */
	public ArrayView<Arc> neighborsOf(int node) {
		return new ArrayView<>(arcs, indices[node], indices[node + 1]);
	}
	
	/**
	 * Returns array of all arcs.
	 * @return array of all arcs
	 */
	public Arc[] getArcs() {
		return arcs;
	}
	
	/** Updates cost of all arcs according to their current flows. */
	public void updateCosts() {
		for (Arc arc : arcs)
			arc.cost = arc.BPR(0);
	}
	
	/**
	 * Calculates the objective function of this network
	 * w.r.t. current flows.
	 * @return value of objective function w.r.t. current flows
	 */
	public double objectiveFunction() {
		double of = 0;
		for (Arc arc : arcs)
			of += arc.BPRintegral();
		return of;
	}
	
	/**
	 * Calculates the gap metric w.r.t. current flows.
	 * @param odm OD matrix needed for AON assignment part
	 * @return value of gap w.r.t. current flows
	 */
	public double gap(Matrix odm) {
		double[] aonFlow = new double[arcs.length];
		
		for (int startZone = 0; startZone < zones; startZone++) {
			Arc[] minTree = minTree(startZone).second();
			
			for (int endZone = 0; endZone < zones; endZone++) {
				double trips = odm.get(startZone, endZone);
				if (trips == 0)
					continue;
				
				Arc arc = minTree[endZone];
				while (arc != null) {
					aonFlow[arc.index] += trips;
					
					arc = minTree[arc.startNode];
				}
			}
		}
		
		double gap = 0;
		for (Arc arc : arcs)
			gap += arc.cost * (aonFlow[arc.index] - arc.currentFlow);
		
		return gap;
	}
	
	/**
	 * Same as {@link #gap(Matrix)} but parallelized.
	 * @param odm        OD matrix needed for AON assignment part
	 * @param threadPool thread pool for executing tasks
	 * @return value of gap w.r.t. current flows, slightly undeterministic
	 */
	public double gap(Matrix odm, ExecutorService threadPool) {
		
		AtomicDoubleArray aonFlow = new AtomicDoubleArray(arcs.length);
		
		ArrayList<Callable<Void>> jobs = new ArrayList<>();
		for (int i = 0; i < zones; i++) {
			int startZone = i;
			
			jobs.add(() -> {
				Network.Arc[] minTree = minTree(startZone).second();
				
				for (int endZone = 0; endZone < zones; endZone++) {
					double trips = odm.get(startZone, endZone);
					if (trips == 0)
						continue;
					
					Network.Arc arc = minTree[endZone];
					while (arc != null) {
						aonFlow.getAndAdd(arc.index, trips);
						
						arc = minTree[arc.startNode];
					}
				}
				return null;
			});
		}
		Utils.tryInvokeAll(threadPool, jobs);
		
		double gap = 0;
		for (Network.Arc arc : arcs)
			gap += arc.cost * (aonFlow.get(arc.index) - arc.currentFlow);
		
		return gap;
	}
	
	/** Class representing an arc in the network. */
	public static class Arc {
		
		/** Node in which this arc ends. */
		public final int endNode;
		
		/** Node in which this arc starts. */
		public final int startNode;
		
		/** Index of this arc in the array {@code arcs}. */
		public final int index;
		
		/** Capacity of this arc. */
		public final double capacity;
		
		/** Free flow time of this arc. */
		public final double freeFlow;
		
		/** Current flow on this arc in the network. */
		private double currentFlow = 0;
		
		/** Cost of this arc determined by BPR function. */
		private double cost = 0;
		
		/**
		 * Creates new arc with given parameters and zero flow.
		 * @param startNode node in which this arc starts
		 * @param endNode   node in which this arc ends
		 * @param capacity  capacity of this arc
		 * @param freeFlow  free flow time of this arc
		 */
		public Arc(int startNode, int endNode, double capacity, double freeFlow) {
			this.startNode = startNode;
			this.endNode = endNode;
			this.capacity = capacity;
			this.freeFlow = freeFlow;
			this.index = -1;
		}
		
		/**
		 * Creates a copy of given arc with initialized index.
		 * @param copy  arc to be copied
		 * @param index index to array {@code arcs} in which will
		 *              this arc reside
		 */
		private Arc(Arc copy, int index) {
			this.startNode = copy.startNode;
			this.endNode = copy.endNode;
			this.capacity = copy.capacity;
			this.freeFlow = copy.freeFlow;
			this.currentFlow = copy.currentFlow;
			this.index = index;
		}
		
		/** Constructor for testing purposes. */
		public Arc(int startNode, int endNode, double capacity,
				   double freeFlow, double flow, int index) {
			this.startNode = startNode;
			this.endNode = endNode;
			this.capacity = capacity;
			this.freeFlow = freeFlow;
			this.currentFlow = flow;
			this.index = index;
		}
		
		/**
		 * Adds flow to current flow of this arc.
		 * @param delta flow to be added
		 */
		public void addFlow(double delta) {
			currentFlow += delta;
		}
		
		/**
		 * Returns current flow.
		 * @return current flow of this arc
		 */
		public double getCurrentFlow() {
			return currentFlow;
		}
		
		/**
		 * Returns cost of this arc. The cost does not
		 * need to be w.r.t. current flow unless
		 * {@link #updateCosts()} is called.
		 * @return cost of this arc
		 */
		public double getCost() {
			return cost;
		}
		
		/**
		 * Returns the value of BPR function (or cost) of this
		 * arc w.r.t current flow plus given delta.
		 * @param delta value that will be added to current flow to compute cost
		 * @return cost of this arc
		 */
		public double BPR(double delta) {
			double ratio = (currentFlow + delta) / capacity;
			double fourthPow = ratio * ratio * ratio * ratio;
			return freeFlow * (1 + 0.15 * fourthPow);
		}
		
		/**
		 * Returns the value of derivative of BPR function
		 * of this arc w.r.t current flow plus given delta.
		 * @param delta value that will be added to current flow
		 *              to compute cost derivative
		 * @return cost derivative of this arc
		 */
		public double BPRderivative(double delta) {
			double a = (currentFlow + delta) / capacity;
			double b = a * a * a;
			return 0.6 * freeFlow * b / capacity;
		}
		
		/**
		 * Returns the value of integral of BPR function
		 * w.r.t. current flow on the arc.
		 * @return the value of integral of BPR function
		 */
		public double BPRintegral() {
			double b = currentFlow / capacity;
			double power = b * b * b * b;
			return freeFlow * currentFlow * (1 + 0.03 * power);
		}
		
		@Override
		public String toString() {
			return startNode + "->" + endNode;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass())
				return false;
			
			Arc arc = (Arc) o;
			return startNode == arc.startNode && endNode == arc.endNode;
		}
	}
	
	/**
	 * Using Dijkstra, this method returns the tree of min paths
	 * and according distances to each node from given root node.
	 * Distances are calculated from arc costs.
	 * @param root node in which the algorithm starts
	 * @return pair (min. distances, min. tree)
	 */
	public Pair<double[], Network.Arc[]> minTree(int root) {
		double[] distance = new double[nodes];
		Arrays.fill(distance, Double.POSITIVE_INFINITY);
		distance[root] = 0;
		
		Arc[] previous = new Arc[nodes];
		
		PriorityQueue pq = new PriorityQueue(nodes);
		int[] mark = new int[nodes];
		
		
		pq.add(root, 0);
		while (!pq.isEmpty()) {
			int fromVertex = pq.popMin();
			mark[fromVertex] = 2;
			
			for (Arc arc : neighborsOf(fromVertex)) {
				int toVertex = arc.endNode;
				if (mark[toVertex] == 2)
					continue;
				
				double newDistance = distance[fromVertex] + arc.cost;
				if (mark[toVertex] == 0) {
					mark[toVertex] = 1;
					distance[toVertex] = newDistance;
					previous[toVertex] = arc;
					pq.add(toVertex, newDistance);
				} else if (mark[toVertex] == 1 && newDistance < distance[toVertex]) {
					distance[toVertex] = newDistance;
					previous[toVertex] = arc;
					pq.decreasePriority(toVertex, newDistance);
				}
			}
		}
		
		return new Pair<>(distance, previous);
	}
}
