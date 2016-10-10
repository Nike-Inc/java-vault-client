package com.nike.vault.client.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Represent a response for listing keys at a path.
 */
public class VaultListResponse {

    private List<String> keys = new LinkedList<String>();

    public List<String> getKeys() {
        return keys;
    }

    public VaultListResponse setKeys(List<String> keys) {
        this.keys = keys;
        return this;
    }
}
