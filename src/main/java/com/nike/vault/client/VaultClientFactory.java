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
import okhttp3.ConnectionSpec;
import okhttp3.Dispatcher;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static okhttp3.ConnectionSpec.CLEARTEXT;
import static okhttp3.ConnectionSpec.MODERN_TLS;

/**
 * Convenience factory for creating instances of Vault clients.
 */
public class VaultClientFactory {

    public static final int DEFAULT_TIMEOUT = 15_000;
    public static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

    /**
     * Modify "MODERN_TLS" to remove TLS v1.0 and 1.1
     */
    public static final ConnectionSpec TLS_1_2_OR_NEWER = new ConnectionSpec.Builder(MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_3, TlsVersion.TLS_1_2)
            .build();

    /**
     * A VaultAdminClient may need to make many requests to Vault simultaneously.
     * <p>
     * (Default value in OkHttpClient for maxRequests was 64 and maxRequestsPerHost was 5).
     */
    private static final int DEFAULT_MAX_REQUESTS = 200;
    private static final Map<String, String> DEFAULT_HEADERS = new HashMap<>();

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
        return getClient(new DefaultVaultUrlResolver(), new DefaultVaultCredentialsProviderChain(), new HashMap<String, String>());
    }

    /**
     * Factory method allows setting of the Vault URL resolver, but will use
     * the default recommended credentials provider chain and http client.
     *
     * @param vaultUrlResolver URL resolver for Vault
     * @return Vault client
     */
    public static VaultClient getClient(final UrlResolver vaultUrlResolver) {
        return getClient(vaultUrlResolver, new DefaultVaultCredentialsProviderChain(), new HashMap<String, String>());
    }

    /**
     * Factory method that allows for a user defined Vault URL resolver and credentials provider.
     *
     * @param vaultUrlResolver         URL resolver for Vault
     * @param vaultCredentialsProvider Credential provider for acquiring a token for interacting with Vault
     * @return Vault client
     */
    public static VaultClient getClient(final UrlResolver vaultUrlResolver,
                                        final VaultCredentialsProvider vaultCredentialsProvider) {
        return getClient(vaultUrlResolver, vaultCredentialsProvider, new HashMap<String, String>());
    }

    /**
     * Factory method that allows a user to define default HTTP defaultHeaders to be added to every HTTP request made from the
     * VaultClient. The user can also define their Vault URL resolver and credentials provider.
     *
     * @param vaultUrlResolver         URL resolver for Vault
     * @param vaultCredentialsProvider Credential provider for acquiring a token for interacting with Vault
     * @param defaultHeaders           Map of default header names and values to add to every HTTP request
     * @return Vault client
     */
    public static VaultClient getClient(final UrlResolver vaultUrlResolver,
                                        final VaultCredentialsProvider vaultCredentialsProvider,
                                        final Map<String, String> defaultHeaders) {

        List<ConnectionSpec> connectionSpecs = new ArrayList<>();
        connectionSpecs.add(TLS_1_2_OR_NEWER);
        // for unit tests
        connectionSpecs.add(CLEARTEXT);

        return getClient(
                vaultUrlResolver,
                vaultCredentialsProvider,
                defaultHeaders,
                new OkHttpClient.Builder()
                        .connectTimeout(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT)
                        .writeTimeout(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT)
                        .readTimeout(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT)
                        .connectionSpecs(connectionSpecs)
                        .build()
        );
    }

    /**
     * Factory method that allows a user to define the OkHttpClient to be used.
     *
     * @param vaultUrlResolver         URL resolver for Vault
     * @param vaultCredentialsProvider Credential provider for acquiring a token for interacting with Vault
     * @param defaultHeaders           Map of default header names and values to add to every HTTP request
     * @param httpClient
     * @return Vault client
     */
    public static VaultClient getClient(final UrlResolver vaultUrlResolver,
                                        final VaultCredentialsProvider vaultCredentialsProvider,
                                        final Map<String, String> defaultHeaders,
                                        final OkHttpClient httpClient) {
        if (defaultHeaders == null) {
            throw new IllegalArgumentException("Default headers cannot be null.");
        }

        Headers.Builder headers = new Headers.Builder();
        for (Map.Entry<String, String> header : defaultHeaders.entrySet()) {
            headers.add(header.getKey(), header.getValue());
        }

        return new VaultClient(vaultUrlResolver,
                vaultCredentialsProvider,
                httpClient,
                headers.build());
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
        return getAdminClient(new DefaultVaultUrlResolver(),
                new DefaultVaultCredentialsProviderChain(),
                DEFAULT_MAX_REQUESTS,
                DEFAULT_MAX_REQUESTS,
                DEFAULT_TIMEOUT,
                DEFAULT_TIMEOUT,
                DEFAULT_TIMEOUT,
                DEFAULT_HEADERS
        );
    }

    /**
     * Factory method allows setting of the Vault URL resolver, but will use
     * the default recommended credentials provider chain and http client.
     *
     * @param vaultUrlResolver URL resolver for Vault
     * @return Vault admin client
     */
    public static VaultAdminClient getAdminClient(final UrlResolver vaultUrlResolver) {
        return getAdminClient(vaultUrlResolver,
                new DefaultVaultCredentialsProviderChain(),
                DEFAULT_MAX_REQUESTS,
                DEFAULT_MAX_REQUESTS,
                DEFAULT_TIMEOUT,
                DEFAULT_TIMEOUT,
                DEFAULT_TIMEOUT,
                DEFAULT_HEADERS
        );
    }

    /**
     * Factory method that allows for a user defined Vault URL resolver and credentials provider.
     *
     * @param vaultUrlResolver         URL resolver for Vault
     * @param vaultCredentialsProvider Credential provider for acquiring a token for interacting with Vault
     * @return Vault admin client
     */
    public static VaultAdminClient getAdminClient(final UrlResolver vaultUrlResolver,
                                                  final VaultCredentialsProvider vaultCredentialsProvider) {
        return getAdminClient(vaultUrlResolver,
                vaultCredentialsProvider,
                DEFAULT_MAX_REQUESTS,
                DEFAULT_MAX_REQUESTS,
                DEFAULT_TIMEOUT,
                DEFAULT_TIMEOUT,
                DEFAULT_TIMEOUT,
                DEFAULT_HEADERS
        );
    }

    /**
     * Factory method that allows for a user defined Vault URL resolver and credentials provider.
     *
     * @param vaultUrlResolver         URL resolver for Vault
     * @param vaultCredentialsProvider Credential provider for acquiring a token for interacting with Vault
     * @param maxRequestsPerHost       Max Requests per Host used by the dispatcher
     * @return Vault admin client
     */
    public static VaultAdminClient getAdminClient(final UrlResolver vaultUrlResolver,
                                                  final VaultCredentialsProvider vaultCredentialsProvider,
                                                  final int maxRequestsPerHost) {
        return getAdminClient(vaultUrlResolver,
                vaultCredentialsProvider,
                DEFAULT_MAX_REQUESTS,
                maxRequestsPerHost,
                DEFAULT_TIMEOUT,
                DEFAULT_TIMEOUT,
                DEFAULT_TIMEOUT,
                DEFAULT_HEADERS);
    }

    /**
     * Factory method that allows a user to define default HTTP headers to be added to every HTTP request made from the
     * VaultClient. The user can also define their Vault URL resolver and credentials provider.
     *
     * @param vaultUrlResolver         URL resolver for Vault
     * @param vaultCredentialsProvider Credential provider for acquiring a token for interacting with Vault
     * @param defaultHeaders           Map of default header names and values to add to every HTTP request
     * @return Vault client
     */
    public static VaultAdminClient getAdminClient(final UrlResolver vaultUrlResolver,
                                                  final VaultCredentialsProvider vaultCredentialsProvider,
                                                  final Map<String, String> defaultHeaders) {
        return getAdminClient(vaultUrlResolver,
                vaultCredentialsProvider,
                DEFAULT_MAX_REQUESTS,
                DEFAULT_MAX_REQUESTS,
                DEFAULT_TIMEOUT,
                DEFAULT_TIMEOUT,
                DEFAULT_TIMEOUT,
                defaultHeaders);
    }

    /**
     * Factory method that allows the user to completely configure the VaultClient.
     *
     * @param vaultUrlResolver         URL resolver for Vault
     * @param vaultCredentialsProvider Credential provider for acquiring a token for interacting with Vault
     * @param maxRequestsPerHost       Max Requests per Host used by the dispatcher
     * @param defaultHeaders           Map of default header names and values to add to every HTTP request
     * @return Vault admin client
     */
    public static VaultAdminClient getAdminClient(final UrlResolver vaultUrlResolver,
                                                  final VaultCredentialsProvider vaultCredentialsProvider,
                                                  final int maxRequestsPerHost,
                                                  final Map<String, String> defaultHeaders) {
        return getAdminClient(vaultUrlResolver,
                vaultCredentialsProvider,
                DEFAULT_MAX_REQUESTS,
                maxRequestsPerHost,
                DEFAULT_TIMEOUT,
                DEFAULT_TIMEOUT,
                DEFAULT_TIMEOUT,
                defaultHeaders);
    }

    /**
     * Factory method that allows the user to completely configure the VaultClient.
     *
     * @param vaultUrlResolver         URL resolver for Vault
     * @param vaultCredentialsProvider Credential provider for acquiring a token for interacting with Vault
     * @param maxRequests              Max HTTP Requests allowed in-flight
     * @param maxRequestsPerHost       Max HTTP Requests per Host
     * @param connectTimeoutMillis     HTTP connect timeout in milliseconds
     * @param readTimeoutMillis        HTTP read timeout in milliseconds
     * @param writeTimeoutMillis       HTTP write timeout in milliseconds
     * @param defaultHeaders           Map of default header names and values to add to every HTTP request
     * @return Vault admin client
     */
    public static VaultAdminClient getAdminClient(final UrlResolver vaultUrlResolver,
                                                  final VaultCredentialsProvider vaultCredentialsProvider,
                                                  final int maxRequests,
                                                  final int maxRequestsPerHost,
                                                  final int connectTimeoutMillis,
                                                  final int readTimeoutMillis,
                                                  final int writeTimeoutMillis,
                                                  final Map<String, String> defaultHeaders) {
        if (defaultHeaders == null) {
            throw new IllegalArgumentException("Default headers cannot be null.");
        }

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(maxRequests);
        dispatcher.setMaxRequestsPerHost(maxRequestsPerHost);


        List<ConnectionSpec> connectionSpecs = new ArrayList<>();
        connectionSpecs.add(TLS_1_2_OR_NEWER);
        // for unit tests
        connectionSpecs.add(CLEARTEXT);

        Headers.Builder headers = new Headers.Builder();
        for (Map.Entry<String, String> header : defaultHeaders.entrySet()) {
            headers.add(header.getKey(), header.getValue());
        }

        return new VaultAdminClient(vaultUrlResolver,
                vaultCredentialsProvider,
                new OkHttpClient.Builder()
                        .connectTimeout(connectTimeoutMillis, DEFAULT_TIMEOUT_UNIT)
                        .writeTimeout(writeTimeoutMillis, DEFAULT_TIMEOUT_UNIT)
                        .readTimeout(readTimeoutMillis, DEFAULT_TIMEOUT_UNIT)
                        .dispatcher(dispatcher)
                        .connectionSpecs(connectionSpecs)
                        .build(),
                headers.build());
    }
}
