package com.nike.vault.client.auth;

/**
 * Interface defining the contract for credentials providers.
 */
public interface VaultCredentialsProvider {

    VaultCredentials getCredentials();
}
