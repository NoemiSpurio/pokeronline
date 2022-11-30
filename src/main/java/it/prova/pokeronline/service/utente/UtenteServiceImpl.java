package it.prova.pokeronline.service.utente;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.prova.pokeronline.model.StatoUtente;
import it.prova.pokeronline.model.Tavolo;
import it.prova.pokeronline.model.Utente;
import it.prova.pokeronline.repository.utente.UtenteRepository;
import it.prova.pokeronline.web.api.exception.UtenteNotFoundException;

@Service
@Transactional(readOnly = true)
public class UtenteServiceImpl implements UtenteService {

	@Autowired
	private UtenteRepository repository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<Utente> listAllUtenti() {
		return (List<Utente>) repository.findAll();
	}

	public Utente caricaSingoloUtente(Long id) {
		return repository.findById(id).orElse(null);
	}

	public Utente caricaSingoloUtenteConRuoli(Long id) {
		return repository.findByIdConRuoli(id).orElse(null);
	}

	@Transactional
	public void aggiorna(Utente utenteInstance) {
		// deve aggiornare solo nome, cognome, username, ruoli
		Utente utenteReloaded = repository.findById(utenteInstance.getId()).orElse(null);
		if (utenteReloaded == null)
			throw new RuntimeException("Elemento non trovato");
		utenteReloaded.setNome(utenteInstance.getNome());
		utenteReloaded.setCognome(utenteInstance.getCognome());
		utenteReloaded.setUsername(utenteInstance.getUsername());
		utenteReloaded.setRuoli(utenteInstance.getRuoli());
		repository.save(utenteReloaded);
	}

	@Transactional
	public void inserisciNuovo(Utente utenteInstance) {
		utenteInstance.setStato(StatoUtente.CREATO);
		utenteInstance.setPassword(passwordEncoder.encode(utenteInstance.getPassword()));
		utenteInstance.setDataRegistrazione(new Date());
		utenteInstance.setCreditoAccumulato(0);
		utenteInstance.setEsperienzaAccumulata(0);
		repository.save(utenteInstance);
	}

	@Transactional
	public void rimuovi(Long idToRemove) {
		repository.deleteById(idToRemove);
		;
	}

	public List<Utente> findByExample(Utente example) {
		// TODO Da implementare
		return listAllUtenti();
	}

	public Utente eseguiAccesso(String username, String password) {
		return repository.findByUsernameAndPasswordAndStato(username, password, StatoUtente.ATTIVO);
	}

	public Utente findByUsernameAndPassword(String username, String password) {
		return repository.findByUsernameAndPassword(username, password);
	}

	@Transactional
	public void changeUserAbilitation(Long utenteInstanceId) {
		Utente utenteInstance = caricaSingoloUtente(utenteInstanceId);
		if (utenteInstance == null)
			throw new UtenteNotFoundException("Elemento non trovato.");

		if (utenteInstance.getStato() == null || utenteInstance.getStato().equals(StatoUtente.CREATO))
			utenteInstance.setStato(StatoUtente.ATTIVO);
		else if (utenteInstance.getStato().equals(StatoUtente.ATTIVO))
			utenteInstance.setStato(StatoUtente.DISABILITATO);
		else if (utenteInstance.getStato().equals(StatoUtente.DISABILITATO))
			utenteInstance.setStato(StatoUtente.ATTIVO);
	}

	public Utente findByUsername(String username) {
		return repository.findByUsername(username).orElse(null);
	}

	@Override
	@Transactional
	public void compraCredito(Integer creditoAggiunto, Utente utenteLoggato) {

		if (utenteLoggato == null)
			throw new UtenteNotFoundException("Elemento non trovato.");

		Integer nuovoCredito = utenteLoggato.getCreditoAccumulato() + creditoAggiunto;
		utenteLoggato.setCreditoAccumulato(nuovoCredito);
	}

	@Override
	public Tavolo dammiUltimoGame(Utente utenteLoggato) {

//		if (utenteLoggato.getTavoloJoined() == null) {
//			throw new TavoloNotFoundException("Non stai giocando in nessun tavolo");
//		}
//
//		return utenteLoggato.getTavoloJoined();
		return null;
	}

	@Override
	@Transactional
	public void abbandonaPartita(Utente utenteLoggato) {

//		if (utenteLoggato.getTavoloJoined() == null) {
//			throw new TavoloNotFoundException(
//					"Non puoi abbandonare una partita perche' non stai giocando in nessun tavolo");
//		}
//
//		utenteLoggato.setTavoloJoined(null);
//		utenteLoggato.setEsperienzaAccumulata(utenteLoggato.getEsperienzaAccumulata() + 1);
	}

	@Override
	@Transactional
	public void giocaPartita(Tavolo tavoloDaJoinare, Utente utenteLoggato) {

//		if (utenteLoggato.getTavoloJoined() != null) {
//			throw new UtenteGiaInGiocoException(
//					"Non puoi giocare una partita perche' sei ancora unito ad un altro tavolo.");
//		}
//		
//		utenteLoggato.setTavoloJoined(tavoloDaJoinare);
// TODO	
		
	}

}
