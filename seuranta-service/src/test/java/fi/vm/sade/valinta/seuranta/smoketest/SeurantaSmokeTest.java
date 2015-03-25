package fi.vm.sade.valinta.seuranta.smoketest;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import fi.vm.sade.integrationtest.tomcat.EmbeddedTomcat;
import fi.vm.sade.integrationtest.util.ProjectRootFinder;
import fi.vm.sade.integrationtest.util.SpringProfile;
import fi.vm.sade.valinta.seuranta.dto.*;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * @author Jussi Jartamo
 */
public class SeurantaSmokeTest {
    public static final Gson GSON= new GsonBuilder()
            .registerTypeAdapter(Date.class, new JsonDeserializer() {
                @Override
                public Object deserialize(JsonElement json, Type typeOfT,
                                          JsonDeserializationContext context)
                        throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            })
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
    public void testKeskeytettyaHakukohdettaEiVoiValmistaa() throws Throwable{
        String HAKU1 = "haku1";
        String HAKUKOHDE1 = "hakukohde1";
        String uuid;
        EventSource eventSource;
        {
            Response luonti = luoLaskenta(HAKU1, LaskentaTyyppi.VALINTARYHMA, true, null, null, Arrays.asList(new HakukohdeDto(HAKUKOHDE1, "organisaatio1")));
            Assert.assertEquals(200, luonti.getStatus());
            uuid = IOUtils.toString((InputStream) luonti.getEntity());
            eventSource = sseSource(uuid);
        }
        try {
            {
                barrier.await(1000, TimeUnit.SECONDS);
                Assert.assertEquals(1,
                        new ArrayList<>(spyList).stream().filter(y -> {
                            return y.getHakukohteitaValmiina() == 0 && y.getHakukohteitaKeskeytetty() == 0 && y.getHakukohteitaYhteensa() == 1;
                        }).count());
            }
            {
                Collection<YhteenvetoDto> listaus = getYhteenvedotToHaku(HAKU1);
                Assert.assertEquals(1, listaus.size());
                YhteenvetoDto yhteenveto = listaus.iterator().next();
                Assert.assertEquals(yhteenveto.getHakukohteitaKeskeytetty(), 0);
                Assert.assertEquals(yhteenveto.getHakukohteitaValmiina(), 0);
                Assert.assertEquals(yhteenveto.getHakukohteitaYhteensa(), 1);
            }
            {
                Response paivitys = paivitaLaskenta(uuid, HAKUKOHDE1, HakukohdeTila.KESKEYTETTY);
                Assert.assertEquals(200, paivitys.getStatus());
                System.err.println("\r\n" + paivitys.getStatus());
            }
            {
                // KESKEYTETTYA EI VOI VALMISTAA
                Response paivitys = paivitaLaskenta(uuid, HAKUKOHDE1, HakukohdeTila.VALMIS);
                Assert.assertEquals(204, paivitys.getStatus());
                System.err.println("\r\n" + paivitys.getStatus());
            }
            {
                barrier.await(1000, TimeUnit.SECONDS);
                Assert.assertEquals(1,
                        new ArrayList<>(spyList).stream().filter(y ->
                                y.getHakukohteitaValmiina() == 0 &&
                                        y.getHakukohteitaKeskeytetty() == 1 &&
                                        y.getHakukohteitaYhteensa() == 1).count());
            }
        } finally {
            eventSource.close();
        }
    }
    @Test
    public void testKeskeytettyaLaskentaaEiVoiValmistaa() throws Throwable{
        String HAKU1 = "haku2";
        String HAKUKOHDE1 = "hakukohde2";
        String uuid;
        EventSource eventSource;
        {
            Response luonti = luoLaskenta(HAKU1, LaskentaTyyppi.VALINTARYHMA, true, null, null, Arrays.asList(new HakukohdeDto(HAKUKOHDE1, "organisaatio1")));
            Assert.assertEquals(200, luonti.getStatus());
            uuid = IOUtils.toString((InputStream) luonti.getEntity());
            eventSource = sseSource(uuid);
        }
        try {
            {
                barrier.await(1000, TimeUnit.SECONDS);
                Assert.assertEquals(1,
                        new ArrayList<>(spyList).stream().filter(y -> {
                            return y.getHakukohteitaValmiina() == 0 && y.getHakukohteitaKeskeytetty() == 0 && y.getHakukohteitaYhteensa() == 1;
                        }).count());
            }
            {
                Collection<YhteenvetoDto> listaus = getYhteenvedotToHaku(HAKU1);
                Assert.assertEquals(1, listaus.size());
                YhteenvetoDto yhteenveto = listaus.iterator().next();
                Assert.assertEquals(yhteenveto.getHakukohteitaKeskeytetty(), 0);
                Assert.assertEquals(yhteenveto.getHakukohteitaValmiina(), 0);
                Assert.assertEquals(yhteenveto.getHakukohteitaYhteensa(), 1);
            }
            {
                Response paivitys = paivitaLaskenta(uuid, LaskentaTila.VALMIS, HakukohdeTila.KESKEYTETTY);
                Assert.assertEquals(200, paivitys.getStatus());
                System.err.println("\r\n" + paivitys.getStatus());
            }
            {
                // KESKEYTETTYA EI VOI VALMISTAA
                Response paivitys = paivitaLaskenta(uuid, LaskentaTila.VALMIS, HakukohdeTila.VALMIS);
                Assert.assertEquals(204, paivitys.getStatus());
                System.err.println("\r\n" + paivitys.getStatus());
            }
            {
                barrier.await(1000, TimeUnit.SECONDS);
                Assert.assertEquals(1,
                        new ArrayList<>(spyList).stream().filter(y ->
                                y.getHakukohteitaValmiina() == 0 &&
                                        y.getHakukohteitaKeskeytetty() == 1 &&
                                        y.getHakukohteitaYhteensa() == 1).count());
            }
        } finally {
            eventSource.close();
        }
    }

