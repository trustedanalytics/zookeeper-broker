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

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.cloudfoundry.community.servicebroker.model.Plan;
import org.cloudfoundry.community.servicebroker.model.ServiceDefinition;
import org.trustedanalytics.cfbroker.store.impl.ForwardingServiceInstanceServiceStore;
import org.trustedanalytics.cfbroker.store.zookeeper.service.ZookeeperClient;

import org.cloudfoundry.community.servicebroker.exception.ServiceBrokerException;
import org.cloudfoundry.community.servicebroker.exception.ServiceInstanceExistsException;
import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceRequest;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trustedanalytics.servicebroker.zk.config.catalog.BrokerPlans;

import java.io.IOException;

public class ZKServiceInstanceService extends ForwardingServiceInstanceServiceStore {

  private final BrokerPlans brokerPlans;

  private final ZookeeperClient zkClient;

  private static final Logger LOGGER = LoggerFactory.getLogger(ZKServiceInstanceService.class);

  public ZKServiceInstanceService(ServiceInstanceService delegate,
      BrokerPlans brokerPlans, ZookeeperClient zkClient) {
    super(delegate);
    this.zkClient = zkClient;
    this.brokerPlans = brokerPlans;
  }

  @Override
  public ServiceInstance createServiceInstance(CreateServiceInstanceRequest request)
      throws ServiceInstanceExistsException, ServiceBrokerException {
    ServiceInstance serviceInstance = super.createServiceInstance(request);

    //provisioning (creating znode for service instance)
    if(brokerPlans.getPlanProvisioning(request.getPlanId())) {
      try {
        byte[] content = new byte[]{};
        LOGGER.info("Creating znode for service instance.");
        zkClient.addZNode(request.getServiceInstanceId(), content);
      } catch (IOException e) {
        throw new ServiceBrokerException(e);
      }
    }
    return serviceInstance;
  }

}
