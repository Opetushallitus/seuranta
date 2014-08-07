package fi.vm.sade.valinta.seuranta.dto;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
public class HakukohdeDto {

	private final String hakukohdeOid;
	private final HakukohdeTila tila;
	private final List<IlmoitusDto> ilmoitukset;

	public HakukohdeDto() {
		this.hakukohdeOid = null;
		this.tila = null;
		this.ilmoitukset = null;
	}

	public HakukohdeDto(String hakukohdeOid, HakukohdeTila tila,
			List<IlmoitusDto> ilmoitukset) {
		this.hakukohdeOid = hakukohdeOid;
		this.tila = tila;
		this.ilmoitukset = ilmoitukset;
	}

	public HakukohdeTila getTila() {
		return tila;
	}

	public List<IlmoitusDto> getIlmoitukset() {
		return ilmoitukset;
	}

	public String getHakukohdeOid() {
		return hakukohdeOid;
	}

}
