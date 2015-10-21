package eu.domibus.submission.routing;

import eu.domibus.common.model.AbstractBaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract Class of Routing Criteria
 * <p/>
 * Created by walcz01 on 31.07.2015.
 */
@Entity
@Table(name = "TB_ROUTING_CRITERIA")
public abstract class AbstractRoutingCriteria extends AbstractBaseEntity implements IRoutingCriteria {

    @Column(name = "NAME")
    private String name;
    @Column(name = "EXPRESSION")
    private String expression;
    @Transient
    private HashMap<String, Pattern> patternHashMap;

    /**
     * Standard Constructor
     *
     * @param name of Routing Criteria
     */
    public AbstractRoutingCriteria(String name) {
        this.name = name;
        patternHashMap = new HashMap<>();
    }

    protected AbstractRoutingCriteria() {
    }


    @Override
    public String getExpression() {
        return expression;
    }

    @Override
    public void setExpression(String expression) {
        this.expression = expression;
    }

    private Pattern getPattern(String expression) {
        if (patternHashMap.containsKey(expression)) {
            return patternHashMap.get(expression);
        } else {
            Pattern pattern = Pattern.compile(expression);
            patternHashMap.put(expression, pattern);
            return pattern;
        }

    }

    public boolean matches(String candidate) {

        Matcher m = getPattern(expression).matcher(candidate);

        return m.matches();
    }

    @Override
    public String getName() {
        return name;
    }

}
