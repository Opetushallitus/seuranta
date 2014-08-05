package fi.vm.sade.valinta.seuranta.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Id;
import com.google.common.collect.Maps;

import fi.vm.sade.valinta.seuranta.dto.HakukohdeTila;
import fi.vm.sade.valinta.seuranta.dto.IlmoitusTyyppi;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTila;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
public class Laskenta {

	@Id
	private ObjectId uuid;
	private final String hakuOid;
	private final Date luotu;
	private final LaskentaTila tila;
	private final Map<HakukohdeTila, Collection<String>> hakukohteet;
	private final Map<IlmoitusTyyppi, Collection<Ilmoitus>> ilmoitukset;

	public Laskenta() {
		this.uuid = null;
		this.hakuOid = null;
		this.tila = null;
		this.hakukohteet = null;
		this.ilmoitukset = null;
		this.luotu = null;
	}

	public Laskenta(String hakuOid, Collection<String> hakukohdeOids) {
		this.uuid = null;
		this.hakuOid = hakuOid;
		this.luotu = new Date();
		this.tila = LaskentaTila.MENEILLAAN;
		this.hakukohteet = Maps.newHashMap();
		this.hakukohteet.put(HakukohdeTila.TEKEMATTA, hakukohdeOids);
		this.ilmoitukset = Collections.emptyMap();
	}

	public Map<HakukohdeTila, Collection<String>> getHakukohteet() {
		return hakukohteet;
	}

	public Map<IlmoitusTyyppi, Collection<Ilmoitus>> getIlmoitukset() {
		return ilmoitukset;
	}

	public String getHakuOid() {
		return hakuOid;
	}

	public Date getLuotu() {
		return luotu;
	}

	public LaskentaTila getTila() {
		return tila;
	}

	public ObjectId getUuid() {
		return uuid;
	}
}
