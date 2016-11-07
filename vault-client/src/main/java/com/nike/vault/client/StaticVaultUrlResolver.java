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

/**
 * Wrapper for the URL resolver interface for a static URL.
 */
public class StaticVaultUrlResolver implements UrlResolver {

    private final String vaultUrl;

    /**
     * Explicit constructor for holding a static Vault URL.
     *
     * @param vaultUrl Vault URL
     */
    public StaticVaultUrlResolver(final String vaultUrl) {
        if (StringUtils.isBlank(vaultUrl)) {
            throw new IllegalArgumentException("Vault URL can not be blank.");
        }

        this.vaultUrl = vaultUrl;
    }

    /**
     * Returns a static Vault URL.
     *
     * @return Vault URL
     */
    @Override
    public String resolve() {
        return vaultUrl;
    }
}
