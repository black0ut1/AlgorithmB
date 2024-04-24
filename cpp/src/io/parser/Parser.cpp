#include "Parser.h"
#include <fstream>
#include <map>
#include <iostream>
#include "boost/algorithm/string.hpp"

const char COMMENT_SIGN = '~';
const std::string HEADER_END = "<END OF METADATA>";
const std::string ZONES_NUMBER = "<NUMBER OF ZONES>";
const std::string NODES_NUMBER = "<NUMBER OF NODES>";

void sanitize(std::string &line) {
	size_t pos = line.find_first_of(COMMENT_SIGN);
	if (pos != std::string::npos)
		line.erase(pos);
	boost::trim(line);
}

std::vector<std::string_view> split(const std::string &str, char sep) {
	std::vector<std::string_view> result;
	result.reserve(100);

	for (auto p = str.begin();; ++p) {
		auto q = p;
		p = std::find(p, str.end(), sep);
		result.emplace_back(q, p);
		if (p == str.end())
			return result;
	}
}

std::map<std::string, std::string> parseHeader(std::ifstream &file) {
	std::map<std::string, std::string> header;

	std::string line;
	while (std::getline(file, line)) {
		sanitize(line);
		if (line.empty())
			continue;
		if (line == HEADER_END)
			break;

		std::vector<std::string> result;
		boost::split(result, line, boost::is_any_of(">"));
		result.at(0) += '>';
		boost::trim(result.at(0));
		boost::trim(result.at(1));

		header.emplace(result.at(0), result.at(1));
	}

	return header;
}

Network Parser::parseNetwork(const std::string &path) {
	std::ifstream file(path);
	if (file.fail())
		throw std::runtime_error("File " + path + " does not exist");

	auto header = parseHeader(file);

	int nodes = std::stoi(header.at(NODES_NUMBER));
	int zones = std::stoi(header.at(ZONES_NUMBER));

	std::vector<std::vector<Network::Arc>> adjList(nodes);

	std::string line;
	while (std::getline(file, line)) {
		sanitize(line);
		if (line.empty())
			continue;

		std::vector<std::string> split;
		boost::split(split, line, boost::is_any_of(" \t"), boost::token_compress_on);

		int fromNode = std::stoi(split.at(0)) - 1;
		int toNode = std::stoi(split.at(1)) - 1;
		double capacity = std::stod(split.at(2));
		double freeFlow = std::stod(split.at(4));
		if (freeFlow == 0)
			freeFlow = .0001;

		Network::Arc arc(fromNode, toNode, capacity, freeFlow);
		adjList.at(fromNode).push_back(arc);
	}

	return {adjList, zones};
}

Matrix Parser::parseODMatrix(const std::string &path) {
	std::ifstream file(path);
	if (file.fail())
		throw std::runtime_error("File " + path + " does not exist");

	auto header = parseHeader(file);

	int zones = std::stoi(header[ZONES_NUMBER]);

	Matrix odMatrix(zones);

	int fromNode = -1;
	std::string line;
	while (std::getline(file, line)) {
		sanitize(line);
		if (line.empty())
			continue;

		if (line.rfind("Origin", 0) == 0) {
			size_t pos = line.find_first_of(' ');
			fromNode = std::stoi(line.substr(pos + 1)) - 1;
		} else {
			auto splt = split(line, ';');

			for (const auto &item: splt) {
				if (item.empty())
					continue;

				size_t pos = item.find_first_of(':');

				int toNode = std::strtol(item.data(), nullptr, 10) - 1;
				double trips = std::strtod(item.data() + pos + 1, nullptr);
				odMatrix(fromNode, toNode) = trips;
			}
		}
	}

	return odMatrix;
}
