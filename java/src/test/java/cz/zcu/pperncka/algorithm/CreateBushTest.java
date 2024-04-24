package cz.zcu.pperncka.algorithm;

import cz.zcu.pperncka.data.Bush;
import cz.zcu.pperncka.data.Matrix;
import cz.zcu.pperncka.data.Network;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
@DisplayName("createBush")
public class CreateBushTest {
	
	static Network network;
	
	@BeforeAll
	static void beforeAll() {
		//  2---3
		//  | / |
		//  0---1
		var arc01 = new Network.Arc(0, 1, 1, 1);
		var arc10 = new Network.Arc(1, 0, 1, 1);
		var arc02 = new Network.Arc(0, 2, 1, 1);
		var arc20 = new Network.Arc(2, 0, 1, 1);
		var arc03 = new Network.Arc(0, 3, 1, 3);
		var arc30 = new Network.Arc(3, 0, 1, 3);
		var arc13 = new Network.Arc(1, 3, 1, 1);
		var arc31 = new Network.Arc(3, 1, 1, 1);
		var arc23 = new Network.Arc(2, 3, 1, 1);
		var arc32 = new Network.Arc(3, 2, 1, 1);
		var adjList = new List[]{
				List.of(arc01, arc02, arc03),
				List.of(arc10, arc13),
				List.of(arc20, arc23),
				List.of(arc30, arc31, arc32)
		};
		network = new Network(adjList, 4);
		network.updateCosts();
	}
	
	@Test
	void test1() {
		Matrix m = new Matrix(4);
		m.set(0, 1, 100);
		m.set(0, 2, 200);
		m.set(0, 3, 300);
		
		Algorithm a = new Algorithm(network, m, 0, 0);
		
		Bush bush = a.createBush(0);
		
		assertTrue(bush.arcExists(0));
		assertTrue(bush.arcExists(1));
		assertTrue(bush.arcExists(2));
		assertTrue(bush.arcExists(4));
		assertTrue(bush.arcExists(6));
		assertFalse(bush.arcExists(3));
		assertFalse(bush.arcExists(5));
		assertFalse(bush.arcExists(7));
		assertFalse(bush.arcExists(8));
		assertFalse(bush.arcExists(9));
		
		assertEquals(400, bush.getArcFlow(0));
		assertEquals(200, bush.getArcFlow(1));
		assertEquals(300, bush.getArcFlow(4));
		assertEquals(0, bush.getArcFlow(2));
		assertEquals(0, bush.getArcFlow(6));
	}
	
	@Test
	void test2() {
		Matrix m = new Matrix(4);
		m.set(1, 0, 100);
		m.set(1, 3, 200);
		m.set(1, 2, 300);
		
		Algorithm a = new Algorithm(network, m, 0, 0);
		
		Bush bush = a.createBush(1);
		
		assertTrue(bush.arcExists(3));
		assertTrue(bush.arcExists(4));
		assertTrue(bush.arcExists(1));
		assertTrue(bush.arcExists(9));
		assertFalse(bush.arcExists(0));
		assertFalse(bush.arcExists(2));
		assertFalse(bush.arcExists(5));
		assertFalse(bush.arcExists(6));
		assertFalse(bush.arcExists(7));
		assertFalse(bush.arcExists(8));
		
		assertEquals(400, bush.getArcFlow(3));
		assertEquals(200, bush.getArcFlow(4));
		assertEquals(300, bush.getArcFlow(1));
		assertEquals(0, bush.getArcFlow(9));
	}
}
