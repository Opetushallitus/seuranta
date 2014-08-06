package fi.vm.sade.valinta.seuranta.resource.impl;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import fi.vm.sade.valinta.seuranta.dao.SeurantaDao;
import fi.vm.sade.valinta.seuranta.dto.HakukohdeTila;
import fi.vm.sade.valinta.seuranta.dto.LaskentaDto;
import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;
import fi.vm.sade.valinta.seuranta.resource.SeurantaResource;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
@Api(value = "/seuranta", description = "Seurantapalvelun rajapinta")
@Component
public class LaskennanSeurantaResourceImpl implements SeurantaResource {

	@Autowired
	private SeurantaDao seurantaDao;

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Yhteenvedot kaikista hakuun tehdyista laskennoista", response = Collection.class)
	public Collection<YhteenvetoDto> hae(String hakuOid) {
		return seurantaDao.haeYhteenvedotHaulle(hakuOid);
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Yhteenvedot kaikista kaynnissa olevista laskennoista haulle", response = Collection.class)
	public Collection<YhteenvetoDto> haeKaynnissaOlevatLaskennat(String hakuOid) {
		return seurantaDao.haeKaynnissaOlevienYhteenvedotHaulle(hakuOid);
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Laskennan tiedot", response = Collection.class)
	public LaskentaDto laskenta(String uuid) {
		return seurantaDao.haeLaskenta(uuid);
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Luo uuden laskennan", response = Response.class)
	public String luoLaskenta(String hakuOid, List<String> hakukohdeOids) {
		return seurantaDao.luoLaskenta(hakuOid, hakukohdeOids);
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Paivittaa hakukohteen tilaa laskennassa", response = Response.class)
	public Response merkkaaHakukohteenTila(String uuid, String hakukohdeOid,
			HakukohdeTila tila) {
		seurantaDao.merkkaaTila(uuid, hakukohdeOid, tila);
		return Response.ok().build();
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Paivittaa laskennan tilaa", response = Response.class)
	public Response merkkaaLaskennanTila(String uuid,
			fi.vm.sade.valinta.seuranta.dto.LaskentaTila tila) {
		seurantaDao.merkkaaTila(uuid, tila);
		return Response.ok().build();
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Poistaa laskennan", response = Response.class)
	public Response poistaLaskenta(String uuid) {
		seurantaDao.poistaLaskenta(uuid);
		return Response.ok().build();
	}

}
