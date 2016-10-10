package com.nike.vault.client.auth;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the TokenVaultCredentials class
 */
public class TokenVaultCredentialsTest {

    @Test
    public void getToken_returns_the_token_set_during_construction() {
        final String token = "TOKEN";

        TokenVaultCredentials credentials = new TokenVaultCredentials(token);

        assertThat(credentials.getToken()).isEqualTo(token);
    }
}