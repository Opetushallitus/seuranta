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
    
    public Sijoittelu() {
		this.uuid = null;
		this.hakuOid = null;
		this.jatkuvaSijoitteluPaalla = false;
		this.viimeksiAjettu = null;
		this.ilmoitukset = null;
	}
    
    public Sijoittelu(String hakuOid, boolean jatkuvaSijoitteluPaalla, Date viimeksiAjettu) {
		this.uuid = null;
		this.hakuOid = hakuOid;
		this.jatkuvaSijoitteluPaalla = jatkuvaSijoitteluPaalla;
		this.viimeksiAjettu = viimeksiAjettu;
		this.ilmoitukset = Collections.emptyList();
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
}
