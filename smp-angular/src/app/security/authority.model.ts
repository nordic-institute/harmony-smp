/**
 * Note the difference between authority and Role. User with one role could have multiple authorities.
 * At the moment role and authorites matches - but this can change in the future.
 */

export enum Authority {
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
