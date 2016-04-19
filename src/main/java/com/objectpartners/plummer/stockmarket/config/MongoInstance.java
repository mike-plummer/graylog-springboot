package com.objectpartners.plummer.stockmarket.config;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.*;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.store.IDownloadConfig;
import de.flapdoodle.embed.process.io.directories.FixedPath;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.runtime.Network;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;

@Named
@Singleton
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MongoInstance {

    private static final IDirectory artifactStorePath = new FixedPath("./data/mongodb");
    private static final IDirectory extractedStorePath = new FixedPath("./data/mongodb/extracted");

    private MongodExecutable mongo;

    @PostConstruct
    public void mongoExecutable() throws IOException {

        IDownloadConfig downloadConfig = new DownloadConfigBuilder()
                .defaultsForCommand(Command.MongoD)
                .artifactStorePath(artifactStorePath)
                .build();

        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                .defaults(Command.MongoD)
                .artifactStore(new ExtractedArtifactStoreBuilder()
                        .defaults(Command.MongoD)
                        .download(downloadConfig)
                        .extractDir(extractedStorePath)
                )
                .build();

        Storage replication = new Storage("./data/mongodb/data", null, 0);

        MongodStarter starter = MongodStarter.getInstance(runtimeConfig);

        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .cmdOptions(new MongoCmdOptionsBuilder()
                        .useNoJournal(false)
                        .build())
                .net(new Net(MongoProperties.DEFAULT_PORT, Network.localhostIsIPv6()))
                .replication(replication)
                .build();

        mongo = starter.prepare(mongodConfig);
        mongo.start();
    }

    @PreDestroy
    public void cleanup() {
        if (mongo != null) {
            mongo.stop();
        }
    }
}
