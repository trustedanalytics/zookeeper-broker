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
package org.trustedanalytics.servicebroker.zk.service;

import org.trustedanalytics.cfbroker.store.zookeeper.service.ZookeeperClient;
import org.trustedanalytics.cfbroker.store.zookeeper.service.ZookeeperClientBuilder;
import org.trustedanalytics.servicebroker.zk.config.ExternalConfiguration;
import org.trustedanalytics.servicebroker.zk.config.Qualifiers;
import org.trustedanalytics.servicebroker.zk.utils.ZookeeperUtils;

import org.apache.curator.test.TestingServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
@Profile("test")
public class ConfigurationTest {

  private final String USER = "cf";

  private final String PASSWORD = "cf1";

  @Autowired
  private ExternalConfiguration conf;

  @Bean
  public TestingServer initEmbededZKServer() throws Exception {
    TestingServer zkServer = new TestingServer();
    zkServer.start();

    //Preparing broker store space. It has to be done by provisioning mechanism.
    ZookeeperUtils.ZookeeperCredentials credendials =
        new ZookeeperUtils.ZookeeperCredentials(zkServer.getConnectString(),
                                                    USER,
                                                    PASSWORD);

    ZookeeperUtils.createDir(credendials, conf.getBrokerStoreNode());
    ZookeeperUtils.createDir(credendials, conf.getBrokerRootNode());

    return zkServer;
  }

  @Bean(initMethod = "init", destroyMethod = "destroy")
  @Qualifier(Qualifiers.BROKER_STORE)
  public ZookeeperClient getZkClientForBrokerStore(TestingServer zkServer) throws IOException {
    ZookeeperClient client =
        new ZookeeperClientBuilder(zkServer.getConnectString(),
                                   USER,
                                   PASSWORD,
                                   conf.getBrokerStoreNode()).build();
    return client;
  }

  @Bean(initMethod = "init", destroyMethod = "destroy")
  @Qualifier(Qualifiers.BROKER_INSTANCE)
  public ZookeeperClient getZkClientForBrokerInstance(TestingServer zkServer) throws IOException {
    ZookeeperClient client =
        new ZookeeperClientBuilder(zkServer.getConnectString(),
                                   USER,
                                   PASSWORD,
                                   conf.getBrokerRootNode()).build();
    return client;
  }

}
