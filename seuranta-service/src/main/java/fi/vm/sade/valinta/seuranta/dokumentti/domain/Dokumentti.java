package fi.vm.sade.valinta.seuranta.dokumentti.domain;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fi.vm.sade.valinta.seuranta.dto.DokumenttiDto;
import fi.vm.sade.valinta.seuranta.dto.IlmoitusDto;
import fi.vm.sade.valinta.seuranta.dto.VirheilmoitusDto;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
public class Dokumentti {

	@Id
	private final ObjectId uuid;
	private final String kuvaus; // teksti kayttoliittymaan siita mita juuri nyt tehdaan, voi paivittya kun tehtava paivittyy
	private final String dokumenttiId;
	private final List<VirheilmoitusDto> virheilmoitukset;

	public Dokumentti() {
		this.uuid = null;
		this.kuvaus = null;
		this.dokumenttiId = null;
		this.virheilmoitukset = null;
	}

	public DokumenttiDto asDto() {
		return new DokumenttiDto(uuid.toString(), dokumenttiId, kuvaus,
				virheilmoitukset);
	}

	public Dokumentti(String dokumenttiId, String kuvaus, List<VirheilmoitusDto> virheilmoitukset) {
		this.uuid = null;
		this.dokumenttiId = dokumenttiId;
		this.kuvaus = kuvaus;
		this.virheilmoitukset = virheilmoitukset;
	}

	public String getKuvaus() {
		return kuvaus;
	}

	public List<VirheilmoitusDto> getVirheilmoitukset() {
		return virheilmoitukset;
	}

	public String getDokumenttiId() {
		return dokumenttiId;
	}

	public ObjectId getUuid() {
		return uuid;
	}
}
