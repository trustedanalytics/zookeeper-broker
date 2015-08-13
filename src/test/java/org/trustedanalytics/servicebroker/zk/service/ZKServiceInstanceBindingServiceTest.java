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

import com.google.common.collect.ImmutableMap;

import org.trustedanalytics.servicebroker.zk.config.ExternalConfiguration;

import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceRequest;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.model.ServiceInstanceBinding;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceBindingService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ZKServiceInstanceBindingServiceTest {

  @Mock
  private ExternalConfiguration configuration;

  @Mock
  private ServiceInstanceBindingService instanceBindingService;

  private ZKServiceInstanceBindingService service;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void init() {
    service = new ZKServiceInstanceBindingService(instanceBindingService, ImmutableMap
        .of("zk.node", "/platform/serviceId"), configuration);
  }
  @Test
  public void testCreateServiceInstanceBinding_success_saveAndReturnsNewServiceInstanceBindingWithCredentials()
      throws Exception {
    when(configuration.getBrokerRootNode()).thenReturn("/platform");

    CreateServiceInstanceBindingRequest request = new CreateServiceInstanceBindingRequest(
        getServiceInstance("serviceId").getServiceDefinitionId(), "planId", "appGuid").
        withBindingId("bindingId").withServiceInstanceId("serviceId");
    when(instanceBindingService.createServiceInstanceBinding(request)).thenReturn(getServiceInstanceBinding("id"));
    ServiceInstanceBinding instance = service.createServiceInstanceBinding(request);

    assertThat(instance.getCredentials().get("zk.node"), equalTo("/platform/serviceId"));
  }

  private ServiceInstanceBinding getServiceInstanceBinding(String id) {
    return new ServiceInstanceBinding(id, "serviceId", Collections.emptyMap(), null, "guid");
  }

  private ServiceInstance getServiceInstance(String id) {
    return new ServiceInstance(new CreateServiceInstanceRequest(id, "planId", "organizationGuid", "spaceGuid"));
  }
}