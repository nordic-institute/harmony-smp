import {SearchTableEntity} from "../search-table/search-table-entity.model";
import {VisibilityEnum} from "../enums/visibility.enum";

/**
 * ResourceRo interface for resource data
 *
 * @since 5.0
 * @author  Joze RIHTARSIC
 */
export interface ResourceRo extends SearchTableEntity {

  resourceId?: string;
  resourceTypeIdentifier?: string;
  identifierValue: string;
  identifierScheme?: string;

  smlRegistered?: boolean;
  reviewEnabled?: boolean;
  hasCurrentUserReviewPermission?: boolean;
  visibility?: VisibilityEnum;
}
