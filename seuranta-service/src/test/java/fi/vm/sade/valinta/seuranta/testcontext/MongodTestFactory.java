package fi.vm.sade.valinta.seuranta.testcontext;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.commands.ServerAddress;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongodTestFactory {
    private static final Logger LOG = LoggerFactory.getLogger(MongodTestFactory.class);

    private final TransitionWalker.ReachedState<RunningMongodProcess> mongodProcess;

    public MongodTestFactory() {
        mongodProcess = Mongod.instance().start(Version.Main.V3_6);
        LOG.info("Mongo käynnistetty porttiin {}", mongodProcess.current().getServerAddress().getPort());
    }

    public void shutdown() {
        if (mongodProcess != null
                && mongodProcess.current() != null
                && mongodProcess.current().isAlive()) {
            int port = mongodProcess.current().getServerAddress().getPort();
            System.out.println("Pysäytetään mongod (" + port + ")...");
            mongodProcess.current().stop();
            System.out.println("Mongod pysäytetty (" + port + ")");
        }
    }

    public MongoClient newMongo() {
        final ServerAddress serverAddress = mongodProcess.current().getServerAddress();
        return new MongoClient(serverAddress.getHost(), serverAddress.getPort());
    }
}
