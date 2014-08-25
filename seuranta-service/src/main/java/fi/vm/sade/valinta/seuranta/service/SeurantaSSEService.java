package fi.vm.sade.valinta.seuranta.service;

import org.glassfish.jersey.media.sse.EventOutput;

import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;

/**
 * 
 * @author Jussi Jartamo
 * 
 * 
 *         Server Sent Events support
 */
public interface SeurantaSSEService {

	void paivita(YhteenvetoDto yhteenveto);

	void rekisteroi(String uuid, EventOutput event);

	void sammuta(String uuid);
}
