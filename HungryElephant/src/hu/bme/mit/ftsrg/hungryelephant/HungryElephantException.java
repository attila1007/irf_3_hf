package hu.bme.mit.ftsrg.hungryelephant;

public class HungryElephantException extends Exception {
	private static final long serialVersionUID = -1879117007387221046L;

	public HungryElephantException(String message, Throwable cause) {
		super(message, cause);
	}

	public HungryElephantException(String message) {
		super(message);
	}
}
