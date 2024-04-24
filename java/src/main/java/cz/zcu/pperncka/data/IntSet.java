package cz.zcu.pperncka.data;

import java.util.Arrays;

/** Trivial implementation of set of primitive inregers */
public class IntSet {
	
	/** Rate of enlargement of {@code data} after its filled. */
	private final double growthRate;
	
	/** Array of data. */
	private int[] data;
	
	/** Number of elements in this set. */
	private int count = 0;
	
	/**
	 * Constructor creating a integer set with given
	 * initial capacity and growth rate.
	 * @param initSize   initial capacity
	 * @param growthRate growth rate
	 */
	public IntSet(int initSize, double growthRate) {
		this.growthRate = growthRate;
		this.data = new int[initSize];
	}
	
	/**
	 * Adds integer to this set.
	 * @param x integer added to set
	 */
	public void add(int x) {
		if (count == data.length)
			data = Arrays.copyOf(data, (int) (data.length * growthRate));
		
		data[count++] = x;
	}
	
	/**
	 * Checks, if this set contains integer.
	 * Suboptimal implementation but it is expected that
	 * the set will be small and will fit to cache.
	 * @param x integer
	 * @return true, if this set contains given integer, false otherwise
	 */
	public boolean contains(int x) {
		for (int i = 0; i < count; i++)
			if (x == data[i])
				return true;
		
		return false;
	}
}
