package com.nike.vault.client;

import com.nike.vault.client.auth.DefaultVaultCredentialsProviderChain;
import com.nike.vault.client.auth.VaultCredentials;
import com.nike.vault.client.auth.VaultCredentialsProvider;
import com.nike.vault.client.http.HttpStatus;
import com.nike.vault.client.model.VaultClientTokenResponse;
import com.nike.vault.client.model.VaultListResponse;
import com.nike.vault.client.model.VaultResponse;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the VaultClient class
 */
public class VaultClientTest {

    private VaultClient vaultClient;

    private MockWebServer mockWebServer;

    @Before
    public void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        final String vaultUrl = "http://localhost:" + mockWebServer.getPort();
        final VaultCredentialsProvider vaultCredentialsProvider = mock(VaultCredentialsProvider.class);
        vaultClient = VaultClientFactory.getClient(
                new StaticVaultUrlResolver(vaultUrl),
                vaultCredentialsProvider);

        when(vaultCredentialsProvider.getCredentials()).thenReturn(new TestVaultCredentials());
    }

    @After
    public void teardown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_throws_error_if_no_resolver_set() {
        new VaultClient(null,
                new DefaultVaultCredentialsProviderChain(),
                new OkHttpClient.Builder().build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_throws_error_if_no_creds_provider() {
        new VaultClient(new DefaultVaultUrlResolver(),
                null,
                new OkHttpClient.Builder().build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_throws_error_if_no_http_client() {
        new VaultClient(new DefaultVaultUrlResolver(),
                new DefaultVaultCredentialsProviderChain(),
                null);
    }

    @Test
    public void list_returns_map_of_keys_for_specified_path_if_exists() throws IOException {
        final MockResponse response = new MockResponse();
        response.setResponseCode(200);
        response.setBody(getResponseJson("list"));
        mockWebServer.enqueue(response);

        VaultListResponse vaultListResponse = vaultClient.list("app/demo");

        assertThat(vaultListResponse).isNotNull();
        assertThat(vaultListResponse.getKeys()).isNotEmpty();
        assertThat(vaultListResponse.getKeys()).contains("foo", "foo/");
    }

    @Test
    public void list_returns_an_empty_response_if_vault_returns_a_404() throws IOException {
        final MockResponse response = new MockResponse();
        response.setResponseCode(404);
        mockWebServer.enqueue(response);

        VaultListResponse vaultListResponse = vaultClient.list("app/demo");

        assertThat(vaultListResponse).isNotNull();
        assertThat(vaultListResponse.getKeys()).isEmpty();
    }

    @Test
    public void read_returns_map_of_data_for_specified_path_if_exists() throws IOException {
        final MockResponse response = new MockResponse();
        response.setResponseCode(200);
        response.setBody(getResponseJson("secret"));
        mockWebServer.enqueue(response);

        VaultResponse vaultResponse = vaultClient.read("app/api-key");

        assertThat(vaultResponse).isNotNull();
        assertThat(vaultResponse.getData().containsKey("value")).isTrue();
        assertThat(vaultResponse.getData().get("value")).isEqualToIgnoringCase("world");
    }

    @Test
    public void read_throws_vault_server_exception_if_response_is_not_ok() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(404);
        response.setBody(getResponseJson("error"));
        mockWebServer.enqueue(response);

        try {
            vaultClient.read("app/not-found-path");
        } catch (VaultServerException se) {
            assertThat(se.getCode()).isEqualTo(404);
            assertThat(se.getErrors()).hasSize(2);
        }
    }

    @Test(expected = VaultClientException.class)
    public void read_throws_runtime_exception_if_unexpected_error_encountered() throws IOException {
        final ServerSocket serverSocket = new ServerSocket(0);
        final String vaultUrl = "http://localhost:" + serverSocket.getLocalPort();
        final VaultCredentialsProvider vaultCredentialsProvider = mock(VaultCredentialsProvider.class);
        final OkHttpClient httpClient = buildHttpClient(1, TimeUnit.SECONDS);
        vaultClient = new VaultClient(new StaticVaultUrlResolver(vaultUrl), vaultCredentialsProvider, httpClient);

        when(vaultCredentialsProvider.getCredentials()).thenReturn(new TestVaultCredentials());

        vaultClient.read("app/api-key");
    }

    @Test
    public void write_returns_gives_no_error_if_write_204_returned() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(204);
        mockWebServer.enqueue(response);

        Map<String, String> data = new HashMap<>();
        data.put("key", "value");
        vaultClient.write("app/api-key", data);
    }

    @Test
    public void write_throws_vault_server_exception_if_response_is_not_204() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(403);
        response.setBody(getResponseJson("error"));
        mockWebServer.enqueue(response);

        try {
            Map<String, String> data = new HashMap<>();
            data.put("key", "value");
            vaultClient.write("app/not-allowed", data);
        } catch (VaultServerException se) {
            assertThat(se.getCode()).isEqualTo(403);
            assertThat(se.getErrors()).hasSize(2);
        }
    }

    @Test(expected = VaultClientException.class)
    public void write_throws_runtime_exception_if_unexpected_error_encountered() throws IOException {
        final ServerSocket serverSocket = new ServerSocket(0);
        final String vaultUrl = "http://localhost:" + serverSocket.getLocalPort();
        final VaultCredentialsProvider vaultCredentialsProvider = mock(VaultCredentialsProvider.class);
        final OkHttpClient httpClient = buildHttpClient(1, TimeUnit.SECONDS);
        vaultClient = new VaultClient(new StaticVaultUrlResolver(vaultUrl), vaultCredentialsProvider, httpClient);

        when(vaultCredentialsProvider.getCredentials()).thenReturn(new TestVaultCredentials());

        Map<String, String> data = new HashMap<>();
        data.put("key", "value");
        vaultClient.write("app/api-key", data);
    }

    @Test
    public void delete_returns_gives_no_error_if_write_204_returned() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(204);
        mockWebServer.enqueue(response);

        vaultClient.delete("app/api-key");
    }

    @Test
    public void delete_throws_vault_server_exception_if_response_is_not_204() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(403);
        response.setBody(getResponseJson("error"));
        mockWebServer.enqueue(response);

        try {
            vaultClient.delete("app/not-allowed");
        } catch (VaultServerException se) {
            assertThat(se.getCode()).isEqualTo(403);
            assertThat(se.getErrors()).hasSize(2);
        }
    }

    @Test(expected = VaultClientException.class)
    public void delete_throws_runtime_exception_if_unexpected_error_encountered() throws IOException {
        final ServerSocket serverSocket = new ServerSocket(0);
        final String vaultUrl = "http://localhost:" + serverSocket.getLocalPort();
        final VaultCredentialsProvider vaultCredentialsProvider = mock(VaultCredentialsProvider.class);
        final OkHttpClient httpClient = buildHttpClient(1, TimeUnit.SECONDS);
        vaultClient = new VaultClient(new StaticVaultUrlResolver(vaultUrl), vaultCredentialsProvider, httpClient);

        when(vaultCredentialsProvider.getCredentials()).thenReturn(new TestVaultCredentials());

        vaultClient.delete("app/api-key");
    }

    @Test
    public void lookup_self_returns_client_token_details() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.OK);
        response.setBody(getResponseJson("lookup-self"));
        mockWebServer.enqueue(response);

        final VaultClientTokenResponse actualResponse = vaultClient.lookupSelf();

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getId()).isEqualTo("ClientToken");
        assertThat(actualResponse.getPath()).isEqualTo("auth/token/create");
        assertThat(actualResponse.getMeta()).hasSize(2);
        assertThat(actualResponse.getPolicies()).contains("web", "stage");
        assertThat(actualResponse.getDisplayName()).isEqualTo("token-foo");
        assertThat(actualResponse.getNumUses()).isEqualTo(0);
    }

    @Test(expected = VaultServerException.class)
    public void lookup_self_throws_exception_if_not_found() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.NOT_FOUND);
        response.setBody(getResponseJson("error"));
        mockWebServer.enqueue(response);

        vaultClient.lookupSelf();
    }

    private OkHttpClient buildHttpClient(int timeout, TimeUnit timeoutUnit) {
        return new OkHttpClient.Builder()
                .connectTimeout(timeout, timeoutUnit)
                .writeTimeout(timeout, timeoutUnit)
                .readTimeout(timeout, timeoutUnit)
                .build();
    }

    private String getResponseJson(final String title) {
        InputStream inputStream = getClass().getResourceAsStream(
                String.format("/com/nike/vault/client/%s.json", title));
        try {
            return IOUtils.toString(inputStream, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private static class TestVaultCredentials implements VaultCredentials {
        @Override
        public String getToken() {
            return "TOKEN";
        }
    }
}