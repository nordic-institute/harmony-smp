package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.config.SMPEnvironmentProperties;
import eu.europa.ec.edelivery.smp.config.enums.SMPDomainPropertyEnum;
import eu.europa.ec.edelivery.smp.config.enums.SMPEnvPropertyEnum;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBDomainConfiguration;
import eu.europa.ec.edelivery.smp.data.ui.DomainPropertyRO;
import eu.europa.ec.edelivery.smp.data.ui.PropertyRO;
import eu.europa.ec.edelivery.smp.data.ui.PropertyValidationRO;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPRole;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIDomainEditService;
import eu.europa.ec.edelivery.smp.utils.PropertyUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.PARAM_DOMAIN_ID;
import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.QUERY_DOMAIN_CONFIGURATION_ALL;


/**
 * Specific DAO for DomainConfiguration
 *
 * @author Joze Rihtarsic
 * @since 5.1
 */
@Repository
public class DomainConfigurationDao extends BaseDao<DBDomainConfiguration> {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIDomainEditService.class);


    /**
     * Method returns all Domain properties for the given domain and role. If the property does not exist in the database,
     * the default value is returned. If the role is not system admin, and
     * property is system admin only, the property is not returned!
     *
     * @param domain - domain to get properties
     * @param role   - the properties are filtered based on the role. System admin gets all roles
     *               but other roles get only properties which are not system admin only.
     * @return list of domain properties
     */
    public List<DBDomainConfiguration> getDomainPropertiesForRole(DBDomain domain, SMPRole role) {
        // get domain configuration from the database
        List<DBDomainConfiguration> domainConfiguration = getDomainConfiguration(domain);
        // convert list to map
        Map<String, DBDomainConfiguration> dbList = domainConfiguration.stream()
                .collect(Collectors.toMap(DBDomainConfiguration::getProperty, Function.identity()));

        // return only properties that are not system admin only
        return Arrays.stream(SMPDomainPropertyEnum.values())
                .filter(domainPropertyRO -> role == SMPRole.SYSTEM_ADMIN || !domainPropertyRO.isSystemAdminOnly())
                .map(enumType -> {
                    if (dbList.containsKey(enumType.getProperty())) {
                        return dbList.get(enumType.getProperty());
                    }
                    return createDBDomainConfiguration(enumType);
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Method creates a Domain properties for the given SMPDomainPropertyEnum. The object is not
     * persisted in the database.
     */
    private DBDomainConfiguration createDBDomainConfiguration(SMPDomainPropertyEnum domainPropertyEnum) {
        DBDomainConfiguration dbDomainConfiguration = new DBDomainConfiguration();
        dbDomainConfiguration.setProperty(domainPropertyEnum.getProperty());
        dbDomainConfiguration.setValue(domainPropertyEnum.getDefValue());
        dbDomainConfiguration.setDescription(domainPropertyEnum.getDesc());
        dbDomainConfiguration.setUseSystemDefault(true);
        return dbDomainConfiguration;
    }

    /**
     * Method updates domain properties for the given domain and role. If the role is not system admin, and
     * property is system admin only, the property is not updated!
     *
     * @param domain           - domain to update properties
     * @param domainProperties - list of domain properties
     * @param role             - role of the user
     * @return list of updated domain properties
     */
    @Transactional
    public List<DBDomainConfiguration> updateDomainPropertiesForRole(DBDomain domain,
                                                                     List<DomainPropertyRO> domainProperties,
                                                                     SMPRole role) {

        // get current domain configuration
        Map<String, DBDomainConfiguration> currentDomainConfiguration = getDomainConfiguration(domain)
                .stream().collect(Collectors.toMap(DBDomainConfiguration::getProperty, Function.identity()));
        Map<String, DomainPropertyRO> newDomainPropertyValues =
                domainProperties.stream().collect(Collectors.toMap(DomainPropertyRO::getProperty, Function.identity()));

        List<DBDomainConfiguration> listOfDomainConfiguration = new ArrayList<>();

        // database domain configuration property list must match SMPDomainPropertyEnum
        for (SMPDomainPropertyEnum domainProp : SMPDomainPropertyEnum.values()) {
            if (role != SMPRole.SYSTEM_ADMIN && domainProp.isSystemAdminOnly()) {
                // skip system admin only properties
                continue;
            }
            DBDomainConfiguration domainConfiguration = currentDomainConfiguration.get(domainProp.getProperty());
            DomainPropertyRO domainPropertyRO = newDomainPropertyValues.get(domainProp.getProperty());
            // if property already exists in the database, update value
            DBDomainConfiguration updatedDomainProp = updateDomainProperty(domain, domainProp, domainConfiguration, domainPropertyRO);
            listOfDomainConfiguration.add(updatedDomainProp);
            // remove updated property from the map
            currentDomainConfiguration.remove(domainProp.getProperty());
            LOG.debug("Updated domain property [{}]: [{}] for domain [{}]",
                    domainProp.getProperty(), updatedDomainProp, domain.getDomainCode());

        }
        // remove properties that are not in the new list
        currentDomainConfiguration.values().forEach(domainConfiguration -> {
            removeConfiguration(domainConfiguration);
            LOG.debug("Removed domain property [{}]: [{}] for domain [{}]",
                    domainConfiguration.getProperty(), domainConfiguration.getValue(),
                    domain.getDomainCode());
        });

        //
        return listOfDomainConfiguration;
    }


    public PropertyValidationRO validateDomainProperty(PropertyRO propertyRO) {
        LOG.info("Validate property: [{}]", propertyRO.getProperty());
        PropertyValidationRO propertyValidationRO = new PropertyValidationRO();
        propertyValidationRO.setProperty(propertyRO.getProperty());
        propertyValidationRO.setValue(propertyRO.getValue());

        Optional<SMPDomainPropertyEnum> optPropertyEnum = SMPDomainPropertyEnum.getByProperty(propertyRO.getProperty());
        if (!optPropertyEnum.isPresent()) {
            LOG.debug("Property: [{}] is not Domain SMP property!", propertyRO.getProperty());
            propertyValidationRO.setErrorMessage("Property [" + propertyRO.getProperty() + "] is not SMP property!");
            propertyValidationRO.setPropertyValid(false);
            return propertyValidationRO;
        }

        SMPDomainPropertyEnum propertyEnum = optPropertyEnum.get();
        return validateDomainPropertyValue(propertyEnum, propertyValidationRO);
    }

    /**
     * Method validates domain property value for given property enum.
     *
     * @param propertyEnum         - property enum
     * @param propertyValidationRO - property validation object with value.
     * @return property validation object with validation result and error message if validation failed.
     */
    private PropertyValidationRO validateDomainPropertyValue(SMPDomainPropertyEnum propertyEnum,
                                                             PropertyValidationRO propertyValidationRO) {
        // try to parse value
        try {
            File confDir = Paths.get(SMPEnvironmentProperties.getInstance().getEnvPropertyValue(SMPEnvPropertyEnum.SECURITY_FOLDER)).toFile();
            PropertyUtils.parseProperty(propertyEnum.getPropertyEnum(), propertyValidationRO.getValue(), confDir);
        } catch (SMPRuntimeException ex) {
            propertyValidationRO.setErrorMessage(ex.getMessage());
            propertyValidationRO.setPropertyValid(false);
            return propertyValidationRO;
        }

        propertyValidationRO.setPropertyValid(true);
        return propertyValidationRO;
    }


    /**
     * Returns the domain properties for the given domain.
     *
     * @param domain - domain for which the properties are requested
     * @return - list of domain properties
     */
    public List<DBDomainConfiguration> getDomainConfiguration(DBDomain domain) {
        TypedQuery<DBDomainConfiguration> query = memEManager.createNamedQuery(QUERY_DOMAIN_CONFIGURATION_ALL,
                DBDomainConfiguration.class);
        query.setParameter(PARAM_DOMAIN_ID, domain.getId());
        return query.getResultList();
    }

    /**
     * Update domain property. If property does not exist in the database, it will be created.
     * The method must be called in transactional context, else TransactionRequiredException
     * will be thrown from JPA merge method.
     *
     * @param domain              - domain to update. Value is used in case domain configuration does not exist in the database.
     * @param domainProp          - domain property to update
     * @param domainConfiguration - current domain configuration or null if it does not exist in the database.
     *                            The object must be attached to the persistence context.
     * @param domainPropertyRO    - new domain property value and system default flag
     * @return new/updated  domain configuration
     */
    public DBDomainConfiguration updateDomainProperty(DBDomain domain, SMPDomainPropertyEnum domainProp,
                                                      DBDomainConfiguration domainConfiguration, DomainPropertyRO domainPropertyRO) {
        if (domainConfiguration != null && domainPropertyRO == null) {
            LOG.debug("Domain property [{}] is not set. Skip the update of property value.",
                    domainProp.getProperty());
            return domainConfiguration;
        }

        if (domainConfiguration == null) {
            domainConfiguration = new DBDomainConfiguration();
            domainConfiguration.setDomain(domain);
            domainConfiguration.setProperty(domainProp.getProperty());
        }

        if (domainPropertyRO != null) {
            domainConfiguration.setValue(domainPropertyRO.getValue());
            domainConfiguration.setUseSystemDefault(domainPropertyRO.isSystemDefault());
        } else {
            LOG.debug("Domain property [{}] is not set and propperty does not exists in database. Create new system default value",
                    domainProp.getProperty());
            domainConfiguration.setValue(domainProp.getDefValue());
            domainConfiguration.setDescription(domainProp.getDesc());
            domainConfiguration.setUseSystemDefault(true);
        }
        mergeConfiguration(domainConfiguration);
        return domainConfiguration;
    }


    /**
     * The method Merge the state of the given domain configuration into the current
     * persistence context.  The method must be
     * called in existing transaction, and it is used to manage domain properties.
     *
     * @param entity - domain configuration to be merged
     * @return - jpa merged/managed domain configuration
     * @throws TransactionRequiredException if there is no transaction when
     *                                      invoked on a container-managed entity manager of that is of type
     *                                      <code>PersistenceContextType.TRANSACTION</code>
     */
    public DBDomainConfiguration mergeConfiguration(DBDomainConfiguration entity) {
        return memEManager.merge(entity);
    }

    public void removeConfiguration(DBDomainConfiguration entity) {
        memEManager.remove(entity);
    }
}
