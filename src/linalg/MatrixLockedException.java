package linalg;

public class MatrixLockedException extends MatrixException {

	private static final long serialVersionUID = 1L;

	public MatrixLockedException(){
		super();
	}
	
	public MatrixLockedException(String description){
		super(description);
	}
}
