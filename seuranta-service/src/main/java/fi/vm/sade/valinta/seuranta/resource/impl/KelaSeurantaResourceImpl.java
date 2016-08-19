package fi.vm.sade.valinta.seuranta.resource.impl;

import fi.vm.sade.valinta.seuranta.kela.dao.KelaDao;
import fi.vm.sade.valinta.seuranta.resource.KelaSeurantaResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

public class KelaSeurantaResourceImpl implements KelaSeurantaResource {
    @Autowired
    private KelaDao kelaDao;

    @Override
    public Collection<String> tarkistaOnkoHakijatJoVietyKelanFtpPalvelimelle(String hakuOid, Collection<String> hakijaOids) {
        return null;
    }

    @Override
    public Collection<String> merkkaaHakijatViedyksiKelanFtpPalvelimelle(String hakuOid, Collection<String> hakijaOids) {
        return null;
    }

}
