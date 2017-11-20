package com.nike.vault.client.model;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class VaultAsymmetricKeyResponse extends VaultKeyResponse {
  public class AsymmetricKeyInformation {
      public class AsymmetricKey {
        private String creationTime;
        private String name;
        private String publicKey;

        public String getCreationTime() {
          return creationTime;
        }

        public void setCreationTime(String creationTime) {
          this.creationTime = creationTime;
        }

        public String getName() {
          return name;
        }

        public void setName(String name) {
          this.name = name;
        }

        public String getPublicKey() {
          return publicKey;
        }

        public void setPublicKey(String publicKey) {
          this.publicKey = publicKey;
        }

        @Override
        public String toString() {
          return new ToStringBuilder(this)
              .append("creationTime", creationTime)
              .append("name", name)
              .append("publicKey", publicKey)
              .toString();
        }
      }

    @SerializedName("1")
    private AsymmetricKey keyData;

    public AsymmetricKey getKeyData() {
      return keyData;
    }

    public void setKeyData(
        AsymmetricKey keyData) {
      this.keyData = keyData;
    }

    @Override
    public String toString() {
      return new ToStringBuilder(this)
          .append("keyData", keyData)
          .toString();
    }
  }
  private AsymmetricKeyInformation keys;

  public AsymmetricKeyInformation getKeys() {
    return keys;
  }

  public void setKeys(AsymmetricKeyInformation keys) {
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
