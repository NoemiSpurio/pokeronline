package it.prova.pokeronline.web.api.exception;

public class CreditoNonValidoException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public CreditoNonValidoException(String message) {
		super(message);
	}

}
