package com.nike.vault.client.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class VaultEncryptDataResponse {
  private String ciphertext;

  public String getCiphertext() {
    return ciphertext;
  }

  public void setCiphertext(String ciphertext) {
    this.ciphertext = ciphertext;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("ciphertext", ciphertext)
        .toString();
  }
}
