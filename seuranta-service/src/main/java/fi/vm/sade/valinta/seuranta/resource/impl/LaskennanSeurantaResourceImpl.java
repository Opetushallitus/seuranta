package fi.vm.sade.valinta.seuranta.resource.impl;

import java.util.*;

import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.media.sse.EventOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.google.gson.GsonBuilder;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import fi.vm.sade.valinta.seuranta.dto.HakukohdeDto;
import fi.vm.sade.valinta.seuranta.dto.HakukohdeTila;
import fi.vm.sade.valinta.seuranta.dto.IlmoitusDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTila;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTyyppi;
import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;
import fi.vm.sade.valinta.seuranta.laskenta.dao.SeurantaDao;
import fi.vm.sade.valinta.seuranta.laskenta.service.SeurantaSSEService;
import fi.vm.sade.valinta.seuranta.resource.LaskentaSeurantaResource;

@Api(value = "/seuranta", description = "Seurantapalvelun rajapinta")
@Component
public class LaskennanSeurantaResourceImpl implements LaskentaSeurantaResource {
    private final static Logger LOG = LoggerFactory.getLogger(LaskennanSeurantaResourceImpl.class);

    private SeurantaDao seurantaDao;
    private SeurantaSSEService seurantaSSEService;

    @Autowired
    public LaskennanSeurantaResourceImpl(SeurantaDao seurantaDao, SeurantaSSEService seurantaSSEService) {
        this.seurantaDao = seurantaDao;
        this.seurantaSSEService = seurantaSSEService;
    }

    // @PreAuthorize("isAuthenticated()") ei tarvi, ei tarvisi muissakaan
    @ApiOperation(value = "SSE Yhteenvedot kaikista hakuun tehdyista laskennoista", response = Collection.class)
    public EventOutput yhteenvetoSSE(String uuid) {
        LOG.debug("REKISTEROIDAAN KUUNTELIJA {}", uuid);
        final EventOutput eventOutput = new EventOutput();
        try {
            seurantaSSEService.rekisteroi(uuid, eventOutput);
        } catch (Exception e) {
            LOG.error("Rekisterointi epaonnistui!" + uuid, e);
        }
        try {
            YhteenvetoDto y = null;
            try {
                y = seurantaDao.haeYhteenveto(uuid);
            } catch (Exception e) {
                y = new YhteenvetoDto(uuid, StringUtils.EMPTY, new Date().getTime(), LaskentaTila.MENEILLAAN, 0, 0, 0);
            }
            seurantaSSEService.paivita(y);
        } catch (Exception e) {
            LOG.error("Yhteenvetoa ei ole viela saatavilla. Ehka laskentaa ei ole ehditty viela muodostaa." + uuid, e);
        }
        LOG.debug("REKISTEROITY {}", uuid);
        return eventOutput;
    }

    @ApiOperation(value = "Laskennan tiedot", response = Collection.class)
    public LaskentaDto kuormantasausLaskenta(String uuid) {
        try {
            LaskentaDto l = seurantaDao.haeLaskenta(uuid);
            if (l == null) {
                LOG.error("SeurantaDao palautti null olion uuid:lle {}", uuid);
                throw new RuntimeException("SeurantaDao palautti null olion uuid:lle " + uuid);
            }
            return l;
        } catch (Exception e) {
            LOG.error("Ei saatu laskentaa uuid:lle " + uuid, e);
            throw e;
        }
    }

