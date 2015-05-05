package fi.vm.sade.valinta.seuranta.smoketest;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import fi.vm.sade.integrationtest.tomcat.EmbeddedTomcat;
import fi.vm.sade.integrationtest.util.ProjectRootFinder;
import fi.vm.sade.integrationtest.util.SpringProfile;
import fi.vm.sade.valinta.seuranta.dto.*;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.SseFeature;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * @author Jussi Jartamo
 */
public class SeurantaSmokeTest {
    public static final Gson GSON= new GsonBuilder()
            .registerTypeAdapter(Date.class, (JsonDeserializer) (json, typeOfT, context) -> new Date(json.getAsJsonPrimitive().getAsLong()))
            .create();
    private static final Logger LOG = LoggerFactory.getLogger(SeurantaSmokeTest.class);
    static final String SEURANTA_MODULE_ROOT = ProjectRootFinder.findProjectRoot() + "/seuranta/seuranta-service";
    static final String SEURANTA_CONTEXT_PATH = "seuranta-service";
    private static final EmbeddedTomcat smokecat = new EmbeddedTomcat(0, SEURANTA_MODULE_ROOT, SEURANTA_CONTEXT_PATH);
    final String root = "http://localhost:" + smokecat.port + "/seuranta-service/resources";

    @Before
    public void start() {
        SpringProfile.setProfile("it");
        smokecat.start();
    }
    final CopyOnWriteArrayList<YhteenvetoDto> spyList = new CopyOnWriteArrayList<>();
    final CyclicBarrier barrier = new CyclicBarrier(2);
    @Test
    public void testKeskeytettyaHakukohdettaEiVoiValmistaa() throws Throwable {
        String HAKU1 = "haku1";
        String HAKUKOHDE1 = "hakukohde1";
        String uuid;
        EventSource eventSource;
        {
            Response luonti = luoLaskenta(HAKU1, LaskentaTyyppi.VALINTARYHMA, true, Arrays.asList(new HakukohdeDto(HAKUKOHDE1, "organisaatio1")));
            assertEquals(200, luonti.getStatus());
            uuid = IOUtils.toString((InputStream) luonti.getEntity());
            eventSource = sseSource(uuid);
            Response laskenta = otaSeuraavaLaskentaTyonAlle();
            assertEquals("Seuraavan laskennan aloittaminen epäonnistui", 200, laskenta.getStatus());
            assertEquals(uuid, IOUtils.toString((InputStream) laskenta.getEntity()));
        }
        try {
            {
                barrier.await(1000, TimeUnit.SECONDS);
                assertEquals(1, new ArrayList<>(spyList).stream().filter(y -> y.getHakukohteitaValmiina() == 0 && y.getHakukohteitaKeskeytetty() == 0 && y.getHakukohteitaYhteensa() == 1).count());
            }
            {
                Collection<YhteenvetoDto> listaus = getYhteenvedotToHaku(HAKU1);
                assertEquals(1, listaus.size());
                YhteenvetoDto yhteenveto = listaus.iterator().next();
                assertEquals(yhteenveto.getHakukohteitaKeskeytetty(), 0);
                assertEquals(yhteenveto.getHakukohteitaValmiina(), 0);
                assertEquals(yhteenveto.getHakukohteitaYhteensa(), 1);
            }
            {
                Response paivitys = paivitaLaskenta(uuid, HAKUKOHDE1, HakukohdeTila.KESKEYTETTY);
                assertEquals(200, paivitys.getStatus());
                System.err.println("\r\n" + paivitys.getStatus());
            }
            {
                // KESKEYTETTYA EI VOI VALMISTAA
                Response paivitys = paivitaLaskenta(uuid, HAKUKOHDE1, HakukohdeTila.VALMIS);
                assertEquals(204, paivitys.getStatus());
                System.err.println("\r\n" + paivitys.getStatus());
            }
            {
                barrier.await(1000, TimeUnit.SECONDS);
                assertEquals(1, new ArrayList<>(spyList).stream().filter(y -> y.getHakukohteitaValmiina() == 0 && y.getHakukohteitaKeskeytetty() == 1 && y.getHakukohteitaYhteensa() == 1).count());
            }
        } finally {
            eventSource.close();
        }
    }
    @Test
    public void testKeskeytettyaLaskentaaEiVoiValmistaa() throws Throwable {
        String HAKU1 = "haku2";
        String HAKUKOHDE1 = "hakukohde2";
        String uuid;
        EventSource eventSource;
        {
            Response luonti = luoLaskenta(HAKU1, LaskentaTyyppi.VALINTARYHMA, true, Arrays.asList(new HakukohdeDto(HAKUKOHDE1, "organisaatio1")));
            assertEquals(200, luonti.getStatus());
            uuid = IOUtils.toString((InputStream) luonti.getEntity());
            eventSource = sseSource(uuid);
            Response laskenta = otaSeuraavaLaskentaTyonAlle();
            assertEquals("Seuraavan laskennan aloittaminen epäonnistui", 200, laskenta.getStatus());
            assertEquals(uuid, IOUtils.toString((InputStream) laskenta.getEntity()));
        }
        try {
            {
                barrier.await(1000, TimeUnit.SECONDS);
                assertEquals(1, new ArrayList<>(spyList).stream().filter(y -> y.getHakukohteitaValmiina() == 0 && y.getHakukohteitaKeskeytetty() == 0 && y.getHakukohteitaYhteensa() == 1).count());
            }
            {
                Collection<YhteenvetoDto> listaus = getYhteenvedotToHaku(HAKU1);
                assertEquals(1, listaus.size());
                YhteenvetoDto yhteenveto = listaus.iterator().next();
                assertEquals(yhteenveto.getHakukohteitaKeskeytetty(), 0);
                assertEquals(yhteenveto.getHakukohteitaValmiina(), 0);
                assertEquals(yhteenveto.getHakukohteitaYhteensa(), 1);
            }
            {
                Response paivitys = paivitaLaskenta(uuid, LaskentaTila.VALMIS, HakukohdeTila.KESKEYTETTY);
                assertEquals(200, paivitys.getStatus());
                System.err.println("\r\n" + paivitys.getStatus());
            }
            {
                // KESKEYTETTYA EI VOI VALMISTAA
                Response paivitys = paivitaLaskenta(uuid, LaskentaTila.VALMIS, HakukohdeTila.VALMIS);
                assertEquals(204, paivitys.getStatus());
                System.err.println("\r\n" + paivitys.getStatus());
            }
            {
                barrier.await(1000, TimeUnit.SECONDS);
                assertEquals(1, new ArrayList<>(spyList).stream().filter(y -> y.getHakukohteitaValmiina() == 0 && y.getHakukohteitaKeskeytetty() == 1 && y.getHakukohteitaYhteensa() == 1).count());
            }
        } finally {
            eventSource.close();
        }
    }

