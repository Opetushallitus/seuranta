package fi.vm.sade.valinta.seuranta.sijoittelu.domain;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import org.mongodb.morphia.annotations.Id;

public class Sijoittelu {
    @Id
    private final ObjectId uuid;
    private final String hakuOid;
    private boolean jatkuvaSijoitteluPaalla;
    private Date viimeksiAjettu;
    private final List<Ilmoitus> ilmoitukset;
    private Date aloitusajankohta;
    private Integer ajotiheys;

    public Sijoittelu() {
        this.uuid = null;
        this.hakuOid = null;
        this.jatkuvaSijoitteluPaalla = false;
        this.viimeksiAjettu = null;
        this.ilmoitukset = null;
        this.aloitusajankohta = null;
        this.ajotiheys = null;
    }

    public Sijoittelu(String hakuOid, boolean jatkuvaSijoitteluPaalla, Date viimeksiAjettu, Date aloitusajankohta, Integer ajotiheys) {
        this.uuid = null;
        this.hakuOid = hakuOid;
        this.jatkuvaSijoitteluPaalla = jatkuvaSijoitteluPaalla;
        this.viimeksiAjettu = viimeksiAjettu;
        this.ilmoitukset = Collections.emptyList();
        this.aloitusajankohta = aloitusajankohta;
        this.ajotiheys = ajotiheys;
    }

    public String getHakuOid() {
        return hakuOid;
    }

    public List<Ilmoitus> getIlmoitukset() {
        return ilmoitukset;
    }

    public ObjectId getUuid() {
        return uuid;
    }

    public Date getViimeksiAjettu() {
        return viimeksiAjettu;
    }

    public boolean isJatkuvaSijoitteluPaalla() {
        return jatkuvaSijoitteluPaalla;
    }

    public void setJatkuvaSijoitteluPaalla(boolean jatkuvaSijoitteluPaalla) {
        this.jatkuvaSijoitteluPaalla = jatkuvaSijoitteluPaalla;
    }

    public void setViimeksiAjettu(Date viimeksiAjettu) {
        this.viimeksiAjettu = viimeksiAjettu;
    }

    public Date getAloitusajankohta() {
        return aloitusajankohta;
    }

    public void setAloitusajankohta(Date aloitusajankohta) {
        this.aloitusajankohta = aloitusajankohta;
    }

    public Integer getAjotiheys() {
        return ajotiheys;
    }

    public void setAjotiheys(Integer ajotiheys) {
        this.ajotiheys = ajotiheys;
    }
}
