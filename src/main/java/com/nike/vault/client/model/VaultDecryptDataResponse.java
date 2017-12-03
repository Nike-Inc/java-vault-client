package com.nike.vault.client.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class VaultDecryptDataResponse {
  private String plaintext;

  public String getPlaintext() {
    return plaintext;
  }

  public void setPlaintext(String plaintext) {
    this.plaintext = plaintext;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("plaintext", plaintext)
        .toString();
  }
}
