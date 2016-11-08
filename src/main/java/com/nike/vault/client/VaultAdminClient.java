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

import com.google.gson.reflect.TypeToken;
import com.nike.vault.client.auth.VaultCredentialsProvider;
import com.nike.vault.client.http.HttpMethod;
import com.nike.vault.client.http.HttpStatus;
import com.nike.vault.client.model.VaultAuthResponse;
import com.nike.vault.client.model.VaultClientTokenResponse;
import com.nike.vault.client.model.VaultEnableAuditBackendRequest;
import com.nike.vault.client.model.VaultHealthResponse;
import com.nike.vault.client.model.VaultInitResponse;
import com.nike.vault.client.model.VaultPolicy;
import com.nike.vault.client.model.VaultRevokeTokenRequest;
import com.nike.vault.client.model.VaultSealStatusResponse;
import com.nike.vault.client.model.VaultTokenAuthRequest;
import com.nike.vault.client.model.VaultUnsealRequest;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Admin client for interacting with Vault's sys endpoints.
 */
public class VaultAdminClient extends VaultClient {

    private static final String SYS_PATH_PREFIX = "v1/sys/";

    private static final Set<Integer> HEALTH_RESPONSE_CODES = new HashSet<>();

    static {
        HEALTH_RESPONSE_CODES.add(HttpStatus.OK);
        HEALTH_RESPONSE_CODES.add(HttpStatus.TOO_MANY_REQUESTS);
        HEALTH_RESPONSE_CODES.add(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Explicit constructor that allows for full control over construction of the Vault client.
     *
     * @param vaultUrlResolver    URL resolver for Vault
     * @param credentialsProvider Credential provider for acquiring a token for interacting with Vault
     * @param httpClient          HTTP client for calling Vault
     */
    public VaultAdminClient(final UrlResolver vaultUrlResolver,
                            final VaultCredentialsProvider credentialsProvider,
                            final OkHttpClient httpClient) {
        super(vaultUrlResolver, credentialsProvider, httpClient);
    }

    /**
     * Initializes a new Vault. The Vault must've not been previously initialized.
     *
     * @param secretShares    The number of shares to split the master key into
     * @param secretThreshold The number of shares required to reconstruct the master key.
     *                        This must be less than or equal to secret_shares
     * @return Object including the master keys and initial root token
     */
    public VaultInitResponse init(final int secretShares, final int secretThreshold) {
        final HttpUrl url = buildUrl(SYS_PATH_PREFIX, "init");

        final Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("secret_shares", secretShares);
        requestBody.put("secret_threshold", secretThreshold);

        final Response response = execute(url, HttpMethod.PUT, requestBody);

        if (response.code() != HttpStatus.OK) {
            parseAndThrowErrorResponse(response);
        }

        return parseResponseBody(response, VaultInitResponse.class);
    }

    /**
     * Returns the health status of Vault. This matches the semantics of a Consul HTTP health check and
     * provides a simple way to monitor the health of a Vault instance.
     *
     * @return Object including status flags for initialized, sealed and standby
     */
    public VaultHealthResponse health() {
        final HttpUrl url = buildUrl(SYS_PATH_PREFIX, "health");
        final Response response = execute(url, HttpMethod.GET, null);

        if (!HEALTH_RESPONSE_CODES.contains(response.code())) {
            parseAndThrowErrorResponse(response);
        }

        return parseResponseBody(response, VaultHealthResponse.class);
    }

    /**
     * Enter a single master key share to progress the unsealing of the Vault. If the threshold number of master key
     * shares is reached, Vault will attempt to unseal the Vault. Otherwise, this API must be called multiple times
     * until that threshold is met.
     * <p>
     * Either the key or reset parameter must be provided; if both are provided, reset takes precedence.
     * </p>
     *
     * @param key   A single master share key
     * @param reset If true, the previously-provided unseal keys are discarded from memory and the unseal process
     *              is reset.
     * @return Seal status
     */
    public VaultSealStatusResponse unseal(final String key, final boolean reset) {
        final HttpUrl url = buildUrl(SYS_PATH_PREFIX, "unseal");
        final VaultUnsealRequest request = new VaultUnsealRequest(key, reset);

        final Response response = execute(url, HttpMethod.PUT, request);

        if (response.code() != HttpStatus.OK) {
            parseAndThrowErrorResponse(response);
        }

        return parseResponseBody(response, VaultSealStatusResponse.class);
    }

    /**
     * Lists all the available policies.
     *
     * @return Set of policy names
     */
    public Set<String> policies() {
        final HttpUrl url = buildUrl(SYS_PATH_PREFIX, "policy");
        final Response response = execute(url, HttpMethod.GET, null);

        if (response.code() != HttpStatus.OK) {
            parseAndThrowErrorResponse(response);
        }

        final Type mapType = new TypeToken<Map<String, Set<String>>>() {
        }.getType();
        final Map<String, Set<String>> policyMap = parseResponseBody(response, mapType);

        return policyMap.get("policies");
    }

    /**
     * Retrieve the rules for the named policy.
     *
     * @param name Policy name
     * @return Policy rules
     */
    public VaultPolicy policy(final String name) {
        final HttpUrl url = buildUrl(SYS_PATH_PREFIX, String.format("policy/%s", name));
        final Response response = execute(url, HttpMethod.GET, null);

        if (response.code() != HttpStatus.OK) {
            parseAndThrowErrorResponse(response);
        }

        return parseResponseBody(response, VaultPolicy.class);
    }

    /**
     * Add or update a policy. Once a policy is updated, it takes effect immediately to all associated users.
     *
     * @param name   Policy name
     * @param policy Policy document
     */
    public void putPolicy(final String name, final VaultPolicy policy) {
        final HttpUrl url = buildUrl(SYS_PATH_PREFIX, String.format("policy/%s", name));
        final Response response = execute(url, HttpMethod.PUT, policy);

        if (response.code() != HttpStatus.NO_CONTENT) {
            parseAndThrowErrorResponse(response);
        }
    }

    /**
     * Delete the policy with the given name. This will immediately affect all associated users.
     *
     * @param name Policy name
     */
    public void deletePolicy(final String name) {
        final HttpUrl url = buildUrl(SYS_PATH_PREFIX, String.format("policy/%s", name));
        final Response response = execute(url, HttpMethod.DELETE, null);

        if (response.code() != HttpStatus.NO_CONTENT) {
            parseAndThrowErrorResponse(response);
        }
    }

    /**
     * Creates a new token. Certain options are only available to when called by a root token.
     *
     * @param vaultTokenAuthRequest Request object with optional parameters
     * @return Auth response with the token and details
     */
    public VaultAuthResponse createToken(final VaultTokenAuthRequest vaultTokenAuthRequest) {
        final HttpUrl url = buildUrl(AUTH_PATH_PREFIX, "token/create");
        final Response response = execute(url, HttpMethod.POST, vaultTokenAuthRequest);

        if (response.code() != HttpStatus.OK) {
            parseAndThrowErrorResponse(response);
        }

        final Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        final Map<String, Object> authData = parseResponseBody(response, mapType);
        return getGson().fromJson(getGson().toJson(authData.get("auth")), VaultAuthResponse.class);
    }

    /**
     * Creates a new token. Certain options are only available to when called by a root token.
     * A root token is not required to create an orphan token (otherwise set with the no_parent option).
     *
     * @param vaultTokenAuthRequest Request object with optional parameters
     * @return Auth response with the token and details
     */
    public VaultAuthResponse createOrphanToken(final VaultTokenAuthRequest vaultTokenAuthRequest) {
        final HttpUrl url = buildUrl(AUTH_PATH_PREFIX, "token/create-orphan");
        final Response response = execute(url, HttpMethod.POST, vaultTokenAuthRequest);

        if (response.code() != HttpStatus.OK) {
            parseAndThrowErrorResponse(response);
        }

        final Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        final Map<String, Object> authData = parseResponseBody(response, mapType);
        return getGson().fromJson(getGson().toJson(authData.get("auth")), VaultAuthResponse.class);
    }

    /**
     * Revokes a token and all child tokens. When the token is revoked, all secrets generated with it are also revoked.
     *
     * @param token Token to revoke
     */
    public void revokeToken(final String token) {
        final HttpUrl url = buildUrl(AUTH_PATH_PREFIX, String.format("token/revoke/%s", token));
        final Response response = execute(url, HttpMethod.POST, new VaultRevokeTokenRequest(token));

        if (response.code() != HttpStatus.NO_CONTENT) {
            parseAndThrowErrorResponse(response);
        }
    }

    /**
     * Revokes a token but not its child tokens. When the token is revoked, all secrets generated with it are also
     * revoked. All child tokens are orphaned, but can be revoked subsequently using /auth/token/revoke/.
     * This is a root-protected endpoint.
     *
     * @param token Token to revoke
     */
    public void revokeOrphanToken(final String token) {
        final HttpUrl url = buildUrl(AUTH_PATH_PREFIX, String.format("token/revoke-orphan/%s", token));
        final Response response = execute(url, HttpMethod.POST, new VaultRevokeTokenRequest(token));

        if (response.code() != HttpStatus.NO_CONTENT) {
            parseAndThrowErrorResponse(response);
        }
    }

    /**
     * Lookup up the specified token and return details about it.
     *
     * @param token Token to lookup
     * @return Token details
     */
    public VaultClientTokenResponse lookupToken(final String token) {
        final HttpUrl url = buildUrl(AUTH_PATH_PREFIX, String.format("token/lookup/%s", token));
        final Response response = execute(url, HttpMethod.GET, null);

        if (response.code() != HttpStatus.OK) {
            parseAndThrowErrorResponse(response);
        }

        final Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        final Map<String, Object> rootData = parseResponseBody(response, mapType);
        return getGson().fromJson(getGson().toJson(rootData.get("data")), VaultClientTokenResponse.class);
    }

    /**
     * Enables the specified audit backend.
     *
     * @param path    Audit backend path
     * @param request Audit backend details
     */
    public void enableAuditBackend(final String path,
                                   final VaultEnableAuditBackendRequest request) {
        final HttpUrl url = buildUrl(SYS_PATH_PREFIX, String.format("audit/%s", path));
        final Response response = execute(url, HttpMethod.PUT, request);

        if (response.code() != HttpStatus.NO_CONTENT) {
            parseAndThrowErrorResponse(response);
        }
    }

    /**
     * Disables the specified audit backend.
     *
     * @param path Audit backend path
     */
    public void disableAuditBackend(final String path) {
        final HttpUrl url = buildUrl(SYS_PATH_PREFIX, String.format("audit/%s", path));
        final Response response = execute(url, HttpMethod.DELETE, null);

        if (response.code() != HttpStatus.NO_CONTENT) {
            parseAndThrowErrorResponse(response);
        }
    }

    /**
     * Barebones method that can be used to make any call to Vault.  The caller is responsible for interpreting
     * and de-serializing the response.  The Gson instance used by the client is accessible via {@link #getGson()}
     *
     * @param path        Path to the resource
     * @param method      HTTP method
     * @param requestBody Request body to be serialized as JSON.  Set to null if no request body
     * @return HTTP response object
     */
    public Response execute(final String path, final String method, final Object requestBody) {
        final HttpUrl url = buildUrl("", path);
        return execute(url, method, requestBody);
    }
}
