package com.nike.vault.client.model;

import java.util.Map;
import java.util.Set;

/**
 * Represents an authentication response from Vault
 */
public class VaultAuthResponse {

    private String clientToken;

    private Set<String> policies;

    private Map<String, String> metadata;

    private int leaseDuration;

    private boolean renewable;

    public String getClientToken() {
        return clientToken;
    }

    public VaultAuthResponse setClientToken(String clientToken) {
        this.clientToken = clientToken;
        return this;
    }

    public Set<String> getPolicies() {
        return policies;
    }

    public VaultAuthResponse setPolicies(Set<String> policies) {
        this.policies = policies;
        return this;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public VaultAuthResponse setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
        return this;
    }

    public int getLeaseDuration() {
        return leaseDuration;
    }

    public VaultAuthResponse setLeaseDuration(int leaseDuration) {
        this.leaseDuration = leaseDuration;
        return this;
    }

    public boolean isRenewable() {
        return renewable;
    }

    public VaultAuthResponse setRenewable(boolean renewable) {
        this.renewable = renewable;
        return this;
    }
}
