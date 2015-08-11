package linalg;

public class InvalidParameterException extends Exception{

	private static final long serialVersionUID = 1L;

	public InvalidParameterException(){
		
	}
	
	public InvalidParameterException(String parameter){
		super(parameter);
	}
	
}
