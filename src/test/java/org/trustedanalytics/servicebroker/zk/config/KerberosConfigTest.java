/**
 * Copyright (c) 2015 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trustedanalytics.servicebroker.zk.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.trustedanalytics.hadoop.config.ConfigurationHelper;
import org.trustedanalytics.hadoop.config.PropertyLocator;

import java.io.IOException;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KerberosConfigTest {

    private static final PropertyLocator USER_LOCATOR = PropertyLocator.USER;

    private KerberosConfig kerberosConfig;

    @Mock
    private ConfigurationHelper configurationHelper;

    @Before
    public void init() {
        kerberosConfig = new KerberosConfig();
        kerberosConfig.confHelper = configurationHelper;
    }

    @Test
    public void get_helperReturnsUser_returnUser() throws IOException {
        String EXPECTED_USER = "user";

        when(configurationHelper.getPropertyFromEnv(USER_LOCATOR))
            .thenReturn(Optional.of(EXPECTED_USER));

        String actualUser = kerberosConfig.get(USER_LOCATOR);

        assertThat(actualUser, equalTo(EXPECTED_USER));
    }

    @Test
    public void get_helperReturnsNull_returnDefault() throws IOException {
        String DEFAULT_USER = "";

        when(configurationHelper.getPropertyFromEnv(USER_LOCATOR))
            .thenReturn(Optional.ofNullable(null));

        String actualUser = kerberosConfig.get(USER_LOCATOR);

        assertThat(actualUser, equalTo(DEFAULT_USER));
    }

    @Test
    public void get_helperThrowsCheckedException_returnDefault() throws IOException {
        String DEFAULT_USER = "";

        when(configurationHelper.getPropertyFromEnv(USER_LOCATOR))
            .thenThrow(new IOException());

        String actualUser = kerberosConfig.get(USER_LOCATOR);

        assertThat(actualUser, equalTo(DEFAULT_USER));
    }

    @Test
    public void get_helperThrowsUncheckedException_returnDefault() throws IOException {
        String DEFAULT_USER = "";

        when(configurationHelper.getPropertyFromEnv(USER_LOCATOR))
            .thenThrow(new RuntimeException());

        String actualUser = kerberosConfig.get(USER_LOCATOR);

        assertThat(actualUser, equalTo(DEFAULT_USER));
    }
}
