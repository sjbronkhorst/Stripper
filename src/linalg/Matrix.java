package linalg;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class Matrix implements Externalizable, Serializable {

	private static final long serialVersionUID = 1L;

	private static Set<Matrix> s_matrixSet = new HashSet<Matrix>();
	private static Map<String, Set<Matrix>> s_matrixMap = new HashMap<String, Set<Matrix>>();
	private static Map<String, Integer> s_couterMap = new HashMap<String, Integer>();

	private static boolean verbose = System.getProperties().containsKey(
			"matrixInfo");

	private static String getKey(int rows, int cols) {
		return "[" + rows + "x" + cols + "]";
	}

	/**
	 * Returns a matrix with the specified number of rows and columns. 
	 * Real values (double) can be inserted with respect to the row and column. 
	 * The rows and columns are numbered from zero.
	 * The matrix is created with standalone set to false, so it
	 * should be released after use in order to free up the
	 * memory it consumed.
	 * 
	 * @param rows
	 *            - number of rows
	 * @param cols
	 *            - number of columns
	 * @return a matrix with specified size
	 */
	public synchronized static Matrix getMatrix(int rows, int cols) {
		String key = getKey(rows, cols);
		if (!s_matrixMap.containsKey(key)) {
			s_matrixMap.put(key, new HashSet<Matrix>());
			s_couterMap.put(key, 0);
		}
		Set<Matrix> set = s_matrixMap.get(key);
		if (set.isEmpty()) {
			s_couterMap.put(key, s_couterMap.get(key) + 1);
			Matrix mat = new Matrix(rows, cols);
			if (verbose)
				System.err.println("Matrix [" + rows + "," + cols
						+ "] created!");
			s_matrixSet.add(mat);
			mat.m_standalone = false;
			return mat;
		} else {
			Iterator<Matrix> iter = set.iterator();
			Matrix mat = iter.next();
			iter.remove();
			mat.locked = false;
			mat.m_standalone = false;
			return mat;
		}
	}

	/**
	 * Returns a matrix with the specified number of rows and columns, and
	 * given "mode" of usage. If the matrix should exist for the duration of
	 * the application, standalone should be set to true. If the matrix will
	 * only exist for a while, i.e. it gets used and then recomputed, standalone
	 * should be set to false. After usage the matrix is then released using
	 * the release() method and its memory requirements are freed for re-use.
	 * Real values (double) can be inserted with respect to the row and column. 
	 * The rows and columns are numbered from zero.
	 * The matrix should be "release()" after use in order to free up the
	 * memory it consumed.
	 * 
	 * @param rows
	 *            - number of rows
	 * @param cols
	 *            - number of columns
	 * @param standalone
	 * 			  - true if matrix is permanent, false if it is transient
	 * @return a matrix with specified size
	 */
	public synchronized static Matrix getMatrix(int rows, int cols,
			boolean standalone) {
		if (standalone)
			return new Matrix(rows, cols);
		else
			return getMatrix(rows, cols);
	}

	/**
	 * Prints the statistics of the program with respect to all matrices. Prints
	 * the following:
	 * <ul>
	 * <li>Size of the matrices
	 * <li>Percentage available matrices
	 * <li>Total number of matrices
	 * </ul>
	 */
	public static void stats(PrintStream stream) {
		Iterator<Map.Entry<String, Set<Matrix>>> iter = s_matrixMap.entrySet()
				.iterator();
		stream.println("\nMatrix service stats...");
		int available = 0;
		while (iter.hasNext()) {
			Map.Entry<String, Set<Matrix>> entry = iter.next();
			int _total = s_couterMap.get(entry.getKey());
			stream.println(entry.getKey() + "\t(" + entry.getValue().size()
					+ " of " + _total + ") available");
			available += entry.getValue().size();
		}
		stream.println("Total number of matrices = " + s_matrixSet.size()
				+ "\t[" + Math.round(available * 100. / s_matrixSet.size())
				+ "% available]\n");
	}

	private static final String VALUE = "[\\-|\\+]?\\d*\\.?\\d*";
	private static final String OPR = "\\s*[\\+|\\*|\\-|\\/]\\s*";  
	private static final String EXPR = "[(|"+VALUE+"]+[[("+VALUE+OPR+"]|"+OPR+VALUE+"|)]*";
	private static final String VEC_STRING = "\\{["+EXPR+"|"+VALUE+"][\\s*,\\s*["+EXPR+"|"+VALUE+"]]*\\}";
	private static final String MAT_STRING = "\\{\\s*"+VEC_STRING+"[\\s*,\\s*"+VEC_STRING+"]*\\}";
	
	public static Matrix getMatrix(String values, boolean standalone){
		if(values.matches(MAT_STRING)){
			String[] _parts = values.substring(1,values.length()-1).split("\\}\\s*,\\s*\\{");
			StringTokenizer st = new StringTokenizer(_parts[0].replace(" ", "").substring(1),",");
			int rows = _parts.length;
			int cols = st.countTokens();
			Matrix mat = Matrix.getMatrix(rows,cols, standalone);
			for(int i = 0; i < rows; i++){
				for(int j = 0; j < cols; j++){
					String val = st.nextToken().trim().replace(" ", "");
					if(val.matches(VALUE))
						mat.m_values[i][j] = Double.parseDouble(val);
					else if(val.matches(EXPR)){
						try {
							mat.m_values[i][j] = InlineCalculator.calc("="+val);
						} catch (InvalidParameterException e) {
							throw new MatrixDimensionException("Invalid matrix format : " +val);
						}
					}					
				}	
				if(i < rows - 2)
					st = new StringTokenizer(_parts[i+1].replace(" ", ""),",");
				else if(i < rows - 1)
					st = new StringTokenizer(_parts[i+1].replace(" ", "").replace("}",""),",");
				if(st.countTokens() != cols && i < rows - 1)
					throw new MatrixDimensionException("Incorrect entry count of "+st.countTokens()+" (should be "+cols+")");
			}
			return mat;
		}
		else
			throw new VectorDimensionException("Invalid vector format : " +values);
	}
	
	public static Matrix getMatrix(String values){
		return getMatrix(values,false);
	}
	
	
	// end of static stuff.
	private double[][] m_values;
	private boolean locked = false;
	private boolean m_standalone = false;

	public Matrix() {
	}

	private Matrix(int rows, int cols) {
		m_values = new double[rows][cols];
		m_standalone = true;
	}

	private void _check() {
		if (locked)
			throw new MatrixLockedException();
	}

	/**
	 * Returns a boolean which states if a double value is zero. This method
	 * rounds a value and test if it is near equal to zero.
	 * 
	 * @param d
	 *            a value that needs to be tested if it is near zero.
	 * @return a boolean if the value is near zero.
	 */
	private boolean isZero(double d) {
		if (Math.abs(d) < 1e-6)
			return true;
		return false;
	}

	/**
	 * Sets a value to an element in a matrix. The position is specified by the
	 * <code>int row</code> and <code>col</code>. If the matrix is locked a
	 * matrix locked exception error will occur.
	 * 
	 * @param a
	 *            value to be assigned to the element
	 * @param an
	 *            index of the row the value are inserted into
	 * @param an
	 *            index of the column the value are to be inserted into
	 */
	public void set(double value, int row, int col) {
		_check();
		m_values[row][col] = value;
	}

	/**
	 * Adds a value to an element in a matrix. The position is specified by the
	 * <code>int row</code> and <code>col</code>. If the matrix is locked a
	 * matrix locked exception error will occur. The element in the matrix will
	 * be added to the new <code>double</code>.
	 * 
	 * @param a
	 *            value to be added to the element
	 * @param an
	 *            index of the row the value are inserted into
	 * @param an
	 *            index of the column the value are to be inserted into
	 */
	public void add(double value, int row, int col) {
		_check();
		m_values[row][col] = value;
	}

	/**
	 * Sets a vector to a matrix. The vector will be set to a row specified in
	 * <code>int row</code>. The vector's length must be the same as the
	 * matrix's number of columns. If the matrix is locked a matrix locked
	 * exception will be shown.
	 * 
	 * @param the
	 *            vector that will be set in the matrix
	 * @param the
	 *            index of the row the vector will be set
	 */
	public void setRow(Vector vec, int row) {
		_check();
		vec._check();
		if (m_values[row].length != vec.size() || row >= rows())
			throw new MatrixDimensionException();
		System.arraycopy(vec.get(), 0, m_values[row], 0, cols());
	}

	/**
	 * Sets a vector to a matrix. The vector will be set to a column specified
	 * in <code>int col</code>. The vector's length must be the same as the
	 * matrix's number of rows. If the matrix is locked a matrix locked
	 * exception will be shown.
	 * 
	 * @param the
	 *            vector that will be set in the matrix
	 * @param the
	 *            index of the column the vector will be set
	 */
	public void setCol(Vector vec, int col) {
		_check();
		vec._check();
		if (m_values.length != vec.size() || col >= cols())
			throw new MatrixDimensionException();
		for (int i = 0; i < vec.size(); i++)
			m_values[i][col] = vec.get(i);
	}

	/**
	 * Sets a array to a matrix. The vector will be set to a row specified in
	 * <code>int row</code>. The vector's length must be the same as the
	 * matrix's number of columns. If the matrix is locked a matrix locked
	 * exception will be shown.
	 * 
	 * @param the
	 *            array that will be set in the matrix
	 * @param the
	 *            index of the row the array will be set
	 */
	public void setRow(double[] values, int row) {
		_check();
		if (m_values[row].length != values.length || row >= rows())
			throw new MatrixDimensionException();
		System.arraycopy(values, 0, m_values[row], 0, cols());
	}

	/**
	 * Sets a array to a matrix. The vector will be set to a column specified in
	 * <code>int col</code>. The vector's length must be the same as the
	 * matrix's number of rows. If the matrix is locked a matrix locked
	 * exception will be shown.
	 * 
	 * @param the
	 *            array that will be set in the matrix
	 * @param the
	 *            index of the column the array will be set
	 */
	public void setCol(double[] values, int col) {
		_check();
		if (m_values.length != values.length || col >= cols())
			throw new MatrixDimensionException();
		for (int i = 0; i < values.length; i++)
			m_values[i][col] = values[i];
	}

	/**
	 * Sets a value to an element in a matrix. The matrix's dimensions must
	 * agree with that of the double array.
	 * 
	 * @param values
	 *            is the matrix's values in the same order as given by
	 *            <code>double[][] values</code>
	 */
	public void set(double[][] values) {
		_check();
		if (values.length != rows() || values[0].length != cols())
			throw new MatrixDimensionException();
		for (int i = 0; i < m_values.length; i++)
			System.arraycopy(values[i], 0, m_values[i], 0, cols());
	}

	/**
	 * Returns a <code>double</code> value which is the element at a specific
	 * index. The index include specifying the row and column. If the row- or
	 * column index is not in the index of the matrix a
	 * <code>MatrixDimensionException</code> will be shown.
	 * 
	 * @param row
	 *            the index of the row the element will be found at
	 * @param col
	 *            the index of the column the element will be found at
	 * @return the element of specified location
	 */
	public double get(int row, int col) {
		_check();
		if (row >= rows() || col >= cols())
			throw new MatrixDimensionException();
		return m_values[row][col];
	}

	/**
	 * Returns the number of rows in the matrix. If the matrix is
	 * <code>locked</code> a
	 * <code>MatrixLockedException<code> will be shown. Method counts and returns 
	 * the number of rows
	 * 
	 * @return the number of rows present
	 */
	public int rows() {
		_check();
		return m_values.length;
	}

	/**
	 * Returns the number of columns in the matrix. If the matrix is
	 * <code>locked</code> a <code>MatrixLockedException<code> will be shown.
	 * 
	 * @return the number of columns present
	 */
	public int cols() {
		_check();
		return m_values[0].length;
	}

	/**
	 * Clones the contents of this matrix, expensive to call. If the matrix is
	 * <code>locked</code> a
	 * <code>MatrixDimensionException<code> will be shown. This method sets all
	 * the values of the matrix in a double array.
	 * 
	 * @return the same elements as in the matrix but in array form
	 */
	public double[][] get() {
		_check();
		double[][] _val = new double[rows()][];
		for (int i = 0; i < _val.length; i++)
			_val[i] = m_values[i].clone();
		return _val;
	}

	/**
	 * Returns a <code>vector</code> with the same elements as the column has.
	 * The column must be specified and must be in the bounds of the matrix. The
	 * returned vector is called a column vector. Cannot be called if matrix is
	 * locked
	 * 
	 * @param the
	 *            index of the column
	 * @return the column vector
	 */
	public Vector col(int col) {
		_check();
		Vector vec = Vector.getVector(rows());
		for (int i = 0; i < rows(); i++)
			vec.set(m_values[i][col], i);
		return vec;
	}

	/**
	 * Returns a <code>vector</code> with the same elements as the row has. The
	 * row must be specified and must be in the bounds of the matrix. Cannot be
	 * called if matrix is locked. The returned vector is a row vector.
	 * 
	 * @param the
	 *            index of the row
	 * @return the row vector but in transposed form
	 */
	public Vector row(int row) {
		_check();
		Vector vec = Vector.getVector(cols());
		vec.set(m_values[row]);
		return vec;
	}

	/**
	 * Swops two rows in a matrix. The two row's index's must be specified and
	 * be in the bounds of the matrix's dimensions. No value is returned, but
	 * the matrix has been changed.
	 * 
	 * @param row1
	 *            's index is specified
	 * @param row2
	 *            's index is specified
	 */
	public void swopRow(int r1, int r2) {
		_check();
		for (int j = 0; j < m_values[0].length; j++) {
			double _tmp = m_values[r1][j];
			m_values[r1][j] = m_values[r2][j];
			m_values[r2][j] = _tmp;
		}
	}

	/**
	 * Swops two columns in a matrix. The column's index's must be specified and
	 * be in the bounds of the matrix's dimensions. No value is returned, but
	 * the matrix has been changed from within.
	 * 
	 * @param column1
	 *            's index is specified
	 * @param column2
	 *            's index is specified
	 */
	public void swopCol(int c1, int c2) {
		_check();
		for (int i = 0; i < m_values.length; i++) {
			double _tmp = m_values[i][c1];
			m_values[i][c1] = m_values[i][c2];
			m_values[i][c2] = _tmp;
		}
	}

	/**
	 * Sets a copied row in a matrix at the same row index. The
	 * <code>integer row</code> is the index in which the row is copied. This
	 * index is also the row of the matrix <code>src</code> as well as the given
	 * matrix. The row is copied from one matrix to another.
	 * 
	 * @param The
	 *            matrix out of which the row will be copied
	 * @param The
	 *            index of the row
	 */
	public void copyRow(Matrix src, int row) {
		_check();
		src._check();
		if (cols() != src.cols())
			throw new MatrixDimensionException();
		System.arraycopy(src.m_values[row], 0, m_values[row], 0, cols());
	}

	/**
	 * Sets a copied column in a matrix at the same column index. The
	 * <code>integer column</code> is the index in which the column is copied.
	 * This index is also the column of the matrix <code>src</code> as well as
	 * the given matrix. The column is copied from one matrix to another.
	 * 
	 * @param The
	 *            matrix out of which the row will be copied
	 * @param The
	 *            index of the row
	 */
	public void copyRow(Vector src, int row) {
		_check();
		if (cols() != src.rows())
			throw new MatrixDimensionException();
		System.arraycopy(src.get(), 0, m_values[row], 0, cols());
	}

	/**
	 * Returns the sum of all the elements in the spesified row. The row must be
	 * in the dimensions of the matrix.
	 * 
	 * @param selects
	 *            the row of the elements that must be added
	 * @return the sum of the elements in a row
	 */
	public double sumRow(int rowIndex) {
		_check();
		double sum = 0.0;
		for (int j = 0; j < m_values[rowIndex].length; j++)
			sum += m_values[rowIndex][j];
		return sum;
	}

	/**
	 * Returns the sum of all the elements in the spesified column. The column
	 * must be in the dimensions of the matrix.
	 * 
	 * @param selects
	 *            the column of the elements that must be added
	 * @return the sum of the elements in a column
	 */
	public double sumColumn(int colIndex) {
		_check();
		double sum = 0.0;
		for (int i = 0; i < m_values.length; i++)
			sum += m_values[i][colIndex];
		return sum;
	}

	/**
	 * Returns the maximum number in the matrix. The values must have been set
	 * earlier This value is a <code>double</code>. This is also the element at
	 * the same position
	 * 
	 * @return the maximum number in the given matrix
	 */
	public double maxEntry() {
		double max = -Double.MAX_VALUE;
		for (int i = 0; i < m_values.length; i++) {
			for (int j = 0; j < m_values[i].length; j++) {
				max = (max < m_values[i][j]) ? m_values[i][j] : max;
			}
		}
		return max;
	}

	/**
	 * Returns the minimum number in the matrix. The values must have been set
	 * earlier to retrieve the correct value.
	 * 
	 * @return the minimum value in the matrix
	 */
	public double minEntry() {
		double min = Double.MAX_VALUE;
		for (int i = 0; i < m_values.length; i++) {
			for (int j = 0; j < m_values[i].length; j++) {
				min = (min > m_values[i][j]) ? m_values[i][j] : min;
			}
		}
		return min;
	}

	/**
	 * Returns the minimum entry in a specific row in a matrix. The row index
	 * must be equal or smaller than the matrix's number of rows.
	 * 
	 * @param row
	 *            the index of the matrix's row
	 * @return the minimum value in a matrix
	 */
	public double minRowEntry(int row) {
		_check();
		double _min = m_values[row][0];
		for (int i = 1; i < rows(); i++)
			_min = (_min > m_values[row][i]) ? m_values[row][i] : _min;
		return _min;
	}

	/**
	 * Search for the smallest number in a column. The specified column must be
	 * in the dimensions of the matrix.
	 * 
	 * @param the
	 *            column index
	 * @return the minimum value in the specified column
	 */
	public double minColEntry(int col) {
		_check();
		double _min = m_values[0][col];
		for (int i = 1; i < cols(); i++)
			_min = (_min > m_values[i][col]) ? m_values[i][col] : _min;
		return _min;
	}

	/**
	 * Search for the maximum number in a row. The row specified must be in the
	 * matrix's dimensions
	 * 
	 * @param row
	 *            an index of the row
	 * @return the maximum value in a certain row
	 */
	public double maxRowEntry(int row) {
		_check();
		double _max = m_values[row][0];
		for (int i = 1; i < rows(); i++)
			_max = (_max < m_values[row][i]) ? m_values[row][i] : _max;
		return _max;
	}

	/**
	 * Search for the maximum number in a column. The column specified must be
	 * in the matrix's dimensions.
	 * 
	 * @param an
	 *            index of the column
	 * @return the maximum value in a certain column
	 */
	public double maxColEntry(int col) {
		_check();
		double _max = m_values[0][col];
		for (int i = 1; i < cols(); i++)
			_max = (_max < m_values[i][col]) ? m_values[i][col] : _max;
		return _max;
	}

	/**
	 * Sets the memory free that was used by the matrix. This allows the user to
	 * use this matrix again
	 * 
	 * @return a null matrix
	 */
	public Matrix release() {
		if (m_standalone)
			throw new MatrixLockedException(
					"May not release a standalone Matrix");
		clear();
		locked = true;
		s_matrixMap.get(getKey(m_values.length, m_values[0].length)).add(this);
		return null;
	}

	/**
	 * Sets a matrix's values all to zero. Useful when large matrix's values
	 * have all to be set to zero. The matrix must be given a size but need not
	 * have any values
	 */
	public void clear() {
		_check();
		for (int i = 0; i < m_values.length; i++)
			for (int j = 0; j < m_values[i].length; j++)
				m_values[i][j] = 0.0;
	}

	/**
	 * Makes the given matrix an identity matrix. Matrix must have specified
	 * size Creates everywhere in the matrix zero's except on the diagonals the
	 * values are set to 1. Also known as [I]
	 */
	public void identity() {
		_check();
		if (rows() != cols())
			throw new MatrixDimensionException();
		clear();
		for (int i = 0; i < rows(); i++)
			m_values[i][i] = 1.0;
	}

	/**
	 * Returns a matrix which is this matrix multiplied by the specified matrix
	 * in <code>matrix</code> Special cases:
	 * <ul>
	 * <li>the given matrix's columns must be the same as the number of rows in
	 * the specified matrix
	 * <li>both matrix's must have been instantiated
	 * <li>the products size will depend on the given matrix's number of rows
	 * and the specified matrix's number of columns
	 * </ul>
	 * 
	 * @param matrix
	 *            - the specified matrix aka the second matrix in the order
	 * @return a matrix which is <code>[return new] = [this]x[matrix]</code>
	 */
	public Matrix multiply(Matrix matrix) {
		_check();
		matrix._check();
		Matrix result = getMatrix(rows(), matrix.cols());
		double[][] a = m_values;
		double[][] b = matrix.m_values;
		double _r = 0.0;
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < b[0].length; j++) {
				for (int k = 0; k < a[0].length; k++) {
					_r += a[i][k] * b[k][j];
				}
				result.set(_r, i, j);
				_r = 0.0;
			}
		}
		return result;
	}

	/**
	 * Returns a matrix which is pre and post multiplied by the matrix <code>
	 * matrix</code>. This means <code>[matrix][given matrix][matrix]T.
	 * 
	 * @param matrix
	 *            a matrix that will be pre- and postmultiplied.
	 * @return the result of a pre- post multiplication.
	 */
	public Matrix prePostTMultiply(Matrix matrix) {
		_check();
		matrix._check();
		Matrix tmp = matrix.multiply(this);
		Matrix transpose = matrix.transpose();
		Matrix result = tmp.multiply(transpose);
		tmp.release();
		transpose.release();
		return result;
	}

	/**
	 * Returns the multiplication of a matrix with a vector Special cases:
	 * <ul>
	 * <li>The vector's size must equal the number of columns in the matrix
	 * <li>The matrix's values must have been set earlier
	 * <li>The vector's values must have been set earlier
	 * <li>The returned vector is one with size = matrix's number of rows
	 * <li>The first row of the matrix is multiplied by the vector ect.
	 * </ul>
	 * 
	 * @param vector
	 *            is the vector that will be multiplied by the matrix
	 * @return vector which is <code>[matrix]{vector}</code>
	 */
	public Vector multiply(Vector vector) {
		_check();
		vector._check();
		if (cols() != vector.rows())
			throw new VectorDimensionException("Length of vector = "
					+ vector.rows() + " should be " + cols());
		Vector result = Vector.getVector(rows());
		double[][] a = m_values;
		double[] b = vector.get();
		double _r = 0.0;
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				_r += a[i][j] * b[j];
			}
			result.set(_r, i);
			_r = 0.0;
		}
		return result;
	}

	/**
	 * Returns the inverse of a 2x2 matrix Rules for use:
	 * <ul>
	 * <li>The matrix must be of size 2x2
	 * <li>The inverse will be of size 2x2
	 * <li>The matrix must not be singular (determinant = 0)
	 * <li>The values must have been set earlier
	 * </ul>
	 * 
	 * @return a matrix - the inverse of the given matrix
	 */
	public Matrix inverse2x2() {
		_check();
		if (rows() != 2 || cols() != 2)
			throw new MatrixDimensionException(
					"only support 2x2 inverse calculations at the moment");
		Matrix inv = getMatrix(2, 2);
		inv.set(m_values[1][1], 0, 0);
		inv.set(-m_values[0][1], 0, 1);
		inv.set(-m_values[1][0], 1, 0);
		inv.set(m_values[0][0], 1, 1);
		inv.scale(1. / determinant());
		return inv;
	}

	// the first row contains the eigen values,
	// the second row the eigen vector that corresponds to the first eigen value
	// and the third row the eigen vector that corresponds to the second eigen
	// value
	/**
	 * Returns the eigenvalues of the given matrix
	 * <ul>
	 * <li>The matrix must be of size 2x2
	 * <li>Two eigenvalues will be returned in vector form
	 * <li>The first eigenvalue corresponds to the first eigenvector
	 * </ul>
	 * 
	 * @return vector with first and second eigenvalues
	 */
	public Vector getEigenValues2x2() {
		_check();
		Vector ev = Vector.getVector(2);

		// http://en.wikipedia.org/wiki/Eigenvalue_algorithm
		// http://www.math.harvard.edu/archive/21b_fall_04/exhibits/2dmatrices/index.html
		// lamda = (a+d)/2 +- sqrt(4*b+(a-d)²)/2
		// | a b |
		// | c d |

		double a = m_values[0][0];
		double b = m_values[0][1];
		double c = m_values[1][0];
		double d = m_values[1][1];

		double _trace = a + d;
		double _deter = a * d - b * c;
		double sqrt = Math.sqrt(_trace * _trace / 4. - _deter);
		double l1 = _trace / 2 + sqrt;
		double l2 = _trace / 2 - sqrt;

		ev.set(l1, 0);
		ev.set(l2, 1);
		return ev;
	}

	/**
	 * Returns the eigenvectors of a certain matrix The return value is in
	 * vector array form The first element in the array is the first eigenvector
	 * This vector corresponds to the first eigenvalue, the same for the second
	 * The size of the matrix must be a 2x2 The matrix's values must have been
	 * set earlier
	 * 
	 * @return the eigenvectors in a array of vectors
	 */
	public Vector[] getEigenVectors2x2() {
		double a = m_values[0][0];
		double b = m_values[0][1];
		double c = m_values[1][0];
		double d = m_values[1][1];

		double _trace = a + d;
		double _deter = a * d - b * c;
		double sqrt = Math.sqrt(_trace * _trace / 4. - _deter);
		double l1 = _trace / 2 + sqrt;
		double l2 = _trace / 2 - sqrt;

		Vector[] _ev = new Vector[2];
		_ev[0] = Vector.getVector(2);
		_ev[1] = Vector.getVector(2);

		if (c != 0) {
			_ev[0].set(l1 - d, 0);
			_ev[0].set(c, 1);
			_ev[1].set(l2 - d, 0);
			_ev[1].set(c, 1);
		} else if (m_values[0][1] != 0) {
			_ev[0].set(b, 0);
			_ev[0].set(l1 - a, 1);
			_ev[1].set(b, 0);
			_ev[1].set(l2 - a, 1);
		} else {
			_ev[0].set(1.0, 0);
			_ev[1].set(1.0, 1);
		}
		_ev[0].normalize();
		_ev[1].normalize();
		_ev[0].scale(l1);
		_ev[1].scale(l2);
		return _ev;
	}

	
	public EigenvalueDecomposition getEigenValueDecomposition(){
		return new EigenvalueDecomposition(this);
	}
	
	/**
	 * @return The eigenvalues of the matrix as the elements of a Vector
	 */
	public Vector eigenValues(){
		EigenvalueDecomposition ed = new EigenvalueDecomposition(this);
		return ed.getRealEigenvalues();
	}
	
	/**
	 * @return The eigenvalues of the matrix in diagonal-matrix form
	 */
	public Matrix eigenMatrix(){
		EigenvalueDecomposition ed = new EigenvalueDecomposition(this);
		return ed.getD();
	}
	
	/**
	 * @return The eigenvectors of the matrix as the columns of the returned Matrix
	 */
	public Matrix getEigenVectors(){
		EigenvalueDecomposition ed = new EigenvalueDecomposition(this);
		return ed.getEigenVectors();		
	}
	

	/**
	 * Returns the determinant of the matrix. If the determinant is equal to
	 * zero the matrix is singular. Matrix up to 3x3
	 * 
	 * @return the determinant of a matrix
	 */
    public double determinant(){    
		_check();
       if (rows() > 3)
			throw new MatrixDimensionException("Only support up to 3x3 determinant calculations.");
       if ((rows() != cols()))
			throw new MatrixDimensionException("Determinant only for square matrices up to 3x3.");
        if (rows() == 1){
            return m_values[0][0];
        }
        else if (rows() == 2){
    		return m_values[0][0]*m_values[1][1]-m_values[0][1]*m_values[1][0];
        }
        else {   // 3x3 matrix
            double t1 = m_values[0][0] * (m_values[1][1] * m_values[2][2] - m_values[1][2] * m_values[2][1]);
            double t2 = m_values[0][1] * (m_values[1][0] * m_values[2][2] - m_values[1][2] * m_values[2][0]);
            double t3 = m_values[0][2] * (m_values[1][0] * m_values[2][1] - m_values[1][1] * m_values[2][0]);
            return (t1 - t2 + t3);
         }
      }

	/**
	 * Returns the transpose of the given matrix. This will be a matrix which
	 * has size <code>numOfCols, numOfRows</code> where <code>numOfCols</code>
	 * and <code>numOfRows</code> the number of columns and rows of the matrix
	 * is respectively.
	 * <p>
	 * <code>transpose()</code> let's the first row become the first column and
	 * the second and third ect.
	 * <p>
	 * This method will be correct for any given size of the matrix
	 * 
	 * @return the transpose of the matrix
	 */
	public Matrix transpose() {
		Matrix transpose = getMatrix(cols(), rows());
		for (int i = 0; i < m_values.length; i++)
			for (int j = 0; j < m_values[i].length; j++)
				transpose.m_values[j][i] = m_values[i][j];
		return transpose;
	}

	/**
	 * Returns the same matrix but each element scaled with a factor. This
	 * factor is the <code>double value</code>. This factor will be multiplied
	 * with all the elements.
	 * <p>
	 * This allows the user to scale the whole matrix with a constant.
	 * 
	 * @param value
	 *            - the constant that will be multiplied by the matrix
	 * @return the matrix multiplied by a constant
	 */
	public void scale(double value) {
		_check();
		for (int i = 0; i < m_values.length; i++)
			for (int j = 0; j < m_values[i].length; j++)
				m_values[i][j] *= value;
	}

	/**
	 * Adds to matrix's and returns the answer. The two matrix's must have the
	 * same number of rows and columns to have a succesful addition.Each element
	 * will be added to the corresponding element in the other matrix.
	 * 
	 * @param matrix
	 *            - the matrix with with the addition must be done
	 * @return the addition of two matrices
	 */
	public void add(Matrix matrix) {
		_check();
		matrix._check();
		if (rows() != matrix.rows() || cols() != matrix.cols())
			throw new MatrixDimensionException();

		for (int i = 0; i < m_values.length; i++)
			for (int j = 0; j < m_values[i].length; j++)
				m_values[i][j] += matrix.m_values[i][j];
	}

	/**
	 * Adds a submatrix to another matrix. This submatrix must be smaller or of
	 * the same size as the main matrix. The submatrix will be added in a
	 * certain position specified by the <code>int[] indices</code>
	 * <p>
	 * The indices is a discription of the rows and columns the submatrix should
	 * be added to. For example: if the submatrix have to be added to first and
	 * third row this is the indides the parameter would have.
	 * 
	 * @param indices
	 *            contains the rows and columns in which the submatrix will be
	 *            added
	 * @return the matrix with the submatrix added.
	 */
	public void addSubmatrix(Matrix mat, int[] indices) {
		_check();
		mat._check();
		if (mat.rows() != indices.length || mat.cols() != indices.length)
			throw new MatrixDimensionException();
		for (int i = 0; i < mat.rows(); i++) {
			for (int j = 0; j < mat.cols(); j++) {
				m_values[indices[i]][indices[j]] += mat.m_values[i][j];
			}
		}
	}

	/**
	 * Adds a submatrix to another matrix. This submatrix must be smaller or of
	 * the same size as the main matrix. The submatrix will be added in a
	 * certain position specified by the <code>int[] rows , 
	 * int[] cols</code>.The <code>int[] rows</code> is the rows the submatrix
	 * have to be added and <code>int[] cols</code> the columns specifies the
	 * position of the columns.
	 * 
	 * @param indices
	 *            contains the rows and columns in which the submatrix will be
	 *            added
	 * @return the matrix with the submatrix added.
	 */
	public void addSubmatrix(Matrix mat, int[] rows, int[] cols) {
		_check();
		mat._check();
		if (mat.rows() != rows.length || mat.cols() != cols.length)
			throw new MatrixDimensionException();
		for (int i = 0; i < mat.rows(); i++) {
			for (int j = 0; j < mat.cols(); j++) {
				m_values[rows[i]][cols[j]] += mat.m_values[i][j];
			}
		}
	}

	/**
	 * Adds a submatrix to another matrix. This submatrix must be smaller or the
	 * same size as the main matrix. The submatrix will be added in a certain
	 * position specified by the <code>boolean[] indices</code>
	 * <p>
	 * The indices is a discription of the rows and columns the submatrix should
	 * be added to. If a element in the array is true the row and column will be
	 * taken as the position where the submatrix will be added.
	 * 
	 * @param indices
	 *            contains the rows and columns in which the submatrix will be
	 *            added
	 * @return the matrix with the submatrix added.
	 */
	public Matrix getSubMatrix(boolean[] indices) {
		_check();
		if (rows() != indices.length || cols() != indices.length)
			throw new MatrixDimensionException();
		int dim = 0;
		for (int i = 0; i < indices.length; i++) {
			if (indices[i])
				dim++;
		}
		Matrix _sub = Matrix.getMatrix(dim, dim);
		int row = 0;
		for (int i = 0; i < indices.length; i++) {
			if (!indices[i])
				continue;
			int col = 0;
			for (int j = 0; j < indices.length; j++) {
				if (!indices[j])
					continue;
				_sub.set(m_values[i][j], row, col);
				col++;
			}
			row++;
		}
		return _sub;
	}

	/**
	 * Adds a submatrix to another matrix. This submatrix must be smaller or the
	 * same size as the main matrix. The submatrix will be added in a certain
	 * position specified by the <code>boolean[] rows,
	 * boolean[] cols</code>.
	 * <p>
	 * The rows and cols is a discription of the rows and columns the submatrix
	 * should be added to respectively. If an element in the array is true the
	 * row or column will be taken as the position where the submatrix will be
	 * added.
	 * <p>
	 * This method is useful if the row and column specifications are not the
	 * same.
	 * 
	 * @param rows
	 *            contains the rows in which the submatrix will be added
	 * @param cols
	 *            contains the columns in which the submatrix will be added
	 * @return the matrix with the submatrix added.
	 */
	public Matrix getSubMatrix(boolean[] rows, boolean[] cols) {
		_check();
		if (rows() != rows.length || cols() != cols.length)
			throw new MatrixDimensionException();
		int _rows = 0;
		for (int i = 0; i < rows.length; i++) {
			if (rows[i])
				_rows++;
		}
		int _cols = 0;
		for (int j = 0; j < cols.length; j++) {
			if (cols[j])
				_cols++;
		}
		Matrix _sub = Matrix.getMatrix(_rows, _cols);
		int row = 0;
		for (int i = 0; i < rows(); i++) {
			if (!rows[i])
				continue;
			int col = 0;
			for (int j = 0; j < cols(); j++) {
				if (!cols[j])
					continue;
				_sub.set(m_values[i][j], row, col);
				col++;
			}
			row++;
		}
		return _sub;
	}

	/**
	 * Returns a matrix in another matrix in other words a submatrix in a larger
	 * matrix. This submatrix is retrievable by this method. The submatrix is a
	 * matrix which size depends on the number of rows and columns included in
	 * the <code>int[] indices</code>. The matrix returned is square and
	 * contains the same elements as the main matrix but only with the specified
	 * rows and columns.
	 * 
	 * @param indices
	 *            - gives the selections of the rows and columns in the
	 *            submatrix.
	 * @return a submatrix.
	 */
	public Matrix getSubMatrix(int[] indices) {
		_check();
		if (rows() < indices.length || cols() < indices.length)
			throw new MatrixDimensionException();
		for (int i = 0; i < indices.length; i++) {
			if (indices[i] >= rows())
				throw new MatrixDimensionException(
						"Row index > number of rows : (" + indices[i] + " > "
								+ rows() + ")");
		}
		for (int j = 0; j < indices.length; j++) {
			if (indices[j] >= cols())
				throw new MatrixDimensionException(
						"Col index > number of cols : (" + indices[j] + " > "
								+ cols() + ")");
		}
		Matrix _sub = Matrix.getMatrix(indices.length, indices.length);
		for (int i = 0; i < indices.length; i++) {
			for (int j = 0; j < indices.length; j++) {
				_sub.set(m_values[indices[i]][indices[j]], i, j);
			}
		}
		return _sub;
	}

	/**
	 * Returns a matrix in another matrix in other words a submatrix in a larger
	 * matrix. This submatrix is retrievable by this method. The submatrix is a
	 * matrix which size depends on the number of rows and columns included in
	 * the <code>int[] rows, int[] cols</code>. The matrix returned contains the
	 * same elements as the main matrix but only with the specified rows and
	 * columns. The rows array contains the number of the row included in the
	 * submatrix and the cols array the number of the columns included. The
	 * arrangement of the integers will make a difference in the order of the
	 * submatrix arrangement.
	 * 
	 * @param rows
	 *            gives the selections of the rows in the submatrix in a
	 *            specific order.
	 * @param cols
	 *            gives the selection of the columns in the submatrix in a
	 *            specific order
	 * @return a submatrix.
	 */
	public Matrix getSubMatrix(int[] rows, int[] cols) {
		_check();
		if (rows() < rows.length || cols() < cols.length)
			throw new MatrixDimensionException();
		for (int i = 0; i < rows.length; i++) {
			if (rows[i] >= rows())
				throw new MatrixDimensionException(
						"Row index > number of rows : (" + rows[i] + " > "
								+ rows() + ")");
		}
		for (int j = 0; j < cols.length; j++) {
			if (cols[j] >= cols())
				throw new MatrixDimensionException(
						"Col index > number of cols : (" + cols[j] + " > "
								+ cols() + ")");
		}
		Matrix _sub = Matrix.getMatrix(rows.length, cols.length);
		for (int i = 0; i < rows.length; i++) {
			for (int j = 0; j < cols.length; j++) {
				_sub.set(m_values[rows[i]][cols[j]], i, j);
			}
		}
		return _sub;
	}

	/**
	 * Replaces a part of a matrix. The replaced part of the matrix is included
	 * in the matrix <code>mat</code>. The number of indices must be the same as
	 * the dimension of the matrix <code>mat</code>. This matrix is square.
	 * <p>
	 * The indices include all the rows and columns the replaced matrix will be
	 * inserted into. If the row are specified that same column will be
	 * specified to be replaced. This method only replaces the submatrix and do
	 * not add the elements.
	 * 
	 * @param mat
	 *            is the matrix that will replace the elements in that spaces
	 * @param indices
	 *            includes the rows and columns that needs o be replaced
	 * @return a matrix with a part replaced.
	 */
	public void replaceSubmatrix(Matrix mat, int[] indices) {
		_check();
		mat._check();
		if (mat.rows() != indices.length || mat.cols() != indices.length)
			throw new MatrixDimensionException();
		for (int i = 0; i < mat.rows(); i++) {
			for (int j = 0; j < mat.cols(); j++) {
				m_values[indices[i]][indices[j]] = mat.m_values[i][j];
			}
		}
	}

	/**
	 * Replaces a part of a matrix. The replaced part of the matrix are included
	 * in the matrix <code>mat</code>.
	 * <p>
	 * The indices include all the rows and columns the replaced matrix will be
	 * inserted into. The <code>int[] rows</code> refers to the rows in the main
	 * matrix that will be replaced and the <code>cols</cols> to the 
	 * columns. This method only replaces the submatrix and do not 
	 * add the elements.
	 * 
	 * @param mat
	 *            is the matrix that will replace the elements
	 * @param rows
	 *            includes the rows that will be replaced
	 * @param cols
	 *            includes the columns that will be replaced
	 * @return a matrix with a part replaced.
	 */

	public void replaceSubmatrix(Matrix mat, int[] rows, int[] cols) {
		_check();
		mat._check();
		if (mat.rows() != rows.length || mat.cols() != cols.length)
			throw new MatrixDimensionException();
		for (int i = 0; i < mat.rows(); i++) {
			for (int j = 0; j < mat.cols(); j++) {
				m_values[rows[i]][cols[j]] = mat.m_values[i][j];
			}
		}
	}

	/**
	 * Test whether the matrix is symmetric over the diagonal. The returned
	 * value is a boolean which states whether the matrix is symmetrix or not.
	 * If the matrix is not square a matrix dimension exception will be shown
	 * 
	 * @return a boolean that will state if the matrix is symmetric.
	 */
	public boolean isSymmetric() {
		_check();
		if (rows() != cols())
			throw new MatrixDimensionException("Matrix not square!");
		for (int i = 0; i < rows(); i++) {
			for (int j = 0; j < i; j++) {
				if (m_values[i][j] != m_values[j][i])
					return false;
			}
		}
		return true;
	}

	/**
	 * Returns a boolean that states whether the matrix is square. A square
	 * matrix is a matrix which number of rows and columns are equal.
	 * 
	 * @return a boolean which states if the matrix is square.
	 */
	public boolean isSquare() {
		_check();
		if (rows() != cols())
			return false;
		return true;
	}

	/**
	 * Computes the inverse of a matrix. A matrix multiplied by it's inverse
	 * produce the identity matrix. Special requirements:
	 * <ul>
	 * <li>The matrix must be a square matrix
	 * <li>The matrix cannot be singular (determinant equal to zero)
	 * </ul>
	 * The matrix returned will be one of the same size but different elements.
	 * 
	 * @return the inverse of a matrix
	 */
	public Matrix inverse() {
		// by gcvr.
		//
		// SYMBOLS:
		// -------
		//
		// matrix ...... square matrix
		// dimension ...... size (rows/columns) of matrix matrix
		// inverseMatrix ...... inverse of matrix
		//
		// Isym ... flag for symmetry of matrix matrix (1 = symmetric)
		// Iwlpvt.. 0 for no pivoting, 1 for pivoting
		//
		// eps..... tolerance to identify a singular matrix
		// tol..... tolerance for the residuals
		//
		// l ...... lower triangular matrix
		// u ...... upper triangular matrix
		// det .... determinant (det(matrix)=det(l)*det(u))
		// Istop... flag: Istop = 1 if something is wrong
		//
		// pivot .. absolute value of pivot candidates
		// ipv..... location of pivotal element
		// icount . counter for number of row interchanges
		// ---------------------------------------------------------------
		_check();
		if (!isSquare())
			throw new MatrixDimensionException("invalid matrix dimensions");

		Matrix inverse = getMatrix(rows(), cols());

		boolean doPivoting = true;
		int dimension = rows();

		double[][] c = new double[dimension][2 * dimension]; // (500,1000),
		double[][] u = new double[dimension][dimension]; // (500,500)
		double[][] l = new double[dimension][dimension]; // (500,500)

		// c----------
		// c initialize
		// c-----------
		int icount = 0; // ! counts row interchanges
		// c--------
		// c prepare
		// c--------
		int na = dimension - 1;
		int nn = 2 * dimension;
		// c----------------------------------------------
		// c Initialize l and define the extended matrix c
		// c----------------------------------------------
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				l[i][j] = 0.0;
				c[i][j] = m_values[i][j];
			}
			for (int j = 0; j < dimension; j++) {
				c[i][dimension + j] = 0.0;
			}
			c[i][dimension + i] = 1.0;
		}
		// ----------------------
		// begin row reductions
		// ----------------------
		for (int m = 0; m < na; m++) {// outer loop for working row
			int ma = m - 1;
			int m1 = m + 1;
			if (doPivoting) {// Go to 97 to skip pivoting ( ;-) good old basic?)
				// -----------------------------
				// Pivoting:
				// begin by searching column i
				// for largest element
				// -----------------------------
				int ipv = m;
				double pivot = Math.abs(c[m][m]);
				for (int j = m1; j < dimension; j++) {
					if ((Math.abs(c[j][m])) > pivot) {
						ipv = j;
						pivot = Math.abs(c[j][m]);
					}
				}
				if (isZero(pivot))
					throw new MatrixException(
							"Singular matrix cannot be inverted");
				// ---
				// switch the working row with the row containing the
				// pivot element (also switch rows in l)
				// ---
				if (ipv != m) {
					for (int j = m; j < nn; j++) {
						double save = c[m][j];
						c[m][j] = c[ipv][j];
						c[ipv][j] = save;
					}
					for (int j = 0; j < ma; j++) {
						double save = l[m][j];
						l[m][j] = l[ipv][j];
						l[ipv][j] = save;
					}
					icount = icount + 1;
				}
			}
			// ---------------------------------------
			// reduce column i beneath element c(m,m)
			// ---------------------------------------
			for (int i = m1; i < dimension; i++) {
				l[i][m] = c[i][m] / c[m][m];
				c[i][m] = 0.0;
				for (int j = m1; j < nn; j++) {
					c[i][j] = c[i][j] - l[i][m] * c[m][j];
				}
			}
		}
		// --------------------------------
		// check the last diagonal element
		// for singularity
		// -------------------------------
		if (isZero(c[dimension - 1][dimension - 1]))
			throw new MatrixException("Singular matrix cannot be inverted");
		// ----------------------
		// complete the matrix l
		// ----------------------
		for (int i = 0; i < dimension; i++) {
			l[i][i] = 1.0;
		}
		// --------------------
		// define the matrix u
		// --------------------x
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				u[i][j] = c[i][j];
			}
		}
		// ------------------------------------
		// perform back-substitution to solve
		// the reduced system
		// using the upper triangular matrix c
		// ------------------------------------
		for (int ll = 0; ll < dimension; ll++) {
			inverse.m_values[dimension - 1][ll] = c[dimension - 1][dimension
					+ ll]
					/ c[dimension - 1][dimension - 1];

			for (int i = na - 1; i >= 0; i--) {
				double sum = c[i][dimension + ll];
				for (int j = i + 1; j < dimension; j++) {
					sum = sum - c[i][j] * inverse.m_values[j][ll];
				}
				inverse.m_values[i][ll] = sum / c[i][i];
			}
		}
		return inverse;
	}

	/**
	 * Returns the Cholesky factor of a matrix. The Cholesky factor is [L] in
	 * the next statement: [A] = [L][L]T. This yields a lower triangular matrix
	 * with dimensions n x n. The matrix must be positive definite and
	 * symmetrical. Only then can the matrix be computed correctly.
	 * 
	 * @return a lower triangular matrix of dimension n x n.
	 */
	public Matrix cholesky() {
		_check();
		if (!isSymmetric()) {
			throw new RuntimeException("Matrix is not symmetrical");
		}
		Matrix w = Matrix.getMatrix(rows(), cols());
		for (int i = 0; i < rows(); i++) {
			for (int m = 0; m <= i; m++) {
				if (i == m) { // diagonal term
					double sum = 0.0;
					for (int k = 0; k < i; k++) { // dot product
						sum += w.m_values[i][k] * w.m_values[i][k];
					}
					w.m_values[i][i] = Math.sqrt(m_values[i][i] - sum);
				} else {
					double sum = 0.0;
					for (int k = 0; k < i; k++) {
						sum += w.m_values[i][k] * w.m_values[m][k];
					}
					w.m_values[i][m] = (m_values[i][m] - sum)
							/ w.m_values[m][m];
				}
			}
		}
		return w;
	}

	/**
	 * Returns a matrix which is exactly the same in context. The method makes a
	 * duplucate of the original matrix and returns the new matrix.
	 * 
	 * @return a duplicate of the original matrix
	 * 
	 */
	public Matrix clone() {
		Matrix clone = getMatrix(rows(), cols());
		for (int i = 0; i < m_values.length; i++)
			for (int j = 0; j < m_values[i].length; j++)
				clone.m_values[i][j] = m_values[i][j];
		return clone;
	}

	public String toString() {
		_check();
		return super.toString();
	}

	/**
	 * This method sets a name and all the values to a printstream named
	 * <code> stream</code>
	 * 
	 * @param name
	 *            is the name of the matrix
	 * @param stream
	 *            is the stream to be printed in.
	 */
	public void print(String name, PrintStream stream) {
		_check();
		stream.println(name);
		for (int i = 0; i < m_values.length; i++) {
			for (int j = 0; j < m_values[i].length; j++)
				stream.print(m_values[i][j] + "\t");
			stream.println();
		}
		stream.println();
	}
	
	public void print(String name){
		print(name,System.out);
	}

	private String format(double d, NumberFormat f, int length) {
		String s = f.format(d) + " ";
		while (s.length() < length) {
			s = " " + s;
		}
		return s;
	}

	private void print(PrintStream stream, int offset, int length, int num) {
		if (stream == null)
			return;
		for (int i = 0; i < offset; i++)
			stream.print(" ");
		stream.print("+");
		for (int i = 0; i < num; i++) {
			for (int j = 0; j < length; j++)
				stream.print("-");
			stream.print("+");
		}
		stream.print("\n");
	}

	public void printf(String name, PrintStream stream) {
		if (stream == null)
			return;
		_check();
		double max = maxEntry();
		double min = minEntry();
		NumberFormat f;
		if (max > 1e8)
			f = new DecimalFormat("0.000E0");
		else if (max > 1000)
			f = new DecimalFormat("0");
		else if (max > 100)
			f = new DecimalFormat("0.0");
		else if (max > 10)
			f = new DecimalFormat("0.00");
		else if (max > 1)
			f = new DecimalFormat("0.000");
		else if (max > 1e-2)
			f = new DecimalFormat("0.0000");
		else
			f = new DecimalFormat("0.000E0");

		int length = Math.max(f.format(max).length(), f.format(min).length()) + 2;
		print(stream, name.length() + 3, length, cols());
		int counter = 0;
		for (int i = 0; i < m_values.length; i++) {
			if (counter == rows() - 1) {
				stream.print(name + " = |");
			} else {
				for (int k = 0; k < name.length() + 3; k++)
					stream.print(" ");
				stream.print("|");
			}
			counter++;
			for (int j = 0; j < m_values[i].length; j++)
				stream.print(format(m_values[i][j], f, length) + "|");
			stream.println();
			if (counter == rows() - 1) {
				stream.print(name + " = ");
				print(stream, 0, length, cols());
			} else {
				print(stream, name.length() + 3, length, cols());
			}
			counter++;
		}
		stream.println();
	}

	public void printf(String name){
		printf(name,System.out);
	}
	
	// ............Externalizable...........................
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(rows());
		out.writeInt(cols());
		for (int i = 0; i < rows(); i++) {
			for (int j = 0; j < cols(); j++) {
				out.writeDouble(m_values[i][j]);
			}
		}
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		int rows = in.readInt();
		int cols = in.readInt();
		m_values = new double[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				m_values[i][j] = in.readDouble();
			}
		}
	}

	public static void main(String[] args) {

		// Create matrix using String definition
		Matrix m1 = Matrix.getMatrix("{{11, 12, 13, 14},{21, 22, 23, 24}}");
		m1.printf("String defined");
		m1.release();
		// Simple in-line calculating is supported
		Matrix m2 = Matrix.getMatrix("{{1+1, 4-2.5, 3*3, 5./6.}, {1, 2, 3, 4}}");
		m2.printf("String defined with inline calc");
		m2.release();

		// Java array defined
		Matrix marr = Matrix.getMatrix(3, 3);
		double[][] v = { { 25, -5, 10 }, { -5, 17, 10. }, { 10, 10, 62 } };
		marr.set(v);
		marr.printf("Java array defined");
		marr.release();

		// Individual elements defined.
		Matrix a = getMatrix(2, 2);
		a.set(1.0, 0, 0);
		a.set(-1.0, 1, 0);
		a.set(-1.0, 0, 1);
		a.set(1.01, 1, 1);
		a.printf("Individual elements defined");
		
		Matrix ainv = a.inverse();
		ainv.printf("Inverse of above matrix");
		
		Matrix i = a.multiply(ainv);
		i.printf("Should be [I]");
	}
}
