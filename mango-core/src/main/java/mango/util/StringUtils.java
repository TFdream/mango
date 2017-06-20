package mango.util;

import java.nio.charset.Charset;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class StringUtils {

    public static final String EMPTY = "";

    public static final Charset UTF8 = Charset.forName("UTF-8");

    public static String getString(byte[] data) {
        return getString(data, UTF8);
    }
    public static String getString(byte[] data, Charset charset) {
        return new String(data, charset);
    }

    public static byte[] getBytes(String str) {
        return getBytes(str, UTF8);
    }

    public static byte[] getBytes(String str, Charset charset) {
        return str.getBytes(charset);
    }


    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(final CharSequence str) {
        return !isEmpty(str);
    }

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean equals(String str1, String str2) {
        return str1 == null?str2 == null:str1.equals(str2);
    }

    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 == null?str2 == null:str1.equalsIgnoreCase(str2);
    }

    public static boolean contains(String[] arr, String searchStr) {
        if (arr == null || searchStr == null) {
            return false;
        }
        for(String str : arr) {
            if(searchStr.equals(str)){
                return true;
            }
        }
        return false;
    }

    /**
     * StringUtils.capitalize(null)  = null
     * StringUtils.capitalize("")    = ""
     * StringUtils.capitalize("cat") = "Cat"
     * StringUtils.capitalize("cAt") = "CAt"
     * StringUtils.capitalize("'cat'") = "'cat'"
     *
     * @param str
     * @return
     */
    public static String capitalize(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        final char firstChar = str.charAt(0);
        final char newChar = Character.toTitleCase(firstChar);
        if (firstChar == newChar) {
            // already capitalized
            return str;
        }

        char[] newChars = new char[strLen];
        newChars[0] = newChar;
        str.getChars(1,strLen, newChars, 1);
        return String.valueOf(newChars);
    }
}
