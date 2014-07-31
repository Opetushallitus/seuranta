package fi.vm.sade.valinta.seuranta.domain;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
public class Ilmoitus {
	
	private final String hakukohdeOid;
	private final String otsikko;
	private final Date paivamaara;
	private final List<String> data;
	
	public Ilmoitus() {
		this.otsikko = null;
		this.paivamaara = null;
		this.data = null;
		this.hakukohdeOid = null;
	}
	
	public Ilmoitus(String hakukohdeOid, String otsikko, List<String> data) {
		this.otsikko = otsikko;
		this.data = data;
		this.paivamaara = new Date();
		this.hakukohdeOid = hakukohdeOid;
	}
	
	public String getHakukohdeOid() {
		return hakukohdeOid;
	}
	
	public List<String> getData() {
		return data;
	}
	public String getOtsikko() {
		return otsikko;
	}
	public Date getPaivamaara() {
		return paivamaara;
	}
}
