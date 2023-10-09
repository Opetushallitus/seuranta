package fi.vm.sade.valinta.seuranta.dokumentti;

import com.google.gson.GsonBuilder;
import fi.vm.sade.valinta.dokumenttipalvelu.Dokumenttipalvelu;
import fi.vm.sade.valinta.dokumenttipalvelu.dto.ObjectMetadata;
import fi.vm.sade.valinta.seuranta.dokumentti.repository.DokumenttiRepository;
import fi.vm.sade.valinta.seuranta.dto.DokumenttiDto;
import fi.vm.sade.valinta.seuranta.dto.VirheilmoitusDto;
import fi.vm.sade.valinta.seuranta.resource.DokumentinSeurantaResource;
import fi.vm.sade.valinta.seuranta.resource.impl.DokumentinSeurantaResourceImpl;
import org.junit.Assert;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static java.util.Arrays.*;
import static org.mockito.Mockito.*;

import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

/**
 * @author Jussi Jartamo
 */
public class DokumenttiResurssiTest extends TestCase {
    private final static Logger LOG = LoggerFactory.getLogger(DokumenttiResurssiTest.class);
    private final DokumentinSeurantaResource resurssi;

    public DokumenttiResurssiTest() {
        this.resurssi = new DokumentinSeurantaResourceImpl(new DokumenttiRepositoryMock());
    }

    @Test
    public void testLisausPaivitysLukuJaPoisto() {
        final String key = UUID.randomUUID().toString();
        final String uuid;
        // CREATE
        {
            Response r = resurssi.luoDokumentti("Uuden dokumentin kuvaus");
            Assert.assertEquals(200, r.getStatus());
            // throws if null or invalid id
            uuid = (String) r.getEntity();
            LOG.error("{}", uuid);
        }
        // READ
        {
            Response r = resurssi.dokumentti(uuid);
            DokumenttiDto dokumentti = (DokumenttiDto)r.getEntity();

            LOG.info("{}", new GsonBuilder().setPrettyPrinting().create().toJson(dokumentti));

            Assert.assertEquals("Uuden dokumentin kuvaus",dokumentti.getKuvaus());


            resurssi.paivitaKuvaus(uuid,"Päivitetty kuvaus!");

        }
        // UPDATE KUVAUS
        {
            Response r = resurssi.paivitaKuvaus(uuid, "Päivitetty kuvaus!");
            DokumenttiDto dokumentti = (DokumenttiDto)r.getEntity();

            LOG.info("{}", new GsonBuilder().setPrettyPrinting().create().toJson(dokumentti));

            Assert.assertEquals("Päivitetty kuvaus!",dokumentti.getKuvaus());



        }
        // UPDATE VIRHEILMOT
        {
            Response r = resurssi.lisaaVirheita(uuid, asList(new VirheilmoitusDto("Virhetyyppi!", "Virheilmoitus!")));
            DokumenttiDto dokumentti = (DokumenttiDto)r.getEntity();

            LOG.info("{}", new GsonBuilder().setPrettyPrinting().create().toJson(dokumentti));
            VirheilmoitusDto virheilmo = dokumentti.getVirheilmoitukset().iterator().next();
            Assert.assertEquals(virheilmo.getTyyppi(), "Virhetyyppi!");
            Assert.assertEquals(virheilmo.getIlmoitus(), "Virheilmoitus!");


            Response r0 = resurssi.lisaaVirheita(uuid, asList(new VirheilmoitusDto("Virhetyyppi2!", "Virheilmoitus2!")));
            DokumenttiDto dokumentti2 = (DokumenttiDto)r0.getEntity();
            LOG.info("{}", new GsonBuilder().setPrettyPrinting().create().toJson(dokumentti2));
            Assert.assertEquals(2, dokumentti2.getVirheilmoitukset().size());
        }
        // DELETE
        {
            Response rx = resurssi.poista(uuid);

            Response r0 = resurssi.dokumentti(uuid);

            Assert.assertEquals(500, r0.getStatus());
        }
    }

}
