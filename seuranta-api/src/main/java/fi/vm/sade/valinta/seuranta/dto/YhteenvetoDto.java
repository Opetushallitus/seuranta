package fi.vm.sade.valinta.seuranta.dto;

import java.util.Date;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
public class YhteenvetoDto {

	private final String uuid;
	private final String hakuOid;
	private final Date luotu;
	private final LaskentaTila tila;
	private final int hakukohteitaYhteensa;
	private final int hakukohteitaValmiina;
	private final int hakukohteitaKeskeytetty;
	
	public YhteenvetoDto(String uuid, String hakuOid, Date luotu, LaskentaTila tila, int hakukohteitaYhteensa, int hakukohteitaValmiina, int hakukohteitaKeskeytetty) {
		this.uuid = uuid;
		this.hakuOid = hakuOid;
		this.luotu = luotu;
		this.tila = tila;
		this.hakukohteitaYhteensa  = hakukohteitaYhteensa;
		this.hakukohteitaValmiina  = hakukohteitaValmiina;
		this.hakukohteitaKeskeytetty = hakukohteitaKeskeytetty;
	}
	
	public int getHakukohteitaKeskeytetty() {
		return hakukohteitaKeskeytetty;
	}
	public int getHakukohteitaValmiina() {
		return hakukohteitaValmiina;
	}
	public int getHakukohteitaYhteensa() {
		return hakukohteitaYhteensa;
	}
	public Date getLuotu() {
		return luotu;
	}
	public String getUuid() {
		return uuid;
	}
	public LaskentaTila getTila() {
		return tila;
	}
}
