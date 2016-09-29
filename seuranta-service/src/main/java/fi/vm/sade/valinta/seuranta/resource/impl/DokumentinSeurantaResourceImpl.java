package fi.vm.sade.valinta.seuranta.resource.impl;

import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import io.swagger.annotations.Api;
import fi.vm.sade.valinta.seuranta.dokumentti.dao.DokumenttiDao;
import fi.vm.sade.valinta.seuranta.dto.DokumenttiDto;
import fi.vm.sade.valinta.seuranta.dto.VirheilmoitusDto;

import static org.apache.commons.lang.StringUtils.*;

import fi.vm.sade.valinta.seuranta.resource.DokumentinSeurantaResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Api(value = "/dokumentinseuranta", description = "Dokumentinseurantarajapinta")
@Component
@Path("dokumentinseuranta")
public class DokumentinSeurantaResourceImpl implements DokumentinSeurantaResource {
    private static final Logger LOG = LoggerFactory.getLogger(DokumentinSeurantaResourceImpl.class);

    private final DokumenttiDao dokumenttiDao;

    @Autowired
    public DokumentinSeurantaResourceImpl(DokumenttiDao dokumenttiDao) {
        this.dokumenttiDao = dokumenttiDao;
    }

    @Override
    public Response dokumentti(String uuid) {
        try {
            if (trimToNull(uuid) == null) {
                LOG.error("Uuid({}) ei saa olla tyhjä!", uuid);
                throw new RuntimeException("Uuid ei saa olla tyhjä!");
            }
            return Response.ok(dokumenttiDao.haeDokumentti(uuid)).build();
        } catch (Throwable t) {
            LOG.error("Poikkeus dokumentinseurannassa dokumenttia luettauessa uuid=" + uuid, t);
            return Response.serverError().entity(t.getMessage()).build();
        }
    }

    @Override
    public Response dokumentinTunniste(String uuid, String dokumenttiId) {
        try {
            if (trimToNull(dokumenttiId) == null || trimToNull(uuid) == null) {
                LOG.error("Uuid({}) tai dokumenttiId({}) ei saa olla tyhjä!", uuid, dokumenttiId);
                throw new RuntimeException("Uuid tai kuvaus ei saa olla tyhjä!");
            }
            DokumenttiDto dokkari = dokumenttiDao.paivitaDokumenttiId(uuid, dokumenttiId);
            return Response.ok(dokkari).build();
        } catch (Throwable t) {
            LOG.error("Poikkeus dokumentinseurannassa dokumenttiId:tä(" + dokumenttiId + ") paivitettaessa uuid:lle " +  uuid, t);
            return Response.serverError().entity(t.getMessage()).build();
        }
    }

    @Override
    public Response luoDokumentti(String kuvaus) {
        try {
            if (trimToNull(kuvaus) == null) {
                LOG.error("Kuvaus({}) ei saa olla tyhjä!", kuvaus);
                throw new RuntimeException("Kuvaus ei saa olla tyhjä!");
            }
            return Response.ok(dokumenttiDao.luoDokumentti(kuvaus)).build();
        } catch (Throwable t) {
            LOG.error("Poikkeus dokumentinseurannassa uutta dokumenttia luotaessa kuvauksella " + kuvaus, t);
            return Response.serverError().entity(t.getMessage()).build();
        }
    }

    @Override
    public Response paivitaKuvaus(String uuid, String kuvaus) {
        try {
            if (trimToNull(kuvaus) == null || trimToNull(uuid) == null) {
                LOG.error("Uuid({}) tai kuvaus({}) ei saa olla tyhjä!", uuid, kuvaus);
                throw new RuntimeException("Uuid tai kuvaus ei saa olla tyhjä!");
            }

            DokumenttiDto dokkari = dokumenttiDao.paivitaKuvaus(uuid, kuvaus);
            return Response.ok(dokkari).build();
        } catch (Throwable t) {
            LOG.error("Poikkeus dokumentinseurannassa kuvausta " + kuvaus + " paivitettaessa uuid:lle " + uuid, t);
            return Response.serverError().entity(t.getMessage()).build();
        }
    }

    @Override
    public Response lisaaVirheita(String uuid, List<VirheilmoitusDto> virheita) {
        try {
            if (virheita == null || virheita.isEmpty() || trimToNull(uuid) == null) {
                LOG.error("Uuid({}) tai virheet({}) ei saa olla tyhjä!", uuid, virheita);
                throw new RuntimeException("Uuid tai virheet ei saa olla tyhjä!");
            }
            DokumenttiDto dokkari = dokumenttiDao.lisaaVirheita(uuid, virheita);
            return Response.ok(dokkari).build();
        } catch (Throwable t) {
            LOG.error("Poikkeus dokumentinseurannassa virhetiloja lisattaessa!", t);
            return Response.serverError().entity(t.getMessage()).build();
        }
    }

    @Override
    public Response poista(String uuid) {
        try {
            if (trimToNull(uuid) == null) {
                LOG.error("Uuid({}) ei saa olla tyhjä!", uuid);
                throw new RuntimeException("Uuid ei saa olla tyhjä!");
            }
            dokumenttiDao.poistaDokumentti(uuid);
            return Response.ok(uuid).build();
        } catch (Throwable t) {
            LOG.error("Poistaminen epäonnistui virheeseen!", t);
            return Response.serverError().entity(t.getMessage()).build();
        }
    }
}
