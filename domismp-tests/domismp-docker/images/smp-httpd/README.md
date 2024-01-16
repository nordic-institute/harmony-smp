# Edelivery HTTPD

This image is created from httpd:2.4.39.
It contains an HTTPD server prepared to be used as a load balancer 
for the weblogic SMP cluster environment. It has configured AllowEncodedSlashes NoDecode to allow
%2F for the test PASSING_AUTO_BAMBOO/SMP063EDELIVERY364_slash_encodingTomca

# Usage

Usage via docker-compose, e.g:

```
  smp-httpd:
    depends_on:
      - smp-node-01
      - smp-node-02
    image: smp-httpd:${SMP_VERSION:-5.0-SNAPSHOT}
    environment:
      - VHOST_CORNER_HOSTNAME=smp.edelivery.eu
      - NODES_COUNT=2
      - NODE_HOSTNAMES=smp-node-01,smp-node-02
      - NODE_PORT_NUMBERS=8001,8001
      - AllowEncodedSlashes=NoDecode
```


   
# More information

Please check the base image:
https://hub.docker.com/_/httpd/