package linalg;

public class VectorDimensionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public VectorDimensionException(){
		super();
	}
	
	public VectorDimensionException(String description){
		super(description);
	}
}
