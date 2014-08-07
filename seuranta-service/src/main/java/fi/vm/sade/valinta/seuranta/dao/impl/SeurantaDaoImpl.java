package fi.vm.sade.valinta.seuranta.dao.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import fi.vm.sade.valinta.seuranta.dto.LaskentaDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTila;
import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
@Component
public class SeurantaDaoImpl implements SeurantaDao {

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
		AggregationOutput aggregation = collection.aggregate(new BasicDBObject(
				"$match", new BasicDBObject(eqs)), new BasicDBObject(
				"$project", new BasicDBObject(YHTEENVETO_FIELDS)));
		return Lists.newArrayList(aggregation.results().iterator()).stream()
				.map(result -> dbObjectAsYhteenvetoDto(result))
				.collect(Collectors.toList());
	}

	@Override
	public Collection<YhteenvetoDto> haeYhteenvedotHaulle(String hakuOid) {
		DBCollection collection = datastore.getCollection(Laskenta.class);
		AggregationOutput aggregation = collection.aggregate(new BasicDBObject(
				"$match", new BasicDBObject("hakuOid", hakuOid)),
				new BasicDBObject("$project", new BasicDBObject(
						YHTEENVETO_FIELDS)));
		return Lists.newArrayList(aggregation.results().iterator()).stream()
				.map(result -> dbObjectAsYhteenvetoDto(result))
				.collect(Collectors.toList());
	}

	public YhteenvetoDto haeYhteenveto(String uuid) {
		DBCollection collection = datastore.getCollection(Laskenta.class);
		AggregationOutput aggregation = collection.aggregate(new BasicDBObject(
				"$match", new BasicDBObject("_id", new ObjectId(uuid))),
				new BasicDBObject("$project", new BasicDBObject(
						YHTEENVETO_FIELDS)));
		return dbObjectAsYhteenvetoDto(aggregation.results().iterator().next());
	}

	private YhteenvetoDto dbObjectAsYhteenvetoDto(DBObject result) {
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

		return new YhteenvetoDto(uuid, hakuOid, luotu, tila,
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
		List<String> o = m.getOhitettu();
		List<String> t = m.getTekematta();
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
				.set("hakukohteitaOhitettu", 0).set("valmiit", m.getValmiit())
				.set("tekematta", uusiTekematta)
				.set("ohitettu", Collections.emptyList());
		if (nollaaIlmoitukset) {
			ops.set("ilmoitukset", Collections.emptyMap());
		}
		Laskenta uusi = datastore.findAndModify(query, ops);
		return uusi.asDto();
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
	public void lisaaIlmoitus(String uuid, String hakukohdeOid,
			IlmoitusDto ilmoitus) {
		Ilmoitus i = new Ilmoitus(ilmoitus.getTyyppi(), ilmoitus.getOtsikko(),
				ilmoitus.getData());
		Query<Laskenta> query = datastore.createQuery(Laskenta.class)
				.field("_id").equal(new ObjectId(uuid));
		UpdateOperations<Laskenta> ops = datastore.createUpdateOperations(
				Laskenta.class).add("ilmoitukset." + hakukohdeOid, i, true);
		datastore.findAndModify(query, ops);
	}

	@Override
	public void merkkaaTila(String uuid, String hakukohdeOid,
			HakukohdeTila tila, IlmoitusDto ilmoitus) {
		// Valmistui
		Ilmoitus i = new Ilmoitus(ilmoitus.getTyyppi(), ilmoitus.getOtsikko(),
				ilmoitus.getData());
		if (HakukohdeTila.VALMIS.equals(tila)) {
			Query<Laskenta> query = datastore.createQuery(Laskenta.class)
					.field("_id").equal(new ObjectId(uuid)).field("tekematta")
					.contains(hakukohdeOid);
			UpdateOperations<Laskenta> ops = datastore
					.createUpdateOperations(Laskenta.class)
					.dec("hakukohteitaTekematta").add("valmiit", hakukohdeOid)
					.removeAll("tekematta", hakukohdeOid)
					.add("ilmoitukset." + hakukohdeOid, i, true);
			datastore.findAndModify(query, ops);
		} else if (HakukohdeTila.KESKEYTETTY.equals(tila)) {
			Query<Laskenta> query = datastore.createQuery(Laskenta.class)
					.field("_id").equal(new ObjectId(uuid)).field("tekematta")
					.contains(hakukohdeOid);
			UpdateOperations<Laskenta> ops = datastore
					.createUpdateOperations(Laskenta.class)
					.inc("hakukohteitaOhitettu").dec("hakukohteitaTekematta")
					.add("ohitettu", hakukohdeOid)
					.removeAll("tekematta", hakukohdeOid)
					.add("ilmoitukset." + hakukohdeOid, i, true);
			datastore.findAndModify(query, ops);
		} else {
			throw new RuntimeException(
					"Tekematta tilaa ei saa asettaa manuaalisesti");
		}
	}

	@Override
	public void merkkaaTila(String uuid, String hakukohdeOid, HakukohdeTila tila) {
		// Valmistui
		if (HakukohdeTila.VALMIS.equals(tila)) {
			// Query<Laskenta> query = datastore.createQuery(Laskenta.class)
			// .field("_id").equal(new
			// ObjectId(uuid)).field("valmiit").not().contains(hakukohdeOid);
			Query<Laskenta> query = datastore.createQuery(Laskenta.class)
					.field("_id").equal(new ObjectId(uuid)).field("tekematta")
					.contains(hakukohdeOid);
			UpdateOperations<Laskenta> ops = datastore
					.createUpdateOperations(Laskenta.class)
					.dec("hakukohteitaTekematta").add("valmiit", hakukohdeOid)
					.removeAll("tekematta", hakukohdeOid);
			datastore.findAndModify(query, ops);
		} else if (HakukohdeTila.KESKEYTETTY.equals(tila)) {
			Query<Laskenta> query = datastore.createQuery(Laskenta.class)
					.field("_id").equal(new ObjectId(uuid)).field("tekematta")
					.contains(hakukohdeOid);
			UpdateOperations<Laskenta> ops = datastore
					.createUpdateOperations(Laskenta.class)
					.inc("hakukohteitaOhitettu").dec("hakukohteitaTekematta")
					.add("ohitettu", hakukohdeOid)
					.removeAll("tekematta", hakukohdeOid);
			datastore.findAndModify(query, ops);
		} else {
			throw new RuntimeException(
					"Tekematta tilaa ei saa asettaa manuaalisesti");
		}
	}

	public String luoLaskenta(String hakuOid, Collection<String> hakukohdeOids) {
		Laskenta l = new Laskenta(hakuOid, hakukohdeOids);
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
