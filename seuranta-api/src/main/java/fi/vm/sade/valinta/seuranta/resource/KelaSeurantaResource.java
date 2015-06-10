package fi.vm.sade.valinta.seuranta.resource;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/kela")
public interface KelaSeurantaResource {

    /**
     * Paivittaa yksittaisen hakukohteen tilaa laskennassa
     *
     * @param hakuOid
     * @return Palauttaa hakijat joita ei ole viela merkattu viedyiksi Kelan
     * FTP-palvelimelle
     */
    @POST
    @Path("/haku/{hakuOid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Collection<String> tarkistaOnkoHakijatJoVietyKelanFtpPalvelimelle(@PathParam("hakuOid") String hakuOid, Collection<String> hakijaOids);

    /**
     * Paivittaa yksittaisen hakukohteen tilaa laskennassa
     *
     * @param hakuOid
     * @return Palauttaa hakijat jotka oli jo merkattu viedyiksi Kelan
     * FTP-palvelimelle
     */
    @PUT
    @Path("/haku/{hakuOid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Collection<String> merkkaaHakijatViedyksiKelanFtpPalvelimelle(@PathParam("hakuOid") String hakuOid, Collection<String> hakijaOids);
}
