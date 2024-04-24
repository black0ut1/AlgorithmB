#ifndef BP_CPP_WRITER_H
#define BP_CPP_WRITER_H

#include <vector>
#include "../../data/network/Network.h"

namespace Writer {

	/**
	 * Writes flows assigned to network arcs to a file.
	 * @param arcs array of arcs with assigned flows
	 * @param outputFile path to a flow TNTP file which will be created
	 */
	void writeArcs(const std::vector<Network::Arc> &arcs, const std::string &outputFile);
}

#endif
