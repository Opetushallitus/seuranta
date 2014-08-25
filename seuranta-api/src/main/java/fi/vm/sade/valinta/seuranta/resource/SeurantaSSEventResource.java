package fi.vm.sade.valinta.seuranta.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.SseFeature;

public interface SeurantaSSEventResource {
	/**
	 * Kaikki yksityiskohdat
	 * 
	 * @param hakuOid
	 * @return
	 */
	@GET
	@Path("/yhteenveto/{uuid}/sse")
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	EventOutput yhteenvetoSSE(@PathParam("uuid") String uuid);
}
