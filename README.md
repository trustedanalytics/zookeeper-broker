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

## Deploy 
Push broker binary code to cloud foundry (use cf client).:
```
cf push zookeeper-broker -p targer/zook-broker-*.jar -m 512M -i 1 --no-start
```

## Configure
For strict separation of config from code (twelve-factor principle), configuration must be placed in environment variables.
 
Broker configuration params list (environment properties):
* obligatory
  * USER_PASSWORD - password to interact with service broker
  * ZK_CLUSTER_URL - comma separated ip addresses of zookeeper nodes (i.e.: 10.10.9.145:2181,10.10.9.146:2181)
* obligatory only when zookeeper requires kerberos authentication:
  * KRB_KDC_HOST - kerberos kdc host address
  * KRB_REALM - kerberos realm name
* optional :
  * BASE_GUID - base id for catalog plan creation (uuid)
  * CF_CATALOG_SERVICENAME - service name in cloud foundry catalog (default: zookeeper)
  * CF_CATALOG_SERVICEID - service id in cloud foundry catalog (default: zookeeper)
  * ZK_BRK_SPACE - (default: /zkbrk_space)
  * ZK_BRK_ROOT - (default: /platform)

For instance.:
```
cf se zookeeper-broker ZK_CLUSTER_URL 10.10.9.145:2181,10.10.9.146:2181
```

When zookeeper requires kerberos authentication set:
```
cf se zookeeper-broker KRB_KDC_HOST ip-10-10-9-198.us-west-2.compute.internal
cf se zookeeper-broker KRB_REALM US-WEST-2.COMPUTE.INTERNAL
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
     "zk.cluster": "10.10.9.145:2181,10.10.9.146:2181",
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