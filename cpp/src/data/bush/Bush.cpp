#include "Bush.h"

Bush::Bush(int arcs)
		: arcFlags(arcs), flows(arcs) {}

void Bush::addArc(int arcIndex) {
	arcFlags[arcIndex] = true;
}

void Bush::removeArc(int arcIndex) {
	arcFlags[arcIndex] = false;
}

bool Bush::arcExists(int arcIndex) const {
	return arcFlags[arcIndex];
}

double Bush::getArcFlow(int arcIndex) const {
	return flows[arcIndex];
}

void Bush::addFlow(int arcIndex, double delta) {
	flows[arcIndex] += delta;
}
