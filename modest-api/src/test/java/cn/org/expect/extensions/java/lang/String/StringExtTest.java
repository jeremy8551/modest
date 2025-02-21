package cn.org.expect.extensions.java.lang.String;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class StringExtTest {

    @Test
    public void split1() {
        Assertions.assertEquals("3", "1,2,3".split(',')[2]);
    }

    @Test
    public void split() {
    }

    @Test
    public void isBlank() {
        Assertions.assertTrue("".isBlank());
    }

    @Test
    public void length() {
        Assertions.assertEquals(6, "世界".length("utf-8"));
    }

    @Test
    public void width() {
        Assertions.assertEquals(1, "a".width("utf-8"));
        Assertions.assertEquals(2, "ab".width("utf-8"));
        Assertions.assertEquals(3, "abc".width("utf-8"));
        Assertions.assertEquals(4, "世界".width("utf-8"));
    }

    @Test
    public void trim() {
        Assertions.assertEquals("12", ",;,;12;,;,".trim(',', ';'));
    }

    @Test
    public void trimBlank() {
        Assertions.assertEquals("12", "   ,;  12,; ,;".trimBlank(',', ';'));
    }

    @Test
    public void trimParenthes() {
    }

    @Test
    public void rtrim() {
    }

    @Test
    public void testRtrim() {
    }

    @Test
    public void rtrimBlank() {
    }

    @Test
    public void ltrim() {
    }

    @Test
    public void testLtrim() {
    }

    @Test
    public void ltrimBlank() {
    }

    @Test
    public void replace() {
    }

    @Test
    public void testReplace() {
    }

    @Test
    public void replaceAll() {
    }

    @Test
    public void replaceLast() {
    }

    @Test
    public void replaceEnvironment() {
    }

    @Test
    public void testReplaceEnvironment() {
    }

    @Test
    public void replaceProperties() {
    }

    @Test
    public void testReplaceProperties() {
    }

    @Test
    public void replaceVariable() {
    }

    @Test
    public void testReplaceVariable() {
    }

    @Test
    public void testReplaceVariable1() {
    }

    @Test
    public void replaceHalfWidthChar() {
    }

    @Test
    public void replaceHalfWidthBlank() {
    }

    @Test
    public void substring() {
    }

    @Test
    public void testSubstring() {
    }

    @Test
    public void substr() {
    }

    @Test
    public void left() {
    }

    @Test
    public void testLeft() {
    }

    @Test
    public void testLeft1() {
    }

    @Test
    public void testLeft2() {
    }

    @Test
    public void right() {
    }

    @Test
    public void testRight() {
    }

    @Test
    public void testRight1() {
    }

    @Test
    public void testRight2() {
    }

    @Test
    public void middle() {
    }

    @Test
    public void escape() {
    }

    @Test
    public void testEscape() {
    }

    @Test
    public void escapeRegex() {
    }

    @Test
    public void escapeLineSeparator() {
    }

    @Test
    public void unescape() {
    }

    @Test
    public void quote() {
    }

    @Test
    public void quotes() {
    }

    @Test
    public void unquote() {
    }

    @Test
    public void unquotes() {
    }

    @Test
    public void unquotation() {
    }

    @Test
    public void indexOf() {
    }

    @Test
    public void indexOfBlank() {
    }

    @Test
    public void indexOfNotBlank() {
    }

    @Test
    public void testIndexOf() {
    }

    @Test
    public void indexOfEOL() {
    }

    @Test
    public void indexOfQuotation() {
    }

    @Test
    public void indexOfDoubleQuotation() {
    }

    @Test
    public void indexOfParenthes() {
    }

    @Test
    public void indexOfUnixVariable() {
    }

    @Test
    public void lastIndexOfNotBlank() {
    }

    @Test
    public void lastIndexOfBlank() {
    }

    @Test
    public void lastIndexOfStr() {
    }

    @Test
    public void testSplit() {
    }

    @Test
    public void testSplit1() {
    }

    @Test
    public void testSplit2() {
    }

    @Test
    public void testSplit3() {
    }

    @Test
    public void testSplit4() {
    }

    @Test
    public void testSplit5() {
    }

    @Test
    public void testSplit6() {
    }

    @Test
    public void testSplit7() {
    }

    @Test
    public void testSplit8() {
    }

    @Test
    public void testSplit9() {
    }

    @Test
    public void testSplit10() {
    }

    @Test
    public void testSplit11() {
    }

    @Test
    public void testSplit12() {
    }

    @Test
    public void splitProperty() {
    }

    @Test
    public void testSplitProperty() {
    }

    @Test
    public void splitPropertyForce() {
    }

    @Test
    public void testSplitPropertyForce() {
    }

    @Test
    public void splitVariable() {
    }

    @Test
    public void splitLines() {
    }

    @Test
    public void splitParameters() {
    }

    @Test
    public void testSplitParameters() {
    }

    @Test
    public void splitByBlank() {
    }

    @Test
    public void testSplitByBlank() {
    }

    @Test
    public void splitByBlanks() {
    }

    @Test
    public void testSplitByBlank1() {
    }

    @Test
    public void toBytes() {
    }

    @Test
    public void toCase() {
    }

    @Test
    public void inArray() {
    }

    @Test
    public void inArrayIgnoreCase() {
    }

    @Test
    public void inCollection() {
    }

    @Test
    public void startsWithIgnoreCase() {
    }

    @Test
    public void startsWith() {
    }

    @Test
    public void testStartsWith() {
    }

    @Test
    public void testStartsWith1() {
    }

    @Test
    public void firstCharToUpper() {
    }

    @Test
    public void firstCharToLower() {
    }

    @Test
    public void encodeCharset() {
    }

    @Test
    public void encodeGBKtoUTF8() {
    }

    @Test
    public void encodeUTF8toGBK() {
    }

    @Test
    public void encodeJvmUtf8HexString() {
    }

    @Test
    public void decodeJvmUtf8HexString() {
    }

    @Test
    public void addLinePrefix() {
    }

    @Test
    public void remove() {
    }

    @Test
    public void removeBlank() {
    }

    @Test
    public void removePrefix() {
    }

    @Test
    public void testRemovePrefix() {
    }

    @Test
    public void removeSuffix() {
    }

    @Test
    public void removeEOL() {
    }

    @Test
    public void removeLineSeparator() {
    }

    @Test
    public void isLetterOrNumber() {
    }

    @Test
    public void isNumber() {
    }

    @Test
    public void contains() {
    }

    @Test
    public void containsParenthes() {
    }

    @Test
    public void containsQuotation() {
    }

    @Test
    public void containsDoubleQuotation() {
    }

    @Test
    public void defaultString() {
    }

    @Test
    public void isInt() {
    }

    @Test
    public void isDouble() {
    }

    @Test
    public void isLong() {
    }

    @Test
    public void isDecimal() {
    }

    @Test
    public void testEncoding() {
    }

    @Test
    public void parseInt() {
    }

    @Test
    public void parseHexadecimal() {
    }

    @Test
    public void parseHexString() {
    }

    @Test
    public void compile() {
    }
}
