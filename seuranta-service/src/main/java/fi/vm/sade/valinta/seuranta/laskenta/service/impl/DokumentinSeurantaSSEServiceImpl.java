package fi.vm.sade.valinta.seuranta.laskenta.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.gson.Gson;
import fi.vm.sade.valinta.seuranta.dto.DokumenttiDto;
import fi.vm.sade.valinta.seuranta.laskenta.service.DokumentinSeurantaSSEService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseContext;
import javax.ws.rs.sse.SseEventOutput;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class DokumentinSeurantaSSEServiceImpl implements DokumentinSeurantaSSEService {
    private static final Logger LOG = LoggerFactory.getLogger(DokumentinSeurantaSSEServiceImpl.class);

    private final Cache<String, SseBroadcaster> EVENT_CACHE = CacheBuilder
            .newBuilder().expireAfterAccess(45, TimeUnit.MINUTES)
            .removalListener(new RemovalListener<String, SseBroadcaster>() {
                public void onRemoval(RemovalNotification<String, SseBroadcaster> notification) {
                    try {
                        notification.getValue().close();
                    } catch (Exception e) {
                        LOG.error(
                                "Yhteys selaimeen oli viela auki! Onkohan 45minuutin timeout riittava? {}",
                                notification.getKey());
                    }
                }
            }).build();

    public void paivita(SseContext sseContext, DokumenttiDto dokumentti) {
        if (dokumentti == null) {
            LOG.error("Dokumentti oli null SSE paivitysta yritettaessa!");
            throw new RuntimeException(
                    "Dokumentti oli null SSE paivitysta yritettaessa!");
        }
        SseBroadcaster outputs = EVENT_CACHE.getIfPresent(dokumentti.getUuid());
        if (outputs == null) {
            LOG.info("KUKAAN EI KUUNTELE {}", dokumentti.getUuid());
            return;
        }
        final OutboundSseEvent.Builder eventBuilder = sseContext.newEvent();
        eventBuilder.mediaType(MediaType.APPLICATION_JSON_TYPE);
        eventBuilder.data(new Gson().toJson(dokumentti));
        outputs.broadcast(eventBuilder.build());
        LOG.info("VIESTI LAHETETTY KIINNOSTUNEILLE {}", dokumentti.getUuid());
    }

    public void rekisteroi(SseContext sseContext, String uuid, final SseEventOutput event) {
        SseBroadcaster outputs;
        try {
            outputs = EVENT_CACHE.get(uuid, () -> sseContext.newBroadcaster());
            outputs.register(event);
        } catch (ExecutionException e) {
            LOG.error("Eventoutputin lisays cacheen epaonnistui!", e);
        }
    }

    public void sammuta(String uuid) {
        LOG.info("SAMMUTETAAN SSE BROADCASTER UUID:LLE {}", uuid);
        SseBroadcaster outputs = EVENT_CACHE.asMap().remove(uuid);
        outputs.close();
    }
}
