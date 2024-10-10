import {PropertyValueTypeEnum} from "../property-value-type.enum";

/**
 *  Utility class for PropertyValueTypeEnum to return description, regular expression
 *  and input type for each enum value.
 *
 *  @since 5.1
 *  @author Joze Rihtarsic
 */
export class PropertyValueTypeEnumUtil {

  // property name pattern. It must start with a letter and can contain letters, numbers and dots and must not be bigger than 255 characters long!
  public static readonly PROPERTY_NAME_PATTERN = '^[a-zA-Z][a-zA-Z0-9.]{0,254}$';

  static getKeyNames(): Array<string> {
    return Object.keys(PropertyValueTypeEnum).filter(k => typeof PropertyValueTypeEnum[k as any] === "number");
  }

  static getKeyName(enumItem: PropertyValueTypeEnum): string {
    return PropertyValueTypeEnum[enumItem];
  }

  static getDescription(enumItem: PropertyValueTypeEnum): string {
    switch (enumItem) {
      case PropertyValueTypeEnum.STRING:
        return 'String';
      case PropertyValueTypeEnum.LIST_STRING:
        return 'List of strings';
      case PropertyValueTypeEnum.MAP_STRING:
        return 'Map of strings';
      case PropertyValueTypeEnum.FILENAME:
        return 'Filename';
      case PropertyValueTypeEnum.PATH:
        return 'Path';
      case PropertyValueTypeEnum.INTEGER:
        return 'Integer';
      case PropertyValueTypeEnum.BOOLEAN:
        return 'Boolean';
      case PropertyValueTypeEnum.REGEXP:
        return 'Regular expression';
      case PropertyValueTypeEnum.EMAIL:
        return 'Email';
      case PropertyValueTypeEnum.URL:
        return 'URL';
      default:
        return '';
    }
  }

  static getInputType(propertyType: PropertyValueTypeEnum): string {
    console.log("Get input type for row " + propertyType)
    switch (propertyType) {
      case PropertyValueTypeEnum.STRING:
      case PropertyValueTypeEnum.LIST_STRING:
      case PropertyValueTypeEnum.MAP_STRING:
      case PropertyValueTypeEnum.FILENAME:
      case PropertyValueTypeEnum.PATH:
      case PropertyValueTypeEnum.REGEXP:
        return 'text';
      case PropertyValueTypeEnum.INTEGER:
        return 'number';
      case PropertyValueTypeEnum.BOOLEAN:
        return 'checkbox';
      case PropertyValueTypeEnum.EMAIL:
        return 'email';
      case PropertyValueTypeEnum.URL:
        return 'url';
      default:
        return 'text';
    }
  }

  static getRegExp(enumItem: PropertyValueTypeEnum): RegExp {
    console.log("Get input pattern for row " + enumItem)
    switch (enumItem) {
      case PropertyValueTypeEnum.STRING:
      case PropertyValueTypeEnum.LIST_STRING:
      case PropertyValueTypeEnum.MAP_STRING:
      case PropertyValueTypeEnum.FILENAME:
        return /.*/
      case PropertyValueTypeEnum.PATH:
        return /^(.+)\/([^\/]+)$/;
      case PropertyValueTypeEnum.INTEGER:
        return /^-?\d+$/;
      case PropertyValueTypeEnum.BOOLEAN:
        return /^(true|false)$/i;
      case PropertyValueTypeEnum.REGEXP:
        // This regular expression checks if a string could be a valid RegExp
        return /^\/(.*)\/([gimuy]*)$/;
      case PropertyValueTypeEnum.EMAIL:
        return /^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]{2,7}$/;
      case PropertyValueTypeEnum.URL:
        return /^(http|https):\/\/[^ "]+$/;
      default:
        return null;
    }
  }


}
