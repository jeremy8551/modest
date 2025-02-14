package cn.org.expect.extensions.java.lang.String;

import java.text.Format;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;

import cn.org.expect.util.StringUtils;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public final class StringExt {

    public static boolean isBlank(@This String str) {
        return StringUtils.isBlank(str);
    }

    public static int length(@This String str, String charsetName) {
        return StringUtils.length(str, charsetName);
    }

    public static int width(@This String str, String charsetName) {
        return StringUtils.width(str, charsetName);
    }

    public static String trim(@This String str, char... array) {
        return StringUtils.trim(str, array);
    }

    public static String trimBlank(@This String obj, char... array) {
        return StringUtils.trimBlank(obj, array);
    }

    public static String trimParenthes(@This String str) {
        return StringUtils.trimParenthes(str);
    }

    public static String rtrim(@This String str) {
        return StringUtils.rtrim(str);
    }

    public static String rtrim(@This String str, char... array) {
        return StringUtils.rtrim(str, array);
    }

    public static String rtrimBlank(@This String obj, char... array) {
        return StringUtils.rtrimBlank(obj, array);
    }

    public static String ltrim(@This String str) {
        return StringUtils.ltrim(str);
    }

    public static String ltrim(@This String str, char... array) {
        return StringUtils.ltrim(str, array);
    }

    public static String ltrimBlank(@This String obj, char... array) {
        return StringUtils.ltrimBlank(obj, array);
    }

    public static String replace(@This String str, int begin, int length, CharSequence replacement) {
        return StringUtils.replace(str, begin, length, replacement);
    }

    public static String replace(@This String str, String oldStr, String newStr) {
        return StringUtils.replace(str, oldStr, newStr);
    }

    public static String replaceAll(@This String str, String oldStr, String newStr) {
        return StringUtils.replaceAll(str, oldStr, newStr);
    }

    public static String replaceLast(@This String str, String oldStr, String newStr) {
        return StringUtils.replaceLast(str, oldStr, newStr);
    }

    public static String replaceEnvironment(@This String str, Map<String, String> map) {
        return StringUtils.replaceEnvironment(str, map);
    }

    public static String replaceEnvironment(@This String str) {
        return StringUtils.replaceEnvironment(str);
    }

    public static String replaceProperties(@This String str) {
        return StringUtils.replaceProperties(str);
    }

    public static String replaceProperties(@This String str, Properties p) {
        return StringUtils.replaceProperties(str, p);
    }

    public static String replaceVariable(@This String str, Map<String, Object> map, Format convert) {
        return StringUtils.replaceVariable(str, map, convert);
    }

    public static String replaceVariable(@This String str, Map<String, String> map) {
        return StringUtils.replaceVariable(str, map);
    }

    public static String replaceVariable(@This String str, CharSequence... array) {
        return StringUtils.replaceVariable(str, array);
    }

    public static String replaceHalfWidthChar(@This String str) {
        return StringUtils.replaceHalfWidthChar(str);
    }

    public static String replaceHalfWidthBlank(@This String str) {
        return StringUtils.replaceHalfWidthBlank(str);
    }

    public static String substring(@This String str, int begin, int length, String charsetName) {
        return StringUtils.substring(str, begin, length, charsetName);
    }

    public static String substring(@This String str, int index, int left, int right) {
        return StringUtils.substring(str, index, left, right);
    }

    public static String substr(@This String str, int index, int left, int right) {
        return StringUtils.substr(str, index, left, right);
    }

    public static String left(@This String obj, int length) {
        return StringUtils.left(obj, length);
    }

    public static String left(@This String obj, int length, char c) {
        return StringUtils.left(obj, length, c);
    }

    public static String left(@This String obj, int width, String charsetName) {
        return StringUtils.left(obj, width, charsetName);
    }

    public static String left(@This String obj, int width, String charsetName, char d) {
        return StringUtils.left(obj, width, charsetName, d);
    }

    public static String right(@This String obj, int length) {
        return StringUtils.right(obj, length);
    }

    public static String right(@This String obj, int length, char c) {
        return StringUtils.right(obj, length, c);
    }

    public static String right(@This String obj, int width, String charsetName) {
        return StringUtils.right(obj, width, charsetName);
    }

    public static String right(@This String obj, int width, String charsetName, char d) {
        return StringUtils.right(obj, width, charsetName, d);
    }

    public static String middle(@This String obj, int width, String charsetName, char d) {
        return StringUtils.middle(obj, width, charsetName, d);
    }

    public static String escape(@This String str) {
        return StringUtils.escape(str);
    }

    public static String escape(@This String str, char escape) {
        return StringUtils.escape(str, escape);
    }

    public static String escapeRegex(@This String regex) {
        return StringUtils.escapeRegex(regex);
    }

    public static String escapeLineSeparator(@This String str) {
        return StringUtils.escapeLineSeparator(str);
    }

    public static String unescape(@This String str) {
        return StringUtils.unescape(str);
    }

    public static String quote(@This String str) {
        return StringUtils.quote(str);
    }

    public static String quotes(@This String str) {
        return StringUtils.quotes(str);
    }

    public static String unquote(@This String str) {
        return StringUtils.unquote(str);
    }

    public static String unquotes(@This String str) {
        return StringUtils.unquotes(str);
    }

    public static String unquotation(@This String str) {
        return StringUtils.unquotation(str);
    }

    public static int indexOf(@This String str, CharSequence dest, int from, boolean ignoreCase) {
        return StringUtils.indexOf(str, dest, from, ignoreCase);
    }

    public static int indexOfBlank(@This String str, int from, int end) {
        return StringUtils.indexOfBlank(str, from, end);
    }

    public static int indexOfNotBlank(@This String str, int from, int end) {
        return StringUtils.indexOfNotBlank(str, from, end);
    }

    public static int indexOfEOL(@This String str, int from) {
        return StringUtils.indexOfEOL(str, from);
    }

    public static int indexOfQuotation(@This String str, int from, boolean escape) {
        return StringUtils.indexOfQuotation(str, from, escape);
    }

    public static int indexOfDoubleQuotation(@This String str, int from, boolean escape) {
        return StringUtils.indexOfDoubleQuotation(str, from, escape);
    }

    public static int indexOfParenthes(@This String str, int from) {
        return StringUtils.indexOfParenthes(str, from);
    }

    public static int indexOfUnixVariable(@This String str, int from, int length) {
        return StringUtils.indexOfUnixVariable(str, from, length);
    }

    public static int lastIndexOfNotBlank(@This String str, int from) {
        return StringUtils.lastIndexOfNotBlank(str, from);
    }

    public static int lastIndexOfBlank(@This String str, int from) {
        return StringUtils.lastIndexOfBlank(str, from);
    }

    public static int lastIndexOfStr(@This String str, String dest, int from, int end, boolean ignoreCase) {
        return StringUtils.lastIndexOfStr(str, dest, from, end, ignoreCase);
    }

    public static String[] split(@This String str, String delimiter) {
        return StringUtils.split(str, delimiter);
    }

    public static void split(@This String str, String delimiter, Collection<String> list) {
        StringUtils.split(str, delimiter, list);
    }

    public static String[] split(@This String str, String delimiter, boolean ignoreCase) {
        return StringUtils.split(str, delimiter, ignoreCase);
    }

    public static void split(@This String str, String delimiter, boolean ignoreCase, Collection<String> list) {
        StringUtils.split(str, delimiter, ignoreCase, list);
    }

    public static String[] split(@This String str, Collection<String> delimiter, boolean ignoreCase) {
        return StringUtils.split(str, delimiter, ignoreCase);
    }

    public static void split(@This String str, Collection<String> delimiter, boolean ignoreCase, Collection<String> list) {
        StringUtils.split(str, delimiter, ignoreCase, list);
    }

    public static void split(@This String str, Collection<String> delimiter, boolean ignoreCase, Collection<String> list, Collection<String> delimiters) {
        StringUtils.split(str, delimiter, ignoreCase, list, delimiters);
    }

    public static String[] split(@This String str, char delimiter) {
        return StringUtils.split(str, delimiter);
    }

    public static void split(@This String str, char delimiter, Collection<String> list) {
        StringUtils.split(str, delimiter, list);
    }

    public static String[] split(@This String str, char delimiter, char escape) {
        return StringUtils.split(str, delimiter, escape);
    }

    public static void split(@This String str, char delimiter, char escape, Collection<String> list) {
        StringUtils.split(str, delimiter, escape, list);
    }

    public static String[] split(@This String str, String delimiter, char escape) {
        return StringUtils.split(str, delimiter, escape);
    }

    public static void split(@This String str, String delimiter, char escape, Collection<String> list) {
        StringUtils.split(str, delimiter, escape, list);
    }

    public static String[] splitProperty(@This String str) {
        return StringUtils.splitProperty(str);
    }

    public static String[] splitProperty(@This String str, char delimiter) {
        return StringUtils.splitProperty(str, delimiter);
    }

    public static String[] splitPropertyForce(@This String str) {
        return StringUtils.splitPropertyForce(str);
    }

    public static List<String[]> splitPropertyForce(@This String str, String delimiter) {
        return StringUtils.splitPropertyForce(str, delimiter);
    }

    public static List<String> splitVariable(@This String str, List<String> list) {
        return StringUtils.splitVariable(str, list);
    }

    public static List<String> splitLines(@This String str, List<String> list) {
        return StringUtils.splitLines(str, list);
    }

    public static String[] splitParameters(@This String str) {
        return StringUtils.splitParameters(str);
    }

    public static void splitParameters(@This String str, Collection<String> list) {
        StringUtils.splitParameters(str, list);
    }

    public static void splitByBlank(@This String str, Collection<String> list) {
        StringUtils.splitByBlank(str, list);
    }

    public static List<String> splitByBlank(@This String str, int column) {
        return StringUtils.splitByBlank(str, column);
    }

    public static List<String> splitByBlanks(@This String str) {
        return StringUtils.splitByBlanks(str);
    }

    public static String[] splitByBlank(@This String str) {
        return StringUtils.splitByBlank(str);
    }

    public static byte[] toBytes(@This String str, String charsetName) {
        return StringUtils.toBytes(str, charsetName);
    }

    public static String toCase(@This String obj, boolean lower, Locale locale) {
        return StringUtils.toCase(obj, lower, locale);
    }

    public static boolean inArray(@This String str, CharSequence... array) {
        return StringUtils.inArray(str, array);
    }

    public static boolean inArrayIgnoreCase(@This String str, String... array) {
        return StringUtils.inArrayIgnoreCase(str, array);
    }

    public static boolean inCollection(@This String str, Collection<String> c, boolean ignoreCase) {
        return StringUtils.inCollection(str, c, ignoreCase);
    }

    public static boolean startsWithIgnoreCase(@This String str, String prefix) {
        return StringUtils.startsWithIgnoreCase(str, prefix);
    }

    public static boolean startsWith(@This String str, CharSequence prefix, int from, boolean ignoreCase, boolean ignoreBlank) {
        return StringUtils.startsWith(str, prefix, from, ignoreCase, ignoreBlank);
    }

    public static boolean startsWith(@This String str, Collection<? extends CharSequence> prefix, boolean ignoreCase) {
        return StringUtils.startsWith(str, prefix, ignoreCase);
    }

    public static boolean startsWith(@This String str, Collection<? extends CharSequence> prefix, int from, boolean ignoreCase, boolean ignoreBlank) {
        return StringUtils.startsWith(str, prefix, from, ignoreCase, ignoreBlank);
    }

    public static String firstCharToUpper(@This String str) {
        return StringUtils.firstCharToUpper(str);
    }

    public static String firstCharToLower(@This String str) {
        return StringUtils.firstCharToLower(str);
    }

    public static String encodeCharset(@This String str, String charsetName1, String charsetName2) {
        return StringUtils.encodeCharset(str, charsetName1, charsetName2);
    }

    public static String encodeGBKtoUTF8(@This String str) {
        return StringUtils.encodeGBKtoUTF8(str);
    }

    public static String encodeUTF8toGBK(@This String str) {
        return StringUtils.encodeUTF8toGBK(str);
    }

    public static String encodeJvmUtf8HexString(@This String str) {
        return StringUtils.encodeJvmUtf8HexString(str);
    }

    public static String decodeJvmUtf8HexString(@This String str) {
        return StringUtils.decodeJvmUtf8HexString(str);
    }

    public static String addLinePrefix(@This String str, CharSequence prefix) {
        return StringUtils.addLinePrefix(str, prefix);
    }

    public static String remove(@This String str, int begin, int end) {
        return StringUtils.remove(str, begin, end);
    }

    public static String removeBlank(@This String str) {
        return StringUtils.removeBlank(str);
    }

    public static String removePrefix(@This String str) {
        return StringUtils.removePrefix(str);
    }

    public static String removePrefix(@This String str, CharSequence prefix) {
        return StringUtils.removePrefix(str, prefix);
    }

    public static String removeSuffix(@This String str) {
        return StringUtils.removeSuffix(str);
    }

    public static String removeEOL(@This String str) {
        return StringUtils.removeEOL(str);
    }

    public static StringBuilder removeLineSeparator(@This String str) {
        return StringUtils.removeLineSeparator(str);
    }

    public static boolean isLetterOrNumber(@This String str) {
        return StringUtils.isLetterOrNumber(str);
    }

    public static boolean isNumber(@This String str) {
        return StringUtils.isNumber(str);
    }

    public static boolean contains(@This String str, char... array) {
        return StringUtils.contains(str, array);
    }

    public static boolean containsParenthes(@This String str) {
        return StringUtils.containsParenthes(str);
    }

    public static boolean containsSingleQuotation(@This String str) {
        return StringUtils.containsSingleQuotation(str);
    }

    public static boolean containsDoubleQuotation(@This String str) {
        return StringUtils.containsDoubleQuotation(str);
    }

    public static String defaultString(@This String str, CharSequence defaultStr) {
        return StringUtils.coalesce(str, defaultStr);
    }

    public static boolean isInt(@This String str) {
        return StringUtils.isInt(str);
    }

    public static boolean isDouble(@This String str) {
        return StringUtils.isDouble(str);
    }

    public static boolean isLong(@This String str) {
        return StringUtils.isLong(str);
    }

    public static boolean isDecimal(@This String str) {
        return StringUtils.isDecimal(str);
    }

    public static int parseInt(@This String str, int defaultVal) {
        return StringUtils.parseInt(str, defaultVal);
    }

    public static int parseHexadecimal(@This String str) {
        return StringUtils.parseHexadecimal(str);
    }

    public static byte[] parseHexString(@This String str) {
        return StringUtils.parseHexString(str);
    }

    public static Matcher compile(@This String str, String regex) {
        return StringUtils.compile(str, regex);
    }
}
