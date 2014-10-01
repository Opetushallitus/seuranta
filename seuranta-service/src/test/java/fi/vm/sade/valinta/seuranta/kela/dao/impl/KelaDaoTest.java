package fi.vm.sade.valinta.seuranta.kela.dao.impl;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.vm.sade.valinta.seuranta.kela.dao.KelaDao;
import fi.vm.sade.valinta.seuranta.testcontext.MongoConfiguration;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
@ContextConfiguration(classes = { MongoConfiguration.class, KelaDaoTest.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class KelaDaoTest {
	@Bean
	public KelaDao createKelaDao(Datastore datastore) {
		return new KelaDaoImpl(datastore);
	}

	@Autowired
	private KelaDao kelaDao;

	@Test
	public void testaaKelaDao() {

	}
}
