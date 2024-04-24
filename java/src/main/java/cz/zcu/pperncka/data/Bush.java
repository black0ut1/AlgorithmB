package cz.zcu.pperncka.data;

/**
 * Class representing a bush constructed from a {@code Network}.
 * Bush is an acyclic subgraph with only one root.
 */
public class Bush {
	
	/**
	 * Array indicating which arcs of {@code arcs}
	 * array of {@code Network} are in this bush.
	 */
	private final boolean[] arcFlags;
	
	/** Array representing flow on this bush for each arc on this bush */
	private final double[] flows;
	
	/** Root of this bush. */
	public final int root;
	
	/**
	 * Constructor creating bush with given root and
	 * supporting given number of edges.
	 * @param arcsNum number of edges in original network
	 * @param root    node from which was this bush created
	 */
	public Bush(int arcsNum, int root) {
		this.arcFlags = new boolean[arcsNum];
		this.flows = new double[arcsNum];
		this.root = root;
	}
	
	/**
	 * Adds arc to the bush.
	 * @param arcIndex index of added arc
	 */
	public void addArc(int arcIndex) {
		arcFlags[arcIndex] = true;
	}
	
	/**
	 * Removes arc from the bush.
	 * @param arcIndex index of removed arc
	 */
	public void removeArc(int arcIndex) {
		arcFlags[arcIndex] = false;
	}
	
	/**
	 * Returns if arc exists in this bush.
	 * @param arcIndex index of arc
	 * @return true, if arc exists in the bush, false otherwise
	 */
	public boolean arcExists(int arcIndex) {
		return arcFlags[arcIndex];
	}
	
	/**
	 * Returns flow on the bush of arc.
	 * @param arcIndex index of arc
	 * @return flow on the bush
	 */
	public double getArcFlow(int arcIndex) {
		return flows[arcIndex];
	}
	
	/**
	 * Adds to a flow on the bush of arc.
	 * @param arcIndex index of arc
	 * @param flow     flow to be added
	 */
	public void addFlow(int arcIndex, double flow) {
		flows[arcIndex] += flow;
	}
}
