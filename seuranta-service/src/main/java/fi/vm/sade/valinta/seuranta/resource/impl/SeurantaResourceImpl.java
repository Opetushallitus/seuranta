package fi.vm.sade.valinta.seuranta.resource.impl;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import fi.vm.sade.valinta.seuranta.dao.SeurantaDao;
import fi.vm.sade.valinta.seuranta.dto.HakukohdeTila;
import fi.vm.sade.valinta.seuranta.dto.LaskentaDto;
import fi.vm.sade.valinta.seuranta.dto.YhteenvetoDto;
import fi.vm.sade.valinta.seuranta.resource.SeurantaResource;

public class SeurantaResourceImpl implements SeurantaResource {

	@Autowired
	private SeurantaDao seurantaDao;
	
	@Override
	public Collection<YhteenvetoDto> hae(String hakuOid) {
		
		return seurantaDao.haeYhteenvedotHaulle(hakuOid);
	}
	@Override
	public LaskentaDto laskenta(String uuid) {
		return seurantaDao.haeLaskenta(uuid);
	}
	@Override
	public Response luoLaskenta(String hakuOid, List<String> hakukohdeOids) {
		return Response.ok().entity(seurantaDao.luoLaskenta(hakuOid, hakukohdeOids)).build();
	}
	@Override
	public Response merkkaaHakukohteenTila(String uuid, String hakukohdeOid,
			HakukohdeTila tila) {
		seurantaDao.merkkaaTila(uuid, hakukohdeOid, tila);
		return Response.ok().build();
	}
	public Response merkkaaLaskennanTila(String uuid, fi.vm.sade.valinta.seuranta.dto.LaskentaTila tila) {
		seurantaDao.merkkaaTila(uuid, tila);
		return Response.ok().build();
	}
	@Override
	public Response poistaLaskenta(String uuid) {
		seurantaDao.poistaLaskenta(uuid);
		return Response.ok().build();
	}
	
	
}
