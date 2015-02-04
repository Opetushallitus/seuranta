package fi.vm.sade.valinta.seuranta.dokumentti.dao.impl;

import fi.vm.sade.valinta.seuranta.dokumentti.dao.DokumenttiDao;
import fi.vm.sade.valinta.seuranta.dokumentti.domain.Dokumentti;
import fi.vm.sade.valinta.seuranta.dto.DokumenttiDto;
import fi.vm.sade.valinta.seuranta.dto.HakukohdeDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTyyppi;
import fi.vm.sade.valinta.seuranta.dto.VirheilmoitusDto;
import fi.vm.sade.valinta.seuranta.laskenta.domain.Laskenta;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Jussi Jartamo
 */
@Component
public class DokumenttiDaoImpl implements DokumenttiDao {
    private final static Logger LOG = LoggerFactory
            .getLogger(DokumenttiDaoImpl.class);
    private Datastore datastore;
    @Autowired
    public DokumenttiDaoImpl(Datastore datastore) {
        this.datastore = datastore;
    }

    @Override
    public DokumenttiDto haeDokumentti(String uuid) {
        ObjectId oid = new ObjectId(uuid);
        Dokumentti m = datastore.find(Dokumentti.class).field("_id").equal(oid)
                .get();
        if (m == null) {
            LOG.error("Dokumenttia ei ole olemassa uuid:lla {}", uuid);
            throw new RuntimeException("Dokumenttia ei ole olemassa uuid:lla "
                    + uuid);
        }
        return m.asDto();
    }

    @Override
    public String luoDokumentti(String kuvaus) {

            Dokumentti d = new Dokumentti(null, kuvaus, null);
            datastore.save(d);
            return d.getUuid().toString();
    }

    @Override
    public DokumenttiDto paivitaDokumenttiId(String uuid, String dokumenttiId) {
        Query<Dokumentti> query = datastore.createQuery(Dokumentti.class)
                .field("_id").equal(new ObjectId(uuid));
        // .field("tila").notEqual(LaskentaTila.VALMIS);
        UpdateOperations<Dokumentti> ops = datastore
                .createUpdateOperations(Dokumentti.class);
        ops.set("dokumenttiId", dokumenttiId);
        return datastore.findAndModify(query, ops).asDto();
    }

    @Override
    public DokumenttiDto paivitaKuvaus(String uuid, String uusiKuvaus) {
        Query<Dokumentti> query = datastore.createQuery(Dokumentti.class)
                .field("_id").equal(new ObjectId(uuid));
        // .field("tila").notEqual(LaskentaTila.VALMIS);
        UpdateOperations<Dokumentti> ops = datastore
                .createUpdateOperations(Dokumentti.class);
        ops.set("kuvaus", uusiKuvaus);
        return datastore.findAndModify(query, ops).asDto();
    }

    @Override
    public DokumenttiDto lisaaVirheita(String uuid, List<VirheilmoitusDto> virheita) {
        Query<Dokumentti> query = datastore.createQuery(Dokumentti.class)
                .field("_id").equal(new ObjectId(uuid));
        // .field("tila").notEqual(LaskentaTila.VALMIS);
        UpdateOperations<Dokumentti> ops = datastore
                .createUpdateOperations(Dokumentti.class);
        ops.addAll("virheilmoitukset", virheita, true);
        return datastore.findAndModify(query, ops).asDto();
    }

    @Override
    public void poistaDokumentti(String uuid) {
        Query<Dokumentti> query = datastore.createQuery(Dokumentti.class)
                .field("_id").equal(new ObjectId(uuid));
        datastore.delete(query);
    }
}

