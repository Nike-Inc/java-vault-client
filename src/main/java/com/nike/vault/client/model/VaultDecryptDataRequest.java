package com.nike.vault.client.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class VaultDecryptDataRequest {

  private String ciphertext;
  private String context;

  /**
   * Specifies the ciphertext to decrypt.
   */
  public String getCiphertext() {
    return ciphertext;
  }

  public void setCiphertext(String ciphertext) {
    this.ciphertext = ciphertext;
  }

  /**
   * Specifies the base64 encoded context for key derivation. This is required if key derivation is
   * enabled.
   */
  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("ciphertext", ciphertext)
        .append("context", context)
        .toString();
  }
}
