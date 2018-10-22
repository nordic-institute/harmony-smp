export enum Role {
  /**
   * The system administrator (a.k.a. the "super admin") role
   */
  SYSTEM_ADMIN = 'ROLE_SYSTEM_ADMIN',
  /**
   * The SMP Administrator role. It is assimilable to the {@link SERVICE_GROUP_ADMINISTRATOR} role for now.
    */
  SMP_ADMIN = 'ROLE_SMP_ADMIN',
  /**
   * The ServiceGroup administrator role
   */
  SERVICE_GROUP_ADMIN = 'ROLE_SERVICE_GROUP_ADMIN',
}
