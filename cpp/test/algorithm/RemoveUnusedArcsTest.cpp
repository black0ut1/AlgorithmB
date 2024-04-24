#include "../include.h"

TEST_CASE("removeUnusedArcs", "[]") {
	Matrix m(0);

	SECTION("All arcs with positive flow, empty minTree") {
		Network::Arc arc01(0, 1, 1, 1, 1, 0);
		Network::Arc arc02(0, 2, 1, 1, 1, 0);
		Network::Arc arc13(1, 3, 1, 1, 1, 0);
		Network::Arc arc23(2, 3, 1, 1, 1, 0);

		std::vector<std::vector<Network::Arc>> adjList{{arc01, arc02}, {arc13}, {arc23}, {}};
		Network n(adjList, 0);
		n.updateCosts();

		Bush bush(4);
		for (int i = 0; i < 4; ++i) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}

		std::vector<Network::Arc *> minTree;
		Algorithm a(n, m, 0, 0);

		a.removeUnusedArcs(bush, minTree);

		REQUIRE(bush.arcExists(0));
		REQUIRE(bush.arcExists(1));
		REQUIRE(bush.arcExists(2));
		REQUIRE(bush.arcExists(3));
	}

	SECTION("All arcs with no flow, empty minTree") {
		Network::Arc arc01(0, 1, 1, 1, 1, 0);
		Network::Arc arc02(0, 2, 1, 1, 1, 0);
		Network::Arc arc13(1, 3, 1, 1, 1, 0);
		Network::Arc arc23(2, 3, 1, 1, 1, 0);

		std::vector<std::vector<Network::Arc>> adjList{{arc01, arc02}, {arc13}, {arc23}, {}};
		Network n(adjList, 0);
		n.updateCosts();

		Bush bush(4);
		for (int i = 0; i < 4; ++i)
			bush.addArc(i);

		std::vector<Network::Arc *> minTree;
		Algorithm a(n, m, 0, 0);

		a.removeUnusedArcs(bush, minTree);

		REQUIRE_FALSE(bush.arcExists(0));
		REQUIRE_FALSE(bush.arcExists(1));
		REQUIRE_FALSE(bush.arcExists(2));
		REQUIRE_FALSE(bush.arcExists(3));
	}

	SECTION("All arcs with no flow, all arcs in minTree") {
		Network::Arc arc01(0, 1, 1, 1, 1, 0);
		Network::Arc arc02(0, 2, 1, 1, 1, 0);
		Network::Arc arc13(1, 3, 1, 1, 1, 0);
		Network::Arc arc23(2, 3, 1, 1, 1, 0);

		std::vector<std::vector<Network::Arc>> adjList{{arc01, arc02}, {arc13}, {arc23}, {}};
		Network n(adjList, 0);
		n.updateCosts();

		Bush bush(4);
		for (int i = 0; i < 4; ++i)
			bush.addArc(i);

		std::vector<Network::Arc *> minTree;
		for (auto &arc: n.getArcs())
			minTree.push_back(&arc);

		Algorithm a(n, m, 0, 0);

		a.removeUnusedArcs(bush, minTree);

		REQUIRE(bush.arcExists(0));
		REQUIRE(bush.arcExists(1));
		REQUIRE(bush.arcExists(2));
		REQUIRE(bush.arcExists(3));
	}

	SECTION("One arc with no flow, empty minTree") {
		Network::Arc arc01(0, 1, 1, 1, 1, 0);
		Network::Arc arc02(0, 2, 1, 1, 1, 0);
		Network::Arc arc13(1, 3, 1, 1, 1, 0);
		Network::Arc arc23(2, 3, 1, 1, 1, 0);

		std::vector<std::vector<Network::Arc>> adjList{{arc01, arc02}, {arc13}, {arc23}, {}};
		Network n(adjList, 0);
		n.updateCosts();

		Bush bush(4);
		for (int i = 0; i < 4; ++i) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}
		bush.addFlow(3, -1);

		std::vector<Network::Arc *> minTree;
		Algorithm a(n, m, 0, 0);

		a.removeUnusedArcs(bush, minTree);

		REQUIRE(bush.arcExists(0));
		REQUIRE(bush.arcExists(1));
		REQUIRE(bush.arcExists(2));
		REQUIRE_FALSE(bush.arcExists(3));
	}
}