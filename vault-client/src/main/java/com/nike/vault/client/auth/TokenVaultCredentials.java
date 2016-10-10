package com.nike.vault.client.auth;

/**
 * Default implementation of {@link VaultCredentials} that holds a token.
 */
public class TokenVaultCredentials implements VaultCredentials {

    private final String token;

    /**
     * Explicit constructor that sets the token.
     *
     * @param token Token to represent
     */
    public TokenVaultCredentials(final String token) {
        this.token = token;
    }

    /**
     * Returns the token set during construction.
     *
     * @return Token
     */
    @Override
    public String getToken() {
        return token;
    }
}
