package com.nike.vault.client.auth;

/**
 * Interface defining the contract for credentials.
 */
public interface VaultCredentials {

    String getToken();
}
