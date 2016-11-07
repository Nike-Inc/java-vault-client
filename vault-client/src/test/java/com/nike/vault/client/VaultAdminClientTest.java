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

import com.nike.vault.client.auth.VaultCredentials;
import com.nike.vault.client.auth.VaultCredentialsProvider;
import com.nike.vault.client.http.HttpMethod;
import com.nike.vault.client.http.HttpStatus;
import com.nike.vault.client.model.VaultAuthResponse;
import com.nike.vault.client.model.VaultClientTokenResponse;
import com.nike.vault.client.model.VaultEnableAuditBackendRequest;
import com.nike.vault.client.model.VaultHealthResponse;
import com.nike.vault.client.model.VaultInitResponse;
import com.nike.vault.client.model.VaultPolicy;
import com.nike.vault.client.model.VaultSealStatusResponse;
import com.nike.vault.client.model.VaultTokenAuthRequest;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the VaultAdminClientTest class
 */
public class VaultAdminClientTest {

    private VaultAdminClient vaultClient;

    private MockWebServer mockWebServer;

    @Before
    public void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        final String vaultUrl = "http://localhost:" + mockWebServer.getPort();
        final VaultCredentialsProvider vaultCredentialsProvider = mock(VaultCredentialsProvider.class);
        vaultClient = VaultClientFactory.getAdminClient(new StaticVaultUrlResolver(vaultUrl),
                vaultCredentialsProvider);

