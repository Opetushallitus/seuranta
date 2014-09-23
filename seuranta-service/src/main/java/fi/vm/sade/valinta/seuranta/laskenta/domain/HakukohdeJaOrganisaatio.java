package fi.vm.sade.valinta.seuranta.laskenta.domain;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
public class HakukohdeJaOrganisaatio {
	private final String hakukohdeOid;
	private final String organisaatioOid;

	public HakukohdeJaOrganisaatio() {
		this.hakukohdeOid = null;
		this.organisaatioOid = null;
	}

	public HakukohdeJaOrganisaatio(String hakukohdeOid, String organisaatioOid) {
		this.hakukohdeOid = hakukohdeOid;
		this.organisaatioOid = organisaatioOid;
	}

	public String getHakukohdeOid() {
		return hakukohdeOid;
	}

	public String getOrganisaatioOid() {
		return organisaatioOid;
	}
}
