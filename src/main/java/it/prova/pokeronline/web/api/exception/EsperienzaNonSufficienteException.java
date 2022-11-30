package it.prova.pokeronline.web.api.exception;

public class EsperienzaNonSufficienteException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EsperienzaNonSufficienteException(String message) {
		super(message);
	}
}
