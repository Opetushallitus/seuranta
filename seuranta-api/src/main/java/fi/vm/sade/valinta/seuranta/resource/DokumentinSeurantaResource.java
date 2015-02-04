package fi.vm.sade.valinta.seuranta.resource;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.valinta.seuranta.dto.DokumenttiDto;
import fi.vm.sade.valinta.seuranta.dto.VirheilmoitusDto;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.SseFeature;

import fi.vm.sade.valinta.seuranta.dto.LaskentaTyyppi;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
@Path("dokumentinseuranta")
public interface DokumentinSeurantaResource {
	/**
	 * Kaikki yksityiskohdat
	 *
	 * @return
	 */
	@GET
	@Path("/{uuid}/sse")
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	EventOutput dokumenttiSSE(@PathParam("uuid") String uuid);
	/**
	 * Kaikki yksityiskohdat
	 *
	 * @return
	 */
	@GET
	@Path("/{uuid}")
	@Produces(MediaType.APPLICATION_JSON)
	Response dokumentti(@PathParam("uuid") String uuid);
	/**
	 * Luo uuden dokumentin seurantaan
	 *
	 * @return 200 OK jos onnistui
	 */
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	Response luoDokumentti(String kuvaus);

	/**
	 * Paivita kuvaus
	 *
	 * @return 200 OK jos onnistui
	 */
	@POST
	@Path("/{uuid}/paivita_kuvaus")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	Response paivitaKuvaus(@PathParam("uuid") String uuid, String kuvaus);

	/**
	 * Paivita kuvaus
	 *
	 * @return 200 OK jos onnistui
	 */
	@POST
	@Path("/{uuid}/paivita_dokumenttiId")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	Response dokumentinTunniste(@PathParam("uuid") String uuid, String dokumenttiId);

	/**
	 * Lisaa virheita
	 *
	 * @return 200 OK jos onnistui
	 */
	@POST
	@Path("/{uuid}/lisaa_virheita")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response lisaaVirheita(@PathParam("uuid") String uuid, List<VirheilmoitusDto> virheita);


	/**
	 * Luo uuden dokumentin seurantaan
	 *
	 * @return 200 OK jos onnistui
	 */
	@POST
	@Path("/{uuid}/poista")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	Response poista(@PathParam("uuid") String uuid);

}
