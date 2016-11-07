/*
 * Copyright (c) 2016 Nike, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
