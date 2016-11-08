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

import java.util.Map;
import java.util.Set;

/**
 * Represents a token authentication request to Vault
 */
public class VaultTokenAuthRequest {

    private String id;

    private Set<String> policies;

    private Map<String, String> meta;

    private boolean noParent;

    private boolean noDefaultPolicy;

    private String ttl;

    private String displayName;

    private int numUses;

    public String getId() {
        return id;
    }

    public VaultTokenAuthRequest setId(String id) {
        this.id = id;
        return this;
    }

    public Set<String> getPolicies() {
        return policies;
    }

    public VaultTokenAuthRequest setPolicies(Set<String> policies) {
        this.policies = policies;
        return this;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public VaultTokenAuthRequest setMeta(Map<String, String> meta) {
        this.meta = meta;
        return this;
    }

    public boolean isNoParent() {
        return noParent;
    }

    public VaultTokenAuthRequest setNoParent(boolean noParent) {
        this.noParent = noParent;
        return this;
    }

    public boolean isNoDefaultPolicy() {
        return noDefaultPolicy;
    }

    public VaultTokenAuthRequest setNoDefaultPolicy(boolean noDefaultPolicy) {
        this.noDefaultPolicy = noDefaultPolicy;
        return this;
    }

    public String getTtl() {
        return ttl;
    }

    public VaultTokenAuthRequest setTtl(String ttl) {
        this.ttl = ttl;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public VaultTokenAuthRequest setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public int getNumUses() {
        return numUses;
    }

    public VaultTokenAuthRequest setNumUses(int numUses) {
        this.numUses = numUses;
        return this;
    }
}
