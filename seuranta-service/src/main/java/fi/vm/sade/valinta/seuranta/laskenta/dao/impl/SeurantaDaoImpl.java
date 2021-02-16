package fi.vm.sade.valinta.seuranta.laskenta.dao.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import fi.vm.sade.valinta.seuranta.dto.*;
import fi.vm.sade.valinta.seuranta.laskenta.dao.SeurantaDao;
import fi.vm.sade.valinta.seuranta.laskenta.domain.Ilmoitus;
import fi.vm.sade.valinta.seuranta.laskenta.domain.Laskenta;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static fi.vm.sade.valinta.seuranta.laskenta.domain.Laskenta.escapeHakukohdeOid;

@Component
public class SeurantaDaoImpl implements SeurantaDao {
    private final static Logger LOG = LoggerFactory.getLogger(SeurantaDaoImpl.class);
    private Datastore datastore;
    private static final BasicDBObject YHTEENVETO_PROJECTION = createYhteenvetoProjection();

    @Autowired
    public SeurantaDaoImpl(Datastore datastore) {
        this.datastore = datastore;
        try {
            this.datastore.ensureIndexes(Laskenta.class);
        } catch (Exception e) {
            LOG.error("Ensuring indexes failed!", e);
        }
        resetoiMeneillaanOlevatLaskennat();
    }

    @Override
    public void siivoa(Date asti) {
        Query<Laskenta> query = datastore.createQuery(Laskenta.class)
                .field("luotu").lessThanOrEq(asti);
        WriteResult wr = datastore.delete(query);
        LOG.info("Seurantakannasta poistettiin {} laskentaa!", wr.getN());
    }

    @Override
    public LaskentaDto haeLaskenta(String uuid) {
        ObjectId oid = new ObjectId(uuid);
        Laskenta m = datastore.find(Laskenta.class).field("_id").equal(oid).get();
        if (m == null) {
            LOG.error("Laskentaa ei ole olemassa uuid:lla {}", uuid);
            throw new RuntimeException("Laskentaa ei ole olemassa uuid:lla " + uuid);
        }
        return m.asDto(jonosijaProvider(), true);
    }

