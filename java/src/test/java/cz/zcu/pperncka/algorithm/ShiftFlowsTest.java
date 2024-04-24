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
@DisplayName("shiftFlows")
public class ShiftFlowsTest {
	
	static Algorithm a;
	
	@BeforeAll
	static void beforeAll() {
		Network n = new Network(new List[0], 0);
		a = new Algorithm(n, null, 0, 0);
	}
	
	@DisplayName("Diamond graph - flows in network")
	@Test
	void test1() {
		double maxPathFlow = 1000;
		double flowDelta = 300;
		var arc01 = new Arc(0, 1, 0, 0, maxPathFlow, 0);
		var arc02 = new Arc(0, 2, 0, 0, 		  0, 1);
		var arc13 = new Arc(1, 3, 0, 0, maxPathFlow, 2);
		var arc23 = new Arc(2, 3, 0, 0, 		  0, 3);
		
		var minTree = new Arc[]{null, arc01, arc02, arc23};
		var maxTree = new Arc[]{null, arc01, arc02, arc13};
		
		Bush b = new Bush(4, 0);
		a.shiftFlows(minTree, maxTree, b, 3, 0, flowDelta);
		
		assertEquals(maxPathFlow - flowDelta, arc01.getCurrentFlow());
		assertEquals(maxPathFlow - flowDelta, arc13.getCurrentFlow());
		assertEquals(flowDelta, arc02.getCurrentFlow());
		assertEquals(flowDelta, arc23.getCurrentFlow());
	}
	
	@DisplayName("Diamond graph - flows in bush")
	@Test
	void test2() {
		double maxPathFlow = 1000;
		double flowDelta = 300;
		var arc01 = new Arc(0, 1, 0, 0, maxPathFlow, 0);
		var arc02 = new Arc(0, 2, 0, 0, 		  0, 1);
		var arc13 = new Arc(1, 3, 0, 0, maxPathFlow, 2);
		var arc23 = new Arc(2, 3, 0, 0, 		  0, 3);
		
		var minTree = new Arc[]{null, arc01, arc02, arc23};
		var maxTree = new Arc[]{null, arc01, arc02, arc13};
		
		Bush bush = new Bush(4, 0);
		bush.addFlow(0, maxPathFlow);
		bush.addFlow(2, maxPathFlow);
		
		a.shiftFlows(minTree, maxTree, bush, 3, 0, flowDelta);
		
		assertEquals(maxPathFlow - flowDelta, bush.getArcFlow(arc01.index));
		assertEquals(maxPathFlow - flowDelta, bush.getArcFlow(arc13.index));
		assertEquals(flowDelta, bush.getArcFlow(arc02.index));
		assertEquals(flowDelta, bush.getArcFlow(arc23.index));
	}
	
	@DisplayName("Diamond graph alt. - flows in network")
	@Test
	void test3() {
		double maxPathFlow = 1000;
		double flowDelta = 300;
		var arc01 = new Arc(0, 1, 0, 0, 		  0, 0);
		var arc12 = new Arc(1, 2, 0, 0, maxPathFlow, 1);
		var arc13 = new Arc(1, 3, 0, 0, 		  0, 2);
		var arc24 = new Arc(2, 4, 0, 0, maxPathFlow, 3);
		var arc34 = new Arc(3, 4, 0, 0, 		  0, 4);
		
		var minTree = new Arc[]{null, arc01, arc12, arc13, arc34};
		var maxTree = new Arc[]{null, arc01, arc12, arc13, arc24};
		
		Bush b = new Bush(5, 0);
		a.shiftFlows(minTree, maxTree, b, 4, 1, flowDelta);
		
		assertEquals(0, arc01.getCurrentFlow());
		assertEquals(maxPathFlow - flowDelta, arc12.getCurrentFlow());
		assertEquals(maxPathFlow - flowDelta, arc24.getCurrentFlow());
		assertEquals(flowDelta, arc13.getCurrentFlow());
		assertEquals(flowDelta, arc34.getCurrentFlow());
	}
	
	@DisplayName("Diamond graph alt. - flows in bush")
	@Test
	void test4() {
		double maxPathFlow = 1000;
		double flowDelta = 300;
		var arc01 = new Arc(0, 1, 0, 0, 		  0, 0);
		var arc12 = new Arc(1, 2, 0, 0, maxPathFlow, 1);
		var arc13 = new Arc(1, 3, 0, 0, 		  0, 2);
		var arc24 = new Arc(2, 4, 0, 0, maxPathFlow, 3);
		var arc34 = new Arc(3, 4, 0, 0, 		  0, 4);
		
		var minTree = new Arc[]{null, arc01, arc12, arc13, arc34};
		var maxTree = new Arc[]{null, arc01, arc12, arc13, arc24};
		
		Bush bush = new Bush(5, 0);
		bush.addFlow(1, maxPathFlow);
		bush.addFlow(3, maxPathFlow);
		
		a.shiftFlows(minTree, maxTree, bush, 4, 1, flowDelta);
		
		assertEquals(0, bush.getArcFlow(arc01.index));
		assertEquals(maxPathFlow - flowDelta, bush.getArcFlow(arc12.index));
		assertEquals(maxPathFlow - flowDelta, bush.getArcFlow(arc24.index));
		assertEquals(flowDelta, bush.getArcFlow(arc13.index));
		assertEquals(flowDelta, bush.getArcFlow(arc34.index));
	}
}
