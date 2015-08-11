package linalg;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DiagonalMatrix {

	private Vector m_values;
	
	public DiagonalMatrix(int size){
		this.m_values = Vector.getVector(size);
	}
	
	public double get(int row){
		return this.m_values.get(row);
	}
	
	public void set(double value, int row){
		this.m_values.set(value, row);
	}
	
	public void add(double value, int row){
		this.m_values.add(value, row);
	}
	
	public DiagonalMatrix clone(){
		DiagonalMatrix _clone = new DiagonalMatrix(0);
		_clone.m_values.release();
		_clone.m_values = m_values.clone();
		return _clone;
	}
	
	public Vector multi(Vector vec){
		Vector _clone = m_values.clone();
		_clone.multiply(vec);
		return _clone;
	}
	
	public void inverse(){
		for(int i = 0; i < m_values.size(); i++){
			m_values.set(1./m_values.get(i),i);
		}
	}
	
	public int size(){
		return m_values.size();
	}
	
	public DiagonalMatrix release(){
		m_values.release();
		return null;
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
				if(i == j)
					stream.print(format(get(i),df,cols_per_value)+"|");
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
}
