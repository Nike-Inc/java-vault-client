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
 * Represents a seal status response from Vault
 */
public class VaultSealStatusResponse {

    private boolean sealed;

    private int t;

    private int n;

    private int progress;

    public boolean isSealed() {
        return sealed;
    }

    public VaultSealStatusResponse setSealed(boolean sealed) {
        this.sealed = sealed;
        return this;
    }

    public int getT() {
        return t;
    }

    public VaultSealStatusResponse setT(int t) {
        this.t = t;
        return this;
    }

    public int getN() {
        return n;
    }

    public VaultSealStatusResponse setN(int n) {
        this.n = n;
        return this;
    }

    public int getProgress() {
        return progress;
    }

    public VaultSealStatusResponse setProgress(int progress) {
        this.progress = progress;
        return this;
    }
}
