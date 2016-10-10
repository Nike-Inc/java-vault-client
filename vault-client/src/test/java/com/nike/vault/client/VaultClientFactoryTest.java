package com.nike.vault.client;

import com.nike.vault.client.auth.TokenVaultCredentials;
import com.nike.vault.client.auth.VaultCredentials;
import com.nike.vault.client.auth.VaultCredentialsProvider;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the VaultClientFactoryTest class
 */
public class VaultClientFactoryTest {

    private final String url = "https://localhost/";

    private final StaticVaultUrlResolver urlResolver = new StaticVaultUrlResolver(url);

    private final String TOKEN = "TOKEN";

    private final VaultCredentialsProvider credentialsProvider = new VaultCredentialsProvider() {

        @Override
        public VaultCredentials getCredentials() {
            return new TokenVaultCredentials(TOKEN);
        }
    };

    @Test
    public void test_get_client_returns_configured_client() {
        final VaultClient client = VaultClientFactory.getClient();
        assertThat(client).isNotNull();
    }

    @Test
    public void test_get_client_uses_custom_url_resolver() {
        final VaultClient client = VaultClientFactory.getClient(urlResolver);
        assertThat(client).isNotNull();
        assertThat(client.getVaultUrl().url().toString()).isEqualTo(url);
    }

    @Test
    public void test_get_client_uses_custom_url_resolver_and_creds_provider() {
        final VaultClient client = VaultClientFactory.getClient(urlResolver, credentialsProvider);
        assertThat(client).isNotNull();
        assertThat(client.getVaultUrl().url().toString()).isEqualTo(url);
        assertThat(client.getCredentialsProvider()).isNotNull();
        assertThat(client.getCredentialsProvider().getCredentials().getToken()).isEqualTo(TOKEN);
    }

    @Test
    public void test_get_admin_client_returns_configured_client() {
        final VaultAdminClient client = VaultClientFactory.getAdminClient();
        assertThat(client).isNotNull();
    }

    @Test
    public void test_get_admin_client_uses_custom_url_resolver() {
        final VaultAdminClient client = VaultClientFactory.getAdminClient(urlResolver);
        assertThat(client).isNotNull();
        assertThat(client.getVaultUrl().url().toString()).isEqualTo(url);
    }

    @Test
    public void test_get_admin_client_uses_custom_url_resolver_and_creds_provider() {
        final VaultAdminClient client = VaultClientFactory.getAdminClient(urlResolver, credentialsProvider);
        assertThat(client).isNotNull();
        assertThat(client.getVaultUrl().url().toString()).isEqualTo(url);
        assertThat(client.getCredentialsProvider()).isNotNull();
        assertThat(client.getCredentialsProvider().getCredentials().getToken()).isEqualTo(TOKEN);
    }
}