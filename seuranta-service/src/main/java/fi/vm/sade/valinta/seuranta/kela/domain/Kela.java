package fi.vm.sade.valinta.seuranta.kela.domain;

import java.util.Collection;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

public class Kela {

	@Id
	private final ObjectId hakuOid;
	private final Collection<String> hakijaOids;

	public Kela() {
		this.hakijaOids = null;
		this.hakuOid = null;
	}

	public Kela(Collection<String> hakijaOids) {
		this.hakuOid = null;
		this.hakijaOids = hakijaOids;
	}

	public Collection<String> getHakijaOids() {
		return hakijaOids;
	}

	public ObjectId getHakuOid() {
		return hakuOid;
	}

}
