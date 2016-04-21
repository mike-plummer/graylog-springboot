package com.objectpartners.plummer.stockmarket.data;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.*;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.store.IDownloadConfig;
import de.flapdoodle.embed.process.io.directories.FixedPath;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.runtime.Network;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;

@Named
@Singleton
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MongoInstance implements DisposableBean {

    private static final IDirectory ARTIFACT_STORE_PATH = new FixedPath("./data/mongodb");
    private static final IDirectory EXTRACTED_STORE_PATH = new FixedPath("./data/mongodb/extracted");

    private MongodExecutable mongo;
    private MongodProcess process;

    public MongoInstance() throws IOException, InterruptedException {

        IDownloadConfig downloadConfig = new DownloadConfigBuilder()
                .defaultsForCommand(Command.MongoD)
                .artifactStorePath(ARTIFACT_STORE_PATH)
                .build();

        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                .defaults(Command.MongoD)
                .artifactStore(new ExtractedArtifactStoreBuilder()
                        .defaults(Command.MongoD)
                        .download(downloadConfig)
                        .extractDir(EXTRACTED_STORE_PATH)
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
        process = mongo.start();
    }

    @Override
    public void destroy() throws Exception {
        if (process != null) {
            process.stop();
        }
        if (mongo != null) {
            mongo.stop();
        }
    }
}
