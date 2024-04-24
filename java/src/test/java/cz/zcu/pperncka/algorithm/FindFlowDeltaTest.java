package cz.zcu.pperncka.algorithm;

import cz.zcu.pperncka.data.Bush;
import cz.zcu.pperncka.data.Network;
import cz.zcu.pperncka.data.Network.Arc;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("unchecked")
@DisplayName("findFlowDelta")
public class FindFlowDeltaTest {
	
	static Algorithm a;
	
	@BeforeAll
	static void beforeAll() {
		Network n = new Network(new List[0], 0);
		a = new Algorithm(n, null, 0, 0);
	}
	
	@DisplayName("Case from Wikipedia - diamond")
	@Test
	void test1() {
		var arc01 = new Arc(0, 1, 	 1,  0, 8000, 0);
		var arc02 = new Arc(0, 2, 	 1,  0,    0, 0);
		var arc13 = new Arc(1, 3, 1000, 15, 8000, 0);
		var arc23 = new Arc(2, 3, 3000, 20,    0, 0);
		
		var minTree = new Arc[]{null, arc01, arc02, arc23};
		var maxTree = new Arc[]{null, arc01, arc02, arc13};
		
		Bush bush = new Bush(1, 0);
		bush.addFlow(0, 10000);
		
		assertEquals(5847, Math.round(a.findFlowDelta(minTree, maxTree, bush, 3, 0)));
	}
	
	@DisplayName("Case from Wikipedia - diamond with tail")
	@Test
	void test2() {
		var arc01 = new Arc(0, 1, 	 1,  0,    0, 0);
		var arc12 = new Arc(1, 2, 	 1,  0, 8000, 0);
		var arc13 = new Arc(1, 3, 	 1,  0,    0, 0);
		var arc24 = new Arc(2, 4, 1000, 15, 8000, 0);
		var arc34 = new Arc(3, 4, 3000, 20,    0, 0);
		
		var minTree = new Arc[]{null, arc01, arc12, arc13, arc34};
		var maxTree = new Arc[]{null, arc01, arc12, arc13, arc24};
		
		Bush bush = new Bush(1, 0);
		bush.addFlow(0, 10000);
		
		assertEquals(5847, Math.round(a.findFlowDelta(minTree, maxTree, bush, 4, 1)));
	}
	
	@DisplayName("Test maxDeltaX <= μ")
	@Test
	void test3() {
		var arc01 = new Arc(0, 1, 	 1,  0, 8000, 0);
		var arc02 = new Arc(0, 2, 	 1,  0,    0, 0);
		var arc13 = new Arc(1, 3, 1000, 15, 8000, 0);
		var arc23 = new Arc(2, 3, 3000, 20,    0, 0);
		
		var minTree = new Arc[]{null, arc01, arc02, arc23};
		var maxTree = new Arc[]{null, arc01, arc02, arc13};
		
		Bush bush = new Bush(1, 0);
		bush.addFlow(0, 1000);
		
		assertEquals(1000, a.findFlowDelta(minTree, maxTree, bush, 3, 0));
	}
	
	@DisplayName("Nonconverging case")
	@Test
	void test4() {
		var arc01 = new Arc(0, 1,  17782.7941, 2, 1900, 0);
		var arc12 = new Arc(1, 2,  4908.82673, 6,  700, 0);
		var arc23 = new Arc(2, 3, 		10000, 5, 20783.42548485832, 0);
		var arc04 = new Arc(0, 4, 		10000, 5, 2300, 0);
		var arc43 = new Arc(4, 3, 13915.78842, 3, 1900, 0);
		
		var minTree = new Arc[]{null, arc01, arc12, arc23, arc04};
		var maxTree = new Arc[]{null, arc01, arc12, arc43, arc04};
		
		Bush bush = new Bush(1, 0);
		bush.addFlow(0, 700);
		
		assertEquals(700, a.findFlowDelta(minTree, maxTree, bush, 3, 0));
	}
	
	@DisplayName("maxDeltaX == 0")
	@Test
	void test5() {
		var arc01 = new Arc(0, 1, 	 1,  0, 8000, 0);
		var arc02 = new Arc(0, 2, 	 1,  0,    0, 0);
		var arc13 = new Arc(1, 3, 1000, 15, 8000, 0);
		var arc23 = new Arc(2, 3, 3000, 20,    0, 0);
		
		var minTree = new Arc[]{null, arc01, arc02, arc23};
		var maxTree = new Arc[]{null, arc01, arc02, arc13};
		
		Bush bush = new Bush(1, 0);
		
		assertEquals(0, a.findFlowDelta(minTree, maxTree, bush, 3, 0));
	}
}
