/*-
 * #START_LICENSE#
 * smp-server-library
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
package eu.europa.ec.edelivery.smp.data.ui;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class NavigationTreeNodeRO extends BaseRO {

    private static final long serialVersionUID = 9008583888835630011L;

    private String code;
    private String name;
    private String icon;
    private String tooltip;
    private String routerLink;
    private final List<NavigationTreeNodeRO> children = new ArrayList<>();

    public NavigationTreeNodeRO() {
    }

    public NavigationTreeNodeRO(String code, String name, String icon, String routerLink) {
        this(code, name, icon, routerLink, null);

    }

    public NavigationTreeNodeRO(String code, String name, String icon, String routerLink, String tooltip) {
        this.code = code;
        this.name = name;
        this.icon = icon;
        this.routerLink = routerLink;
        this.tooltip = tooltip;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getRouterLink() {
        return routerLink;
    }

    public void setRouterLink(String routerLink) {
        this.routerLink = routerLink;
    }

    public List<NavigationTreeNodeRO> getChildren() {
        return children;
    }


    public  void addChild(NavigationTreeNodeRO nodeRO) {
        children.add(nodeRO);
    }

}
