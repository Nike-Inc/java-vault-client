package com.nike.vault.client.model;

/**
 * Represents a policy from Vault
 */
public class VaultPolicy {

    private String rules;

    public String getRules() {
        return rules;
    }

    public VaultPolicy setRules(String rules) {
        this.rules = rules;
        return this;
    }
}
