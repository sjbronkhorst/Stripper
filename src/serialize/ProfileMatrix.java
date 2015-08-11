package serialize;

import java.io.PrintStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import linalg.*;
import linalg.*;

public class ProfileMatrix implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5671174037392584449L;
	
	private Vector[] m_values;

	public ProfileMatrix(int rows){
		this.m_values = new Vector[rows];
	}
	
	public ProfileMatrix(int[] profile) {
		this.m_values = new Vector[profile.length];
		for (int i = 0; i < profile.length; i++) {
			m_values[i] = Vector.getVector(i - profile[i] + 1);
		}
	}

	public int size(){
		return m_values.length;
	}
	
	public int numEntries(){
		int num = 0;
		for (int i = 0; i < size(); i++) {
			num += m_values.length;
		}
		return num;
	}
	
	public ProfileMatrix clone(){
		ProfileMatrix clone = new ProfileMatrix(size());
		for (int i = 0; i < size(); i++) {
			clone.m_values[i] = m_values[i].clone();
		}
		return clone;
	}
	
	/**
	 * @param row
	 * @return the start column of this row
	 */
	public int profile(int row){
		return row - m_values[row].size() + 1;
	}
	
	protected Vector getRow(int index){
		return m_values[index];
	}
	
	protected void setRow(SparseVector sv, int row){
		
	}
	
	protected void setRow(double[] values, int row){
		// get first non-zero entry
		int profile;
		for(profile = 0; profile < values.length; profile++){
			if(values[profile]*values[profile] > 1e-20)
				break;
		}
		m_values[row] = Vector.getVector(row-profile+1);
		for(int i = 0; i < m_values[row].size(); i++){
			m_values[row].set(values[profile+i], i);
		}
	}
	
	public void scale(double scalar){
		for(Vector row : m_values)
			row.scale(scalar);
	}
	
	public double getValue(int row, int col) {
		return m_values[row].get(col - profile(row));
	}

	public void setValue(int row, int col, double value) {
		m_values[row].set(value, col-profile(row));
	}

	public void addValue(int row, int col, double value) {
		m_values[row].add(value, col-profile(row));
	}

	public void addMatrix(Matrix mat, int[] indices) {
		double[][] _mat = mat.get();
		for (int i = 0; i < _mat.length; i++){
			for (int j = 0; j < _mat[i].length; j++){
				if(indices[j] <= indices[i])
					addValue(indices[i], indices[j], _mat[i][j]);
			}
		}
	}

	public void setMatrix(Matrix mat, int[] indices) {
		double[][] _mat = mat.get();
		for (int i = 0; i < _mat.length; i++)
			for (int j = 0; j < _mat[i].length; j++)
				setValue(indices[i], indices[j], _mat[i][j]);
	}
	
	public void add(DiagonalMatrix mat){
		if(size() != mat.size())
			throw new MatrixDimensionException();
		for(int i = 0; i < mat.size(); i++){
			addValue(i, i, mat.get(i));
		}
	}
	
	// calculation stuff...

	public Vector multiply(Vector vec){
		Vector result = Vector.getVector(vec.size());
		for(int i = 0; i < size(); i++){
			double sum = 0.0;
			for(int j = profile(i); j <= i; j++){
				sum += vec.get(j)*getValue(i, j);				
			}
			for(int j = i+1; j < size(); j++){
				if(i >= profile(j))
					sum += vec.get(j)*getValue(j, i);
			}
			result.set(sum, i);
		}
		return result;
	}
		
	public Vector multiply(Vector vec, boolean[] row, boolean[] col){
		Vector result = Vector.getVector(vec.size());
		for(int i = 0; i < size(); i++){
			if(!row[i])
				continue;
			double sum = 0.0;
			for(int j = profile(i); j <= i; j++){
				if(!col[j])
					continue;
				sum += vec.get(j)*getValue(i, j);				
			}
			for(int j = i+1; j < size(); j++){
				if(!col[j])
					continue;
				if(i >= profile(j))
					sum += vec.get(j)*getValue(j, i);
			}
			result.set(sum, i);
		}
		return result;
	}
	
	/** Account for status in multiplication, i.e. ignore rows and columns for
	 * which status = true. 
	 * Definition of status:  true if DOF value is known, false otherwise.
	 */
	public Vector multiply(Vector vec, boolean[] status){		
		Vector result = Vector.getVector(vec.size());
		result.clear(); // important since some rows are not computed
		for(int i = 0; i < size(); i++){
			if(status[i])
				continue;
			double sum = 0.0;
			for(int j = profile(i); j <= i; j++){
				if(status[j])
					continue;
				sum += vec.get(j)*getValue(i, j);				
			}
			for(int j = i+1; j < size(); j++){
				if(status[j])
					continue;
				if(i >= profile(j))
					sum += vec.get(j)*getValue(j, i);
			}
			result.set(sum, i);
		}
		return result;
	}
	
	public double prePostTMultiply(Vector vec){
		if(this.size() != vec.rows()) // Assume profile matrix is square
			throw new VectorDimensionException("Length of vector = " +vec.rows()+ " should be "+size());
		Vector matVec = this.multiply(vec);
		double dot = vec.dot(matVec);
		matVec.release();
		return dot;
	}
	
	public double prePostTMultiply(Vector vec, boolean[] status){
		if(this.size() != vec.rows()) // Assume profile matrix is square
			throw new VectorDimensionException("Length of vector = " +vec.rows()+ " should be "+size());
		Vector matVec = this.multiply(vec, status);
		double dot = 0.0;
		for(int i = 0; i < vec.rows(); i++){
			if(status[i]) continue;
			dot +=  vec.get(i) * matVec.get(i);
		}	
		matVec.release();
		return dot;
	}
	
	public void solvePrimal(Vector primal, Vector dual, Vector system, boolean[] status){
		Vector rhs = Vector.add(dual, system);
		// dual part		
		for (int row = 0; row < size(); row++) {
			if (status[row])
				continue;
			primal.set(rhs.get(row),row);
			for (int column = profile(row); column < row; column++) {
				if (!status[column])
					continue;
				primal.add(-getValue(row, column)* primal.get(column),row);
			}
		}
		rhs.release();
		
		for (int column = 0; column < size(); column++) {
			if (!status[column])
				continue;
			for (int row = profile(column); row < column; row++) {
				if (status[row])
					continue;
				primal.add(-getValue(column, row) * primal.get(column),row);
			}
		}
		
		// forward sweep
		for(int row = 0; row < size(); row++){
			if(status[row])
				continue;
			for(int col = profile(row); col < row; col++){
				if(status[col])
					continue;
				primal.add(-getValue(row, col)*primal.get(col), row);
			}
			primal.set(primal.get(row)/getValue(row, row), row);
		}
		
		// backward sweep
		for(int col = size()-1; col >= 0; col--){
			if(status[col])
				continue;
			primal.set(primal.get(col)/getValue(col, col),col);
			for(int row = profile(col); row < col; row++){
				if(status[row])
					continue;
				primal.add(-getValue(col, row)*primal.get(col), row);
			}
		}
	}
	
	public void solveDual(Vector primal, Vector dual, Vector system, boolean[] status){
		Vector _tmp = system.clone();
		for (int row = 0; row < size(); row++) 
			if(!status[row])
				_tmp.set(0, row);
		dual.subtract(_tmp);
		_tmp.release();
		for (int row = 0; row < size(); row++) {
			if (!status[row])
				continue;
			for (int column = profile(row); column <= row; column++) {
				dual.add(getValue(row, column) * primal.get(column),row);
			}
		}

		for (int column = 0; column < size(); column++) {
			for (int row = profile(column); row < column; row++) {
				if (!status[row])
					continue;
				dual.add(getValue(column, row) * primal.get(column),row);
			}
		}	
	}
	
	public Vector solve(Vector rhs){
		Vector vec = Vector.getVector(rhs.size());

		// forward sweep
		for(int row = 0; row < size(); row++){
			vec.set(rhs.get(row),row);
			for(int col = profile(row); col < row; col++){
				vec.add(-getValue(row, col)*vec.get(col), row);
			}
			vec.set(vec.get(row)/getValue(row, row), row);
		}
		
		// backward sweep
		for(int col = size()-1; col >= 0; col--){
			vec.set(vec.get(col)/getValue(col, col),col);
			for(int row = profile(col); row < col; row++){
				vec.add(-getValue(col, row)*vec.get(col), row);
			}
		}		
		return vec;		
	}
	
	public Vector solve(Vector rhs, boolean[] status){
		Vector res = Vector.getVector(rhs.size());
		// forward sweep
		for(int row = 0; row < size(); row++){
			res.set(rhs.get(row),row);
			if(status[row])
				continue;
			for(int col = profile(row); col < row; col++){
				if(status[col])
					continue;
				res.add(-getValue(row, col)*res.get(col), row);
			}
			res.set(res.get(row)/getValue(row, row), row);
		}
		
		// backward sweep
		for(int col = size()-1; col >= 0; col--){
			if(status[col])
				continue;
			res.set(res.get(col)/getValue(col, col),col);
			for(int row = profile(col); row < col; row++){
				if(status[row])
					continue;
				res.add(-getValue(col, row)*res.get(col), row);
			}
		}		
		return res;
	}

	public SparseMatrix toSparseMatrix() {
		SparseMatrix smat = new SparseMatrix(numEntries());
		for (int i = 0; i < size(); i++) {
			for (int j = profile(i); j <= i; j++) {
				double value = getValue(i, j);
				if (value * value < 1e-20)
					continue;
				smat.addValue(i, j, value);
				if (j != i) {
					smat.addValue(j, i, value);
				}
			}
		}
		return smat;
	}
	
	public void print(String name, PrintStream ps){
		ps.println(name+" = ");
		for(int i = 0; i < m_values.length; i++){
			m_values[i].print(i+"\t", ps);
		}
	}
	
	// formatting
	private int cols_per_value = 10;
	private DecimalFormat df = new DecimalFormat("0.0000");
	private String blank(int length){
		String s = " ";
		while(s.length() < length){
				s += " ";
		}
		return s;
	}
	
	private String format(double d, NumberFormat f, int length){
		String s = f.format(d)+" ";
		while(s.length() < length){
				s = " "+s;
		}
		return s;
	}	
	
	private void print(PrintStream stream, int offset, int length, int num){
		for(int i = 0; i < offset; i++)
			stream.print(" ");
		stream.print("+");
		for(int i = 0; i < num; i++){
			for(int j = 0; j < length; j++)
				stream.print("-");
			stream.print("+");
		}
		stream.print("\n");
	}
	
	public void printf(String name, PrintStream stream){
		print(stream,name.length()+3,cols_per_value,size());
		int counter = 0;

		for(int i = 0; i < size(); i++){
			if(counter == size()-1){
				stream.print(name+" = |");
			}			
			else{
				for(int k = 0; k < name.length() + 3; k++)
					stream.print(" ");
				stream.print("|");
			}
			counter++;
			for(int j = 0; j < size();j++){
				if(profile(i) <= j && j <= i)
					stream.print(format(getValue(i, j),df,cols_per_value)+"|");
				else
					stream.print(blank(cols_per_value)+"|");
			}			
			stream.println();
			if(counter == size()-1){
				stream.print(name+" = ");
				print(stream,0,cols_per_value,size());
			}
			else{
				print(stream,name.length()+3,cols_per_value,size());
			}
			counter++;
		}
		stream.println();
	}
	
	public ProfileMatrix release(){
		for(int i = 0; i < m_values.length; i++){
			m_values[i].release();
			m_values[i] = null;
		}
		return null;
	}
	
	public static void main(String[] args) {
		ProfileMatrix npm = new ProfileMatrix(new int[]{0,0,1,1,2});
		
		Matrix sm1 = Matrix.getMatrix(2, 2);
		sm1.set(1, 0, 0);
		sm1.set(2, 1, 0);
		sm1.set(3, 0, 1);
		sm1.set(4, 1, 1);
		
		Vector.stats(System.out);
		
//		sm1.print("sm1", System.out);
		
//		npm.printf("NPM", System.out);
		npm.addMatrix(sm1, new int[]{0,1});
		npm.addMatrix(sm1, new int[]{1,3});
		npm.addMatrix(sm1, new int[]{2,4});
//		npm.printf("NPM - 2", System.out);
		
		
		
//		SparseMatrix sm = npm.toSparseMatrix();
//		sm.printf("Mat", System.out);
		
		npm.printf("Mat", System.out);
		
		Vector v = Vector.getVector(5);
		v.ones();
		v.scale(2);

		v.printf("vec", System.out);
		
		Vector v2 = npm.multiply(v);		
		v2.printf("Mat*vec", System.out);

//		boolean[] status = {true, true, true, true, true};
//		boolean[] status = {false, false, false, false, false};
//		boolean[] status = {false, false, false, false, true};
		boolean[] status = {false, false, true, false, false};
		Vector v3 = npm.multiply(v, status);		
		v3.printf("Mat*vec with status", System.out);
		
		double ppm = npm.prePostTMultiply(v);
		System.out.println('\n'+"vecT * Mat * vec = "+ppm+'\n');

		double ppm2 = npm.prePostTMultiply(v,status);
		System.out.println('\n'+"vecT * Mat * vec with status = "+ppm2+'\n');

		npm.release();
		Vector.stats(System.out);
	}	
}
