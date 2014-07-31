package fi.vm.sade.valinta.seuranta.resource.impl;

import java.util.Collection;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import fi.vm.sade.valinta.seuranta.dao.SijoittelunSeurantaDao;
import fi.vm.sade.valinta.seuranta.resource.SijoittelunSeurantaResource;
import fi.vm.sade.valinta.seuranta.sijoittelu.dto.SijoitteluDto;

@Api(value = "/sijoittelunseuranta", description = "Sijoittelun seurantapalvelun rajapinta")
@Component
public class SijoittelunSeurantaResourceImpl implements SijoittelunSeurantaResource {

	@Autowired
	private SijoittelunSeurantaDao sijoittelunSeurantaDao;
	
	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Palauttaa kaikki kantaan tallennetut sijoittelutilat", response = Collection.class)
	public Collection<SijoitteluDto> hae() {
		return null;
	}
	
	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Palauttaa haun sijoittelun tilan", response = Collection.class)
	public SijoitteluDto hae(String hakuOid) {
		return null;
	}
	
	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Merkkaa sijoittelun ajetuksi haulle", response = Collection.class)
	public SijoitteluDto merkkaaSijoittelunAjetuksi(String hakuOid) {
		return null;
	}
	
	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Asettaa jatkuvan sijoittelun haulle", response = Collection.class)
	public SijoitteluDto merkkaaSijoittelunAjossaTila(String hakuOid, boolean ajossa) {
		return null;
	}
	
	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Poistaa hakuun liittyvat sijoittelutilat kannasta", response = Collection.class)
	public Response poistaSijoittelu(String hakuOid) {
		return null;
	}
	
}
