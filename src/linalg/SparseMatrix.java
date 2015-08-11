package linalg;

import serialize.ProfileMatrix;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class SparseMatrix{	
	
	int[] rowIndices;
	int[] colIndices;
	double[] values;
	int counter;
	int rtemp,ctemp;
	double vtemp;
	
	boolean isRowSorted = false;
	boolean isColumnSorted = false;
	
  public SparseMatrix(int size){
  		rowIndices = new int[size+1];
  		colIndices = new int[size+1];
  		values = new double[size+1];
  		counter = 0;
  }

  public int size(){
  	return counter;
  }

  public int rows(){
  	int rows = 0;
  	for(int i = 0; i < counter; i++){
  		if(rowIndices[i]>rows)
  			rows = rowIndices[i];
  	}
  	return rows+1;
  }
  
  public int cols(){
  	int cols = 0;
  	for(int i = 0; i < counter; i++){
  		if(colIndices[i]>cols)
  			cols = colIndices[i];
  	}
  	return cols+1;
  }
  
  public Profile getProfile(){
	  Profile profile = new Profile(rows());
	  for(int i = 0; i < size(); i++){
		  profile.setProfile(rowIndices[i], colIndices[i]);
	  }
	  return profile;
  }
  
  /**
   * Method <code>addValue</code> adds a new matrix entry into the sparse matrix. 
   *
   * @param matrixEntry a <code>Entry</code> value, the matrix entry to be
   * added to the this sparse matrix
   */
  public void addValue(int rowIndex, int colIndex, double value) {
  		if(value == 0) return;
  		if(counter == values.length-1){ // grows the size of this sparse matrix with 25%
  			int _newSize = (int)(counter*1.25);
  			int[] _row = rowIndices;
  			int[] _col = colIndices;
  			double[] _val = values;
  			rowIndices = new int[_newSize];
  			colIndices = new int[_newSize];
  			values = new double[_newSize];
  			System.arraycopy(_row,0,rowIndices,0,_row.length);
  			System.arraycopy(_col,0,colIndices,0,_col.length);
  			System.arraycopy(_val,0,values,0,_val.length);
  		}
  		rowIndices[counter] = rowIndex;
  		colIndices[counter] = colIndex;
  		values[counter] = value;
  		counter++;
  }

  public SparseMatrix multi(SparseMatrix mat2){
  		SparseMatrix resultMat=null;
		if ((cols()) != (mat2.rows()))
			throw new MatrixDimensionException(cols()+" != "+mat2.rows());
		if(!isRowSorted)
			rowSort();
		if(!mat2.isColumnSorted)
			mat2.colSort();
	  	
		resultMat = new SparseMatrix(size()+ mat2.size());
		double sumproduct = 0;
		
		int m1i=0,m2i=0;
		int _row = rowIndices[0];  // current row
		int _col = mat2.colIndices[0];  // current col
		
		int rowStartIndex = 0;
		
		while (m1i < size()) {
			mat2: while (m2i <= mat2.size()) {
				// inner loop that performs the dot product of mat1[i][k] and
				// mat2[k][j]
				sumproduct = 0.;
				while (_row == rowIndices[m1i]
						&& _col == mat2.colIndices[m2i]) {
					if (colIndices[m1i] == mat2.rowIndices[m2i]) {
						sumproduct += values[m1i] * mat2.values[m2i];
						m2i++;
					}
					if (m2i < mat2.size()) {
						if (colIndices[m1i] < mat2.rowIndices[m2i]) {
							m1i++;
						} else {
							m2i++;
						}
					} else {
						resultMat.addValue(_row, _col, sumproduct);
						m2i = 0;
						_col = 0;
						while (rowIndices[m1i] > _row)
							m1i--;
						while (rowIndices[m1i] == _row)
							m1i++;

						_row = rowIndices[m1i];
						rowStartIndex = m1i;
						break mat2;
					}
				}
				while (mat2.colIndices[m2i] > _col)
					m2i--;
				while (mat2.colIndices[m2i] == _col)
					m2i++;

				resultMat.addValue(_row, _col, sumproduct);

				// reasign indices
				_col = mat2.colIndices[m2i];
				m1i = rowStartIndex;
			}
		}
		return resultMat;
	}
  
  public int rowIndex(int index){
	  return rowIndices[index];
  }
  public int colIndex(int index){
	  return colIndices[index];
  }
  public double value(int index){
	  return values[index];
  }
  private void swap(int indexA,int indexB){
  	rtemp = rowIndices[indexA];
  	ctemp = colIndices[indexA];
  	vtemp = values[indexA];
  	
  	rowIndices[indexA] = rowIndices[indexB];
  	colIndices[indexA] = colIndices[indexB];
  	values[indexA] = values[indexB];
  	
  	rowIndices[indexB] = rtemp;
  	colIndices[indexB] = ctemp;
  	values[indexB] = vtemp; 	
  }
  
  // Quick sort algorithm
  
  private int median(int a, int b, int c) {
		if ((a > b && b > c) || (c > b && b > a))
			return b; /* a>b>c or c>b>a */
		if ((b > c && c > a) || (a > c && c > b))
			return c; /* b>c>a or a>c>b */
		return a;
	}

	// Partition element into two halfs, first half < pivot, second half > pivot
	private int partition(int[] a, int p, int r) {
		int x, i, j;
		x = median(a[p], a[r], a[(r + p) / 2]);

		i = p - 1;
		j = r + 1;
		while (true) {
			do {
				--j;
			} while (!(a[j] <= x)); /* find element < pivot */
			do {
				++i;
			} while (!(a[i] >= x)); /* find element > pivot */
			if (i < j) {
				swap(i, j);
			}else
				return j; /* Return dividing point */
		}
	}

	private void quicksort(int[] a, int p, int r) {
		int q;
		if (p < r) {
			q = partition(a, p, r);
			quicksort(a, p, q);
			quicksort(a, q + 1, r);
		}
	}

  
  public void rowSort(){
  	// group rows together...
  	quicksort(rowIndices,0,counter-1);
  	// sort each rows elements by using column indices
  	int rowStart = 0;
  	int rowEnd = 0;
  	
  	while(rowEnd < counter){
	  	while(rowIndices[rowStart]==rowIndices[rowEnd])rowEnd++;
	  	quicksort(colIndices,rowStart,rowEnd-1);
	  	verify(colIndices,rowStart,rowEnd);
	  	rowStart = rowEnd;
  	}
  	isColumnSorted = false;
  	isRowSorted = true;
  }
  

	public void print(String name, PrintStream ps){
		ps.println(name+" = ");
	  	for(int i = 0; i < counter; i++)
	  		ps.println(rowIndices[i]+"\t"+colIndices[i]+"\t"+values[i]);
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
		print(stream,name.length()+3,cols_per_value,cols());
		if(!isRowSorted)
			rowSort();
		int counter = 0;
		int nextValue = 0;
		for(int i = 0; i < rows(); i++){
			if(counter == rows()-1){
				stream.print(name+" = |");
			}			
			else{
				for(int k = 0; k < name.length() + 3; k++)
					stream.print(" ");
				stream.print("|");
			}
			counter++;
			for(int j = 0; j < cols(); j++){
				if(i == rowIndices[nextValue] && j == colIndices[nextValue]){
					stream.print(format(values[nextValue],df,cols_per_value)+"|");
					nextValue++;
				}
				else {
					stream.print(blank(cols_per_value)+"|");
				}
			}
			stream.println();
			if(counter == rows()-1){
				stream.print(name+" = ");
				print(stream,0,cols_per_value,cols());
			}
			else{
				print(stream,name.length()+3,cols_per_value,cols());
			}
			counter++;
		}
		stream.println();
	}
  
  private void verify(int[] v,int s, int e){
  	for(int i = s; i < e-1; i++){
  		if(v[i]==v[i+1])
  			throw new MatrixLockedException("Matrix contains duplicate entries!");
  	}
  }
  
  public void colSort(){
  	// group cols together
  	quicksort(colIndices,0,counter-1);
  	// sort each cols elements by using row indices
  	int colStart = 0;
  	int colEnd = 0;
  	
  	while(colEnd < counter){
	  	while(colIndices[colStart]==colIndices[colEnd])colEnd++;
	  	quicksort(rowIndices,colStart,colEnd-1);
	  	verify(rowIndices,colStart,colEnd);
	  	colStart = colEnd;
  	}
  	isRowSorted = false;
  	isColumnSorted = true;
  }
  
  public String toString(){
  	StringBuilder sb = new StringBuilder();
  	for(int i = 0; i < counter; i++)
  		sb.append(rowIndices[i]+"\t"+colIndices[i]+"\t"+values[i]+"\n");
  	return sb.toString();
  }
  
  /**
   * Method <code>profileMatrix2SparseMatrix</code> creates a sparse matrix
   * from a given profile matrix
   * 
   * @param pMat
   *            a <code>ProfileMatrix</code> object
   * @return a <code>SparseMatrix</code> object
   */
  public static SparseMatrix profileMatrix2SparseMatrix(ProfileMatrix pMat) {
		SparseMatrix smat = new SparseMatrix( pMat.numEntries());
		for (int i = 0; i < pMat.size(); i++) {
			for (int j = pMat.profile(i); j <= i; j++) {
				double value =  pMat.getValue(i,j);
				if(value*value < 1e-20)
					continue;
				smat.addValue(i, j,	value);
				if (j != i) {
					smat.addValue(j, i,	value);
				}
			}
		}
		return smat;
	}



  public ProfileMatrix toProfileMatrix() {
		ProfileMatrix pMat = new ProfileMatrix(getProfile().profile());
		for(int i = 0; i < size(); i++){
			if(colIndices[i] <= rowIndices[i])
			  pMat.setValue(rowIndices[i], colIndices[i], values[i]);
		  }		
		return pMat;
	}


  public SparseMatrix transpose(){
  	int[] tmp = rowIndices;
  	rowIndices = colIndices;
  	colIndices = tmp;
  	
  	boolean flag = isRowSorted;
  	isRowSorted = isColumnSorted;
  	isColumnSorted = flag;
  	
  	return this;
  }

  /**
   * Method <code>matrixVectorMult</code> multiplies a vector with a sparse matrix
   *
   * @param smat a <code>SparseMatrix</code> object
   * @param vector a <code>double[]</code> array
   * @return a <code>double[]</code> array
   */
  public Vector matrixVectorMult(Vector vector)
  {
    if(cols() != vector.size()) 
    	throw new MatrixDimensionException("incorrect dimensions!");
    
    if(!isRowSorted)
    	rowSort();
    
	Vector result = Vector.getVector(rows());
	
	double sumproduct = 0;
	
	int rowStart = 0, rowEnd = 0;
	
	while(rowEnd < size()){
		rowStart = rowEnd;
		while(rowIndices[rowStart] == rowIndices[rowEnd]){
			sumproduct += values[rowEnd]*vector.get(colIndices[rowEnd]);
			rowEnd++;
		}
		result.set(sumproduct, rowIndices[rowStart]);
		sumproduct = 0.0;
	}
	return result;
  }
  
  public SparseMatrix deepClone(){
  	SparseMatrix smat = new SparseMatrix(values.length);
  	System.arraycopy(values,0,smat.values,0,counter);
  	System.arraycopy(rowIndices,0,smat.rowIndices,0,counter);
  	System.arraycopy(colIndices,0,smat.colIndices,0,counter);
  	return smat;
  }
  
  public static ProfileMatrix axbxat2(SparseMatrix smat, ProfileMatrix pmat) {	  
	  SparseVector _rowA = new SparseVector(smat.cols());
	  SparseVector _rowB = new SparseVector(pmat.size());	  
	  SparseVector _AdotB = new SparseVector(pmat.size());	    
	  SparseMatrix _temp = new SparseMatrix(5*pmat.numEntries()/2);	  
	  for(int j = 0; j < smat.cols(); j++){
		  _rowB.set(pmat, j);		  
		  _AdotB.clear();
		  int rowPointer = 0;
		  for(int i = 0; i < smat.rows(); i++){
			  // populate row
			  _rowA.clear();
			  while(smat.rowIndices[rowPointer] == i){
				  _rowA.addValue(smat.colIndices[rowPointer], smat.values[rowPointer]);
				  rowPointer++;
			  }
			  _temp.addValue(i,j,_rowB.dot(_rowA));
		  }	  
	  }
	  smat.transpose();
	  SparseMatrix _r = _temp.multi(smat);
	  _r.printf("result", System.out);
	  return _r.toProfileMatrix();
  }
 
  public static ProfileMatrix axbxat(SparseMatrix a, ProfileMatrix pmat) {	  
	  SparseVector _rowA = new SparseVector(pmat.size());
	  SparseVector _colA = new SparseVector(pmat.size());
	  SparseVector _colB = new SparseVector(pmat.size());	  
	  SparseVector _AdotB = new SparseVector(pmat.size());	    
	  
	  SparseMatrix b = profileMatrix2SparseMatrix(pmat);
	  SparseMatrix _result = new SparseMatrix(pmat.numEntries()*2); 
	  b.colSort();
	  
	 int rowPointer = 0;	 
	 for(int i = 0; i < a.rows(); i++){
		  // populate row
		  _rowA.clear();
		  while(a.rowIndices[rowPointer] == i){
			  _rowA.addValue(a.colIndices[rowPointer], a.values[rowPointer]);
			  rowPointer++;
		  }
		  
		 int colPointer = 0;
		 for(int j = 0; j < a.cols(); j++){
			 // populate col
			 _colB.clear();
			  while(b.colIndices[colPointer] == j){
				  _colB.addValue(b.rowIndices[colPointer], b.values[colPointer]);
				  colPointer++;
			  }		  
			  _AdotB.addValue(j,_rowA.dot(_colB));
		  }
  
		  int _rp2 = 0;
		  for(int k = 0; k < a.rows(); k++){
			  _colA.clear();
			  while(a.rowIndices[_rp2] == k){
				  _colA.addValue(a.colIndices[_rp2], a.values[_rp2]);
				  _rp2++;
			  }
			  _result.addValue(i,k,_AdotB.dot(_colA));
		  }
		  _AdotB.clear();
	  }
	  return _result.toProfileMatrix();
  }
  
  public SparseMatrix axbxat(SparseMatrix a) {
	  a.rowSort();
	  int max = Math.max(a.rows(), a.cols());
		SparseVector _rowA = new SparseVector(max);
		SparseVector _colA = new SparseVector(max);
		SparseVector _colB = new SparseVector(max);
		SparseVector _AdotB = new SparseVector(max);
		SparseMatrix _result = new SparseMatrix(size());
		colSort();

		int _iPointer = 0;
		for (int i = 0; i < a.rows(); i++) {
			// populate row
			_rowA.clear();
			while (a.rowIndices[_iPointer] == i) {
				_rowA.addValue(a.colIndices[_iPointer], a.values[_iPointer]);
				_iPointer++;
			}

			int _jPointer = 0;
			for (int j = 0; j < a.cols(); j++) {
				// populate col
				_colB.clear();
				while (colIndices[_jPointer] == j) {
					_colB.addValue(rowIndices[_jPointer], values[_jPointer]);
					_jPointer++;
				}
				_AdotB.addValue(j, _rowA.dot(_colB));				
			}

			int _kPointer = 0;
			for (int k = 0; k < a.rows(); k++) {
				_colA.clear();
				while (a.rowIndices[_kPointer] == k) {
					_colA.addValue(a.colIndices[_kPointer], a.values[_kPointer]);
					_kPointer++;
				}
				_result.addValue(i, k, _AdotB.dot(_colA));
			}
			_AdotB.clear();
		}
		return _result;
	}
  
}// SparseMatrix
