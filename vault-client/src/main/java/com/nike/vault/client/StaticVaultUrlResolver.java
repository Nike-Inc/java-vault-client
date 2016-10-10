package com.nike.vault.client;

import org.apache.commons.lang3.StringUtils;

/**
 * Wrapper for the URL resolver interface for a static URL.
 */
public class StaticVaultUrlResolver implements UrlResolver {

    private final String vaultUrl;

    public StaticVaultUrlResolver(final String vaultUrl) {
        if (StringUtils.isBlank(vaultUrl)) {
            throw new IllegalArgumentException("Vault URL can not be blank.");
        }

        this.vaultUrl = vaultUrl;
    }

    @Override
    public String resolve() {
        return vaultUrl;
    }
}
