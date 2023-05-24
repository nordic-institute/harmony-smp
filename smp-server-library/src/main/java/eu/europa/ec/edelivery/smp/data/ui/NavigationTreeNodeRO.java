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
    private List<NavigationTreeNodeRO> children = new ArrayList<>();

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
