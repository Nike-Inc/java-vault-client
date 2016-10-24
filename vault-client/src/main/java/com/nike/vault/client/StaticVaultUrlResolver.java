package com.nike.vault.client;

import org.apache.commons.lang3.StringUtils;

/**
 * Wrapper for the URL resolver interface for a static URL.
 */
public class StaticVaultUrlResolver implements UrlResolver {

    private final String vaultUrl;

    /**
     * Explicit constructor for holding a static Vault URL.
     *
     * @param vaultUrl Vault URL
     */
    public StaticVaultUrlResolver(final String vaultUrl) {
        if (StringUtils.isBlank(vaultUrl)) {
            throw new IllegalArgumentException("Vault URL can not be blank.");
        }

        this.vaultUrl = vaultUrl;
    }

    /**
     * Returns a static Vault URL.
     *
     * @return Vault URL
     */
    @Override
    public String resolve() {
        return vaultUrl;
    }
}
