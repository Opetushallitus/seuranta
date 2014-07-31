package fi.vm.sade.valinta.seuranta.dao.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.common.collect.Lists;

import fi.vm.sade.valinta.seuranta.dao.SijoittelunSeurantaDao;
import fi.vm.sade.valinta.seuranta.sijoittelu.domain.Sijoittelu;
import fi.vm.sade.valinta.seuranta.sijoittelu.dto.SijoitteluDto;

public class SijoittelunSeurantaDaoImpl implements SijoittelunSeurantaDao {

	private final Datastore datastore;
	
	public SijoittelunSeurantaDaoImpl(Datastore datastore) {
		this.datastore = datastore;
	}
	
	public Collection<SijoitteluDto> hae() {
		List<SijoitteluDto> s = Lists.newArrayList();
				
		for(Sijoittelu s0: Lists.newArrayList(datastore.find(Sijoittelu.class).iterator())) {
			s.add(sijoitteluAsSijoitteluDto(s0));
		}
		return s;
	}
	
	public SijoitteluDto hae(String hakuOid) {
		Sijoittelu s = datastore.find(Sijoittelu.class).field("hakuOid").equal(hakuOid)
				.get();
		if(s == null) {
			s = new Sijoittelu(hakuOid, false, null);
		}
		return sijoitteluAsSijoitteluDto(s);
	}
	private SijoitteluDto sijoitteluAsSijoitteluDto(Sijoittelu s) {
		return new SijoitteluDto(s.getHakuOid(), s.isJatkuvaSijoitteluPaalla(), s.getViimeksiAjettu(), null);
	}
	@Override
	public SijoitteluDto asetaJatkuvaSijoittelu(String hakuOid, boolean jatkuvaSijoittelu) {
		Sijoittelu s = datastore.find(Sijoittelu.class).field("hakuOid").equal(hakuOid)
				.get();
		if(s == null) {
			s = new Sijoittelu(hakuOid, jatkuvaSijoittelu, null);	
		} else {
			s.setJatkuvaSijoitteluPaalla(jatkuvaSijoittelu);
		}
		datastore.save(s);
		return sijoitteluAsSijoitteluDto(s);
	}
	@Override
	public SijoitteluDto paivitaViimeksiAjettuPaivamaara(String hakuOid) {
		Sijoittelu s = datastore.find(Sijoittelu.class).field("hakuOid").equal(hakuOid)
				.get();
		if(s == null) {
			s = new Sijoittelu(hakuOid, false, new Date());	
		} else {
			s.setViimeksiAjettu(new Date());
		}
		datastore.save(s);
		return sijoitteluAsSijoitteluDto(s);
	}
	public void poistaSijoittelu(String hakuOid) {
		Query<Sijoittelu> query = datastore.createQuery(Sijoittelu.class)
				.field("hakuOid").equal(hakuOid);
		datastore.delete(query);
	}
}
