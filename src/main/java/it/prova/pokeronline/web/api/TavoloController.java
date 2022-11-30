package it.prova.pokeronline.web.api;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.prova.pokeronline.dto.TavoloDTO;
import it.prova.pokeronline.model.Tavolo;
import it.prova.pokeronline.model.Utente;
import it.prova.pokeronline.service.tavolo.TavoloService;
import it.prova.pokeronline.service.utente.UtenteService;
import it.prova.pokeronline.web.api.exception.IdNotNullForInsertException;
import it.prova.pokeronline.web.api.exception.PermessoNegatoException;
import it.prova.pokeronline.web.api.exception.TavoloNotFoundException;

@RestController
@RequestMapping("api/tavolo")
public class TavoloController {

	@Autowired
	private TavoloService tavoloService;

	@Autowired
	private UtenteService utenteService;

	@GetMapping
	public List<TavoloDTO> listAll() {
		return TavoloDTO.createTavoloDTOListFromModelList(tavoloService.listAll(true), true);
	}

	@GetMapping("/{id}")
	public TavoloDTO findById(@PathVariable(value = "id", required = true) long id) {
		Tavolo result = tavoloService.caricaSingoloTavolo(id, true);

		if (result == null) {
			throw new TavoloNotFoundException("Tavolo not found con id: " + id);
		}

		return TavoloDTO.buildTavoloDTOFromModel(result, true);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TavoloDTO createNew(@Valid @RequestBody TavoloDTO tavoloInput) throws IdNotNullForInsertException {

		if (tavoloInput.getId() != null) {
			throw new IdNotNullForInsertException("Non è ammesso fornire un id per la creazione");
		}

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteLoggato = utenteService.findByUsername(username);

		if (utenteLoggato.getRuoli().stream().filter(r -> r.getCodice().equals("ROLE_ADMIN")).findAny()
				.orElse(null) == null
				&& utenteLoggato.getRuoli().stream().filter(r -> r.getCodice().equals("ROLE_SPECIAL_PLAYER")).findAny()
						.orElse(null) == null) {
			throw new PermessoNegatoException("Non hai i permessi per creare un tavolo!");
		}

		Tavolo tavoloInserito = tavoloService.inserisciNuovo(tavoloInput.buildFromModel());
		return TavoloDTO.buildTavoloDTOFromModel(tavoloInserito, false);
	}

	@PutMapping("/{id}")
	public TavoloDTO update(@Valid @RequestBody TavoloDTO tavoloInput, @PathVariable(required = true) Long id) {
		Tavolo tavolo = tavoloService.caricaSingoloTavolo(id, true);

		if (tavolo == null) {
			throw new TavoloNotFoundException("Tavolo not found con id: " + id);
		}

		tavoloInput.setId(id);
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteLoggato = utenteService.findByUsername(username);

		if (utenteLoggato.getRuoli().stream().filter(r -> r.getCodice().equals("ROLE_ADMIN")).findAny()
				.orElse(null) == null
				|| (utenteLoggato.getRuoli().stream().filter(r -> r.getCodice().equals("ROLE_SPECIAL_PLAYER")).findAny()
						.orElse(null) != null && tavolo.getUtenteCreazione().getId() != utenteLoggato.getId())) {
			throw new PermessoNegatoException("Non hai i permessi per modificare questo tavolo");
		}

		Tavolo tavoloAggiornato = tavoloService.aggiorna(tavoloInput.buildFromModel());
		return TavoloDTO.buildTavoloDTOFromModel(tavoloAggiornato, false);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable(required = true) Long id) {
		Tavolo tavolo = tavoloService.caricaSingoloTavolo(id, true);

		if (tavolo == null) {
			throw new TavoloNotFoundException("Tavolo not found con id: " + id);
		}

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteLoggato = utenteService.findByUsername(username);

		if (utenteLoggato.getRuoli().stream().filter(r -> r.getCodice().equals("ROLE_ADMIN")).findAny()
				.orElse(null) == null
				|| (utenteLoggato.getRuoli().stream().filter(r -> r.getCodice().equals("ROLE_SPECIAL_PLAYER")).findAny()
						.orElse(null) != null && tavolo.getUtenteCreazione().getId() != utenteLoggato.getId())) {
			throw new PermessoNegatoException("Non hai i permessi per modificare questo tavolo");
		}

		tavoloService.rimuovi(id);
	}
}
