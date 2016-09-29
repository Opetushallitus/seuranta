package fi.vm.sade.valinta.seuranta.laskenta.service;

import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;

import javax.ws.rs.sse.SseContext;
import javax.ws.rs.sse.SseEventOutput;
import java.util.Collection;

/**
 *         Server Sent Events support
 */
public interface SeurantaSSEService {

    Collection<String> aktiivisetUUIDt();

    void paivita(SseContext sseContext, YhteenvetoDto yhteenveto);

    void rekisteroi(SseContext sseContext, String uuid, SseEventOutput event);

    void sammuta(String uuid);
}
