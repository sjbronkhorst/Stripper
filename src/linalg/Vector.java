package linalg;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;


public class Vector implements Externalizable {

	private static final long serialVersionUID = 1L;
	private static Set<Vector> s_vectorSet = new HashSet<Vector>();
	private static Map<String, Set<Vector>> s_vectorMap = new HashMap<String, Set<Vector>>();
	private static Map<String, Integer> s_couterMap = new HashMap<String, Integer>();

	private static boolean verbose = System.getProperties().containsKey(
			"vectorInfo");

	private static String getKey(int rows) {
		return "[" + rows + "]";
	}

	/**
	 * Returns a column vector with specified number of rows.
	 * 
	 * @param rows
	 *            the number of rows available in the vector.
	 * @return a vector with specified length.
	 */
	public synchronized static Vector getVector(int rows) {
		String key = getKey(rows);
		if (!s_vectorMap.containsKey(key)) {
			s_vectorMap.put(key, new HashSet<Vector>());
			s_couterMap.put(key, 0);
		}
		Set<Vector> set = s_vectorMap.get(key);
		if (set.isEmpty()) {
			s_couterMap.put(key, s_couterMap.get(key) + 1);
			Vector vec = new Vector(rows);
			vec.m_standalone = false;
			if (verbose)
				System.err.println("Vector [" + rows + "] created!");
			s_vectorSet.add(vec);
			return vec;
		} else {
			Iterator<Vector> iter = set.iterator();
			Vector vec = iter.next();
			iter.remove();
			vec.locked = false;
			vec.m_standalone = false;
			return vec;
		}
	}

	/**
	 * Returns a column vector with specified number of rows.
	 * 
	 * @param rows
	 *            the number of rows available in the vector.
	 * @return a vector with specified length.
	 */
	public synchronized static Vector getVector(int rows, boolean standalone) {
		if (standalone)
			return new Vector(rows);
		else
			return getVector(rows);
	}

	/**
	 * Prints the statistics of the vectors found. The stats include the number
	 * of vectors found and the status of those vectors. The availability of the
	 * vectors is shown and as a percentage of the total number of the vectors.
	 * The size of the vectors is printed.
	 * 
	 * @param stream
	 *            is the stream to be printed in
	 */
	public static void stats(PrintStream stream) {
		Iterator<Map.Entry<String, Set<Vector>>> iter = s_vectorMap.entrySet()
				.iterator();
		stream.println("\nVector service stats...");
		int available = 0;
		while (iter.hasNext()) {
			Map.Entry<String, Set<Vector>> entry = iter.next();
			int _total = s_couterMap.get(entry.getKey());
			stream.println(entry.getKey() + "\t(" + entry.getValue().size()
					+ " of " + _total + ") available");
			available += entry.getValue().size();
		}
		stream.println("Total number of vectors = " + s_vectorSet.size()
				+ "\t[" + Math.round(available * 100. / s_vectorSet.size())
				+ "% available]\n");
	}

	private static final String VALUE = "\\-?\\d*\\.?\\d";
	private static final String OPR = "\\s*[\\+|\\*|\\-|\\/]\\s*";
	private static final String EXPR = "[(|" + VALUE + "]+[[(" + VALUE + OPR+ "]|" + OPR + VALUE + "|)]*";
	private static final String VEC_STRING = "\\{[" + EXPR + "|" + VALUE+ "][\\s*,\\s*[" + EXPR + "|" + VALUE + "]]*\\}";

	
	/**
	 * Returns a column vector formed by interpreting the given values String.
	 * The values String has the same format as the one used to define Java
	 * arrays: for example "{2.5, 3.7, 2, 9}" will create a vector with the
	 * 4 elements given. The String may also contain simple expressions,
	 * for example: "{2+3, 4-7, 3.5*6.2, 9.1/4}".
	 * 
	 * @param values
	 *            the String that defines the vector's elements.
	 * @param standalone
	 *            boolean indicating if the vector may later be released
	 *            for re-use (false). If true the vector will exist while
	 *            the application executes.
	 * @return a vector with the contents formed by the given values String.
	 */
	public static Vector getVector(String values, boolean standalone) {
		if (values.matches(VEC_STRING)) {
			StringTokenizer st = new StringTokenizer(values.substring(1,
					values.length() - 1), ",");
			Vector vec = getVector(st.countTokens());
			for (int i = 0; i < vec.size(); i++) {
				String val = st.nextToken().trim().replace(" ", "");
				if (val.matches(VALUE))
					vec.m_values[i] = Double.parseDouble(val);
				else if (val.matches(EXPR)) {
					try {
						vec.m_values[i] = InlineCalculator.calc("=" + val);
					} catch (InvalidParameterException e) {
						throw new VectorDimensionException(
								"Invalid vector format : " + val);
					}
				}
			}
			return vec;
		} else
			throw new VectorDimensionException("Invalid vector format : "
					+ values);
	}
	
