package com.nike.vault.client.auth;

import com.nike.vault.client.VaultClientException;
import org.apache.commons.lang3.StringUtils;

/**
 * {@link VaultCredentialsProvider} implementation that attempts to acquire the token
 * via the environment variable, <code>VAULT_TOKEN</code>.
 */
public class EnvironmentVaultCredentialsProvider implements VaultCredentialsProvider {

    public static final String VAULT_TOKEN_ENV_PROPERTY = "VAULT_TOKEN";

    /**
     * Attempts to acquire credentials from an environment variable.
     *
     * @return credentials
     */
    @Override
    public VaultCredentials getCredentials() {
        final String token = System.getenv(VAULT_TOKEN_ENV_PROPERTY);

        if (StringUtils.isNotBlank(token)) {
            return new TokenVaultCredentials(token);
        }

        throw new VaultClientException("Vault token not found in the environment property.");
    }
}
