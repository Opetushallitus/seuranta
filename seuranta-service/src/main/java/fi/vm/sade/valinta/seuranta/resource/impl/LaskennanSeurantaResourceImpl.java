package fi.vm.sade.valinta.seuranta.resource.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.media.sse.EventOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import fi.vm.sade.valinta.seuranta.dao.SeurantaDao;
import fi.vm.sade.valinta.seuranta.dto.HakukohdeTila;
import fi.vm.sade.valinta.seuranta.dto.IlmoitusDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTila;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTyyppi;
import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;
import fi.vm.sade.valinta.seuranta.resource.LaskentaSeurantaResource;
import fi.vm.sade.valinta.seuranta.service.SeurantaSSEService;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
@Api(value = "/seuranta", description = "Seurantapalvelun rajapinta")
@Component
public class LaskennanSeurantaResourceImpl implements LaskentaSeurantaResource {

	private final static Logger LOG = LoggerFactory
			.getLogger(LaskennanSeurantaResourceImpl.class);

	@Autowired
	private SeurantaDao seurantaDao;
	@Autowired
	private SeurantaSSEService seurantaSSEService;

	// @PreAuthorize("isAuthenticated()") ei tarvi, ei tarvisi muissakaan
	@ApiOperation(value = "SSE Yhteenvedot kaikista hakuun tehdyista laskennoista", response = Collection.class)
	public EventOutput yhteenvetoSSE(String uuid) {
		LOG.debug("REKISTEROIDAAN KUUNTELIJA {}", uuid);
		final EventOutput eventOutput = new EventOutput();
		try {
			seurantaSSEService.rekisteroi(uuid, eventOutput);
		} catch (Exception e) {
			LOG.error("Rekisterointi epaonnistui! {}: {}", uuid, e.getMessage());
		}
		try {
			YhteenvetoDto y = null;
			try {
				y = seurantaDao.haeYhteenveto(uuid);
			} catch (Exception e) {
				y = new YhteenvetoDto(uuid, StringUtils.EMPTY,
						new Date().getTime(), LaskentaTila.MENEILLAAN, 0, 0, 0);
			}
			seurantaSSEService.paivita(y);
		} catch (Exception e) {
			LOG.error(
					"Yhteenvetoa ei ole viela saatavilla {}. Ehka laskentaa ei ole ehditty viela muodostaa. {}",
					uuid, e.getMessage());
		}
		LOG.debug("REKISTEROITY {}", uuid);
		return eventOutput;
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
		// TODO Auto-generated method stub
		return seurantaDao.haeYhteenvedotHaulle(hakuOid, tyyppi);
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Yhteenvedot kaikista hakuun tehdyista laskennoista", response = Collection.class)
	public String resetoiTilat(String uuid) {
		try {
			LaskentaDto ldto = seurantaDao.resetoiEiValmiitHakukohteet(uuid,
					true);
			if (ldto == null) {
				LOG.error(
						"Laskennan {} tila resetoitiin mutta ei saatu yhteenvetoa resetoinnista!",
						uuid);
			} else {
				seurantaSSEService.paivita(ldto.asYhteenveto());
			}
			return new GsonBuilder().create().toJson(ldto);
		} catch (Exception e) {
			LOG.error(
					"Seurantapalvelu epaonnistui resetoimaan laskennan {}. Virhe {}\r\n{}",
					uuid, e.getMessage(), Arrays.toString(e.getStackTrace()));
			throw e;
		}
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Yhteenvedot kaikista kaynnissa olevista laskennoista haulle", response = Collection.class)
	public Collection<YhteenvetoDto> haeKaynnissaOlevatLaskennat(String hakuOid) {
		return seurantaDao.haeKaynnissaOlevienYhteenvedotHaulle(hakuOid);
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Laskennan tiedot", response = Collection.class)
	public String laskenta(String uuid) {
		try {
			LaskentaDto l = seurantaDao.haeLaskenta(uuid);
			if (l == null) {
				LOG.error("SeurantaDao palautti null olion uuid:lle {}", uuid);
				throw new RuntimeException(
						"SeurantaDao palautti null olion uuid:lle " + uuid);
			}
			return new Gson().toJson(l);
		} catch (Exception e) {
			LOG.error("Ei saatu laskentaa uuid:lle {}: {}", uuid,
					e.getMessage());
			throw e;
		}
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
			Integer valinnanvaihe, Boolean valintakoelaskenta,
			List<String> hakukohdeOids) {
		return seurantaDao.luoLaskenta(hakuOid, tyyppi, valinnanvaihe,
				valintakoelaskenta, hakukohdeOids);
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Paivittaa hakukohteen tilaa laskennassa", response = Response.class)
	public Response merkkaaHakukohteenTila(String uuid, String hakukohdeOid,
			HakukohdeTila tila) {
		try {
			YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, hakukohdeOid, tila);
			if (y == null) {
				LOG.error(
						"Seurantaan markattiin hakukohteen {} tila {} laskentaan {} mutta ei saatu yhteenvetoa lisayksesta!",
						hakukohdeOid, tila, uuid);
			} else {
				seurantaSSEService.paivita(y);
			}
			return Response.ok().build();
		} catch (Exception e) {
			LOG.error(
					"Tilan merkkauksessa tapahtui poikkeus {}. Kayttoliittymaa ei ehka paivitetty",
					e.getMessage());
		}
		return Response.serverError().build();
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Paivittaa hakukohteen tilaa laskennassa", response = Response.class)
	public Response lisaaIlmoitusHakukohteelle(String uuid,
			String hakukohdeOid, IlmoitusDto ilmoitus) {
		YhteenvetoDto y = seurantaDao.lisaaIlmoitus(uuid, hakukohdeOid,
				ilmoitus);
		if (y == null) {
			LOG.error(
					"Seurantaan lisattiin ilmoitus laskentaan {} hakukohteelle {} mutta ei saatu yhteenvetoa lisayksesta!",
					uuid, hakukohdeOid);
		} else {
			seurantaSSEService.paivita(y);
		}
		return Response.ok().build();
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Paivittaa hakukohteen tilaa laskennassa", response = Response.class)
	public Response merkkaaHakukohteenTila(String uuid, String hakukohdeOid,
			HakukohdeTila tila, IlmoitusDto ilmoitus) {
		YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, hakukohdeOid, tila,
				ilmoitus);
		if (y == null) {
			LOG.error(
					"Seurantaan paivitettiin laskennan {} tila {} hakukohteelle {} mutta ei saatu yhteenvetoa lisayksesta!",
					uuid, tila, hakukohdeOid);
		} else {
			seurantaSSEService.paivita(y);
		}
		return Response.ok().build();
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Paivittaa laskennan tilaa", response = Response.class)
	public Response merkkaaLaskennanTila(String uuid,
			fi.vm.sade.valinta.seuranta.dto.LaskentaTila tila) {
		YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, tila);
		if (y == null) {
			LOG.error(
					"Seurantaan paivitettiin laskennan {} tila {} mutta ei saatu yhteenvetoa lisayksesta!",
					uuid, tila);
		} else {
			seurantaSSEService.paivita(y);
		}

		return Response.ok().build();
	}

	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Poistaa laskennan", response = Response.class)
	public Response poistaLaskenta(String uuid) {
		seurantaDao.poistaLaskenta(uuid);
		// TODO: Ehka SSE paivitys, ei tosin oo ikina tapahtumassa
		return Response.ok().build();
	}

}
