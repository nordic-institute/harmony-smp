export enum VisibilityEnum {
  /**
   * Resource, group of domain is marked as PUBLIC.
   */
  Public= 'PUBLIC',
  /**
   * Access to the resource is within the domain/group. Users must be authenticated and must be members of the domain/group/resource in order to read it.
   */
  Internal= 'INTERNAL',
  /**
   *  Access to the resource is possible only to the resource members
   */
  Private= 'PRIVATE'
}
