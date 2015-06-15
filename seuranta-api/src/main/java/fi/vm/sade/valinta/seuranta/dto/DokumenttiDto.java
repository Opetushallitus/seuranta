package fi.vm.sade.valinta.seuranta.dto;

import java.util.List;

public class DokumenttiDto {
    private final String uuid;
    private final String dokumenttiId;
    private final String kuvaus;
    private final List<VirheilmoitusDto> virheilmoitukset;

    public DokumenttiDto() {
        this.uuid = null;
        this.kuvaus = null;
        this.virheilmoitukset = null;
        this.dokumenttiId = null;
    }

    public DokumenttiDto(String uuid, String dokumenttiId, String kuvaus, List<VirheilmoitusDto> virheilmoitukset) {
        this.uuid = uuid;
        this.dokumenttiId = dokumenttiId;
        this.kuvaus = kuvaus;
        this.virheilmoitukset = virheilmoitukset;
    }

    public String getUuid() {
        return uuid;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public String getDokumenttiId() {
        return dokumenttiId;
    }

    public boolean isValmis() {
        return dokumenttiId != null;
    }

    public boolean isVirheita() {
        return virheilmoitukset != null && !virheilmoitukset.isEmpty();
    }

    public List<VirheilmoitusDto> getVirheilmoitukset() {
        return virheilmoitukset;
    }
}
