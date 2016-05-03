package fi.vm.sade.valinta.seuranta.laskenta.dao;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import fi.vm.sade.valinta.seuranta.dto.HakukohdeDto;
import fi.vm.sade.valinta.seuranta.dto.HakukohdeTila;
import fi.vm.sade.valinta.seuranta.dto.IlmoitusDto;
import fi.vm.sade.valinta.seuranta.dto.IlmoitusTyyppi;
import fi.vm.sade.valinta.seuranta.dto.LaskentaDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTila;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTyyppi;
import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;
import fi.vm.sade.valinta.seuranta.laskenta.domain.Ilmoitus;

public interface SeurantaDao {

    /**
     * Siivoaa ylimaaraiset laskennat pois
     */
    void siivoa(Date viimeinenSailottavaPaivamaara);

    /**
     * Kaikki laskentaan liittyva tieto
     */
    LaskentaDto haeLaskenta(String uuid);

    /**
     * Yhteenveto laskennan kulusta
     */
    YhteenvetoDto haeYhteenveto(String uuid);

    /**
     * Yhteenvedot laskennan kulusta
     */
    Collection<YhteenvetoDto> haeYhteenvedotHaulle(String hakuOid);

    /**
     * Yhteenvedot laskennan kulusta
     */
    Collection<YhteenvetoDto> haeYhteenvedotHaulle(String hakuOid, LaskentaTyyppi tyyppi);

    /**
     * Yhteenvedot kaynnissa olevien laskentojen kulusta
     */
    Collection<YhteenvetoDto> haeKaynnissaOlevienYhteenvedotHaulle(String hakuOid);

    String luoLaskenta(String hakuOid, LaskentaTyyppi tyyppi, Boolean erillishaku,
                       Integer valinnanvaihe, Boolean valintakoelaskenta,
                       Collection<HakukohdeDto> hakukohdeOids);

    void poistaLaskenta(String uuid);

    YhteenvetoDto merkkaaTila(String uuid, String hakukohdeOid, HakukohdeTila tila);

    YhteenvetoDto merkkaaTila(String uuid, String hakukohdeOid, HakukohdeTila tila, IlmoitusDto ilmoitus);

    YhteenvetoDto lisaaIlmoitus(String uuid, String hakukohdeOid, IlmoitusDto ilmoitus);

    YhteenvetoDto merkkaaTila(String uuid, LaskentaTila tila, Optional<IlmoitusDto> ilmoitus);

    YhteenvetoDto merkkaaTila(String uuid, LaskentaTila tila, HakukohdeTila hakukohdeTila, Optional<IlmoitusDto> ilmoitus);

    LaskentaDto resetoiEiValmiitHakukohteet(String uuid, boolean nollaaIlmoitukset);

    void lisaaIlmoitus(String uuid, String hakukohdeOid, Ilmoitus ilmoitus);

    /**
     * @return uuid
     */
    String otaSeuraavaLaskentaTyonAlle();
}
