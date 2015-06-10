package fi.vm.sade.valinta.seuranta.sijoittelu.domain;

import java.util.Date;
import java.util.List;

public class Ilmoitus {
    private final String otsikko;
    private final Date paivamaara;
    private final List<String> data;

    public Ilmoitus() {
        this.otsikko = null;
        this.paivamaara = null;
        this.data = null;
    }

    public Ilmoitus(String hakukohdeOid, String otsikko, List<String> data) {
        this.otsikko = otsikko;
        this.data = data;
        this.paivamaara = new Date();
    }

    public List<String> getData() {
        return data;
    }

    public String getOtsikko() {
        return otsikko;
    }

    public Date getPaivamaara() {
        return paivamaara;
    }
}
