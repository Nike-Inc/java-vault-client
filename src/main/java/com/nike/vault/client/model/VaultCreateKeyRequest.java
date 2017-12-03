package com.nike.vault.client.model;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class VaultCreateKeyRequest {

  /**
   * AES-256 wrapped with GCM using a 12-byte nonce size (symmetric, supports derivation)
   */
  public static final String TYPE_AES256_GCM96 = "aes256-gcm96";
  /**
   * ECDSA using the P-256 elliptic curve (asymmetric)
   */
  public static final String TYPE_ACDSA_P256 = "ecdsa-p256 ";
  /**
   * ED25519 (asymmetric, supports derivation)
   */
  public static final String TYPE_ED25519 = "ed25519 ";
  /**
   * RSA with bit size of 2048 (asymmetric)
   */
  public static final String TYPE_RSA_2048 = "rsa-2048 ";
  /**
   * RSA with bit size of 4096 (asymmetric)
   */
  public static final String TYPE_RSA_4096 = "rsa-4096 ";

  private boolean convergentEncryption;
  private boolean derived;
  private boolean exportable;
  private String type;

  /**
   * If enabled, the key will support convergent encryption, where the same plaintext creates the
   * same ciphertext. This requires derived to be set to true. When enabled, each
   * encryption(/decryption/rewrap/datakey) operation will derive a nonce value rather than randomly
   * generate it. Note that while this is useful for particular situations, all nonce values used
   * with a given context value must be unique or it will compromise the security of your key, and
   * the key space for nonces is 96 bit -- not as large as the AES key itself.
   *
   * @return convergent flag
   */
  public boolean isConvergentEncryption() {
    return convergentEncryption;
  }

  public void setConvergentEncryption(boolean convergentEncryption) {
    this.convergentEncryption = convergentEncryption;
  }

  /**
   * Specifies if key derivation is to be used. If enabled, all encrypt/decrypt requests to this
   * named key must provide a context which is used for key derivation.
   *
   * @return Derriviation flag
   */
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("convergentEncryption", convergentEncryption)
        .append("derived", derived)
        .append("exportable", exportable)
        .append("type", type)
        .toString();
  }
}
