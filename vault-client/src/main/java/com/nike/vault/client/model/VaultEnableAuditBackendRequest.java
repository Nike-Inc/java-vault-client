package com.nike.vault.client.model;

import java.util.Map;

/**
 * Represents the request object for enabling an audit backend.
 */
public class VaultEnableAuditBackendRequest {

    private String type;

    private String description;

    private Map<String, String> options;

    public String getType() {
        return type;
    }

    public VaultEnableAuditBackendRequest setType(String type) {
        this.type = type;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public VaultEnableAuditBackendRequest setDescription(String description) {
        this.description = description;
        return this;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public VaultEnableAuditBackendRequest setOptions(Map<String, String> options) {
        this.options = options;
        return this;
    }
}
