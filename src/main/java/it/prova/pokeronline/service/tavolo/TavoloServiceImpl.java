package it.prova.pokeronline.service.tavolo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.prova.pokeronline.model.Tavolo;
import it.prova.pokeronline.model.Utente;
import it.prova.pokeronline.repository.tavolo.TavoloRepository;
import it.prova.pokeronline.service.utente.UtenteService;
import it.prova.pokeronline.web.api.exception.TavoloNotFoundException;

@Service
@Transactional(readOnly = true)
public class TavoloServiceImpl implements TavoloService {

	@Autowired
	private TavoloRepository tavoloRepository;

	@Autowired
	private UtenteService utenteService;

	@Override
	public List<Tavolo> listAll(boolean eager) {
		if (eager) {
			return (List<Tavolo>) tavoloRepository.findAllTavoloEager();
		}
		return (List<Tavolo>) tavoloRepository.findAll();
	}

	@Override
	public Tavolo caricaSingoloTavolo(Long id, boolean eager) {
		if (eager) {
			return tavoloRepository.findSingleTavoloEager(id);
		}
		return tavoloRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Tavolo aggiorna(Tavolo tavoloInstance) {
		return tavoloRepository.save(tavoloInstance);
	}

	@Override
	@Transactional
	public Tavolo inserisciNuovo(Tavolo tavoloInstance) {

		tavoloInstance.setDataCreazione(new Date());

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Utente utenteLoggato = utenteService.findByUsername(username);
		tavoloInstance.setUtenteCreazione(utenteLoggato);

		return tavoloRepository.save(tavoloInstance);
	}

	@Override
	@Transactional
	public void rimuovi(Long idToRemove) {
		tavoloRepository.findById(idToRemove)
				.orElseThrow(() -> new TavoloNotFoundException("Tavolo not found con id: " + idToRemove));
		tavoloRepository.deleteById(idToRemove);
	}

}