	/**
	 * Returns a column vector formed by interpreting the given values String.
	 * The values String has the same format as the one used to define Java
	 * arrays: for example "{2.5, 3.7, 2, 9}" will create a vector with the
	 * 4 elements given. The String may also contain simple expressions,
	 * for example: "{2+3, 4-7, 3.5*6.2, 9.1/4}".
	 * 
	 * @param values
	 *            the String that defines the vector's elements.
	 * @return a vector with the contents formed by the given values String.
	 */
	public static Vector getVector(String values) {
		return getVector(values, false);
	}

	// end of static stuff.

	private double[] m_values;
	private boolean locked = false;
	private boolean m_standalone = false;

	public Vector() {
	}

	private Vector(int rows) {
		m_values = new double[rows];
		m_standalone = true;
	}

	/**
	 * Returns the size of a vector. This size is calculated as the number of
	 * elements in a certain vector.
	 * 
	 * @return the number of rows in a vector.
	 */
	public int size() {
		return m_values.length;
	}

	protected void _check() {
		if (locked)
			throw new VectorLockedException();
	}

	/**
	 * Returns the maximum value in a vector. This method searches for the
	 * algabraic largest number in a vector and returns it as a
	 * <code>double</code> value.
	 * 
	 * @return the largest number found in a vector.
	 */
	public double maxEntry() {
		double max = m_values[0];
		for (int i = 1; i < m_values.length; i++)
			max = (max < m_values[i]) ? m_values[i] : max;
		return max;
	}

	/**
	 * Returns the minimum value in a vector. This method searches for the
	 * algabraic smallest number in a vector and returns it as a
	 * <code>double</code> value.
	 * 
	 * @return the smallest number found in a vector.
	 */
	public double minEntry() {
		double min = m_values[0];
		for (int i = 1; i < m_values.length; i++)
			min = (min > m_values[i]) ? m_values[i] : min;
		return min;
	}

	/**
	 * Adds a value to an element in the vector. The index of the position of
	 * the element is the <code>int row<code>. This index starts at zero.
	 * 
	 * @param value
	 *            - The value to be added to the element
	 * @param row
	 *            - The index the element will be added to
	 */
	public void add(double value, int row) {
		_check();
		m_values[row] += value;
	}

	/**
	 * Sets a value in a certain row. This positional integer <code>row</code>
	 * is a index of which row will be chosen to insert the value. This index
	 * may not exceed the number of rows in the vector. The double
	 * <code>value</code> will be inserted and replaces the currrent value in
	 * the same position.
	 * 
	 * @param value
	 *            is the value to be inserted in a vector
	 * @param row
	 *            is the position where the value will be set.
	 */
	public void set(double value, int row) {
		_check();
		m_values[row] = value;
	}

	/**
	 * Sets an array to a vector. This method will set the elements in an array
	 * to a vector with the same order as in the array.
	 * 
	 * @param values
	 *            is an array of all the elements to be set to a vector.
	 */
	public void set(double[] values) {
		_check();
		if (values.length != rows())
			throw new VectorDimensionException();
		System.arraycopy(values, 0, m_values, 0, rows());
	}

	/**
	 * Adds two vectors and returns the result. The vectors <code>v1, v2</code>
	 * must have the same number of elements.
	 * 
	 * @param v1
	 *            - a vector to be added.
	 * @param v2
	 *            - a vector to be added.
	 * @return a vector which is the sum of the vectors <code>v1</code> and
	 *         <code>v2</code>
	 */

	public static Vector add(Vector v1, Vector v2) {
		v1._check();
		v2._check();
		Vector sum = v1.clone();
		sum.add(v2);
		return sum;
	}

	/**
	 * Returns a vector which is the difference between two vectors. This method
	 * calculate the difference between corresponding elements and sets it to a
	 * new vector. The product will be <code> v1 - v2</code>.
	 * 
	 * @param v1
	 *            is the first vector in the subtraction
	 * @param v2
	 *            is the second vector in the subtraction
	 * @return a vector which is the difference between the two.
	 */
	public static Vector subtract(Vector v1, Vector v2) {
		v1._check();
		v2._check();
		Vector sum = v1.clone();
		sum.subtract(v2);
		return sum;
	}

