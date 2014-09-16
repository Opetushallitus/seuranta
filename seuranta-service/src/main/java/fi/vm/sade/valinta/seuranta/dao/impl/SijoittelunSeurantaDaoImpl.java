package fi.vm.sade.valinta.seuranta.dao.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import com.google.common.collect.Lists;

import fi.vm.sade.valinta.seuranta.dao.SijoittelunSeurantaDao;
import fi.vm.sade.valinta.seuranta.sijoittelu.domain.Sijoittelu;
import fi.vm.sade.valinta.seuranta.sijoittelu.dto.SijoitteluDto;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
@Component
public class SijoittelunSeurantaDaoImpl implements SijoittelunSeurantaDao {

	private final Datastore datastore;
	@Autowired
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
			s = new Sijoittelu(hakuOid, false, null, null, null);
		}
		return sijoitteluAsSijoitteluDto(s);
	}
	private SijoitteluDto sijoitteluAsSijoitteluDto(Sijoittelu s) {
		return new SijoitteluDto(s.getHakuOid(), s.isJatkuvaSijoitteluPaalla(), s.getViimeksiAjettu(), null, s.getAloitusajankohta(), s.getAjotiheys());
	}
	@Override
	public SijoitteluDto asetaJatkuvaSijoittelu(String hakuOid, boolean jatkuvaSijoittelu) {
		Sijoittelu s = datastore.find(Sijoittelu.class).field("hakuOid").equal(hakuOid)
				.get();
		if(s == null) {
			s = new Sijoittelu(hakuOid, jatkuvaSijoittelu, null, null, null);
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
			s = new Sijoittelu(hakuOid, false, new Date(), new Date(), 0);
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

    @Override
    public SijoitteluDto paivitaAloitusAjankohta(String hakuOid, Date aloitusajankohta, Integer ajotiheys) {
        Sijoittelu s = datastore.find(Sijoittelu.class).field("hakuOid").equal(hakuOid)
                .get();
        if(s == null) {
            s = new Sijoittelu(hakuOid, false, new Date(), new Date(), 0);
        } else {
        }
        datastore.save(s);
        return sijoitteluAsSijoitteluDto(s);
    }
}
