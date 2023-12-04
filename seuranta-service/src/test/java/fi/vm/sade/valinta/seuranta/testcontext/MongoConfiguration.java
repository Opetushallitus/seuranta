package fi.vm.sade.valinta.seuranta.testcontext;

import java.io.IOException;
import java.util.Random;

import de.flapdoodle.embed.mongo.runtime.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.TransitionWalker.ReachedState;
import org.mongodb.morphia.mapping.DefaultCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
@Configuration
public class MongoConfiguration {
	@Bean(name = "mongodFactory", destroyMethod = "shutdown")
	public MongodTestFactory mongodFactory() {
		return new MongodTestFactory();
	}

	@Bean(name = "mongo")
	public MongoClient mongo(
			@Qualifier("mongodFactory") final MongodTestFactory mongodFactory) {
		return mongodFactory.newMongo();
	}

	@Bean(name = "morphia")
	public Morphia morphia() {
		Morphia morphia = new Morphia();
		morphia
				.getMapper()
				.getOptions()
				.setObjectFactory(
						new DefaultCreator() {
							@Override
							protected ClassLoader getClassLoaderForClass() {
								return MongoConfiguration.class.getClassLoader();
							}
						});
		return morphia;
	}

	@Bean(name = "datastore2")
	public Datastore datastore2(
			@Qualifier("morphia") final Morphia morphia, @Qualifier("mongo") final MongoClient mongo) {
		return morphia.createDatastore(mongo, "test");
	}
}
