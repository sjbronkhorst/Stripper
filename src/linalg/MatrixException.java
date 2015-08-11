package linalg;

public class MatrixException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MatrixException(){
		super();
	}
	
	public MatrixException(String description){
		super(description);
	}
}
