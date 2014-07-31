package fi.vm.sade.valinta.seuranta.dao;

import java.util.Collection;

import fi.vm.sade.valinta.seuranta.domain.Ilmoitus;
import fi.vm.sade.valinta.seuranta.dto.HakukohdeTila;
import fi.vm.sade.valinta.seuranta.dto.IlmoitusTyyppi;
import fi.vm.sade.valinta.seuranta.dto.LaskentaDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTila;
import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
public interface SeurantaDao {

	/**
	 * Kaikki laskentaan liittyva tieto
	 * 
	 * @param uuid
	 * @return
	 */
	LaskentaDto haeLaskenta(String uuid);
	
	/**
	 * Yhteenveto laskennan kulusta
	 * 
	 * @param uuid
	 * @return
	 */
	YhteenvetoDto haeYhteenveto(String uuid);
	
	/**
	 * Yhteenvedot laskennan kulusta
	 * 
	 * @param uuid
	 * @return
	 */
	Collection<YhteenvetoDto> haeYhteenvedotHaulle(String hakuOid);
	/**
	 * 
	 * @param hakuOid
	 * @param hakukohdeOids
	 * @return uuid
	 */
	String luoLaskenta(String hakuOid, Collection<String> hakukohdeOids);
	
	
	void poistaLaskenta(String uuid);
	/**
	 * 
	 * @param uuid
	 * @param hakukohdeOid
	 * @param tila
	 */
	void merkkaaTila(String uuid, String hakukohdeOid, HakukohdeTila tila);
	/**
	 * 
	 * @param uuid
	 * @param hakukohdeOid
	 * @param tila
	 */
	void merkkaaTila(String uuid, LaskentaTila tila);
	/**
	 * 
	 * 
	 * @param uuid
	 * @param tyyppi
	 * @param ilmoitus
	 */
	void lisaaIlmoitus(String uuid, IlmoitusTyyppi tyyppi, Ilmoitus ilmoitus);
}
