package fi.vm.sade.valinta.seuranta.resource;

import java.util.Collection;
import java.util.Date;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.vm.sade.valinta.seuranta.sijoittelu.dto.SijoitteluDto;

@Path("sijoittelunseuranta")
public interface SijoittelunSeurantaResource {

    /**
     * Sijoittelun tila haulle
     */
    @GET
    @Path("/hae/{hakuOid}")
    @Produces(MediaType.APPLICATION_JSON)
    SijoitteluDto hae(@PathParam("hakuOid") String hakuOid);

    /**
     * Kantaan tallennetut sijoittelut
     */
    @GET
    @Path("/hae")
    @Produces(MediaType.APPLICATION_JSON)
    Collection<SijoitteluDto> hae();

    /**
     * Paivittaa sijoittelun tilan
     */
    @PUT
    @Path("/sijoittelu/{hakuOid}/ajossa/{tila}")
    @Produces(MediaType.APPLICATION_JSON)
    SijoitteluDto merkkaaSijoittelunAjossaTila(@PathParam("hakuOid") String hakuOid, @PathParam("tila") boolean tila);

    /**
     * Asettaa sijoittelun virheen
     */
    @PUT
    @Path("/sijoittelu/{hakuOid}/virhe")
    @Produces(MediaType.APPLICATION_JSON)
    SijoitteluDto merkkaaSijoittelunAjossaVirhe(@PathParam("hakuOid") String hakuOid, @QueryParam("virhe") String virhe);


    /**
     * Paivittaa sijoittelun viimeksiajettupaivamaaran tamanhetkiseksi aikaleimaksi
     */
    @PUT
    @Path("/sijoittelu/{hakuOid}")
    @Produces(MediaType.APPLICATION_JSON)
    SijoitteluDto merkkaaSijoittelunAjetuksi(@PathParam("hakuOid") String hakuOid);

    /**
     * Poistaa laskennan
     *
     * @return 200 OK jos onnistui
     */
    @DELETE
    @Path("/sijoittelu/{hakuOid}")
    Response poistaSijoittelu(@PathParam("hakuOid") String hakuOid);

    /**
     * Päivittää sijoittelun aloitusajankohdan
     *
     * @return 200 OK jos onnistui
     */
    @PUT
    @Path("/sijoittelu/{hakuOid}/paivita")
    Response paivitaSijoittelunAloitusajankohta(@PathParam("hakuOid") String hakuOid, @QueryParam("aloitusajankohta") Long aloitusajankohta, @QueryParam("ajotiheys") Integer ajotiheys);
}
