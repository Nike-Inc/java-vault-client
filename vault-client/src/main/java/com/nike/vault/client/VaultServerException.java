/*
 * Copyright (c) 2016 Nike, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
