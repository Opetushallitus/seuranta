package fi.vm.sade.valinta.seuranta.dokumentti.domain;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
public class Dokumentti {

	@Id
	private ObjectId uuid;
	private final Date luotu;
	private final int oidejaYhteensa;
	private final int oidejaTekematta;
	private final int oidejaOhitettu;
	private final List<String> valmiit;
	private final List<String> ohitettu;
	private final List<String> tekematta;

	public Dokumentti() {
		this.uuid = null;
		this.luotu = null;
		this.oidejaYhteensa = 0;
		this.oidejaTekematta = 0;
		this.oidejaOhitettu = 0;
		this.valmiit = null;
		this.ohitettu = null;
		this.tekematta = null;
	}

	public Dokumentti(List<String> tekematta) {
		this.valmiit = Collections.emptyList();
		this.ohitettu = Collections.emptyList();
		this.tekematta = tekematta;
		this.luotu = new Date();
		this.oidejaYhteensa = 0;
		this.oidejaOhitettu = 0;
		this.oidejaTekematta = tekematta.size();
	}

	public Date getLuotu() {
		return luotu;
	}

	public int getOidejaOhitettu() {
		return oidejaOhitettu;
	}

	public int getOidejaTekematta() {
		return oidejaTekematta;
	}

	public int getOidejaYhteensa() {
		return oidejaYhteensa;
	}

	public List<String> getOhitettu() {
		return ohitettu;
	}

	public List<String> getTekematta() {
		return tekematta;
	}

	public List<String> getValmiit() {
		return valmiit;
	}

	public ObjectId getUuid() {
		return uuid;
	}
}
