#include "Matrix.h"

Matrix::Matrix(int n)
		: n(n), data(n * n) {}

double Matrix::operator()(int i, int j) const {
	return data[i * n + j];
}

double &Matrix::operator()(int i, int j) {
	return (double &) data[i * n + j];
}
