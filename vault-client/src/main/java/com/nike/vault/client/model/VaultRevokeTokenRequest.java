package com.nike.vault.client.model;

/**
 * Request object for the revoke token request.
 */
public class VaultRevokeTokenRequest {

    private final String token;

    public VaultRevokeTokenRequest(final String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
