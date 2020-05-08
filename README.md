# HashiCorpVaultCredentialResolver

This project implement a custom ServiceNow Credential Resolver able to resolve credential identifiers sent from the MID Server into actual credentials from the Hashicorp Vault Credential repository. 

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

This project use a community developed Java client for the Vault secrets management solution from HashiCorp. 

### Installing

You can download the binaries directly from the [releases](https://github.com/JefMuller/HashiCorpVaultCredentialResolver/releases) section.

### From Source:

* HashiCorpVaultCredentialResolver requires JDK 1.8 or newer and Vault Java Driver (https://github.com/BetterCloud/vault-java-driver) 5.1 or newer.
* Import the project in Eclipse
* Add Mid.jar to Java Build Path
* As well as vault-java-driver.jar
* Export to JAR File

## Running the tests

* Use a ServiceNow Instance with “External Credential Storage” plugin (com.snc.discovery.external_credentials) 
* Import above exported JAR file and vault-java-driver.jar to your ServiceNow instance. (MID Server - JAR Files)
* Create Credential in your instance with "External credential store" flag activated.
* Ensure that the "Credential ID" match a secret path in your Hashicorp credential store (ex: secret/mysecret)

## Built With

* [Vault Java Driver](https://github.com/BetterCloud/vault-java-driver) - A zero-dependency Java client for the Hashicorp Vault

## Authors

* **Jean-François (Jef) Muller** - *Initial work* - [JefMuller](https://www.linkedin.com/in/jef-muller/)
