package cz.zcu.pperncka;

import com.beust.jcommander.JCommander;
import cz.zcu.pperncka.algorithm.Algorithm;
import cz.zcu.pperncka.algorithm.ParallelAlgorithm;
import cz.zcu.pperncka.data.Matrix;
import cz.zcu.pperncka.data.Network;
import cz.zcu.pperncka.io.args.Args;
import cz.zcu.pperncka.io.TNTPParser;
import cz.zcu.pperncka.io.Writer;

/** Main class of the program. */
public class Main {
	
	/**
	 * Entry point of the program.
	 * @param argv command line arguments
	 */
	public static void main(String[] argv) {
		Args args = new Args();
		var jcom = new JCommander(args, null, argv);
		if (args.help) {
			jcom.usage();
			return;
		}
		
		System.out.print("Loading network... ");
		long startTime = System.currentTimeMillis();
		Network map = TNTPParser.parseNetwork(args.networkFile);
		long endTime = System.currentTimeMillis();
		System.out.println("OK (" + (endTime - startTime) + "ms)");
		
		System.out.print("Loading OD matrix... ");
		startTime = System.currentTimeMillis();
		Matrix odMatrix = TNTPParser.parseODMatrix(args.matrixFile);
		endTime = System.currentTimeMillis();
		System.out.println("OK (" + (endTime - startTime) + "ms)");
		
		Algorithm a = (args.threads == 0)
				? new Algorithm(map, odMatrix, args.relativeGap, args.iterations)
				: new ParallelAlgorithm(map, odMatrix, args.relativeGap, args.iterations, args.threads);
		
		startTime = System.currentTimeMillis();
		var arcs = a.start();
		endTime = System.currentTimeMillis();
		System.out.println("Static traffic assigment computation time is " + (endTime - startTime) + " ms.");
		
		Writer.writeArcs(arcs, args.outputFile);
	}
}