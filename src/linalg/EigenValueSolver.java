package linalg;

import serialize.ProfileMatrix;

public class EigenValueSolver {

	private int m_numberOfStates;
	
	public EigenValueSolver(ProfileMatrix pmat, int numberOfStates){
		this.m_numberOfStates = numberOfStates;
		
	}
	
	public EigenValueSolver(ProfileMatrix pmat, boolean[] status, int numberOfStates){
		this.m_numberOfStates = numberOfStates;
		
	}
	
	public int numberOfEigenStates(){
		return m_numberOfStates;
	}
	
	public void solve(){
		
	}
	
	public Matrix getEigenVectors(){
		return null;
	}
	
	public Vector getEigenValues(){
		return null;
	}
	
}
