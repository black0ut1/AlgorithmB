package cz.zcu.pperncka.algorithm;

import cz.zcu.pperncka.data.Network;
import cz.zcu.pperncka.data.Network.Arc;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
@DisplayName("LCA")
public class LcaTest {
	
	private final static int MAX_NODES = 7;
	
	static Algorithm a;
	static Arc[][] arc;
	
	@BeforeAll
	static void beforeAll() {
		List<Arc>[] adjList = new List[MAX_NODES];
		for (int i = 0; i < adjList.length; i++)
			adjList[i] = new ArrayList<>();
		
		Network n = new Network(adjList, 0);
		a = new Algorithm(n, null, 0, 0);
		
		arc = new Arc[MAX_NODES][];
		for (int i = 0; i < arc.length; i++) {
			arc[i] = new Arc[MAX_NODES];
			for (int j = 0; j < arc[i].length; j++)
				arc[i][j] = new Arc(i, j, 0, 0);
		}
	}
	
	@DisplayName("Positive tests")
	@ParameterizedTest
	@MethodSource
	void positive(Arc[] minTree, Arc[] maxTree, int node, int expected) {
		assertEquals(expected, a.LCA(minTree, maxTree, node));
	}
	
	@DisplayName("Negative tests")
	@ParameterizedTest
	@MethodSource
	void negative(Arc[] minTree, Arc[] maxTree, int node) {
		assertEquals(-1, a.LCA(minTree, maxTree, node));
	}
	
	private static List<Arguments> positive() {
		return List.of(
				//	 -> 1
				//  0	^
				//	 -> 2
				Arguments.of(new Arc[]{null, arc[0][1], arc[0][2]},
						new Arc[]{null, arc[2][1], arc[0][1]}, 1, 0),
				//	 -> 1 ∨
				//  0	  3
				//	 -> 2 ^
				Arguments.of(new Arc[]{null, arc[0][1], arc[0][2], arc[1][3]},
						new Arc[]{null, arc[0][1], arc[0][2], arc[2][3]}, 3, 0),
				//   -> 1 -> 4
				//  0	     ^
				//	 -> 2 -> 3
				Arguments.of(new Arc[]{null, arc[0][1], arc[0][2], arc[2][3], arc[1][4]},
						new Arc[]{null, arc[0][1], arc[0][2], arc[2][3], arc[3][4]}, 4, 0),
				//	      -> 2 ∨
				//  0 -> 1	   4
				//	      -> 3 ^
				Arguments.of(new Arc[]{null, arc[0][1], arc[1][2], arc[1][3], arc[2][4]},
						new Arc[]{null, arc[0][1], arc[1][2], arc[1][3], arc[3][4]}, 4, 1),
				//	 -> 1 ->   -> 4 ∨
				//  0        3      6
				//	 -> 2 ->   -> 5 ^
				Arguments.of(new Arc[]{null, arc[0][1], arc[0][2], arc[1][3], arc[3][4], arc[3][5], arc[4][6]},
						new Arc[]{null, arc[0][1], arc[0][2], arc[2][3], arc[3][4], arc[3][5], arc[5][6]}, 6, 3),
				//	      -> 0 ∨
				//  4 -> 2	   3
				//	      -> 1 ^
				Arguments.of(new Arc[]{arc[2][0], arc[2][1], arc[4][2], arc[0][3], null},
						new Arc[]{arc[2][0], arc[2][1], arc[4][2], arc[1][3], null}, 3, 2),
				//  0 -> 1 -> 2 <- 6
				//       ∨         ^
				//       3 -> 4 -> 5
				Arguments.of(new Arc[]{null, arc[0][1], arc[1][2], arc[1][3], arc[3][4], arc[4][5], arc[5][6]},
						new Arc[]{null, arc[0][1], arc[6][2], arc[1][3], arc[3][4], arc[4][5], arc[5][6]}, 2, 1)
		);
	}
	
	private static List<Arguments> negative() {
		return List.of(
				//
				//  0 -> 1 -> 2
				//
				Arguments.of(new Arc[]{null, arc[0][1], arc[1][2]},
						new Arc[]{null, arc[0][1], arc[1][2]}, 2),
				//   -> 2
				//  0   ^
				//   => 1
				Arguments.of(new Arc[]{null, arc[0][1], arc[0][2]},
						new Arc[]{null, null, arc[1][2]}, 2),
				//  0 -> 1 -> 2 <- 6
				//       ∨         ^
				//       3 -> 4 => 5
				Arguments.of(new Arc[]{null, arc[0][1], arc[1][2], arc[1][3], arc[3][4], arc[4][5], arc[5][6]},
						new Arc[]{null, arc[0][1], arc[6][2], arc[1][3], arc[3][4], null, arc[5][6]}, 2),
				//
				//  0 -> 1 -> 2
				//
				Arguments.of(new Arc[]{null, arc[0][1], null},
						new Arc[]{null, arc[0][1], arc[1][2]}, 2),
				//
				//  0 -> 1 -> 2
				//
				Arguments.of(new Arc[]{null, arc[0][1], arc[1][2]},
						new Arc[]{null, arc[0][1], null}, 2)
		);
	}
}
