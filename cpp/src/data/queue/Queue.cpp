#include "Queue.h"

Queue::Queue(int capacity)
		: data(capacity), rear(capacity - 1), front(0), size(0) {}

void Queue::enqueue(int item) {
	rear = (rear + 1) % (int) data.size();
	data[rear] = item;
	size++;
}

int Queue::dequeue() {
	int item = data[front];
	front = (front + 1) % (int) data.size();
	size--;
	return item;
}

bool Queue::isEmpty() const {
	return size == 0;
}
