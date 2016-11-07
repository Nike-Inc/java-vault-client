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

/**
 * Represents the request object for enabling an audit backend.
 */
public class VaultEnableAuditBackendRequest {

    private String type;

    private String description;

    private Map<String, String> options;

    public String getType() {
        return type;
    }

    public VaultEnableAuditBackendRequest setType(String type) {
        this.type = type;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public VaultEnableAuditBackendRequest setDescription(String description) {
        this.description = description;
        return this;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public VaultEnableAuditBackendRequest setOptions(Map<String, String> options) {
        this.options = options;
        return this;
    }
}
