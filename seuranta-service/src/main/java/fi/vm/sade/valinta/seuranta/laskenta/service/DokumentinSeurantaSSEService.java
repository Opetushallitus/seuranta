package fi.vm.sade.valinta.seuranta.laskenta.service;

import fi.vm.sade.valinta.seuranta.dto.DokumenttiDto;
import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;
import org.glassfish.jersey.media.sse.EventOutput;

/**
 * @author Jussi Jartamo
 */
public interface DokumentinSeurantaSSEService {
    void paivita(DokumenttiDto dokumentti);

    void rekisteroi(String uuid, EventOutput event);

    void sammuta(String uuid);
}
