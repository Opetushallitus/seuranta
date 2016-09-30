package fi.vm.sade.valinta.seuranta.resource.impl;

import fi.vm.sade.valinta.seuranta.resource.SijoittelunSeurantaResource;
import fi.vm.sade.valinta.seuranta.sijoittelu.dao.SijoittelunSeurantaDao;
import fi.vm.sade.valinta.seuranta.sijoittelu.dto.SijoitteluDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Date;

@PreAuthorize("isAuthenticated()")
@Component
@Path("sijoittelunseuranta")
@Api(value = "/sijoittelunseuranta", description = "Sijoittelun seurantapalvelun rajapinta")
public class SijoittelunSeurantaResourceImpl implements SijoittelunSeurantaResource {
    private static final Logger LOG = LoggerFactory.getLogger(SijoittelunSeurantaResourceImpl.class);

    @Autowired
    private SijoittelunSeurantaDao sijoittelunSeurantaDao;

    @ApiOperation(value = "Palauttaa kaikki kantaan tallennetut sijoittelutilat", response = Collection.class)
    public Collection<SijoitteluDto> hae() {
        return sijoittelunSeurantaDao.hae();
    }

    @ApiOperation(value = "Palauttaa haun sijoittelun tilan", response = Collection.class)
    public SijoitteluDto hae(String hakuOid) {
        return sijoittelunSeurantaDao.hae(hakuOid);
    }

    @ApiOperation(value = "Merkkaa sijoittelun ajetuksi haulle", response = Collection.class)
    public SijoitteluDto merkkaaSijoittelunAjetuksi(String hakuOid) {
        return sijoittelunSeurantaDao.paivitaViimeksiAjettuPaivamaara(hakuOid);
    }

    @ApiOperation(value = "Asettaa jatkuvan sijoittelun haulle", response = Collection.class)
    public SijoitteluDto merkkaaSijoittelunAjossaTila(String hakuOid, boolean tila) {
        return sijoittelunSeurantaDao.asetaJatkuvaSijoittelu(hakuOid, tila);
    }

    @ApiOperation(value = "Asettaa jatkuvan sijoittelulle virheen", response = Collection.class)
    public SijoitteluDto merkkaaSijoittelunAjossaVirhe(String hakuOid, String virhe) {
        return sijoittelunSeurantaDao.asetaSijoitteluVirhe(hakuOid, virhe);
    }

    @ApiOperation(value = "Poistaa hakuun liittyvat sijoittelutilat kannasta", response = Collection.class)
    public Response poistaSijoittelu(String hakuOid) {
        sijoittelunSeurantaDao.poistaSijoittelu(hakuOid);
        return Response.ok().build();
    }

    @ApiOperation(value = "Päivittää sijoittelun aloitusajankohdan", response = Response.class)
    public Response paivitaSijoittelunAloitusajankohta(String hakuOid, Long aloitusajankohta, Integer ajotiheys) {
        sijoittelunSeurantaDao.paivitaAloitusajankohta(hakuOid, new Date(aloitusajankohta), ajotiheys);
        return Response.ok().build();
    }
}
