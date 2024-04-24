package cz.zcu.pperncka.io;

import cz.zcu.pperncka.data.Network;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

/** Class with method for writing flows. */
public class Writer {
	
	/**
	 * Writes flows assigned to network arcs to a file.
	 * @param arcs       array of arcs with assigned flows
	 * @param outputFile path to a flow TNTP file which will be created
	 */
	public static void writeArcs(Network.Arc[] arcs, String outputFile) {
		try (BufferedWriter bfw = new BufferedWriter(new FileWriter(outputFile))) {
			
			bfw.write("From\tTo\tVolume\tCost\n");
			for (Network.Arc arc : arcs) {
				bfw.write((arc.startNode + 1) + "\t"
						+ (arc.endNode + 1) + "\t"
						+ String.format(Locale.ROOT, "%.15f", arc.getCurrentFlow()) + "\t"
						+ String.format(Locale.ROOT, "%.15f", arc.getCost()) + "\n");
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
