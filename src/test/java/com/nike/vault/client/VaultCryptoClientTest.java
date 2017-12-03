package com.nike.vault.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nike.vault.client.auth.VaultCredentials;
import com.nike.vault.client.auth.VaultCredentialsProvider;
import com.nike.vault.client.http.HttpStatus;
import com.nike.vault.client.model.VaultAsymmetricKeyResponse;
import com.nike.vault.client.model.VaultCreateKeyRequest;
import com.nike.vault.client.model.VaultDecryptDataRequest;
import com.nike.vault.client.model.VaultDecryptDataResponse;
import com.nike.vault.client.model.VaultEncryptDataRequest;
import com.nike.vault.client.model.VaultEncryptDataResponse;
import com.nike.vault.client.model.VaultKeyResponse;
import com.nike.vault.client.model.VaultSymmetricKeyResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VaultCryptoClientTest {

  private VaultCryptoClient vaultClient;

  private MockWebServer mockWebServer;

  @Before
  public void setup() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();
    final String vaultUrl = "http://localhost:" + mockWebServer.getPort();
    final VaultCredentialsProvider vaultCredentialsProvider = mock(VaultCredentialsProvider.class);
    vaultClient = VaultClientFactory.getCryptoClient(new StaticVaultUrlResolver(vaultUrl),
        vaultCredentialsProvider);

    when(vaultCredentialsProvider.getCredentials()).thenReturn(new TestVaultCredentials());
  }

  @After
  public void teardown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  public void create_key_returns_204_when_successful() {
    final MockResponse response = new MockResponse();
    response.setResponseCode(HttpStatus.NO_CONTENT);
    mockWebServer.enqueue(response);

    final VaultCreateKeyRequest createKeyRequest = new VaultCreateKeyRequest();
    createKeyRequest.setConvergentEncryption(true);
    createKeyRequest.setDerived(true);
    createKeyRequest.setExportable(true);
    createKeyRequest.setType(VaultCreateKeyRequest.TYPE_ACDSA_P256);

    vaultClient.createKey("test-key", createKeyRequest);

    // Silence is success!
  }

  @Test
  public void query_key_information_symmetric_returns_ok_if_created() {
    final MockResponse response = new MockResponse();
    response.setResponseCode(HttpStatus.OK);
    response.setBody(getResponseJson("symmetric_key"));
    mockWebServer.enqueue(response);

    VaultKeyResponse actualResponse = vaultClient.getKeyInfo("test-key");

    assertThat(actualResponse).isNotNull();
    assertThat(actualResponse instanceof VaultSymmetricKeyResponse);
    VaultSymmetricKeyResponse symmetricResponse = (VaultSymmetricKeyResponse) actualResponse;
    assertThat(symmetricResponse.getKeys().getKeyData()).isEqualTo(1511100122l);
    assertThat(symmetricResponse.getMinDecryptionVersion() == 1).isTrue();
    assertThat(symmetricResponse.isSupportsSigning()).isFalse();
  }

  @Test
  public void query_key_information_asymmetric_returns_ok_if_created() {
    final MockResponse response = new MockResponse();
    response.setResponseCode(HttpStatus.OK);
    response.setBody(getResponseJson("asymmetric_key"));
    mockWebServer.enqueue(response);

    VaultKeyResponse actualResponse = vaultClient.getKeyInfo("test-key");

    assertThat(actualResponse).isNotNull();
    assertThat(actualResponse instanceof VaultAsymmetricKeyResponse);
    VaultAsymmetricKeyResponse asymmetricResponse = (VaultAsymmetricKeyResponse) actualResponse;
    assertThat(asymmetricResponse.getKeys().getKeyData().getName().equalsIgnoreCase("rsa-4096"));
    assertThat(asymmetricResponse.getKeys().getKeyData().getCreationTime()
        .equalsIgnoreCase("2017-11-19T20:20:15.854167+08:00"));
    assertThat(asymmetricResponse.getMinDecryptionVersion() == 1).isTrue();
    assertThat(asymmetricResponse.isSupportsSigning()).isTrue();
  }

  @Test
  public void encrypt_returns_ok_if_created() {
    final MockResponse response = new MockResponse();
    response.setResponseCode(HttpStatus.OK);
    response.setBody(getResponseJson("encrypt"));
    mockWebServer.enqueue(response);

    VaultEncryptDataRequest request = new VaultEncryptDataRequest();
    request.setPlaintext("QWxsIHdlIGFyZSBidXQgc3BlY2tzIGluIGEgaHVnZSB1bml2ZXJzZSAuLi4=");

    VaultEncryptDataResponse actualResponse = vaultClient.encrypt("test-key", request);

    assertThat(actualResponse).isNotNull();
    assertThat(actualResponse.getCiphertext()).isEqualTo("vault:v1:ZqkozlOaopGR4jf+/OhVS1iHyczM19Ax6ku1VJq4il7X7Wrqqc21dqKN8MTiOV4Iku8784X12NvOIfQcSvIWV2mW44q0HMlG");
  }

  @Test
  public void decrypt_returns_ok_if_created() {
    final MockResponse response = new MockResponse();
    response.setResponseCode(HttpStatus.OK);
    response.setBody(getResponseJson("decrypt"));
    mockWebServer.enqueue(response);

    VaultDecryptDataRequest request = new VaultDecryptDataRequest();
    request.setCiphertext("vault:v1:ZqkozlOaopGR4jf+/OhVS1iHyczM19Ax6ku1VJq4il7X7Wrqqc21dqKN8MTiOV4Iku8784X12NvOIfQcSvIWV2mW44q0HMlG");

    VaultDecryptDataResponse actualResponse = vaultClient.decrypt("test-key", request);

    assertThat(actualResponse).isNotNull();
    assertThat(actualResponse.getPlaintext()).isEqualTo("QWxsIHdlIGFyZSBidXQgc3BlY2tzIGluIGEgaHVnZSB1bml2ZXJzZSAuLi4=");
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
