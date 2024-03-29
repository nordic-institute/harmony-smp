package utils.enums;

public class SMPMessages {

	public static final String MSG_1 = "Login failed; Invalid userID or password";
	public static final String MSG_2 = "Too many invalid attempts to log in. Access has been temporarily\n" +
			"suspended. Please try again later with the right credentials.";
	public static final String MSG_3 = "To abandon all changes performed since last save, click on the \"Cancel\" button.\n" +
			"Click on the \"Ok\" button keep your changes and come back to the current\n" +
			"window unchanged.";
	public static final String MSG_4 = "Please confirm by clicking on the \"Save\" button that you want to save all changes.\n" +
			"If you don't want to save these changes now, please click on the \"Don't\n" +
			"save now\" button";
	public static final String MSG_5 = "To delete the current item(s) click on the \"Ok\" button.\n" +
			"Click on the \"Cancel\" button to keep this item and come back to the\n" +
			"current window unchanged";
	public static final String MSG_6 = "The system detected a concurrent access.\n" +
			"Your changes are irremediably lost, and the data were reverted to what\n" +
			"the concurrent user saved before you.";
	public static final String MSG_7 = "Component ${COMPONENT} is not accessible. Administration console is\n" +
			"disabled.";
	public static final String MSG_8 = "You are about to leave the edition of the current ${OBJECT_TYPE} which\n" +
			"modifications were not saved yet.\n" +
			"Click on \"Abandon\" to abandon your changes.\n" +
			"Click \"Keep\" to stay on the current screen and keep your changes without\n" +
			"saving them now" +
			"Click \"Save\" to save your changes and move to the selected screen.";
	public static final String MSG_9 = "The selection criteria you provided are too restrictive, no result matches\n" +
			"these criteria. Please enter less selective criteria to obtain some results";
	public static final String MSG_10 = "A value must be provided for the plugin and at least for one of the other\n" +
			"column for the filter to be applicable.";
	public static final String MSG_11 = "You are about to delete ServiceGroup: ${ServiceGroup} and its ServiceMetadata.\n" +
			"Click on \"Delete\" to confirm the deletion.\n" +
			"Click on \"Keep\" to keep the ServiceMetadata.";
	public static final String MSG_12 = "You are about to delete ServiceMetadata: ${ServiceMetadata}.\n" +
			"Click on \"Delete\" to confirm the deletion.\n" +
			"Click on \"Keep\" to keep the ServiceMetadata";
	public static final String MSG_13 = "You are about to delete User: ${User}.\n" +
			"Click on \"Delete\" to confirm the deletion.\n" +
			"Click on \"Keep\" to keep the user.\n";
	public static final String MSG_14 = "You are about to create an SMP Domain: ${SMP_BDMSL_ID}. Action will\n" +
			"register new user SMP user to SML for domain ${ BDMSL _DOMAIN}.\n" +
			"Domain will be saved to SMP. Action is not recoverable.\n" +
			"Click on \"Register\" to confirm the registration and saving.\n" +
			"Click on \"Cancel\" to cancel the registration.";
	@SuppressWarnings("SpellCheckingInspection")
	public static final String MSG_15 = "You are about to delete an SMP Domain: ${SMP_DOMAIN_ID}. Action\n" +
			"will unregister SMP domain user ${SMP_SML_ID}. from SML for domain\n" +
			"${ BDMSL _DOMAIN}. Action is not recoverable.\n" +
			"Click on \"Delete\" to confirm the deleting and unregistration of domain.\n" +
			"Click on \"Keep\" to keep the domain.";
	public static final String MSG_16 = "You are about to delete an X509 private key: ${Key }. Action is not recoverable.\n" +
			"Click on \"Delete\" to confirm the deleting the key.\n" +
			"Click on \"Keep\" to keep the key.";
	public static final String MSG_17 = "You are about to delete Domain: ${SMP_DOMAIN_ID}.\n" +
			"Click on \"Delete\" to confirm the deletion.\n" +
			"Click on \"Keep\" to keep the domain.";
	public static final String MSG_18 = "The operation 'update' completed successfully.";

	public static final String USER_OWN_DELETE_ERR = "Delete validation error Could not delete logged user!";

	public static final String MSG_19 = "Domain ${BDMSL_DOMAIN} is already registered with id ${DOMAIN_ID}";
	public static final String MSG_20 = "All changes were aborted and data restored into the present window";
	public static final String MSG_21 = "Unable to login. SMP is not running.";
	public static final String MSG_22 = "The user is suspended. Please try again later or contact your administrator.";
	public static final String MSG_23 = "Configuration error: Subject must have less than 256 character!";


	public static final String USERNAME_VALIDATION_MESSAGE = "Username is case insensitive and can only contain alphanumeric characters (letters a-zA-Z, numbers 0-9) and must have from 4 to 32 characters!";
	public static final String PASS_POLICY_MESSAGE = "Minimum length: 16 characters;Maximum length: 32 characters;At least one letter in lowercase;At least one letter in uppercase;At least one digit;At least one special character";
	public static final String PASS_NO_MATCH_MESSAGE = "Passwords do not match";
	public static final String PASS_NO_EMPTY_MESSAGE = "You should type a password";

	public static final String KEYSTORE_IMPORTED_MSG = "Keystore %s imported!";
	public static final String KEYSTORE_DELETION_MSG = "Certificate %s deleted!";
	public static final String SMLSMPID_VALIDATION_MESSAGE = "SML SMP ID should be up to 63 characters long, should only contain alphanumeric and hyphen characters, should not start with a digit nor a hyphen and should not end with a hyphen.";
	public static final String USER_EMAIL_VALIDATION_MESSAGE = "Email is invalid!";
	public static final String DOMAINCODE_VALIDATION_MESSAGE = "Domain code must contain only chars and numbers and must be less than 63 chars long.";
	public static final String VALID_XML_MESSAGE = "Servicemetadata is valid!";
	public static final String INVALID_XML_MESSAGE1 = "SAXParseException: XML document structures must start and end within the same entity.";
	public static final String INVALID_XML_MESSAGE2 = "SAXParseException: Content is not allowed in prolog.";
	public static final String INVALID_XML_MESSAGE3 = "SAXParseException: Content is not allowed in trailing section.";
	public static final String INVALID_XML_MESSAGE4 = "SAXParseException: cvc-complex-type.2.3: Element 'ServiceMetadata' cannot have character [children], because the type's content type is element-only.";
	public static final String INVALID_XML_MESSAGE5 = "SAXParseException: The end-tag for element type \"ServiceMetadata\" must end with a '>' delimiter.";
	public static final String INVALID_XML_MESSAGE6 = "SAXParseException: cvc-complex-type.2.3: Element 'Process' cannot have character [children], because the type's content type is element-only.";
	public static final String EMPTY_XML_WARN_MESSAGE = "Service metadata xml must not be empty";
	public static final String EMPTY_XML_MESSAGE = "Valid service metadata XML is required!";
	public static final String INVALID_XML_MESSAGE7 = "SAXParseException: The markup in the document following the root element must be well-formed.";

}
