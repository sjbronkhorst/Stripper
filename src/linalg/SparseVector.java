package linalg;

import serialize.ProfileMatrix;
import java.io.PrintStream;

/**
 * @author bertie
 */
public class SparseVector{
  	
	int itemp;
	double vtemp;
  	
  	int [] indices;
  	double[] values;
  	int counter;
  	
  	private boolean isSorted = false;
  	
  	/**
	 * 
	 */
	public SparseVector(int size) {
		indices = new int[size];
		values = new double[size];
		counter = 0;

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
  	
		void addValue(int index, double value){
  		indices[counter] = index;
  		values[counter] = value;
  		counter++;
  		isSorted = false;
  	}
  	
  	void sort(){
  		quicksort(indices,0,counter-1);
  		isSorted = true;
  	}
  	
  	public double dot(SparseVector sv){  	
  		double dot = 0;
  		
  		if(!isSorted)
  			sort();
  		if(!sv.isSorted)
  			sv.sort();
  		
  		int i = 0;
  		int j = 0;
  		if(counter <= sv.counter){
  			while(i < counter){
  				if(indices[i] == sv.indices[j]){
  					dot += values[i] * sv.values[j];
  					i++;
  					j++;
  				}
  				else if(indices[i] < sv.indices[j]){
  					i++;
  				}
  				else if(indices[i] > sv.indices[j]){
  					j++;
  					if(j > sv.counter)
  						break;
  				}
  			}
  		}
  		else {
  			while(j < sv.counter){
  				if(indices[i] == sv.indices[j]){
  					dot += values[i] * sv.values[j];
  					i++;
  					j++;
  				}
  				else if(indices[i] < sv.indices[j]){
  					i++;
  					if(i > counter)
  						break;
  				}
  				else if(indices[i] > sv.indices[j]){
  					j++;
  				}
  			} 	  			
  		}  			
  		return dot;
  	}
  	
  	
    private void swap(int indexA,int indexB){
      	itemp = indices[indexA];
      	vtemp = values[indexA];
      	
      	indices[indexA] = indices[indexB];
      	values[indexA] = values[indexB];
      	
      	indices[indexB] = itemp;
      	values[indexB] = vtemp; 	
      }
  	
  	
    public void set(ProfileMatrix pmat, int row){
    	clear();
    	int profile = pmat.profile(row);
    	for(int i = profile; i <= row; i++){
    		addValue(i, pmat.getValue(row, i));
    	}
    	for(int i = row + 1 ; i < pmat.size(); i++)
    		if(row >= pmat.profile(i))
    			addValue(i, pmat.getValue(i, row));
    	
    	isSorted = true;
    }
    
    void clear(){
    	for(int i = 0; i < counter;i++){
    		indices[i] = 0;
    		values[i] = 0.0;
    	}
    	counter = 0;
    }
    
    void printf(String name, PrintStream ps){

    }

    void print(String name, PrintStream ps){
    	ps.println(name +" = ");
    	for(int i = 0; i < counter; i++)
	  		ps.println(indices[i]+"\t"+values[i]);    	
    }

    
    public static void main(String[] args) {
		SparseVector sv1 = new SparseVector(10);
		
		sv1.addValue(3, 10.0);
		
		SparseVector sv2 = new SparseVector(10);
		sv2.addValue(4, 10.1);
		sv2.addValue(3, 2.9);
		
		sv2.print("sv2", System.out);
		sv2.sort();

		sv1.print("sv1", System.out);
		sv2.print("sv2", System.out);
		System.out.println("DOT = "+sv1.dot(sv2));
		System.out.println("DOT = "+sv2.dot(sv1));
	}
    
}

