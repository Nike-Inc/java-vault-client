package com.nike.vault.client.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class VaultEncryptDataRequest {

  private String plaintext;
  private String context;
  private int keyVersion;

  /**
   * @return Base64 encoded plaintext to be encoded.
   */
  public String getPlaintext() {
    return plaintext;
  }

  public void setPlaintext(String plaintext) {
    this.plaintext = plaintext;
  }

  /**
   * @return Base64 encoded context for key derivation. This is required if key derivation is
   * enabled.
   */
  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  /**
   * @return Version of the key to use for encryption. If not set, uses the latest version.
   * Must be greater than or equal to the key's min_encryption_version, if set.
   */
  public int getKeyVersion() {
    return keyVersion;
  }

  public void setKeyVersion(int keyVersion) {
    this.keyVersion = keyVersion;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("plaintext", plaintext)
        .append("context", context)
        .append("keyVersion", keyVersion)
        .toString();
  }
}
