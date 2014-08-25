package fi.vm.sade.valinta.seuranta.dto;

import java.util.List;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
public class LaskentaDto {

	private final String uuid;
	private final String hakuOid;
	private final long luotu;
	private final LaskentaTila tila;
	private final List<HakukohdeDto> hakukohteet;
	private final Integer valinnanvaihe;
	private final Boolean valintakoelaskenta;

	public LaskentaDto(String uuid, String hakuOid, long luotu,
			LaskentaTila tila, List<HakukohdeDto> hakukohteet,
			Integer valinnanvaihe, Boolean valintakoelaskenta) {
		this.uuid = uuid;
		this.hakuOid = hakuOid;
		this.luotu = luotu;
		this.tila = tila;
		this.hakukohteet = hakukohteet;
		this.valinnanvaihe = valinnanvaihe;
		this.valintakoelaskenta = valintakoelaskenta;
	}

	public YhteenvetoDto asYhteenveto() {
		int valmiit = 0;
		int keskeytetty = 0;
		for (HakukohdeDto h : hakukohteet) {
			if (HakukohdeTila.KESKEYTETTY.equals(h.getTila())) {
				++keskeytetty;
			} else if (HakukohdeTila.VALMIS.equals(h.getTila())) {
				++valmiit;
			}
		}
		return new YhteenvetoDto(uuid, hakuOid, luotu, tila,
				hakukohteet.size(), valmiit, keskeytetty);
	}

	public Integer getValinnanvaihe() {
		return valinnanvaihe;
	}

	public Boolean getValintakoelaskenta() {
		return valintakoelaskenta;
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

	public Long getLuotu() {
		return luotu;
	}

	public String getUuid() {
		return uuid;
	}
}
