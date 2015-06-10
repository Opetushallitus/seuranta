package fi.vm.sade.valinta.seuranta.kela.dao;

import java.util.Collection;

public interface KelaDao {
    Collection<String> tarkistaOnkoHakijatJoVietyKelanFtpPalvelimelle(String hakuOid, Collection<String> hakijaOids);

    Collection<String> merkkaaHakijatViedyksiKelanFtpPalvelimelle(String hakuOid, Collection<String> hakijaOids);
}
