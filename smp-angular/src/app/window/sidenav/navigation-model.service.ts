import {MatTreeNestedDataSource} from "@angular/material/tree";
import {Injectable} from "@angular/core";

/**
 * The smp navigation tree
 */


let NAVIGATION_TREE: NavigationNode[] = [
  {
    code: "search-tools",
    name: "Search",
    icon: "search",
    tooltip: "Search tools",
    routerLink: "/",
    children: [
      {
        code: "search-resources",
        name: "Resources",
        icon: "find_in_page",
        tooltip: "Search registered resources",
        routerLink: "/",

      },
      {
        code: "search-lookup",
        name: "DNS lookup",
        icon: "dns",
        tooltip: "DNS lookup tool",
        routerLink: "/dns-lookup",
      }
    ]
  },

  {
    code: "admin-domains",
    name: "Domain Admin",
    icon: "domain",
    routerLink: "/domain",
    children: [
      {
        code: "admin-domains-settings",
        name: "Settings",
        icon: "settings",
      },
      {
        code: "admin-domains-members",
        name: "Members",
        icon: "person",
      },
      {
        code: "admin-domains-groups",
        name: "Groups",
        icon: "group",
      },
      {
        code: "admin-domains-resource-types",
        name: "Resource types",
        icon: "folder"
      }
    ]
  },
  {
    code: "admin-group",
    name: "Group Admin",
    icon: "group",
    children: [
      {
        code: "admin-group-settings",
        name: "Settings",
        icon: "settings",
      },
      {
        code: "admin-group-members",
        name: "Members",
        icon: "person",
      },
      {
        code: "admin-group-resources",
        name: "Resources",
        icon: "description"
      }
    ]
  },
  {
    code: "admin-resources",
    name: "Resources Admin",
    icon: "edit_document",
    children: [
      {
        code: "admin-resource-settings",
        name: "Settings",
        icon: "settings"
      },
      {
        code: "admin-resource-members",
        name: "Members",
        icon: "person",
      },
      {
        code: "admin-subresources",
        name: "Resources",
        icon: "file-open"
      }
    ]
  },
  {
    code: "system-admin",
    name: "System settings",
    icon: "settings",
    children: [
      {
        code: "system-admin-domain",
        name: "Domain",
        icon: "domain",
        routerLink: "/domain",
      },
      {
        code: "system-admin-users",
        name: "Users",
        icon: "people",
        routerLink: "/users",
      },
      {
        code: "system-admin-authentication",
        name: "Authentication",
        icon: "shield",
      },
      {
        code: "system-admin-properties",
        name: "Properties",
        icon: "shield",
      },
      {
        code: "system-admin-keystore",
        name: "Keystore"
      },
      {
        code: "system-admin-truststore",
        name: "Truststore"
      },
      {
        code: "system-admin-extensions",
        name: "Extensions",
        icon: "extension"
      }
    ]
  },
  {
    code: "alerts",
    name: "Alerts",
    icon: "notifications",
  },
  {
    code: "user-data",
    name: "User profile",
    icon: "account_circle",
    children: [
      {
        code: "user-data-profile",
        name: "User profile"
      },
      {
        code: "user-data-access-token",
        name: "Access tokens"
      },
      {
        code: "user-data-certificates",
        name: "Certificates"
      },
      {
        code: "user-data-membership",
        name: "Membership"
      }
    ]
  },
];


/**
 * Food data with nested structure.
 * Each node has a name and an optional list of children.
 */
export interface NavigationNode {
  code: string;
  name: string;
  icon?: string;
  tooltip?: string;
  routerLink?: string;
  children?: NavigationNode[];

  selected?: boolean;
}

@Injectable()
export class NavigationModel extends MatTreeNestedDataSource<NavigationNode> {

  selected: NavigationNode;

  selectedPath: NavigationNode[];

  constructor() {
    super();
    this.data = NAVIGATION_TREE;
  }

  select(node: NavigationNode) {
    this.selected = node;
    const rootNode = {code: "home", name: "Home", icon: "home", children: this.data};
    this.selectedPath = this.findPathForNode(node, rootNode);

  }

  /** Add node as child of parent */
  public add(node: NavigationNode, parent: NavigationNode) {
    // add root node
    const rootNode = {code: "home", name: "Home", icon: "home", children: this.data};
    this._add(node, parent, rootNode);
    this.data = rootNode.children;
  }

  /** Remove node from tree */
  public remove(node: NavigationNode) {
    const newTreeData = {code: "home", name: "Home", icon: "home", children: this.data};
    this._remove(node, newTreeData);
    this.data = newTreeData.children;
  }

  /*
   * For immutable update patterns, have a look at:
   * https://redux.js.org/recipes/structuring-reducers/immutable-update-patterns/
   */

  protected _add(newNode: NavigationNode, parent: NavigationNode, tree: NavigationNode) {
    if (tree === parent) {
      console.log(
        `replacing children array of '${parent.name}', adding ${newNode.name}`
      );
      tree.children = [...tree.children!, newNode];
      return true;
    }
    if (!tree.children) {
      console.log(`reached leaf node '${tree.name}', backing out`);
      return false;
    }
    return this.update(tree, this._add.bind(this, newNode, parent));
  }

  _remove(node: NavigationNode, tree: NavigationNode): boolean {
    if (!tree.children) {
      return false;
    }
    const i = tree.children.indexOf(node);
    if (i > -1) {
      tree.children = [
        ...tree.children.slice(0, i),
        ...tree.children.slice(i + 1)
      ];
      console.log(`found ${node.name}, removing it from`, tree);
      return true;
    }
    return this.update(tree, this._remove.bind(this, node));
  }

  protected update(tree: NavigationNode, predicate: (n: NavigationNode) => boolean) {
    let updatedTree: NavigationNode, updatedIndex: number;

    tree.children!.find((node, i) => {
      if (predicate(node)) {
        console.log(`creating new node for '${node.name}'`);
        updatedTree = {...node};
        updatedIndex = i;
        return true;
      }
      return false;
    });

    if (updatedTree!) {
      console.log(`replacing node '${tree.children![updatedIndex!].name}'`);
      tree.children![updatedIndex!] = updatedTree!;
      return true;
    }
    return false;
  }

  protected findPathForNode(targetNode: NavigationNode, parentNode: NavigationNode): NavigationNode[] {
    console.log("Search for parent node: " + parentNode.name );
    if (!parentNode.children) {
      console.log("Got parent node with no children: " + parentNode.name + " return null");
      return null;
    }

    const index = parentNode.children.indexOf(targetNode);
    if (index > -1) {
      // got target return initial array
      console.log("Got target node: " + targetNode.name);
      return [parentNode, targetNode ];
    }

    for (const child of parentNode.children) {
      let result = this.findPathForNode(targetNode, child);
      if (result) {
        console.log("Add parent node: " + parentNode.name);

        return [parentNode, ...result];
      }
    }
    return null;
  }

}
