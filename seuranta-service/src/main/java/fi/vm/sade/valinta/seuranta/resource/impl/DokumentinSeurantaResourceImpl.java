package fi.vm.sade.valinta.seuranta.resource.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import fi.vm.sade.valinta.seuranta.dokumentti.dao.DokumenttiDao;
import fi.vm.sade.valinta.seuranta.dto.DokumenttiDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTila;
import fi.vm.sade.valinta.seuranta.dto.VirheilmoitusDto;
import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;
import fi.vm.sade.valinta.seuranta.laskenta.service.DokumentinSeurantaSSEService;
import static org.apache.commons.lang.StringUtils.*;

import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.media.sse.EventOutput;

import fi.vm.sade.valinta.seuranta.resource.DokumentinSeurantaResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
@Api(value = "/dokumentinseuranta", description = "Dokumentinseurantarajapinta")
@Component
public class DokumentinSeurantaResourceImpl implements
		DokumentinSeurantaResource {
	private static final Logger LOG = LoggerFactory.getLogger(DokumentinSeurantaResourceImpl.class);

	private final DokumenttiDao dokumenttiDao;
	private final DokumentinSeurantaSSEService sseService;

	@Autowired
	public DokumentinSeurantaResourceImpl(
			DokumenttiDao dokumenttiDao,
			DokumentinSeurantaSSEService sseService
	) {
		this.dokumenttiDao = dokumenttiDao;
		this.sseService = sseService;
	}

	public Response dokumentti(@PathParam("uuid") String uuid) {
		try {
			if(trimToNull(uuid) == null) {
				LOG.error("Uuid({}) ei saa olla tyhjä!", uuid);
				throw new RuntimeException("Uuid ei saa olla tyhjä!");
			}
			return Response.ok(dokumenttiDao.haeDokumentti(uuid)).build();
		} catch (Throwable t) {
			LOG.error("Poikkeus dokumentinseurannassa dokumenttia luettauessa {}: {} {}", uuid, t.getMessage(), Arrays.toString(t.getStackTrace()));
			return Response.serverError().entity(t.getMessage()).build();
		}
	}

	@Override
	public Response dokumentinTunniste(String uuid, String dokumenttiId) {
		try {
			if(trimToNull(dokumenttiId) == null || trimToNull(uuid) == null) {
				LOG.error("Uuid({}) tai dokumenttiId({}) ei saa olla tyhjä!", uuid, dokumenttiId);
				throw new RuntimeException("Uuid tai kuvaus ei saa olla tyhjä!");
			}
			DokumenttiDto dokkari = dokumenttiDao.paivitaDokumenttiId(uuid, dokumenttiId);
			sseService.paivita(dokkari);
			return Response.ok(dokkari).build();
		} catch (Throwable t) {
			LOG.error("Poikkeus dokumentinseurannassa dokumenttiId:tä({}) paivitettaessa uuid:lle {}: {} {}", dokumenttiId, uuid, t.getMessage(), Arrays.toString(t.getStackTrace()));
			return Response.serverError().entity(t.getMessage()).build();
		}
	}

	@ApiOperation(value = "Dokumentti SSE-tapahtumana", response = DokumenttiDto.class)
	public EventOutput dokumenttiSSE(String uuid) {
		LOG.debug("REKISTEROIDAAN KUUNTELIJA {}", uuid);
		final EventOutput eventOutput = new EventOutput();
		try {
			sseService.rekisteroi(uuid, eventOutput);
		} catch (Exception e) {
			LOG.error("Rekisterointi epaonnistui! {}: {}", uuid, e.getMessage());
		}
		try {
			DokumenttiDto y = null;
			try {
				y = dokumenttiDao.haeDokumentti(uuid);
			} catch (Exception e) {
				LOG.error(
						"Dokumenttia ei ole viela saatavilla {}. Ehka seurantaoliota ei ole ehditty viela muodostaa. {}",
						uuid, e.getMessage());
			}
			sseService.paivita(y);
		} catch (Exception e) {
			LOG.error(
					"Dokumenttia ei ole viela saatavilla {}. Ehka seurantaoliota ei ole ehditty viela muodostaa. {}",
					uuid, e.getMessage());
		}
		LOG.debug("REKISTEROITY {}", uuid);
		return eventOutput;
	}

	@Override
	public Response luoDokumentti(String kuvaus) {
		try {
			if(trimToNull(kuvaus) == null) {
				LOG.error("Kuvaus({}) ei saa olla tyhjä!", kuvaus);
				throw new RuntimeException("Kuvaus ei saa olla tyhjä!");
			}
			return Response.ok(dokumenttiDao.luoDokumentti(kuvaus)).build();
		} catch (Throwable t) {
			LOG.error("Poikkeus dokumentinseurannassa uutta dokumenttia luotaessa kuvauksella {}: {} {}", kuvaus, t.getMessage(), Arrays.toString(t.getStackTrace()));
			return Response.serverError().entity(t.getMessage()).build();
		}
	}

	@Override
	public Response paivitaKuvaus(String uuid, String kuvaus) {
		try {
			if(trimToNull(kuvaus) == null || trimToNull(uuid) == null) {
				LOG.error("Uuid({}) tai kuvaus({}) ei saa olla tyhjä!", uuid, kuvaus);
				throw new RuntimeException("Uuid tai kuvaus ei saa olla tyhjä!");
			}

			DokumenttiDto dokkari = dokumenttiDao.paivitaKuvaus(uuid, kuvaus);
			sseService.paivita(dokkari);
			return Response.ok(dokkari).build();
		} catch (Throwable t) {
			LOG.error("Poikkeus dokumentinseurannassa kuvausta({}) paivitettaessa uuid:lle {}: {} {}", kuvaus, uuid, t.getMessage(), Arrays.toString(t.getStackTrace()));
			return Response.serverError().entity(t.getMessage()).build();
		}
	}

	@Override
	public Response lisaaVirheita(String uuid, List<VirheilmoitusDto> virheita) {
		try {
			if(virheita == null || virheita.isEmpty() || trimToNull(uuid) == null) {
				LOG.error("Uuid({}) tai virheet({}) ei saa olla tyhjä!", uuid, virheita);
				throw new RuntimeException("Uuid tai virheet ei saa olla tyhjä!");
			}
			DokumenttiDto dokkari = dokumenttiDao.lisaaVirheita(uuid, virheita);
			sseService.paivita(dokkari);
			return Response.ok(dokkari).build();
		} catch (Throwable t) {
			LOG.error("Poikkeus dokumentinseurannassa virhetiloja lisattaessa: {} {}", t.getMessage(), Arrays.toString(t.getStackTrace()));
			return Response.serverError().entity(t.getMessage()).build();
		}
	}

	@Override
	public Response poista(String uuid) {
		try {
			if(trimToNull(uuid) == null) {
				LOG.error("Uuid({}) ei saa olla tyhjä!", uuid);
				throw new RuntimeException("Uuid ei saa olla tyhjä!");
			}
			dokumenttiDao.poistaDokumentti(uuid);
			return Response.ok(uuid).build();
		} catch (Throwable t) {
			LOG.error("Poistaminen epäonnistui virheeseen: {} {}", t.getMessage(), Arrays.toString(t.getStackTrace()));
			return Response.serverError().entity(t.getMessage()).build();
		}
	}
}