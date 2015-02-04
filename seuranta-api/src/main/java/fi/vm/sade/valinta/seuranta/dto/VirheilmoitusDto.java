package fi.vm.sade.valinta.seuranta.dto;

/**
 * @author Jussi Jartamo
 *
 * Yksinkertaisempi versio ilmoituksesta
 */
public class VirheilmoitusDto {

    private final String tyyppi;
    private final String ilmoitus;

    public VirheilmoitusDto() {
        this.tyyppi = null;
        this.ilmoitus = null;
    }
    public VirheilmoitusDto(String tyyppi, String ilmoitus) {
        this.tyyppi = tyyppi;
        this.ilmoitus = ilmoitus;
    }

    public String getIlmoitus() {
        return ilmoitus;
    }

    public String getTyyppi() {
        return tyyppi;
    }
}
