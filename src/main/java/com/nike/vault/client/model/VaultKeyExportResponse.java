package com.nike.vault.client.model;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by gutzeit on 28/12/2017. All rights reserved to LeapXpert.
 */
public class VaultKeyExportResponse {
  public class KeyInformation {

    @SerializedName("1")
    private String keyData;

    public String getKeyData() {
      return keyData;
    }

    public void setKeyData(
        String keyData) {
      this.keyData = keyData;
    }

    @Override
    public String toString() {
      return new ToStringBuilder(this)
          .append("keyData", keyData)
          .toString();
    }
  }

  private KeyInformation keys;
  private String name;
  private String type;

  public KeyInformation getKeys() {
    return keys;
  }

  public void setKeys(KeyInformation keys) {
    this.keys = keys;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("keys", keys)
        .append("name", name)
        .append("type", type)
        .toString();
  }
}
