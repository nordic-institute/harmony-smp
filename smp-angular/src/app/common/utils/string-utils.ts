/**
 * String utils
 */
export default class StringUtils {
  /**
   * Capitalize the first letter of the string
   * @param value
   * @returns {string} Capitalized string
   */
  static capitalizeFirst(value: string): string {
    if (value === null) {
      return 'Not assigned';
    }
    return value.charAt(0).toUpperCase() + value.slice(1).toLowerCase();
  }

  /**
   * Format string
   * @param format - the format as example: 'Hello {1} {0}'
   * @param parameters - the parameters as example: ['Janez', 'Novak']
   * @returns {string} Formatted string as example: 'Hello Novak Janez'
   */
  static format(format: string, parameters: string[]): string {
    if (parameters) {
      format = format.replace(/\{([^}]+)}/g, function (match, key) {
        return (parameters != null && key in parameters) ? parameters[key] : match;
      });
    }
    return format;
  }

  static isEmpty(str): boolean {
    return (!str || 0 === str.length);
  }
}
