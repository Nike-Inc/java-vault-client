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

package com.nike.vault.client.auth;

import com.nike.vault.client.VaultClientException;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the VaultCredentialsProviderChain class
 */
public class VaultCredentialsProviderChainTest {

    private static final String TOKEN = "TOKEN";

    private VaultCredentialsProvider credentialsProviderOne;

    private VaultCredentialsProvider credentialsProviderTwo;

    private VaultCredentialsProviderChain credentialsProviderChain;

    @Before
    public void setup() {
        credentialsProviderOne = mock(VaultCredentialsProvider.class);
        credentialsProviderTwo = mock(VaultCredentialsProvider.class);
        credentialsProviderChain = new VaultCredentialsProviderChain(credentialsProviderOne, credentialsProviderTwo);
    }

    @Test
    public void getCredentials_returns_credentials_from_first_successful_provider() {
        when(credentialsProviderOne.getCredentials()).thenReturn(new TestVaultCredentials());

        VaultCredentials credentials = credentialsProviderChain.getCredentials();

        assertThat(credentials).isNotNull();
    }

    @Test(expected = VaultClientException.class)
    public void getCredentials_throws_client_exception_if_all_providers_fail() {
        when(credentialsProviderOne.getCredentials()).thenThrow(new VaultClientException(""));
        when(credentialsProviderTwo.getCredentials()).thenThrow(new VaultClientException(""));

        credentialsProviderChain.getCredentials();
    }

    @Test
    public void getCredentials_uses_last_successful_provider() {
        when(credentialsProviderOne.getCredentials()).thenThrow(new VaultClientException(""));
        when(credentialsProviderTwo.getCredentials()).thenReturn(new TestVaultCredentials());

        VaultCredentials credentials = credentialsProviderChain.getCredentials();

        assertThat(credentials).isNotNull();

        VaultCredentials credentialsAgain = credentialsProviderChain.getCredentials();

        verify(credentialsProviderOne, times(1)).getCredentials();
        verify(credentialsProviderTwo, times(2)).getCredentials();

        assertThat(credentials.getToken()).isEqualTo(credentialsAgain.getToken());
    }

    @Test
    public void getCredentials_with_reuse_last_provider_disabled_attempts_chain_of_providers() {
        when(credentialsProviderOne.getCredentials()).thenThrow(new VaultClientException(""));
        when(credentialsProviderTwo.getCredentials()).thenReturn(new TestVaultCredentials());

        VaultCredentials credentials = credentialsProviderChain.getCredentials();

        assertThat(credentials).isNotNull();

        credentialsProviderChain.setReuseLastProvider(false);

        VaultCredentials credentialsAgain = credentialsProviderChain.getCredentials();

        verify(credentialsProviderOne, times(2)).getCredentials();
        verify(credentialsProviderTwo, times(2)).getCredentials();

        assertThat(credentials.getToken()).isEqualTo(credentialsAgain.getToken());
    }

    @Test
    public void getCredentials_attempts_full_chain_even_if_one_throws_exception() {
        when(credentialsProviderOne.getCredentials()).thenThrow(new RuntimeException());
        when(credentialsProviderTwo.getCredentials()).thenReturn(new TestVaultCredentials());

        VaultCredentials credentials = credentialsProviderChain.getCredentials();

        assertThat(credentials).isNotNull();
        assertThat(credentials.getToken()).isEqualTo(TOKEN);
    }

    @Test
    public void isReuseLastProvider_returns_if_reuse_last_provider_is_enabled() {
        assertThat(credentialsProviderChain.isReuseLastProvider()).isTrue();
        credentialsProviderChain.setReuseLastProvider(false);
        assertThat(credentialsProviderChain.isReuseLastProvider()).isFalse();
        credentialsProviderChain.setReuseLastProvider(true);
        assertThat(credentialsProviderChain.isReuseLastProvider()).isTrue();
    }

    @Test
    public void list_contstructor_set_provider_list() {
        List<VaultCredentialsProvider> list = new LinkedList<>();
        list.add(credentialsProviderOne);
        list.add(credentialsProviderTwo);

        VaultCredentialsProviderChain chain = new VaultCredentialsProviderChain(list);

        when(credentialsProviderOne.getCredentials()).thenThrow(new VaultClientException(""));
        when(credentialsProviderTwo.getCredentials()).thenReturn(new TestVaultCredentials());

        VaultCredentials credentials = chain.getCredentials();

        assertThat(credentials).isNotNull();
        assertThat(credentials.getToken()).isEqualTo(TOKEN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void new_chain_without_providers_throws_exception() {
        new VaultCredentialsProviderChain();
    }

    @Test(expected = IllegalArgumentException.class)
    public void new_chain_with_empty_providers_throws_exception() {
        new VaultCredentialsProviderChain(new VaultCredentialsProviderChain[]{});
    }

    @Test(expected = IllegalArgumentException.class)
    public void new_chain_with_null_list_of_providers_throws_exception() {
        List<VaultCredentialsProvider> list = null;
        new VaultCredentialsProviderChain(list);
    }

    @Test(expected = IllegalArgumentException.class)
    public void new_chain_with_empty_list_of_providers_throws_exception() {
        List<VaultCredentialsProvider> list = Collections.emptyList();
        new VaultCredentialsProviderChain(list);
    }

    private static class TestVaultCredentials implements VaultCredentials {
        @Override
        public String getToken() {
            return TOKEN;
        }
    }
}