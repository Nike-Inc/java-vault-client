package com.nike.vault.client.model;

/**
 * Represents an unseal request
 */
public class VaultUnsealRequest {

    private final String key;

    private final boolean reset;

    /**
     * Either the key or reset parameter must be provided; if both are provided, reset takes precedence.
     *
     * @param key   a single master share key
     * @param reset if true, the previously-provided unseal keys are discarded from memory and the unseal
     *              process is reset
     */
    public VaultUnsealRequest(String key, boolean reset) {
        this.key = key;
        this.reset = reset;
    }

    public String getKey() {
        return key;
    }

    public boolean isReset() {
        return reset;
    }
}
