package cz.zcu.pperncka.data;

/** Class representing row-major square matrix of doubles. */
public class Matrix {
	
	/** Row-major array of matrix data. */
	private final double[] arr;
	
	/** Rank of matrix. */
	public final int n;
	
	/**
	 * Constructor creating square matrix.
	 * @param n rank of matrix
	 */
	public Matrix(int n) {
		this.arr = new double[n * n];
		this.n = n;
	}
	
	/**
	 * Returns element on position (i, j).
	 * @param i row
	 * @param j column
	 * @return element on position (i, j)
	 */
	public double get(int i, int j) {
		return arr[i * n + j];
	}
	
	/**
	 * Sets element on position (i, j) to given value.
	 * @param i     row
	 * @param j     column
	 * @param value value to be set
	 */
	public void set(int i, int j, double value) {
		arr[i * n + j] = value;
	}
}
