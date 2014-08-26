package fi.vm.sade.valinta.seuranta.laskenta.domain;

import java.util.List;

import fi.vm.sade.valinta.seuranta.dto.HakukohdeTila;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
public class Hakukohde {

	private final HakukohdeTila tila;
	private final List<Ilmoitus> ilmoitukset;

	public Hakukohde() {
		this.tila = HakukohdeTila.TEKEMATTA;
		this.ilmoitukset = null;
	}

	public Hakukohde(HakukohdeTila tila, List<Ilmoitus> ilmoitukset) {
		this.tila = tila;
		this.ilmoitukset = ilmoitukset;
	}

	public HakukohdeTila getTila() {
		return tila;
	}

	public List<Ilmoitus> getIlmoitukset() {
		return ilmoitukset;
	}
}
