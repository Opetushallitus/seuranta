package fi.vm.sade.valinta.seuranta.resource.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import fi.vm.sade.valinta.seuranta.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.GsonBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import fi.vm.sade.valinta.seuranta.laskenta.dao.SeurantaDao;
import fi.vm.sade.valinta.seuranta.resource.LaskentaSeurantaResource;

import static org.apache.commons.lang.StringUtils.*;

@Api(value = "/seuranta", description = "Seurantapalvelun rajapinta")
@Component
public class LaskennanSeurantaResourceImpl implements LaskentaSeurantaResource {
    private final static Logger LOG = LoggerFactory.getLogger(LaskennanSeurantaResourceImpl.class);

    private SeurantaDao seurantaDao;

    @Autowired
    public LaskennanSeurantaResourceImpl(SeurantaDao seurantaDao) {
        this.seurantaDao = seurantaDao;
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

    @ApiOperation(value = "Yhteenveto laskennasta", response = Collection.class)
    public YhteenvetoDto yhteenveto(String uuid) {
        return seurantaDao.haeYhteenveto(uuid);
    }

    @ApiOperation(value = "Yhteenvedot kaikista hakuun tehdyista laskennoista", response = Collection.class)
    public Collection<YhteenvetoDto> hae(String hakuOid) {
        return seurantaDao.haeYhteenvedotHaulle(hakuOid);
    }

    @ApiOperation(value = "Yhteenvedot kaikista hakuun tehdyista laskennoista", response = Collection.class)
    public Collection<YhteenvetoDto> hae(String hakuOid, LaskentaTyyppi tyyppi) {
        return seurantaDao.haeYhteenvedotHaulle(hakuOid, tyyppi);
    }

    @ApiOperation(value = "Yhteenvedot kaikista hakuun tehdyista laskennoista", response = Collection.class)
    public LaskentaDto resetoiTilat(String uuid) {
        try {
            LaskentaDto ldto = seurantaDao.resetoiEiValmiitHakukohteet(uuid, true);
            if (ldto == null) {
                LOG.error("Laskennan {} tila resetoitiin mutta ei saatu yhteenvetoa resetoinnista!", uuid);
            }
            return ldto;
        } catch (Exception e) {
            LOG.error("Seurantapalvelu epaonnistui resetoimaan laskennan uuid="+ uuid, e);
            throw e;
        }
    }

    @ApiOperation(value = "Yhteenvedot kaikista kaynnissa olevista laskennoista haulle", response = Collection.class)
    public Collection<YhteenvetoDto> haeKaynnissaOlevatLaskennat(String hakuOid) {
        return seurantaDao.haeKaynnissaOlevienYhteenvedotHaulle(hakuOid);
    }

    @ApiOperation(value = "Yhteenvedot kaikista kaynnissa olevista laskennoista haulle", response = Collection.class)
    public Collection<YhteenvetoDto> haeYhteenvetoKaikilleLaskennoille() {
        return seurantaDao.haeYhteenvetoKaikilleLaskennoille();
    }

    @ApiOperation(value = "Seuraavan työn alle otetun laskennan uuid", response = String.class)
    public Response otaSeuraavaLaskentaTyonAlle() {
        Optional<String> uuid = Optional.ofNullable(seurantaDao.otaSeuraavaLaskentaTyonAlle());
        LOG.info("Ota seuraava tyon alle: " + (uuid.isPresent() ? uuid.get() : "Ei tyota"));
        if(uuid.isPresent()) {
            final String u = uuid.get();
            return Response.ok(u).build();
        } else {
            return Response.noContent().build();
        }

    }

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

    @ApiOperation(value = "Laskennan tiedot", response = Collection.class)
    public Response lataa(String uuid) {
        LaskentaDto laskenta = seurantaDao.haeLaskenta(uuid);
        return Response
                .ok(laskenta)
                .header("Content-Disposition", "attachment; filename=laskenta_" + laskenta.getUuid() + ".json")
                .build();
    }

    @ApiOperation(value = "Luo uuden laskennan", response = Response.class)
    public TunnisteDto luoLaskenta(String hakuOid, LaskentaTyyppi tyyppi, String userOID, String haunnimi, String nimi, Boolean erillishaku, Integer valinnanvaihe,
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
        return seurantaDao.luoLaskenta(userOID, haunnimi, nimi, hakuOid, tyyppi, erillishaku, valinnanvaihe, valintakoelaskenta, hakukohdeOids);
    }

    @ApiOperation(value = "Paivittaa hakukohteen tilaa laskennassa", response = Response.class)
    public YhteenvetoDto merkkaaHakukohteenTila(String uuid, String hakukohdeOid, HakukohdeTila tila) {
        try {
            YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, hakukohdeOid, tila);
            if (y == null) {
                LOG.error("Seurantaan markattiin hakukohteen {} tila {} laskentaan {} mutta ei saatu yhteenvetoa lisayksesta!",
                        hakukohdeOid, tila, uuid);
            }
            return y;
        } catch (Exception e) {
            LOG.error("Tilan merkkauksessa tapahtui poikkeus. Kayttoliittymaa ei ehka paivitetty", e);
            throw e;
        }
    }

