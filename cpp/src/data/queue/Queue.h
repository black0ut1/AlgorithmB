#ifndef BP_CPP_QUEUE_H
#define BP_CPP_QUEUE_H

#include <vector>

/**
 * Class representing a queue of primitive integers with constant capacity.
 * Behaves exactly as one would expect of a queue.
 */
class Queue {
private:
	std::vector<int> data;
	int front;
	int rear;
	int size;

public:
	explicit Queue(int capacity);

	void enqueue(int item);

	int dequeue();

	bool isEmpty() const;
};

#endif
