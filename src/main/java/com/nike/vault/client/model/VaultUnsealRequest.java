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

package com.nike.vault.client.model;

/**
 * Represents an unseal request
 */
public class VaultUnsealRequest {

    private final String key;

    private final boolean reset;

    /**
     * Either the key or reset parameter must be provided; if both are provided, reset takes precedence.
     *
     * @param key   a single master share key
     * @param reset if true, the previously-provided unseal keys are discarded from memory and the unseal
     *              process is reset
     */
    public VaultUnsealRequest(String key, boolean reset) {
        this.key = key;
        this.reset = reset;
    }

    public String getKey() {
        return key;
    }

    public boolean isReset() {
        return reset;
    }
}
