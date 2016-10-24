package com.nike.vault.client;

/**
 * Interface for resolving the URL for Vault.
 */
public interface UrlResolver {

    /**
     * Resolves the URL for the Vault instance.
     *
     * @return Vault URL
     */
    String resolve();
}
