package cz.zcu.pperncka.data;

/**
 * A min priority queue implementation of primitive
 * integers as elements. Using a binary heap.
 */
public class PriorityQueue {
	
	/** Array of values in the queue. */
	private final int[] values;
	
	/** Array of priorities of values. */
	private final double[] priorities;
	
	/** Current size of queue. */
	private int count = 0;
	
	/**
	 * Creates priority queue of constant size.
	 * @param size size of the queue
	 */
	public PriorityQueue(int size) {
		this.values = new int[size];
		this.priorities = new double[size];
	}
	
	/**
	 * Adds an element with given priority to the queue.
	 * @param value    element added to queue
	 * @param priority priority of added element
	 */
	public void add(int value, double priority) {
		count++;
		priorities[count] = priority;
		values[count] = value;
		fixUp(count);
	}
	
	/**
	 * Returns the element with lowest priority and
	 * removes it from the queue.
	 * @return element with lowest priority
	 */
	public int popMin() {
		int min = values[1];
		
		values[1] = values[count];
		priorities[1] = priorities[count];
		count--;
		fixDown(1);
		
		return min;
	}
	
	/**
	 * Decreases priority of given element to new priority.
	 * @param value       element in the queue
	 * @param newPriority new priority, must be lower
	 */
	public void decreasePriority(int value, double newPriority) {
		for (int i = 1; i <= count; i++)
			if (values[i] == value) {
				priorities[i] = newPriority;
				fixUp(i);
				return;
			}
	}
	
	/**
	 * Returns if the queue is empty.
	 * @return true, if the queue is empty, false otherwise
	 */
	public boolean isEmpty() {
		return count == 0;
	}
	
	/**
	 * Fixes the binary heap in upwards motion.
	 * @param n index/starting point
	 */
	private void fixUp(int n) {
		
		while (n != 1) {
			int p = n / 2;
			
			if (priorities[p] > priorities[n]) {
				swap(p, n);
				n = p;
			} else
				return;
		}
	}
	
	/**
	 * Fixes the binary heap in downwards motion.
	 * @param n index/starting point
	 */
	private void fixDown(int n) {
		while (2 * n <= count) {
			int j = 2 * n;
			
			if (j + 1 <= count)
				if (priorities[j + 1] < priorities[j])
					j++;
			
			if (priorities[n] < priorities[j])
				return;
			else {
				swap(j, n);
				n = j;
			}
		}
	}
	
	/**
	 * Swaps two elements and their according priorities
	 * @param x index of first element
	 * @param y index of first element
	 */
	private void swap(int x, int y) {
		double tmp = priorities[x];
		priorities[x] = priorities[y];
		priorities[y] = tmp;
		
		int tmp2 = values[x];
		values[x] = values[y];
		values[y] = tmp2;
	}
}
