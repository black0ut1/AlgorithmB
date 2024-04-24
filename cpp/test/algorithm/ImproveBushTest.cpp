#include "../include.h"

TEST_CASE("improveBush", "[]") {
	Matrix m(0);

	SECTION("") {
		Network::Arc arc01(0, 1, 1, 1, 1, 0);
		Network::Arc arc02(0, 2, 1, 1, 1, 0);
		Network::Arc arc12(1, 2, 1, 1, 1, 0);

		std::vector<std::vector<Network::Arc>> adjList{{arc01, arc02}, {arc12}, {}};
		Network network(adjList, 0);
		network.updateCosts();

		Bush bush(3);
		bush.addArc(0);
		bush.addFlow(0, 1);
		bush.addArc(2);
		bush.addFlow(2, 1);

		Algorithm a(network, m, 0, 0);

		a.improveBush(bush);

		REQUIRE(bush.arcExists(0));
		REQUIRE(bush.arcExists(1));
		REQUIRE(bush.arcExists(2));
	}

	SECTION("") {
		Network::Arc arc01(0, 1, 1, 1, 1, 0);
		Network::Arc arc02(0, 2, 1, 1, 1, 0);
		Network::Arc arc12(1, 2, 1, 1, 1, 0);
		Network::Arc arc23(2, 3, 1, 1, 1, 0);

		std::vector<std::vector<Network::Arc>> adjList{{arc01, arc02}, {arc12}, {arc23}, {}};
		Network network(adjList, 0);
		network.updateCosts();

		Bush bush(4);
		for (int i = 0; i < 3; ++i) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}

		Algorithm a(network, m, 0, 0);

		a.improveBush(bush);

		REQUIRE(bush.arcExists(0));
		REQUIRE(bush.arcExists(1));
		REQUIRE(bush.arcExists(2));
		REQUIRE_FALSE(bush.arcExists(3));
	}
}