    public EventSource sseSource(String uuid) {
        Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
        WebTarget target = client.target(root + "/seuranta/yhteenveto/" + uuid + "/sse");
        EventSource eventSource = EventSource.target(target).build();
        eventSource.register((event) -> {
            spyList.add(GSON.fromJson(new String(event.getRawData()), YhteenvetoDto.class));
            try {
                barrier.await(1000, TimeUnit.SECONDS);
            } catch(Throwable ignored) {}
        });
        eventSource.open();
        return eventSource;
    }

    //@PUT
    //@Path("/kuormantasaus/laskenta/{uuid}/tila/{tila}/hakukohde/{hakukohteentila}")
    public Response paivitaLaskenta(String uuid, LaskentaTila tila, HakukohdeTila hakukohdeTila) throws Throwable {
        return put(root + "/seuranta/kuormantasaus/laskenta/" + uuid + "/tila/" + tila + "/hakukohde/" + hakukohdeTila);
    }

    private Response put(String url) {
        LOG.info("\r\nPATH {}", url);
        Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
        WebTarget target = client.target(url);
        return target.request().accept(MediaType.WILDCARD).put(Entity.json(""));
    }

    public Response paivitaLaskenta(String uuid, String hakukohdeOid, HakukohdeTila hakukohdeTila) throws Throwable {
        return put(root + "/seuranta/kuormantasaus/laskenta/"+ uuid +"/hakukohde/"+ hakukohdeOid + "/tila/"+ hakukohdeTila);
    }

    public Response luoLaskenta(String hakuOid, LaskentaTyyppi tyyppi, Boolean erillishaku, List<HakukohdeDto> hakukohteet) throws Throwable {
        Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
        WebTarget target = client.target(root + "/seuranta/kuormantasaus/laskenta/" + hakuOid + "/tyyppi/" + tyyppi + "?erillishaku=" + Boolean.TRUE.equals(erillishaku));
        Invocation.Builder builder = target.request().accept(MediaType.WILDCARD);
        return builder.post(Entity.json(GSON.toJson(hakukohteet)));
    }

    public Response otaSeuraavaLaskentaTyonAlle() {
        String url = root + "/seuranta/laskenta/otaSeuraavaLaskentaTyonAlle";
        LOG.info("\r\nPATH {}", url);
        Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
        WebTarget target = client.target(url);
        return target.request().accept(MediaType.WILDCARD).get();
    }

    public Collection<YhteenvetoDto> getYhteenvedotToHaku(String hakuOid) throws Throwable{
        Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
        WebTarget target = client.target(root + "/seuranta/hae/" + hakuOid);
        return GSON.fromJson(IOUtils.toString((InputStream) target.request().get().getEntity()), new TypeToken<List<YhteenvetoDto>>() {}.getType());
    }
}
