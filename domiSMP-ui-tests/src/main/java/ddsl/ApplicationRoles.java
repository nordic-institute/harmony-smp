package ddsl;

import java.util.Arrays;
import java.util.List;

public class ApplicationRoles {
    public static final String SYSTEM_ADMIN = "SYSTEM_ADMIN";
    public static final String USER = "USER";

    public static List<String> userRoleValues() {
        return Arrays.asList(new String[]{"SYSTEM_ADMIN", "ROLE_ADMIN", "ROLE_USER"});
    }
}
