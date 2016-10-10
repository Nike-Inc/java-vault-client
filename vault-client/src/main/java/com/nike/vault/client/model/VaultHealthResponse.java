package com.nike.vault.client.model;

/**
 * Represents a health response from Vault
 */
public class VaultHealthResponse {

    private boolean initialized;

    private boolean sealed;

    private boolean standby;

    public boolean isInitialized() {
        return initialized;
    }

    public VaultHealthResponse setInitialized(boolean initialized) {
        this.initialized = initialized;
        return this;
    }

    public boolean isSealed() {
        return sealed;
    }

    public VaultHealthResponse setSealed(boolean sealed) {
        this.sealed = sealed;
        return this;
    }

    public boolean isStandby() {
        return standby;
    }

    public VaultHealthResponse setStandby(boolean standby) {
        this.standby = standby;
        return this;
    }
}
