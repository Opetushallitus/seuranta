package fi.vm.sade.valinta.seuranta.kela.dao.impl;

import java.util.Collection;

import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fi.vm.sade.valinta.seuranta.kela.dao.KelaDao;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
public class KelaDaoImpl implements KelaDao {

	private final static Logger LOG = LoggerFactory
			.getLogger(KelaDaoImpl.class);
	private Datastore datastore;

	@Autowired
	public KelaDaoImpl(Datastore datastore) {
		this.datastore = datastore;
	}

	@Override
	public Collection<String> merkkaaHakijatViedyksiKelanFtpPalvelimelle(
			String hakuOid, Collection<String> hakijaOids) {

		return null;
	}

	@Override
	public Collection<String> tarkistaOnkoHakijatJoVietyKelanFtpPalvelimelle(
			String hakuOid, Collection<String> hakijaOids) {

		return null;
	}

}
