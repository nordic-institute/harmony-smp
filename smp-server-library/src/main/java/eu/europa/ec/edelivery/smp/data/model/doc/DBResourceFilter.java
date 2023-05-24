package eu.europa.ec.edelivery.smp.data.model.doc;

import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.StringUtils.wrapIfMissing;

public class DBResourceFilter {
    private static final List<MembershipRoleType> ALL_ROLES = Arrays.asList(MembershipRoleType.values());
    DBUser user;
    List<MembershipRoleType> membershipRoleType;
    DBResourceDef resourceDef;
    DBGroup group;
    DBDomain domain;

    String identifierFilter;

    protected DBResourceFilter(Builder builder) {
        this.user = builder.user;
        this.membershipRoleType = builder.membershipRoleType == null || builder.membershipRoleType.isEmpty() ? ALL_ROLES : builder.membershipRoleType;
        this.resourceDef = builder.resourceDef;
        this.group = builder.group;
        this.domain = builder.domain;
        this.identifierFilter = builder.identifierFilter;
    }

    public String getIdentifierFilter() {
        return identifierFilter;

    }

    public DBUser getUser() {
        return user;
    }

    public List<MembershipRoleType> getMembershipRoleTypes() {
        return membershipRoleType;
    }

    public DBResourceDef getResourceDef() {
        return resourceDef;
    }


    public DBGroup getGroup() {
        return group;
    }

    public DBDomain getDomain() {
        return domain;
    }


    public Long getResourceDefId() {
        return resourceDef == null ? null : resourceDef.getId();
    }

    public Long getGroupId() {
        return group == null ? null : group.getId();
    }

    public Long getDomainId() {
        return domain == null ? null : domain.getId();
    }

    public Long getUserId() {
        return user == null ? null : user.getId();
    }


    @Override
    public String toString() {
        return "DBResourceFilter{" +
                "user=" + user +
                ", membershipRoleType=" + membershipRoleType +
                ", resourceDef=" + resourceDef +
                ", group=" + group +
                ", domain=" + domain +
                ", identifierFilter='" + identifierFilter + '\'' +
                '}';
    }

    public static Builder createBuilder() {
        return new Builder();
    }


    public static class Builder {
        DBUser user;
        List<MembershipRoleType> membershipRoleType = new ArrayList<>();
        DBResourceDef resourceDef;
        DBGroup group;
        DBDomain domain;
        String identifierFilter;

        public Builder user(DBUser user) {
            this.user = user;
            return this;
        }

        public Builder membershipRoleType(MembershipRoleType... membershipRoleType) {
            this.membershipRoleType = Arrays.asList(membershipRoleType);
            return this;
        }

        public Builder resourceDef(DBResourceDef resourceDef) {
            this.resourceDef = resourceDef;
            return this;
        }

        public Builder group(DBGroup group) {
            this.group = group;
            return this;
        }

        public Builder domain(DBDomain domain) {
            this.domain = domain;
            return this;
        }

        public Builder identifierFilter(String identifierFilter) {
            this.identifierFilter = wrapIfMissing(trim(identifierFilter), "%");
            return this;
        }

        public DBResourceFilter build() {

            return new DBResourceFilter(this);
        }


    }


}
