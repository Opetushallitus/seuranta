package fi.vm.sade.valinta.seuranta.dao.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.GsonBuilder;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import fi.vm.sade.valinta.seuranta.dao.SeurantaDao;
import fi.vm.sade.valinta.seuranta.domain.Ilmoitus;
import fi.vm.sade.valinta.seuranta.domain.Laskenta;
import fi.vm.sade.valinta.seuranta.dto.HakukohdeDto;
import fi.vm.sade.valinta.seuranta.dto.HakukohdeTila;
import fi.vm.sade.valinta.seuranta.dto.IlmoitusDto;
import fi.vm.sade.valinta.seuranta.dto.IlmoitusTyyppi;
import fi.vm.sade.valinta.seuranta.dto.LaskentaDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTila;
import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;
import fi.vm.sade.valinta.seuranta.resource.impl.LaskennanSeurantaResourceImpl;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
@Component
public class SeurantaDaoImpl implements SeurantaDao {

	private Datastore datastore;

	@Autowired
	public SeurantaDaoImpl(Datastore datastore) {
		this.datastore = datastore;
	}

	@Override
	public LaskentaDto haeLaskenta(String uuid) {
		ObjectId oid = new ObjectId(uuid);
		Laskenta m = datastore.find(Laskenta.class).field("_id").equal(oid)
				.get();
		if (m == null) {
			throw new RuntimeException("Laskentaa ei ole olemassa uuid:lla "
					+ uuid);
		}
		// System.out.println(new GsonBuilder().setPrettyPrinting().create()
		// .toJson(m));
		List<HakukohdeDto> hakukohteet;
		Map<String, List<IlmoitusDto>> ilmots;
		if (m.getIlmoitukset() != null) {
			ilmots = Maps.newHashMap();
			for (Entry<IlmoitusTyyppi, Collection<Ilmoitus>> ilmot : m
					.getIlmoitukset().entrySet()) {
				for (Ilmoitus i0 : ilmot.getValue()) {
					String hk = i0.getHakukohdeOid();
					if (!ilmots.containsKey(hk)) {
						ilmots.put(hk, Lists.newArrayList());
					}
					ilmots.get(hk).add(
							new IlmoitusDto(ilmot.getKey(), i0.getOtsikko(), i0
									.getData()));
				}
			}
		} else {
			ilmots = Collections.emptyMap();
		}
		if (m.getHakukohteet() != null) {
			hakukohteet = Lists.newArrayList();
			for (Entry<HakukohdeTila, Collection<String>> entry : m
					.getHakukohteet().entrySet()) {
				if (entry == null || entry.getValue() != null) {
					for (String hakukohdeOid : entry.getValue()) {
						hakukohteet.add(new HakukohdeDto(hakukohdeOid, entry
								.getKey(), ilmots.get(hakukohdeOid)));
					}
				}
			}
		} else {
			hakukohteet = Collections.emptyList();
		}
		return new LaskentaDto(m.getUuid().toString(), m.getHakuOid(),
				m.getLuotu(), m.getTila(), hakukohteet);
	}

	@Override
	public Collection<YhteenvetoDto> haeKaynnissaOlevienYhteenvedotHaulle(
			String hakuOid) {
		Collection<YhteenvetoDto> y = Lists.newArrayList();
		for (Laskenta l : datastore.find(Laskenta.class).field("hakuOid")
				.equal(hakuOid).field("tila").equal(LaskentaTila.MENEILLAAN)
				.fetch()) {
			y.add(laskentaToYhteenveto(l));
		}
		return y;
	}

	@Override
	public Collection<YhteenvetoDto> haeYhteenvedotHaulle(String hakuOid) {
		Collection<YhteenvetoDto> y = Lists.newArrayList();
		for (Laskenta l : datastore.find(Laskenta.class).field("hakuOid")
				.equal(hakuOid).fetch()) {
			y.add(laskentaToYhteenveto(l));
		}
		return y;
	}

	public YhteenvetoDto haeYhteenveto(String uuid) {
		ObjectId oid = new ObjectId(uuid);
		// DBCollection collection = datastore.getCollection(Laskenta.class);
		//
		// AggregationOutput aggregation = collection
		// .aggregate(
		// // Haetaan valintatapajono oidin mukaan
		// new BasicDBObject("$match", new BasicDBObject(
		// "_id",oid)),
		// new BasicDBObject("$unwind", "$hakukohteet.VALMIS"),
		// new BasicDBObject("$group", new BasicDBObject(
		// "_id",oid).append("count", new BasicDBObject("$sum",1))));
		//
		// DBObject result = aggregation.results().iterator().next();

		Laskenta m = datastore.find(Laskenta.class).field("_id").equal(oid)
				.get();
		return laskentaToYhteenveto(m);
	}

	private YhteenvetoDto laskentaToYhteenveto(Laskenta m) {
		Collection<String> tmp = null;
		int valmiit = 0;
		tmp = m.getHakukohteet().get(HakukohdeTila.VALMIS);
		if (tmp != null) {
			valmiit = tmp.size();
		}
		int keskeytetty = 0;
		tmp = m.getHakukohteet().get(HakukohdeTila.KESKEYTETTY);
		if (tmp != null) {
			keskeytetty = tmp.size();
		}
		int tekematta = 0;
		tmp = m.getHakukohteet().get(HakukohdeTila.TEKEMATTA);
		if (tmp != null) {
			tekematta = tmp.size();
		}
		return new YhteenvetoDto(m.getUuid().toString(), m.getHakuOid(),
				m.getLuotu(), m.getTila(), tekematta + valmiit + keskeytetty,
				(int) valmiit, (int) keskeytetty);
	}

	@Override
	public void poistaLaskenta(String uuid) {
		Query<Laskenta> query = datastore.createQuery(Laskenta.class)
				.field("_id").equal(new ObjectId(uuid));
		datastore.delete(query);
	}

	@Override
	public void merkkaaTila(String uuid, LaskentaTila tila) {
		Query<Laskenta> query = datastore.createQuery(Laskenta.class)
				.field("_id").equal(new ObjectId(uuid));
		UpdateOperations<Laskenta> ops = datastore
				.createUpdateOperations(Laskenta.class);
		ops.set("tila", tila);

		datastore.update(query, ops);
	}

	@Override
	public void merkkaaTila(String uuid, String hakukohdeOid, HakukohdeTila tila) {
		Query<Laskenta> query = datastore.createQuery(Laskenta.class)
				.field("_id").equal(new ObjectId(uuid));
		Set<HakukohdeTila> s = Sets.newHashSet(HakukohdeTila.values());
		s.remove(tila);
		UpdateOperations<Laskenta> ops = datastore
				.createUpdateOperations(Laskenta.class);
		for (HakukohdeTila h : s) {
			ops.removeAll("hakukohteet." + h.name(), hakukohdeOid);
		}
		ops.add("hakukohteet." + tila.name(), hakukohdeOid);

		datastore.update(query, ops);
	}

	public String luoLaskenta(String hakuOid, Collection<String> hakukohdeOids) {
		Laskenta l = new Laskenta(hakuOid, hakukohdeOids);
		datastore.save(l);
		return l.getUuid().toString();
	}

	public void lisaaIlmoitus(String uuid, IlmoitusTyyppi tyyppi,
			Ilmoitus ilmoitus) {
		Query<Laskenta> query = datastore.createQuery(Laskenta.class)
				.field("_id").equal(new ObjectId(uuid));
		UpdateOperations<Laskenta> ops = datastore.createUpdateOperations(
				Laskenta.class).add("ilmoitukset." + tyyppi.name(), ilmoitus);

		datastore.update(query, ops);
	}
}
