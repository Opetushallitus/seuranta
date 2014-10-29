package fi.vm.sade.valinta.seuranta.laskenta.dao.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.Action;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;
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

/**
 * 
 * @author Jussi Jartamo
 * 
 */
@ContextConfiguration(classes = SeurantaConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SeurantaDaoTest {
	private static final Logger LOG = LoggerFactory
			.getLogger(SeurantaDaoTest.class);
	@Autowired
	private SeurantaDao seurantaDao;

	// YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, tila, hakukohteentila);
	// <br>
	// YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, tila);
	// <br>
	// YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, hakukohdeOid,
	// tila,ilmoitus);
	// <br>
	// YhteenvetoDto y = seurantaDao.lisaaIlmoitus(uuid, hakukohdeOid,ilmoitus);
	// <br>
	// YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, hakukohdeOid, tila);
	@Test
	public void testaaMerkkaaLaskennanTilaJaHakukohteidenTilaKerrallaValmistuneelleLaskennalle() {
		String hakuOid = "hk1";
		Collection<HakukohdeDto> hakukohdeOids = Arrays.asList(
				new HakukohdeDto("h1", "o1"), new HakukohdeDto("h2", "o2"));

		String uuid = seurantaDao.luoLaskenta(hakuOid, LaskentaTyyppi.HAKU,
				true, null, null, hakukohdeOids);

		LaskentaTila tila = LaskentaTila.VALMIS;
		HakukohdeTila hakukohteentila = HakukohdeTila.KESKEYTETTY;
		seurantaDao.merkkaaTila(uuid, tila);
		YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, tila, hakukohteentila);
		Assert.assertNotNull(y);
		Assert.assertEquals(uuid, y.getUuid());
	}

	@Test
	public void testaaMerkkaaTilaHakukohteelle() {
		String hakuOid = "hk1";
		Collection<HakukohdeDto> hakukohdeOids = Arrays.asList(
				new HakukohdeDto("h1", "o1"), new HakukohdeDto("h2", "o2"));

		String uuid = seurantaDao.luoLaskenta(hakuOid, LaskentaTyyppi.HAKU,
				true,null, null, hakukohdeOids);

		HakukohdeTila tila = HakukohdeTila.KESKEYTETTY;
		YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, "h1", tila);
		Assert.assertNotNull(y);
		Assert.assertEquals(uuid, y.getUuid());
	}

	@Test
	public void testaaLisaaIlmoitusHakukohteelle() {
		String hakuOid = "hk1";
		Collection<HakukohdeDto> hakukohdeOids = Arrays.asList(
				new HakukohdeDto("h1", "o1"), new HakukohdeDto("h2", "o2"));

		String uuid = seurantaDao.luoLaskenta(hakuOid, LaskentaTyyppi.HAKU,
				true,null, null, hakukohdeOids);

		YhteenvetoDto y = seurantaDao.lisaaIlmoitus(uuid, "h1",
				new IlmoitusDto(IlmoitusTyyppi.ILMOITUS, "Jee"));
		Assert.assertNotNull(y);
		Assert.assertEquals(uuid, y.getUuid());
	}

	@Test
	public void testaaMerkkaaLaskennanTilaJaHakukohteidenTilaKerralla() {
		String hakuOid = "hk1";
		Collection<HakukohdeDto> hakukohdeOids = Arrays.asList(
				new HakukohdeDto("h1", "o1"), new HakukohdeDto("h2", "o2"));

		String uuid = seurantaDao.luoLaskenta(hakuOid, LaskentaTyyppi.HAKU,
				true,null, null, hakukohdeOids);

		LaskentaTila tila = LaskentaTila.VALMIS;
		HakukohdeTila hakukohteentila = HakukohdeTila.KESKEYTETTY;

		YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, tila, hakukohteentila);
		Assert.assertNotNull(y);
		Assert.assertEquals(uuid, y.getUuid());
	}

	@Test
	public void testaaMerkkaaLaskennanTila() {
		String hakuOid = "hk1";
		Collection<HakukohdeDto> hakukohdeOids = Arrays.asList(
				new HakukohdeDto("h1", "o1"), new HakukohdeDto("h2", "o2"));

		String uuid = seurantaDao.luoLaskenta(hakuOid, LaskentaTyyppi.HAKU,
				true,null, null, hakukohdeOids);

		LaskentaTila tila = LaskentaTila.VALMIS;
		YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, tila);
		Assert.assertNotNull(y);
		Assert.assertEquals(uuid, y.getUuid());
	}

	@Test
	public void testaaMerkkaaLaskennanTilaMerkatulleLaskennalle() {
		String hakuOid = "hk1";
		Collection<HakukohdeDto> hakukohdeOids = Arrays.asList(
				new HakukohdeDto("h1", "o1"), new HakukohdeDto("h2", "o2"));

		String uuid = seurantaDao.luoLaskenta(hakuOid, LaskentaTyyppi.HAKU,
				true,null, null, hakukohdeOids);

		LaskentaTila tila = LaskentaTila.VALMIS;
		seurantaDao.merkkaaTila(uuid, tila);
		YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, tila);
		Assert.assertNotNull(y);
		Assert.assertEquals(uuid, y.getUuid());
	}

	@Test
	public void testaaMerkkaaHakukohteenTila() {
		String hakuOid = "hk1";
		Collection<HakukohdeDto> hakukohdeOids = Arrays.asList(
				new HakukohdeDto("h1", "o1"), new HakukohdeDto("h2", "o2"));

		String uuid = seurantaDao.luoLaskenta(hakuOid, LaskentaTyyppi.HAKU,
				true,null, null, hakukohdeOids);

		HakukohdeTila tila = HakukohdeTila.KESKEYTETTY;
		YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, "h1", tila,
				new IlmoitusDto(IlmoitusTyyppi.ILMOITUS, "Jee"));
		Assert.assertNotNull(y);
		Assert.assertEquals(uuid, y.getUuid());
	}

	@Ignore
	@Test
	public void testaaSeuranta() throws InterruptedException {
		String hakuOid = "hakuOid";
		Collection<HakukohdeDto> hakukohdeOids = Arrays.asList(
				new HakukohdeDto("hk1", "oo1"), new HakukohdeDto("hk2", "oo2"),
				new HakukohdeDto("hk3", "oo3"));

		String uuid = seurantaDao.luoLaskenta(hakuOid, LaskentaTyyppi.HAKU,
				true,null, null, hakukohdeOids);
		seurantaDao.merkkaaTila(uuid, "hk3", HakukohdeTila.KESKEYTETTY);
		seurantaDao.merkkaaTila(uuid, "hk2", HakukohdeTila.VALMIS);
		seurantaDao.merkkaaTila(uuid, "hk1", HakukohdeTila.KESKEYTETTY);
		seurantaDao.merkkaaTila(uuid, "hk1", HakukohdeTila.KESKEYTETTY);
		seurantaDao.merkkaaTila(uuid, "hk2", HakukohdeTila.VALMIS);
		seurantaDao.merkkaaTila(uuid, "hk3", HakukohdeTila.VALMIS);
		seurantaDao.luoLaskenta(hakuOid, LaskentaTyyppi.HAKU, true,null, null,
				hakukohdeOids);
		seurantaDao.lisaaIlmoitus(uuid, "hk1", new Ilmoitus(
				IlmoitusTyyppi.ILMOITUS, "Ei toimi", null));
		seurantaDao.haeYhteenvedotHaulle(hakuOid);
		Collection<YhteenvetoDto> yhteenvedot = seurantaDao
				.haeYhteenvedotHaulle(hakuOid, LaskentaTyyppi.HAKU);
		Assert.assertEquals(2, yhteenvedot.size());
		LaskentaDto l = seurantaDao.haeLaskenta(uuid);
		l.getHakukohteet().forEach(
				hk -> {
					LOG.info("Hakukohde {} ja organisaatio {}",
							hk.getHakukohdeOid(), hk.getOrganisaatioOid());
					Assert.assertTrue("Organisaatio Oid puuttui",
							hk.getOrganisaatioOid() != null);
					Assert.assertTrue("HakukohdeOid puuttui",
							hk.getHakukohdeOid() != null);
				});
		Assert.assertEquals(LaskentaTila.MENEILLAAN,
				seurantaDao.haeLaskenta(uuid).getTila());
		seurantaDao.merkkaaTila(uuid, LaskentaTila.VALMIS);
		Assert.assertEquals(LaskentaTila.VALMIS, seurantaDao.haeLaskenta(uuid)
				.getTila());
		seurantaDao.merkkaaTila(uuid, LaskentaTila.PERUUTETTU);
		Assert.assertEquals(LaskentaTila.VALMIS, seurantaDao.haeLaskenta(uuid)
				.getTila());
		seurantaDao.merkkaaTila(uuid, "hk3", HakukohdeTila.VALMIS,
				new IlmoitusDto(IlmoitusTyyppi.VAROITUS, "Hehei2"));
		seurantaDao.lisaaIlmoitus(uuid, "hk3", new IlmoitusDto(
				IlmoitusTyyppi.VAROITUS, "Hehei3"));
		seurantaDao.resetoiEiValmiitHakukohteet(uuid, false);
		org.junit.Assert.assertEquals(2,
				seurantaDao.haeYhteenvedotHaulle(hakuOid).size());
		seurantaDao.siivoa(DateTime.now().minusDays(1).toDate());
		org.junit.Assert.assertEquals(2,
				seurantaDao.haeYhteenvedotHaulle(hakuOid).size());
		seurantaDao.siivoa(DateTime.now().plusHours(1).toDate());
		org.junit.Assert.assertEquals(0,
				seurantaDao.haeYhteenvedotHaulle(hakuOid).size());
	}

	@Ignore
	@Test
	public void testaaHakukohteetKerrallaValmiiksi() {
		String hakuOid = "hakuOidKerrallaValmiiksi";
		Collection<HakukohdeDto> hakukohdeOids = Arrays.asList(
				new HakukohdeDto("hk1", "oo1"), new HakukohdeDto("hk2", "oo2"),
				new HakukohdeDto("hk3", "oo3"));

		String uuid = seurantaDao.luoLaskenta(hakuOid,
				LaskentaTyyppi.VALINTARYHMA, true,null, null, hakukohdeOids);
		YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, LaskentaTila.VALMIS,
				HakukohdeTila.VALMIS);
		// YhteenvetoDto y = seurantaDao.haeYhteenveto(uuid);
		LOG.error("### {}", new GsonBuilder().setPrettyPrinting().create()
				.toJson(y));
		Assert.assertEquals(LaskentaTila.VALMIS, y.getTila());
		Assert.assertEquals(3, y.getHakukohteitaValmiina());
		Assert.assertEquals(3, y.getHakukohteitaYhteensa());
		Assert.assertEquals(0, y.getHakukohteitaKeskeytetty());
	}

	@Ignore
	@Test
	public void testaaHakukohteetKerrallaOhitettu() {
		String hakuOid = "hakuOidKerrallaOhitettu";
		Collection<HakukohdeDto> hakukohdeOids = Arrays.asList(
				new HakukohdeDto("hk1", "oo1"), new HakukohdeDto("hk2", "oo2"),
				new HakukohdeDto("hk3", "oo3"));

		String uuid = seurantaDao.luoLaskenta(hakuOid,
				LaskentaTyyppi.VALINTARYHMA, true,null, null, hakukohdeOids);
		YhteenvetoDto y = seurantaDao.merkkaaTila(uuid, LaskentaTila.VALMIS,
				HakukohdeTila.KESKEYTETTY);
		// YhteenvetoDto y = seurantaDao.haeYhteenveto(uuid);
		LOG.error("### {}", new GsonBuilder().setPrettyPrinting().create()
				.toJson(y));
		Assert.assertEquals(LaskentaTila.VALMIS, y.getTila());
		Assert.assertEquals(0, y.getHakukohteitaValmiina());
		Assert.assertEquals(3, y.getHakukohteitaYhteensa());
		Assert.assertEquals(3, y.getHakukohteitaKeskeytetty());
	}
}
