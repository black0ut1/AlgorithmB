#include "../include.h"

TEST_CASE("findFlowDelta", "[]") {
	Network n(std::vector<std::vector<Network::Arc>>(0), 0);
	Matrix m(0);
	Algorithm a(n, m, 0, 0);

	SECTION("Case from Wikipedia - diamond") {
		Network::Arc arc01(0, 1, 	1,  0, 8000, 0);
		Network::Arc arc02(0, 2, 	1,  0,    0, 0);
		Network::Arc arc13(1, 3, 1000, 15, 8000, 0);
		Network::Arc arc23(2, 3, 3000, 20,    0, 0);

		std::vector<Network::Arc *> minTree{nullptr, &arc01, &arc02, &arc23};
		std::vector<Network::Arc *> maxTree{nullptr, &arc01, &arc02, &arc13};

		Bush bush(1);
		bush.addFlow(0, 10'000);

		REQUIRE(5847 == round(a.findFlowDelta(minTree, maxTree, bush, 3, 0)));
	}

	SECTION("Case from Wikipedia - diamond with tail") {
		Network::Arc arc01(0, 1, 	1,  0,    0, 0);
		Network::Arc arc12(1, 2, 	1,  0, 8000, 0);
		Network::Arc arc13(1, 3, 	1,  0,    0, 0);
		Network::Arc arc24(2, 4, 1000, 15, 8000, 0);
		Network::Arc arc34(3, 4, 3000, 20,    0, 0);

		std::vector<Network::Arc *> minTree{nullptr, &arc01, &arc12, &arc13, &arc34};
		std::vector<Network::Arc *> maxTree{nullptr, &arc01, &arc12, &arc13, &arc24};

		Bush bush(1);
		bush.addFlow(0, 10000);

		REQUIRE(5847 == round(a.findFlowDelta(minTree, maxTree, bush, 4, 1)));
	}

	SECTION("Test maxDeltaX <= μ") {
		Network::Arc arc01(0, 1, 	1,  0, 8000, 0);
		Network::Arc arc02(0, 2, 	1,  0,    0, 0);
		Network::Arc arc13(1, 3, 1000, 15, 8000, 0);
		Network::Arc arc23(2, 3, 3000, 20,    0, 0);

		std::vector<Network::Arc *> minTree{nullptr, &arc01, &arc02, &arc23};
		std::vector<Network::Arc *> maxTree{nullptr, &arc01, &arc02, &arc13};

		Bush bush(1);
		bush.addFlow(0, 1000);

		REQUIRE(1000 == a.findFlowDelta(minTree, maxTree, bush, 3, 0));
	}

	SECTION("Nonconverging case") {
		Network::Arc arc01(0, 1,  17782.7941, 2, 1900, 0);
		Network::Arc arc12(1, 2,  4908.82673, 6,  700, 0);
		Network::Arc arc23(2, 3, 	   10000, 5, 20783.42548485832, 0);
		Network::Arc arc04(0, 4, 	   10000, 5, 2300, 0);
		Network::Arc arc43(4, 3, 13915.78842, 3, 1900, 0);

		std::vector<Network::Arc *> minTree{nullptr, &arc01, &arc12, &arc23, &arc04};
		std::vector<Network::Arc *> maxTree{nullptr, &arc01, &arc12, &arc43, &arc04};

		Bush bush(1);
		bush.addFlow(0, 700);

		REQUIRE(700 == a.findFlowDelta(minTree, maxTree, bush, 3, 0));
	}

	SECTION("maxDeltaX == 0") {
		Network::Arc arc01(0, 1, 	1,  0, 8000, 0);
		Network::Arc arc02(0, 2, 	1,  0,    0, 0);
		Network::Arc arc13(1, 3, 1000, 15, 8000, 0);
		Network::Arc arc23(2, 3, 3000, 20,    0, 0);

		std::vector<Network::Arc *> minTree{nullptr, &arc01, &arc02, &arc23};
		std::vector<Network::Arc *> maxTree{nullptr, &arc01, &arc02, &arc13};

		Bush bush(1);

		REQUIRE(0 == a.findFlowDelta(minTree, maxTree, bush, 3, 0));
	}
}