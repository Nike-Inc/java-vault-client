package com.nike.vault.client.auth;

/**
 * Default credentials provider chain that will attempt to retrieve a token in the following order:
 * <ul>
 * <li>Environment Variable - <code>VAULT_TOKEN</code></li>
 * <li>Java System Property - <code>vault.token</code></li>
 * </ul>
 *
 * @see EnvironmentVaultCredentialsProvider
 * @see SystemPropertyVaultCredentialsProvider
 */
public class DefaultVaultCredentialsProviderChain extends VaultCredentialsProviderChain {

    /**
     * Default constructor that sets up the ordered chain of providers.
     */
    public DefaultVaultCredentialsProviderChain() {
        super(new EnvironmentVaultCredentialsProvider(),
                new SystemPropertyVaultCredentialsProvider());
    }
}
