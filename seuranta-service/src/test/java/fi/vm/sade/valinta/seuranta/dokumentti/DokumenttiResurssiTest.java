package fi.vm.sade.valinta.seuranta.dokumentti;

import com.google.gson.GsonBuilder;
import de.flapdoodle.embed.mongo.MongodExecutable;
import fi.vm.sade.valinta.seuranta.dokumentti.dao.impl.DokumenttiDaoImpl;
import fi.vm.sade.valinta.seuranta.dto.DokumenttiDto;
import fi.vm.sade.valinta.seuranta.dto.VirheilmoitusDto;
import fi.vm.sade.valinta.seuranta.laskenta.service.DokumentinSeurantaSSEService;
import fi.vm.sade.valinta.seuranta.resource.DokumentinSeurantaResource;
import fi.vm.sade.valinta.seuranta.resource.impl.DokumentinSeurantaResourceImpl;
import fi.vm.sade.valinta.seuranta.testcontext.MongoConfiguration;
import org.junit.Assert;
import junit.framework.TestCase;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;
import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static java.util.Arrays.*;
import javax.ws.rs.core.Response;

/**
 * @author Jussi Jartamo
 */
public class DokumenttiResurssiTest extends TestCase {
    private final static Logger LOG = LoggerFactory.getLogger(DokumenttiResurssiTest.class);
    private final DokumentinSeurantaResource resurssi;
    private final MongodExecutable exe;
    private final Datastore datastore;
    private final DokumentinSeurantaSSEService sseService;
    public DokumenttiResurssiTest() throws Exception {
        MongoConfiguration mongocfg = new MongoConfiguration();
        this.exe = mongocfg.getMongodExecutable();
        this.datastore = mongocfg.getDatastore(mongocfg.getMorphia(), mongocfg.getMongo(mongocfg.getMongoProcess(exe)));
        this.sseService = Mockito.mock(DokumentinSeurantaSSEService.class);
        this.resurssi = new DokumentinSeurantaResourceImpl(new DokumenttiDaoImpl(datastore), sseService);
    }

    @After
    public void stop() {
        exe.stop();
    }

    @Test
    public void testLisausPaivitysLukuJaPoisto() {
        final String uuid;
        // CREATE
        {
            Response r = resurssi.luoDokumentti("Uuden dokumentin kuvaus");
            Assert.assertEquals(200, r.getStatus());
            // throws if null or invalid id
            uuid = (String) r.getEntity();
            ObjectId objId = new ObjectId(uuid);
            LOG.error("{}", objId);
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
