package fi.vm.sade.valinta.seuranta.resource;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.valinta.seuranta.dto.HakukohdeTila;
import fi.vm.sade.valinta.seuranta.dto.IlmoitusDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTila;
import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
@Path("seuranta")
public interface SeurantaResource {

	/**
	 * Yhteenvedot olemassa olevista laskennoista haulle
	 * 
	 * @param hakuOid
	 * @return
	 */
	@GET
	@Path("/hae/{hakuOid}")
	@Produces(MediaType.APPLICATION_JSON)
	Collection<YhteenvetoDto> hae(@PathParam("hakuOid") String hakuOid);

	/**
	 * Yhteenvedot olemassa olevista laskennoista haulle
	 * 
	 * @param hakuOid
	 * @return
	 */
	@GET
	@Path("/hae/{hakuOid}/kaynnissa")
	@Produces(MediaType.APPLICATION_JSON)
	Collection<YhteenvetoDto> haeKaynnissaOlevatLaskennat(
			@PathParam("hakuOid") String hakuOid);

	/**
	 * Kaikki yksityiskohdat
	 * 
	 * @param hakuOid
	 * @return
	 */
	@GET
	@Path("/laskenta/{uuid}")
	@Produces(MediaType.APPLICATION_JSON)
	LaskentaDto laskenta(@PathParam("uuid") String uuid);

	/**
	 * Kaikki yksityiskohdat
	 * 
	 * @param hakuOid
	 * @return
	 */
	@GET
	@Path("/lataa/{uuid}")
	@Produces(MediaType.APPLICATION_JSON)
	Response lataa(@PathParam("uuid") String uuid);

	/**
	 * Kaikki yksityiskohdat
	 * 
	 * @param hakuOid
	 * @return
	 */
	@GET
	@Path("/yhteenveto/{uuid}")
	@Produces(MediaType.APPLICATION_JSON)
	YhteenvetoDto yhteenveto(@PathParam("uuid") String uuid);

	/**
	 * Paivittaa yksittaisen hakukohteen tilaa laskennassa
	 * 
	 * @param hakuOid
	 * @param hakukohdeOid
	 * @return
	 */
	@PUT
	@Path("/laskenta/{uuid}/hakukohde/{hakukohdeOid}/tila/{tila}")
	@Produces(MediaType.APPLICATION_JSON)
	Response merkkaaHakukohteenTila(@PathParam("uuid") String uuid,
			@PathParam("hakukohdeOid") String hakukohdeOid,
			@PathParam("tila") HakukohdeTila tila);

	/**
	 * Paivittaa yksittaisen hakukohteen tilaa laskennassa ja jattaa ilmoituksen
	 * 
	 * @param hakuOid
	 * @param hakukohdeOid
	 * @return
	 */
	@POST
	@Path("/laskenta/{uuid}/hakukohde/{hakukohdeOid}/tila/{tila}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Response merkkaaHakukohteenTila(@PathParam("uuid") String uuid,
			@PathParam("hakukohdeOid") String hakukohdeOid,
			@PathParam("tila") HakukohdeTila tila, IlmoitusDto ilmoitus);

	/**
	 * Jattaa ilmoituksen
	 * 
	 * @param hakuOid
	 * @param hakukohdeOid
	 * @return
	 */
	@POST
	@Path("/laskenta/{uuid}/hakukohde/{hakukohdeOid}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Response lisaaIlmoitusHakukohteelle(@PathParam("uuid") String uuid,
			@PathParam("hakukohdeOid") String hakukohdeOid, IlmoitusDto ilmoitus);

	/**
	 * Resetoi hakukohteiden tilat. Poistaa logit. Sailoo valmiit tilat.
	 * 
	 * @param hakuOid
	 * @param hakukohdeOid
	 * @return
	 */
	@PUT
	@Path("/laskenta/{uuid}/resetoi")
	@Produces(MediaType.APPLICATION_JSON)
	String resetoiTilat(@PathParam("uuid") String uuid);

	/**
	 * Paivittaa laskennan tilan
	 * 
	 * @param hakuOid
	 * @param hakukohdeOid
	 * @return
	 */
	@PUT
	@Path("/laskenta/{uuid}/tila/{tila}")
	@Produces(MediaType.APPLICATION_JSON)
	Response merkkaaLaskennanTila(@PathParam("uuid") String uuid,
			@PathParam("tila") LaskentaTila tila);

	/**
	 * Luo uuden laskennan seurantaan
	 * 
	 * @param hakuOid
	 * @param hakukohdeOids
	 * @return 200 OK jos onnistui
	 */
	@POST
	@Path("/laskenta/{hakuOid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	String luoLaskenta(@PathParam("hakuOid") String hakuOid,
			List<String> hakukohdeOids);

	/**
	 * Poistaa laskennan
	 * 
	 * @param hakuOid
	 * @return 200 OK jos onnistui
	 */
	@DELETE
	@Path("/laskenta/{uuid}")
	@Produces(MediaType.APPLICATION_JSON)
	Response poistaLaskenta(@PathParam("uuid") String uuid);
}
