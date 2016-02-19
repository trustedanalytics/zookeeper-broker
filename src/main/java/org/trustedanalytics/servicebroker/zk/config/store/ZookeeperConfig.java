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
package org.trustedanalytics.servicebroker.zk.config.store;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.trustedanalytics.cfbroker.store.zookeeper.service.ZookeeperClient;
import org.trustedanalytics.cfbroker.store.zookeeper.service.ZookeeperClientBuilder;
import org.trustedanalytics.hadoop.kerberos.KrbLoginManager;
import org.trustedanalytics.hadoop.kerberos.KrbLoginManagerFactory;
import org.trustedanalytics.servicebroker.zk.config.ExternalConfiguration;
import org.trustedanalytics.servicebroker.zk.config.Profiles;
import org.trustedanalytics.servicebroker.zk.config.Qualifiers;
import org.trustedanalytics.servicebroker.zk.config.kerberos.KerberosProperties;

@Profile(Profiles.CLOUD)
@Configuration
public class ZookeeperConfig {

  private final static Logger LOGGER = LoggerFactory.getLogger(ZookeeperConfig.class);

  @Autowired
  private ExternalConfiguration config;

  @Autowired
  private KerberosProperties kerberosProperties;

  @Bean(initMethod = "init", destroyMethod = "destroy")
  @Profile(Profiles.CLOUD)
  @Qualifier(Qualifiers.BROKER_INSTANCE)
  public ZookeeperClient getZkClientForBrokerInstance(KerberosProperties kerberosProperties)
      throws IOException, LoginException {
    return getZKClient(config.getBrokerRootNode());
  }

  @Bean(initMethod = "init", destroyMethod = "destroy")
  @Profile(Profiles.CLOUD)
  @Qualifier(Qualifiers.BROKER_STORE)
  public ZookeeperClient getZkClientForBrokerStore(KerberosProperties kerberosProperties)
      throws IOException, LoginException {
    return getZKClient(config.getBrokerStoreNode());
  }

  private ZookeeperClient getZKClient(String brokerNode) throws IOException, LoginException {
    if (kerberosProperties.isValid()) {
      LOGGER.info("Found kerberos configuration - trying to authenticate");
      KrbLoginManager loginManager =
          KrbLoginManagerFactory.getInstance().getKrbLoginManagerInstance(
              kerberosProperties.getKdc(), kerberosProperties.getRealm());
      loginManager.loginWithCredentials(kerberosProperties.getUser(), kerberosProperties
          .getPassword().toCharArray());
    } else {
      LOGGER.warn("kerberos configuration empty or invalid - will not try to authenticate.");
    }

    return new ZookeeperClientBuilder(config.getZkClusterHosts(), kerberosProperties.getUser(),
        kerberosProperties.getPassword(), brokerNode).build();
  }
}
