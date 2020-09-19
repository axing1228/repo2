package com.sz.fts.utils.liuliang;

public class LLStringUtils
{
  public static final String EMPTY = "";
  public static final int INDEX_NOT_FOUND = -1;

  public static boolean isEmpty(String str)
  {
    return ((str == null) || (str.length() == 0));
  }

  public static boolean isNotEmpty(String str)
  {
    return (!(isEmpty(str)));
  }

  public static boolean isBlank(String str)
  {
    int strLen;
    if ((str == null) || ((strLen = str.length()) == 0)) {
      return true;
    }
    for (int i = 0; i < strLen; ++i) {
      if (!(Character.isWhitespace(str.charAt(i)))) {
        return false;
      }
    }
    return true;
  }

  public static boolean isNotBlank(String str)
  {
    return (!(isBlank(str)));
  }

  /** @deprecated */
  public static String clean(String str)
  {
    return ((str == null) ? "" : str.trim());
  }

  public static String trim(String str)
  {
    return ((str == null) ? null : str.trim());
  }

  public static String trimToNull(String str)
  {
    String ts = trim(str);
    return ((isEmpty(ts)) ? null : ts);
  }

  public static String trimToEmpty(String str)
  {
    return ((str == null) ? "" : str.trim());
  }

  public static String substringAfter(String str, String separator)
  {
    if (isEmpty(str)) {
      return str;
    }
    if (separator == null) {
      return "";
    }
    int pos = str.indexOf(separator);
    if (pos == -1) {
      return "";
    }
    return str.substring(pos + separator.length());
  }
  }