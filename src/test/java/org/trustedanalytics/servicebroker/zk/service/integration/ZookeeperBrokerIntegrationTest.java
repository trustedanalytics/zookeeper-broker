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
package org.trustedanalytics.servicebroker.zk.service.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import org.apache.curator.test.TestingServer;
import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceRequest;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.model.ServiceInstanceBinding;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceBindingService;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.trustedanalytics.servicebroker.zk.config.Application;
import org.trustedanalytics.servicebroker.zk.config.ExternalConfiguration;
import org.trustedanalytics.servicebroker.zk.service.integration.config.kerberos.KerberosLocalConfiguration;
import org.trustedanalytics.servicebroker.zk.service.integration.config.store.ZkLocalConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, ZkLocalConfiguration.class,
    KerberosLocalConfiguration.class})
@WebAppConfiguration
@IntegrationTest("server.port=0")
@ActiveProfiles("integration-test")
public class ZookeeperBrokerIntegrationTest {

  @Autowired
  private TestingServer zkServer;

  @Autowired
  private ExternalConfiguration conf;

  @Autowired
  private ServiceInstanceService serviceBean;

  @Autowired
  private ServiceInstanceBindingService bindingBean;

  @Test
  public void testCreateServiceInstance_success_shouldStoreInstanceDataInBrokerStore()
      throws Exception {
    CreateServiceInstanceRequest request = getCreateInstanceRequest("zk_instance");

    serviceBean.createServiceInstance(request);

    ServiceInstance serviceInstance = serviceBean.getServiceInstance("zk_instance");
    assertThat(serviceInstance.getServiceInstanceId(), equalTo(request.getServiceInstanceId()));
  }

  @Test
  public void testCreateInstanceBinding_success_shouldReturnZookeeperNodeInCredentials()
      throws Exception {
    CreateServiceInstanceBindingRequest request =
        new CreateServiceInstanceBindingRequest(getServiceInstance("serviceId")
            .getServiceDefinitionId(), "-shared-plan", "appGuid").withBindingId("bindingId")
            .withServiceInstanceId("serviceId");

    ServiceInstanceBinding binding = bindingBean.createServiceInstanceBinding(request);

    String namespaceInCredentials = (String) binding.getCredentials().get("zk.node");
    assertThat(namespaceInCredentials, equalTo(conf.getBrokerRootNode() + "/serviceId"));
  }

  private ServiceInstance getServiceInstance(String id) {
    return new ServiceInstance(new CreateServiceInstanceRequest(id, "-shared-plan", "organizationGuid",
        "spaceGuid"));
  }

  private CreateServiceInstanceRequest getCreateInstanceRequest(String serviceId) {
    return new CreateServiceInstanceRequest("serviceDefinitionId", "-shared-plan", "organizationGuid",
        "spaceGuid").withServiceInstanceId(serviceId);
  }
}
