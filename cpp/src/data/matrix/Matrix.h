#ifndef BP_CPP_MATRIX_H
#define BP_CPP_MATRIX_H

#include <vector>

/** Class representing row-major square matrix of doubles. */
class Matrix {
public:
	/** Rank of matrix. */
	const int n;

private:
	/** Row-major vector of matrix data. */
	const std::vector<double> data;

public:
	/**
	 * Constructor creating square matrix.
	 * @param n rank of matrix
	 */
	explicit Matrix(int n);

	/**
	 * Returns element on position (i, j).
	 * @param i row
	 * @param j column
	 * @return element on position (i, j)
	 */
	double operator()(int i, int j) const;

	/**
	 * Sets element on position (i, j) to given value.
	 * @param i     row
	 * @param j     column
	 * @param value value to be set
	 */
	double &operator()(int i, int j);
};

#endif
