package it.prova.pokeronline.web.api.exception;

public class UtenteNonInGiocoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UtenteNonInGiocoException(String message) {
		super(message);
	}
}
