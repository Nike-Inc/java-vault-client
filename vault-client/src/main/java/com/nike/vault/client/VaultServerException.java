package com.nike.vault.client;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Represents a unexpected response from the Vault server.  Contains the HTTP response code and list of error messages.
 */
public class VaultServerException extends VaultClientException {

    private static final String MESSAGE_FORMAT = "Response Code: %s, Messages: %s";

    private final int code;

    private final List<String> errors;

    /**
     * Construction of the exception with the specified code and error message list.
     *
     * @param code   HTTP response code
     * @param errors List of error messages
     */
    public VaultServerException(final int code, final List<String> errors) {
        super(String.format(MESSAGE_FORMAT, code, StringUtils.join(errors, ", ")));
        this.code = code;
        this.errors = errors;
    }

    /**
     * Returns the HTTP response code
     *
     * @return HTTP response code
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns the list of error messages.
     *
     * @return Error messages
     */
    public List<String> getErrors() {
        return errors;
    }
}
