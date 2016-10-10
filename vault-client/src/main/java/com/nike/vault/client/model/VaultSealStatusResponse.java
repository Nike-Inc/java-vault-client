package com.nike.vault.client.model;

/**
 * Represents a seal status response from Vault
 */
public class VaultSealStatusResponse {

    private boolean sealed;

    private int t;

    private int n;

    private int progress;

    public boolean isSealed() {
        return sealed;
    }

    public VaultSealStatusResponse setSealed(boolean sealed) {
        this.sealed = sealed;
        return this;
    }

    public int getT() {
        return t;
    }

    public VaultSealStatusResponse setT(int t) {
        this.t = t;
        return this;
    }

    public int getN() {
        return n;
    }

    public VaultSealStatusResponse setN(int n) {
        this.n = n;
        return this;
    }

    public int getProgress() {
        return progress;
    }

    public VaultSealStatusResponse setProgress(int progress) {
        this.progress = progress;
        return this;
    }
}
