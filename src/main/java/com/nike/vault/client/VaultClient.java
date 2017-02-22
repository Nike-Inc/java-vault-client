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

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.nike.vault.client.auth.VaultCredentialsProvider;
import com.nike.vault.client.http.HttpHeader;
import com.nike.vault.client.http.HttpMethod;
import com.nike.vault.client.http.HttpStatus;
import com.nike.vault.client.model.VaultClientTokenResponse;
import com.nike.vault.client.model.VaultListResponse;
import com.nike.vault.client.model.VaultResponse;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Client for interacting with a Vault.
 */
public class VaultClient {

    public static final String SECRET_PATH_PREFIX = "v1/secret/";

    public static final String AUTH_PATH_PREFIX = "v1/auth/";

    public static final MediaType DEFAULT_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private final VaultCredentialsProvider credentialsProvider;

    private final OkHttpClient httpClient;

    private final UrlResolver urlResolver;

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .disableHtmlEscaping()
            .create();

    /**
     * Explicit constructor that allows for full control over construction of the Vault client.
     *
     * @param vaultUrlResolver    URL resolver for Vault
     * @param credentialsProvider Credential provider for acquiring a token for interacting with Vault
     * @param httpClient          HTTP client for calling Vault
     */
    public VaultClient(final UrlResolver vaultUrlResolver,
                       final VaultCredentialsProvider credentialsProvider,
                       final OkHttpClient httpClient) {
        if (vaultUrlResolver == null) {
            throw new IllegalArgumentException("Vault URL resolver can not be null.");
        }

        if (credentialsProvider == null) {
            throw new IllegalArgumentException("Credentials provider can not be null.");
        }

        if (httpClient == null) {
            throw new IllegalArgumentException("Http client can not be null.");
        }

        this.urlResolver = vaultUrlResolver;
        this.credentialsProvider = credentialsProvider;
        this.httpClient = httpClient;
    }


    /**
     * List operation for the specified path.  Will return a {@link Map} with a single entry of keys which is an
     * array of strings that represents the keys at that path. If Vault returns an unexpected response code, a
     * {@link VaultServerException} will be thrown with the code and error details.  If an unexpected I/O error is
     * encountered, a {@link VaultClientException} will be thrown wrapping the underlying exception.
     * <p>
     * See https://www.vaultproject.io/docs/secrets/generic/index.html for details on what the list operation returns.
     * </p>
     *
     * @param path Path to the data
     * @return Map containing the keys at that path
     */
    public VaultListResponse list(final String path) {
        final HttpUrl url = buildUrl(SECRET_PATH_PREFIX, path + "?list=true");
        final Response response = execute(url, HttpMethod.GET, null);

        if (response.code() == HttpStatus.NOT_FOUND) {
            return new VaultListResponse();
        } else if (response.code() != HttpStatus.OK) {
            parseAndThrowErrorResponse(response);
        }

        final Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        final Map<String, Object> rootData = parseResponseBody(response, mapType);
        return gson.fromJson(gson.toJson(rootData.get("data")), VaultListResponse.class);
    }

    /**
     * Read operation for a specified path.  Will return a {@link Map} of the data stored at the specified path.
     * If Vault returns an unexpected response code, a {@link VaultServerException} will be thrown with the code
     * and error details.  If an unexpected I/O error is encountered, a {@link VaultClientException} will be thrown
     * wrapping the underlying exception.
     *
     * @param path Path to the data
     * @return Map of the data
     */
    public VaultResponse read(final String path) {
        final HttpUrl url = buildUrl(SECRET_PATH_PREFIX, path);
        final Response response = execute(url, HttpMethod.GET, null);

        if (response.code() != HttpStatus.OK) {
            parseAndThrowErrorResponse(response);
        }

        return parseResponseBody(response, VaultResponse.class);
    }

    /**
     * Write operation for a specified path and data set. If Vault returns an unexpected response code, a
     * {@link VaultServerException} will be thrown with the code and error details.  If an unexpected I/O
     * error is encountered, a {@link VaultClientException} will be thrown wrapping the underlying exception.
     *
     * @param path Path for where to store the data
     * @param data Data to be stored
     */
    public void write(final String path, final Map<String, String> data) {
        final HttpUrl url = buildUrl(SECRET_PATH_PREFIX, path);
        final Response response = execute(url, HttpMethod.POST, data);

        if (response.code() != HttpStatus.NO_CONTENT) {
            parseAndThrowErrorResponse(response);
        }
    }

    /**
     * Delete operation for a specified path.  If Vault returns an unexpected response code, a
     * {@link VaultServerException} will be thrown with the code and error details.  If an unexpected I/O
     * error is encountered, a {@link VaultClientException} will be thrown wrapping the underlying exception.
     *
     * @param path Path to data to be deleted
     */
    public void delete(final String path) {
        final HttpUrl url = buildUrl(SECRET_PATH_PREFIX, path);
        final Response response = execute(url, HttpMethod.DELETE, null);

        if (response.code() != HttpStatus.NO_CONTENT) {
            parseAndThrowErrorResponse(response);
        }
    }

