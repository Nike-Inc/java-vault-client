package com.nike.vault.client.auth;

import com.nike.vault.client.VaultClientException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Tests the EnvironmentVaultCredentialsProvider class
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({EnvironmentVaultCredentialsProvider.class})
public class EnvironmentVaultCredentialsProviderTest {

    private static final String TOKEN = "TOKEN";

    private EnvironmentVaultCredentialsProvider credentialsProvider;

    @Before
    public void setup() {
        credentialsProvider = new EnvironmentVaultCredentialsProvider();
    }

    @Test
    public void getCredentials_returns_creds_from_env_when_set() {
        mockStatic(System.class);
        when(System.getenv(EnvironmentVaultCredentialsProvider.VAULT_TOKEN_ENV_PROPERTY)).thenReturn(TOKEN);

        VaultCredentials credentials = credentialsProvider.getCredentials();

        assertThat(credentials).isNotNull();
        assertThat(credentials.getToken()).isEqualTo(TOKEN);
    }

    @Test(expected = VaultClientException.class)
    public void getCredentials_throws_client_exception_when_not_set() {
        mockStatic(System.class);
        when(System.getenv(EnvironmentVaultCredentialsProvider.VAULT_TOKEN_ENV_PROPERTY)).thenReturn(null);

        credentialsProvider.getCredentials();
    }

    @Test(expected = VaultClientException.class)
    public void getCredentials_returns_empty_creds_object_when_env_variable_is_blank() {
        mockStatic(System.class);
        when(System.getenv(EnvironmentVaultCredentialsProvider.VAULT_TOKEN_ENV_PROPERTY)).thenReturn("");

        credentialsProvider.getCredentials();
    }
}