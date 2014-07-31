package fi.vm.sade.valinta.seuranta.domain;

import java.util.Collection;
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
	private String hakuOid;
	private Date luotu;
	private LaskentaTila tila;
	private Map<HakukohdeTila, Collection<String>> hakukohteet;
	private Map<IlmoitusTyyppi, Collection<Ilmoitus>> ilmoitukset;
	
	public Laskenta() {
		
	}
	
	public Laskenta(String hakuOid, Collection<String> hakukohdeOids) {
		this.hakuOid = hakuOid;
		this.luotu = new Date();
		this.hakukohteet = Maps.newHashMap();
		this.hakukohteet.put(HakukohdeTila.TEKEMATTA, hakukohdeOids);
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
