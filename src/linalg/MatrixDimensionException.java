package linalg;

public class MatrixDimensionException extends MatrixException {

	private static final long serialVersionUID = 1L;

	public MatrixDimensionException(){
		super();
	}
	
	public MatrixDimensionException(String description){
		super(description);
	}
}
