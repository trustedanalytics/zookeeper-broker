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

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.trustedanalytics.servicebroker.framework.Credentials;
import org.trustedanalytics.servicebroker.framework.kerberos.KerberosProperties;

import com.google.common.collect.ImmutableMap;

@Configuration
public class ServiceInstanceBindingConfig {

  @Autowired
  private KerberosProperties kerberosProperties;

  @Autowired
  private ExternalConfiguration configuration;

  @Bean
  public Credentials getCredentials() throws IOException {
    ImmutableMap.Builder<String, Object> credentialsBuilder =
        new ImmutableMap.Builder<String, Object>().put("zk.cluster", configuration.getZkClusterHosts());

    if(kerberosProperties.isKerberosEnabled()){
      credentialsBuilder.put("kerberos", kerberosProperties.getCredentials());
    }

    return new Credentials(credentialsBuilder.build());
  }

  @Bean
  public String userspacePathTemplate() {
    return configuration.getBrokerRootNode();
  }
}
