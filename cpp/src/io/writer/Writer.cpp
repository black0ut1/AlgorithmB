#include "Writer.h"
#include <fstream>
#include <iomanip>

void Writer::writeArcs(const std::vector<Network::Arc> &arcs, const std::string &path) {
	std::ofstream file(path);
	file << std::fixed << std::setprecision(15);

	file << "From\tTo\tVolume\tCost\n";
	for (const auto &arc: arcs) {
		file << arc.startNode + 1 << '\t'
			 << arc.endNode + 1 << '\t'
			 << arc.getCurrentFlow() << '\t'
			 << arc.getCost() << '\n';
	}
}