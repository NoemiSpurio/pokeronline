package it.prova.pokeronline.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import it.prova.pokeronline.model.Tavolo;
import it.prova.pokeronline.model.Utente;

public class TavoloDTO {

	private Long id;

	@NotNull(message = "{esperienzaMinima.notnull}")
	private Integer esperienzaMinima;

	@NotNull(message = "{cifraMinima.notnull}")
	private Integer cifraMinima;

	@NotBlank(message = "{denominazione.notblank}")
	private String denominazione;

	private Date dataCreazione;

	@JsonIgnoreProperties(value = { "tavoliCreati", "tavoloJoined" })
	@NotNull(message = "utenteCreazione.notnull")
	private UtenteDTO utenteCreazione;

	@JsonIgnoreProperties(value = { "tavoliCreati", "tavoloJoined" })
	private Set<UtenteDTO> giocatori = new HashSet<UtenteDTO>(0);

	public TavoloDTO() {

	}

	public TavoloDTO(Long id, Integer esperienzaMinima, Integer cifraMinima, String denominazione, Date dataCreazione,
			UtenteDTO utenteCreazione) {
		super();
		this.id = id;
		this.esperienzaMinima = esperienzaMinima;
		this.cifraMinima = cifraMinima;
		this.denominazione = denominazione;
		this.dataCreazione = dataCreazione;
		this.utenteCreazione = utenteCreazione;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getEsperienzaMinima() {
		return esperienzaMinima;
	}

	public void setEsperienzaMinima(Integer esperienzaMinima) {
		this.esperienzaMinima = esperienzaMinima;
	}

	public Integer getCifraMinima() {
		return cifraMinima;
	}

	public void setCifraMinima(Integer cifraMinima) {
		this.cifraMinima = cifraMinima;
	}

	public String getDenominazione() {
		return denominazione;
	}

	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}

	public Date getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(Date dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public UtenteDTO getUtenteCreazione() {
		return utenteCreazione;
	}

	public void setUtenteCreazione(UtenteDTO utenteCreazione) {
		this.utenteCreazione = utenteCreazione;
	}

	public Set<UtenteDTO> getGiocatori() {
		return giocatori;
	}

	public void setGiocatori(Set<UtenteDTO> giocatori) {
		this.giocatori = giocatori;
	}

	public Tavolo buildFromModel() {
		Tavolo result = new Tavolo(this.id, this.esperienzaMinima, this.cifraMinima, this.denominazione,
				this.dataCreazione, this.utenteCreazione.buildUtenteModel(false));
		if (giocatori.size() != 0) {
			for (UtenteDTO giocatoreItem : giocatori) {
				result.getGiocatori().add(giocatoreItem.buildUtenteModel(false));
			}
		}
		return result;
	}

	public static TavoloDTO buildTavoloDTOFromModel(Tavolo tavoloModel, boolean includeGiocatori) {
		TavoloDTO result = new TavoloDTO(tavoloModel.getId(), tavoloModel.getEsperienzaMinima(),
				tavoloModel.getCifraMinima(), tavoloModel.getDenominazione(), tavoloModel.getDataCreazione(),
				UtenteDTO.buildUtenteDTOFromModel(tavoloModel.getUtenteCreazione()));

		if (includeGiocatori) {
			for (Utente giocatoriItem : tavoloModel.getGiocatori()) {
				result.getGiocatori().add(UtenteDTO.buildUtenteDTOFromModel(giocatoriItem));
			}
		}

		return result;
	}

	public static List<TavoloDTO> createTavoloDTOListFromModelList(List<Tavolo> modelList, boolean includeGiocatori) {
		return modelList.stream().map(tavoloEntity -> {
			return TavoloDTO.buildTavoloDTOFromModel(tavoloEntity, includeGiocatori);
		}).collect(Collectors.toList());
	}

}
