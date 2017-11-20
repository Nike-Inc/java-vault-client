package com.nike.vault.client.model;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class VaultSymmetricKeyResponse extends VaultKeyResponse {

  public class SymmetricKeyInformation {

    @SerializedName("1")
    private long keyData;

    public long getKeyData() {
      return keyData;
    }

    public void setKeyData(
        long keyData) {
      this.keyData = keyData;
    }

    @Override
    public String toString() {
      return new ToStringBuilder(this)
          .append("keyData", keyData)
          .toString();
    }
  }

  private SymmetricKeyInformation keys;

  public SymmetricKeyInformation getKeys() {
    return keys;
  }

  public void setKeys(SymmetricKeyInformation keys) {
    this.keys = keys;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("keys", keys)
        .append("deletionAllowed", deletionAllowed)
        .append("derived", derived)
        .append("exportable", exportable)
        .append("latestVersion", latestVersion)
        .append("minDecryptionVersion", minDecryptionVersion)
        .append("minEncryptionVersion", minEncryptionVersion)
        .append("name", name)
        .append("supportsDecryption", supportsDecryption)
        .append("supportsDerivation", supportsDerivation)
        .append("supportsEncryption", supportsEncryption)
        .append("supportsSigning", supportsSigning)
        .append("type", type)
        .toString();
  }


}
