package com.nike.vault.client.model;

import java.util.Map;
import java.util.Set;

/**
 * Represents an authentication client token response from Vault
 */
public class VaultClientTokenResponse {

    private String id;

    private Set<String> policies;

    private String path;

    private Map<String, String> meta;

    private String displayName;

    private int numUses;

    public String getId() {
        return id;
    }

    public VaultClientTokenResponse setId(String id) {
        this.id = id;
        return this;
    }

    public Set<String> getPolicies() {
        return policies;
    }

    public VaultClientTokenResponse setPolicies(Set<String> policies) {
        this.policies = policies;
        return this;
    }

    public String getPath() {
        return path;
    }

    public VaultClientTokenResponse setPath(String path) {
        this.path = path;
        return this;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public VaultClientTokenResponse setMeta(Map<String, String> meta) {
        this.meta = meta;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public VaultClientTokenResponse setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public int getNumUses() {
        return numUses;
    }

    public VaultClientTokenResponse setNumUses(int numUses) {
        this.numUses = numUses;
        return this;
    }
}
