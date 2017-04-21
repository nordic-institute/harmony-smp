package eu.europa.ec.cipa.smp.server.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by gutowpa on 07/04/2017.
 */

@Controller
public class UsernameReturningTestController {

    @RequestMapping("/getLoggedUsername")
    @ResponseBody
    public String getLoggedUsername(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
