#include "../include.h"

TEST_CASE("getMaxDistance", "[]") {
	Matrix m(0);
	constexpr double EPSILON = 1e-10;

	SECTION("Path P4") {
		Network::Arc arc01(0, 1, 1, 1, 0, 0);
		Network::Arc arc12(1, 2, 1, 1, 0, 0);
		Network::Arc arc23(2, 3, 1, 1, 0, 0);

		std::vector<std::vector<Network::Arc>> adjList{{arc01}, {arc12}, {arc23}, {}};
		Network network(adjList, 0);
		network.updateCosts();

		Bush bush(3);
		for (int i = 0; i < 3; ++i)
			bush.addArc(i);

		Algorithm a(network, m, 0, 0);

		auto res = a.getMaxDistance(bush);

		std::vector<double> expectedDistance{0, 1, 2, 3};

		REQUIRE(res == expectedDistance);
	}

	SECTION("Path P4 (2)") {
		Network::Arc arc01(0, 1, 1, 1, 1, 0);
		Network::Arc arc12(1, 2, 1, 1, 1, 0);
		Network::Arc arc23(2, 3, 1, 1, 1, 0);

		std::vector<std::vector<Network::Arc>> adjList{{arc01}, {arc12}, {arc23}, {}};
		Network network(adjList, 0);
		network.updateCosts();

		Bush bush(3);
		for (int i = 0; i < 3; ++i)
			bush.addArc(i);

		Algorithm a(network, m, 0, 0);

		auto res = a.getMaxDistance(bush);

		std::vector<double> expectedDistance{0, 1.15, 2.3, 3.45};

		REQUIRE_THAT(res, Catch::Approx(expectedDistance).margin(EPSILON));
	}

	SECTION("Cyclic graph C3") {
		Network::Arc arc01(0, 1, 1, 1, 0, 0);
		Network::Arc arc02(0, 2, 1, 1, 0, 0);
		Network::Arc arc12(1, 2, 1, 1, 0, 0);

		std::vector<std::vector<Network::Arc>> adjList{{arc01, arc02}, {arc12}, {}};
		Network network(adjList, 0);
		network.updateCosts();

		Bush bush(3);
		for (int i = 0; i < 3; ++i)
			bush.addArc(i);

		Algorithm a(network, m, 0, 0);

		auto res = a.getMaxDistance(bush);

		std::vector<double> expectedDistance{0, 1, 2};

		REQUIRE(res == expectedDistance);
	}

	SECTION("Bull graph") {
		Network::Arc arc01(0, 1, 1, 1, 0, 0);
		Network::Arc arc12(1, 2, 1, 1, 0, 0);
		Network::Arc arc13(1, 3, 1, 1, 0, 0);
		Network::Arc arc23(2, 3, 1, 1, 0, 0);
		Network::Arc arc34(3, 4, 1, 1, 0, 0);

		std::vector<std::vector<Network::Arc>> adjList{
				{arc01}, {arc12, arc13}, {arc23}, {arc34}, {}};
		Network network(adjList, 0);
		network.updateCosts();

		Bush bush(5);
		for (int i = 0; i < 5; ++i) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}

		Algorithm a(network, m, 0, 0);

		auto res = a.getMaxDistance(bush);

		std::vector<double> expectedDistance{0, 1, 2, 3, 4};

		REQUIRE(res == expectedDistance);
	}
}
