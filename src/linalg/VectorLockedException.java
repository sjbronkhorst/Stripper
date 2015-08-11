package linalg;

public class VectorLockedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public VectorLockedException(){
		super();
	}
	
	public VectorLockedException(String description){
		super(description);
	}
}