    @ApiOperation(value = "Paivittaa hakukohteen tilaa laskennassa", response = Response.class)
    public YhteenvetoDto lisaaIlmoitusHakukohteelle(String uuid, String hakukohdeOid, IlmoitusDto ilmoitus) {
        YhteenvetoDto y = seurantaDao.lisaaIlmoitus(uuid, hakukohdeOid, ilmoitus);
        if (y == null) {
            LOG.error("Seurantaan lisattiin ilmoitus laskentaan {} hakukohteelle {} mutta ei saatu yhteenvetoa lisayksesta!",
                    uuid, hakukohdeOid);
        }
        return y;
    }

    @ApiOperation(value = "Paivittaa hakukohteen tilaa laskennassa", response = Response.class)
    public YhteenvetoDto merkkaaHakukohteenTila(String uuid, String hakukohdeOid, HakukohdeTila tila, IlmoitusDto ilmoitus) {
        YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, hakukohdeOid, tila, ilmoitus);
        if (y == null) {
            LOG.error("Seurantaan paivitettiin laskennan {} tila {} hakukohteelle {} mutta ei saatu yhteenvetoa lisayksesta!",
                    uuid, tila, hakukohdeOid);
        }
        return y;
    }

    @ApiOperation(value = "Paivittaa laskennan tilaa", response = Response.class)
    public YhteenvetoDto merkkaaLaskennanTila(String uuid, fi.vm.sade.valinta.seuranta.dto.LaskentaTila tila) {
        YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, tila, Optional.empty());
        if (y == null) {
            LOG.error("Seurantaan paivitettiin laskennan {} tila {} mutta ei saatu yhteenvetoa lisayksesta!",
                    uuid, tila);
        }
        return y;
    }

    @ApiOperation(value = "Paivittaa laskennan tilaa ja merkkaa ilmoituksen", response = Response.class)
    public YhteenvetoDto merkkaaLaskennanTila(String uuid, fi.vm.sade.valinta.seuranta.dto.LaskentaTila tila,
                                              IlmoitusDto ilmoitusDto) {
        YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, tila, Optional.ofNullable(ilmoitusDto));
        if (y == null) {
            LOG.error("Seurantaan paivitettiin laskennan {} tila {} mutta ei saatu yhteenvetoa lisayksesta!",
                    uuid, tila);
        }
        return y;
    }

    @ApiOperation(value = "Paivittaa laskennan tilaa", response = Response.class)
    public YhteenvetoDto merkkaaLaskennanTila(String uuid, LaskentaTila tila, HakukohdeTila hakukohteentila) {
        YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, tila, hakukohteentila, Optional.empty());
        if (y == null) {
            LOG.error("Seurantaan paivitettiin laskennan {} tila {} mutta ei saatu yhteenvetoa lisayksesta!", uuid, tila);
        }
        return y;
    }

    @ApiOperation(value = "Paivittaa laskennan tilaa ja merkkaa ilmoituksen", response = Response.class)
    public YhteenvetoDto merkkaaLaskennanTila(String uuid, LaskentaTila tila, HakukohdeTila hakukohteentila, IlmoitusDto ilmoitusDto) {
        YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, tila, hakukohteentila, Optional.ofNullable(ilmoitusDto));
        if (y == null) {
            LOG.error("Seurantaan paivitettiin laskennan {} tila {} mutta ei saatu yhteenvetoa lisayksesta!", uuid, tila);
        }
        return y;
    }

    @ApiOperation(value = "Poistaa laskennan", response = Response.class)
    public Response poistaLaskenta(String uuid) {
        seurantaDao.poistaLaskenta(uuid);
        return Response.ok().build();
    }
}
