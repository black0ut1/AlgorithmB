#include "../include.h"

TEST_CASE("shiftFlows", "[]") {

	SECTION("Diamond graph - flows in network") {
		double maxPathFlow = 1000;
		double flowDelta = 300;
		Network::Arc arc01(0, 1, 0, 0, maxPathFlow, 0);
		Network::Arc arc02(0, 2, 0, 0, 0		  , 1);
		Network::Arc arc13(1, 3, 0, 0, maxPathFlow, 2);
		Network::Arc arc23(2, 3, 0, 0, 0		  , 3);

		std::vector<Network::Arc *> minTree{nullptr, &arc01, &arc02, &arc23};
		std::vector<Network::Arc *> maxTree{nullptr, &arc01, &arc02, &arc13};

		Bush bush(4);
		Algorithm::shiftFlows(minTree, maxTree, bush, 3, 0, flowDelta);

		REQUIRE(maxPathFlow - flowDelta == arc01.getCurrentFlow());
		REQUIRE(maxPathFlow - flowDelta == arc13.getCurrentFlow());
		REQUIRE(flowDelta 				== arc02.getCurrentFlow());
		REQUIRE(flowDelta 				== arc23.getCurrentFlow());
	}

	SECTION("Diamond graph - flows in bush") {
		double maxPathFlow = 1000;
		double flowDelta = 300;
		Network::Arc arc01(0, 1, 0, 0, maxPathFlow, 0);
		Network::Arc arc02(0, 2, 0, 0, 0		  , 1);
		Network::Arc arc13(1, 3, 0, 0, maxPathFlow, 2);
		Network::Arc arc23(2, 3, 0, 0, 0		  , 3);

		std::vector<Network::Arc *> minTree{nullptr, &arc01, &arc02, &arc23};
		std::vector<Network::Arc *> maxTree{nullptr, &arc01, &arc02, &arc13};

		Bush bush(4);
		bush.addFlow(0, maxPathFlow);
		bush.addFlow(2, maxPathFlow);

		Algorithm::shiftFlows(minTree, maxTree, bush, 3, 0, flowDelta);

		REQUIRE(maxPathFlow - flowDelta == bush.getArcFlow(arc01.index));
		REQUIRE(maxPathFlow - flowDelta == bush.getArcFlow(arc13.index));
		REQUIRE(flowDelta 				== bush.getArcFlow(arc02.index));
		REQUIRE(flowDelta 				== bush.getArcFlow(arc23.index));
	}

	SECTION("Diamond graph alt. - flows in network") {
		double maxPathFlow = 1000;
		double flowDelta = 300;
		Network::Arc arc01(0, 1, 0, 0, 			 0, 0);
		Network::Arc arc12(1, 2, 0, 0, maxPathFlow, 1);
		Network::Arc arc13(1, 3, 0, 0, 			 0, 2);
		Network::Arc arc24(2, 4, 0, 0, maxPathFlow, 3);
		Network::Arc arc34(3, 4, 0, 0, 			 0, 4);

		std::vector<Network::Arc *> minTree{nullptr, &arc01, &arc12, &arc13, &arc34};
		std::vector<Network::Arc *> maxTree{nullptr, &arc01, &arc12, &arc13, &arc24};

		Bush bush(5);
		Algorithm::shiftFlows(minTree, maxTree, bush, 4, 1, flowDelta);

		REQUIRE(0 						== arc01.getCurrentFlow());
		REQUIRE(maxPathFlow - flowDelta == arc12.getCurrentFlow());
		REQUIRE(maxPathFlow - flowDelta == arc24.getCurrentFlow());
		REQUIRE(flowDelta 				== arc13.getCurrentFlow());
		REQUIRE(flowDelta 				== arc34.getCurrentFlow());
	}

	SECTION("Diamond graph alt. - flows in bush") {
		double maxPathFlow = 1000;
		double flowDelta = 300;
		Network::Arc arc01(0, 1, 0, 0, 			 0, 0);
		Network::Arc arc12(1, 2, 0, 0, maxPathFlow, 1);
		Network::Arc arc13(1, 3, 0, 0, 			 0, 2);
		Network::Arc arc24(2, 4, 0, 0, maxPathFlow, 3);
		Network::Arc arc34(3, 4, 0, 0, 			 0, 4);

		std::vector<Network::Arc *> minTree{nullptr, &arc01, &arc12, &arc13, &arc34};
		std::vector<Network::Arc *> maxTree{nullptr, &arc01, &arc12, &arc13, &arc24};

		Bush bush(5);
		bush.addFlow(1, maxPathFlow);
		bush.addFlow(3, maxPathFlow);

		Algorithm::shiftFlows(minTree, maxTree, bush, 4, 1, flowDelta);

		REQUIRE(0 						== bush.getArcFlow(arc01.index));
		REQUIRE(maxPathFlow - flowDelta == bush.getArcFlow(arc12.index));
		REQUIRE(maxPathFlow - flowDelta == bush.getArcFlow(arc24.index));
		REQUIRE(flowDelta 				== bush.getArcFlow(arc13.index));
		REQUIRE(flowDelta 				== bush.getArcFlow(arc34.index));
	}
}