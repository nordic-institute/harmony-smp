# pass keystore as the first parameter
keystore_path = sys.argv[1]
truststore_path = sys.argv[2]
domain_name = os.environ.get("WL_DOMAIN_NAME", "")
domain_path = os.environ.get("WL_DOMAIN_HOME", "")
admin_server_name = os.environ.get("WL_ADMIN_NAME", "")
admin_https_port = int(os.environ.get("WL_ADMIN_PORT_HTTPS", "7002"))
ksIdentityPassword=os.environ.get("WL_SERVER_TLS_KEYSTORE_PASS", "")
ksIdentityAlias=os.environ.get("WL_ADMIN_HOST", "")

print('domain_name : [%s]' % domain_name)
print('domain_home : [%s]' % domain_path)
print('keystore_path : [%s]' % keystore_path)
print('truststore_path : [%s]' % truststore_path)
print('admin_server_name : [%s]' % admin_server_name)
print('admin_https_port : [%s]' % admin_https_port)
print('Configure  : [%s]' % '/Servers/'+admin_server_name+'/TLS/' + admin_server_name)

def configureHTTPS():
    # ------------------------------------
    try:
        # configure HTTPS for admin server
        cd('/Servers/%s/' % admin_server_name)
        # set custom Identity and standards java Trust..
        cmo.setKeyStores('CustomIdentityAndCustomTrust')
        cmo.setCustomIdentityKeyStoreFileName(keystore_path)
        cmo.setCustomIdentityKeyStoreType('PKCS12')
        set('CustomIdentityKeyStorePassPhraseEncrypted', ksIdentityPassword)

        # set truststore
        cmo.setCustomTrustKeyStoreFileName(truststore_path)
        set('CustomTrustKeyStorePassPhraseEncrypted', ksIdentityPassword)
        cmo.setCustomTrustKeyStoreType('PKCS12')

        create(admin_server_name, 'SSL')
        cd('/Servers/'+admin_server_name+'/SSL/' + admin_server_name)
        cmo.setServerPrivateKeyAlias(ksIdentityAlias)
        set('ServerPrivateKeyPassPhraseEncrypted', ksIdentityPassword)


        cd('/Servers/'+admin_server_name+'/SSL/' + admin_server_name)
        cmo.setEnabled(true)
        cmo.setListenPort(admin_https_port)
    except Exception, e:
        print "Error occurred while configuring server keystore and HTTPS connector"
        dumpStack()
        print e

# Enable Use Authorization Providers to Protect JMX Access by default
print('Enable server SSL ...')

readDomain(domain_path)
configureHTTPS()
updateDomain()
closeDomain()

exit()
