package fi.vm.sade.valinta.seuranta.sijoittelu.dto;

import java.util.Date;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
public class SijoitteluDto {

    private final String hakuOid;
    private final boolean ajossa;
    private final Date viimeksiAjettu;
    private final String virhe;
    
    public SijoitteluDto() {
		this.hakuOid = null;
		this.ajossa = false;
		this.viimeksiAjettu = null;
		this.virhe = null;
	}
    
    public SijoitteluDto(String hakuOid, boolean ajossa, Date viimeksiAjettu, String virhe) {
		this.hakuOid = hakuOid;
		this.ajossa = ajossa;
		this.viimeksiAjettu = viimeksiAjettu;
		this.virhe = virhe;
	}
    public String getVirhe() {
		return virhe;
	}
    public String getHakuOid() {
		return hakuOid;
	}
    public Date getViimeksiAjettu() {
		return viimeksiAjettu;
	}
    public boolean isAjossa() {
		return ajossa;
	}
    public boolean getAjossa() {
		return ajossa;
	}
    
}
