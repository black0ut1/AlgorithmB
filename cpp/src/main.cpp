#include <chrono>
#include <iostream>
#include <iomanip>
#include "algorithm/algorithm/Algorithm.h"
#include "algorithm/parallel_algorithm/ParallelAlgorithm.h"
#include "io/parser/Parser.h"
#include "argparse/argparse.hpp"
#include "io/writer/Writer.h"

/**
 * Initializes argument parser to parse command
 * line arguments
 * @param args argument parser
 */
void initArgs(argparse::ArgumentParser &args) {
	args.add_argument("-net", "--network")
			.required()
			.help("Specifies a path to a .tntp file containing "
				  "the network.");
	args.add_argument("-odm", "--odmatrix")
			.required()
			.help("Specifies a path to a .tntp file containing "
				  "the origin-destination matrix.");
	args.add_argument("-o", "--output")
			.required()
			.help("Specifies a path to a file to which the "
				  "resulting trips/flow of each arc will be written.");
	args.add_argument("-i", "--iterations")
			.required()
			.scan<'i', int>()
			.help("Specifies a maximum number of iterations the "
				  "algorithm will make.");
	args.add_argument("-rg", "--relative-gap")
			.default_value(0.0)
			.scan<'g', double>()
			.help("When specified, the relative gap will be "
				  "computed after each iteration and if it is lower "
				  "than the number specified by this parameter, the "
				  "algorithm will end before reaching max. iterations. "
				  "Beware that the calculation of relative gap is very "
				  "computationally demanding.");
	args.add_argument("-t", "--threads")
			.default_value(0)
			.scan<'i', int>()
			.help("When present, the parallelized version of "
				  "the algorithm will be executed. The number specifies "
				  "how many worker threads will be used. Note: launching "
				  "with -t 1 is not desirable since the program will take "
				  "longer than when launched without -t at all because "
				  "of additional overhead of the parallelized version.");
}

/**
 * Entry point of the program.
 * @param argc number of cmd arguments
 * @param argv command line arguments
 * @return return code
 */
int main(int argc, char *argv[]) {
	std::cout << std::fixed << std::setprecision(15);

	argparse::ArgumentParser args("BP_Cpp");
	initArgs(args);

	try {
		args.parse_args(argc, argv);
	} catch (std::exception &e) {
		std::cerr << e.what();
		return 1;
	}


	std::cout << "Loading network... " << std::flush;
	auto tick = std::chrono::system_clock::now();
	auto network = Parser::parseNetwork(args.get("-net"));
	auto tock = std::chrono::system_clock::now();
	auto elapsed = std::chrono::duration_cast<std::chrono::milliseconds>(tock - tick);
	std::cout << "OK (" << elapsed.count() << "ms)" << std::endl;

	std::cout << "Loading OD matrix... " << std::flush;
	tick = std::chrono::system_clock::now();
	auto odm = Parser::parseODMatrix(args.get("-odm"));
	tock = std::chrono::system_clock::now();
	elapsed = std::chrono::duration_cast<std::chrono::milliseconds>(tock - tick);
	std::cout << "OK (" << elapsed.count() << "ms)" << std::endl;

	int i = args.get<int>("-i");
	int t = args.get<int>("-t");
	auto rg = args.get<double>("-rg");
	std::unique_ptr<Algorithm> a = (t == 0)
			? std::make_unique<Algorithm>(network, odm, rg, i)
			: std::make_unique<ParallelAlgorithm>(network, odm, rg, i, t);

	tick = std::chrono::system_clock::now();
	auto arcs = a->start();
	tock = std::chrono::system_clock::now();
	auto elapsed2 = std::chrono::duration_cast<std::chrono::milliseconds>(tock - tick);
	std::cout << "Static traffic assigment computation time is "
			  << elapsed2.count() << " ms." << std::endl;

	Writer::writeArcs(arcs, args.get("-o"));

	return 0;
}
