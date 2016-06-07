package fi.vm.sade.valinta.seuranta.laskenta.domain;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.google.common.collect.ComparisonChain;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import fi.vm.sade.valinta.seuranta.dto.HakukohdeDto;
import fi.vm.sade.valinta.seuranta.dto.HakukohdeTila;
import fi.vm.sade.valinta.seuranta.dto.IlmoitusDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTila;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTyyppi;

@Indexes(@Index(name = "tilaAndLuonti", value = "tila, luotu"))
public class Laskenta {
    private static final Logger LOG = LoggerFactory.getLogger(Laskenta.class);
    @Id
    private ObjectId uuid;
    private final String haunnimi;
    private final String nimi;
    private final String hakuOid;
    private final Date luotu;
    private final LaskentaTila tila;
    private final LaskentaTyyppi tyyppi;
    private final int hakukohteitaYhteensa;
    private final int hakukohteitaTekematta;
    private final int hakukohteitaOhitettu;
    private final List<String> valmiit;
    private final List<String> ohitettu;
    private final List<String> tekematta;
    private final List<HakukohdeJaOrganisaatio> hakukohdeOidJaOrganisaatioOids;
    private final Map<String, List<Ilmoitus>> ilmoitukset;
    private final Ilmoitus ilmoitus;
    private final Integer valinnanvaihe;
    private final Boolean valintakoelaskenta;
    private final Boolean erillishaku;
    private final String userOID;
    @Indexed
    private final String identityHash;

    public Laskenta() {
        this.userOID = null;
        this.haunnimi = null;
        this.nimi = null;
        this.hakukohteitaYhteensa = 0;
        this.hakukohteitaTekematta = 0;
        this.hakukohteitaOhitettu = 0;
        this.uuid = null;
        this.hakuOid = null;
        this.tila = null;
        this.ilmoitukset = null;
        this.luotu = null;
        this.valmiit = null;
        this.ohitettu = null;
        this.tekematta = null;
        this.tyyppi = null;
        this.valinnanvaihe = null;
        this.ilmoitus = null;
        this.valintakoelaskenta = null;
        this.hakukohdeOidJaOrganisaatioOids = null;
        this.erillishaku = null;
        this.identityHash = null;
    }

    public Laskenta(String userOID, String haunnimi, String nimi, String hakuOid, LaskentaTyyppi tyyppi,
                    Boolean erillishaku,
                    Integer valinnanvaihe, Boolean valintakoelaskenta,
                    Collection<HakukohdeDto> hakukohdeOids) {
        this.haunnimi = haunnimi;
        this.nimi = nimi;
        this.hakukohteitaYhteensa = hakukohdeOids.size();
        this.hakukohteitaTekematta = this.hakukohteitaYhteensa;
        this.hakukohteitaOhitettu = 0;
        this.uuid = null;
        this.userOID = userOID;
        this.hakuOid = hakuOid;
        this.luotu = new Date();
        this.tila = LaskentaTila.ALOITTAMATTA;
        this.ilmoitukset = Collections.emptyMap();
        this.ilmoitus = null;
        this.valmiit = Collections.emptyList();
        this.ohitettu = Collections.emptyList();
        this.hakukohdeOidJaOrganisaatioOids = hakukohdeOids.stream()
                .map(hk -> new HakukohdeJaOrganisaatio(hk.getHakukohdeOid(), hk.getOrganisaatioOid()))
                .collect(Collectors.toList());
        this.tekematta = hakukohdeOids.stream()
                .map(HakukohdeDto::getHakukohdeOid)
                .collect(Collectors.toList());
        this.tyyppi = tyyppi;
        this.erillishaku = erillishaku;
        this.valinnanvaihe = valinnanvaihe;
        this.valintakoelaskenta = valintakoelaskenta;
        this.identityHash = createIdentityHash().toString();
    }

    public String getIdentityHash() {
        return identityHash;
    }

    public String getHaunnimi() {
        return haunnimi;
    }

    public String getNimi() {
        return nimi;
    }

    private HashCode createIdentityHash() {
        /*
        private final String hakuOid;
        private final LaskentaTyyppi tyyppi;
        private final int hakukohteitaYhteensa;
        private final List<HakukohdeJaOrganisaatio> hakukohdeOidJaOrganisaatioOids;
        private final Integer valinnanvaihe;
        private final Boolean valintakoelaskenta;
        private final Boolean erillishaku;
        */
        final long DELIMETER = 1000000000L;
        return Hashing.md5().newHasher()
                .putString(hakuOid)
                .putLong(DELIMETER + 1L)
                .putInt(tyyppi != null ? tyyppi.ordinal() : -1 )
                .putLong(DELIMETER + 2L)
                .putInt(hakukohteitaYhteensa)
                .putLong(DELIMETER + 3L)
                .putInt(valinnanvaihe != null ? valinnanvaihe : -1)
                .putLong(DELIMETER + 4L)
                .putBoolean(Boolean.TRUE.equals(valintakoelaskenta))
                .putLong(DELIMETER + 5L)
                .putBoolean(Boolean.TRUE.equals(erillishaku))
                .putLong(DELIMETER + 6L)
                .putObject(hakukohdeOidJaOrganisaatioOids, (oids, sink) -> {
                    Optional.ofNullable(hakukohdeOidJaOrganisaatioOids).orElse(Collections.emptyList()).stream().sorted((h1,h2) ->
                        ComparisonChain.start().compare(h1.getHakukohdeOid(),h2.getHakukohdeOid())
                                .compare(h1.getOrganisaatioOid(),h2.getOrganisaatioOid()).result()
                    ).forEach(h -> {
                        sink.putString(h.getHakukohdeOid())
                                .putLong(DELIMETER + 7L)
                                .putString(h.getOrganisaatioOid())
                                .putLong(DELIMETER + 8L);
                    });
                })
                .hash();
    }

