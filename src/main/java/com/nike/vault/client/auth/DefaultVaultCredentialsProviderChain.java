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

/**
 * Default credentials provider chain that will attempt to retrieve a token in the following order:
 * <ul>
 * <li>Environment Variable - <code>VAULT_TOKEN</code></li>
 * <li>Java System Property - <code>vault.token</code></li>
 * </ul>
 *
 * @see EnvironmentVaultCredentialsProvider
 * @see SystemPropertyVaultCredentialsProvider
 */
public class DefaultVaultCredentialsProviderChain extends VaultCredentialsProviderChain {

    /**
     * Default constructor that sets up the ordered chain of providers.
     */
    public DefaultVaultCredentialsProviderChain() {
        super(new EnvironmentVaultCredentialsProvider(),
                new SystemPropertyVaultCredentialsProvider());
    }
}
