package fi.vm.sade.valinta.seuranta.dao.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.Action;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fi.vm.sade.valinta.seuranta.dao.SeurantaDao;
import fi.vm.sade.valinta.seuranta.domain.Ilmoitus;
import fi.vm.sade.valinta.seuranta.dto.HakukohdeTila;
import fi.vm.sade.valinta.seuranta.dto.IlmoitusTyyppi;
import fi.vm.sade.valinta.seuranta.dto.LaskentaDto;
import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;
import fi.vm.sade.valinta.seuranta.testcontext.SeurantaConfiguration;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
@ContextConfiguration(classes = SeurantaConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SeurantaDaoTest {

	@Autowired
	private SeurantaDao seurantaDao;
	
	@Test
	public void testaaSeuranta() {
		String hakuOid = "hakuOid";
		Collection<String> hakukohdeOids = Arrays.asList("hk1","hk2","hk3");
		
		String uuid = seurantaDao.luoLaskenta(hakuOid, hakukohdeOids);
		//YhteenvetoDto yhteenveto =seurantaDao.haeYhteenveto(uuid);
		seurantaDao.lisaaIlmoitus(uuid, IlmoitusTyyppi.VAROITUS,new Ilmoitus("hk1","i1",Arrays.asList("d1","d2")));
		seurantaDao.merkkaaTila(uuid, "hk3", HakukohdeTila.KESKEYTETTY);
		//seurantaDao.
		YhteenvetoDto yhteenveto =seurantaDao.haeYhteenveto(uuid);
		
		LaskentaDto laskenta =seurantaDao.haeLaskenta(uuid);
		//System.err.println(new GsonBuilder().setPrettyPrinting().create().toJson(yhteenveto));
		//System.err.println(new GsonBuilder().setPrettyPrinting().create().toJson(laskenta));

	}
}
