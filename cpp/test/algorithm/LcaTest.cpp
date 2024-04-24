#include "../include.h"

typedef std::tuple<std::vector<std::pair<int, int>>, std::vector<std::pair<int, int>>, int, int> lcaArgs;

std::vector<Network::Arc *> toArcVector(const std::vector<std::pair<int, int>> &pairs,
										std::vector<std::vector<Network::Arc>> &arc) {
	std::vector<Network::Arc *> arcVector;

	for (const auto &pair: pairs) {
		if (pair.first == -1)
			arcVector.push_back(nullptr);
		else
			arcVector.push_back(&arc.at(pair.first).at(pair.first));
	}

	return arcVector;
}

TEST_CASE("LCA", "[]") {
	int MAX_NODES = 7;
	std::vector<std::vector<Network::Arc>> adjList(MAX_NODES);
	Network n(adjList, 0);
	Matrix m(0);
	Algorithm a(n, m, 0, 0);

	std::vector<std::vector<Network::Arc>> arc(MAX_NODES);
	for (int i = 0; i < MAX_NODES; ++i) {
		for (int j = 0; j < MAX_NODES; ++j)
			arc.at(i).emplace_back(i, j, 0, 0);
	}

	SECTION("Positive tests") {
		auto i = GENERATE(lcaArgs{{{-1, -1}, {0, 1}, {0, 2}},
								  {{-1, -1}, {2, 1}, {0, 1}}, 1, 0},

						  lcaArgs{{{-1, -1}, {0, 1}, {0, 2}, {1, 3}},
								  {{-1, -1}, {0, 1}, {0, 2}, {2, 3}}, 3, 0},

						  lcaArgs{{{-1, -1}, {0, 1}, {0, 2}, {2, 3}, {1, 4}},
								  {{-1, -1}, {0, 1}, {0, 2}, {2, 3}, {3, 4}}, 4, 0},

						  lcaArgs{{{-1, -1}, {0, 1}, {1, 2}, {1, 3}, {2, 4}},
								  {{-1, -1}, {0, 1}, {1, 2}, {1, 3}, {3, 4}}, 4, 1},

						  lcaArgs{{{-1, -1}, {0, 1}, {0, 2}, {1, 3}, {3, 4}, {3, 5}, {4, 6}},
								  {{-1, -1}, {0, 1}, {0, 2}, {2, 3}, {3, 4}, {3, 5}, {5, 6}}, 6, 3},

						  lcaArgs{{{2, 0}, {2, 1}, {4, 2}, {0, 3}, {-1, -1}},
								  {{2, 0}, {2, 1}, {4, 2}, {1, 3}, {-1, -1}}, 3, 2},

						  lcaArgs{{{-1, -1}, {0, 1}, {1, 2}, {1, 3}, {3, 4}, {4, 5}, {5, 6}},
								  {{-1, -1}, {0, 1}, {6, 2}, {1, 3}, {3, 4}, {4, 5}, {5, 6}}, 2, 1});

		std::vector<Network::Arc *> minTree = toArcVector(get<0>(i), arc);
		std::vector<Network::Arc *> maxTree = toArcVector(get<1>(i), arc);
		int node = get<2>(i);
		int lca = get<3>(i);

		REQUIRE(lca == a.LCA(minTree, maxTree, node));
	}

	SECTION("Negative tests") {
		auto i = GENERATE(lcaArgs{{{-1, -1}, {0, 1}, {1, 2}},
								  {{-1, -1}, {0, 1}, {1, 2}}, 2, -1},

						  lcaArgs{{{-1, -1}, {0, 1}, {0, 2}},
								  {{-1, -1}, {-1, -1}, {1, 2}}, 2, -1},

						  lcaArgs{{{-1, -1}, {0, 1}, {1, 2}, {1, 3}, {3, 4}, {4, 5}, {5, 6}},
								  {{-1, -1}, {0, 1}, {6, 2}, {1, 3}, {3, 4}, {-1, -1}, {5, 6}}, 2, -1},

						  lcaArgs{{{-1, -1}, {0, 1}, {-1, -1}},
								  {{-1, -1}, {0, 1}, {1, 2}}, 2, -1},

						  lcaArgs{{{-1, -1}, {0, 1}, {1, 2}},
								  {{-1, -1}, {0, 1}, {-1, -1}}, 2, -1});

		std::vector<Network::Arc *> minTree = toArcVector(get<0>(i), arc);
		std::vector<Network::Arc *> maxTree = toArcVector(get<1>(i), arc);
		int node = get<2>(i);

		REQUIRE(-1 == a.LCA(minTree, maxTree, node));
	}
}