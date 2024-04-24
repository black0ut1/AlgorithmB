package cz.zcu.pperncka.data;

import java.util.Iterator;

/**
 * Class representing a subarray without copy.
 * @param <T> type of elements of this subarray
 */
public class ArrayView<T> implements Iterator<T>, Iterable<T> {
	
	/** Original array of this subarray. */
	private final T[] source;
	
	/** Index to source representing last element of this subarray (exclusive). */
	private final int last;
	
	/** Index to source representing first element of this subarray (inclusive). */
	private int first;
	
	/**
	 * Constructor creating a subarray of source array
	 * in the range first (inclusive) to last (exclusive).
	 * @param source source array of this subarray
	 * @param first  index to source representing first element of subarray
	 * @param last   index to source representing element after the last element of subarray
	 */
	public ArrayView(T[] source, int first, int last) {
		this.source = source;
		this.first = first;
		this.last = last;
	}
	
	@Override
	public boolean hasNext() {
		return first < last;
	}
	
	@Override
	public T next() {
		return source[first++];
	}
	
	@Override
	public Iterator<T> iterator() {
		return this;
	}
}
