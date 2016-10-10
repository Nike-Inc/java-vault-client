package com.nike.vault.client;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the StaticVaultUrlResolverTest class
 */
public class StaticVaultUrlResolverTest {

    private final String testUrl = "https://localhost";

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_throws_error_if_vault_url_is_blank() {
        new StaticVaultUrlResolver(" ");
    }

    @Test
    public void test_resolve_returns_url_that_was_set() {
        final UrlResolver urlResolver = new StaticVaultUrlResolver(testUrl);

        assertThat(urlResolver.resolve()).isEqualTo(testUrl);
    }
}