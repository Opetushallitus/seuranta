package fi.vm.sade.valinta.seuranta.laskenta.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final Logger LOG = LoggerFactory.getLogger(Laskenta.class);
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
	private final List<HakukohdeJaOrganisaatio> hakukohdeOidJaOrganisaatioOids;
	private final Map<String, List<Ilmoitus>> ilmoitukset;
	private final Integer valinnanvaihe;
	private final Boolean valintakoelaskenta;
	private final Boolean erillishaku;

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
		this.valinnanvaihe = null;
		this.valintakoelaskenta = null;
		this.hakukohdeOidJaOrganisaatioOids = null;
		this.erillishaku = null;
	}

	public Laskenta(String hakuOid, LaskentaTyyppi tyyppi,
			Boolean erillishaku,
			Integer valinnanvaihe, Boolean valintakoelaskenta,
			Collection<HakukohdeDto> hakukohdeOids) {
		this.hakukohteitaYhteensa = hakukohdeOids.size();
		this.hakukohteitaTekematta = this.hakukohteitaYhteensa;
		this.hakukohteitaOhitettu = 0;
		this.uuid = null;
		this.hakuOid = hakuOid;
		this.luotu = new Date();
		this.tila = LaskentaTila.ALOITAMATTA;
		this.ilmoitukset = Collections.emptyMap();
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
		try {
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
					getTila(), getTyyppi(), hakukohteet, erillishaku, valinnanvaihe,
					valintakoelaskenta);
		} catch (Exception e) {
			LOG.error(
					"LaskentaDto:n muodostus Laskentaentiteetista epaonnistui! {}",
					e.getMessage());
			throw e;
		}
	}

	private List<HakukohdeDto> ilmoituksetHakukohteelle(
			Collection<String> hakukohdeOids, HakukohdeTila tila,
			Map<String, List<Ilmoitus>> ilmoitukset) {
		if (hakukohdeOids == null) {
			return null;
		}
		Map<String, String> hkJaOrg = hakukohdeOidJaOrganisaatioOids.stream()
				.collect(
						Collectors.toMap(h -> h.getHakukohdeOid(),
								h -> h.getOrganisaatioOid()));
		return hakukohdeOids
				.stream()
				.map(v -> new HakukohdeDto(v, hkJaOrg.get(v), tila,
						ilmoituksetHakukohteelle(v, ilmoitukset)))
				.collect(Collectors.toList());
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
