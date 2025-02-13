import {SearchTableEntity} from "../search-table/search-table-entity.model";

/**
 * SubresourceRo interface for subresource data
 *
 * @since 5.0
 * @author  Joze RIHTARSIC
 */
export interface SubresourceRo extends SearchTableEntity {
  subresourceId?: string;
  subresourceTypeIdentifier?: string;
  identifierValue: string;
  identifierScheme?: string;
}
