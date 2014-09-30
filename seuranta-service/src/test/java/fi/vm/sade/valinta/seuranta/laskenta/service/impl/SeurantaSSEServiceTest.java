package fi.vm.sade.valinta.seuranta.laskenta.service.impl;

import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.media.sse.EventOutput;
import org.junit.Test;
import org.mockito.Mockito;

import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;
import fi.vm.sade.valinta.seuranta.laskenta.service.impl.SeurantaSSEServiceImpl;

public class SeurantaSSEServiceTest {

	@Test
	public void testaaSSE() {
		SeurantaSSEServiceImpl sseService = new SeurantaSSEServiceImpl();
		String uuid = "uuid";
		sseService.paivita(new YhteenvetoDto(uuid, StringUtils.EMPTY, 0L, null,
				0, 0, 0));
		EventOutput eo1 = Mockito.mock(EventOutput.class);
		Mockito.when(eo1.isClosed()).thenReturn(true);
		EventOutput eo2 = Mockito.mock(EventOutput.class);
		Mockito.when(eo2.isClosed()).thenReturn(true);
		sseService.rekisteroi(uuid, eo1);
		sseService.rekisteroi(uuid, eo2);
		sseService.paivita(new YhteenvetoDto(uuid, StringUtils.EMPTY, 0L, null,
				0, 0, 0));
		sseService.sammuta(uuid);
	}
}
