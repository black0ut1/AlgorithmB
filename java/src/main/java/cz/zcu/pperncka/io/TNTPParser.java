package cz.zcu.pperncka.io;

import cz.zcu.pperncka.data.Matrix;
import cz.zcu.pperncka.data.Network;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/** Class with methods for parsing TNTP files. */
public class TNTPParser {
	
	private final static String COMMENT_SIGN = "~";
	private final static String HEADER_END = "<END OF METADATA>";
	private final static String ZONES_NUMBER = "<NUMBER OF ZONES>";
	private final static String NODES_NUMBER = "<NUMBER OF NODES>";
	
	/**
	 * Parses TNTP file representing road network.
	 * @param path path to network TNTP file
	 * @return graph of road network
	 */
	public static Network parseNetwork(String path) {
		try (var reader = new BufferedReader(new FileReader(path))) {
			var header = parseHeader(reader);
			
			int nodes = Integer.parseInt(header.get(NODES_NUMBER));
			int zones = Integer.parseInt(header.get(ZONES_NUMBER));
			
			Vector<Network.Arc>[] graph = new Vector[nodes];
			for (int i = 0; i < graph.length; i++)
				graph[i] = new Vector<>();
			
			String line;
			while ((line = reader.readLine()) != null) {
				line = removeComments(line);
				if (line.isEmpty())
					continue;
				
				String[] split = line.split("[ \t]+");
				
				int fromNode = Integer.parseInt(split[0]) - 1;
				int endNode = Integer.parseInt(split[1]) - 1;
				double capacity = Double.parseDouble(split[2]);
				double freeFlow = Double.parseDouble(split[4]);
				if (freeFlow == 0)
					freeFlow = .0001;
				Network.Arc arc = new Network.Arc(
						fromNode, endNode, capacity, freeFlow);
				
				graph[fromNode].add(arc);
			}
			
			return new Network(graph, zones);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Parses TNTP file representing OD matrix.
	 * @param path path to matrix TNTP file
	 * @return OD matrix
	 */
	public static Matrix parseODMatrix(String path) {
		try (var reader = new BufferedReader(new FileReader(path))) {
			var header = parseHeader(reader);
			
			int zonesNumber = Integer.parseInt(header.get(ZONES_NUMBER));
			Matrix odMatrix = new Matrix(zonesNumber);
			
			int fromNode = 0;
			String line;
			while ((line = reader.readLine()) != null) {
				line = removeComments(line);
				if (line.isEmpty())
					continue;
				
				if (line.contains("Origin"))
					fromNode = Integer.parseInt(line.replace("Origin", "").trim()) - 1;
				else {
					
					String[] split = line.replaceAll("[ \t]", "").split(";");
					for (String s : split) {
						
						String[] pair = s.split(":");
						int toNode = Integer.parseInt(pair[0]) - 1;
						double demand = Double.parseDouble(pair[1]);
						odMatrix.set(fromNode, toNode, demand);
					}
				}
			}
			
			return odMatrix;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Parses header key-value pairs of TNTP file into a map.
	 * @param reader BufferedReader of TNTP file with
	 *               pointer at the start of the header
	 * @return map of parsed header key-value pairs
	 * @throws IOException if an I/O error occurs
	 */
	private static Map<String, String> parseHeader(BufferedReader reader) throws IOException {
		HashMap<String, String> header = new HashMap<>();
		
		String line;
		while (!(line = reader.readLine().trim()).equals(HEADER_END)) {
			line = removeComments(line);
			if (line.isEmpty())
				continue;
			
			String[] split = line.split(">");
			header.put((split[0] + '>').trim(),
					split.length == 1 ? null : split[1].trim());
		}
		
		return header;
	}
	
	/**
	 * Method for sanitizing lines of TNTP files, removing comments
	 * and trimming whitespaces.
	 * @param line dirty line
	 * @return sanitized line
	 */
	private static String removeComments(String line) {
		return line.trim().split(COMMENT_SIGN)[0].trim();
	}
}
