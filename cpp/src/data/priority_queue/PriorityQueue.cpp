#include "PriorityQueue.h"

PriorityQueue::PriorityQueue(int size)
		: values(size), priorities(size), count(0) {}

void PriorityQueue::add(int value, double priority) {
	++count;
	priorities[count] = priority;
	values[count] = value;
	fixUp(count);
}

int PriorityQueue::popMin() {
	int min = values[1];

	values[1] = values[count];
	priorities[1] = priorities[count];
	--count;
	fixDown(1);

	return min;
}

void PriorityQueue::decreasePriority(int value, double newPriority) {
	for (int i = 0; i <= count; ++i) {
		if (values[i] == value) {
			priorities[i] = newPriority;
			fixUp(i);
			return;
		}
	}
}

bool PriorityQueue::isEmpty() const {
	return count == 0;
}

void PriorityQueue::fixDown(int n) {
	while (2 * n <= count) {
		int j = 2 * n;

		if (j + 1 <= count)
			if (priorities[j + 1] < priorities[j])
				++j;

		if (priorities[n] < priorities[j])
			return;
		else {
			std::swap(values[j], values[n]);
			std::swap(priorities[j], priorities[n]);
			n = j;
		}
	}
}

void PriorityQueue::fixUp(int n) {
	while (n != 1) {
		int p = n / 2;

		if (priorities[p] > priorities[n]) {
			std::swap(values[p], values[n]);
			std::swap(priorities[p], priorities[n]);
			n = p;
		} else return;
	}
}
