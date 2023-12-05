package eu.europa.ec.edelivery.smp.data.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Enum for membership role types. At the moment there are two types of roles:
 * <ul>
 *     <li>VIEWER - user is the readonly member of the the domain.</li>
 *     <li>ADMIN - user is administrator of the domain and can view and edit the domain data.</li>
 * </ul>
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public enum MembershipRoleType {
    VIEWER,
    ADMIN
    ;

    /**
     * Method to convert MembershipRoleType to List<MembershipRoleType>. if roleTypes is null or empty,
     * all values are returned as list
     *
     * @param roleTypes - list of role types
     * @return list of role types
     */
    public static List<MembershipRoleType> toList(MembershipRoleType ... roleTypes){
        return Arrays.asList(roleTypes ==null || roleTypes.length==0 ?values(): roleTypes);
    }
}
