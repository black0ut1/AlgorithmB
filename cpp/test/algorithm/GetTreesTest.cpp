#include "../include.h"

TEST_CASE("getTrees", "[]") {
	Matrix m(0);

	SECTION("Path P4") {
		Network::Arc arc01(0, 1, 1, 1, 1, 0);
		Network::Arc arc12(1, 2, 1, 1, 1, 0);
		Network::Arc arc23(2, 3, 1, 1, 1, 0);

		std::vector<std::vector<Network::Arc>> adjList{{arc01}, {arc12}, {arc23}, {}};
		Network network(adjList, 0);
		network.updateCosts();

		Bush bush(3);
		for (int i = 0; i < 3; ++i) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}

		Algorithm a(network, m, 0, 0);

		auto[minTree, maxTree] = a.getTrees(bush);

		std::vector<Network::Arc *> expectedMinTree{
			nullptr, &network.getArc(0, 1), &network.getArc(1, 2), &network.getArc(2, 3)};
		std::vector<Network::Arc *> expectedMaxTree{
			nullptr, &network.getArc(0, 1), &network.getArc(1, 2), &network.getArc(2, 3)};

		REQUIRE(minTree == expectedMinTree);
		REQUIRE(maxTree == expectedMaxTree);
	}

	SECTION("Path P4 with non-active arc") {
		Network::Arc arc01(0, 1, 1, 1, 1, 0);
		Network::Arc arc12(1, 2, 1, 1, 0, 0);
		Network::Arc arc23(2, 3, 1, 1, 1, 0);

		std::vector<std::vector<Network::Arc>> adjList{{arc01}, {arc12}, {arc23}, {}};
		Network network(adjList, 0);
		network.updateCosts();

		Bush bush(3);
		for (int i = 0; i < 3; ++i) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}
		bush.addFlow(1, -1);

		Algorithm a(network, m, 0, 0);

		auto[minTree, maxTree] = a.getTrees(bush);

		std::vector<Network::Arc *> expectedMinTree{
				nullptr, &network.getArc(0, 1), &network.getArc(1, 2), &network.getArc(2, 3)};
		std::vector<Network::Arc *> expectedMaxTree{
				nullptr, &network.getArc(0, 1), nullptr, nullptr};

		REQUIRE(minTree == expectedMinTree);
		REQUIRE(maxTree == expectedMaxTree);
	}

	SECTION("Cyclic graph C3") {
		Network::Arc arc01(0, 1, 1, 1, 1, 0);
		Network::Arc arc02(0, 2, 1, 1, 1, 0);
		Network::Arc arc12(1, 2, 1, 1, 1, 0);

		std::vector<std::vector<Network::Arc>> adjList{{arc01, arc02}, {arc12}, {}};
		Network network(adjList, 0);
		network.updateCosts();

		Bush bush(3);
		for (int i = 0; i < 3; ++i) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}

		Algorithm a(network, m, 0, 0);

		auto[minTree, maxTree] = a.getTrees(bush);

		std::vector<Network::Arc *> expectedMinTree{
				nullptr, &network.getArc(0, 1), &network.getArc(0, 2)};
		std::vector<Network::Arc *> expectedMaxTree{
				nullptr, &network.getArc(0, 1), &network.getArc(1, 2)};

		REQUIRE(minTree == expectedMinTree);
		REQUIRE(maxTree == expectedMaxTree);
	}

	SECTION("Cyclic graph C3 (2)") {
		Network::Arc arc01(0, 1, 1, 1, 1, 0);
		Network::Arc arc12(1, 2, 1, 1, 1, 0);
		Network::Arc arc02(0, 2, 1, 9, 1, 0);

		std::vector<std::vector<Network::Arc>> adjList{{arc01, arc02}, {arc12}, {}};
		Network network(adjList, 0);
		network.updateCosts();

		Bush bush(3);
		for (int i = 0; i < 3; ++i) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}

		Algorithm a(network, m, 0, 0);

		auto[minTree, maxTree] = a.getTrees(bush);

		std::vector<Network::Arc *> expectedMinTree{
				nullptr, &network.getArc(0, 1), &network.getArc(1, 2)};
		std::vector<Network::Arc *> expectedMaxTree{
				nullptr, &network.getArc(0, 1), &network.getArc(0, 2)};

		REQUIRE(minTree == expectedMinTree);
		REQUIRE(maxTree == expectedMaxTree);
	}

	SECTION("Cyclic graph C3 with non-active arc") {
		Network::Arc arc01(0, 1, 1, 1, 1, 0);
		Network::Arc arc12(1, 2, 1, 1, 1, 0);
		Network::Arc arc02(0, 2, 1, 1, 1, 0);

		std::vector<std::vector<Network::Arc>> adjList{{arc01, arc02}, {arc12}, {}};
		Network network(adjList, 0);
		network.updateCosts();

		Bush bush(3);
		for (int i = 0; i < 3; ++i) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}
		bush.addFlow(2, -1);

		Algorithm a(network, m, 0, 0);

		auto[minTree, maxTree] = a.getTrees(bush);

		std::vector<Network::Arc *> expectedMinTree{
				nullptr, &network.getArc(0, 1), &network.getArc(0, 2)};
		std::vector<Network::Arc *> expectedMaxTree{
				nullptr, &network.getArc(0, 1), &network.getArc(0, 2)};

		REQUIRE(minTree == expectedMinTree);
		REQUIRE(maxTree == expectedMaxTree);
	}

	SECTION("Bull graph") {
		Network::Arc arc01(0, 1, 1, 1, 1, 0);
		Network::Arc arc12(1, 2, 1, 1, 1, 0);
		Network::Arc arc13(1, 3, 1, 1, 1, 0);
		Network::Arc arc23(2, 3, 1, 1, 1, 0);
		Network::Arc arc34(3, 4, 1, 1, 1, 0);

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

		auto[minTree, maxTree] = a.getTrees(bush);

		std::vector<Network::Arc *> expectedMinTree{
				nullptr, &network.getArc(0, 1), &network.getArc(1, 2),
				&network.getArc(1, 3), &network.getArc(3, 4)};
		std::vector<Network::Arc *> expectedMaxTree{
				nullptr, &network.getArc(0, 1), &network.getArc(1, 2),
				&network.getArc(2, 3), &network.getArc(3, 4)};

		REQUIRE(minTree == expectedMinTree);
		REQUIRE(maxTree == expectedMaxTree);
	}

	SECTION("Graph with alternative max path") {
		Network::Arc arc01(0, 1, 1, 1, 1, 0);
		Network::Arc arc12(1, 2, 1, 1, 1, 0);
		Network::Arc arc13(1, 3, 1, 1, 1, 0);
		Network::Arc arc34(3, 4, 1, 1, 1, 0);
		Network::Arc arc42(4, 2, 1, 1, 1, 0);
		Network::Arc arc45(4, 5, 1, 1, 0, 0);
		Network::Arc arc56(5, 6, 1, 1, 1, 0);
		Network::Arc arc62(6, 2, 1, 1, 1, 0);

		std::vector<std::vector<Network::Arc>> adjList{
				{arc01}, {arc12, arc13}, {}, {arc34}, {arc42, arc45}, {arc56}, {arc62}};
		Network network(adjList, 0);
		network.updateCosts();

		Bush bush(8);
		for (int i = 0; i < 8; ++i) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}
		bush.addFlow(5, -1);

		Algorithm a(network, m, 0, 0);

		auto[minTree, maxTree] = a.getTrees(bush);

		std::vector<Network::Arc *> expectedMinTree{
				nullptr, &network.getArc(0, 1), &network.getArc(1, 2),
				&network.getArc(1, 3), &network.getArc(3, 4),
				&network.getArc(4, 5), &network.getArc(5, 6)};
		std::vector<Network::Arc *> expectedMaxTree{
				nullptr, &network.getArc(0, 1), &network.getArc(4, 2),
				&network.getArc(1, 3), &network.getArc(3, 4),
				nullptr, nullptr};

		REQUIRE(minTree == expectedMinTree);
		REQUIRE(maxTree == expectedMaxTree);
	}

	SECTION("Bush is not whole graph") {
		Network::Arc arc01(0, 1, 1, 1, 1, 0);
		Network::Arc arc12(1, 2, 1, 1, 1, 0);
		Network::Arc arc23(2, 3, 1, 1, 1, 0);

		std::vector<std::vector<Network::Arc>> adjList{{arc01}, {arc12}, {arc23}, {}};
		Network network(adjList, 0);
		network.updateCosts();

		Bush bush(3);
		for (int i = 0; i < 2; ++i) {
			bush.addArc(i);
			bush.addFlow(i, 1);
		}

		Algorithm a(network, m, 0, 0);

		auto[minTree, maxTree] = a.getTrees(bush);

		std::vector<Network::Arc *> expectedMinTree{
				nullptr, &network.getArc(0, 1), &network.getArc(1, 2), nullptr};
		std::vector<Network::Arc *> expectedMaxTree{
				nullptr, &network.getArc(0, 1), &network.getArc(1, 2), nullptr};

		REQUIRE(minTree == expectedMinTree);
		REQUIRE(maxTree == expectedMaxTree);
	}
}