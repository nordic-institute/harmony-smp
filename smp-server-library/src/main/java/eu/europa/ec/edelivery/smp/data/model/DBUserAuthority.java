package eu.europa.ec.edelivery.smp.data.model;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@NamedNativeQueries({
        @NamedNativeQuery(
                name = "DBUserAuthority.getRolesForUsernameNativeQuery",
                query = "SELECT 'SMP_ADMIN' AS AUTHORITY FROM smp_user WHERE isadmin = 1 and username=:username " +
                        "UNION ALL " +
                        "SELECT CONCAT(businessIdentifierScheme, CONCAT('::', businessIdentifier)) AS AUTHORITY FROM smp_ownership  WHERE username=:username",
                resultSetMapping = "RoleDTO"
        )})

@SqlResultSetMapping(
        name = "RoleDTO",
        classes = @ConstructorResult
                (
                        targetClass = DBUserAuthority.class,
                        columns = {
                                @ColumnResult(name = "authority", type = String.class)
                        }
                )
)
public class DBUserAuthority implements GrantedAuthority {

    public static DBUserAuthority S_ROLE_SERVICEGROUP_ADMIN = new DBUserAuthority("ROLE_SERVICEGROUP_ADMIN");


    public DBUserAuthority(String authority) {
        this.authority = authority;
    }

    String authority;

    @Override
    public String getAuthority() {
        return authority;
    }
}
