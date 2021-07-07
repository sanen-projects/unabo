package online.sanen.unabo.api.exception;

/**
 * 
 *
 * @author LazyToShow <br>
 *         Date: Nov 22, 2018 <br>
 *         Time: 10:38:30 AM
 */
public class StructuralException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public StructuralException(Throwable e) {
		super(e);
	}

	public StructuralException(String arg0) {
		super(arg0);
	}

}
