package fi.vm.sade.valinta.seuranta.dao;

import java.util.Collection;
import java.util.Date;

import fi.vm.sade.valinta.seuranta.sijoittelu.dto.SijoitteluDto;

public interface SijoittelunSeurantaDao {

	SijoitteluDto hae(String hakuOid);

	Collection<SijoitteluDto> hae();

	SijoitteluDto asetaJatkuvaSijoittelu(String hakuOid, boolean jatkuvaSijoittelu);

	SijoitteluDto paivitaViimeksiAjettuPaivamaara(String hakuOid);

	void poistaSijoittelu(String hakuOid);

    SijoitteluDto paivitaAloitusAjankohta(String hakuOid, Date aloitusajankohta, Integer ajotiheys);
}
