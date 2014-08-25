package fi.vm.sade.valinta.seuranta.service.impl;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import fi.vm.sade.valinta.seuranta.dto.LaskentaTila;
import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;
import fi.vm.sade.valinta.seuranta.service.SeurantaSSEService;

/**
 * 
 * @author Jussi Jartamo
 * 
 *         Expires events if no access in 45min
 */
@Component
public class SeurantaSSEServiceImpl implements SeurantaSSEService {

	private static final Logger LOG = LoggerFactory
			.getLogger(SeurantaSSEServiceImpl.class);
	private final Cache<String, EventOutput> EVENT_CACHE = CacheBuilder
			.newBuilder().expireAfterAccess(45, TimeUnit.MINUTES)
			.removalListener(new RemovalListener<String, EventOutput>() {
				public void onRemoval(
						RemovalNotification<String, EventOutput> notification) {
					try {
						if (!notification.getValue().isClosed()) {
							LOG.error(
									"Yhteys selaimeen oli viela auki! Onkohan 45minuutin timeout riittava? {}",
									notification.getKey());
							notification.getValue().close();
						}
					} catch (Exception e) {
					}
				}
			}).build();
	private SseBroadcaster broadcaster = new SseBroadcaster();

	public void paivita(YhteenvetoDto yhteenveto) {

		if (yhteenveto == null) {
			LOG.error("Yhteenveto oli null SSE paivitysta yritettaessa!");
			throw new RuntimeException(
					"Yhteenveto oli null SSE paivitysta yritettaessa!");
		}
		EventOutput output = EVENT_CACHE.getIfPresent(yhteenveto.getUuid());
		if (output == null) {
			return;
		}
		if (output.isClosed()) {
			LOG.error("Yhteys selaimeen on sammutettu {}", yhteenveto.getUuid());
			EVENT_CACHE.invalidate(yhteenveto.getUuid());
		}
		final OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
		eventBuilder.mediaType(MediaType.APPLICATION_JSON_TYPE);
		eventBuilder.data(yhteenveto);
		final OutboundEvent event = eventBuilder.build();
		try {
			output.write(event);
		} catch (IOException e) {
			LOG.error("Eventin kirjoitus epaonnistui! {}", e.getMessage());
		}
	}

	public void rekisteroi(String uuid, EventOutput event) {
		sammuta(uuid); // sammutetaan varmuuden vuoksi ensin
		if (!event.isClosed()) {
			EVENT_CACHE.put(uuid, event);
		} else {
			LOG.error(
					"Yhteys selaimeen sulkeutui ennen kuin ehdittiin rekisteroida {}",
					uuid);
		}
	}

	public void sammuta(String uuid) {
		EventOutput oldOutput = EVENT_CACHE.getIfPresent(uuid);
		if (oldOutput != null) {
			LOG.error("Invalidointiin vanha yhteys selaimeen {}!", uuid);
			EVENT_CACHE.invalidate(uuid);
		}
	}

}
