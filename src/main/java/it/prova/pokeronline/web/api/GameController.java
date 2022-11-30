package it.prova.pokeronline.web.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.prova.pokeronline.dto.TavoloDTO;
import it.prova.pokeronline.model.Tavolo;
import it.prova.pokeronline.model.Utente;
import it.prova.pokeronline.service.tavolo.TavoloService;
import it.prova.pokeronline.service.utente.UtenteService;
import it.prova.pokeronline.web.api.exception.CreditoNonValidoException;
import it.prova.pokeronline.web.api.exception.UtenteNotFoundException;

@RestController
@RequestMapping("api/game")
public class GameController {

	@Autowired
	private UtenteService utenteService;

	@Autowired
	private TavoloService tavoloService;

	@GetMapping("/compraCredito/{creditoAggiunto}")
	@ResponseStatus(HttpStatus.OK)
	public void compraCredito(@PathVariable(required = true) Integer creditoAggiunto) {
		if (creditoAggiunto < 0) {
			throw new CreditoNonValidoException("Non puoi comprare del credito negativo");
		}

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteLoggato = utenteService.findByUsername(username);

		if (utenteLoggato == null) {
			throw new UtenteNotFoundException("Utente non trovato");
		}

		utenteService.compraCredito(creditoAggiunto, utenteLoggato);
	}

	@GetMapping("/lastGame")
	public TavoloDTO dammiIlLastGame() {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteLoggato = utenteService.findByUsername(username);

		if (utenteLoggato == null) {
			throw new UtenteNotFoundException("Utente non trovato");
		}

		Tavolo result = utenteService.dammiUltimoGame(utenteLoggato);

		return TavoloDTO.buildTavoloDTOFromModel(result, false);
	}

	@GetMapping("/abbandonaPartita")
	@ResponseStatus(HttpStatus.OK)
	public void abbandonaTavoloJoinato() {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteLoggato = utenteService.findByUsername(username);

		if (utenteLoggato == null) {
			throw new UtenteNotFoundException("Utente non trovato");
		}

		utenteService.abbandonaPartita(utenteLoggato);
	}

	@GetMapping("/cercaTavolo")
	public List<TavoloDTO> cercaTavoloConEsperienzaCompatibile() {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteLoggato = utenteService.findByUsername(username);

		if (utenteLoggato == null) {
			throw new UtenteNotFoundException("Utente non trovato");
		}

		return TavoloDTO.createTavoloDTOListFromModelList(
				tavoloService.tavoliConEsperienzaMinimaMinoreDi(utenteLoggato), false);
	}
}
