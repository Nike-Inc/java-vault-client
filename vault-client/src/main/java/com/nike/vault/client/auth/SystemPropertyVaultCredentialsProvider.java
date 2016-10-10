package com.nike.vault.client.auth;

import com.nike.vault.client.VaultClientException;
import org.apache.commons.lang3.StringUtils;

/**
 * {@link VaultCredentialsProvider} implementation that attempts to acquire the token
 * via the system property, <code>vault.token</code>.
 */
public class SystemPropertyVaultCredentialsProvider implements VaultCredentialsProvider {

    public static final String VAULT_TOKEN_SYS_PROPERTY = "vault.token";

    /**
     * Attempts to acquire credentials from an java system property.
     *
     * @return credentials
     */
    @Override
    public VaultCredentials getCredentials() {
        final String token = System.getProperty(VAULT_TOKEN_SYS_PROPERTY);

        if (StringUtils.isNotBlank(token)) {
            return new TokenVaultCredentials(token);
        }

        throw new VaultClientException("Vault token not found in the java system property.");
    }
}
