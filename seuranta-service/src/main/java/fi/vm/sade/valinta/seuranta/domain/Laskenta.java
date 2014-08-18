package fi.vm.sade.valinta.seuranta.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

import com.google.common.collect.Lists;

import fi.vm.sade.valinta.seuranta.dto.HakukohdeDto;
import fi.vm.sade.valinta.seuranta.dto.HakukohdeTila;
import fi.vm.sade.valinta.seuranta.dto.IlmoitusDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTila;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTyyppi;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
public class Laskenta {

	@Id
	private ObjectId uuid;
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
	private final Map<String, List<Ilmoitus>> ilmoitukset;

	public Laskenta() {
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
	}

	public Laskenta(String hakuOid, LaskentaTyyppi tyyppi,
			Collection<String> hakukohdeOids) {
		this.hakukohteitaYhteensa = hakukohdeOids.size();
		this.hakukohteitaTekematta = this.hakukohteitaYhteensa;
		this.hakukohteitaOhitettu = 0;
		this.uuid = null;
		this.hakuOid = hakuOid;
		this.luotu = new Date();
		this.tila = LaskentaTila.MENEILLAAN;
		this.ilmoitukset = Collections.emptyMap();
		this.valmiit = Collections.emptyList();
		this.ohitettu = Collections.emptyList();
		this.tekematta = Lists.newArrayList(hakukohdeOids);
		this.tyyppi = tyyppi;
	}

	public List<String> getOhitettu() {
		return ohitettu;
	}

	public List<String> getValmiit() {
		return valmiit;
	}

	public List<String> getTekematta() {
		return tekematta;
	}

	public LaskentaTyyppi getTyyppi() {
		return tyyppi;
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
		return ilmoitukset;
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

	public LaskentaDto asDto() {
		List<HakukohdeDto> hakukohteet = Lists
				.newArrayListWithCapacity(getHakukohteitaYhteensa());
		hakukohteet.addAll(ilmoituksetHakukohteelle(getValmiit(),
				HakukohdeTila.VALMIS, getIlmoitukset()));
		hakukohteet.addAll(ilmoituksetHakukohteelle(getTekematta(),
				HakukohdeTila.TEKEMATTA, getIlmoitukset()));
		hakukohteet.addAll(ilmoituksetHakukohteelle(getOhitettu(),
				HakukohdeTila.KESKEYTETTY, getIlmoitukset()));
		return new LaskentaDto(getUuid().toString(), getHakuOid(),
				luotu == null ? new Date().getTime() : luotu.getTime(),
				getTila(), hakukohteet);
	}

	private List<HakukohdeDto> ilmoituksetHakukohteelle(
			Collection<String> hakukohdeOids, HakukohdeTila tila,
			Map<String, List<Ilmoitus>> ilmoitukset) {
		return hakukohdeOids
				.stream()
				.map(v -> new HakukohdeDto(v, tila, ilmoituksetHakukohteelle(v,
						ilmoitukset))).collect(Collectors.toList());
	}

	private List<IlmoitusDto> ilmoituksetHakukohteelle(String hakukohdeOid,
			Map<String, List<Ilmoitus>> ilmoitukset) {
		if (ilmoitukset == null || !ilmoitukset.containsKey(hakukohdeOid)) {
			return null; // Collections.emptyList();
		}
		return ilmoitukset.get(hakukohdeOid).stream().map(i -> i.asDto())
				.collect(Collectors.toList());
	}
}
