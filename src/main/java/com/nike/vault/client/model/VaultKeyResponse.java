package com.nike.vault.client.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class VaultKeyResponse {
  protected boolean deletionAllowed;
  protected boolean derived;
  protected boolean exportable;
  protected int latestVersion;
  protected int minDecryptionVersion;
  protected int minEncryptionVersion;
  protected String name;
  protected boolean supportsDecryption;
  protected boolean supportsDerivation;
  protected boolean supportsEncryption;
  protected boolean supportsSigning;
  protected String type;

  public boolean isDeletionAllowed() {
    return deletionAllowed;
  }

  public void setDeletionAllowed(boolean deletionAllowed) {
    this.deletionAllowed = deletionAllowed;
  }

  public boolean isDerived() {
    return derived;
  }

  public void setDerived(boolean derived) {
    this.derived = derived;
  }

  public boolean isExportable() {
    return exportable;
  }

  public void setExportable(boolean exportable) {
    this.exportable = exportable;
  }

  public int getLatestVersion() {
    return latestVersion;
  }

  public void setLatestVersion(int latestVersion) {
    this.latestVersion = latestVersion;
  }

  public int getMinDecryptionVersion() {
    return minDecryptionVersion;
  }

  public void setMinDecryptionVersion(int minDecryptionVersion) {
    this.minDecryptionVersion = minDecryptionVersion;
  }

  public int getMinEncryptionVersion() {
    return minEncryptionVersion;
  }

  public void setMinEncryptionVersion(int minEncryptionVersion) {
    this.minEncryptionVersion = minEncryptionVersion;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isSupportsDecryption() {
    return supportsDecryption;
  }

  public void setSupportsDecryption(boolean supportsDecryption) {
    this.supportsDecryption = supportsDecryption;
  }

  public boolean isSupportsDerivation() {
    return supportsDerivation;
  }

  public void setSupportsDerivation(boolean supportsDerivation) {
    this.supportsDerivation = supportsDerivation;
  }

  public boolean isSupportsEncryption() {
    return supportsEncryption;
  }

  public void setSupportsEncryption(boolean supportsEncryption) {
    this.supportsEncryption = supportsEncryption;
  }

  public boolean isSupportsSigning() {
    return supportsSigning;
  }

  public void setSupportsSigning(boolean supportsSigning) {
    this.supportsSigning = supportsSigning;
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
