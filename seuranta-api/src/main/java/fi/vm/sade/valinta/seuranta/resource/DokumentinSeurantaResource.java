package fi.vm.sade.valinta.seuranta.resource;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
	 * @param hakuOid
	 * @return
	 */
	@GET
	@Path("/{uuid}/sse")
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	EventOutput yhteenvetoSSE(@PathParam("uuid") String uuid);

	/**
	 * Luo uuden dokumentin seurantaan
	 * 
	 * @param hakuOid
	 * @param hakukohdeOids
	 * @return 200 OK jos onnistui
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	String luoDokumentti(List<String> oids);

	/**
	 * Luo uuden dokumentin seurantaan
	 * 
	 * @param hakuOid
	 * @param hakukohdeOids
	 * @return 200 OK jos onnistui
	 */
	@POST
	@Path("/{uuid}/paivita/{oid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	Response paivitaOid(@PathParam("uuid") String uuid,
			@PathParam("oid") String oid, String json);
}
