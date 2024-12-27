# Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
#
# WLST Offline for deploying an application under APP_NAME packaged in APP_PKG_FILE located in APP_PKG_LOCATION
# It will read the domain under DOMAIN_HOME by default
#
# author: Bruno Borges <bruno.borges@oracle.com>
# since: December, 2015
#
import os

# Deployment Information
domain_name = os.environ.get('WL_DOMAIN_NAME', 'base_domain')
domain_home = os.environ.get('WL_DOMAIN_HOME', '/u01/oracle/user_projects/domains/' + domain_name)
cluster_name =  os.environ.get('WL_CLUSTER_NAME')
admin_name = os.environ.get("WL_ADMIN_NAME", "AdminServer")
target_name =   os.environ.get('WL_DEPLOYMENT_TARGET')


print('Domain Home      : [%s]' % domain_home)
print('Admin Name       : [%s]' % admin_name)
print('Cluster Name     : [%s]' % cluster_name)
print('Deployment target: [%s]' % target_name)
print('Datasource name  : [%s]' % dsname)
print('Datasource JNDI  : [%s]' % dsjndiname)
print('Datasource URL   : [%s]' % dsurl)
print('Datasource Driver: [%s]' % dsdriver)
print('Datasource User  : [%s]' % dsusername)
print('Datasource Test  : [%s]' % dstestquery)

# Read Domain in Offline Mode
# ===========================
readDomain(domain_home)

# Create Datasource
# ==================
create(dsname, 'JDBCSystemResource')
cd('/JDBCSystemResource/' + dsname + '/JdbcResource/' + dsname)
cmo.setName(dsname)

cd('/JDBCSystemResource/' + dsname + '/JdbcResource/' + dsname)
create('myJdbcDataSourceParams','JDBCDataSourceParams')
cd('JDBCDataSourceParams/NO_NAME_0')
set('JNDIName', java.lang.String(dsjndiname))
set('GlobalTransactionsProtocol', java.lang.String('None'))

cd('/JDBCSystemResource/' + dsname + '/JdbcResource/' + dsname)
create('myJdbcDriverParams','JDBCDriverParams')
cd('JDBCDriverParams/NO_NAME_0')
set('DriverName', dsdriver)
set('URL', dsurl)
set('PasswordEncrypted', dspassword)
set('UseXADataSourceInterface', 'false')

print 'create JDBCDriverParams Properties'
create('myProperties','Properties')
cd('Properties/NO_NAME_0')
create('user','Property')
cd('Property/user')
set('Value', dsusername)

cd('../../')
create('databaseName','Property')
cd('Property/databaseName')
set('Value', dsdbname)

print 'create JDBCConnectionPoolParams'
cd('/JDBCSystemResource/' + dsname + '/JdbcResource/' + dsname)
create('myJdbcConnectionPoolParams','JDBCConnectionPoolParams')
cd('JDBCConnectionPoolParams/NO_NAME_0')
set('TestTableName',dstestquery)

# Assign
# ======
assign('JDBCSystemResource', dsname, 'Target', target_name)

# Update Domain, Close It, Exit
# ==========================
updateDomain()
closeDomain()
exit()