    public List<HakukohdeJaOrganisaatio> getHakukohdeOidJaOrganisaatioOids() {
        return hakukohdeOidJaOrganisaatioOids;
    }

    public List<String> getOhitettu() {
        if (ohitettu == null) {
            return Collections.emptyList();
        }
        return ohitettu;
    }

    public List<String> getValmiit() {
        if (valmiit == null) {
            return Collections.emptyList();
        }
        return valmiit;
    }

    public List<String> getTekematta() {
        if (tekematta == null) {
            return Collections.emptyList();
        }
        return tekematta;
    }

    public LaskentaTyyppi getTyyppi() {
        return tyyppi;
    }

    public Boolean getErillishaku() {
        return erillishaku;
    }

    public int getHakukohteitaOhitettu() {
        return hakukohteitaOhitettu;
    }

    public int getHakukohteitaTekematta() {
        return hakukohteitaTekematta;
    }

    public int getHakukohteitaYhteensa() {
        return hakukohteitaYhteensa;
    }

    public Map<String, List<Ilmoitus>> getIlmoitukset() {
        if (ilmoitukset == null) {
            return Collections.emptyMap();
        }
        return ilmoitukset;
    }

    public Ilmoitus getIlmoitus() {
        return ilmoitus;
    }

    public String getHakuOid() {
        return hakuOid;
    }

    public Date getLuotu() {
        return luotu;
    }

    public LaskentaTila getTila() {
        return tila;
    }

    public ObjectId getUuid() {
        return uuid;
    }

    public Integer getValinnanvaihe() {
        return valinnanvaihe;
    }

    public Boolean getValintakoelaskenta() {
        return valintakoelaskenta;
    }

    public LaskentaDto asDto(BiFunction<Date,LaskentaTila,Integer> jonosijaProvider, boolean luotiinkoUusiLaskenta) {
        try {
            List<HakukohdeDto> hakukohteet = Lists.newArrayListWithCapacity(getHakukohteitaYhteensa());
            hakukohteet.addAll(ilmoituksetHakukohteelle(getValmiit(), HakukohdeTila.VALMIS, getIlmoitukset()));
            hakukohteet.addAll(ilmoituksetHakukohteelle(getTekematta(), HakukohdeTila.TEKEMATTA, getIlmoitukset()));
            hakukohteet.addAll(ilmoituksetHakukohteelle(getOhitettu(), HakukohdeTila.KESKEYTETTY, getIlmoitukset()));
            return new LaskentaDto(getUuid().toString(), userOID, haunnimi, nimi, getHakuOid(),
                    luotu == null ? new Date().getTime() : luotu.getTime(),
                    getTila(), getTyyppi(), Optional.ofNullable(ilmoitus).map(Ilmoitus::asDto).orElse(null), hakukohteet, erillishaku, valinnanvaihe,
                    valintakoelaskenta, jonosijaProvider.apply(luotu,getTila()), luotiinkoUusiLaskenta);
        } catch (Exception e) {
            LOG.error("LaskentaDto:n muodostus Laskentaentiteetista epaonnistui!", e);
            throw e;
        }
    }

    public String getUserOID() {
        return userOID;
    }

    private List<HakukohdeDto> ilmoituksetHakukohteelle(Collection<String> hakukohdeOids, HakukohdeTila tila,
                                                        Map<String, List<Ilmoitus>> ilmoitukset) {
        if (hakukohdeOids == null) {
            return null;
        }
        Map<String, String> hkJaOrg = hakukohdeOidJaOrganisaatioOids.stream()
                .collect(Collectors.toMap(h -> h.getHakukohdeOid(), h -> h.getOrganisaatioOid()));
        return hakukohdeOids
                .stream()
                .map(v -> new HakukohdeDto(v, hkJaOrg.get(v), tila, ilmoituksetHakukohteelle(v, ilmoitukset)))
                .collect(Collectors.toList());
    }

    private List<IlmoitusDto> ilmoituksetHakukohteelle(String hakukohdeOid, Map<String, List<Ilmoitus>> ilmoitukset) {
        final String escapedHakukohdeOid = escapeHakukohdeOid(hakukohdeOid);
        if (ilmoitukset == null || !ilmoitukset.containsKey(escapedHakukohdeOid)) {
            return null; // Collections.emptyList();
        }
        return ilmoitukset.get(escapedHakukohdeOid).stream().map(i -> i.asDto()).collect(Collectors.toList());
    }

    public static String escapeHakukohdeOid(String hakukohdeOid) {
        return hakukohdeOid.replace(".", "\uff0E");
    }
}
