package fi.vm.sade.valinta.seuranta.resource.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.google.gson.GsonBuilder;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import fi.vm.sade.valinta.seuranta.dao.SeurantaDao;
import fi.vm.sade.valinta.seuranta.dto.HakukohdeTila;
import fi.vm.sade.valinta.seuranta.dto.IlmoitusDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTyyppi;
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

	private final static Logger LOG = LoggerFactory
			.getLogger(LaskennanSeurantaResourceImpl.class);

	@Autowired
	private SeurantaDao seurantaDao;

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Yhteenvedot kaikista hakuun tehdyista laskennoista", response = Collection.class)
	public Collection<YhteenvetoDto> hae(String hakuOid) {
		return seurantaDao.haeYhteenvedotHaulle(hakuOid);
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Yhteenvedot kaikista hakuun tehdyista laskennoista", response = Collection.class)
	public Collection<YhteenvetoDto> hae(String hakuOid, LaskentaTyyppi tyyppi) {
		// TODO Auto-generated method stub
		return seurantaDao.haeYhteenvedotHaulle(hakuOid, tyyppi);
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Yhteenvedot kaikista hakuun tehdyista laskennoista", response = Collection.class)
	public String resetoiTilat(String uuid) {
		try {
			LaskentaDto ldto = seurantaDao.resetoiEiValmiitHakukohteet(uuid,
					true);
			// LOG.error("LDTO\r\n{}", );
			return new GsonBuilder().create().toJson(ldto);
		} catch (Exception e) {
			LOG.error(
					"Seurantapalvelu epaonnistui resetoimaan laskennan {}. Virhe {}\r\n{}",
					uuid, e.getMessage(), Arrays.toString(e.getStackTrace()));
			throw e;
		}
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Yhteenveto laskennasta", response = Collection.class)
	public YhteenvetoDto yhteenveto(String uuid) {
		return seurantaDao.haeYhteenveto(uuid);
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
	@ApiOperation(value = "Laskennan tiedot", response = Collection.class)
	public Response lataa(String uuid) {
		LaskentaDto laskenta = seurantaDao.haeLaskenta(uuid);
		return Response
				.ok(laskenta)
				.header("Content-Disposition",
						"attachment; filename=laskenta_" + laskenta.getUuid()
								+ ".json").build();
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Luo uuden laskennan", response = Response.class)
	public String luoLaskenta(String hakuOid, LaskentaTyyppi tyyppi,
			List<String> hakukohdeOids) {
		return seurantaDao.luoLaskenta(hakuOid, tyyppi, hakukohdeOids);
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Paivittaa hakukohteen tilaa laskennassa", response = Response.class)
	public Response merkkaaHakukohteenTila(String uuid, String hakukohdeOid,
			HakukohdeTila tila) {
		seurantaDao.merkkaaTila(uuid, hakukohdeOid, tila);
		return Response.ok().build();
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Paivittaa hakukohteen tilaa laskennassa", response = Response.class)
	public Response lisaaIlmoitusHakukohteelle(String uuid,
			String hakukohdeOid, IlmoitusDto ilmoitus) {
		seurantaDao.lisaaIlmoitus(uuid, hakukohdeOid, ilmoitus);
		return Response.ok().build();
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Paivittaa hakukohteen tilaa laskennassa", response = Response.class)
	public Response merkkaaHakukohteenTila(String uuid, String hakukohdeOid,
			HakukohdeTila tila, IlmoitusDto ilmoitus) {
		seurantaDao.merkkaaTila(uuid, hakukohdeOid, tila, ilmoitus);
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
