package cz.zcu.pperncka.algorithm;

import cz.zcu.pperncka.data.Bush;
import cz.zcu.pperncka.data.Network;
import cz.zcu.pperncka.data.Network.Arc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@SuppressWarnings("unchecked")
@DisplayName("getTrees")
public class GetTreesTest {
	
	@DisplayName("Path P4")
	@Test
	void test1() {
		var arc01 = new Arc(0, 1, 1, 1, 1, 0);
		var arc12 = new Arc(1, 2, 1, 1, 1, 0);
		var arc23 = new Arc(2, 3, 1, 1, 1, 0);
		
		var adjList = new List[]{List.of(arc01), List.of(arc12), List.of(arc23), List.of()};
		Network network = new Network(adjList, 0);
		network.updateCosts();
		
		Bush bush = new Bush(3, 0);
		for (int i = 0; i < 3; i++) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}
		
		Algorithm a = new Algorithm(network, null, 0, 0);
		
		var res = a.getTrees(bush);
		
		assertArrayEquals(new Arc[]{null, arc01, arc12, arc23}, res.first());
		assertArrayEquals(new Arc[]{null, arc01, arc12, arc23}, res.second());
	}
	
	@DisplayName("Path P4 with non-active arc")
	@Test
	void test2() {
		var arc01 = new Arc(0, 1, 1, 1, 1, 0);
		var arc12 = new Arc(1, 2, 1, 1, 0, 0);
		var arc23 = new Arc(2, 3, 1, 1, 1, 0);
		
		var adjList = new List[]{List.of(arc01), List.of(arc12), List.of(arc23), List.of()};
		Network network = new Network(adjList, 0);
		network.updateCosts();
		
		Bush bush = new Bush(3, 0);
		for (int i = 0; i < 3; i++) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}
		bush.addFlow(1, -1);
		
		Algorithm a = new Algorithm(network, null, 0, 0);
		
		var res = a.getTrees(bush);
		assertArrayEquals(new Arc[]{null, arc01, arc12, arc23}, res.first());
		assertArrayEquals(new Arc[]{null, arc01, null, null}, res.second());
	}
	
	@DisplayName("Cyclic graph C3")
	@Test
	void test3() {
		var arc01 = new Arc(0, 1, 1, 1, 1, 0);
		var arc02 = new Arc(0, 2, 1, 1, 1, 0);
		var arc12 = new Arc(1, 2, 1, 1, 1, 0);
		
		var adjList = new List[]{List.of(arc01, arc02), List.of(arc12), List.of()};
		Network network = new Network(adjList, 0);
		network.updateCosts();
		
		Bush bush = new Bush(3, 0);
		for (int i = 0; i < 3; i++) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}
		
		Algorithm a = new Algorithm(network, null, 0, 0);
		
		var res = a.getTrees(bush);
		
		assertArrayEquals(new Arc[]{null, arc01, arc02}, res.first());
		assertArrayEquals(new Arc[]{null, arc01, arc12}, res.second());
	}
	
	@DisplayName("Cyclic graph C3 (2)")
	@Test
	void test4() {
		var arc01 = new Arc(0, 1, 1, 1, 1, 0);
		var arc12 = new Arc(1, 2, 1, 1, 1, 0);
		var arc02 = new Arc(0, 2, 1, 9, 1, 0);
		
		var adjList = new List[]{List.of(arc01, arc02), List.of(arc12), List.of()};
		Network network = new Network(adjList, 0);
		network.updateCosts();
		
		Bush bush = new Bush(3, 0);
		for (int i = 0; i < 3; i++) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}
		
		Algorithm a = new Algorithm(network, null, 0, 0);
		
		var res = a.getTrees(bush);
		
		assertArrayEquals(new Arc[]{null, arc01, arc12}, res.first());
		assertArrayEquals(new Arc[]{null, arc01, arc02}, res.second());
	}
	
	@DisplayName("Cyclic graph C3 with non-active arc")
	@Test
	void test5() {
		var arc01 = new Arc(0, 1, 1, 1, 1, 0);
		var arc12 = new Arc(1, 2, 1, 1, 1, 0);
		var arc02 = new Arc(0, 2, 1, 1, 1, 0);
		
		var adjList = new List[]{List.of(arc01, arc02), List.of(arc12), List.of()};
		Network network = new Network(adjList, 0);
		network.updateCosts();
		
		Bush bush = new Bush(3, 0);
		for (int i = 0; i < 3; i++) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}
		bush.addFlow(2, -1);
		
		Algorithm a = new Algorithm(network, null, 0, 0);
		
		var res = a.getTrees(bush);
		
		assertArrayEquals(new Arc[]{null, arc01, arc02}, res.first());
		assertArrayEquals(new Arc[]{null, arc01, arc02}, res.second());
	}
	
	@DisplayName("Bull graph")
	@Test
	void test6() {
		var arc01 = new Arc(0, 1, 1, 1, 1, 0);
		var arc12 = new Arc(1, 2, 1, 1, 1, 0);
		var arc13 = new Arc(1, 3, 1, 1, 1, 0);
		var arc23 = new Arc(2, 3, 1, 1, 1, 0);
		var arc34 = new Arc(3, 4, 1, 1, 1, 0);
		
		var adjList = new List[]{List.of(arc01), List.of(arc12, arc13), List.of(arc23), List.of(arc34), List.of()};
		Network network = new Network(adjList, 0);
		network.updateCosts();
		
		Bush bush = new Bush(5, 0);
		for (int i = 0; i < 5; i++) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}
		
		Algorithm a = new Algorithm(network, null, 0, 0);
		
		var res = a.getTrees(bush);
		
		assertArrayEquals(new Arc[]{null, arc01, arc12, arc13, arc34}, res.first());
		assertArrayEquals(new Arc[]{null, arc01, arc12, arc23, arc34}, res.second());
	}
	
	@DisplayName("Graph with alternative max path")
	@Test
	void test7() {
		//  0 -> 1 -> 2 <- 6
		//       ∨    ^    ^
		//       3 -> 4 => 5
		var arc01 = new Arc(0, 1, 1, 1, 1, 0);
		var arc12 = new Arc(1, 2, 1, 1, 1, 0);
		var arc13 = new Arc(1, 3, 1, 1, 1, 0);
		var arc34 = new Arc(3, 4, 1, 1, 1, 0);
		var arc42 = new Arc(4, 2, 1, 1, 1, 0);
		var arc45 = new Arc(4, 5, 1, 1, 0, 0);
		var arc56 = new Arc(5, 6, 1, 1, 1, 0);
		var arc62 = new Arc(6, 2, 1, 1, 1, 0);
		
		var adjList = new List[]{List.of(arc01), List.of(arc12, arc13), List.of(), List.of(arc34), List.of(arc42, arc45), List.of(arc56), List.of(arc62)};
		Network network = new Network(adjList, 0);
		network.updateCosts();
		
		Bush bush = new Bush(8, 0);
		for (int i = 0; i < 8; i++) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}
		bush.addFlow(5, -1);
		
		Algorithm a = new Algorithm(network, null, 0, 0);
		
		var res = a.getTrees(bush);
		
		assertArrayEquals(new Arc[]{null, arc01, arc12, arc13, arc34, arc45, arc56}, res.first());
		assertArrayEquals(new Arc[]{null, arc01, arc42, arc13, arc34, null, null}, res.second());
	}
	
	@DisplayName("Bush is not whole graph")
	@Test
	void test8() {
		var arc01 = new Arc(0, 1, 1, 1, 1, 0);
		var arc12 = new Arc(1, 2, 1, 1, 1, 0);
		var arc23 = new Arc(2, 3, 1, 1, 1, 0);
		
		var adjList = new List[]{List.of(arc01), List.of(arc12), List.of(arc23), List.of()};
		Network network = new Network(adjList, 0);
		network.updateCosts();
		
		Bush bush = new Bush(3, 0);
		for (int i = 0; i < 2; i++) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}
		
		Algorithm a = new Algorithm(network, null, 0, 0);
		
		var res = a.getTrees(bush);
		
		assertArrayEquals(new Arc[]{null, arc01, arc12, null}, res.first());
		assertArrayEquals(new Arc[]{null, arc01, arc12, null}, res.second());
	}
}
