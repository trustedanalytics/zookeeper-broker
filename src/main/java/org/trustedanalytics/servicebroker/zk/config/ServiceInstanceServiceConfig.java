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

import javax.security.auth.login.LoginException;

import org.cloudfoundry.community.servicebroker.model.Catalog;
import org.cloudfoundry.community.servicebroker.model.ServiceDefinition;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.trustedanalytics.cfbroker.store.api.BrokerStore;
import org.trustedanalytics.cfbroker.store.impl.ServiceInstanceServiceStore;
import org.trustedanalytics.cfbroker.store.zookeeper.service.ZookeeperClient;
import org.trustedanalytics.servicebroker.zk.config.catalog.BrokerPlans;
import org.trustedanalytics.servicebroker.zk.service.ZKServiceInstanceService;

@Configuration
public class ServiceInstanceServiceConfig {

  @Autowired
  private BrokerPlans brokerPlans;

  @Autowired
  @Qualifier(value = Qualifiers.BROKER_INSTANCE)
  private ZookeeperClient zkClient;

  @Autowired
  @Qualifier(value = Qualifiers.SERVICE_INSTANCE)
  private BrokerStore<ServiceInstance> store;

  @Bean
  public ServiceInstanceService getServiceInstanceService() throws IllegalArgumentException,
      IOException, LoginException {
    return new ZKServiceInstanceService(new ServiceInstanceServiceStore(store), brokerPlans, zkClient);
  }
}
