package eu.europa.ec.edelivery.smp.services.mail;

import eu.europa.ec.edelivery.smp.data.model.DBAlert;

import java.util.Properties;

public class PropertiesMailModel implements MailModel<Properties> {

    private final Properties model = new Properties();

    private final String templatePath;

    private final String subject;


    public PropertiesMailModel(final String templatePath, final String subject) {
        this.templatePath = templatePath;
        this.subject = subject;
    }

    public PropertiesMailModel(final DBAlert alert) {
        this.templatePath = alert.getAlertType().getTemplate();
        this.subject = alert.getMailSubject();
        alert.getProperties().forEach((key, prop) -> setProperty(key, prop.getValue())
        );
    }

    @Override
    public Properties getModel() {
        return model;
    }


    public void setProperty(String key, String value) {
        model.setProperty(key, value);
    }

    @Override
    public String getTemplatePath() {
        return templatePath;
    }

    @Override
    public String getSubject() {
        return subject;
    }
}
