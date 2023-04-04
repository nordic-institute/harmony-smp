package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.cron.SMPDynamicCronTrigger;
import eu.europa.ec.edelivery.smp.data.model.DBConfiguration;
import eu.europa.ec.edelivery.smp.data.ui.PropertyRO;
import eu.europa.ec.edelivery.smp.data.ui.PropertyValidationRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResultProperties;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.Map;

import static eu.europa.ec.edelivery.smp.cron.CronTriggerConfig.TRIGGER_BEAN_PROPERTY_REFRESH;
import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.SMP_CLUSTER_ENABLED;
import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.SMP_PROPERTY_REFRESH_CRON;
import static org.junit.Assert.*;

@Ignore
@ContextConfiguration(classes = {UIPropertyService.class})
public class UIPropertyServiceIntegrationTest extends AbstractServiceIntegrationTest {


    @Autowired
    protected UIPropertyService testInstance;
    @Autowired
    @Qualifier(TRIGGER_BEAN_PROPERTY_REFRESH)
    SMPDynamicCronTrigger refreshPropertiesTrigger;


    @Test
    public void getTableListAll() {

        //when
        ServiceResultProperties res = testInstance.getTableList(-1, -1, null, null, null);
        // then
        assertNotNull(res);
        assertEquals(SMPPropertyEnum.values().length, res.getCount().intValue());
        assertEquals(-1, res.getPage().intValue());
        assertEquals(-1, res.getPageSize().intValue());
        assertEquals(SMPPropertyEnum.values().length, res.getServiceEntities().size());
        assertNull(res.getFilter());
    }

    @Test
    public void getTableListFilterByCas() {

        //when
        String filter = ".cas";
        ServiceResultProperties res = testInstance.getTableList(-1, -1, null, null, filter);
        // then
        assertNotNull(res);
        assertTrue(res.getCount().intValue() < SMPPropertyEnum.values().length);
        assertEquals(-1, res.getPage().intValue());
        assertEquals(-1, res.getPageSize().intValue());
        for (PropertyRO propertyRO : res.getServiceEntities()) {
            assertTrue(StringUtils.containsIgnoreCase(propertyRO.getProperty(), filter));
        }
        assertEquals(filter, res.getFilter());
    }

    @Test
    public void createPropertyPendingToUpdate() {
        SMPPropertyEnum propertyType = SMP_PROPERTY_REFRESH_CRON;
        DBConfiguration dbConfiguration = new DBConfiguration();
        dbConfiguration.setProperty(propertyType.getProperty());
        dbConfiguration.setValue("1 1 * * * *");

        Map<String, DBConfiguration> dbProperties = Collections.singletonMap(propertyType.getProperty(), dbConfiguration);
        PropertyRO propertyRO = testInstance.createProperty(propertyType, dbProperties);

        assertEquals(dbConfiguration.getProperty(), propertyRO.getProperty());
        assertEquals(propertyType.getDesc(), propertyRO.getDesc());
        assertEquals(configurationDao.getCachedProperty(propertyType), propertyRO.getValue());

        assertEquals(dbConfiguration.getValue(), propertyRO.getNewValue());
        assertEquals(refreshPropertiesTrigger.getNextExecutionDate(), propertyRO.getUpdateDate());

        assertEquals(propertyType.isEncrypted(), propertyRO.isEncrypted());
        assertEquals(propertyType.isMandatory(), propertyRO.isMandatory());
        assertEquals(propertyType.isRestartNeeded(), propertyRO.isRestartNeeded());
    }

    @Test
    public void updatePropertyList() {
        configurationDao.setPropertyToDatabase(SMP_PROPERTY_REFRESH_CRON.getProperty(), SMP_PROPERTY_REFRESH_CRON.getDefValue());
        // set non cluster - to enable instant refresh
        configurationDao.setPropertyToDatabase(SMP_CLUSTER_ENABLED.getProperty(), "false");
        configurationDao.reloadPropertiesFromDatabase();

        SMPPropertyEnum propertyType = SMP_PROPERTY_REFRESH_CRON;
        String value = "2 1 * * * *";
        PropertyRO propertyRO = new PropertyRO(propertyType.getProperty(), value);
        assertNotEquals(value, configurationDao.getCachedProperty(propertyType));

        testInstance.updatePropertyList(Collections.singletonList(propertyRO));
        assertEquals(value, configurationDao.getCachedProperty(propertyType));
    }

    @Test
    public void validatePropertyNotExists() {
        String propertyName = "DoesNotExist";
        String propertyValue = "DoesNotExistValue";
        PropertyRO property = new PropertyRO(propertyName, propertyValue);

        PropertyValidationRO result = testInstance.validateProperty(property);
        assertNotNull(result);
        assertEquals(propertyName, result.getProperty());
        assertEquals(propertyValue, result.getValue());
        assertFalse(result.isPropertyValid());
        MatcherAssert.assertThat(result.getErrorMessage(), CoreMatchers.containsString("Property [" + propertyName + "] is not SMP property!"));
    }

    @Test
    public void validatePropertyInvalidValue() {
        String propertyName = SMPPropertyEnum.ACCESS_TOKEN_FAIL_DELAY.getProperty();
        String propertyValue = "NotANumber";
        PropertyRO property = new PropertyRO(propertyName, propertyValue);

        PropertyValidationRO result = testInstance.validateProperty(property);
        assertNotNull(result);
        assertEquals(propertyName, result.getProperty());
        assertEquals(propertyValue, result.getValue());
        assertFalse(result.isPropertyValid());
        MatcherAssert.assertThat(result.getErrorMessage(), CoreMatchers.containsString("Invalid integer: [" + propertyValue + "]. Error:NumberFormatException: For input string: \"" + propertyValue + "\"!"));
    }

    @Test
    public void validatePropertyOK() {
        String propertyName = SMPPropertyEnum.ACCESS_TOKEN_FAIL_DELAY.getProperty();
        String propertyValue = "1223232";
        PropertyRO property = new PropertyRO(propertyName, propertyValue);

        PropertyValidationRO result = testInstance.validateProperty(property);
        assertNotNull(result);
        assertEquals(propertyName, result.getProperty());
        assertEquals(propertyValue, result.getValue());
        assertTrue(result.isPropertyValid());
        assertNull(result.getErrorMessage());
    }

}
