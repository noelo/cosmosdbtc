package org.acme;

import org.acme.containers.CosmosDBTestContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.models.CosmosContainerResponse;
import com.azure.cosmos.models.CosmosDatabaseResponse;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@DisplayName("CosmosDB Test")
@QuarkusTestResource(CosmosDBTestContainer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CosmosDBTest {
    private String EPR;
    private String KEY;

    @BeforeEach
    private void init() {
        EPR = System.getProperty("COSMOEPR");
        KEY = System.getProperty("COSMOKEY");
    }

    @Test
    @Order(1)
    @DisplayName("Connect to CosmosDB")
    public void testCosmosConnection() {

        CosmosAsyncClient client = new CosmosClientBuilder()
                .gatewayMode()
                .endpointDiscoveryEnabled(false)
                .endpoint(EPR)
                .key(KEY)
                .buildAsyncClient();

        CosmosDatabaseResponse databaseResponse = client.createDatabaseIfNotExists("Azure").block();
        Assertions.assertEquals(databaseResponse.getStatusCode(),201);
        CosmosContainerResponse containerResponse = client
                .getDatabase("Azure")
                .createContainerIfNotExists("ServiceContainer", "/name")
                .block();
        Assertions.assertEquals(containerResponse.getStatusCode(),201);
    }

}