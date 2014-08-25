package fi.vm.sade.valinta.seuranta.dao.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import fi.vm.sade.valinta.seuranta.dao.SeurantaDao;
import fi.vm.sade.valinta.seuranta.domain.Ilmoitus;
import fi.vm.sade.valinta.seuranta.domain.Laskenta;
import fi.vm.sade.valinta.seuranta.dto.HakukohdeTila;
import fi.vm.sade.valinta.seuranta.dto.IlmoitusDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTila;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTyyppi;
import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
@Component
public class SeurantaDaoImpl implements SeurantaDao {
	private final static Logger LOG = LoggerFactory
			.getLogger(SeurantaDaoImpl.class);
	private Datastore datastore;
	private static final Map<String, Integer> YHTEENVETO_FIELDS = createYhteenvetoFields();

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
		return m.asDto();
	}

	@Override
	public Collection<YhteenvetoDto> haeKaynnissaOlevienYhteenvedotHaulle(
			String hakuOid) {
		Map<String, Object> eqs = Maps.newHashMap();
		eqs.put("hakuOid", hakuOid);
		eqs.put("tila", LaskentaTila.MENEILLAAN);
		DBCollection collection = datastore.getCollection(Laskenta.class);
		AggregationOutput aggregation = collection.aggregate(dbobjs(
				dbobjmap("$match", eqs),
				dbobjmap("$project", YHTEENVETO_FIELDS)));
		Iterator<DBObject> i = aggregation.results().iterator();
		if (!i.hasNext()) {
			return Collections.emptyList();
		}
		return Lists.newArrayList(i).stream()
				.map(result -> dbObjectAsYhteenvetoDto(result))
				.collect(Collectors.toList());
	}

	@Override
	public Collection<YhteenvetoDto> haeYhteenvedotHaulle(String hakuOid) {
		DBCollection collection = datastore.getCollection(Laskenta.class);
		AggregationOutput aggregation = collection.aggregate(dbobjs(
		// $match
				dbobj("$match", dbobj("hakuOid", hakuOid)),
				// $project
				dbobjmap("$project", YHTEENVETO_FIELDS)));

		Iterator<DBObject> i = aggregation.results().iterator();
		if (!i.hasNext()) {
			return Collections.emptyList();
		}
		return Lists.newArrayList(i).stream()
				.map(result -> dbObjectAsYhteenvetoDto(result))
				.collect(Collectors.toList());
	}

	@Override
	public Collection<YhteenvetoDto> haeYhteenvedotHaulle(String hakuOid,
			LaskentaTyyppi tyyppi) {
		if (tyyppi == null) {
			LOG.error("Laskentatyyppi null kutsussa hakea yhteenvedot tietylle laskentatyypille haussa.");
			throw new RuntimeException(
					"Laskentatyyppi null kutsussa hakea yhteenvedot tietylle laskentatyypille haussa.");
		}
		DBCollection collection = datastore.getCollection(Laskenta.class);
		Map<String, Object> matchValues = Maps.newHashMap();
		matchValues.put("hakuOid", hakuOid);
		matchValues.put("tyyppi", tyyppi.toString());
		AggregationOutput aggregation = collection.aggregate(
		// $match
				dbobjs(dbobjmap("$match", matchValues),
				// $project
						dbobjmap("$project", YHTEENVETO_FIELDS)));

		Iterator<DBObject> i = aggregation.results().iterator();
		if (!i.hasNext()) {
			return Collections.emptyList();
		}
		return Lists.newArrayList(i).stream()
				.map(result -> dbObjectAsYhteenvetoDto(result))
				.collect(Collectors.toList());
	}

	public BasicDBObject dbobjmap(String key, Map<?, ?> value) {
		return new BasicDBObject(key, value);
	}

	public BasicDBObject dbobj(String key, Object value) {
		return new BasicDBObject(key, value);
	}

	public List<DBObject> dbobjs(BasicDBObject... objs) {
		return Lists.newArrayList(objs);
	}

	public YhteenvetoDto haeYhteenveto(String uuid) {
		DBCollection collection = datastore.getCollection(Laskenta.class);
		AggregationOutput aggregation = collection.aggregate(dbobjs(
		// $match
				dbobj("$match", dbobj("_id", new ObjectId(uuid))),
				// $project
				dbobjmap("$project", YHTEENVETO_FIELDS)));
		Iterator<DBObject> i = aggregation.results().iterator();
		if (!i.hasNext()) {
			return null;
		}
		return dbObjectAsYhteenvetoDto(i.next());
	}

	private YhteenvetoDto dbObjectAsYhteenvetoDto(DBObject result) {
		if (result == null) {
			return null;
		}
		String uuid = result.get("_id").toString();
		String hakuOid = result.get("hakuOid").toString();
		Date luotu = (Date) result.get("luotu");
		LaskentaTila tila = LaskentaTila.valueOf(result.get("tila").toString());
		int hakukohteitaYhteensa = (Integer) result.get("hakukohteitaYhteensa");
		int hakukohteitaKeskeytetty = (Integer) result
				.get("hakukohteitaOhitettu");
		int hakukohteitaTekematta = (Integer) result
				.get("hakukohteitaTekematta");
		int hakukohteitaValmiina = (hakukohteitaYhteensa - hakukohteitaKeskeytetty)
				- hakukohteitaTekematta;
		long luotuTimestamp;
		if (luotu == null) {
			luotuTimestamp = new Date().getTime();
		} else {
			luotuTimestamp = luotu.getTime();
		}
		return new YhteenvetoDto(uuid, hakuOid, luotuTimestamp, tila,
				hakukohteitaYhteensa, hakukohteitaValmiina,
				hakukohteitaKeskeytetty);
	}

	private YhteenvetoDto laskentaAsYhteenvetoDto(Laskenta laskenta) {
		if (laskenta == null) {
			return null;
		}
		String uuid = laskenta.getUuid().toString();
		String hakuOid = laskenta.getHakuOid();
		Date luotu = laskenta.getLuotu();
		LaskentaTila tila = laskenta.getTila();
		int hakukohteitaYhteensa = laskenta.getHakukohteitaYhteensa();
		int hakukohteitaKeskeytetty = laskenta.getHakukohteitaOhitettu();
		int hakukohteitaTekematta = laskenta.getHakukohteitaTekematta();
		int hakukohteitaValmiina = (hakukohteitaYhteensa - hakukohteitaKeskeytetty)
				- hakukohteitaTekematta;
		long luotuTimestamp;
		if (luotu == null) {
			luotuTimestamp = new Date().getTime();
		} else {
			luotuTimestamp = luotu.getTime();
		}
		return new YhteenvetoDto(uuid, hakuOid, luotuTimestamp, tila,
				hakukohteitaYhteensa, hakukohteitaValmiina,
				hakukohteitaKeskeytetty);
	}

	@Override
	public void poistaLaskenta(String uuid) {
		Query<Laskenta> query = datastore.createQuery(Laskenta.class)
				.field("_id").equal(new ObjectId(uuid));
		datastore.delete(query);
	}

	@Override
	public LaskentaDto resetoiEiValmiitHakukohteet(String uuid,
			boolean nollaaIlmoitukset) {
		ObjectId oid = new ObjectId(uuid);
		Laskenta m = datastore.find(Laskenta.class).field("_id").equal(oid)
				.get();
		if (m == null) {
			throw new RuntimeException("Laskentaa ei ole olemassa uuid:lla "
					+ uuid);
		}
		List<String> o = orEmpty(m.getOhitettu());
		List<String> t = orEmpty(m.getTekematta());
		List<String> uusiTekematta = Lists.newArrayListWithCapacity(o.size()
				+ t.size());
		uusiTekematta.addAll(o);
		uusiTekematta.addAll(t);
		Query<Laskenta> query = datastore.createQuery(Laskenta.class)
				.field("_id").equal(new ObjectId(uuid));
		UpdateOperations<Laskenta> ops = datastore
				.createUpdateOperations(Laskenta.class)
				.set("tila", LaskentaTila.MENEILLAAN)
				.set("hakukohteitaTekematta", uusiTekematta.size())
				.set("hakukohteitaOhitettu", 0)
				//
				.set("valmiit", orEmpty(m.getValmiit()))
				//
				.set("tekematta", uusiTekematta)
				.set("ohitettu", Collections.emptyList());
		if (nollaaIlmoitukset) {
			ops.set("ilmoitukset", Collections.emptyMap());
		}
		Laskenta uusi = datastore.findAndModify(query, ops);
		return uusi.asDto();
	}

	private static <T> List<T> orEmpty(List<T> t) {
		if (t == null) {
			return Collections.emptyList();
		}
		return t;
	}

	@Override
	public YhteenvetoDto merkkaaTila(String uuid, LaskentaTila tila) {
		Query<Laskenta> query = datastore.createQuery(Laskenta.class)
				.field("_id").equal(new ObjectId(uuid));
		UpdateOperations<Laskenta> ops = datastore
				.createUpdateOperations(Laskenta.class);
		ops.set("tila", tila);
		return laskentaAsYhteenvetoDto(datastore.findAndModify(query, ops));
	}

	@Override
	public YhteenvetoDto lisaaIlmoitus(String uuid, String hakukohdeOid,
			IlmoitusDto ilmoitus) {
		Ilmoitus i = new Ilmoitus(ilmoitus.getTyyppi(), ilmoitus.getOtsikko(),
				ilmoitus.getData());
		Query<Laskenta> query = datastore.createQuery(Laskenta.class)
				.field("_id").equal(new ObjectId(uuid));
		UpdateOperations<Laskenta> ops = datastore.createUpdateOperations(
				Laskenta.class).add("ilmoitukset." + hakukohdeOid, i, true);
		return laskentaAsYhteenvetoDto(datastore.findAndModify(query, ops));
	}

	@Override
	public YhteenvetoDto merkkaaTila(String uuid, String hakukohdeOid,
			HakukohdeTila tila, IlmoitusDto ilmoitus) {
		// Valmistui
		Ilmoitus i = new Ilmoitus(ilmoitus.getTyyppi(), ilmoitus.getOtsikko(),
				ilmoitus.getData());
		if (HakukohdeTila.VALMIS.equals(tila)) {
			Query<Laskenta> query = datastore.createQuery(Laskenta.class)
					.field("_id").equal(new ObjectId(uuid));// .field("tekematta").contains(hakukohdeOid);
			UpdateOperations<Laskenta> ops = datastore
					.createUpdateOperations(Laskenta.class)
					.dec("hakukohteitaTekematta").add("valmiit", hakukohdeOid)
					.removeAll("tekematta", hakukohdeOid)
					.add("ilmoitukset." + hakukohdeOid, i, true);
			return laskentaAsYhteenvetoDto(datastore.findAndModify(query, ops));
		} else if (HakukohdeTila.KESKEYTETTY.equals(tila)) {
			Query<Laskenta> query = datastore.createQuery(Laskenta.class)
					.field("_id").equal(new ObjectId(uuid));
			// .field("tekematta").contains(hakukohdeOid);
			UpdateOperations<Laskenta> ops = datastore
					.createUpdateOperations(Laskenta.class)
					.inc("hakukohteitaOhitettu").dec("hakukohteitaTekematta")
					.add("ohitettu", hakukohdeOid)
					.removeAll("tekematta", hakukohdeOid)
					.add("ilmoitukset." + hakukohdeOid, i, true);
			return laskentaAsYhteenvetoDto(datastore.findAndModify(query, ops));
		} else {
			throw new RuntimeException(
					"Tekematta tilaa ei saa asettaa manuaalisesti");
		}
	}

	@Override
	public YhteenvetoDto merkkaaTila(String uuid, String hakukohdeOid,
			HakukohdeTila tila) {
		// Valmistui
		if (HakukohdeTila.VALMIS.equals(tila)) {
			// Query<Laskenta> query = datastore.createQuery(Laskenta.class)
			// .field("_id").equal(new
			// ObjectId(uuid)).field("valmiit").not().contains(hakukohdeOid);
			Query<Laskenta> query = datastore.createQuery(Laskenta.class)
					.field("_id").equal(new ObjectId(uuid)).field("tekematta")
					.contains(hakukohdeOid);
			;
			System.err.println(query.toString());
			//
			UpdateOperations<Laskenta> ops = datastore
					.createUpdateOperations(Laskenta.class)
					.dec("hakukohteitaTekematta").add("valmiit", hakukohdeOid)
					.removeAll("tekematta", hakukohdeOid);
			return laskentaAsYhteenvetoDto(datastore.findAndModify(query, ops));
		} else if (HakukohdeTila.KESKEYTETTY.equals(tila)) {
			Query<Laskenta> query = datastore.createQuery(Laskenta.class)
					.field("_id").equal(new ObjectId(uuid));
			// .field("tekematta").contains(hakukohdeOid);
			UpdateOperations<Laskenta> ops = datastore
					.createUpdateOperations(Laskenta.class)
					.inc("hakukohteitaOhitettu").dec("hakukohteitaTekematta")
					.add("ohitettu", hakukohdeOid)
					.removeAll("tekematta", hakukohdeOid);
			return laskentaAsYhteenvetoDto(datastore.findAndModify(query, ops));
		} else {
			throw new RuntimeException(
					"Tekematta tilaa ei saa asettaa manuaalisesti");
		}
	}

	public String luoLaskenta(String hakuOid, LaskentaTyyppi tyyppi,
			Collection<String> hakukohdeOids) {
		if (hakukohdeOids == null || hakukohdeOids.isEmpty()) {
			throw new RuntimeException(
					"Seurantaa ei muodosteta tyhjalle hakukohdejoukolle. Onko haulla hakukohteita tai rajaako hakukohdemaski kaikki hakukohteet pois? HakuOid = "
							+ hakuOid);
		}
		Laskenta l = new Laskenta(hakuOid, tyyppi, null, null, hakukohdeOids);
		datastore.save(l);
		return l.getUuid().toString();
	}

	public String luoLaskenta(String hakuOid, LaskentaTyyppi tyyppi,
			int valinnanvaihe, boolean valintakoelaskenta,
			Collection<String> hakukohdeOids) {
		if (hakukohdeOids == null || hakukohdeOids.isEmpty()) {
			throw new RuntimeException(
					"Seurantaa ei muodosteta tyhjalle hakukohdejoukolle. Onko haulla hakukohteita tai rajaako hakukohdemaski kaikki hakukohteet pois? HakuOid = "
							+ hakuOid);
		}
		Laskenta l = new Laskenta(hakuOid, tyyppi, valinnanvaihe,
				valintakoelaskenta, hakukohdeOids);
		datastore.save(l);
		return l.getUuid().toString();
	}

	public void lisaaIlmoitus(String uuid, String hakukohdeOid,
			Ilmoitus ilmoitus) {
		Query<Laskenta> query = datastore.createQuery(Laskenta.class)
				.field("_id").equal(new ObjectId(uuid));
		UpdateOperations<Laskenta> ops = datastore.createUpdateOperations(
				Laskenta.class).add("ilmoitukset." + hakukohdeOid, ilmoitus);
		datastore.update(query, ops);
	}

	private static Map<String, Integer> createYhteenvetoFields() {
		Map<String, Integer> fields = Maps.newHashMap();
		fields.put("_id", 1);
		fields.put("hakuOid", 1);
		fields.put("luotu", 1);
		fields.put("tila", 1);
		fields.put("hakukohteitaYhteensa", 1);
		fields.put("hakukohteitaTekematta", 1);
		fields.put("hakukohteitaOhitettu", 1);
		return fields;
	}
}
