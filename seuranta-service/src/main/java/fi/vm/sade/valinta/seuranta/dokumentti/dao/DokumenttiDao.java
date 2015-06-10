package fi.vm.sade.valinta.seuranta.dokumentti.dao;

import fi.vm.sade.valinta.seuranta.dto.DokumenttiDto;
import fi.vm.sade.valinta.seuranta.dto.VirheilmoitusDto;

import java.util.List;

public interface DokumenttiDao {
    String luoDokumentti(String kuvaus);

    DokumenttiDto haeDokumentti(String uuid);

    DokumenttiDto paivitaKuvaus(String uuid, String uusiKuvaus);
    DokumenttiDto paivitaDokumenttiId(String uuid, String dokumenttiId);
    DokumenttiDto lisaaVirheita(String uuid, List<VirheilmoitusDto> virheita);

    void poistaDokumentti(String uuid);
}