	/**
	 * Returns the element found in a specific row. This is a double value and
	 * is returned if it is found. If the row index exceeds that of the given
	 * vector VectorDimensoinExcepsion will occur.
	 * 
	 * @param row
	 *            the index of the row to be searched for.
	 * @return the value found in a specific row.
	 */

	public double get(int row) {
		_check();
		if (row >= rows())
			throw new VectorDimensionException("invalid index " + row);
		return m_values[row];
	}

	/**
	 * Returns the number of rows in a vector. This method returns a integer and
	 * it's size depends on the given size when it was created.
	 * 
	 * @return the number of rows.
	 */
	public int rows() {
		_check();
		return m_values.length;
	}

	/**
	 * Clones the contents of this vector, expensive to call. Method sets all
	 * the values of the vector to an array. The elemnts are in the same order
	 * but in array form.
	 * 
	 * @return an array of the same elements in the vector
	 */
	public double[] get() {
		_check();
		return m_values;// .clone();
	}

	/**
	 * Releases the vector's memory usage for re-use by a new vector. 
	 * After releasing a vector no attempt should be made to use it.
	 * 
	 * @return a null vector.
	 */
	public Vector release() {
		if (m_standalone)
			throw new VectorLockedException(
					"May not release a standalone Vector");
		clear();
		locked = true;
		s_vectorMap.get(getKey(m_values.length)).add(this);
		return null;
	}

	/**
	 * Sets all the elements in the vector equal to zero. This method is void
	 * and does not set the vector to null.
	 */
	public void clear() {
		_check();
		for (int i = 0; i < m_values.length; i++)
			m_values[i] = 0.0;
	}

	/**
	 * Returns the dot-product of two vectors.
	 * 
	 * @param vec
	 *            the vector to be dotted with
	 * @return the dot product of two vectors
	 */
	public double dot(Vector vec) {
		_check();
		vec._check();
		double dot = 0.0;
		for (int i = 0; i < rows(); i++) {
			dot += m_values[i] * vec.m_values[i];
		}
		return dot;
	}

	/**
	 * Sets the product of two vectors to the first. The two vectors must have
	 * the same number of elements. The first vector is multiplied by the second
	 * or mathematically more correctly, each element is multiplied by the
	 * element of the other vector in the same index.
	 * 
	 * @param vec
	 *            the vector wanted to multiply by.
	 */
	public void multiply(Vector vec) {
		_check();
		vec._check();
		if (rows() != vec.rows())
			throw new VectorDimensionException();
		for (int i = 0; i < rows(); i++) {
			m_values[i] *= vec.m_values[i];
		}
	}

	/**
	 * Returns the sum of the elements of a vector. This method adds all the
	 * elements toghether and returns the sum as a <code>double</code>.
	 * 
	 * @return the sum of the elements.
	 */
	public double sum() {
		_check();
		double sum = 0.0;
		for (int i = 0; i < m_values.length; i++)
			sum += m_values[i];
		return sum;
	}

	/**
	 * Scales a vector by a given value. The new values are set to the vector
	 * and The double <code>value</code> are multiplied by all the elements.
	 * 
	 * @param value
	 *            the value o be scaled with.
	 */
	public void scale(double value) {
		_check();
		for (int i = 0; i < m_values.length; i++)
			m_values[i] *= value;
	}

	/**
	 * Returns the absolute value of the vector. This absolute value is the
	 * length represented by the vector. <code>|vec|</code> = sum of the square
	 * of the individual elements.
	 * 
	 * @return the absolute value of the vector
	 */
	public double abs() {
		_check();
		double abs = 0.0;
		for (int i = 0; i < rows(); i++)
			abs += m_values[i] * m_values[i];
		return Math.sqrt(abs);
	}

	/**
	 * This method normalize a vector and sets all the new elements to vector.
	 * The operation caluculates the absolute value of the vector and devides by
	 * it. Thus if the vectors size equals to zero an error message will be
	 * shown.
	 */
	public void normalize() {
		_check();
		double abs = abs();
		for (int i = 0; i < rows(); i++)
			m_values[i] /= abs;
	}

	/**
	 * Sets all the values in the vetor to 1.0. This method is void and does not
	 * return a vector with ones in.
	 */
	public void ones() {
		_check();
		for (int i = 0; i < rows(); i++)
			m_values[i] = 1.0;
	}

	/**
	 * Returns the product of two vectors. This method multiplies a column
	 * vector with a row vector. This produces a matrix of size
	 * <code>row x row</code>. The number of rows must be equal to the number of
	 * rows in the other vector.
	 * 
	 * @param vec
	 *            the vector that will be multiplied by.
	 * @return a matrix which is the prodct of two vectors.
	 */
	public Matrix product(Vector vec) {
		_check();
		vec._check();
		if (rows() != vec.rows())
			throw new VectorDimensionException();
		Matrix mat = Matrix.getMatrix(rows(), rows());
		for (int i = 0; i < rows(); i++) {
			for (int j = 0; j < rows(); j++) {
				mat.set(m_values[i] * vec.m_values[j], i, j);
			}
		}
		return mat;
	}

