package fi.vm.sade.valinta.seuranta.dto;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
public class LaskentaDto {

	private final String uuid;
	private final String hakuOid;
	private final Date luotu;
	private final LaskentaTila tila;
	private final List<HakukohdeDto> hakukohteet;
	
	public LaskentaDto(String uuid, String hakuOid, Date luotu, LaskentaTila tila, List<HakukohdeDto> hakukohteet) {
		this.uuid = uuid;
		this.hakuOid = hakuOid;
		this.luotu = luotu;
		this.tila  = tila;
		this.hakukohteet = hakukohteet;
	}
	
	public LaskentaTila getTila() {
		return tila;
	}
	public List<HakukohdeDto> getHakukohteet() {
		return hakukohteet;
	}
	public String getHakuOid() {
		return hakuOid;
	}
	public Date getLuotu() {
		return luotu;
	}
	public String getUuid() {
		return uuid;
	}
}
