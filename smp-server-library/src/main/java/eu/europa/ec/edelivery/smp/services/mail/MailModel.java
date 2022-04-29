package eu.europa.ec.edelivery.smp.services.mail;

/**
 * Mail model for constitution of the mail content using freemaker template. The class was heavily inspired by Domibus
 * mail implementation
 *
 * @author Thomas Dussart
 * @author Joze Rihtarsic
 *
 * @since 4.2
 */
public interface MailModel<T> {

    T getModel();

    String getTemplatePath();

    String getSubject();
}

