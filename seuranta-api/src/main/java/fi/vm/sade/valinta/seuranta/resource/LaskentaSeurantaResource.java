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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.valinta.seuranta.dto.*;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.SseFeature;

@Path("seuranta")
public interface LaskentaSeurantaResource {

    /**
     * Kaikki yksityiskohdat
     */
    @GET
    @Path("/yhteenveto/{uuid}/sse")
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    EventOutput yhteenvetoSSE(@PathParam("uuid") String uuid);

    /**
     * Yhteenvedot olemassa olevista laskennoista haulle
     */
    @GET
    @Path("/hae/{hakuOid}")
    @Produces(MediaType.APPLICATION_JSON)
    Collection<YhteenvetoDto> hae(@PathParam("hakuOid") String hakuOid);

    /**
     * Yhteenvedot olemassa olevista tietyn tyyppisista laskennoista haulle
     */
    @GET
    @Path("/hae/{hakuOid}/tyyppi/{tyyppi}")
    @Produces(MediaType.APPLICATION_JSON)
    Collection<YhteenvetoDto> hae(@PathParam("hakuOid") String hakuOid, @PathParam("tyyppi") LaskentaTyyppi tyyppi);

    /**
     * Yhteenvedot olemassa olevista laskennoista haulle
     */
    @GET
    @Path("/hae/{hakuOid}/kaynnissa")
    @Produces(MediaType.APPLICATION_JSON)
    Collection<YhteenvetoDto> haeKaynnissaOlevatLaskennat(@PathParam("hakuOid") String hakuOid);

    /**
     * Yhteenvedot olemassa olevista laskennoista
     */
    @GET
    @Path("/yhteenvetokaikillelaskennoille")
    @Produces(MediaType.APPLICATION_JSON)
    Collection<YhteenvetoDto> haeYhteenvetoKaikilleLaskennoille();


    @GET
    @Path("/laskenta/otaSeuraavaLaskentaTyonAlle")
    @Produces(MediaType.TEXT_PLAIN)
    Response otaSeuraavaLaskentaTyonAlle();

    /**
     * Kaikki yksityiskohdat
     */
    @GET
    @Path("/laskenta/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    LaskentaDto laskenta(@PathParam("uuid") String uuid);

    /**
     * Kaikki yksityiskohdat
     */
    @GET
    @Path("/kuormantasaus/laskenta/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    LaskentaDto kuormantasausLaskenta(@PathParam("uuid") String uuid);

    /**
     * Kaikki yksityiskohdat
     */
    @GET
    @Path("/lataa/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    Response lataa(@PathParam("uuid") String uuid);

    /**
     * Kaikki yksityiskohdat
     */
    @GET
    @Path("/yhteenveto/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    YhteenvetoDto yhteenveto(@PathParam("uuid") String uuid);

    /**
     * Paivittaa yksittaisen hakukohteen tilaa laskennassa
     */
    @PUT
    @Path("/kuormantasaus/laskenta/{uuid}/hakukohde/{hakukohdeOid}/tila/{tila}")
    @Produces(MediaType.APPLICATION_JSON)
    YhteenvetoDto merkkaaHakukohteenTila(@PathParam("uuid") String uuid,
                                         @PathParam("hakukohdeOid") String hakukohdeOid,
                                         @PathParam("tila") HakukohdeTila tila);

    /**
     * Paivittaa yksittaisen hakukohteen tilaa laskennassa ja jattaa ilmoituksen
     */
    @POST
    @Path("/kuormantasaus/laskenta/{uuid}/hakukohde/{hakukohdeOid}/tila/{tila}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    YhteenvetoDto merkkaaHakukohteenTila(@PathParam("uuid") String uuid,
                                         @PathParam("hakukohdeOid") String hakukohdeOid,
                                         @PathParam("tila") HakukohdeTila tila, IlmoitusDto ilmoitus);

    /**
     * Jattaa ilmoituksen
     */
    @POST
    @Path("/kuormantasaus/laskenta/{uuid}/hakukohde/{hakukohdeOid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    YhteenvetoDto lisaaIlmoitusHakukohteelle(@PathParam("uuid") String uuid,
                                             @PathParam("hakukohdeOid") String hakukohdeOid, IlmoitusDto ilmoitus);

    /**
     * Resetoi hakukohteiden tilat. Poistaa logit. Sailoo valmiit tilat.
     */
    @PUT
    @Path("/kuormantasaus/laskenta/{uuid}/resetoi")
    @Produces(MediaType.APPLICATION_JSON)
    LaskentaDto resetoiTilat(@PathParam("uuid") String uuid);

    /**
     * Paivittaa laskennan tilan
     */
    @PUT
    @Path("/kuormantasaus/laskenta/{uuid}/tila/{tila}")
    @Produces(MediaType.APPLICATION_JSON)
    YhteenvetoDto merkkaaLaskennanTila(@PathParam("uuid") String uuid,
                                       @PathParam("tila") LaskentaTila tila);
    @POST
    @Path("/kuormantasaus/laskenta/{uuid}/tila/{tila}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    YhteenvetoDto merkkaaLaskennanTila(@PathParam("uuid") String uuid,
                                       @PathParam("tila") LaskentaTila tila, IlmoitusDto ilmoitus);
    /**
     * Paivittaa laskennan tilan ja kaikki hakukohteet samalla
     */
    @PUT
    @Path("/kuormantasaus/laskenta/{uuid}/tila/{tila}/hakukohde/{hakukohteentila}")
    @Produces(MediaType.APPLICATION_JSON)
    YhteenvetoDto merkkaaLaskennanTila(@PathParam("uuid") String uuid,
                                       @PathParam("tila") LaskentaTila tila,
                                       @PathParam("hakukohteentila") HakukohdeTila hakukohteentila);
    @POST
    @Path("/kuormantasaus/laskenta/{uuid}/tila/{tila}/hakukohde/{hakukohteentila}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    YhteenvetoDto merkkaaLaskennanTila(@PathParam("uuid") String uuid,
                                       @PathParam("tila") LaskentaTila tila,
                                       @PathParam("hakukohteentila") HakukohdeTila hakukohteentila,
                                       IlmoitusDto ilmoitus);
    /**
     * Luo uuden laskennan seurantaan
     *
     * @return UUID
     */
    @POST
    @Path("/kuormantasaus/laskenta/{hakuOid}/tyyppi/{tyyppi}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    TunnisteDto luoLaskenta(@PathParam("hakuOid") String hakuOid,
                            @PathParam("tyyppi") LaskentaTyyppi tyyppi,
                            @QueryParam("userOID") String userOID,
                            @QueryParam("haunnimi") String haunnimi,
                            @QueryParam("nimi") String nimi,
                            @QueryParam("erillishaku") Boolean erillishaku,
                            @QueryParam("valinnanvaihe") Integer valinnanvaihe,
                            @QueryParam("valintakoelaskenta") Boolean valintakoelaskenta,
                            List<HakukohdeDto> hakukohdeOids);

    /**
     * Poistaa laskennan
     *
     * @return 200 OK jos onnistui
     */
    @DELETE
    @Path("/kuormantasaus/laskenta/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    Response poistaLaskenta(@PathParam("uuid") String uuid);
}
