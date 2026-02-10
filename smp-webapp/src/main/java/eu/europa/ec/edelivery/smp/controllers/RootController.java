/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */

package eu.europa.ec.edelivery.smp.controllers;

import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.logging.SMPMessageCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.endsWithIgnoreCase;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;


@RestController
@RequestMapping("/")
@Order(HIGHEST_PRECEDENCE)
public class RootController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(RootController.class);

    private final Map<String, String[]> STATIC_RESOURCES = Collections.unmodifiableMap(new HashMap() {{
        put("domismp.css", new String[]{"text/css", "/html/styles/domismp.css"});
        put("smp.js", new String[]{"application/javascript", "/html/scripts/smp.js"});
        put("DomiSMP_logo.svg", new String[]{"image/svg+xml", "/html/images/DomiSMP_logo.svg"});
        put("EC+Logo2.png", new String[]{"image/png", "/html/images/EC+Logo2.png"});
        put("oasis-smp-1.png", new String[]{"image/png", "/html/images/oasis-smp-1.png"});
        put("oasis-smp-2.png", new String[]{"image/png", "/html/images/oasis-smp-2.png"});
        put("favicon.ico", new String[]{"image/x-ico", "/html/images/favicon.ico"});
    }});

    /**
     * redirect if / to index.html.
     *
     * @param model Spring MVC model
     * @return ModelAndView the redirect to index.html
     */
    @GetMapping(value = { "index.html"})
    public ModelAndView redirectOldIndexPath(ModelMap model) {
        return new ModelAndView("redirect:/", model);
    }

    @GetMapping(produces = {MediaType.TEXT_HTML_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            "text/css",
            "application/javascript",
            "image/ico",
            "image/x-ico",
            "image/svg+xml"
    }, value = {"/",
            "/images/DomiSMP_logo.svg",
            "/images/EC+Logo2.png",
            "/images/oasis-smp-1.png",
            "/images/oasis-smp-2.png",
            "/images/favicon.ico",
            "/styles/domismp.css",
            "/scripts/smp.js"})
    @ResponseBody
    public ResponseEntity<InputStreamResource> getStaticResources(HttpServletRequest httpReq) {
        String host = getRemoteHost(httpReq);
        LOG.businessInfo(SMPMessageCode.BUS_HTTP_GET_END_STATIC_CONTENT, host, httpReq.getServletPath());
        String path = httpReq.getRequestURI();
        String[] resourcePath = STATIC_RESOURCES.entrySet().stream()
                .filter(entry -> endsWithIgnoreCase(path, entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(new String[]{MediaType.TEXT_HTML_VALUE, "/html/index.html"});

        MediaType contentType = MediaType.parseMediaType(resourcePath[0]);
        InputStream contentStream = RootController.class.getResourceAsStream(resourcePath[1]);
        if (contentStream == null) {
            LOG.securityWarn(SMPMessageCode.BUS_HTTP_GET_END_STATIC_CONTENT_NOT_FOUND, host, path);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(contentType)
                .body(new InputStreamResource(contentStream));

    }

    /**
     * redirect angular pages to index.html
     * solve the 404 error on refresh
     *
     * @param model
     * @return
     */
    //@GetMapping(value={"/ui","/ui/edit","/ui/search","/ui/search","/ui/domain","/ui/user"})
    @GetMapping(value = {"/ui"})
    public ModelAndView redirectWithUsingRedirectPrefix(ModelMap model) {
        return new ModelAndView("redirect:/ui/", model);
    }

    public String getRemoteHost(HttpServletRequest httpReq) {
        String host = httpReq.getHeader("X-Forwarded-For");
        return StringUtils.isBlank(host) ? httpReq.getRemoteHost() : host;
    }
}