    public EventSource sseSource(String uuid) {
        Client client = ClientBuilder.newBuilder()
                .register(SseFeature.class).build();
        WebTarget target = client.target(root + "/seuranta/yhteenveto/" + uuid + "/sse");
        EventSource eventSource = EventSource.target(target).build();
        eventSource.register((event) -> {
            spyList.add(GSON.fromJson(new String(event.getRawData()), YhteenvetoDto.class));
            try {
                barrier.await(1000, TimeUnit.SECONDS);
            } catch(Throwable t) {

            }
        });
        eventSource.open();
        return eventSource;
    }

    //@PUT
    //@Path("/kuormantasaus/laskenta/{uuid}/tila/{tila}/hakukohde/{hakukohteentila}")
    public Response paivitaLaskenta(String uuid, LaskentaTila tila, HakukohdeTila hakukohdeTila) throws Throwable {
        String url = root + "/seuranta/kuormantasaus/laskenta/"+ uuid +"/tila/" + tila +"/hakukohde/"+ hakukohdeTila;
        LOG.info("\r\nPATH {}", url);
        Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();

        WebTarget target = client.target(url);
        //HttpResource query = new HttpResource(url);
        //WebClient client = query.getWebClient()

        return target.request().accept(MediaType.WILDCARD).put(Entity.json(""));
    }

    public Response paivitaLaskenta(String uuid, String hakukohdeOid, HakukohdeTila hakukohdeTila) throws Throwable {
        String url = root + "/seuranta/kuormantasaus/laskenta/"+ uuid +"/hakukohde/"+ hakukohdeOid + "/tila/"+ hakukohdeTila;
        LOG.info("\r\nPATH {}", url);
        Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();

        WebTarget target = client.target(url);
        //HttpResource query = new HttpResource(url);
        //WebClient client = query.getWebClient()

        return target.request().accept(MediaType.WILDCARD).put(Entity.json(""));
    }
    public Response luoLaskenta(String hakuOid, LaskentaTyyppi tyyppi, Boolean erillishaku, Integer valinnanvaihe, Boolean valintakoelaskenta,
        List<HakukohdeDto> hakukohteet) throws Throwable {

        //@Consumes(MediaType.APPLICATION_JSON)
        //@Produces(MediaType.TEXT_PLAIN)
        Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
        WebTarget target = client.target(root + "/seuranta/kuormantasaus/laskenta/"+hakuOid+"/tyyppi/" + tyyppi + "?erillishaku="+Boolean.TRUE.equals(erillishaku));
        //HttpResource query = new HttpResource();
        //WebClient client = query.getWebClient()
        Invocation.Builder builder = target.request()

                //.query("erillishaku", Boolean.TRUE.equals(erillishaku))
        .accept(MediaType.WILDCARD);
                //.query("valintakoelaskenta", Boolean.TRUE.equals(valintakoelaskenta));

        if(valinnanvaihe != null) {
                    //builder.query("valinnanvaihe", valinnanvaihe);
                }
        return builder.post(Entity.json(GSON.toJson(hakukohteet)));
    }
    public Collection<YhteenvetoDto> getYhteenvedotToHaku(String hakuOid) throws Throwable{
        Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
        WebTarget target = client.target(root + "/seuranta/hae/" + hakuOid);
        return GSON.fromJson(
                IOUtils.toString((InputStream) target.request().get().getEntity()), new TypeToken<List<YhteenvetoDto>>() {
                }.getType());
    }
}
