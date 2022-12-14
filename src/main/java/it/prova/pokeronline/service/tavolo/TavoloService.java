package it.prova.pokeronline.service.tavolo;

import java.util.List;

import it.prova.pokeronline.model.Tavolo;
import it.prova.pokeronline.model.Utente;

public interface TavoloService {

	List<Tavolo> listAll(boolean eager);
	
	Tavolo caricaSingoloTavolo(Long id, boolean eager);
	
	Tavolo aggiorna(Tavolo tavoloInstance);
	
	Tavolo inserisciNuovo(Tavolo tavoloInstance);
	
	void rimuovi(Long idToRemove);
	
	List<Tavolo> tavoliConEsperienzaMinimaMinoreDi(Utente utenteLoggato);
}
