package com.nike.vault.client.model;

import java.util.Map;

/**
 * Represent a response for reading data from Vault
 */
public class VaultResponse {

    private Map<String, String> data;

    /**
     * Returns the key/value pairs stored at a path
     *
     * @return Map of data
     */
    public Map<String, String> getData() {
        return data;
    }

    public VaultResponse setData(Map<String, String> data) {
        this.data = data;
        return this;
    }
}
