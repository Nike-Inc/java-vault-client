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
 * Tests the SystemPropertyVaultCredentialsProvider class
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SystemPropertyVaultCredentialsProvider.class})
public class SystemPropertyVaultCredentialsProviderTest {

    private static final String TOKEN = "TOKEN";

    private SystemPropertyVaultCredentialsProvider credentialsProvider;

    @Before
    public void setup() {
        credentialsProvider = new SystemPropertyVaultCredentialsProvider();
    }

    @Test
    public void getCredentials_returns_creds_from_system_property_when_set() {
        mockStatic(System.class);
        when(System.getProperty(SystemPropertyVaultCredentialsProvider.VAULT_TOKEN_SYS_PROPERTY)).thenReturn(TOKEN);

        VaultCredentials credentials = credentialsProvider.getCredentials();

        assertThat(credentials).isNotNull();
        assertThat(credentials.getToken()).isEqualTo(TOKEN);
    }

    @Test(expected = VaultClientException.class)
    public void getCredentials_returns_empty_creds_object_when_sys_property_not_set() {
        mockStatic(System.class);
        when(System.getProperty(SystemPropertyVaultCredentialsProvider.VAULT_TOKEN_SYS_PROPERTY)).thenReturn(null);

        credentialsProvider.getCredentials();
    }

    @Test(expected = VaultClientException.class)
    public void getCredentials_returns_empty_creds_object_when_sys_property_is_blank() {
        mockStatic(System.class);
        when(System.getProperty(SystemPropertyVaultCredentialsProvider.VAULT_TOKEN_SYS_PROPERTY)).thenReturn("");

        credentialsProvider.getCredentials();
    }

}