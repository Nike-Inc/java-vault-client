# Vault Client

[ ![Download](https://api.bintray.com/packages/nike/maven/vault-client/images/download.svg) ](https://bintray.com/nike/maven/vault-client/_latestVersion)
[![][travis img]][travis]
[![Code Coverage](https://img.shields.io/codecov/c/github/Nike-Inc/java-vault-client/master.svg)](https://codecov.io/github/Nike-Inc/java-vault-client?branch=master)
[![][license img]][license]

<a name="overview"></a>
A simple java library for interacting with Vault.

<a name="quickstart"></a>
## Quickstart

``` java
    final VaultClient vaultClient = VaultClientFactory.getClient();
```

### Default URL Assumptions

The example above uses the `DefaultVaultUrlResolver` to resolve the URL for Vault.

For that to succeed, the environment variable, `VAULT_ADDR`, must be set:

    VAULT_ADDR=http://vault

or the JVM system property, `vault.addr`, must be set:

    vault.addr=http://vault

### Default Credentials Provider Assumptions

Again, for the example above, the `DefaultVaultCredentialsProviderChain` is used to resolve the token needed to interact with Vault.

For that to succeed, the environment variable, `VAULT_TOKEN`, must be set:

    VAULT_TOKEN=TOKEN

or the JVM system property, `vault.token`, must be set:

    vault.token=TOKEN

## Customizing How the URL is Resolved

For scenarios where you want to source the URL from some other subsystem, you can easily implement your own URL resolver:

``` java
    public class GuiceVaultUrlResolver implements UrlResolver {
    
        private final String vaultAddr;
    
        @Inject
        public GuiceVaultUrlResolver(@Named("vault.addr") final String vaultAddr) {
            this.vaultAddr = vaultAddr;
        }
    
        @Override
        public String resolve() {
            return vaultAddr;
        }
    }
```

Use the factory class then to create a Vault client with this custom URL resolver:

``` java
    final VaultClient vaultClient = VaultClientFactory.getClient(guiceVaultUrlResolver);
```

## Customizing How the Credentials are Provided

Much like the URL resolver, you may need to source the Vault token for a different subsystem.  Again, you can easily implement your own:

``` java
    public class GuiceVaultCredentialsProvider implements VaultCredentialsProvider {
        
        private final VaultCredentials vaultCredentials;
        
        @Inject
        public GuiceVaultCredentialsProvider(@Named("vault.token") final String vaultToken) {
            this.vaultCredentials = new TokenVaultCredentials(vaultToken);
        }
        
        @Override
        public VaultCredentials getCredentials() {
            return vaultCredentials;
        }
    }
```

Use the factory class then to create a Vault client with this custom credentials provider:

``` java
    final VaultClient vaultClient = VaultClientFactory.getClient(new DefaultVaultUrlResolver(), guiceVaultCredentialsProvder);
```

## HTTP Client Customization

Vault client uses OkHttp client to make HTTP requests against Vault.

The default client configuration used by the Vault client sets the connect, request and response timeouts to 15 seconds.  No other customizations are made.

If you need to customize the HTTP client further for any reason, such as custom SSL settings, you can do so.

``` java
    final OkHttpClient httpClient = new OkHttpClient.Builder().build();
    
    final VaultClient vaultClient = new VaultClient(new DefaultVaultUrlResolver(), new DefaultVaultCredentialsProviderChain(), httpClient);
```

## Further Details

Vault client is a small project. It only has a few classes and they are all fully documented. For further details please see the source code, including javadocs and unit tests.

<a name="license"></a>
## License

Vault client is released under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

[travis]:https://travis-ci.org/Nike-Inc/java-vault-client
[travis img]:https://api.travis-ci.org/Nike-Inc/java-vault-client.svg?branch=master

[license]:LICENSE.txt
[license img]:https://img.shields.io/badge/License-Apache%202-blue.svg

[toc]:#table_of_contents
