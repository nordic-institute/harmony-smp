/**
 * Object utils
 */
export default class ObjectUtils {
  static isEqual(val1, val2): boolean {
    return (this.isEmpty(val1) && this.isEmpty(val2)
      || val1 === val2);
  }

  static isEmpty(str): boolean {
    return (!str || 0 === str.length);
  }
}
