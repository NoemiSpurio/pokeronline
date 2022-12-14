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
import it.prova.pokeronline.repository.tavolo.TavoloRepository;
import it.prova.pokeronline.repository.utente.UtenteRepository;
import it.prova.pokeronline.web.api.exception.CreditoTerminatoException;
import it.prova.pokeronline.web.api.exception.TavoloNotFoundException;
import it.prova.pokeronline.web.api.exception.UtenteNonInGiocoException;
import it.prova.pokeronline.web.api.exception.UtenteNotFoundException;

@Service
@Transactional(readOnly = true)
public class UtenteServiceImpl implements UtenteService {

	@Autowired
	private UtenteRepository repository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private TavoloRepository tavoloRepository;

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

		if (utenteLoggato == null)
			throw new UtenteNotFoundException("Elemento non trovato.");

		Tavolo result = tavoloRepository.findByGiocatoriId(utenteLoggato.getId());

		if (result == null) {
			throw new UtenteNonInGiocoException("Utente non in gioco");
		}

		return result;
	}

	@Override
	@Transactional
	public void abbandonaPartita(Utente utenteLoggato) {

		if (utenteLoggato == null)
			throw new UtenteNotFoundException("Elemento non trovato.");

		Tavolo result = tavoloRepository.findByGiocatoriId(utenteLoggato.getId());

		if (result == null) {
			throw new TavoloNotFoundException(
					"Non puoi abbandonare una partita perche' non stai giocando in nessun tavolo");
		}

		utenteLoggato.setEsperienzaAccumulata(utenteLoggato.getEsperienzaAccumulata() + 1);
		result.getGiocatori().remove(utenteLoggato);
	}

	@Override
	@Transactional
	public void giocaPartita(Tavolo tavoloDaJoinare, Utente utenteLoggato) {

		if (utenteLoggato == null)
			throw new UtenteNotFoundException("Elemento non trovato.");
		if (tavoloDaJoinare == null)
			throw new TavoloNotFoundException("Tavolo non trovato");

		tavoloDaJoinare.getGiocatori().add(utenteLoggato);

		double randomizer = Math.random();
		int segno = 0;
		if (randomizer >= 0.5) {
			segno = 1;
		} else {
			segno = -1;
		}

		Integer somma = (int) (Math.random() * 1000);
		Integer totDaAggiungereOSottrarre = segno * somma;

		utenteLoggato.setCreditoAccumulato(utenteLoggato.getCreditoAccumulato() + totDaAggiungereOSottrarre);

		if (utenteLoggato.getCreditoAccumulato() < 0) {
			utenteLoggato.setCreditoAccumulato(0);
			utenteLoggato.setEsperienzaAccumulata(utenteLoggato.getEsperienzaAccumulata() + 1);
			tavoloDaJoinare.getGiocatori().remove(utenteLoggato);
			throw new CreditoTerminatoException("Sei stato espulso dal tavolo perche' hai terminato il credito");
		}

		utenteLoggato.setEsperienzaAccumulata(utenteLoggato.getEsperienzaAccumulata() + 1);
		tavoloDaJoinare.getGiocatori().remove(utenteLoggato);
	}

}