    /**
     * Gets all the details about the client token being used by the requester.  Also serves as a simple way
     * to test that a token is still active.  If an unexpected response is recieved, a {@link VaultServerException}
     * will be thrown with details.
     *
     * @return Client token details
     */
    public VaultClientTokenResponse lookupSelf() {
        final HttpUrl url = buildUrl(AUTH_PATH_PREFIX, "token/lookup-self");
        final Response response = execute(url, HttpMethod.GET, null);

        if (response.code() != HttpStatus.OK) {
            parseAndThrowErrorResponse(response);
        }

        final Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        final Map<String, Object> rootData = parseResponseBody(response, mapType);
        return gson.fromJson(gson.toJson(rootData.get("data")), VaultClientTokenResponse.class);
    }

    /**
     * Returns a copy of the URL being used for communicating with Vault
     *
     * @return Copy of the HttpUrl object
     */
    public HttpUrl getVaultUrl() {
        return HttpUrl.parse(urlResolver.resolve());
    }

    /**
     * Returns the configured credentials provider.
     *
     * @return The configured credentials provider
     */
    public VaultCredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    /**
     * Gets the Gson object used for serializing and de-serializing requests.
     *
     * @return Gson object
     */
    public Gson getGson() {
        return gson;
    }

    /**
     * Builds the full URL for preforming an operation against Vault.
     *
     * @param prefix Prefix between the environment URL and specified path
     * @param path   Path for the requested operation
     * @return Full URL to execute a request against
     */
    protected HttpUrl buildUrl(final String prefix, final String path) {
        String baseUrl = urlResolver.resolve();

        if (!StringUtils.endsWith(baseUrl, "/")) {
            baseUrl += "/";
        }

        return HttpUrl.parse(baseUrl + prefix + path);
    }

    /**
     * Executes the HTTP request based on the input parameters.
     *
     * @param url         The URL to execute the request against
     * @param method      The HTTP method for the request
     * @param requestBody The request body of the HTTP request
     * @return Response from the server
     */
    protected Response execute(final HttpUrl url, final String method, final Object requestBody) {
        try {
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .addHeader(HttpHeader.VAULT_TOKEN, credentialsProvider.getCredentials().getToken())
                    .addHeader(HttpHeader.ACCEPT, DEFAULT_MEDIA_TYPE.toString());

            if (requestBody != null) {
                requestBuilder.addHeader(HttpHeader.CONTENT_TYPE, DEFAULT_MEDIA_TYPE.toString())
                        .method(method, RequestBody.create(DEFAULT_MEDIA_TYPE, gson.toJson(requestBody)));
            } else {
                requestBuilder.method(method, null);
            }

            return httpClient.newCall(requestBuilder.build()).execute();
        } catch (IOException e) {
            if (e instanceof SSLException
                    && e.getMessage() != null
                    && e.getMessage().contains("Unrecognized SSL message, plaintext connection?")) {
                // AnyConnect web security proxy can be disabled with:
                //  `sudo /opt/cisco/anyconnect/bin/acwebsecagent -disablesvc -websecurity`
                throw new VaultClientException("I/O error while communicating with vault. Unrecognized SSL message may be due to a web proxy e.g. AnyConnect", e);
            } else {
                throw new VaultClientException("I/O error while communicating with vault.", e);
            }
        }
    }

    /**
     * Convenience method for parsing the HTTP response and mapping it to a class.
     *
     * @param response      The HTTP response object
     * @param responseClass The class to map the response body to
     * @param <M>           Represents the type to map to
     * @return Deserialized object from the response body
     */
    protected <M> M parseResponseBody(final Response response, final Class<M> responseClass) {
        try {
            return gson.fromJson(response.body().string(), responseClass);
        } catch (IOException|JsonSyntaxException e) {
            throw new VaultClientException("Error parsing the response body from vault, response code: " + response.code(), e);
        }
    }

    /**
     * Convenience method for parsing the HTTP response and mapping it to a type.
     *
     * @param response The HTTP response object
     * @param typeOf   The type to map the response body to
     * @param <M>      Represents the type to map to
     * @return Deserialized object from the response body
     */
    protected <M> M parseResponseBody(final Response response, final Type typeOf) {
        try {
            return gson.fromJson(response.body().string(), typeOf);
        } catch (IOException|JsonSyntaxException e) {
            throw new VaultClientException("Error parsing the response body from vault, response code: " + response.code(), e);
        }
    }

    /**
     * Convenience method for parsing the errors from the HTTP response and throwing a {@link VaultServerException}.
     *
     * @param response Response to parses the error details from
     */
    protected void parseAndThrowErrorResponse(final Response response) {
        try {
            ErrorResponse errorResponse = gson.fromJson(response.body().string(), ErrorResponse.class);

            if (errorResponse != null) {
                throw new VaultServerException(response.code(), errorResponse.getErrors());
            } else {
                throw new VaultServerException(response.code(), new LinkedList<String>());
            }
        } catch (IOException|JsonSyntaxException e) {
            throw new VaultClientException("Error parsing the error response body from vault, response code: " + response.code(), e);
        }
    }

    /**
     * POJO for representing error response body from Vault.
     */
    protected static class ErrorResponse {
        private List<String> errors;

        public List<String> getErrors() {
            return errors;
        }
    }
}
