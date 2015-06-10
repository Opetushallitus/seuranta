package fi.vm.sade.valinta.seuranta.dto;

public class YhteenvetoDto {
    private final String uuid;
    private final String hakuOid;
    private final long luotu;
    private final LaskentaTila tila;
    private final int hakukohteitaYhteensa;
    private final int hakukohteitaValmiina;
    private final int hakukohteitaKeskeytetty;

    public YhteenvetoDto(String uuid, String hakuOid, Long luotu,
                         LaskentaTila tila, int hakukohteitaYhteensa,
                         int hakukohteitaValmiina, int hakukohteitaKeskeytetty) {
        this.uuid = uuid;
        this.hakuOid = hakuOid;
        this.luotu = luotu;
        this.tila = tila;
        this.hakukohteitaYhteensa = hakukohteitaYhteensa;
        this.hakukohteitaValmiina = hakukohteitaValmiina;
        this.hakukohteitaKeskeytetty = hakukohteitaKeskeytetty;
    }

    public String getHakuOid() {
        return hakuOid;
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

    public long getLuotu() {
        return luotu;
    }

    public String getUuid() {
        return uuid;
    }

    public LaskentaTila getTila() {
        return tila;
    }
}
