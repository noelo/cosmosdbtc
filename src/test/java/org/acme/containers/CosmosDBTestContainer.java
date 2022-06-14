package org.acme.containers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import org.testcontainers.containers.CosmosDBEmulatorContainer;
import org.testcontainers.utility.DockerImageName;
import io.quarkus.logging.Log;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class CosmosDBTestContainer implements QuarkusTestResourceLifecycleManager {

    public static CosmosDBEmulatorContainer CosmosDBEmulator = new CosmosDBEmulatorContainer(
            DockerImageName.parse("mcr.microsoft.com/cosmosdb/linux/azure-cosmos-emulator:latest"));

    @Override
    public Map<String, String> start() {
        Log.info("Starting  container.....");
        CosmosDBEmulator.start();

        Log.debug("Container Started.....CID=>"+CosmosDBEmulator.getContainerId());
        try {
            File ksFile = new File("/tmp", "azure-cosmos-emulator.keystore");
            Files.deleteIfExists(ksFile.toPath());
            Files.createFile(ksFile.toPath());
            KeyStore keyStore = CosmosDBEmulator.buildNewKeyStore();

            keyStore.store(new FileOutputStream(ksFile),
                    CosmosDBEmulator.getEmulatorKey().toCharArray());

            System.setProperty("javax.net.ssl.trustStore", ksFile.toString());
            System.setProperty("javax.net.ssl.trustStorePassword", CosmosDBEmulator.getEmulatorKey());
            System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");
            System.setProperty("COSMOEPR", CosmosDBEmulator.getEmulatorEndpoint());
            System.setProperty("COSMOKEY", CosmosDBEmulator.getEmulatorKey());
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            Log.error("Exception during keystore setup",e);
            e.printStackTrace();
        }    

        Map<String, String> resMap = new HashMap<String, String>();
        resMap.put("EPR", CosmosDBEmulator.getEmulatorEndpoint());
        resMap.put("KEY", CosmosDBEmulator.getEmulatorKey());
        Log.info("Container Setup Done.....");
        return resMap;
    }

    @Override
    public void stop() {
        Log.info("Container Shutdown.....");
        CosmosDBEmulator.stop();
    }
}