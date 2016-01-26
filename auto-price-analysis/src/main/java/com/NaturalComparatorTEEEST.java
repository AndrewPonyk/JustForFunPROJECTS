package com;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by andrii on 26.01.16.
 */
public class NaturalComparatorTEEEST {
    public static void main(String[] args) {
        System.out.println("Test comparator");
    }
}

/**
 * Strings are sorted as usual, except that decimal integer substrings are
 * compared on their numeric value. For example, a < a0 < a1 < a1a < a1b < a2 <
 * a10 < a20
 */
public class NaturalOrderComparator extends DataItemsComparator {

    @Override
    public int compare(Object val1, Object val2, int order, boolean nullsLast, boolean caseSensitive, boolean exactMatch, boolean useWildcards) {
        int ret = 0;

        if (val1 != null && val2 != null && val1 instanceof String && exactMatch) {
            if (caseSensitive) {
                ret = naturalOrderCompareString((String) val1,(String) val2);
            } else {
                ret = naturalOrderCompareString(((String) val1).toUpperCase(),((String) val2).toUpperCase());
            }
        }
        else {
            ret = -1;
            //ret = super.compare(val1, val2, order, nullsLast, caseSensitive, exactMatch, useWildcards);
        }

        return ret;
    }

    private int naturalOrderCompareString(String leftStr, String rightStr) {
        if (leftStr.length() == 0 && rightStr.length() == 0) {
            return 0;
        }
        if (leftStr.length() == 0) {
            return -1;
        }
        if (rightStr.length() == 0) {
            return 1;
        }
        int result = 0;
        int i = 0;
        while (i < Math.min(leftStr.length(), rightStr.length()) && result == 0) {
            if (Character.isDigit(leftStr.charAt(i)) && Character.isDigit(rightStr.charAt(i)) && leftStr.charAt(i) != '0' && rightStr.charAt(i) != '0') {
                BigInteger parsedLeftInt = parseInt(leftStr, i);
                BigInteger parseRightInt = parseInt(rightStr, i);
                if (parsedLeftInt.equals(parseRightInt)) {
                    i += String.valueOf(parsedLeftInt).length();
                } else {
                    result = parsedLeftInt.compareTo(parseRightInt);
                }
            } else {
                result = leftStr.charAt(i) - rightStr.charAt(i);
                i++;
            }
        }

        if (result == 0) {
            result = leftStr.compareTo(rightStr);
        }
        return result;
    }

    private BigInteger parseInt(String leftStr, int startWith) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(leftStr);
        m.find(startWith);
        System.out.println(" ======" + leftStr + "=======" + startWith + "===" + new BigInteger(m.group()));
        return new BigInteger(m.group());
    }

    private BigInteger parseInt(char[] stringToParse, int startWith) { // // We know the first character is a digit (and not zero), so start checking on the next one //
        int endChar = startWith + 1;
        for (; endChar < stringToParse.length; endChar++) {
            if (!Character.isDigit(stringToParse[endChar])) {
                break;
            }
        }

        return new BigInteger(new String(stringToParse, startWith, endChar - startWith));

    }

}