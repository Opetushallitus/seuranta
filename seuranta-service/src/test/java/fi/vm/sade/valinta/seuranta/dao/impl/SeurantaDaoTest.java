package fi.vm.sade.valinta.seuranta.dao.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.Action;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fi.vm.sade.valinta.seuranta.dao.SeurantaDao;
import fi.vm.sade.valinta.seuranta.dto.HakukohdeTila;
import fi.vm.sade.valinta.seuranta.dto.IlmoitusDto;
import fi.vm.sade.valinta.seuranta.dto.IlmoitusTyyppi;
import fi.vm.sade.valinta.seuranta.dto.LaskentaDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTila;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTyyppi;
import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;
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

	@Test
	public void testaaSeuranta() throws InterruptedException {
		String hakuOid = "hakuOid";
		Collection<String> hakukohdeOids = Arrays.asList("hk1", "hk2", "hk3");

		String uuid = seurantaDao.luoLaskenta(hakuOid, LaskentaTyyppi.HAKU,
				hakukohdeOids);
		seurantaDao.merkkaaTila(uuid, "hk3", HakukohdeTila.KESKEYTETTY);
		seurantaDao.merkkaaTila(uuid, "hk2", HakukohdeTila.VALMIS);
		seurantaDao.merkkaaTila(uuid, "hk1", HakukohdeTila.KESKEYTETTY);
		seurantaDao.merkkaaTila(uuid, "hk1", HakukohdeTila.KESKEYTETTY);
		seurantaDao.merkkaaTila(uuid, "hk2", HakukohdeTila.VALMIS);
		seurantaDao.merkkaaTila(uuid, "hk3", HakukohdeTila.VALMIS);
		seurantaDao.luoLaskenta(hakuOid, LaskentaTyyppi.HAKU, hakukohdeOids);
		seurantaDao.lisaaIlmoitus(uuid, "hk1", new Ilmoitus(
				IlmoitusTyyppi.ILMOITUS, "Ei toimi", null));
		seurantaDao.haeYhteenvedotHaulle(hakuOid);
		Collection<YhteenvetoDto> yhteenvedot = seurantaDao
				.haeYhteenvedotHaulle(hakuOid, LaskentaTyyppi.HAKU);
		Assert.assertEquals(2, yhteenvedot.size());
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
}
