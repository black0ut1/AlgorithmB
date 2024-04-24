#ifndef BP_CPP_PARSER_H
#define BP_CPP_PARSER_H

#include <string>
#include "../../data/network/Network.h"

namespace Parser {

	/**
	 * Parses TNTP file representing road network.
	 * @param path path to network TNTP file
	 * @return graph of road network
	 */
	Network parseNetwork(const std::string &path);

	/**
	 * Parses TNTP file representing OD matrix.
	 * @param path path to matrix TNTP file
	 * @return OD matrix
	 */
	Matrix parseODMatrix(const std::string &path);
}

#endif
