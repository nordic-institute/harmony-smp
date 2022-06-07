#Copyright (c) 2014-2018 Oracle and/or its affiliates. All rights reserved.
#
#Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
#
# WebLogic on Docker Default Domain
#
# Domain, as defined in DOMAIN_NAME, will be created in this script. Name defaults to 'base_domain'.
#
# Since : November, 2018
# Author: monica.riccelli@oracle.com
# ==============================================

import os
import socket

def getEnvVar(var):
  val=os.environ.get(var)
  if val==None:
    print "ERROR: Env var ",var, " not set."
    sys.exit(1)
  return val

# This python script is used to create a WebLogic domain

server_port                   = int(MANAGED_SERVER_PORT)
domain_path                   = os.environ.get("WL_DOMAIN_HOME")
cluster_name                  = CLUSTER_NAME
admin_server_name             = ADMIN_NAME
admin_port                    = int(ADMIN_PORT)
admin_https_port              = int(ADMIN_HTTPS_PORT)
domain_name                   = DOMAIN_NAME
t3_channel_port               = int(T3_CHANNEL_PORT)
t3_public_address             = T3_PUBLIC_ADDRESS
number_of_ms                  = int(CONFIGURED_MANAGED_SERVER_COUNT)
cluster_type                  = CLUSTER_TYPE
managed_server_name_base      = MANAGED_SERVER_NAME_BASE
production_mode_enabled       = PRODUCTION_MODE_ENABLED

# Read the domain secrets from the common python file
#execfile('%s/read-domain-secret.py' % script_dir)

print('domain_path              : [%s]' % domain_path)
print('domain_name              : [%s]' % domain_name)
print('admin_server_name        : [%s]' % admin_server_name)
print('admin_port               : [%s]' % admin_port)
print('cluster_name             : [%s]' % cluster_name)
print('server_port              : [%s]' % server_port)
print('number_of_ms             : [%s]' % number_of_ms)
print('cluster_type             : [%s]' % cluster_type)
print('managed_server_name_base : [%s]' % managed_server_name_base)
print('production_mode_enabled  : [%s]' % production_mode_enabled)
print('t3_channel_port          : [%s]' % t3_channel_port)
print('t3_public_address        : [%s]' % t3_public_address)

# Open default domain template
# ============================
readTemplate("/u01/oracle/wlserver/common/templates/wls/wls.jar")

set('Name', domain_name)
setOption('DomainName', domain_name)
create(domain_name,'Log')
cd('/Log/%s' % domain_name);
set('FileName', '%s.log' % (domain_name))

# Configure the Administration Server
# ===================================
cd('/Servers/AdminServer')
#set('ListenAddress', '%s-%s' % (domain_uid, admin_server_name_svc))
set('ListenPort', admin_port)
set('Name', admin_server_name)


create('T3Channel', 'NetworkAccessPoint')
cd('/Servers/%s/NetworkAccessPoints/T3Channel' % admin_server_name)
set('PublicPort', t3_channel_port)
set('PublicAddress', t3_public_address)
#set('ListenAddress', '%s-%s' % (domain_uid, admin_server_name_svc))
set('ListenPort', t3_channel_port)

#cd('/Servers/%s' % admin_server_name)
#create(admin_server_name, 'Log')
#cd('/Servers/%s/Log/%s' % (admin_server_name, admin_server_name))
#set('FileName', '%s.log' % (admin_server_name))

# Set the admin user's username and password
# ==========================================
cd('/Security/%s/User/weblogic' % domain_name)
cmo.setName(username)
cmo.setPassword(password)

# Write the domain and close the domain template
# ==============================================
setOption('OverwriteDomain', 'true')

# Create a cluster
# ======================
cd('/')
cl=create(cluster_name, 'Cluster')

if cluster_type == "CONFIGURED":

  # Create managed servers
  for index in range(0, number_of_ms):
    cd('/')
    msIndex = index+1

    cd('/')
    name = '%s%s' % (managed_server_name_base, msIndex)
#   name_svc = '%s%s' % (managed_server_name_base_svc, msIndex)

    create(name, 'Server')
    cd('/Servers/%s/' % name )
    print('managed server name is %s' % name);
#   set('ListenAddress', '%s-%s' % (domain_uid, name_svc))
    set('ListenPort', server_port)
    set('NumOfRetriesBeforeMSIMode', 0)
    set('RetryIntervalBeforeMSIMode', 1)
    set('Cluster', cluster_name)

#    create(name,'Log')
#    cd('/Servers/%s/Log/%s' % (name, name))
#    set('FileName', '%s.log' % (name))

else:
  print('Configuring Dynamic Cluster %s' % cluster_name)

  templateName = cluster_name + "-template"
  print('Creating Server Template: %s' % templateName)
  st1=create(templateName, 'ServerTemplate')
  print('Done creating Server Template: %s' % templateName)
  cd('/ServerTemplates/%s' % templateName)
  cmo.setListenPort(server_port)
  cmo.setCluster(cl)
  cd('/Clusters/%s' % cluster_name)
  create(cluster_name, 'DynamicServers')
  cd('DynamicServers/%s' % cluster_name)
  set('ServerTemplate', st1)
  set('ServerNamePrefix', managed_server_name_base)
  set('DynamicClusterSize', number_of_ms)
  set('MaxDynamicClusterSize', number_of_ms)
  set('CalculatedListenPorts', false)

  print('Done setting attributes for Dynamic Cluster: %s' % cluster_name)

# Write Domain
# ============
print('Write domain to: %s' % domain_path)
writeDomain(domain_path)
closeTemplate()
print 'Domain Created'

# Update Domain
readDomain(domain_path)
cd('/')
if production_mode_enabled == "true":
  cmo.setProductionModeEnabled(true)
else: 
  cmo.setProductionModeEnabled(false)

updateDomain()
closeDomain()
print 'Domain Updated'
print 'Done'

# Exit WLST
# =========
exit()
