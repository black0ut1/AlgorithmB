package cz.zcu.pperncka.algorithm;

import cz.zcu.pperncka.Utils;
import cz.zcu.pperncka.data.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Class representing parallel Algorithm B with its input and inner state. */
public class ParallelAlgorithm extends Algorithm {
	
	/** Number of worker threads. */
	protected final int threads;
	
	/** Thread pool for executing tasks. */
	protected final ExecutorService threadPool;
	
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
	public ParallelAlgorithm(Network map, Matrix odMatrix, double relativeGap, int iterations, int threads) {
		super(map, odMatrix, relativeGap, iterations);
		
		this.threads = threads;
		this.threadPool = Executors.newFixedThreadPool(threads);
	}
	
	@Override
	public Network.Arc[] start() {
		map.updateCosts();
		Collection<Callable<Void>> jobs = new ArrayList<>();
		for (int root = 0; root < bushes.length; root++) {
			
			int finalRoot = root;
			jobs.add(() -> {
				bushes[finalRoot] = createBush(finalRoot);
				return null;
			});
		}
		Utils.tryInvokeAll(threadPool, jobs);
		
		
		Network.Arc[] mapArcs = map.getArcs();
		for (Bush bush : bushes)
			for (Network.Arc mapArc : mapArcs)
				mapArc.addFlow(bush.getArcFlow(mapArc.index));
		map.updateCosts();
		
		
		System.out.println("===================================");
		System.out.print("Static traffic assignment algorithm B - ");
		System.out.println("Parallel version (" + threads + " threads)");
		System.out.println("Max. iterations: " + iterations);
		if (relativeGap != 0)
			System.out.printf(Locale.ROOT, "Relative gap: %.15f%n", relativeGap);
		System.out.printf(Locale.ROOT,
				"Starting objective function: %.15f%n", map.objectiveFunction());
		System.out.println("===================================");
		
		
		double currentRelativeGap = Double.POSITIVE_INFINITY;
		double maxLb = Double.NEGATIVE_INFINITY;
		int currentIteration = 0;
		
		while ((currentIteration < iterations && this.relativeGap == 0) ||
				(currentIteration < iterations && currentRelativeGap > this.relativeGap)) {
			System.out.println("Iteration " + currentIteration);
			
			for (int i = 0; i < bushes.length; i += threads) {
				
				Pair<Network.Arc[], Network.Arc[]>[] trees = new Pair[threads];
				int[][] lcas = new int[threads][];
				
				
				Collection<Callable<Void>> jobs2 = new ArrayList<>();
				for (int j = 0; j < threads; j++) {
					if (i + j == bushes.length)
						break;
					Bush bush = bushes[i + j];
					
					int J = j;
					jobs2.add(() -> {
						improveBush(bush);
						trees[J] = getTrees(bush);
						
						lcas[J] = new int[map.nodes];
						for (int node = 0; node < map.nodes; node++)
							lcas[J][node] = LCA(trees[J].first(), trees[J].second(), node);
						
						return null;
					});
				}
				Utils.tryInvokeAll(threadPool, jobs2);
				
				
				for (int j = 0; j < threads; j++) {
					if (i + j == bushes.length)
						break;
					Bush bush = bushes[i + j];
					
					var minTree = trees[j].first();
					var maxTree = trees[j].second();
					for (int node = 0; node < map.nodes; node++) {
						int lca = lcas[j][node];
						if (lca == -1)
							continue;
						
						double deltaX = findFlowDelta(minTree, maxTree, bush, node, lca);
						if (deltaX == 0)
							continue;
						
						shiftFlows(minTree, maxTree, bush, node, lca, deltaX);
					}
				}
				
				
				Collection<Callable<Void>> jobs3 = new ArrayList<>();
				for (int j = 0; j < threads; j++) {
					if (i + j == bushes.length)
						break;
					Bush bush = bushes[i + j];
					
					int J = j;
					jobs3.add(() -> {
						removeUnusedArcs(bush, trees[J].first());
						return null;
					});
				}
				Utils.tryInvokeAll(threadPool, jobs3);
				
				map.updateCosts();
			}
			
			double of = map.objectiveFunction();
			System.out.printf(Locale.ROOT, "Objective function: %.15f%n", of);
			
			if (this.relativeGap != 0) {
				double gap = map.gap(odMatrix, threadPool);
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
		
		threadPool.shutdown();
		try {
			if (!threadPool.awaitTermination(10, TimeUnit.MILLISECONDS))
				System.out.println("Waiting for thread pool termination timed out");
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		return map.getArcs();
	}
}
