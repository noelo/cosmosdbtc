# quarkus-azure-tc Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Purpose
Demo app to show how to use Quarkus, CosmosDB emulator and test containers

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Setting up the cacerts to trust the Emulators cert

### Run the container 
```
docker run --rm -p 127.0.0.1:8081:8081 mcr.microsoft.com/cosmosdb/linux/azure-cosmos-emulator
```

### Extract the cert
```
EMULATOR_HOST=localhost
EMULATOR_PORT=8081
EMULATOR_CERT_PATH=/tmp/cosmos_emulator.cert
openssl s_client -connect ${EMULATOR_HOST}:${EMULATOR_PORT} </dev/null | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > $EMULATOR_CERT_PATH
```
### Delete the cert if already exists
```
sudo $JAVA_HOME/bin/keytool -cacerts -delete -alias cosmos_emulator
```
### Import the cert
```
sudo $JAVA_HOME/bin/keytool -cacerts -importcert -alias cosmos_emulator -file $EMULATOR_CERT_PATH
```


