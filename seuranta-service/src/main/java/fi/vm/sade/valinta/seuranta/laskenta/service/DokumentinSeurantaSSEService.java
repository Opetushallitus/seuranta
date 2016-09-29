package fi.vm.sade.valinta.seuranta.laskenta.service;

import fi.vm.sade.valinta.seuranta.dto.DokumenttiDto;

import javax.ws.rs.sse.SseContext;
import javax.ws.rs.sse.SseEventOutput;

public interface DokumentinSeurantaSSEService {
    void paivita(SseContext sseContext, DokumenttiDto dokumentti);

    void rekisteroi(SseContext sseContext, String uuid, SseEventOutput event);

    void sammuta(String uuid);
}
