package com.nike.vault.client;

/**
 * Represents a unexpected error from the Vault client.
 */
public class VaultClientException extends RuntimeException {

    /**
     * Constructs the exception with a message and underlying exception.
     *
     * @param message Message
     * @param t       Underlying exception
     */
    public VaultClientException(String message, Throwable t) {
        super(message, t);
    }

    /**
     * Constructs the exception with a message.
     *
     * @param message Message
     */
    public VaultClientException(String message) {
        super(message);
    }
}
