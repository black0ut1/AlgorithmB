package cz.zcu.pperncka.algorithm;

import cz.zcu.pperncka.data.Bush;
import cz.zcu.pperncka.data.Network;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@SuppressWarnings("unchecked")
@DisplayName("getMaxDistance")
public class GetMaxDistanceTest {
	
	public static double EPSILON = 1e-10;
	
	@DisplayName("Path P4")
	@Test
	void test1() {
		var arc01 = new Network.Arc(0, 1, 1, 1, 0, 0);
		var arc12 = new Network.Arc(1, 2, 1, 1, 0, 0);
		var arc23 = new Network.Arc(2, 3, 1, 1, 0, 0);
		
		var adjList = new List[]{List.of(arc01), List.of(arc12), List.of(arc23), List.of()};
		Network network = new Network(adjList, 0);
		network.updateCosts();
		
		Bush bush = new Bush(3, 0);
		for (int i = 0; i < 3; i++)
			bush.addArc(i);
		
		Algorithm a = new Algorithm(network, null, 0, 0);
		
		var res = a.getMaxDistance(bush);
		
		assertArrayEquals(new double[]{0, 1, 2, 3}, res);
	}
	
	@DisplayName("Path P4 (2)")
	@Test
	void test2() {
		var arc01 = new Network.Arc(0, 1, 1, 1, 1, 0);
		var arc12 = new Network.Arc(1, 2, 1, 1, 1, 0);
		var arc23 = new Network.Arc(2, 3, 1, 1, 1, 0);
		
		var adjList = new List[]{List.of(arc01), List.of(arc12), List.of(arc23), List.of()};
		Network network = new Network(adjList, 0);
		network.updateCosts();
		
		Bush bush = new Bush(3, 0);
		for (int i = 0; i < 3; i++)
			bush.addArc(i);
		
		Algorithm a = new Algorithm(network, null, 0, 0);
		
		var res = a.getMaxDistance(bush);
		
		assertArrayEquals(new double[]{0, 1.15, 2.3, 3.45}, res, EPSILON);
	}
	
	@DisplayName("Cyclic graph C3")
	@Test
	void test3() {
		var arc01 = new Network.Arc(0, 1, 1, 1, 0, 0);
		var arc02 = new Network.Arc(0, 2, 1, 1, 0, 0);
		var arc12 = new Network.Arc(1, 2, 1, 1, 0, 0);
		
		var adjList = new List[]{List.of(arc01, arc02), List.of(arc12), List.of()};
		Network network = new Network(adjList, 0);
		network.updateCosts();
		
		Bush bush = new Bush(3, 0);
		for (int i = 0; i < 3; i++)
			bush.addArc(i);
		
		Algorithm a = new Algorithm(network, null, 0, 0);
		
		var res = a.getMaxDistance(bush);
		
		assertArrayEquals(new double[]{0, 1, 2}, res);
	}
	
	@DisplayName("Bull graph")
	@Test
	void test6() {
		var arc01 = new Network.Arc(0, 1, 1, 1, 0, 0);
		var arc12 = new Network.Arc(1, 2, 1, 1, 0, 0);
		var arc13 = new Network.Arc(1, 3, 1, 1, 0, 0);
		var arc23 = new Network.Arc(2, 3, 1, 1, 0, 0);
		var arc34 = new Network.Arc(3, 4, 1, 1, 0, 0);
		
		var adjList = new List[]{List.of(arc01), List.of(arc12, arc13), List.of(arc23), List.of(arc34), List.of()};
		Network network = new Network(adjList, 0);
		network.updateCosts();
		
		Bush bush = new Bush(5, 0);
		for (int i = 0; i < 5; i++)
			bush.addArc(i);
		
		Algorithm a = new Algorithm(network, null, 0, 0);
		
		var res = a.getMaxDistance(bush);
		
		assertArrayEquals(new double[]{0, 1, 2, 3, 4}, res);
	}
}
