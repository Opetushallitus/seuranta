package fi.vm.sade.valinta.seuranta.laskenta.dao.impl;

import java.util.*;
import java.util.stream.Collectors;

import fi.vm.sade.valinta.seuranta.laskenta.domain.Laskenta;
import org.junit.Assert;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.GsonBuilder;

import fi.vm.sade.valinta.seuranta.dto.HakukohdeDto;
import fi.vm.sade.valinta.seuranta.dto.HakukohdeTila;
import fi.vm.sade.valinta.seuranta.dto.IlmoitusDto;
import fi.vm.sade.valinta.seuranta.dto.IlmoitusTyyppi;
import fi.vm.sade.valinta.seuranta.dto.LaskentaDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTila;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTyyppi;
import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;
import fi.vm.sade.valinta.seuranta.laskenta.dao.SeurantaDao;
import fi.vm.sade.valinta.seuranta.laskenta.domain.Ilmoitus;
import fi.vm.sade.valinta.seuranta.testcontext.SeurantaConfiguration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.springframework.test.util.AssertionErrors.assertTrue;

/**
 * @author Jussi Jartamo
 */
@ContextConfiguration(classes = SeurantaConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SeurantaDaoTest {
    private static final Logger LOG = LoggerFactory.getLogger(SeurantaDaoTest.class);
    @Autowired
    private SeurantaDao seurantaDao;
    private static final Random rnd = new Random(System.currentTimeMillis());
    private static String randomHakukohde() {
        return "HAKUKOHDE_" + Math.abs(rnd.nextInt(100000000));
    }

    @Before
    public void siivoSeurannat() {
        seurantaDao.siivoa(new Date());
    }

    @Test
    public void testaaMeneillaanOlevienResetointi() {
        final String hakukohde = randomHakukohde();
        final String uuid = aloitaUusiLaskenta(Optional.of(hakukohde));

        final String hakukohde2 = randomHakukohde();
        final String uuid2 = aloitaUusiLaskenta(Optional.of(hakukohde2));

        Assert.assertTrue(LaskentaTila.MENEILLAAN.equals(seurantaDao.haeLaskenta(uuid).getTila()));
        Assert.assertTrue(LaskentaTila.MENEILLAAN.equals(seurantaDao.haeLaskenta(uuid2).getTila()));
        seurantaDao.resetoiMeneillaanOlevatLaskennat();
        Assert.assertTrue(LaskentaTila.PERUUTETTU.equals(seurantaDao.haeLaskenta(uuid).getTila()));
        Assert.assertTrue(LaskentaTila.PERUUTETTU.equals(seurantaDao.haeLaskenta(uuid2).getTila()));
    }

    @Test
    public void testaaMerkkaaLaskennanTilaJaHakukohteidenTilaKerrallaValmistuneelleLaskennalle() {
        String uuid = aloitaUusiLaskenta(Optional.of(randomHakukohde()));
        YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, LaskentaTila.VALMIS, HakukohdeTila.KESKEYTETTY, Optional.of(IlmoitusDto.ilmoitus("Toimiiko!")));
        assertOikeaLaskentaEiOleNull(uuid, y);
    }

    @Test
    public void testaaMerkkaaTilaHakukohteelle() {
        final String hakukohde = randomHakukohde();
        String uuid = aloitaUusiLaskenta(Optional.of(hakukohde));
        YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, hakukohde, HakukohdeTila.KESKEYTETTY);
        assertOikeaLaskentaEiOleNull(uuid, y);
    }

    @Test
    public void testaaLisaaIlmoitusHakukohteelle() {
        final String hakukohde = randomHakukohde();
        String uuid = aloitaUusiLaskenta(Optional.of(hakukohde));
        YhteenvetoDto y = seurantaDao.lisaaIlmoitus(uuid, hakukohde, new IlmoitusDto(IlmoitusTyyppi.ILMOITUS, "Jee"));
        assertOikeaLaskentaEiOleNull(uuid, y);
    }

    @Test
    public void testaaMerkkaaLaskennanTilaJaHakukohteidenTilaKerralla() {
        final String hakukohde = randomHakukohde();
        String uuid = aloitaUusiLaskenta(Optional.of(hakukohde));
        YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, LaskentaTila.VALMIS, HakukohdeTila.KESKEYTETTY, Optional.of(IlmoitusDto.ilmoitus("Toimiiko!")));
        assertOikeaLaskentaEiOleNull(uuid, y);
    }

    @Test
    public void testaaMerkkaaLaskennanTila() {
        final String hakukohde = randomHakukohde();
        String uuid = aloitaUusiLaskenta(Optional.of(hakukohde));
        YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, LaskentaTila.VALMIS, Optional.of(IlmoitusDto.ilmoitus("Toimiiko!")));
        assertOikeaLaskentaEiOleNull(uuid, y);
    }

    @Test
    public void testaaMerkkaaLaskennanTilaMerkatulleLaskennalle() {
        final String hakukohde = randomHakukohde();
        String uuid = aloitaUusiLaskenta(Optional.of(hakukohde));
        YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, LaskentaTila.VALMIS, Optional.of(IlmoitusDto.ilmoitus("Toimiiko!")));
        assertOikeaLaskentaEiOleNull(uuid, y);
    }

    @Test
    public void testaaMerkkaaHakukohteenTila() {
        final String hakukohdeOid = "1.2.246.562.20.14854904639";
        String uuid = aloitaUusiLaskenta(Optional.of(hakukohdeOid));
        YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, hakukohdeOid, HakukohdeTila.KESKEYTETTY, new IlmoitusDto(IlmoitusTyyppi.ILMOITUS, "Jee"));
        assertOikeaLaskentaEiOleNull(uuid, y);
    }

    @Test
    public void testaaLaskennanAloittaminenIlmanLaskentaaPalauttaaNull() {
        assertNull(seurantaDao.otaSeuraavaLaskentaTyonAlle());
    }

    @Test
    public void testaaKolmasLaskennanAloitusPalauttaaNullKunVainKaksiLaskentaaOlemassa() {
        Collection<HakukohdeDto> hakukohdeOids = Arrays.asList(new HakukohdeDto("h1", "o1"), new HakukohdeDto("h2", "o2"));
        seurantaDao.luoLaskenta("U0","", "", "hakuOid1", null, LaskentaTyyppi.HAKU, true, null, null, hakukohdeOids);
        seurantaDao.luoLaskenta("U0","", "", "hakuOid2", null, LaskentaTyyppi.HAKU, true, null, null, hakukohdeOids);
        assertNotNull(seurantaDao.otaSeuraavaLaskentaTyonAlle());
        assertNotNull(seurantaDao.otaSeuraavaLaskentaTyonAlle());
        assertNull(seurantaDao.otaSeuraavaLaskentaTyonAlle());
    }

    @Test
    public void testaaSeuranta() throws InterruptedException {
        String hakuOid = "hakuOid";
        Collection<HakukohdeDto> hakukohdeOids = Arrays.asList(
                new HakukohdeDto("hk1", "oo1"),
                new HakukohdeDto("hk2", "oo2"),
                new HakukohdeDto("hk3", "oo3")
        );
        seurantaDao.luoLaskenta("U0","", "", hakuOid, null, LaskentaTyyppi.HAKU, true, null, null, hakukohdeOids);
        String uuid = seurantaDao.otaSeuraavaLaskentaTyonAlle();
        seurantaDao.merkkaaTila(uuid, "hk3", HakukohdeTila.KESKEYTETTY);
        seurantaDao.merkkaaTila(uuid, "hk2", HakukohdeTila.VALMIS);
        seurantaDao.merkkaaTila(uuid, "hk1", HakukohdeTila.KESKEYTETTY);
        seurantaDao.merkkaaTila(uuid, "hk1", HakukohdeTila.KESKEYTETTY);
        seurantaDao.merkkaaTila(uuid, "hk2", HakukohdeTila.VALMIS);
        seurantaDao.merkkaaTila(uuid, "hk3", HakukohdeTila.VALMIS);
        seurantaDao.luoLaskenta("U0","", "", hakuOid, null, LaskentaTyyppi.HAKU, true, null, null, hakukohdeOids);
        seurantaDao.lisaaIlmoitus(uuid, "hk1", new Ilmoitus(IlmoitusTyyppi.ILMOITUS, "Ei toimi", null));
        seurantaDao.haeYhteenvedotHaulle(hakuOid);
        Collection<YhteenvetoDto> yhteenvedot = seurantaDao.haeYhteenvedotHaulle(hakuOid, LaskentaTyyppi.HAKU);
        assertEquals(1, yhteenvedot.size());
        LaskentaDto l = seurantaDao.haeLaskenta(uuid);
        l.getHakukohteet().forEach(hk -> {
            LOG.info("Hakukohde {} ja organisaatio {}", hk.getHakukohdeOid(), hk.getOrganisaatioOid());
            assertTrue("Organisaatio Oid puuttui", hk.getOrganisaatioOid() != null);
            assertTrue("HakukohdeOid puuttui", hk.getHakukohdeOid() != null);
        });
        assertEquals(LaskentaTila.MENEILLAAN, seurantaDao.haeLaskenta(uuid).getTila());
        seurantaDao.merkkaaTila(uuid, LaskentaTila.VALMIS, Optional.of(IlmoitusDto.ilmoitus("Toimiiko!")));
        assertEquals(LaskentaTila.VALMIS, seurantaDao.haeLaskenta(uuid).getTila());
        seurantaDao.merkkaaTila(uuid, LaskentaTila.PERUUTETTU, Optional.of(IlmoitusDto.ilmoitus("Toimiiko!")));
        assertEquals(LaskentaTila.VALMIS, seurantaDao.haeLaskenta(uuid).getTila());
        seurantaDao.merkkaaTila(uuid, "hk3", HakukohdeTila.VALMIS, new IlmoitusDto(IlmoitusTyyppi.VAROITUS, "Hehei2"));
        seurantaDao.lisaaIlmoitus(uuid, "hk3", new IlmoitusDto(IlmoitusTyyppi.VAROITUS, "Hehei3"));
        String uuid2 = seurantaDao.luoLaskenta("U0","", "", hakuOid, null, LaskentaTyyppi.HAKU, true, null, null, hakukohdeOids).getUuid();
        assertNotSame(uuid, uuid2);
        assertEquals(seurantaDao.resetoiEiValmiitHakukohteet(uuid, false).getUuid(), uuid2);
        assertEquals(2, seurantaDao.haeYhteenvedotHaulle(hakuOid).size());
        seurantaDao.siivoa(DateTime.now().minusDays(1).toDate());
        assertEquals(2, seurantaDao.haeYhteenvedotHaulle(hakuOid).size());
        seurantaDao.siivoa(DateTime.now().plusHours(1).toDate());
        assertEquals(0, seurantaDao.haeYhteenvedotHaulle(hakuOid).size());
    }

    @Test
    public void testaaHakukohteetKerrallaValmiiksi() {
        String hakuOid = "hakuOidKerrallaValmiiksi";
        String valintaryhmaOid = "vr1";
        Collection<HakukohdeDto> hakukohdeOids = Arrays.asList(
                new HakukohdeDto("hk1", "oo1"),
                new HakukohdeDto("hk2", "oo2"),
                new HakukohdeDto("hk3", "oo3")
        );
        seurantaDao.luoLaskenta("U0","", "", hakuOid, valintaryhmaOid, LaskentaTyyppi.VALINTARYHMA, true, null, null, hakukohdeOids);
        String uuid = seurantaDao.otaSeuraavaLaskentaTyonAlle();
        YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, LaskentaTila.VALMIS, HakukohdeTila.VALMIS, Optional.of(IlmoitusDto.ilmoitus("Toimiiko!")));
        LOG.error("### {}", new GsonBuilder().setPrettyPrinting().create().toJson(y));
        assertEquals(LaskentaTila.VALMIS, y.getTila());
        assertEquals(3, y.getHakukohteitaValmiina());
        assertEquals(3, y.getHakukohteitaYhteensa());
        assertEquals(0, y.getHakukohteitaKeskeytetty());
    }

    @Test
    public void testaaHakukohteetKerrallaOhitettu() {
        String hakuOid = "hakuOidKerrallaOhitettu";
        String valintaryhmaOid = "vr1";
        Collection<HakukohdeDto> hakukohdeOids = Arrays.asList(
                new HakukohdeDto("hk1", "oo1"),
                new HakukohdeDto("hk2", "oo2"),
                new HakukohdeDto("hk3", "oo3")
        );

        seurantaDao.luoLaskenta("U0","", "", hakuOid, valintaryhmaOid, LaskentaTyyppi.VALINTARYHMA, true, null, null, hakukohdeOids);
        String uuid = seurantaDao.otaSeuraavaLaskentaTyonAlle();
        YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, LaskentaTila.VALMIS, HakukohdeTila.KESKEYTETTY, Optional.of(IlmoitusDto.ilmoitus("Toimiiko!")));
        LOG.error("### {}", new GsonBuilder().setPrettyPrinting().create().toJson(y));
        assertEquals(LaskentaTila.VALMIS, y.getTila());
        assertEquals(0, y.getHakukohteitaValmiina());
        assertEquals(3, y.getHakukohteitaYhteensa());
        assertEquals(3, y.getHakukohteitaKeskeytetty());
    }

    @Test
    public void testaaTyonAlleOttaminenPalauttaaVanhimmanAloittamattaOlleenLaskennan() {
        Collection<HakukohdeDto> hakukohdeOids = Arrays.asList(new HakukohdeDto("h1", "o1"), new HakukohdeDto("h2", "o2"));
        String oldestUuid = seurantaDao.luoLaskenta("U0","", "", "hk1", null, LaskentaTyyppi.HAKU, true, null, null, hakukohdeOids).getUuid();
        String newestUuid = seurantaDao.luoLaskenta("U0","", "", "hk2", null, LaskentaTyyppi.HAKU, true, null, null, hakukohdeOids).getUuid();
        assertEquals(oldestUuid, seurantaDao.otaSeuraavaLaskentaTyonAlle());
        assertEquals(newestUuid, seurantaDao.otaSeuraavaLaskentaTyonAlle());
    }
    @Test
    public void testaaHaeYhteenvedotAlkamattomilleUUIDeille() {
        String uuid1 = luoUusiLaskenta(Optional.of(randomHakukohde()));
        String uuid2 = luoUusiLaskenta(Optional.of(randomHakukohde()));
        Collection<YhteenvetoDto> yhteenvetoDtos = seurantaDao.haeYhteenvedotAlkamattomille(Arrays.asList(uuid1, uuid2));
        Assert.assertEquals(2, yhteenvetoDtos.size());
        Map<String, YhteenvetoDto> uuidToYhteenveto = yhteenvetoDtos.stream().collect(Collectors.toMap(y -> y.getUuid(), y -> y));
        Integer jonosija1 = uuidToYhteenveto.get(uuid1).getJonosija();
        Integer jonosija2 = uuidToYhteenveto.get(uuid2).getJonosija();
        Assert.assertTrue(jonosija1 < jonosija2);
    }
    private String luoUusiLaskenta(Optional<String> hakukohdeOid) {
        Collection<HakukohdeDto> hakukohdeOids = Arrays.asList(new HakukohdeDto(hakukohdeOid.orElse("h1"), "o1"), new HakukohdeDto("h2", "o2"));
        String uuid = seurantaDao.luoLaskenta("U0","", "", "hk1", null, LaskentaTyyppi.HAKU, true, null, null, hakukohdeOids).getUuid();
        return uuid;
    }
    private String aloitaUusiLaskenta(Optional<String> hakukohdeOid) {
        luoUusiLaskenta(hakukohdeOid);
        return seurantaDao.otaSeuraavaLaskentaTyonAlle();
    }

    private void assertOikeaLaskentaEiOleNull(String uuid, YhteenvetoDto y) {
        assertNotNull(y);
        assertEquals(uuid, y.getUuid());
    }
}
