package cz.zcu.pperncka.algorithm;

import cz.zcu.pperncka.data.Bush;
import cz.zcu.pperncka.data.Network;
import cz.zcu.pperncka.data.Network.Arc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
@DisplayName("removeUnusedArcs")
public class RemoveUnusedArcsTest {
	
	@DisplayName("All arcs with positive flow, empty minTree")
	@Test
	void test1() {
		var arc01 = new Arc(0, 1, 1, 1, 1, 0);
		var arc02 = new Arc(0, 2, 1, 1, 1, 0);
		var arc13 = new Arc(1, 3, 1, 1, 1, 0);
		var arc23 = new Arc(2, 3, 1, 1, 1, 0);
		
		var adjList = new List[]{List.of(arc01, arc02), List.of(arc13), List.of(arc23), List.of()};
		Network network = new Network(adjList, 0);
		network.updateCosts();
		
		Bush bush = new Bush(4, 0);
		for (int i = 0; i < 4; i++) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}
		
		var minTree = new Arc[0];
		Algorithm a = new Algorithm(network, null, 0, 0);
		
		a.removeUnusedArcs(bush, minTree);
		
		assertTrue(bush.arcExists(0));
		assertTrue(bush.arcExists(1));
		assertTrue(bush.arcExists(2));
		assertTrue(bush.arcExists(3));
	}
	
	@DisplayName("All arcs with no flow, empty minTree")
	@Test
	void test2() {
		var arc01 = new Arc(0, 1, 1, 1, 1, 0);
		var arc02 = new Arc(0, 2, 1, 1, 1, 0);
		var arc13 = new Arc(1, 3, 1, 1, 1, 0);
		var arc23 = new Arc(2, 3, 1, 1, 1, 0);
		
		var adjList = new List[]{List.of(arc01, arc02), List.of(arc13), List.of(arc23), List.of()};
		Network network = new Network(adjList, 0);
		network.updateCosts();
		
		Bush bush = new Bush(4, 0);
		for (int i = 0; i < 4; i++)
			bush.addArc(i);
		
		var minTree = new Arc[0];
		Algorithm a = new Algorithm(network, null, 0, 0);
		
		a.removeUnusedArcs(bush, minTree);
		
		assertFalse(bush.arcExists(0));
		assertFalse(bush.arcExists(1));
		assertFalse(bush.arcExists(2));
		assertFalse(bush.arcExists(3));
	}
	
	@DisplayName("All arcs with no flow, all arcs in minTree")
	@Test
	void test3() {
		var arc01 = new Arc(0, 1, 1, 1, 1, 0);
		var arc02 = new Arc(0, 2, 1, 1, 1, 0);
		var arc13 = new Arc(1, 3, 1, 1, 1, 0);
		var arc23 = new Arc(2, 3, 1, 1, 1, 0);
		
		var adjList = new List[]{List.of(arc01, arc02), List.of(arc13), List.of(arc23), List.of()};
		Network network = new Network(adjList, 0);
		network.updateCosts();
		
		Bush bush = new Bush(4, 0);
		for (int i = 0; i < 4; i++)
			bush.addArc(i);
		
		var minTree = network.getArcs();
		Algorithm a = new Algorithm(network, null, 0, 0);
		
		a.removeUnusedArcs(bush, minTree);
		
		assertTrue(bush.arcExists(0));
		assertTrue(bush.arcExists(1));
		assertTrue(bush.arcExists(2));
		assertTrue(bush.arcExists(3));
	}
	
	@DisplayName("One arc with no flow, empty minTree")
	@Test
	void test4() {
		var arc01 = new Arc(0, 1, 1, 1, 1, 0);
		var arc02 = new Arc(0, 2, 1, 1, 1, 0);
		var arc13 = new Arc(1, 3, 1, 1, 1, 0);
		var arc23 = new Arc(2, 3, 1, 1, 1, 0);
		
		var adjList = new List[]{List.of(arc01, arc02), List.of(arc13), List.of(arc23), List.of()};
		Network network = new Network(adjList, 0);
		network.updateCosts();
		
		Bush bush = new Bush(4, 0);
		for (int i = 0; i < 4; i++) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}
		bush.addFlow(3, -1);
		
		var minTree = new Arc[0];
		Algorithm a = new Algorithm(network, null, 0, 0);
		
		a.removeUnusedArcs(bush, minTree);
		
		assertTrue(bush.arcExists(0));
		assertTrue(bush.arcExists(1));
		assertTrue(bush.arcExists(2));
		assertFalse(bush.arcExists(3));
	}
}
