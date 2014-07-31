package fi.vm.sade.valinta.seuranta.testcontext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.google.code.morphia.Datastore;

import fi.vm.sade.valinta.seuranta.dao.impl.SeurantaDaoImpl;
import fi.vm.sade.valinta.seuranta.dao.impl.SijoittelunSeurantaDaoImpl;

@Configuration
@Import(MongoConfiguration.class)
public class SeurantaConfiguration {

	@Bean
    public SeurantaDaoImpl getSeurantaDaoImpl(Datastore datastore) {
        return new SeurantaDaoImpl(datastore);
    }
	
	@Bean
	public SijoittelunSeurantaDaoImpl getSijoittelunSeurantaDaoImpl(
			Datastore datastore) {
		return new SijoittelunSeurantaDaoImpl(datastore);
	}

}