    // @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Yhteenveto laskennasta", response = Collection.class)
    public YhteenvetoDto yhteenveto(String uuid) {
        return seurantaDao.haeYhteenveto(uuid);
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Yhteenvedot kaikista hakuun tehdyista laskennoista", response = Collection.class)
    public Collection<YhteenvetoDto> hae(String hakuOid) {
        return seurantaDao.haeYhteenvedotHaulle(hakuOid);
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Yhteenvedot kaikista hakuun tehdyista laskennoista", response = Collection.class)
    public Collection<YhteenvetoDto> hae(String hakuOid, LaskentaTyyppi tyyppi) {
        return seurantaDao.haeYhteenvedotHaulle(hakuOid, tyyppi);
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Yhteenvedot kaikista hakuun tehdyista laskennoista", response = Collection.class)
    public LaskentaDto resetoiTilat(String uuid) {
        try {
            LaskentaDto ldto = seurantaDao.resetoiEiValmiitHakukohteet(uuid, true);
            if (ldto == null) {
                LOG.error("Laskennan {} tila resetoitiin mutta ei saatu yhteenvetoa resetoinnista!", uuid);
            } else {
                seurantaSSEService.paivita(ldto.asYhteenveto());
            }
            return ldto;
        } catch (Exception e) {
            LOG.error("Seurantapalvelu epaonnistui resetoimaan laskennan uuid="+ uuid, e);
            throw e;
        }
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Yhteenvedot kaikista kaynnissa olevista laskennoista haulle", response = Collection.class)
    public Collection<YhteenvetoDto> haeKaynnissaOlevatLaskennat(String hakuOid) {
        return seurantaDao.haeKaynnissaOlevienYhteenvedotHaulle(hakuOid);
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Seuraavan työn alle otetun laskennan uuid", response = String.class)
    public Response otaSeuraavaLaskentaTyonAlle() {
        String uuid = seurantaDao.otaSeuraavaLaskentaTyonAlle();
        LOG.info("Ota seuraava tyon alle: " + (uuid != null ? uuid : "Ei tyota"));
        Response.ResponseBuilder response = uuid != null ? Response.ok(uuid) : Response.noContent();
        return response.build();
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Laskennan tiedot", response = Collection.class)
    public LaskentaDto laskenta(String uuid) {
        try {
            LaskentaDto l = seurantaDao.haeLaskenta(uuid);
            if (l == null) {
                LOG.error("SeurantaDao palautti null olion uuid:lle {}", uuid);
                throw new RuntimeException("SeurantaDao palautti null olion uuid:lle " + uuid);
            }
            return l;
        } catch (Exception e) {
            LOG.error("Ei saatu laskentaa uuid:lle " + uuid, e);
            throw e;
        }
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Laskennan tiedot", response = Collection.class)
    public Response lataa(String uuid) {
        LaskentaDto laskenta = seurantaDao.haeLaskenta(uuid);
        return Response
                .ok(laskenta)
                .header("Content-Disposition", "attachment; filename=laskenta_" + laskenta.getUuid() + ".json")
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Luo uuden laskennan", response = Response.class)
    public String luoLaskenta(String hakuOid, LaskentaTyyppi tyyppi, Boolean erillishaku, Integer valinnanvaihe,
            Boolean valintakoelaskenta, List<HakukohdeDto> hakukohdeOids) {
        if (hakukohdeOids == null) {
            LOG.error("Laskentaa ei luoda tyhjalle (null) hakukohdedto referenssille!");
            throw new NullPointerException("Laskentaa ei luoda tyhjalle (null) hakukohdedto referenssille!");
        }
        if (hakukohdeOids.isEmpty()) {
            LOG.error("Laskentaa ei luoda tyhjalle (koko on nolla) hakukohdedto joukolle!");
            throw new NullPointerException("Laskentaa ei luoda tyhjalle (koko on nolla) hakukohdedto joukolle!");
        }
        hakukohdeOids.forEach(hk -> {
            if (hk.getHakukohdeOid() == null || hk.getOrganisaatioOid() == null) {
                LOG.error("Laskentaa ei luoda hakukohdejoukkoobjektille koska joukossa oli hakukohde \r\n{}",
                        new GsonBuilder().setPrettyPrinting().create().toJson(hk));
                throw new NullPointerException("Laskentaa ei luoda hakukohdejoukkoobjektille koska joukossa oli null referensseja sisaltava hakukohde!");
            }
        });
        return seurantaDao.luoLaskenta(hakuOid, tyyppi, erillishaku, valinnanvaihe, valintakoelaskenta, hakukohdeOids);
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Paivittaa hakukohteen tilaa laskennassa", response = Response.class)
    public YhteenvetoDto merkkaaHakukohteenTila(String uuid, String hakukohdeOid, HakukohdeTila tila) {
        try {
            YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, hakukohdeOid, tila);
            if (y == null) {
                LOG.error("Seurantaan markattiin hakukohteen {} tila {} laskentaan {} mutta ei saatu yhteenvetoa lisayksesta!",
                        hakukohdeOid, tila, uuid);
            } else {
                seurantaSSEService.paivita(y);
            }
            return y;
        } catch (Exception e) {
            LOG.error("Tilan merkkauksessa tapahtui poikkeus. Kayttoliittymaa ei ehka paivitetty", e);
            throw e;
        }
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Paivittaa hakukohteen tilaa laskennassa", response = Response.class)
    public YhteenvetoDto lisaaIlmoitusHakukohteelle(String uuid, String hakukohdeOid, IlmoitusDto ilmoitus) {
        YhteenvetoDto y = seurantaDao.lisaaIlmoitus(uuid, hakukohdeOid, ilmoitus);
        if (y == null) {
            LOG.error("Seurantaan lisattiin ilmoitus laskentaan {} hakukohteelle {} mutta ei saatu yhteenvetoa lisayksesta!",
                    uuid, hakukohdeOid);
        } else {
            seurantaSSEService.paivita(y);
        }
        return y;
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Paivittaa hakukohteen tilaa laskennassa", response = Response.class)
    public YhteenvetoDto merkkaaHakukohteenTila(String uuid, String hakukohdeOid, HakukohdeTila tila, IlmoitusDto ilmoitus) {
        YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, hakukohdeOid, tila, ilmoitus);
        if (y == null) {
            LOG.error("Seurantaan paivitettiin laskennan {} tila {} hakukohteelle {} mutta ei saatu yhteenvetoa lisayksesta!",
                    uuid, tila, hakukohdeOid);
        } else {
            seurantaSSEService.paivita(y);
        }
        return y;
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Paivittaa laskennan tilaa", response = Response.class)
    public YhteenvetoDto merkkaaLaskennanTila(String uuid, fi.vm.sade.valinta.seuranta.dto.LaskentaTila tila) {
        YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, tila, Optional.empty());
        if (y == null) {
            LOG.error("Seurantaan paivitettiin laskennan {} tila {} mutta ei saatu yhteenvetoa lisayksesta!",
                    uuid, tila);
        } else {
            seurantaSSEService.paivita(y);
        }
        return y;
    }
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Paivittaa laskennan tilaa ja merkkaa ilmoituksen", response = Response.class)
    public YhteenvetoDto merkkaaLaskennanTila(String uuid, fi.vm.sade.valinta.seuranta.dto.LaskentaTila tila,
                                              IlmoitusDto ilmoitusDto) {
        YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, tila, Optional.ofNullable(ilmoitusDto));
        if (y == null) {
            LOG.error("Seurantaan paivitettiin laskennan {} tila {} mutta ei saatu yhteenvetoa lisayksesta!",
                    uuid, tila);
        } else {
            seurantaSSEService.paivita(y);
        }
        return y;
    }
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Paivittaa laskennan tilaa", response = Response.class)
    public YhteenvetoDto merkkaaLaskennanTila(String uuid, LaskentaTila tila, HakukohdeTila hakukohteentila) {
        YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, tila, hakukohteentila, Optional.empty());
        if (y == null) {
            LOG.error("Seurantaan paivitettiin laskennan {} tila {} mutta ei saatu yhteenvetoa lisayksesta!", uuid, tila);
        } else {
            seurantaSSEService.paivita(y);
        }
        return y;
    }
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Paivittaa laskennan tilaa ja merkkaa ilmoituksen", response = Response.class)
    public YhteenvetoDto merkkaaLaskennanTila(String uuid, LaskentaTila tila, HakukohdeTila hakukohteentila, IlmoitusDto ilmoitusDto) {
        YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, tila, hakukohteentila, Optional.ofNullable(ilmoitusDto));
        if (y == null) {
            LOG.error("Seurantaan paivitettiin laskennan {} tila {} mutta ei saatu yhteenvetoa lisayksesta!", uuid, tila);
        } else {
            seurantaSSEService.paivita(y);
        }
        return y;
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Poistaa laskennan", response = Response.class)
    public Response poistaLaskenta(String uuid) {
        seurantaDao.poistaLaskenta(uuid);
        return Response.ok().build();
    }
}
