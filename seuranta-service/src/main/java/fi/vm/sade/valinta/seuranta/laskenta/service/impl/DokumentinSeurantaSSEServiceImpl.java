package fi.vm.sade.valinta.seuranta.laskenta.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.gson.Gson;
import fi.vm.sade.valinta.seuranta.dto.DokumenttiDto;
import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;
import fi.vm.sade.valinta.seuranta.laskenta.service.DokumentinSeurantaSSEService;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import java.util.concurrent.Callable;
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
                        notification.getValue().closeAll();
                    } catch (Exception e) {
                        LOG.error(
                                "Yhteys selaimeen oli viela auki! Onkohan 45minuutin timeout riittava? {}",
                                notification.getKey());
                    }
                }
            }).build();

    public void paivita(DokumenttiDto dokumentti) {
        if (dokumentti == null) {
            LOG.error("Dokumentti oli null SSE paivitysta yritettaessa!");
            throw new RuntimeException(
                    "Dokumentti oli null SSE paivitysta yritettaessa!");
        }
        SseBroadcaster outputs = EVENT_CACHE.getIfPresent(dokumentti.getUuid());
        if (outputs == null) {
            LOG.debug("KUKAAN EI KUUNTELE {}", dokumentti.getUuid());
            return;
        }
        final OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
        eventBuilder.mediaType(MediaType.APPLICATION_JSON_TYPE);
        eventBuilder.data(new Gson().toJson(dokumentti));
        outputs.broadcast(eventBuilder.build());
        LOG.debug("VIESTI LAHETETTY KIINNOSTUNEILLE {}", dokumentti.getUuid());
    }

    public void rekisteroi(String uuid, final EventOutput event) {
        SseBroadcaster outputs;
        try {
            outputs = EVENT_CACHE.get(uuid, new Callable<SseBroadcaster>() {
                @Override
                public SseBroadcaster call() throws Exception {
                    return new SseBroadcaster();
                }
            });
            outputs.add(event);
        } catch (ExecutionException e) {
            LOG.error("Eventoutputin lisays cacheen epaonnistui! {}",
                    e.getMessage());
        }
    }

    public void sammuta(String uuid) {
        LOG.error("SAMMUTETAAN SSE BROADCASTER UUID:LLE {}", uuid);
        SseBroadcaster outputs = EVENT_CACHE.asMap().remove(uuid);
        outputs.closeAll();
    }
}
