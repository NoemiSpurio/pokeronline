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
import it.prova.pokeronline.web.api.exception.TavoloConGiocatoriException;
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
		Tavolo tavoloToUpdate = tavoloRepository.findById(tavoloInstance.getId())
				.orElseThrow(() -> new TavoloNotFoundException("Tavolo not found con id: " + tavoloInstance.getId()));
		;

		if (tavoloToUpdate.getGiocatori().size() != 0) {
			throw new TavoloConGiocatoriException("Non puoi modificare un tavolo se ha dei giocatori associati");
		}

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
		Tavolo tavoloToRemove = tavoloRepository.findById(idToRemove)
				.orElseThrow(() -> new TavoloNotFoundException("Tavolo not found con id: " + idToRemove));
		if (tavoloToRemove.getGiocatori().size() != 0) {
			throw new TavoloConGiocatoriException("Non puoi eliminare un tavolo se ha dei giocatori associati");
		}
		tavoloRepository.deleteById(idToRemove);
	}

	@Override
	public List<Tavolo> tavoliConEsperienzaMinimaMinoreDi(Utente utenteLoggato) {

		return tavoloRepository.findByEsperienzaMinimaLessThan(utenteLoggato.getEsperienzaAccumulata());	
	}

}
