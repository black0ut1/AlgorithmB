#include "../include.h"

TEST_CASE("createBush()", "[]") {
	Network::Arc arc01(0, 1, 1, 1);
	Network::Arc arc10(1, 0, 1, 1);
	Network::Arc arc02(0, 2, 1, 1);
	Network::Arc arc20(2, 0, 1, 1);
	Network::Arc arc03(0, 3, 1, 3);
	Network::Arc arc30(3, 0, 1, 3);
	Network::Arc arc13(1, 3, 1, 1);
	Network::Arc arc31(3, 1, 1, 1);
	Network::Arc arc23(2, 3, 1, 1);
	Network::Arc arc32(3, 2, 1, 1);
	std::vector<std::vector<Network::Arc>> adjList{
		{arc01, arc02, arc03},
		{arc10, arc13},
		{arc20, arc23},
		{arc30, arc31, arc32}
	};
	Network network(adjList, 4);
	network.updateCosts();

	SECTION("") {
		Matrix m(4);
		m(0, 1) = 100;
		m(0, 2) = 200;
		m(0, 3) = 300;

		Algorithm a(network, m, 0, 0);

		a.createBush(0);
		auto bush = a.bushes[0];

		REQUIRE(bush.arcExists(0));
		REQUIRE(bush.arcExists(1));
		REQUIRE(bush.arcExists(2));
		REQUIRE(bush.arcExists(4));
		REQUIRE(bush.arcExists(6));
		REQUIRE_FALSE(bush.arcExists(3));
		REQUIRE_FALSE(bush.arcExists(5));
		REQUIRE_FALSE(bush.arcExists(7));
		REQUIRE_FALSE(bush.arcExists(8));
		REQUIRE_FALSE(bush.arcExists(9));

		REQUIRE(bush.getArcFlow(0) == 400);
		REQUIRE(bush.getArcFlow(1) == 200);
		REQUIRE(bush.getArcFlow(4) == 300);
		REQUIRE(bush.getArcFlow(2) == 0);
		REQUIRE(bush.getArcFlow(6) == 0);
	}

	SECTION("") {
		Matrix m(4);
		m(1, 0) = 100;
		m(1, 3) = 200;
		m(1, 2) = 300;

		Algorithm a(network, m, 0, 0);

		a.createBush(1);
		auto bush = a.bushes[1];

		REQUIRE(bush.arcExists(3));
		REQUIRE(bush.arcExists(4));
		REQUIRE(bush.arcExists(1));
		REQUIRE(bush.arcExists(9));
		REQUIRE_FALSE(bush.arcExists(0));
		REQUIRE_FALSE(bush.arcExists(2));
		REQUIRE_FALSE(bush.arcExists(5));
		REQUIRE_FALSE(bush.arcExists(6));
		REQUIRE_FALSE(bush.arcExists(7));
		REQUIRE_FALSE(bush.arcExists(8));

		REQUIRE(bush.getArcFlow(3) == 400);
		REQUIRE(bush.getArcFlow(4) == 200);
		REQUIRE(bush.getArcFlow(1) == 300);
		REQUIRE(bush.getArcFlow(9) == 0);
	}
}
