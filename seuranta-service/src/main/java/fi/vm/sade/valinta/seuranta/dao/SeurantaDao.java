package fi.vm.sade.valinta.seuranta.dao;

import java.util.Collection;
import java.util.Date;

import fi.vm.sade.valinta.seuranta.dto.HakukohdeTila;
import fi.vm.sade.valinta.seuranta.dto.IlmoitusDto;
import fi.vm.sade.valinta.seuranta.dto.IlmoitusTyyppi;
import fi.vm.sade.valinta.seuranta.dto.LaskentaDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTila;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTyyppi;
import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;
import fi.vm.sade.valinta.seuranta.laskenta.domain.Ilmoitus;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
public interface SeurantaDao {

	/**
	 * Siivoaa ylimaaraiset laskennat pois
	 */
	void siivoa(Date viimeinenSailottavaPaivamaara);

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
	 * Yhteenvedot laskennan kulusta
	 * 
	 * @param uuid
	 * @return
	 */
	Collection<YhteenvetoDto> haeYhteenvedotHaulle(String hakuOid,
			LaskentaTyyppi tyyppi);

	/**
	 * Yhteenvedot kaynnissa olevien laskentojen kulusta
	 * 
	 * @param uuid
	 * @return
	 */
	Collection<YhteenvetoDto> haeKaynnissaOlevienYhteenvedotHaulle(
			String hakuOid);

	/**
	 * 
	 * @param hakuOid
	 * @param hakukohdeOids
	 * @return uuid
	 */
	String luoLaskenta(String hakuOid, LaskentaTyyppi tyyppi,
			Collection<String> hakukohdeOids);

	/**
	 * 
	 * @param hakuOid
	 * @param hakukohdeOids
	 * @return uuid
	 */
	String luoLaskenta(String hakuOid, LaskentaTyyppi tyyppi,
			int valinnanvaihe, boolean valintakoelaskenta,
			Collection<String> hakukohdeOids);

	/**
	 * 
	 * @param uuid
	 */
	void poistaLaskenta(String uuid);

	/**
	 * 
	 * @param uuid
	 * @param hakukohdeOid
	 * @param tila
	 */
	YhteenvetoDto merkkaaTila(String uuid, String hakukohdeOid,
			HakukohdeTila tila);

	/**
	 * 
	 * @param uuid
	 * @param hakukohdeOid
	 * @param tila
	 */
	YhteenvetoDto merkkaaTila(String uuid, String hakukohdeOid,
			HakukohdeTila tila, IlmoitusDto ilmoitus);

	/**
	 * 
	 * @param uuid
	 * @param hakukohdeOid
	 * @param tila
	 */
	YhteenvetoDto lisaaIlmoitus(String uuid, String hakukohdeOid,
			IlmoitusDto ilmoitus);

	/**
	 * 
	 * @param uuid
	 * @param hakukohdeOid
	 * @param tila
	 */
	YhteenvetoDto merkkaaTila(String uuid, LaskentaTila tila);

	/**
	 * 
	 * @param uuid
	 * @param hakukohdeOid
	 * @param tila
	 */
	LaskentaDto resetoiEiValmiitHakukohteet(String uuid,
			boolean nollaaIlmoitukset);

	/**
	 * 
	 * 
	 * @param uuid
	 * @param tyyppi
	 * @param ilmoitus
	 */
	void lisaaIlmoitus(String uuid, String hakukohdeOid, Ilmoitus ilmoitus);
}
