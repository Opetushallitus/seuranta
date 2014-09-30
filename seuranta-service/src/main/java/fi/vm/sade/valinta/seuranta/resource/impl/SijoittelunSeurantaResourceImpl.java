package fi.vm.sade.valinta.seuranta.resource.impl;

import java.util.Collection;
import java.util.Date;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import fi.vm.sade.valinta.seuranta.resource.SijoittelunSeurantaResource;
import fi.vm.sade.valinta.seuranta.sijoittelu.dao.SijoittelunSeurantaDao;
import fi.vm.sade.valinta.seuranta.sijoittelu.dto.SijoitteluDto;

@Api(value = "/sijoittelunseuranta", description = "Sijoittelun seurantapalvelun rajapinta")
@Component
public class SijoittelunSeurantaResourceImpl implements
		SijoittelunSeurantaResource {

	private static final Logger LOG = LoggerFactory
			.getLogger(SijoittelunSeurantaResourceImpl.class);
	@Autowired
	private SijoittelunSeurantaDao sijoittelunSeurantaDao;

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Palauttaa kaikki kantaan tallennetut sijoittelutilat", response = Collection.class)
	public Collection<SijoitteluDto> hae() {
		return sijoittelunSeurantaDao.hae();
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Palauttaa haun sijoittelun tilan", response = Collection.class)
	public SijoitteluDto hae(String hakuOid) {
		return sijoittelunSeurantaDao.hae(hakuOid);
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Merkkaa sijoittelun ajetuksi haulle", response = Collection.class)
	public SijoitteluDto merkkaaSijoittelunAjetuksi(String hakuOid) {
		return sijoittelunSeurantaDao.paivitaViimeksiAjettuPaivamaara(hakuOid);
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Asettaa jatkuvan sijoittelun haulle", response = Collection.class)
	public SijoitteluDto merkkaaSijoittelunAjossaTila(String hakuOid,
			boolean tila) {
		return sijoittelunSeurantaDao.asetaJatkuvaSijoittelu(hakuOid, tila);
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Asettaa jatkuvan sijoittelun haulle", response = Collection.class)
	public SijoitteluDto merkkaaSijoittelunAjossaVirhe(String hakuOid,
			String virhe) {
		LOG.error("Sijoittelun ajossa on virhe metodia ei ole implementoitu!");
		throw new RuntimeException("Yritettiin merkata haulle " + hakuOid
				+ " virhe " + virhe
				+ " mutta virheen merkkausta ei ole implementoitu seurantaan!");
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Poistaa hakuun liittyvat sijoittelutilat kannasta", response = Collection.class)
	public Response poistaSijoittelu(String hakuOid) {
		sijoittelunSeurantaDao.poistaSijoittelu(hakuOid);
		return Response.ok().build();
	}


    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Päivittää sijoittelun aloitusajankohdan", response = Response.class)
    public Response paivitaSijoittelunAloitusajankohta(String hakuOid, Long aloitusajankohta, Integer ajotiheys) {
        sijoittelunSeurantaDao.paivitaAloitusajankohta(hakuOid, new Date(aloitusajankohta), ajotiheys);
        return Response.ok().build();
    }
}