        when(vaultCredentialsProvider.getCredentials()).thenReturn(new TestVaultCredentials());
    }

    @After
    public void teardown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void init_returns_data_on_successful_call() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.OK);
        response.setBody(getResponseJson("init"));
        mockWebServer.enqueue(response);

        final VaultInitResponse actualResponse = vaultClient.init(3, 5);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getRootToken()).isEqualTo("foo");
        assertThat(actualResponse.getKeys()).contains("one", "two", "three");
    }

    @Test(expected = VaultServerException.class)
    public void init_returns_bad_request_if_already_initialized() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.BAD_REQUEST);
        response.setBody(getResponseJson("error"));
        mockWebServer.enqueue(response);

        vaultClient.init(3, 5);
    }

    @Test
    public void health_returns_ok_if_healthy_leader() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.OK);
        response.setBody(getResponseJson("health-unsealed"));
        mockWebServer.enqueue(response);

        final VaultHealthResponse actualResponse = vaultClient.health();

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.isInitialized()).isTrue();
        assertThat(actualResponse.isSealed()).isFalse();
        assertThat(actualResponse.isStandby()).isFalse();
    }

    @Test
    public void health_returns_429_if_healthy_standby() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.TOO_MANY_REQUESTS);
        response.setBody(getResponseJson("health-standby"));
        mockWebServer.enqueue(response);

        final VaultHealthResponse actualResponse = vaultClient.health();

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.isInitialized()).isTrue();
        assertThat(actualResponse.isSealed()).isFalse();
        assertThat(actualResponse.isStandby()).isTrue();
    }

    @Test
    public void health_returns_500_if_sealed() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR);
        response.setBody(getResponseJson("health-sealed"));
        mockWebServer.enqueue(response);

        final VaultHealthResponse actualResponse = vaultClient.health();

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.isInitialized()).isTrue();
        assertThat(actualResponse.isSealed()).isTrue();
        assertThat(actualResponse.isStandby()).isTrue();
    }

    @Test(expected = VaultServerException.class)
    public void health_throws_server_exception_if_unexpected_response() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.BAD_GATEWAY);
        mockWebServer.enqueue(response);

        vaultClient.health();
    }

    @Test
    public void unseal_returns_ok_if_successful() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.OK);
        response.setBody(getResponseJson("seal-status"));
        mockWebServer.enqueue(response);

        final VaultSealStatusResponse actualResponse = vaultClient.unseal("KEY", false);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.isSealed()).isTrue();
        assertThat(actualResponse.getN()).isEqualTo(5);
        assertThat(actualResponse.getProgress()).isEqualTo(2);
        assertThat(actualResponse.getT()).isEqualTo(3);
    }

    @Test(expected = VaultServerException.class)
    public void unseal_throws_server_exception_if_already_unsealed() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.BAD_REQUEST);
        response.setBody(getResponseJson("error"));
        mockWebServer.enqueue(response);

        vaultClient.unseal("KEY", false);
    }

    @Test
    public void policies_returns_set_of_policy_names() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.OK);
        response.setBody(getResponseJson("policies"));
        mockWebServer.enqueue(response);

        final Set<String> actualPolicies = vaultClient.policies();

        assertThat(actualPolicies).hasSize(2);
        assertThat(actualPolicies).contains("root", "deploy");
    }

    @Test(expected = VaultServerException.class)
    public void policies_throws_exception_with_unexpected_response_code() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.UNAUTHORIZED);
        response.setBody(getResponseJson("error"));
        mockWebServer.enqueue(response);

        vaultClient.policies();
    }

    @Test
    public void policy_returns_policy_details_if_exists() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.OK);
        response.setBody(getResponseJson("policy"));
        mockWebServer.enqueue(response);

        final VaultPolicy actualPolicy = vaultClient.policy("vault");

        assertThat(actualPolicy).isNotNull();
        assertThat(actualPolicy.getRules()).isEqualTo("key \"vault\" {policy = \"write\"}");
    }

    @Test(expected = VaultServerException.class)
    public void policy_throws_exception_if_not_found() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.NOT_FOUND);
        response.setBody(getResponseJson("error"));
        mockWebServer.enqueue(response);

        vaultClient.policy("rawr");
    }

    @Test
    public void put_policy_returns_204_when_successful() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.NO_CONTENT);
        mockWebServer.enqueue(response);

        vaultClient.putPolicy("rawr", new VaultPolicy().setRules("rules"));

        // Silence is success!
    }

    @Test(expected = VaultServerException.class)
    public void put_policy_throws_exception_if_unauthorized() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.UNAUTHORIZED);
        response.setBody(getResponseJson("error"));
        mockWebServer.enqueue(response);

        vaultClient.putPolicy("rawr", new VaultPolicy().setRules("rules"));
    }

    @Test
    public void delete_policy_returns_204_when_successful() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.NO_CONTENT);
        mockWebServer.enqueue(response);

        vaultClient.deletePolicy("rawr");

        // Silence is success!
    }

    @Test(expected = VaultServerException.class)
    public void delete_policy_throws_exception_if_unauthorized() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.UNAUTHORIZED);
        response.setBody(getResponseJson("error"));
        mockWebServer.enqueue(response);

        vaultClient.deletePolicy("rawr");
    }

    @Test
    public void create_token_returns_ok_if_created() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.OK);
        response.setBody(getResponseJson("auth"));
        mockWebServer.enqueue(response);

        final VaultTokenAuthRequest request = new VaultTokenAuthRequest()
                .setDisplayName("RAWR")
                .setId("ABCD")
                .setNoDefaultPolicy(false)
                .setNoParent(false)
                .setNumUses(0)
                .setTtl("1h");

        VaultAuthResponse actualResponse = vaultClient.createToken(request);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getClientToken()).isEqualTo("ABCD");
        assertThat(actualResponse.getLeaseDuration()).isEqualTo(3600);
        assertThat(actualResponse.isRenewable()).isTrue();
        assertThat(actualResponse.getPolicies()).hasSize(2);
        assertThat(actualResponse.getMetadata()).hasSize(1);
    }

    @Test(expected = VaultServerException.class)
    public void create_token_throws_exception_if_unauthorized() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.UNAUTHORIZED);
        response.setBody(getResponseJson("error"));
        mockWebServer.enqueue(response);

        final VaultTokenAuthRequest request = new VaultTokenAuthRequest()
                .setDisplayName("RAWR")
                .setId("ID")
                .setNoDefaultPolicy(false)
                .setNoParent(false)
                .setNumUses(0)
                .setTtl("1h");

        vaultClient.createToken(request);
    }

    @Test
    public void create_orphan_token_returns_ok_if_created() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.OK);
        response.setBody(getResponseJson("auth"));
        mockWebServer.enqueue(response);

        final VaultTokenAuthRequest request = new VaultTokenAuthRequest()
                .setDisplayName("RAWR")
                .setId("ABCD")
                .setNoDefaultPolicy(false)
                .setNoParent(false)
                .setNumUses(0)
                .setTtl("1h");

        VaultAuthResponse actualResponse = vaultClient.createOrphanToken(request);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getClientToken()).isEqualTo("ABCD");
        assertThat(actualResponse.getLeaseDuration()).isEqualTo(3600);
        assertThat(actualResponse.isRenewable()).isTrue();
        assertThat(actualResponse.getPolicies()).hasSize(2);
        assertThat(actualResponse.getMetadata()).hasSize(1);
    }

    @Test(expected = VaultServerException.class)
    public void create_orphan_token_throws_exception_if_unauthorized() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.UNAUTHORIZED);
        response.setBody(getResponseJson("error"));
        mockWebServer.enqueue(response);

        final VaultTokenAuthRequest request = new VaultTokenAuthRequest()
                .setDisplayName("RAWR")
                .setId("ABCD")
                .setNoDefaultPolicy(false)
                .setNoParent(false)
                .setNumUses(0)
                .setTtl("1h");

        vaultClient.createOrphanToken(request);
    }

    @Test
    public void revoke_token_returns_nothing_if_successful() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.NO_CONTENT);
        mockWebServer.enqueue(response);

        vaultClient.revokeToken("TOKEN");

        // Silence is success!
    }

    @Test(expected = VaultServerException.class)
    public void revoke_token_throws_exception_if_unauthorized() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.UNAUTHORIZED);
        response.setBody(getResponseJson("error"));
        mockWebServer.enqueue(response);

        vaultClient.revokeToken("TOKEN");
    }

    @Test
    public void revoke_orphan_token_returns_nothing_if_successful() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.NO_CONTENT);
        mockWebServer.enqueue(response);

        vaultClient.revokeOrphanToken("TOKEN");

        // Silence is success!
    }

    @Test(expected = VaultServerException.class)
    public void revoke_orphan_token_throws_exception_if_unauthorized() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.UNAUTHORIZED);
        response.setBody(getResponseJson("error"));
        mockWebServer.enqueue(response);

        vaultClient.revokeOrphanToken("TOKEN");
    }

    @Test
    public void lookup_token_returns_client_token_details() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.OK);
        response.setBody(getResponseJson("lookup-self"));
        mockWebServer.enqueue(response);

        final VaultClientTokenResponse actualResponse = vaultClient.lookupToken("ClientToken");

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getId()).isEqualTo("ClientToken");
        assertThat(actualResponse.getPath()).isEqualTo("auth/token/create");
        assertThat(actualResponse.getMeta()).hasSize(2);
        assertThat(actualResponse.getPolicies()).contains("web", "stage");
        assertThat(actualResponse.getDisplayName()).isEqualTo("token-foo");
        assertThat(actualResponse.getNumUses()).isEqualTo(0);
    }

    @Test(expected = VaultServerException.class)
    public void lookup_token_throws_exception_if_not_found() {
        final MockResponse response = new MockResponse();
        response.setResponseCode(HttpStatus.NOT_FOUND);
        response.setBody(getResponseJson("error"));
        mockWebServer.enqueue(response);

        vaultClient.lookupToken("ClientToken");
    }

    @Test
    public void enable_audit_backend_works() {
        final MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(HttpStatus.NO_CONTENT);
        mockWebServer.enqueue(mockResponse);

        final VaultEnableAuditBackendRequest request = new VaultEnableAuditBackendRequest();
        request.setType("file").setDescription("File audit backend").setOptions(new HashMap<String, String>());
        vaultClient.enableAuditBackend("file", request);

        // 204 is expected to be returned, we should just succeed.
    }

    @Test(expected = VaultServerException.class)
    public void enable_audit_backend_throws_error_if_no_204() {
        final MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(HttpStatus.BAD_REQUEST);
        mockWebServer.enqueue(mockResponse);

        final VaultEnableAuditBackendRequest request = new VaultEnableAuditBackendRequest();
        vaultClient.enableAuditBackend("file", request);
    }

    @Test
    public void disable_audit_backend_works() {
        final MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(HttpStatus.NO_CONTENT);
        mockWebServer.enqueue(mockResponse);

        vaultClient.disableAuditBackend("file");

        // 204 is expected to be returned, we should just succeed.
    }

    @Test(expected = VaultServerException.class)
    public void disable_audit_backend_throws_error_if_no_204() {
        final MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(HttpStatus.BAD_REQUEST);
        mockWebServer.enqueue(mockResponse);

        vaultClient.disableAuditBackend("file");
    }

    @Test
    public void execute_executes_the_http_request() {
        final MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(HttpStatus.UNAUTHORIZED);
        mockResponse.setBody(getResponseJson("error"));
        mockWebServer.enqueue(mockResponse);

        final Response response = vaultClient.execute("foo/bar", HttpMethod.PUT, "{}");

        assertThat(response.code()).isEqualTo(HttpStatus.UNAUTHORIZED);
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