	/**
	 * Returns a vector which is the cross product of two vectors. This linear
	 * algebraic operation known as the cross product is a vector which size is
	 * equal to the area included in the parallelogram, represented by the tw0
	 * vectors. Its direction is perpendicular to the plane represented by the
	 * two vectors.
	 * <p>
	 * The vectors must both have a length of 3. In other words the vector must
	 * be 3Dimensional. Note that the given vector will be the first in the
	 * product and the vector <code>vec</code> will be second. This will lead to
	 * the expresion {given} x {<code>vec</code> .
	 * 
	 * @param vec
	 *            the vector to be crossed with
	 * @return a vector which is the cross product of two vectors
	 */
	public Vector cross(Vector vec) {
		_check();
		if (vec.rows() != 3 || rows() != 3)
			throw new VectorDimensionException();
		Vector cross = getVector(3);
		cross.set(
				m_values[1] * vec.m_values[2] - m_values[2] * vec.m_values[1],
				0);
		cross.set(-m_values[0] * vec.m_values[2] + m_values[2]
				* vec.m_values[0], 1);
		cross.set(
				m_values[0] * vec.m_values[1] - m_values[1] * vec.m_values[0],
				2);
		return cross;
	}

	/**
	 * Adds a vector to the given vector. The number of the rows must be equal.
	 * This method is void and will not return a value, but will set the sum to
	 * the given vector.
	 * 
	 * @param vector
	 *            - The vector to be added to the given vector.
	 */
	public void add(Vector vector) {
		_check();
		vector._check();
		if (rows() != vector.rows())
			throw new VectorDimensionException();

		for (int i = 0; i < m_values.length; i++)
			m_values[i] += vector.m_values[i];
	}

	/**
	 * Adds two vectors and sets it to the given one. The length of the array
	 * <code>indices</code> must be the same as the vectors number of rows.
	 * <p>
	 * The given vectors number of rows do not have to be equal to the vector
	 * <code>vector</code>'s. The <code>indices</code> contains the indexes of
	 * the where the vector's elements must be added.
	 * 
	 * @param vector
	 *            - a vector of size equal or smaller than the given one's size
	 * @param indices
	 *            - the indexes of the vector's elements.
	 */
	public void add(Vector vector, int[] indices) {
		_check();
		vector._check();
		if (indices.length != vector.rows())
			throw new VectorDimensionException();
		for (int i = 0; i < vector.rows(); i++)
			m_values[indices[i]] += vector.m_values[i];
	}

	/**
	 * Returns a subvector found in the larger vector. The number of elements in
	 * <code>indices</code> must smaller than that of the vector. The
	 * <code>indices</code> contains all the rows the subvector will be found
	 * in. It can be in any order but cannot equal that of the vector.
	 * 
	 * @param indices
	 *            the row indexes of the subvector.
	 * @return a subvector
	 */
	public Vector getSubVector(int[] indices) {
		_check();
		if (rows() < indices.length)
			throw new MatrixDimensionException();
		for (int i = 0; i < indices.length; i++) {
			if (indices[i] >= rows())
				throw new MatrixDimensionException(
						"Row index > number of rows : (" + indices[i] + " > "
								+ rows() + ")");
		}
		Vector _sub = Vector.getVector(indices.length);
		for (int i = 0; i < indices.length; i++) {
			_sub.set(m_values[indices[i]], i);
		}
		return _sub;
	}

	/**
	 * Calculates the difference between two vectors and sets the result in the
	 * first. The number of rows of the two vectors must be equal.
	 * 
	 * @param vector
	 *            is the vector that will be subtracted.
	 */
	public void subtract(Vector vector) {
		_check();
		vector._check();
		if (rows() != vector.rows())
			throw new VectorDimensionException();

		for (int i = 0; i < m_values.length; i++)
			m_values[i] -= vector.m_values[i];
	}

	/**
	 * Replaces a vector by substituting other elements in certain positions.
	 * The <code>indices</code> contains the indexes of the elements of the
	 * vector <code>vector</code> to be copied. The method sets the new values
	 * to the given vector by replacing the current values.
	 * 
	 * @param vector
	 *            - the vector with the replacement values.
	 * @param indices
	 *            - the positions where the replacement is necessary
	 */
	public void replace(Vector vector, boolean[] indices) {
		_check();
		vector._check();
		if (rows() != vector.rows() || indices.length != rows())
			throw new VectorDimensionException();

		for (int i = 0; i < m_values.length; i++)
			if (indices[i])
				m_values[i] = vector.m_values[i];
	}