    @Override
    public Collection<YhteenvetoDto> haeKaynnissaOlevienYhteenvedotHaulle(String hakuOid) {
        Map<String, Object> eqs = Maps.newHashMap();
        eqs.put("hakuOid", hakuOid);
        eqs.put("tila", LaskentaTila.MENEILLAAN);
        DBCollection collection = datastore.getCollection(Laskenta.class);
        BasicDBObject query = dbobj(eqs);
        Iterator<DBObject> i = collection.find(query, YHTEENVETO_PROJECTION).iterator();
        if (!i.hasNext()) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(i).stream()
                .map(db -> dbObjectAsYhteenvetoDto(db, jonosijaProvider()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<YhteenvetoDto> haeYhteenvetoKaikilleLaskennoille() {
        DBCollection collection = datastore.getCollection(Laskenta.class);
        Iterator<DBObject> i = collection.find(new BasicDBObject(), YHTEENVETO_PROJECTION).iterator();
        if (!i.hasNext()) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(i).stream()
                .map(db -> dbObjectAsYhteenvetoDto(db,jonosijaProvider()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<YhteenvetoDto> haeYhteenvedotHaulle(String hakuOid) {
        DBCollection collection = datastore.getCollection(Laskenta.class);
        BasicDBObject query = dbobj("hakuOid", hakuOid);
        Iterator<DBObject> i = collection.find(query, YHTEENVETO_PROJECTION).iterator();
        if (!i.hasNext()) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(i).stream()
                .map(db -> dbObjectAsYhteenvetoDto(db,jonosijaProvider()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<YhteenvetoDto> haeYhteenvedotHaulle(String hakuOid, LaskentaTyyppi tyyppi) {
        if (tyyppi == null) {
            LOG.error("Laskentatyyppi null kutsussa hakea yhteenvedot tietylle laskentatyypille haussa.");
            throw new RuntimeException("Laskentatyyppi null kutsussa hakea yhteenvedot tietylle laskentatyypille haussa.");
        }
        DBCollection collection = datastore.getCollection(Laskenta.class);
        Map<String, Object> matchValues = Maps.newHashMap();
        matchValues.put("hakuOid", hakuOid);
        matchValues.put("tyyppi", tyyppi.toString());
        BasicDBObject query = dbobj(matchValues);
        Iterator<DBObject> i = collection.find(query, YHTEENVETO_PROJECTION).iterator();
        if (!i.hasNext()) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(i).stream()
                .map(db -> dbObjectAsYhteenvetoDto(db, jonosijaProvider()))
                .collect(Collectors.toList());
    }

    public BasicDBObject dbobj(String key, Object value) {
        return new BasicDBObject(key, value);
    }
    public BasicDBObject dbobj(Map<String, Object> map) {
        return new BasicDBObject(map);
    }
    public List<DBObject> dbobjs(BasicDBObject... objs) {
        return Lists.newArrayList(objs);
    }

    public YhteenvetoDto haeYhteenveto(String uuid) {
        DBCollection collection = datastore.getCollection(Laskenta.class);
        BasicDBObject query = dbobj("_id", new ObjectId(uuid));
        Iterator<DBObject> i = collection.find(query, YHTEENVETO_PROJECTION).iterator();
        if (!i.hasNext()) {
            return null;
        }
        return dbObjectAsYhteenvetoDto(i.next(),jonosijaProvider());
    }
    public Collection<YhteenvetoDto> haeYhteenvedotAlkamattomille(Collection<String> uuids) {
        DBCollection collection = datastore.getCollection(Laskenta.class);
        Map<String, Object> matchValues = Maps.newHashMap();
        matchValues.put("_id", dbobj("$in", uuids.stream().map(u -> new ObjectId(u)).collect(Collectors.toList())));
        matchValues.put("tila", LaskentaTila.ALOITTAMATTA.toString());
        BasicDBObject query = dbobj(matchValues);
        List<YhteenvetoDto> yhteenvetoDtos = StreamSupport.stream(collection.find(query, YHTEENVETO_PROJECTION).spliterator(), false).map(db ->
                dbObjectAsYhteenvetoDto(db, jonosijaProvider())).filter(Objects::nonNull).collect(Collectors.toList());

        return yhteenvetoDtos;
    }

    private static Map<String, Integer> createLuotuOnlyFields() {
        Map<String, Integer> fields = Maps.newHashMap();
        fields.put("_id", 1);
        fields.put("luotu", 1);
        return fields;
    }

    private YhteenvetoDto dbObjectAsYhteenvetoDto(DBObject result, BiFunction<Date,LaskentaTila,Integer> jonosijaSupplier) {
        if (result == null) {
            return null;
        }
        String uuid = result.get("_id").toString();
        String userOID = Optional.ofNullable(result.get("userOID")).orElse("").toString();
        String hakuOid = result.get("hakuOid").toString();
        String haunnimi = Optional.ofNullable(result.get("haunnimi")).orElse("").toString();
        String nimi = Optional.ofNullable(result.get("nimi")).orElse("").toString();
        Date luotu = (Date) result.get("luotu");
        LaskentaTila tila = LaskentaTila.valueOf(result.get("tila").toString());
        int hakukohteitaYhteensa = (Integer) result.get("hakukohteitaYhteensa");
        int hakukohteitaKeskeytetty = (Integer) result.get("hakukohteitaOhitettu");
        int hakukohteitaTekematta = (Integer) result.get("hakukohteitaTekematta");
        int hakukohteitaValmiina = (hakukohteitaYhteensa - hakukohteitaKeskeytetty) - hakukohteitaTekematta;
        LaskentaTyyppi tyyppi = LaskentaTyyppi.valueOf(result.get("tyyppi").toString());
        Integer valinnanvaihe = (Integer) result.get("valinnanvaihe");
        Boolean valintakoelaskenta = (Boolean) result.get("valintakoelaskenta");
        long luotuTimestamp;
        if (luotu == null) {
            luotuTimestamp = new Date().getTime();
        } else {
            luotuTimestamp = luotu.getTime();
        }
        return new YhteenvetoDto(uuid, userOID, haunnimi, nimi, hakuOid, luotuTimestamp, tila,
                hakukohteitaYhteensa, hakukohteitaValmiina, hakukohteitaKeskeytetty,
                jonosijaSupplier.apply(luotu,tila), tyyppi, valinnanvaihe, valintakoelaskenta);
    }

    private YhteenvetoDto laskentaAsYhteenvetoDto(Laskenta laskenta, BiFunction<Date,LaskentaTila,Integer> jonosijaSupplier) {
        if (laskenta == null) {
            return null;
        }
        final String uuid = laskenta.getUuid().toString();
        final String userOID = laskenta.getUserOID();
        final String hakuOid = laskenta.getHakuOid();
        final Date luotu = laskenta.getLuotu();
        final LaskentaTila tila = laskenta.getTila();
        final int hakukohteitaYhteensa = laskenta.getHakukohteitaYhteensa();
        final int hakukohteitaKeskeytetty = laskenta.getHakukohteitaOhitettu();
        final int hakukohteitaTekematta = laskenta.getHakukohteitaTekematta();
        final int hakukohteitaValmiina = (hakukohteitaYhteensa - hakukohteitaKeskeytetty) - hakukohteitaTekematta;
        final LaskentaTyyppi tyyppi = laskenta.getTyyppi();
        final Integer valinnanvaihe = laskenta.getValinnanvaihe();
        final Boolean valintakoelaskenta = laskenta.getValintakoelaskenta();
        long luotuTimestamp;
        if (luotu == null) {
            luotuTimestamp = new Date().getTime();
        } else {
            luotuTimestamp = luotu.getTime();
        }
        return new YhteenvetoDto(uuid, userOID, laskenta.getHaunnimi(), laskenta.getNimi(), hakuOid,
                luotuTimestamp, tila, hakukohteitaYhteensa, hakukohteitaValmiina, hakukohteitaKeskeytetty,
                jonosijaSupplier.apply(luotu,tila), tyyppi, valinnanvaihe, valintakoelaskenta);
    }

    @Override
    public void poistaLaskenta(String uuid) {
        Query<Laskenta> query = datastore.createQuery(Laskenta.class).field("_id").equal(new ObjectId(uuid));
        datastore.delete(query);
    }

    @Override
    public LaskentaDto resetoiEiValmiitHakukohteet(String uuid, boolean nollaaIlmoitukset) {
        ObjectId oid = new ObjectId(uuid);
        Laskenta m = datastore.find(Laskenta.class).field("_id").equal(oid).get();
        if (m == null) {
            throw new RuntimeException("Laskentaa ei ole olemassa uuid:lla " + uuid);
        }
        Optional<Laskenta> onGoing = orGetOnGoing(m);
        if(onGoing.isPresent()) {
            return onGoing.get().asDto(jonosijaProvider(), false);
        }
        return resetLaskenta(nollaaIlmoitukset, LaskentaTila.ALOITTAMATTA, m);
    }

    private LaskentaDto resetLaskenta(boolean nollaaIlmoitukset, LaskentaTila resetointiTila, Laskenta m) {
        List<String> o = orEmpty(m.getOhitettu());
        List<String> t = orEmpty(m.getTekematta());
        List<String> uusiTekematta = Lists.newArrayListWithCapacity(o.size() + t.size());
        uusiTekematta.addAll(o);
        uusiTekematta.addAll(t);
        Query<Laskenta> query = datastore
                .createQuery(Laskenta.class)
                .field("_id").equal(m.getUuid());
        UpdateOperations<Laskenta> ops = datastore
                .createUpdateOperations(Laskenta.class)
                .set("tila", resetointiTila)
                .set("hakukohteitaTekematta", uusiTekematta.size())
                .set("hakukohteitaOhitettu", 0)
                .set("valmiit", orEmpty(m.getValmiit()))
                .set("tekematta", uusiTekematta)
                .set("ohitettu", Collections.emptyList());
        if (nollaaIlmoitukset) {
            ops.set("ilmoitukset", Collections.emptyMap());
        }
        Laskenta uusi = datastore.findAndModify(query, ops);
        return uusi.asDto(jonosijaProvider(), true);
    }

    private static <T> List<T> orEmpty(List<T> t) {
        if (t == null) {
            return Collections.emptyList();
        }
        return t;
    }

    @Override
    public YhteenvetoDto merkkaaTila(String uuid, LaskentaTila tila, Optional<IlmoitusDto> ilmoitusDtoOptional) {
        final Query<Laskenta> query = datastore
                .createQuery(Laskenta.class)
                .field("_id").equal(new ObjectId(uuid))
                .field("tila").equal(LaskentaTila.MENEILLAAN);

        final UpdateOperations<Laskenta> ops = datastore.createUpdateOperations(Laskenta.class);
        ops.set("tila", tila);
        ilmoitusDtoOptional.ifPresent(ilmoitus -> ops.set("ilmoitus", asIlmoitus(ilmoitus)));
        return laskentaAsYhteenvetoDto(datastore.findAndModify(query, ops),jonosijaProvider());
    }

    public void resetoiMeneillaanOlevatLaskennat() {
        try {
            WriteResult update = datastore.getCollection(Laskenta.class).update(
                    dbobj("tila", LaskentaTila.MENEILLAAN.toString()),
                    dbobj("$set", dbobj("tila", LaskentaTila.PERUUTETTU.toString())), false, true);
            LOG.info("", update);
        } catch(Throwable t){
            LOG.error("Meneillaan olevien laskentojen resetointi epaonnistui!", t);
        }
    }

    private BiFunction<Date,LaskentaTila,Integer> jonosijaProvider() {
        return (luotu,tila) -> {
            if(LaskentaTila.ALOITTAMATTA.equals(tila)) {
                long count = datastore.getCollection(Laskenta.class).getCount(
                        dbobj("$and", dbobjs(dbobj("tila", "ALOITTAMATTA"), dbobj("luotu", dbobj("$lte", luotu)))));
                return new Long(count).intValue();
            } else {
                return null;
            }
        };
    }

    @Override
    public YhteenvetoDto merkkaaTila(String uuid, LaskentaTila tila, HakukohdeTila hakukohdeTila, Optional<IlmoitusDto> ilmoitusDtoOptional) {
        Laskenta l = datastore.get(Laskenta.class, new ObjectId(uuid));
        Query<Laskenta> query = datastore.createQuery(Laskenta.class)
                .field("_id").equal(new ObjectId(uuid))
                .field("tila").equal(LaskentaTila.MENEILLAAN);
        final UpdateOperations<Laskenta> ops = datastore.createUpdateOperations(Laskenta.class);
        ops.set("tila", tila);
        ops.set("valmiit", l.getTekematta());
        ops.set("tekematta", Collections.emptyList());
        ops.set("hakukohteitaTekematta", 0);
        int ohitettuCount = HakukohdeTila.VALMIS.equals(hakukohdeTila) ? 0 : l.getTekematta().size();
        ops.set("hakukohteitaOhitettu", ohitettuCount);
        ilmoitusDtoOptional.ifPresent(ilmoitus -> ops.set("ilmoitus", asIlmoitus(ilmoitus)));
        return laskentaAsYhteenvetoDto(datastore.findAndModify(query, ops),jonosijaProvider());
    }

    @Override
    public YhteenvetoDto lisaaIlmoitus(String uuid, String hakukohdeOid, IlmoitusDto ilmoitus) {
        Ilmoitus i = asIlmoitus(ilmoitus);
        Query<Laskenta> query = datastore
                .createQuery(Laskenta.class)
                .field("_id").equal(new ObjectId(uuid));
        UpdateOperations<Laskenta> ops = datastore
                .createUpdateOperations(Laskenta.class)
                .add("ilmoitukset." + escapeHakukohdeOid(hakukohdeOid), i, true);
        return laskentaAsYhteenvetoDto(datastore.findAndModify(query, ops),jonosijaProvider());
    }

    @Override
    public YhteenvetoDto merkkaaTila(String uuid, String hakukohdeOid, HakukohdeTila tila, IlmoitusDto ilmoitus) {
        Ilmoitus i = asIlmoitus(ilmoitus);
        if (HakukohdeTila.VALMIS.equals(tila)) {
            Query<Laskenta> query = muodostaLaskennanPaivitysQueryHakukohteelle(uuid, hakukohdeOid);
            muodostaLaskennanPaivitysQueryHakukohteelle(uuid, hakukohdeOid);

            UpdateOperations<Laskenta> ops = datastore
                    .createUpdateOperations(Laskenta.class)
                    .dec("hakukohteitaTekematta").add("valmiit", hakukohdeOid)
                    .removeAll("tekematta", hakukohdeOid)
                    .add("ilmoitukset." + escapeHakukohdeOid(hakukohdeOid), i, true);
            return laskentaAsYhteenvetoDto(datastore.findAndModify(query, ops),jonosijaProvider());
        } else if (HakukohdeTila.KESKEYTETTY.equals(tila)) {
            Query<Laskenta> query = muodostaLaskennanPaivitysQueryHakukohteelle(uuid, hakukohdeOid);
            UpdateOperations<Laskenta> ops = datastore
                    .createUpdateOperations(Laskenta.class)
                    .inc("hakukohteitaOhitettu").dec("hakukohteitaTekematta")
                    .add("ohitettu", hakukohdeOid)
                    .removeAll("tekematta", hakukohdeOid)
                    .add("ilmoitukset." + escapeHakukohdeOid(hakukohdeOid), i, true);
            return laskentaAsYhteenvetoDto(datastore.findAndModify(query, ops),jonosijaProvider());
        } else {
            throw new RuntimeException("Tekematta tilaa ei saa asettaa manuaalisesti");
        }
    }

    private Ilmoitus asIlmoitus(IlmoitusDto ilmoitus) {
        return new Ilmoitus(ilmoitus.getTyyppi(), ilmoitus.getOtsikko(), ilmoitus.getData());
    }

    private Query<Laskenta> muodostaLaskennanPaivitysQueryHakukohteelle(String uuid, String hakukohdeOid) {
        return datastore.createQuery(Laskenta.class).field("_id")
                .equal(new ObjectId(uuid)).field("tekematta")
                .hasAllOf(Arrays.asList(hakukohdeOid));
    }

    @Override
    public YhteenvetoDto merkkaaTila(String uuid, String hakukohdeOid, HakukohdeTila tila) {
        if (HakukohdeTila.VALMIS.equals(tila)) {
            Query<Laskenta> query = muodostaLaskennanPaivitysQueryHakukohteelle(uuid, hakukohdeOid);
            UpdateOperations<Laskenta> ops = datastore
                    .createUpdateOperations(Laskenta.class)
                    .dec("hakukohteitaTekematta").add("valmiit", hakukohdeOid)
                    .removeAll("tekematta", hakukohdeOid);
            return laskentaAsYhteenvetoDto(datastore.findAndModify(query, ops),jonosijaProvider());
        } else if (HakukohdeTila.KESKEYTETTY.equals(tila)) {
            Query<Laskenta> query = muodostaLaskennanPaivitysQueryHakukohteelle(uuid, hakukohdeOid);
            UpdateOperations<Laskenta> ops = datastore
                    .createUpdateOperations(Laskenta.class)
                    .inc("hakukohteitaOhitettu").dec("hakukohteitaTekematta")
                    .add("ohitettu", hakukohdeOid)
                    .removeAll("tekematta", hakukohdeOid);
            return laskentaAsYhteenvetoDto(datastore.findAndModify(query, ops),jonosijaProvider());
        } else {
            throw new RuntimeException("Tekematta tilaa ei saa asettaa manuaalisesti");
        }
    }

    public TunnisteDto luoLaskenta(
            String userOID,
            String haunnimi,
            String nimi,
            String hakuOid,
            LaskentaTyyppi tyyppi,
            Boolean erillishaku,
            Integer valinnanvaihe,
            Boolean valintakoelaskenta,
            Collection<HakukohdeDto> hakukohdeOids
    ) {
        if (hakukohdeOids == null || hakukohdeOids.isEmpty()) {
            throw new RuntimeException("Seurantaa ei muodosteta tyhjalle hakukohdejoukolle. Onko haulla hakukohteita tai rajaako hakukohdemaski kaikki hakukohteet pois? HakuOid = " + hakuOid);
        }
        Laskenta l = new Laskenta(userOID, haunnimi, nimi, hakuOid, tyyppi, erillishaku, valinnanvaihe, valintakoelaskenta, hakukohdeOids);
        Optional<Laskenta> onGoing = orGetOnGoing(l);
        if(onGoing.isPresent()) {
            return new TunnisteDto(onGoing.get().getUuid().toString(), false);
        }
        datastore.save(l);
        return new TunnisteDto(l.getUuid().toString(), true);
    }
    private Optional<Laskenta> orGetOnGoing(Laskenta l) {
        final String identityHash = l.getIdentityHash();
        Query<Laskenta> query = datastore
                .createQuery(Laskenta.class)
                .field("identityHash").equal(identityHash)
                .field("tila").in(Arrays.asList(LaskentaTila.ALOITTAMATTA.toString(), LaskentaTila.MENEILLAAN.toString()));
        UpdateOperations<Laskenta> updateNothing = datastore
                .createUpdateOperations(Laskenta.class)
                .set("identityHash", identityHash);
        return Optional.ofNullable(datastore.findAndModify(query, updateNothing, true, false));
    }

    public void lisaaIlmoitus(String uuid, String hakukohdeOid, Ilmoitus ilmoitus) {
        Query<Laskenta> query = datastore
                .createQuery(Laskenta.class)
                .field("_id").equal(new ObjectId(uuid));
        UpdateOperations<Laskenta> ops = datastore
                .createUpdateOperations(Laskenta.class)
                .add("ilmoitukset." + escapeHakukohdeOid(hakukohdeOid), ilmoitus);
        datastore.update(query, ops);
    }

    @Override
    public String otaSeuraavaLaskentaTyonAlle() {
        Query<Laskenta> query = datastore
                .createQuery(Laskenta.class)
                .field("tila").equal(LaskentaTila.ALOITTAMATTA)
                .order("luotu");
        UpdateOperations<Laskenta> ops = datastore
                .createUpdateOperations(Laskenta.class)
                .set("tila", LaskentaTila.MENEILLAAN);
        Laskenta laskenta = datastore.findAndModify(query, ops, false);
        return laskenta != null ? laskenta.getUuid().toString() : null;
    }

    private static BasicDBObject createYhteenvetoProjection() {
        BasicDBObject projection = new BasicDBObject();
        projection.append("_id", 1);
        projection.append("hakuOid", 1);
        projection.append("userOID", 1);
        projection.append("luotu", 1);
        projection.append("tila", 1);
        projection.append("haunnimi", 1);
        projection.append("nimi", 1);
        projection.append("hakukohteitaYhteensa", 1);
        projection.append("hakukohteitaTekematta", 1);
        projection.append("hakukohteitaOhitettu", 1);
        projection.append("tyyppi", 1);
        projection.append("valinnanvaihe", 1);
        projection.append("valintakoelaskenta", 1);
        return projection;
    }
}
