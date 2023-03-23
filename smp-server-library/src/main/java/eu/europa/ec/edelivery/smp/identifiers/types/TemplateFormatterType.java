package eu.europa.ec.edelivery.smp.identifiers.types;

import eu.europa.ec.smp.api.exceptions.MalformedIdentifierException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.trim;

/**
 * Configurable formatter for parsing and serializing identifiers.
 * <p>
 * Example for formatTemplate  "${" + SPLIT_GROUP_SCHEME_NAME + "}:${" + SPLIT_GROUP_SCHEME_NAME + "}";
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class TemplateFormatterType implements FormatterType {
    private static final Logger LOG = LoggerFactory.getLogger(TemplateFormatterType.class);
    public static final String SPLIT_GROUP_SCHEME_NAME = "scheme";
    public static final String SPLIT_GROUP_IDENTIFIER_NAME = "identifier";
    protected static final String[] REPLACE_TAGS = new String[]{"${" + SPLIT_GROUP_SCHEME_NAME + "}", "${" + SPLIT_GROUP_IDENTIFIER_NAME + "}"};

    private final Pattern splitRegularExpression;
    private final Pattern schemaPattern;
    private final String formatTemplate;

    public TemplateFormatterType(Pattern matchSchema, String formatTemplate, Pattern splitRegularExpression) {
        this.schemaPattern = matchSchema;
        this.formatTemplate = formatTemplate;
        this.splitRegularExpression = splitRegularExpression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTypeByScheme(final String scheme) {
        if (StringUtils.isBlank(scheme)) {
            LOG.debug("TemplateFormatterType does not support identifiers with Null/Blank scheme");
            return false;
        }
        Matcher matcher = schemaPattern.matcher(scheme);
        return matcher.matches();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isType(final String value) {
        if (StringUtils.isBlank(value)) {
            LOG.debug("Formatter does not support Null/Blank identifiers ");
            return false;
        }
        Matcher matcher = schemaPattern.matcher(value);
        return matcher.matches();
    }

    @Override
    public String format(String scheme, String identifier, boolean noDelimiterOnEmptyScheme) {
        return StringUtils.replaceEach(formatTemplate, REPLACE_TAGS, new String[]{scheme, identifier});

    }

    @Override
    public String format(final String scheme, final String identifier) {
        return format(scheme, identifier, false);
    }

    @Override
    public String[] parse(final String value) {
        String partyIdPrivate = value.trim();
        Matcher matcher = splitRegularExpression.matcher(trim(partyIdPrivate));
        if (!matcher.matches()) {
            throw new MalformedIdentifierException("Identifier: [" + partyIdPrivate + "] does not match regular expression [" + splitRegularExpression.pattern() + "]");
        }
        return new String[]{
                getGroupByName(matcher, SPLIT_GROUP_SCHEME_NAME),
                getGroupByName(matcher, SPLIT_GROUP_IDENTIFIER_NAME)
        };
    }

    private String getGroupByName(Matcher matcher, String groupName) {
        String result = null;
        try {
            result = matcher.group(groupName);
        } catch (IllegalArgumentException arg) {
            LOG.debug("Group [{}] was not found for pattern: [{}].", groupName, matcher.pattern());
        }
        return result;
    }
}
