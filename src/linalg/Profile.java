package linalg;

public class Profile {
	
	private int[] m_values;
	
	public Profile(int numEq){
		this.m_values = new int[numEq];
		for(int i = 0; i < numEq; i++){
			m_values[i] = i;
		}
	}
	
	public Profile(int[] profile){
		this.m_values = profile.clone();
	}
	
	public int[] profile(){
		return m_values;
	}

	public void add(int[] indices) {
		for (int i = 0; i < indices.length; i++){
			for (int j = 0; j < indices.length; j++){				
				if (m_values[indices[i]] > indices[j]){
					m_values[indices[i]] = indices[j];
				}
			}
		}
	}
	
	public void setProfile(int row, int col){
		if (m_values[row] > col){
			m_values[row] = col;
		}
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder("profile = [");
		for(int i = 0; i < m_values.length; i++){
			sb.append(m_values[i]);
			if(i < m_values.length -1)
				sb.append(", ");
			else
				sb.append(" ");
		}
		sb.append("]");
		return sb.toString();
	}
	
}
