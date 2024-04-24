#ifndef BP_CPP_PRIORITYQUEUE_H
#define BP_CPP_PRIORITYQUEUE_H

#include <vector>

/**
 * A min priority queue implementation of primitive
 * integers as elements. Using a binary heap.
 */
class PriorityQueue {

private:
	/** Vector of values in the queue. */
	std::vector<int> values;

	/** Vector of priorities of values. */
	std::vector<double> priorities;

	/** Current size of queue. */
	int count;

public:
	/**
	 * Creates priority queue of constant size.
	 * @param size size of the queue
	 */
	explicit PriorityQueue(int size);

	/**
	 * Adds an element with given priority to the queue.
	 * @param value    element added to queue
	 * @param priority priority of added element
	 */
	void add(int value, double priority);

	/**
	 * Returns the element with lowest priority and
	 * removes it from the queue.
	 * @return element with lowest priority
	 */
	int popMin();

	/**
	 * Decreases priority of given element to new priority.
	 * @param value       element in the queue
	 * @param newPriority new priority, must be lower
	 */
	void decreasePriority(int value, double newPriority);

	/**
	 * Returns if the queue is empty.
	 * @return true, if the queue is empty, false otherwise
	 */
	bool isEmpty() const;

private:
	/**
	 * Fixes the binary heap in downwards motion.
	 * @param n index/starting point
	 */
	void fixDown(int n);

	/**
	 * Fixes the binary heap in upwards motion.
	 * @param n index/starting point
	 */
	void fixUp(int n);
};

#endif
