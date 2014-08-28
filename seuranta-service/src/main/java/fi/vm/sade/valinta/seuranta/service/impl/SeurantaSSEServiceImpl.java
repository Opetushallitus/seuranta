package fi.vm.sade.valinta.seuranta.service.impl;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
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
	private final Cache<String, CopyOnWriteArrayList<EventOutput>> EVENT_CACHE = CacheBuilder
			.newBuilder()
			.expireAfterAccess(45, TimeUnit.MINUTES)
			.removalListener(
					new RemovalListener<String, CopyOnWriteArrayList<EventOutput>>() {
						public void onRemoval(
								RemovalNotification<String, CopyOnWriteArrayList<EventOutput>> notification) {
							for (EventOutput eo : notification.getValue()) {
								try {
									if (!eo.isClosed()) {
										LOG.error(
												"Yhteys selaimeen oli viela auki! Onkohan 45minuutin timeout riittava? {}",
												notification.getKey());
										eo.close();
									}
								} catch (Exception e) {
								}
							}
						}
					}).build();

	public void paivita(YhteenvetoDto yhteenveto) {
		if (yhteenveto == null) {
			LOG.error("Yhteenveto oli null SSE paivitysta yritettaessa!");
			throw new RuntimeException(
					"Yhteenveto oli null SSE paivitysta yritettaessa!");
		}
		CopyOnWriteArrayList<EventOutput> outputs = EVENT_CACHE
				.getIfPresent(yhteenveto.getUuid());
		if (outputs == null) {
			return;
		}
		final OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
		eventBuilder.mediaType(MediaType.APPLICATION_JSON_TYPE);
		eventBuilder.data(yhteenveto);
		final OutboundEvent event = eventBuilder.build();
		for (EventOutput output : outputs) {
			try {
				if (output == null) {
					outputs.remove(null);
					continue;
				}
				try {
					if (output.isClosed()) {
						outputs.remove(output);
					}
				} catch (Exception e) {
					LOG.error("Virhe suljetun yhteyden poistamisessa! {}",
							e.getMessage());
				}
				output.write(event);
			} catch (IOException e) {
				LOG.error("Eventin kirjoitus epaonnistui! {}", e.getMessage());
			}
		}
		if (outputs.isEmpty()) { // synkataan vaan siina kornerkeississa etta
									// tyhjeni
			synchronized (EVENT_CACHE) {
				if (outputs.isEmpty()) {
					EVENT_CACHE.invalidate(yhteenveto.getUuid());
				}
			}
		}
	}

	public void rekisteroi(String uuid, final EventOutput event) {
		CopyOnWriteArrayList<EventOutput> outputs;
		try {
			outputs = EVENT_CACHE.get(uuid,
					new Callable<CopyOnWriteArrayList<EventOutput>>() {
						@Override
						public CopyOnWriteArrayList<EventOutput> call()
								throws Exception {
							return new CopyOnWriteArrayList<EventOutput>();
						}
					});
			outputs.add(event);
			for (EventOutput output : outputs) {
				try {
					if (output.isClosed()) {
						outputs.remove(output);
					}
				} catch (Exception e) {
					LOG.error("Virhe suljetun yhteyden poistamisessa! {}",
							e.getMessage());
				}
			}
		} catch (ExecutionException e) {
			LOG.error("Eventoutputin lisays cacheen epaonnistui! {}",
					e.getMessage());
		}
	}

	public void sammuta(String uuid) {
		CopyOnWriteArrayList<EventOutput> outputs = EVENT_CACHE.asMap().remove(
				uuid);
		if (outputs != null) {
			for (EventOutput output : outputs) {
				if (!output.isClosed()) {
					try {
						output.close();
					} catch (IOException e) {
						LOG.error("Invalidointiin vanha yhteys selaimeen {}!",
								uuid);
					}
				}
			}
		}
	}

}
