package com.nike.vault.client.model;

import java.util.List;

/**
 * Represents an initialization response from Vault
 */
public class VaultInitResponse {

    private List<String> keys;

    private String rootToken;

    public List<String> getKeys() {
        return keys;
    }

    public VaultInitResponse setKeys(List<String> keys) {
        this.keys = keys;
        return this;
    }

    public String getRootToken() {
        return rootToken;
    }

    public VaultInitResponse setRootToken(String rootToken) {
        this.rootToken = rootToken;
        return this;
    }
}
