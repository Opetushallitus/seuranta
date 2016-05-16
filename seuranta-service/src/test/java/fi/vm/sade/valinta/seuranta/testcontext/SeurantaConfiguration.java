package fi.vm.sade.valinta.seuranta.testcontext;

import org.mongodb.morphia.AdvancedDatastore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.mongodb.morphia.Datastore;

import fi.vm.sade.valinta.seuranta.laskenta.dao.impl.SeurantaDaoImpl;
import fi.vm.sade.valinta.seuranta.sijoittelu.dao.impl.SijoittelunSeurantaDaoImpl;

@Configuration
@Import(MongoConfiguration.class)
public class SeurantaConfiguration {

	@Bean
    public SeurantaDaoImpl getSeurantaDaoImpl(AdvancedDatastore datastore) {
        return new SeurantaDaoImpl(datastore);
    }
	
	@Bean
	public SijoittelunSeurantaDaoImpl getSijoittelunSeurantaDaoImpl(Datastore datastore) {
		return new SijoittelunSeurantaDaoImpl(datastore);
	}

}
