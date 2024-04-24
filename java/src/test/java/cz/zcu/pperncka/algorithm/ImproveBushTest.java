package cz.zcu.pperncka.algorithm;

import cz.zcu.pperncka.data.Bush;
import cz.zcu.pperncka.data.Network;
import cz.zcu.pperncka.data.Network.Arc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("unchecked")
@DisplayName("improveBush")
public class ImproveBushTest {
	
	@Test
	void test1() {
		var arc01 = new Arc(0, 1, 1, 1, 1, 0);
		var arc02 = new Arc(0, 2, 1, 1, 1, 0);
		var arc12 = new Arc(1, 2, 1, 1, 1, 0);
		
		var adjList = new List[]{List.of(arc01, arc02), List.of(arc12), List.of()};
		Network network = new Network(adjList, 0);
		network.updateCosts();
		
		Bush bush = new Bush(3, 0);
		int[] arcsInBush = {0, 2};
		for (int i : arcsInBush) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}
		
		Algorithm a = new Algorithm(network, null, 0, 0);
		
		a.improveBush(bush);
		
		assertTrue(bush.arcExists(0));
		assertTrue(bush.arcExists(1));
		assertTrue(bush.arcExists(2));
	}
	
	@Test
	void test2() {
		var arc01 = new Arc(0, 1, 1, 1, 1, 0);
		var arc02 = new Arc(0, 2, 1, 1, 1, 0);
		var arc12 = new Arc(1, 2, 1, 1, 1, 0);
		var arc23 = new Arc(2, 3, 1, 1, 1, 0);
		
		var adjList = new List[]{List.of(arc01, arc02), List.of(arc12), List.of(arc23), List.of()};
		Network network = new Network(adjList, 0);
		network.updateCosts();
		
		Bush bush = new Bush(4, 0);
		for (int i = 0; i < 3; i++) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}
		
		Algorithm a = new Algorithm(network, null, 0, 0);
		
		a.improveBush(bush);
		
		assertTrue(bush.arcExists(0));
		assertTrue(bush.arcExists(1));
		assertTrue(bush.arcExists(2));
		assertFalse(bush.arcExists(3));
	}
}
