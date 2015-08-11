package linalg;

import serialize.ProfileMatrix;

public class Cholesky {

	public static void decompose(ProfileMatrix mat){
		for(int row = 0; row < mat.size(); row++){
			for(int col = mat.profile(row); col <= row; col++){
				if(row == col){
					double[] _row = mat.getRow(row).get();
					double sum = 0.0;
					for(int k = 0; k < row-mat.profile(row); k++)
						sum += _row[k]*_row[k];
					mat.setValue(row, row, Math.sqrt(mat.getValue(row, row) - sum));					
				}
				else {
					double[] _row = mat.getRow(row).get();
					double[] _col = mat.getRow(col).get();
					double sum = 0.0;
					int _r = mat.profile(row);
					int _c = mat.profile(col);
					for(int k = (_r<_c)?_c:_r; k < col; k++)
						sum += _row[k-_r]*_col[k-_c];	
					mat.setValue(row, col, (mat.getValue(row, col) - sum) / mat.getValue(col, col));					
				}				
			}			
		}		
	}
	
	public static void decompose(ProfileMatrix mat, boolean[] status){
		for(int row = 0; row < mat.size(); row++){
			if(status[row])
				continue;
			int _r = mat.profile(row);
			for(int col = _r; col <= row; col++){
				if(status[col])
					continue;								
				double[] _row = mat.getRow(row).get();
				double[] _col = mat.getRow(col).get();				
				int _c = mat.profile(col);				
				int start = (_r < _c)? _c:_r;				
				if(row == col){
					double sum = mat.getValue(row, row);
					for(int k = 0; k < row-_r; k++){
						if(status[k+_r])
							continue;							
						sum -= _row[k]*_row[k];
					}
					mat.setValue(row, row, Math.sqrt(sum));
				}
				else {
					double sum = mat.getValue(row, col);	
					for(int k = start; k < col; k++){
						if(status[k])
							continue;
						sum -= _row[k-_r]*_col[k-_c];	
					}
					mat.setValue(row, col,  sum / mat.getValue(col, col));					
				}
			}			
		}		
	}
	
	public static void main(String[] args) {
		if(false){
			ProfileMatrix npm = new ProfileMatrix(new int[] {0,0,0});
			npm.setValue(0, 0, 25);
			npm.setValue(1, 0, -5);
			npm.setValue(1, 1, 17);
			npm.setValue(2, 0, 10);
			npm.setValue(2, 1, 10);
			npm.setValue(2, 2, 62);			
			npm.print("Mat", System.out);			
			decompose(npm);			
			npm.print("Cholesky Mat", System.out);
		}
		if(true){
			ProfileMatrix npm = new ProfileMatrix(new int[] {0,0,0,0,1,2});
			npm.addValue(0, 0, 10);
			npm.addValue(1, 1, 20);
			npm.addValue(2, 2, 15);
			npm.addValue(3, 3, 5);
			npm.addValue(4, 4, 10);
			npm.addValue(5, 5, 10);
			
			npm.addValue(1, 0, -5);
			npm.addValue(2, 1, -5);
			npm.addValue(4, 3, -2.5);
			npm.addValue(5, 4, -2.5);
			
			npm.addValue(3, 0, -2.5);
			npm.addValue(4, 1, -5);
			npm.addValue(5, 2, -5);
			
//			npm.printf("example 2", System.out);
			
			decompose(npm);	
			npm.printf("example 2 - decompose ", System.out);
			
//			Vector rhs = Vector.getVector(6);
//			rhs.set(new double[]{25,60,80,0,0,50});
//			rhs.printf("rhs", System.out);
//			
//			Vector _v = npm.solve(rhs);
//			
//			_v.printf("Solution", System.out);
		}
		if(true){
			ProfileMatrix npm = new ProfileMatrix(new int[] {0,0,1,0,1,2,5,3,4,5,6});
			npm.addValue(0,0,5.0);
			npm.addValue(1,0,-2.5);
			npm.addValue(1,1,10.0);
			npm.addValue(2,1,-2.5);
			npm.addValue(2,2,5.0);
			npm.addValue(3,0,-2.5);
			npm.addValue(3,3,10.0);
			npm.addValue(4,1,-5.0);
			npm.addValue(4,3,-5.0);
			npm.addValue(4,4,20);
			npm.addValue(5,2,-2.5);
			npm.addValue(5,4,-5.0);
			npm.addValue(5,5,15.0);
			npm.addValue(6,5,-2.5);
			npm.addValue(6,6,5.0);
			npm.addValue(7,3,-2.5);
			npm.addValue(7,7,5.0);
			npm.addValue(8,4,-5.0);
			npm.addValue(8,7,-2.5);
			npm.addValue(8,8,10.0);
			npm.addValue(9,5,-5.0);
			npm.addValue(9,8,-2.5);
			npm.addValue(9,9,10.0);
			npm.addValue(10,6,-2.5);
			npm.addValue(10,9,-2.5);
			npm.addValue(10,10,5.0);
			
			
			boolean[] status = new boolean[]{true,true,true,false,false,false,true,false,false,false,true};
			
//			npm.printf("example 3", System.out);
			
			decompose(npm, status);	
			npm.printf("example 3 - decompose ", System.out);
			
			Vector rhs = Vector.getVector(11);
			rhs.set(new double[]{10,10,10,25,60,80,20,0,0,50,20});
//			rhs.printf("rhs", System.out);
			
//			Vector _v = npm.solve(rhs.clone(),rhs,status);
			
//			_v.printf("Solution", System.out);
		}
	}
	
}