	/**
	 * Returns a duplicate of this vector. This method makes a "copy" of the
	 * vector and returns another vector with another place in the memory, but
	 * with all the same elements.
	 * 
	 * @return a vector equal to the given vector
	 */
	public Vector clone() {
		_check();
		Vector clone = getVector(size());
		for (int i = 0; i < m_values.length; i++)
			clone.m_values[i] = m_values[i];
		return clone;
	}

	/**
	 * Returns a duplicate of this vector. This method makes a "copy" of the
	 * vector and returns another vector with another place in the memory, but
	 * with all the same elements.
	 * 
	 * @return a vector equal to the given vector
	 */
	public Vector clone(boolean standalone) {
		Vector clone = getVector(size(), standalone);
		for (int i = 0; i < m_values.length; i++)
			clone.m_values[i] = m_values[i];
		return clone;
	}

	public String toString() {
		_check();
		return super.toString();
	}

	/**
	 * Prints all elements found in a vector in a column.
	 * 
	 * @param name
	 *            a string which will be shown in front of the vector.
	 * @param stream
	 *            is the stream printed into
	 */
	public void print(String name, PrintStream stream) {
		_check();
		stream.print(name + "\t = [");
		for (int i = 0; i < m_values.length; i++) {
			stream.print(m_values[i]);
			if (i < m_values.length - 1)
				stream.print(", ");
		}
		stream.println("]");
	}

	public void print(String name) {
		print(name, System.out);
	}

	private String format(double d, NumberFormat f, int length) {
		String s = f.format(d) + " ";
		while (s.length() < length) {
			s = " " + s;
		}
		return s;
	}

	private void print(PrintStream stream, int offset, int length) {
		if (stream == null)
			return;
		for (int i = 0; i < offset; i++)
			stream.print(" ");
		stream.print("+");
		for (int j = 0; j < length; j++)
			stream.print("-");
		stream.print("+");
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
		else if (max > 1e-4)
			f = new DecimalFormat("0.0000");
		else
			f = new DecimalFormat("0.000E0");

		int length = Math.max(f.format(max).length(), f.format(min).length()) + 2;

		print(stream, name.length() + 3, length);
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
			stream.print(format(m_values[i], f, length) + "|");
			stream.println();
			if (counter == rows() - 1) {
				stream.print(name + " = ");
				print(stream, 0, length);
			} else {
				print(stream, name.length() + 3, length);
			}
			counter++;
		}
		stream.println();
	}

	public void printf(String name) {
		printf(name, System.out);
	}

	public static void print(String name, boolean[] fix, PrintStream ps) {
		ps.print(name + " [");
		for (int i = 0; i < fix.length; i++) {
			ps.print(fix[i]);
			if (i < fix.length - 1)
				ps.print(", ");
		}
		ps.println("]");
	}

	public static void print(String name, int[] array, PrintStream ps) {
		ps.print(name + " [");
		for (int i = 0; i < array.length; i++) {
			ps.print(array[i]);
			if (i < array.length - 1)
				ps.print(", ");
		}
		ps.println("]");
	}

	// ............Externalizable...........................
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(rows());
		for (int i = 0; i < rows(); i++) {
			out.writeDouble(m_values[i]);
		}
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		int rows = in.readInt();
		m_values = new double[rows];
		m_standalone = true;
		locked = false;
		for (int i = 0; i < rows; i++) {
			m_values[i] = in.readDouble();
		}
	}

	public static void main(String[] args) {
		// Create vector using String definition
		Vector s1 = Vector.getVector("{1, 2, 3, 4}");
		s1.printf("String defined");
		s1.release();
		// Simple in-line calculating is supported
		Vector s2 = Vector.getVector("{1+1, 4-2.5, 3*3, 5./6.}");
		s2.printf("String defined with inline calc");
		s2.release();

		// Create vector using Java array
		Vector varr = Vector.getVector(3);
		double[] vecArr = { 1, 2, 3 };
		varr.set(vecArr);
		varr.printf("Java array defined");

		// Create vector by individual element definition
		Vector v = Vector.getVector(3);
		v.set(2, 0);
		v.set(4, 1);
		v.set(6, 2);
		v.printf("Individual elements defined");

		// Use a vector object method, e.g. dot-product
		double varrdotv = varr.dot(v);
		System.out.println("varr dot v = " + varrdotv);
	}

}
