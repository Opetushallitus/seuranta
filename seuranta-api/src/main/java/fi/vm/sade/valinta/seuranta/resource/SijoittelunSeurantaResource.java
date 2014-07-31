package fi.vm.sade.valinta.seuranta.resource;

import java.util.Collection;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.valinta.seuranta.dto.LaskentaTila;
import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;
import fi.vm.sade.valinta.seuranta.sijoittelu.dto.SijoitteluDto;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
@Path("sijoittelunseuranta")
public interface SijoittelunSeurantaResource {

	/**
	 * Sijoittelun tila haulle
	 * 
	 * @param hakuOid
	 * @return
	 */
	@GET
	@Path("/hae/{hakuOid}")
	@Produces(MediaType.APPLICATION_JSON)
	SijoitteluDto hae(@PathParam("hakuOid") String hakuOid);
	
	/**
	 * Kantaan tallennetut sijoittelut
	 * 
	 * @param hakuOid
	 * @return
	 */
	@GET
	@Path("/hae")
	@Produces(MediaType.APPLICATION_JSON)
	Collection<SijoitteluDto> hae();
	
	/**
	 * Paivittaa sijoittelun tilan
	 * 
	 * @param hakuOid
	 * @return
	 */
	@PUT
	@Path("/sijoittelu/{hakuOid}/ajossa/{ajossa}")
	@Produces(MediaType.APPLICATION_JSON)
	SijoitteluDto merkkaaSijoittelunAjossaTila(@PathParam("hakuOid") String hakuOid, @PathParam("tila") boolean ajossa);
	
	/**
	 * Paivittaa sijoittelun viimeksiajettupaivamaaran tamanhetkiseksi aikaleimaksi
	 * 
	 * @param hakuOid
	 * @return
	 */
	@PUT
	@Path("/sijoittelu/{hakuOid}")
	@Produces(MediaType.APPLICATION_JSON)
	SijoitteluDto merkkaaSijoittelunAjetuksi(@PathParam("hakuOid") String hakuOid);
	
	/**
	 * Poistaa laskennan
	 * 
	 * @param hakuOid
	 * @return 200 OK jos onnistui
	 */
	@DELETE
	@Path("/sijoittelu/{hakuOid}")
	Response poistaSijoittelu(@PathParam("hakuOid") String hakuOid);
}
