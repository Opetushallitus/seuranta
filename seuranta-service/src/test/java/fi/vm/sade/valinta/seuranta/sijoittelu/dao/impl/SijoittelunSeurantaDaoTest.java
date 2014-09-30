package fi.vm.sade.valinta.seuranta.sijoittelu.dao.impl;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.vm.sade.valinta.seuranta.sijoittelu.dao.SijoittelunSeurantaDao;
import fi.vm.sade.valinta.seuranta.sijoittelu.dto.SijoitteluDto;
import fi.vm.sade.valinta.seuranta.testcontext.SeurantaConfiguration;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
@ContextConfiguration(classes = SeurantaConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SijoittelunSeurantaDaoTest {
	@Autowired
	private SijoittelunSeurantaDao seurantaDao;
	
	@Test
	public void testaaSeuranta() {
		seurantaDao.asetaJatkuvaSijoittelu("haku1", false);
		
		
		SijoitteluDto haku1 = seurantaDao.hae("haku1");
		Assert.assertEquals(haku1.isAjossa(), false);
		
		seurantaDao.asetaJatkuvaSijoittelu("haku2", true);
		SijoitteluDto haku2 = seurantaDao.hae("haku2");
		Assert.assertEquals(haku2.isAjossa(), true);
		
		Assert.assertTrue(seurantaDao.hae().size() == 2);
	}
}
