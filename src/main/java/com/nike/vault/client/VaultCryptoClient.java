package com.nike.vault.client;

import static com.nike.vault.client.model.VaultCreateKeyRequest.TYPE_AES256_GCM96;

import com.google.gson.reflect.TypeToken;
import com.nike.vault.client.auth.VaultCredentialsProvider;
import com.nike.vault.client.http.HttpMethod;
import com.nike.vault.client.http.HttpStatus;
import com.nike.vault.client.model.VaultAsymmetricKeyResponse;
import com.nike.vault.client.model.VaultCreateKeyRequest;
import com.nike.vault.client.model.VaultDecryptDataRequest;
import com.nike.vault.client.model.VaultDecryptDataResponse;
import com.nike.vault.client.model.VaultEncryptDataRequest;
import com.nike.vault.client.model.VaultEncryptDataResponse;
import com.nike.vault.client.model.VaultKeyResponse;
import com.nike.vault.client.model.VaultSymmetricKeyResponse;
import java.lang.reflect.Type;
import java.util.Map;
import javax.annotation.Nonnull;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class VaultCryptoClient extends VaultClient {

  private static final String TRANSIT_PATH_PREFIX = "v1/transit/";

  /**
   * Explicit constructor that allows for full control over construction of the Vault client.
   *
   * @param vaultUrlResolver    URL resolver for Vault
   * @param credentialsProvider Credential provider for acquiring a token for interacting with Vault
   * @param httpClient          HTTP client for calling Vault
   */
  public VaultCryptoClient(final UrlResolver vaultUrlResolver,
      final VaultCredentialsProvider credentialsProvider,
      final OkHttpClient httpClient) {
    super(vaultUrlResolver, credentialsProvider, httpClient);
  }

  /**
   * Explicit constructor that allows for full control over construction of the Vault client.
   *
   * @param vaultUrlResolver    URL resolver for Vault
   * @param credentialsProvider Credential provider for acquiring a token for interacting with Vault
   * @param httpClient          HTTP client for calling Vault
   * @param defaultHeaders      Default HTTP headers to be included in each request made by the returned VaultClient
   */
  public VaultCryptoClient(final UrlResolver vaultUrlResolver,
      final VaultCredentialsProvider credentialsProvider,
      final OkHttpClient httpClient,
      final Headers defaultHeaders) {
    super(vaultUrlResolver, credentialsProvider, httpClient, defaultHeaders);
  }

  /**
   * Creates a new key.
   *
   * @param name Key name
   * @param vaultCreateKeyRequest Request object with optional parameters
   */
  public void createKey(@Nonnull final String name, @Nonnull final VaultCreateKeyRequest vaultCreateKeyRequest) {
    final HttpUrl url = buildUrl(TRANSIT_PATH_PREFIX, "keys/" + name);
    final Response response = execute(url, HttpMethod.POST, vaultCreateKeyRequest);

    if (response.code() != HttpStatus.NO_CONTENT) {
      parseAndThrowErrorResponse(response);
    }
  }

  /**
   * Retrieve key information
   *
   * @param name Name of the key to lookup
   * @return Key information response, can be either Symmetric or Asymmetric keys
   */
  public VaultKeyResponse getKeyInfo(@Nonnull final String name) {
    final HttpUrl url = buildUrl(TRANSIT_PATH_PREFIX, "keys/" + name);
    final Response response = execute(url, HttpMethod.GET, null);

    if (response.code() != HttpStatus.OK) {
      parseAndThrowErrorResponse(response);
    }

    final Type mapType = new TypeToken<Map<String, Object>>() {
    }.getType();
    final Map<String, Object> rootData = parseResponseBody(response, mapType);
    VaultKeyResponse keyResponse = getGson()
        .fromJson(getGson().toJson(rootData.get("data")), VaultKeyResponse.class);
    switch (keyResponse.getType()) {
      case TYPE_AES256_GCM96:
        return getGson()
            .fromJson(getGson().toJson(rootData.get("data")), VaultSymmetricKeyResponse.class);
      default:
        return getGson()
            .fromJson(getGson().toJson(rootData.get("data")), VaultAsymmetricKeyResponse.class);
    }

  }

  /**
   * Encrypt data with a key stored in Vault
   * @param keyName Name of the key to use for the cryptographic operation
   * @param encryptRequest Encryption data
   * @return Encryption response
   */
  @Nonnull
  public VaultEncryptDataResponse encrypt(@Nonnull String keyName, @Nonnull VaultEncryptDataRequest encryptRequest) {
    final HttpUrl url = buildUrl(TRANSIT_PATH_PREFIX, "encrypt/" + keyName);
    final Response response = execute(url, HttpMethod.POST, encryptRequest);

    if (response.code() != HttpStatus.OK) {
      parseAndThrowErrorResponse(response);
    }

    return parseResponse(response, VaultEncryptDataResponse.class);
  }

  /**
   * Decrypt data with a key stored in Vault
   * @param keyName Name of the key to use for the cryptographic operation
   * @param decryptRequest Decryption data
   * @return Decryption response
   */
  @Nonnull
  public VaultDecryptDataResponse decrypt(@Nonnull String keyName, @Nonnull VaultDecryptDataRequest decryptRequest) {
    final HttpUrl url = buildUrl(TRANSIT_PATH_PREFIX, "decrypt/" + keyName);
    final Response response = execute(url, HttpMethod.POST, decryptRequest);

    if (response.code() != HttpStatus.OK) {
      parseAndThrowErrorResponse(response);
    }

    return parseResponse(response, VaultDecryptDataResponse.class);
  }

}
