package eu.europa.ec.edelivery.smp.ui.external;

import eu.europa.ec.edelivery.smp.data.ui.NavigationTreeNodeRO;
import eu.europa.ec.edelivery.smp.data.ui.SearchUserRO;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.services.ui.UIUserService;
import eu.europa.ec.edelivery.smp.ui.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.*;
import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_PUBLIC_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserControllerTest extends AbstractControllerTest {
    private static final String PATH = CONTEXT_PATH_PUBLIC_USER;

    @Autowired
    protected UIUserService uiUserService;

    @BeforeEach
    public void setup() throws IOException {
        super.setup();
    }


    @Test
    public void testGetUserNavigationTreeForSystemAdmin() throws Exception {

        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        MvcResult response = mvc.perform(get(PATH + "/{user-id}/navigation-tree", userRO.getUserId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        NavigationTreeNodeRO result = getObjectFromResponse(response, NavigationTreeNodeRO.class);

        assertNotNull(result);
        assertEquals(4, result.getChildren().size());
        List<String> childrenNames = result.getChildren().stream().map(NavigationTreeNodeRO::getName).collect(Collectors.toList());
        assertEquals(Arrays.asList("Search", "Administration", "System settings", "User Settings"), childrenNames);
    }

    @Test
    public void testGetUserNavigationTreeForUser() throws Exception {

        MockHttpSession session = loginWithUser2(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        MvcResult response = mvc.perform(get(PATH + "/{user-id}/navigation-tree", userRO.getUserId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        NavigationTreeNodeRO result = getObjectFromResponse(response, NavigationTreeNodeRO.class);

        assertNotNull(result);
        assertEquals(3, result.getChildren().size());
        List<String> childrenNames = result.getChildren().stream().map(NavigationTreeNodeRO::getName).collect(Collectors.toList());
        assertEquals(Arrays.asList("Search", "Administration", "User Settings"), childrenNames);
    }

    @Test
    public void testLookupUsers() throws Exception {
        MockHttpSession session = loginWithUser2(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        MvcResult response = mvc.perform(get(PATH + "/{user-id}/search", userRO.getUserId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        List<SearchUserRO> result = getArrayFromResponse(response, SearchUserRO.class);

        assertNotNull(result);
        assertTrue(result.size() > 5);
    }

    @Test
    public void testLookupUsersFilter() throws Exception {
        MockHttpSession session = loginWithUser2(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        MvcResult response = mvc.perform(get(PATH + "/{user-id}/search", userRO.getUserId()).param("filter", userRO.getUsername())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        List<SearchUserRO> result = getArrayFromResponse(response, SearchUserRO.class);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userRO.getUsername(), result.get(0).getUsername());
    }
}
