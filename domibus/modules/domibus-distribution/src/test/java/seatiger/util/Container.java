package seatiger.util;

import eu.eCODEX.testing.infrastructure.ServerBuilder;
import eu.eCODEX.testing.infrastructure.impl.ServerBuilderImpl;
import org.apache.log4j.Logger;
import org.codehaus.cargo.container.deployer.DeployableMonitor;
import org.codehaus.cargo.container.deployer.DeployableMonitorListener;
import org.codehaus.cargo.container.deployer.URLDeployableMonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static seatiger.util.Configuration.*;

public class Container {
    private static final Logger LOG = Logger.getLogger(Container.class);
    Properties props = new Properties();
    private String defaultConfigFile = System.getProperty(DOMIBUS_TESTING_CONFIG_FILE);
    private Type type;
    private ServerBuilder sb = new ServerBuilderImpl();
    private ServerBuilder.Type containerId = ServerBuilder.Type.tomcat6x;

    public String getJvmArgs() {
        return jvmArgs;
    }


    public Container configure() {
        this.sb.configure();
        return this;
    }


    public Container setJvmArgs(String jvmArgs) {
        this.jvmArgs = jvmArgs;
        return this;
    }

    private String jvmArgs = "-Duser.language=en";
    private List<String> dependencies = new ArrayList<String>();
    private String confHome;
    private boolean includeDefaultDependencies = true;
    private boolean deployDomibus = true;
    private boolean includeDefaultConfigFiles = true;
    private List<String> nonFilteredExtensions = new ArrayList<String>();

    public Container(final Type type) {
        this(type, null);
    }


    public Container(final Type type, final String... configfiles) {
        this.nonFilteredExtensions.add(".jks");
        this.nonFilteredExtensions.add(".mar");
        this.nonFilteredExtensions.add(".aar");
        this.nonFilteredExtensions.add(".sql");
        this.nonFilteredExtensions.add(".ddl");
        this.type = type;
        try {
            this.props.load(new FileInputStream(this.defaultConfigFile));
            if (configfiles != null) {
                for (final String file : configfiles) {
                    if (file != null) {
                        this.props.load(new FileInputStream(file));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Container.LOG.error(e);
        } catch (IOException e) {
            Container.LOG.error(e);
        }
    }

    public void start() {
        preparePortProperties();
        this.sb.setContainerID(this.containerId);
        this.sb.setConfigurationHome(
                ((this.confHome == null) || this.confHome.isEmpty()) ? this.type.name() : this.confHome);
        if (this.includeDefaultDependencies) {
            for (final String dep : new File(System.getProperty(GATEWAY_SERVER_DEPENDENCIES)).list()) {
                this.dependencies.add(System.getProperty(GATEWAY_SERVER_DEPENDENCIES) + "/" + dep);
            }
        }
        for (final String dep : this.dependencies) {
            this.sb.addDependency(dep);
        }
        this.sb.addPropertiesForFiltering(this.props);
        if (this.includeDefaultConfigFiles) {
            final File baseDir;
            if (Type.RECEIVING == this.type) {
                baseDir = new File(System.getProperty(GATEWAY_SERVER_RECEIVING_CONFIG_FOLDER));
            } else {
                baseDir = new File(System.getProperty(GATEWAY_SERVER_SENDING_CONFIG_FOLDER));
            }
            this.includeRecursively(baseDir, this.sb, "/");
        }

        if (this.deployDomibus) {
            this.sb.addDeployable(System.getProperty(this.type.getPropertyKey()), "domibus");
        }
        this.sb.addJvmArgs(this.jvmArgs);

        String pingURL = createPingURL();
        try {
            DeployableMonitor depMon = new URLDeployableMonitor(new URL(pingURL));
            depMon.registerListener(new DeployableMonitorListener() {
                @Override
                public void deployed() {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                public void undeployed() {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            });
        } catch (MalformedURLException e) {
            LOG.error(e);
        }

        this.sb.configure().start();
    }

    private String createPingURL() {
        String pingURL = "";
        switch (this.type) {
            case SENDING:
                pingURL += System.getProperty(DOMIBUS_MODULE_EBMS3_MSH_PROTOCOL) + "://";
                pingURL += System.getProperty(DOMIBUS_MODULE_EBMS3_MSH_HOST) + ":";
                pingURL += System.getProperty(DOMIBUS_MODULE_EBMS3_MSH_PORT) + "/";
                pingURL += System.getProperty(DOMIBUS_MODULE_EBMS3_MSH_CONTEXTPATH) + "/";
                pingURL += "services/BackendService?wsdl";
                break;
            case RECEIVING:
                pingURL += System.getProperty(DOMIBUS_TESTING_MSH_PROTOCOL) + "://";
                pingURL += System.getProperty(DOMIBUS_TESTING_MSH_HOST) + ":";
                pingURL += System.getProperty(DOMIBUS_TESTING_MSH_PORT) + "/";
                pingURL += System.getProperty(DOMIBUS_TESTING_MSH_CONTEXTPATH) + "/";
                pingURL += "services/BackendService?wsdl";
        }
        return pingURL;

    }

    private void preparePortProperties() {
        switch (this.type) {
            case SENDING:
                this.props.setProperty("cargo.servlet.port", this.props.getProperty(DOMIBUS_MODULE_EBMS3_MSH_PORT));
                this.props.setProperty("cargo.tomcat.ajp.port", this.props.getProperty(TOMCAT_AJP_PORT));
                this.props.setProperty("cargo.rmi.port", this.props.getProperty(TOMCAT_RMI_PORT));
                break;
            case RECEIVING:
                this.props.setProperty("cargo.servlet.port", this.props.getProperty(DOMIBUS_TESTING_MSH_PORT));
                this.props.setProperty("cargo.tomcat.ajp.port", this.props.getProperty(TOMCAT_TESTING_AJP_PORT));
                this.props.setProperty("cargo.rmi.port", this.props.getProperty(TOMCAT_TESTING_RMI_PORT));
                break;
        }
    }

    private void includeRecursively(final File baseDir, final ServerBuilder sb, final String relativeDir) {
        for (final File f : baseDir.listFiles()) {
            if (f.isDirectory()) {
                this.includeRecursively(f, sb, relativeDir + f.getName() + "/");
            } else {
                sb.includeConfigFile(f, "conf" + relativeDir, !this.isExcludedExtension(f.getName()));
            }
        }
    }

    private boolean isExcludedExtension(final String name) {
        for (final String ext : this.nonFilteredExtensions) {
            if (name.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    public void stop() {
        this.sb.addJvmArgs(jvmArgs).stop();
    }

    public enum Type {
        SENDING(GATEWAY_DEPLOYABLE_SENDING), RECEIVING(GATEWAY_DEPLOYABLE_RECEIVING);
        private String prop;

        private Type(final String prop) {
            this.prop = prop;

        }

        public String getPropertyKey() {
            return this.prop;
        }
    }
}
