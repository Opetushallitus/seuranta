package fi.vm.sade.valinta.seuranta.laskenta.service.impl;

import org.junit.Test;
import org.mockito.Mockito;

import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;

import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseContext;
import javax.ws.rs.sse.SseEventOutput;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.*;

public class SeurantaSSEServiceTest {

	@Test
	public void testaaSSE() {
		SseContext context = new SseContext() {
			@Override
			public SseEventOutput newOutput() {
				return null;
			}

			@Override
			public OutboundSseEvent.Builder newEvent() {
				return Mockito.mock(OutboundSseEvent.Builder.class);
			}

			@Override
			public SseBroadcaster newBroadcaster() {
				final Set<SseEventOutput> outputs = new HashSet<>();
				return new SseBroadcaster() {
					@Override
					public boolean register(Listener listener) {
						return false;
					}

					@Override
					public boolean register(SseEventOutput sseEventOutput) {
						outputs.add(sseEventOutput);
						return true;
					}

					@Override
					public void broadcast(OutboundSseEvent outboundSseEvent) {
						for (SseEventOutput out: outputs) {
							try {
								out.write(outboundSseEvent);
							}catch (IOException e) {
								throw  new RuntimeException(e);
							}
						}

					}

					@Override
					public void close() {

					}
				};
			}
		};
		SeurantaSSEServiceImpl sseService = new SeurantaSSEServiceImpl();
		String uuid = "uuid";
		sseService.paivita(context, new YhteenvetoDto(uuid, EMPTY, EMPTY,EMPTY, EMPTY, 0L, null,
				0, 0, 0, null, null, null, null));
		SseEventOutput eo1 = Mockito.mock(SseEventOutput.class);
		Mockito.when(eo1.isClosed()).thenReturn(true);
		SseEventOutput eo2 = Mockito.mock(SseEventOutput.class);
		Mockito.when(eo2.isClosed()).thenReturn(true);
		sseService.rekisteroi(context, uuid, eo1);
		sseService.rekisteroi(context, uuid, eo2);
		sseService.paivita(context, new YhteenvetoDto(uuid, EMPTY, EMPTY,EMPTY, EMPTY, 0L, null,
				0, 0, 0, null, null, null, null));
		sseService.sammuta(uuid);
	}
}
