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

package com.nike.vault.client;

import okhttp3.HttpUrl;
import org.apache.commons.lang3.StringUtils;

/**
 * Default resolver for the Vault URL.
 */
public class DefaultVaultUrlResolver implements UrlResolver {

    public static final String VAULT_ADDR_ENV_PROPERTY = "VAULT_ADDR";

    public static final String VAULT_ADDR_SYS_PROPERTY = "vault.addr";

    /**
     * Attempts to acquire the Vault URL from the following places:
     * <ul>
     * <li>Environment Variable - <code>VAULT_ADDR</code></li>
     * <li>Java System Property - <code>vault.addr</code></li>
     * </ul>
     *
     * @throws VaultClientException If unable to resolve the URL
     * @return Vault URL
     */
    @Override
    public String resolve() {
        final String envUrl = System.getenv(VAULT_ADDR_ENV_PROPERTY);
        final String sysUrl = System.getProperty(VAULT_ADDR_SYS_PROPERTY);

        if (StringUtils.isNotBlank(envUrl) && HttpUrl.parse(envUrl) != null) {
            return envUrl;
        } else if (StringUtils.isNotBlank(sysUrl) && HttpUrl.parse(sysUrl) != null) {
            return sysUrl;
        }

        throw new VaultClientException(
                "Failed to resolve the Vault URL from the environment and/or system properties.");
    }
}
