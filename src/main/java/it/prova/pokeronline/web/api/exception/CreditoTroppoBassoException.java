package it.prova.pokeronline.web.api.exception;

public class CreditoTroppoBassoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CreditoTroppoBassoException(String message) {
		super(message);
	}
}
