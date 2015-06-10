package fi.vm.sade.valinta.seuranta.resource.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import fi.vm.sade.valinta.seuranta.kela.dao.KelaDao;
import fi.vm.sade.valinta.seuranta.resource.KelaSeurantaResource;

public class KelaSeurantaResourceImpl implements KelaSeurantaResource {
    @Autowired
    private KelaDao kelaDao;

    @PreAuthorize("isAuthenticated()")
    @Override
    public Collection<String> tarkistaOnkoHakijatJoVietyKelanFtpPalvelimelle(String hakuOid, Collection<String> hakijaOids) {
        return null;
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public Collection<String> merkkaaHakijatViedyksiKelanFtpPalvelimelle(String hakuOid, Collection<String> hakijaOids) {
        return null;
    }

}
