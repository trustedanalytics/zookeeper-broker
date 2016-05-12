[![Build Status](https://travis-ci.org/trustedanalytics/zookeeper-broker.svg?branch=master)](https://travis-ci.org/trustedanalytics/zookeeper-broker)
[![Dependency Status](https://www.versioneye.com/user/projects/5729c305a0ca35004baf7db5/badge.svg?style=flat)](https://www.versioneye.com/user/projects/5729c305a0ca35004baf7db5)

zookeeper-broker
================
Cloud foundry broker for Zookeeper.

# How to use it?
To use zookeeper-broker, you need to build it from sources configure, deploy, create instance and bind it to your app. Follow steps described below. 

## Build 
Run command for compile and package.: 
```
mvn clean package
```

## Kerberos configuration
Broker automatically bind to an existing kerberos provide service. This will provide default kerberos configuration, for REALM and KDC host. Before deploy check:

- if kerberos service does not exists in your space, you can create it with command:
```
cf cups kerberos-service -p '{ "kdc": "kdc-host", "kpassword": "kerberos-password", "krealm": "kerberos-realm", "kuser": "kerberos-user" }'
```

- if kerberos-service exists in your space, you can update it with command:
```
cf uups kerberos-service -p '{ "kdc": "kdc-host", "kpassword": "kerberos-password", "krealm": "kerberos-realm", "kuser": "kerberos-user" }'
```

## Deploy 
Push broker binary code to cloud foundry (use cf client).:
```
cf push zookeeper-broker -p target/zookeeper-broker-*.jar -m 512M -i 1 --no-start
```

## Configure
For strict separation of config from code (twelve-factor principle), configuration must be placed in environment variables.
 
Broker configuration params list (environment properties):
* obligatory
  * USER_PASSWORD - password to interact with service broker
  * ZK_CLUSTER_URL - comma separated ip addresses of zookeeper nodes (in case of Kerberos, domain names should be used) for instance : host-1.domain:2181,host-2.domain:2181
* optional :
  * BASE_GUID - base id for catalog plan creation (uuid)
  * CF_CATALOG_SERVICENAME - service name in cloud foundry catalog (default: zookeeper)
  * CF_CATALOG_SERVICEID - service id in cloud foundry catalog (default: zookeeper)
  * ZK_BRK_STORE - (default: /zkbrk_store)
  * ZK_BRK_ROOT - (default: /platform)

For instance.:
```
cf se zookeeper-broker ZK_CLUSTER_URL host-1.domain:2181,host-2.domain:2181
```

## Start  service broker application

Use cf client :
```
cf start  zookeeper-broker
```
## Create new service instance 
  
Use cf client : 
```
cf create-service-broker zookeeper-broker <user> <password> https://zookeeper-broker.<platform_domain>
cf enable-service-access zookeeper
cf cs zookeeper shared  zookeeper-instance
```

## Binding broker instance

Broker instance can be bind with cf client :
```
cf bs <app> zookeeper-instance
```
or by configuration in app's manifest.yml : 
```yaml
  services:
    - zookeeper-instance
```

To check if broker instance is bound, use cf client : 
```
cf env <app>
```
and look for : 
```yaml
  "zookeeper": [
   {
    "credentials": {
     "kerberos": {
      "kdc": "ip-10-10-9-198.us-west-2.compute.internal",
      "krealm": "US-WEST-2.COMPUTE.INTERNAL"
     },
     "zk.cluster": "host-1.domain:2181,host-2.domain:2181",
     "zk.node": "/platform/e59a67b8-bcad-403e-a2a9-6bde5285f05e"
    },
    "label": "zookeeper",
    "name": "zookeeper-instance",
    "plan": "shared",
    "tags": []
   }
  ]
```
in VCAP_SERVICES.
