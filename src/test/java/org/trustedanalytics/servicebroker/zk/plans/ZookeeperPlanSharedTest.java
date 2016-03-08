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
package org.trustedanalytics.servicebroker.zk.plans;

import com.google.common.collect.ImmutableMap;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.junit.Test;
import org.trustedanalytics.servicebroker.framework.Credentials;
import org.trustedanalytics.servicebroker.zk.plans.binding.ZookeeperBindingClientFactory;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.trustedanalytics.servicebroker.test.cloudfoundry.CfModelsFactory.getServiceInstance;

public class ZookeeperPlanSharedTest {
  @Test
  public void bind_addInstanceIdToZnodePath_returnCredentialsMap() throws Exception {
    //arrange
    ZookeeperPlanShared plan =
        new ZookeeperPlanShared(ZookeeperBindingClientFactory.create(
            new Credentials(ImmutableMap.of("zk.cluster", "test")), "/userspace"));

    //act
    ServiceInstance serviceInstance = getServiceInstance();
    Map<String, Object> actualOutputCredentials = plan.bind(serviceInstance);

    //assert
    assertThat(actualOutputCredentials, hasEntry("zk.cluster", "test"));
    assertThat(actualOutputCredentials,
        hasEntry("zk.node", "/userspace/" + serviceInstance.getServiceInstanceId()));
  }
}
