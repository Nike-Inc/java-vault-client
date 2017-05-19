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

import com.nike.vault.client.auth.DefaultVaultCredentialsProviderChain;
import com.nike.vault.client.auth.VaultCredentialsProvider;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Convenience factory for creating instances of Vault clients.
 */
public class VaultClientFactory {

    private static final int DEFAULT_TIMEOUT = 15;
    private static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    /**
     * A VaultAdminClient may need to make many requests to Vault simultaneously.
     *
     * (Default value in OkHttpClient was 5).
     */
    private static final int DEFAULT_MAX_REQUESTS_PER_HOST = 200;

    /**
     * Basic factory method that will build a Vault client that
     * looks up the Vault URL from one of the following places:
     * <ul>
     * <li>Environment Variable - <code>VAULT_ADDR</code></li>
     * <li>Java System Property - <code>vault.addr</code></li>
     * </ul>
     * Default recommended credential provider and http client are used.
     *
     * @return Vault client
     */
    public static VaultClient getClient() {
        return new VaultClient(new DefaultVaultUrlResolver(),
                new DefaultVaultCredentialsProviderChain(),
                new OkHttpClient.Builder()
                        .connectTimeout(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT)
                        .writeTimeout(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT)
                        .readTimeout(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT)
                        .build());
    }

    /**
     * Factory method allows setting of the Vault URL resolver, but will use
     * the default recommended credentials provider chain and http client.
     *
     * @param vaultUrlResolver URL resolver for Vault
     * @return Vault client
     */
    public static VaultClient getClient(final UrlResolver vaultUrlResolver) {
        return new VaultClient(vaultUrlResolver,
                new DefaultVaultCredentialsProviderChain(),
                new OkHttpClient.Builder()
                        .connectTimeout(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT)
                        .writeTimeout(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT)
                        .readTimeout(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT)
                        .build());
    }

    /**
     * Factory method that allows for a user defined Vault URL resolver and credentials provider.
     *
     * @param vaultUrlResolver    URL resolver for Vault
     * @param vaultCredentialsProvider Credential provider for acquiring a token for interacting with Vault
     * @return Vault client
     */
    public static VaultClient getClient(final UrlResolver vaultUrlResolver,
                                        final VaultCredentialsProvider vaultCredentialsProvider) {
        return new VaultClient(vaultUrlResolver, vaultCredentialsProvider, new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT)
                .writeTimeout(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT)
                .readTimeout(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT)
                .build());
    }

    /**
     * Basic factory method that will build a Vault admin client that
     * looks up the Vault URL from one of the following places:
     * <ul>
     * <li>Environment Variable - <code>VAULT_ADDR</code></li>
     * <li>Java System Property - <code>vault.addr</code></li>
     * </ul>
     * Default recommended credential provider and http client are used.
     *
     * @return Vault admin client
     */
    public static VaultAdminClient getAdminClient() {
        return new VaultAdminClient(new DefaultVaultUrlResolver(),
                new DefaultVaultCredentialsProviderChain(),
                new OkHttpClient.Builder()
                        .connectTimeout(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT)
                        .writeTimeout(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT)
                        .readTimeout(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT)
                        .build());
    }

    /**
     * Factory method allows setting of the Vault URL resolver, but will use
     * the default recommended credentials provider chain and http client.
     *
     * @param vaultUrlResolver URL resolver for Vault
     * @return Vault admin client
     */
    public static VaultAdminClient getAdminClient(final UrlResolver vaultUrlResolver) {
        return getAdminClient(vaultUrlResolver, new DefaultVaultCredentialsProviderChain(), DEFAULT_MAX_REQUESTS_PER_HOST);
    }

    /**
     * Factory method that allows for a user defined Vault URL resolver and credentials provider.
     *
     * @param vaultUrlResolver    URL resolver for Vault
     * @param vaultCredentialsProvider Credential provider for acquiring a token for interacting with Vault
     * @return Vault admin client
     */
    public static VaultAdminClient getAdminClient(final UrlResolver vaultUrlResolver,
                                                  final VaultCredentialsProvider vaultCredentialsProvider) {
        return getAdminClient(vaultUrlResolver, vaultCredentialsProvider, DEFAULT_MAX_REQUESTS_PER_HOST);
    }

    /**
     * Factory method that allows for a user defined Vault URL resolver and credentials provider.
     *
     * @param vaultUrlResolver    URL resolver for Vault
     * @param vaultCredentialsProvider Credential provider for acquiring a token for interacting with Vault
     * @param maxRequestsPerHost Max Requests per Host used by the dispatcher
     * @return Vault admin client
     */
    public static VaultAdminClient getAdminClient(final UrlResolver vaultUrlResolver,
                                                  final VaultCredentialsProvider vaultCredentialsProvider,
                                                  final int maxRequestsPerHost) {

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(maxRequestsPerHost);

        return new VaultAdminClient(vaultUrlResolver, vaultCredentialsProvider, new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT)
                .writeTimeout(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT)
                .readTimeout(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT)
                .dispatcher(dispatcher)
                .build());
    }
}
