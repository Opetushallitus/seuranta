package fi.vm.sade.valinta.seuranta.resource.impl;

import java.util.List;

import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.sse.EventOutput;

import fi.vm.sade.valinta.seuranta.resource.DokumentinSeurantaResource;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
public class DokumentinSeurantaResourceImpl implements
		DokumentinSeurantaResource {

	@Override
	public Response keskeyta(String uuid) {
		return null;
	}

	@Override
	public Response paivitaOid(String uuid, String oid, String json) {
		return null;
	}

	@Override
	public String luoDokumentti(List<String> oids) {
		return null;
	}

	@Override
	public EventOutput yhteenvetoSSE(String uuid) {
		return null;
	}
}
