package com.rcs.newsletter.core.service.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.StringTokenizer;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log.Log;

import java.io.UnsupportedEncodingException;

/**
 * An extension to the apache String Utils.
 *
 */
public class StringUtils extends org.apache.commons.lang.StringUtils {

    private static transient Log logger = LogFactoryUtil.getLog(StringUtils.class);
    private static final int DATE_SIZE = 15;
    /** Zeg ken jij het alfabet, dat gaat van ABC, zeg ken jij het alfabet, daar maak je woorden mee!  */
    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /** the allowed characters for a user name please note that an email address is also usable */
    public static final String ALLOWED = "0123456789.- @_" + ALPHABET;
    private static final String PLAIN_ASCII =
            "AaEeIiOoUu" // grave
            + "AaEeIiOoUuYy" // acute
            + "AaEeIiOoUuYy" // circumflex
            + "AaEeIiOoUuYy" // tilde
            + "AaEeIiOoUuYy" // umlaut
            + "Aa" // ring
            + "Cc" // cedilla
            + "Oo" // stroke
            ;
    private static final String UNICODE =
            "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9" // grave
            + "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD" // acute
            + "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177" // circumflex
            + "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177" // tilde
            + "\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF" // umlaut
            + "\u00C5\u00E5" // ring
            + "\u00C7\u00E7" // cedilla
            + "\u00D8\u00F8" // stroke
            ;
    private static final String[] HTML_WHITESPACE = new String[]{
        "<BR>", "<Br>", "<bR>", "<br>", "&NBSP;", "&nbsp;", "&Nbsp;", "&NBsp;", "&NBSp;", "&NbSp;", "&NbSP;"
    };
    /**
     * Initialization lock for the whole class. Init's only happen once per class load so this
     * shouldn't be a bottleneck.
     */
    private static Object initLock = new Object();
    /** Used by the hash method. */
    private static MessageDigest digest = null;
    /**
     * A list of some of the most common words. For searching and indexing, we often want to filter
     * out these words since they just confuse searches. The list was not created scientifically
     * so may be incomplete :)
     */
    private static final String[] commonWords = new String[]{
        "a", "and", "as", "at", "be", "do", "i", "if", "in", "is", "it", "so", "the", "to", "de",
        "het", "een", "van"
    };
    private static Map<String, String> commonWordsMap = null;
    /**
     * Pseudo-random number generator object for use with randomString(). The Random class is not
     * considered to be cryptographically secure, so only use these random Strings for low to
     * medium security applications.
     */
    private static Random randGen = null;
    /**
     * Array of numbers and letters of mixed case. Numbers appear in the list twice so that there
     * is a more equal chance that a number will be picked. We can use the array to get a random
     * number or letter by picking a random array index.
     */
    private static char[] numbersAndLetters = null;

    /**
     * check if the string that is passed can be used as a username
     *
     * Reinout 20080331 : Valid usernames can now also exist completely of numbers.
     * We still check that the first character is a letter or digit.
     * 
     * @param attempt DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static boolean isUserName(String attempt) {
        if ((attempt == null) || (attempt.length() == 0)) {
            return false;
        }

        if (!Character.isLetterOrDigit(attempt.charAt(0))) {
            return false;
        }
        for (int c = 0; c < attempt.length(); c++) {
            if (ALLOWED.indexOf(attempt.charAt(c)) == -1) {
                return false;
            }
        }

        // OK
        return true;
    }

    /**
     * This is a replacement method for isNumeric from StringUtils by apache since the
     * isNumeric fails on negative numberrs.
     *
     * @param string
     * @return <code>true</code> when the string could be converted to a number, <code>false</code> otherwise (string empty or not a number)
     */
    public static boolean isANumber(String string) {
        if (isBlank(string)) {
            return false;
        }
        try {
            new Double(string);
        } catch (Exception e) {
            //logger.warn("An error has occured: ", e);
            return false;
        }
        return true;
    }

    /**
     * Replaces all instances of oldString with newString in line with the added feature that
     * matches of newString in oldString ignore case.
     *
     * @param line the String to search to perform replacements on
     * @param oldString the String that should be replaced by newString
     * @param newString the String that will replace all instances of oldString
     *
     * @return a String will all instances of oldString replaced by newString
     */
    public static final String replaceIgnoreCase(String line, String oldString, String newString) {
        if (line == null) {
            return null;
        }

        String lcLine = line.toLowerCase();
        String lcOldString = oldString.toLowerCase();
        int i = 0;

        if ((i = lcLine.indexOf(lcOldString, i)) >= 0) {
            char[] line2 = line.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuffer buf = new StringBuffer(line2.length);
            buf.append(line2, 0, i).append(newString2);
            i += oLength;

            int j = i;

            while ((i = lcLine.indexOf(lcOldString, i)) > 0) {
                buf.append(line2, j, i - j).append(newString2);
                i += oLength;
                j = i;
            }

            buf.append(line2, j, line2.length - j);

            return buf.toString();
        }

        return line;
    }

    /**
     * Replaces all instances of oldString with newString in line. The count Integer is updated
     * with number of replaces.
     *
     * @param line the String to search to perform replacements on
     * @param oldString the String that should be replaced by newString
     * @param newString the String that will replace all instances of oldString
     * @param count DOCUMENT ME!
     *
     * @return a String will all instances of oldString replaced by newString
     */
    public static final String replace(String line, String oldString, String newString, int[] count) {
        if (line == null) {
            return null;
        }

        int i = 0;

        if ((i = line.indexOf(oldString, i)) >= 0) {
            int counter = 0;
            counter++;

            char[] line2 = line.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuffer buf = new StringBuffer(line2.length);
            buf.append(line2, 0, i).append(newString2);
            i += oLength;

            int j = i;

            while ((i = line.indexOf(oldString, i)) > 0) {
                counter++;
                buf.append(line2, j, i - j).append(newString2);
                i += oLength;
                j = i;
            }

            buf.append(line2, j, line2.length - j);
            count[0] = counter;

            return buf.toString();
        }

        return line;
    }

    /**
     * This method takes a string which may contain HTML tags (ie, &lt;b&gt;, &lt;table&gt;, etc)
     * and converts the '&lt'' and '&gt;' characters to their HTML escape sequences.
     *
     * @param input the text to be converted.
     *
     * @return the input string with the characters '&lt;' and '&gt;' replaced with their HTML
     *         escape sequences.
     */
    public static final String escapeHTMLTags(String input) {
        //Check if the string is null or zero length -- if so, return
        //what was sent in.
        if ((input == null) || (input.length() == 0)) {
            return input;
        }

        //Use a StringBuffer in lieu of String concatenation -- it is
        //much more efficient this way.
        StringBuffer buf = new StringBuffer(input.length());
        char ch = ' ';

        for (int i = 0; i < input.length(); i++) {
            ch = input.charAt(i);

            if (ch == '<') {
                buf.append("&lt;");
            } else if (ch == '>') {
                buf.append("&gt;");
            } else {
                buf.append(ch);
            }
        }

        return buf.toString();
    }

    /**
     * Hashes a String using the Md5 algorithm and returns the result as a String of hexadecimal
     * numbers. This method is synchronized to avoid excessive MessageDigest object creation. If
     * calling this method becomes a bottleneck in your code, you may wish to maintain a pool of
     * MessageDigest objects instead of using this method.
     *
     * <p>
     * A hash is a one-way function -- that is, given an input, an output is easily computed.
     * However, given the output, the input is almost impossible to compute. This is useful for
     * passwords since we can store the hash and a hacker will then have a very hard time
     * determining the original password.
     * </p>
     *
     * <p>
     * In Cops, every time a user logs in, we simply take their plain text password, compute the
     * hash, and compare the generated hash to the stored hash. Since it is almost impossible that
     * two passwords will generate the same hash, we know if the user gave us the correct password
     * or not. The only negative to this system is that password recovery is basically impossible.
     * Therefore, a reset password method is used instead.
     * </p>
     *
     * @param data the String to compute the hash of.
     *
     * @return a hashed version of the passed-in String
     */
    public synchronized static final String hash(String data) {
        if (digest == null) {
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException nsae) {
                System.err.println("Failed to load the MD5 MessageDigest. " +
                        "Cops will be unable to function normally.");
                logger.warn("exception: ", nsae);
            }
        }

        //Now, compute hash.
        digest.update(data.getBytes());

        return toHex(digest.digest());
    }

    /**
     * Hashes a String using the 'algo' algorithm (MD5 or SHA1) and returns the result as a
     * String of hexadecimal numbers. This method is synchronized to avoid excessive MessageDigest
     * object creation. If calling this method becomes a bottleneck in your code, you may wish to
     * maintain a pool of MessageDigest objects instead of using this method.
     *
     * <p>
     * In Cops, every time a user logs in, we simply take their plain text password, compute the
     * hash, and compare the generated hash to the stored hash. Since it is almost impossible that
     * two passwords will generate the same hash, we know if the user gave us the correct password
     * or not. The only negative to this system is that password recovery is basically impossible.
     * Therefore, a reset password method is used instead.
     * </p>
     *
     * @param algo algorithm (MD5, SHA1, etc)
     * @param data the String to compute the hash of.
     *
     * @return a hashed version of the passed-in String
     */
    public synchronized static final String hash(String algo, String data) {
        if (digest == null) {
            try {
                digest = MessageDigest.getInstance(algo);

            } catch (NoSuchAlgorithmException e) {
                System.err.println("Failed to load the " + algo + " MessageDigest. " +
                        "Cops will be unable to function normally.");
                logger.warn("exception: ", e);
            }
        }

        //Now, compute hash.
        digest.update(data.getBytes());

        return toHex(digest.digest());
    }

    /**
     * Turns an array of bytes into a String representing each byte as an unsigned hex number.
     *
     * <p>
     * Method by Santeri Paavolainen, Helsinki Finland 1996<br>
     * (c) Santeri Paavolainen, Helsinki Finland 1996<br>
     * Distributed under LGPL.
     * </p>
     *
     * @param hash an rray of bytes to convert to a hex-string
     *
     * @return generated hex string
     */
    public static final String toHex(byte[] hash) {
        StringBuffer buf = new StringBuffer(hash.length * 2);
        int i;

        for (i = 0; i < hash.length; i++) {
            if (((int) hash[i] & 0xff) < 0x10) {
                buf.append("0");
            }

            buf.append(Long.toString((int) hash[i] & 0xff, 16));
        }

        return buf.toString();
    }

    /**
     * Converts a line of text into an array of lower case words. Words are delimited by the
     * following characters: , .\r\n:/\+
     *
     * <p>
     * In the future, this method should be changed to use a BreakIterator.wordInstance(). That
     * class offers much more fexibility.
     * </p>
     *
     * @param text a String of text to convert into an array of words
     *
     * @return text broken up into an array of words.
     */
    public static final String[] toLowerCaseWordArray(String text) {
        if ((text == null) || (text.length() == 0)) {
            return new String[0];
        }

        StringTokenizer tokens = new StringTokenizer(text, " ,\r\n.:/\\+");
        String[] words = new String[tokens.countTokens()];

        for (int i = 0; i < words.length; i++) {
            words[i] = tokens.nextToken().toLowerCase();
        }

        return words;
    }

    /**
     * Returns a new String array with some of the most common English words removed. The specific
     * words removed are: a, and, as, at, be, do, i, if, in, is, it, so, the, to
     *
     * @param words DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static final String[] removeCommonWords(String[] words) {
        //See if common words map has been initialized. We don't statically
        //initialize it to save some memory. Even though this a small savings,
        //it adds up with hundreds of classes being loaded.
        if (commonWordsMap == null) {
            synchronized (initLock) {
                if (commonWordsMap == null) {
                    commonWordsMap = new HashMap<String, String>();

                    for (int i = 0; i < commonWords.length; i++) {
                        commonWordsMap.put(commonWords[i], commonWords[i]);
                    }
                }
            }
        }

        //Now, add all words that aren't in the common map to results
        ArrayList<String> results = new ArrayList<String>(words.length);

        for (int i = 0; i < words.length; i++) {
            if (!commonWordsMap.containsKey(words[i])) {
                results.add(words[i]);
            }
        }

        return (String[]) results.toArray(new String[results.size()]);
    }

    /**
     * Returns a random String of numbers and letters of the specified length. The method uses the
     * Random class that is built-in to Java which is suitable for low to medium grade security
     * uses. This means that the output is only pseudo random, i.e., each number is mathematically
     * generated so is not truly random.
     *
     * <p>
     * For every character in the returned String, there is an equal chance that it will be a
     * letter or number. If a letter, there is an equal chance that it will be lower or upper
     * case.
     * </p>
     *
     * <p>
     * The specified length must be at least one. If not, the method will return null.
     * </p>
     *
     * @param length the desired length of the random String to return.
     *
     * @return a random String of numbers and letters of the specified length.
     */
    public static final String randomString(int length) {
        if (length < 1) {
            return null;
        }

        //Init of pseudo random number generator.
        if (randGen == null) {
            synchronized (initLock) {
                if (randGen == null) {
                    randGen = new Random();

                    //Also initialize the numbersAndLetters array
                    numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" +
                            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
                }
            }
        }

        //Create a char buffer to put random letters and numbers in.
        char[] randBuffer = new char[length];

        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
        }

        return new String(randBuffer);
    }

    /**
     * create a random string of a certain size with a give alphabet. This method is
     * slower than using the standard alphabet.
     *
     */
    public static final String randomString(int length, String alphabet) {
        char[] numbersAndLetters = alphabet.toCharArray();
        int size = alphabet.length();

        if (length < 1) {
            return null;
        }

        if (isEmpty(alphabet)) {
            return null;
        }

        //Init of pseudo random number generator.
        if (randGen == null) {
            synchronized (initLock) {
                if (randGen == null) {
                    randGen = new Random();
                }
            }
        }

        //Create a char buffer to put random letters and numbers in.
        char[] randBuffer = new char[length];

        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(size)];
        }

        return new String(randBuffer);
    }

    /**
     * Intelligently chops a String at a word boundary (whitespace) that occurs at the specified
     * index in the argument or before. However, if there is a newline character before
     * <code>length</code>, the String will be chopped there. If no newline or whitespace is found
     * in <code>string</code> up to the index <code>length</code>, the String will chopped at
     * <code>length</code>.
     *
     * <p>
     * For example, chopAtWord("This is a nice String", 10) will return "This is a" which is the
     * first word boundary less than or equal to 10 characters into the original String.
     * </p>
     *
     * @param string the String to chop.
     * @param length the index in <code>string</code> to start looking for a whitespace boundary
     *        at.
     *
     * @return a substring of <code>string</code> whose length is less than or equal to
     *         <code>length</code>, and that is chopped at whitespace.
     */
    public static final String chopAtWord(String string, int length) {
        if (string == null) {
            return string;
        }

        char[] charArray = string.toCharArray();
        int sLength = string.length();

        if (length < sLength) {
            sLength = length;
        }

        //First check if there is a newline character before length; if so,
        //chop word there.
        for (int i = 0; i < (sLength - 1); i++) {
            //Windows
            if ((charArray[i] == '\r') && (charArray[i + 1] == '\n')) {
                return string.substring(0, i);
            } //Unix
            else if (charArray[i] == '\n') {
                return string.substring(0, i);
            }
        }

        //Also check boundary case of Unix newline
        if (charArray[sLength - 1] == '\n') {
            return string.substring(0, sLength - 1);
        }

        //Done checking for newline, now see if the total string is less than
        //the specified chop point.
        if (string.length() < length) {
            return string;
        }

        //No newline, so chop at the first whitespace.
        for (int i = length - 1; i > 0; i--) {
            if (charArray[i] == ' ') {
                return string.substring(0, i).trim();
            }
        }

        //Did not find word boundary so return original String chopped at
        //specified length.
        return string.substring(0, length);
    }

    /**
     * Highlights words in a string. Words matching ignores case. The actual higlighting method is
     * specified with the start and end higlight tags. Those might be beginning and ending HTML
     * bold tags, or anything else.
     *
     * @param string the String to highlight words in.
     * @param words an array of words that should be highlighted in the string.
     * @param startHighlight the tag that should be inserted to start highlighting.
     * @param endHighlight the tag that should be inserted to end highlighting.
     *
     * @return a new String with the specified words highlighted.
     */
    public static final String highlightWords(String string, String[] words, String startHighlight,
            String endHighlight) {
        if ((string == null) || (words == null) || (startHighlight == null) ||
                (endHighlight == null)) {
            return null;
        }

        //Iterate through each word.
        for (int x = 0; x < words.length; x++) {
            //we want to ignore case.
            String lcString = string.toLowerCase();

            //using a char [] is more efficient
            char[] string2 = string.toCharArray();
            String word = words[x].toLowerCase();

            //perform specialized replace logic
            int i = 0;

            if ((i = lcString.indexOf(word, i)) >= 0) {
                int oLength = word.length();
                StringBuffer buf = new StringBuffer(string2.length);

                //we only want to highlight distinct words and not parts of
                //larger words. The method used below mostly solves this. There
                //are a few cases where it doesn't, but it's close enough.
                boolean startSpace = false;
                char startChar = ' ';

                if ((i - 1) > 0) {
                    startChar = string2[i - 1];

                    if (!Character.isLetter(startChar)) {
                        startSpace = true;
                    }
                }

                boolean endSpace = false;
                char endChar = ' ';

                if ((i + oLength) < string2.length) {
                    endChar = string2[i + oLength];

                    if (!Character.isLetter(endChar)) {
                        endSpace = true;
                    }
                }

                if ((startSpace && endSpace) || ((i == 0) && endSpace)) {
                    buf.append(string2, 0, i);

                    if (startSpace && (startChar == ' ')) {
                        buf.append(startChar);
                    }

                    buf.append(startHighlight);
                    buf.append(string2, i, oLength).append(endHighlight);

                    if (endSpace && (endChar == ' ')) {
                        buf.append(endChar);
                    }
                } else {
                    buf.append(string2, 0, i);
                    buf.append(string2, i, oLength);
                }

                i += oLength;

                int j = i;

                while ((i = lcString.indexOf(word, i)) > 0) {
                    startSpace = false;
                    startChar = string2[i - 1];

                    if (!Character.isLetter(startChar)) {
                        startSpace = true;
                    }

                    endSpace = false;

                    if ((i + oLength) < string2.length) {
                        endChar = string2[i + oLength];

                        if (!Character.isLetter(endChar)) {
                            endSpace = true;
                        }
                    }

                    if ((startSpace && endSpace) || ((i + oLength) == string2.length)) {
                        buf.append(string2, j, i - j);

                        if (startSpace && (startChar == ' ')) {
                            buf.append(startChar);
                        }

                        buf.append(startHighlight);
                        buf.append(string2, i, oLength).append(endHighlight);

                        if (endSpace && (endChar == ' ')) {
                            buf.append(endChar);
                        }
                    } else {
                        buf.append(string2, j, i - j);
                        buf.append(string2, i, oLength);
                    }

                    i += oLength;
                    j = i;
                }

                buf.append(string2, j, string2.length - j);
                string = buf.toString();
            }
        }

        return string;
    }

    /**
     * Escapes all necessary characters in the String so that it can be used in an XML doc.
     *
     * @param string the string to escape.
     *
     * @return the string with appropriate characters escaped.
     * @deprecated Please use Apache Commons StringEscapeUtils#escapeXML() instead
     */
    public static final String escapeForXML(String string) {
        //Check if the string is null or zero length -- if so, return
        //what was sent in.
        if ((string == null) || (string.length() == 0)) {
            return string;
        }

        char[] sArray = string.toCharArray();
        StringBuffer buf = new StringBuffer(sArray.length);
        char ch;

        for (int i = 0; i < sArray.length; i++) {
            ch = sArray[i];

            if (ch == '<') {
                buf.append("&lt;");
            } else if (ch == '&') {
                buf.append("&amp;");
            } else if (ch == '"') {
                buf.append("&quot;");
            } else {
                buf.append(ch);
            }
        }

        return buf.toString();
    }

    /**
     * Method to return a string of a Date. The Date will be represented as milliseconds, prepended
     * with 0's. This way we can make sure the Date is stored correctly in whatever  database we
     * will use. It makes comparing two Dates easier as well.
     *
     * @param date The date we wish to format
     *
     * @return The formatted Date. It will be 15 characters long.
     */
    public static final String dateToMilliseconds(Date date) {
        long datelong = date.getTime();
        String returnValue = "";
        if (datelong < 0L) {
            // Date before 1970.
            String dateString = Long.toString(datelong).substring(1);
            int length = dateString.length();
            returnValue = "-00000000000000000".substring(0, DATE_SIZE - length) + dateString;
        } else {
            String dateString = Long.toString(date.getTime());
            int length = dateString.length();
            returnValue = "000000000000000000".substring(0, DATE_SIZE - length) + dateString;
        }
        return returnValue;
    }

    /**
     * Method to convert a string like "001046171590175" to an actual Date object.
     *
     * @param dateString The dateString (in milliseconds) we wish to format
     *
     * @return The Date Object.
     */
    public static final Date millisecondStringToDate(String dateString) {
        if (isBlank(dateString)) {
            return null;
        }
        Date dateObject = new Date(new java.lang.Long(dateString).longValue());

        return dateObject;
    }

    /**
     * Returns the db encoded string of the original. Encoding is done via input.getBytes(DBENCODING), which is most likely ISO-8859-1.
     *
     * @param input The string to dbencode.
     *
     * @return A dbencoded string, or an empty String in case an encoding exception occured.
     *         If the input parameter was null, the result will be null as well.
     */
//    public static final String getDBEncoded(String input) {
//        if (input == null) {
//            return null;
//        }
//
//        String returnString = "";
//
//        try {
//            returnString = new String(input.getBytes(DBENCODING));
//        } catch (Exception e) {
//            logger.error("encoding exception", e);
//        }
//
//        return returnString;
//
//    }

    /**
     * DOCUMENT ME!
     *
     * @param input DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static final String getMimeImage(String input) {
        String returnString = "";

        try {
            if ("image/jpeg".equalsIgnoreCase(input)) {
                returnString = "/images/iconJPG.gif";
            }

            if ("image/pjpeg".equalsIgnoreCase(input)) {
                returnString = "/images/iconJPG.gif";
            }

            if ("image/gif".equalsIgnoreCase(input)) {
                returnString = "/images/iconGIF.gif";
            }

            if ("image/bmp".equalsIgnoreCase(input)) {
                returnString = "/images/iconGIF.gif";
            }

            if ("application/msword".equalsIgnoreCase(input)) {
                returnString = "/images/iconDOC.gif";
            }

            if ("application/excel".equalsIgnoreCase(input)) {
                returnString = "/images/iconXLS.gif";
            }

            if ("application/pdf".equalsIgnoreCase(input)) {
                returnString = "/images/iconPDF.gif";
            }

            if ("application/octet-stream".equalsIgnoreCase(input)) {
                returnString = "/images/iconGIF.gif";
            }
        } catch (Exception e) {
            logger.warn("exception: ", e);
            logger.error("mimetype error");
        }

        return returnString;
    }

    /**
     * Escapes all necessary characters to store arbitrary texts in a CDATA block.
     *
     * @param string the string to escape.
     *
     * @return the string with appropriate characters escaped.
     */
    public static final String toCDATA(String string) {
        //Check if the string is null or zero length -- if so, return
        //what was sent in.
        if ((string == null) || (string.length() == 0)) {
            return string;
        }

        char[] sArray = string.toCharArray();
        StringBuffer buf = new StringBuffer(sArray.length);

        char ch;

        for (int i = 0; i < sArray.length; i++) {
            ch = sArray[i];

            if (ch == '\\') {
                buf.append("\\\\");
            } else {
                if (ch == ']') {
                    buf.append("\\]");
                } else {
                    buf.append(ch);
                }
            }
        }

        return buf.toString();
    }

    /**
     * deEscapes all necessary characters to store arbitrary texts in a CDATA block.
     *
     * @param string the string to escape.
     *
     * @return the string with appropriate characters escaped.
     */
    public static final String fromCDATA(String string) {
        //Check if the string is null or zero length -- if so, return
        //what was sent in.
        if ((string == null) || (string.length() == 0)) {
            return string;
        }

        char[] sArray = string.toCharArray();
        StringBuffer buf = new StringBuffer(sArray.length);
        char ch;
        boolean writeSlash = false;

        for (int i = 0; i < sArray.length; i++) {
            ch = sArray[i];

            //logger.debug("before: " + writeSlash);
            //logger.debug("i:"+i);
            //logger.debug("ch: "+ch);
            if (ch == '\\') {
                if (writeSlash) {
                    buf.append(ch);
                    writeSlash = false;
                } else {
                    writeSlash = true;
                }
            } else {
                writeSlash = false;
                buf.append(ch);
            }
        }

        return buf.toString();
    }

    /**
     * parse inserts such as {1}, {2} etc. etc.
     *
     * @param line String,the line that contains the replacement tags
     * @param inserts String, the inserts that need to replace the tags
     *
     * @return a parsed result
     */
    public static String parseInserts(String line, String[] inserts) {
        String result = new String(line);

        for (int i = 0; i < inserts.length; i++) {
            String tag = "{" + i + "}";
            result = StringUtils.replace(result, tag, inserts[i]);
        }

        return result;
    }

    /** Parse inserts based on the String values in key.
     *  The key value is wrapped in {key} and at the place
     *  found in the line the {key} is replaced by the value.
     */
    public static String parseInserts(String line, Map<String, String> inserts) {
        String result = new String(line);

        for (Iterator<Entry<String, String>> i = inserts.entrySet().iterator(); i.hasNext();) {
            Entry<String, String> entry = i.next();
            String tag = "{" + entry.getKey().trim() + "}";
            result = StringUtils.replace(result, tag, entry.getValue());
        }

        return result;
    }

    /** Parse inserts based on the String values in key.
     *  The key value is NOT wrapped in "{" / "}" and at the place
     *  found in the line the {key} is replaced by the value.
     */
    public static String parseExactInserts(String line, Map<String, String> inserts) {
        String result = new String(line);

        for (Iterator<Entry<String, String>> i = inserts.entrySet().iterator(); i.hasNext();) {
            Entry<String, String> entry = i.next();
            String tag = entry.getKey().trim();
            result = StringUtils.replace(result, tag, entry.getValue());
        }

        return result;
    }

    /**
     * Cleans a string from all non-alphabet characters
     *
     * @param str DOCUMENT ME!
     * @deprecated use the small cap version.
     * @return DOCUMENT ME!
     */

    public static String CleanNonAlphabets(String str) {
        return cleanNonAlphabets(str);
    }

    /**
     * Get rid of all characers that are not alphabetic.
     *
     * @param str
     * @return
     */
    public static String cleanNonAlphabets(String str) {
        int last = str.length();
        int noOfChars = ALPHABET.length();
        String cleaned = "";

        for (int i = 0; i < last; i++) {
            for (int j = 0; j < noOfChars; j++) {
                if (str.charAt(i) == ALPHABET.charAt(j)) {
                    cleaned += str.charAt(i);
                }
            }
        }

        return cleaned;
    }

    /**
     * seperates the individual items from a line based on a separator
     *
     * @param line String, the line
     * @param separator char, the character on which to separate
     * @param trim boolean, true if the results need to be trimmed before returning
     *
     * @return the separated parts of the line in order of occurence
     */
    public static String[] separate(String line, char separator, boolean trim) {
        if (StringUtils.isEmpty(line)) {
            return new String[0];
        } else {
            String[] tmp = StringUtils.split(line, separator);
            List<String> valid = new ArrayList<String>();
            for (int i = 0; i < tmp.length; i++) {
                String value = tmp[i];
                if (trim) {
                    value = value.trim();
                }
                if (!"".equals(value)) {
                    valid.add(value);
                }
            }
            return (String[]) valid.toArray(new String[0]);
        }
    }

    /**
     * Validate the form of an email address.
     *
     * <P>Return <code>true</code> only if
     *<ul>
     * <li> <code>aEmailAddress</code> can successfully construct an
     * <tt>javax.mail.internet.InternetAddress</tt>
     * <li> when parsed with a "@" delimiter, <code>aEmailAddress</code> contains
     * two tokens which satisfy {@link hirondelle.web4j.util.Util#textHasContent}.
     *</ul>
     *
     *<P> The second condition arises since local email addresses, simply of the form
     * "albert", for example, are valid but almost always undesired.
     */
    public static boolean isValidEmailAddress(String emailaddress) {
        if (emailaddress == null) {
            return false;
        }
        boolean result = true;
        try {
            new InternetAddress(emailaddress);
            if (!hasNameAndDomain(emailaddress)) {
                result = false;
            }
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    /**
     * @param emailaddress
     * @return
     */
    public static boolean hasNameAndDomain(String emailaddress) {
        String[] tokens = emailaddress.split("@");
        return (tokens.length == 2 &&
                org.apache.commons.lang.StringUtils.isNotBlank(tokens[0]) &&
                org.apache.commons.lang.StringUtils.isNotBlank(tokens[1]));
    }

    /**
     * Remove accentued from a string and replace with ascii equivalent
     * See: http://www.rgagnon.com/javadetails/java-0456.html
     *
     * @param s string to be processed
     * @return a String instance, the processed string.
     */
    public static String convertNonAscii(String s) {
        StringBuffer sb = new StringBuffer();
        int n = s.length();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            int pos = UNICODE.indexOf(c);
            if (pos > -1) {
                sb.append(PLAIN_ASCII.charAt(pos));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /** 
     * Compares two Strings on equality ignoring any accents.
     * This method uses a Collator beased on the COPS default locale.
     * @return true if the two Strings are equal ignoring accents, false otherwise.
     * @throws COPSException if the default locale couldn't be retrieved
     */
//    public static boolean equalsIgnoreAccents(String lhs, String rhs) throws COPSException {
//        return equalsIgnoreAccents(lhs, rhs, LocaleUtils.getDefaultLocale());
//    }

    /** 
     * Compares two Strings on equality ignoring any accents.
     * This method uses a Collator with PRIMARY strength, based on the locale passed in the parameters.
     * Example: equalsIgnoreAccents("Michèle", "Michele") will return true.
     *          equalsIgnoreAccents("Michèle", "Michale") will return false.
     * @return true if the two Strings are equal ignoring accents, false otherwise.
     * @see http://java.sun.com/mailers/techtips/corejava/2006/tt0822.html#2
     */
    public static boolean equalsIgnoreAccents(String lhs, String rhs, Locale locale) {
        Collator collator = Collator.getInstance(locale);
        collator.setStrength(Collator.PRIMARY);
        int comparison = collator.compare(lhs, rhs);
        return (comparison == 0);
    }

    /** Returns the original string with all single quotes turned into 2 single quotes. This is useful in case you want to perform a query
     * to the database with string that might contain a single quote. Such cases would create an error.
     * @param input A string to be processed
     * @return a String instance, with single quotes replaced by two single quotes.
     */
    public static String doubleSingleQuotes(String input) {
        return StringUtils.replace(input, "'", "''");
    }

    /** Returns the original string with all single quotes turned into 2 single quotes and all wildcard symbols 
     * converted to the database wildcards. This is useful in case you want to perform a query
     * to the database with string that might contain a single quote. Such cases would create an error.
     * Also, it turns an asterisk into a percentage.
     * @param input A string to be processed
     * @return a String instance, with single quotes replaced by two single quotes and "*" replaced by "%" and a single backslash replaced by two.
     */
    public static String formatWhereClauseField(String input) {
        return StringUtils.replace(StringUtils.replace(StringUtils.replace(input, "\\", "\\\\"), "'", "''"), "*", "%");
    }

    /** 
     * Removes &nsbp; and &lt;br&gt; from start + end of strings
     * @param  the string containing HTML whitespaces to be stripped
     * @return a copy of str with HTML whitespace characters stripped
     */
    public static String trimHTMLWhitespace(String str) {
        String result = StringUtils.defaultString(str);

        for (String remove : HTML_WHITESPACE) {
            result = result.trim();
            result = removeStart(result, remove);
            result = removeEnd(result, remove);
        }

        return result;
    }

    /**
     * Levenshtein Distance Algorithm.
     * This method is already implemented in a new version of org.apache.commons.lang.StringUtils.
     * 
     * @param s
     * @param t
     * @return the distance between two strings
     */
    public static int getLevenshteinDistance(String s, String t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        /*
        The difference between this impl. and the previous is that, rather 
        than creating and retaining a matrix of size s.length()+1 by t.length()+1, 
        we maintain two single-dimensional arrays of length s.length()+1.  The first, d,
        is the 'current working' distance array that maintains the newest distance cost
        counts as we iterate through the characters of String s.  Each time we increment
        the index of String t we are comparing, d is copied to p, the second int[].  Doing so
        allows us to retain the previous cost counts as required by the algorithm (taking 
        the minimum of the cost count to the left, up one, and diagonally up and to the left
        of the current cost count being calculated).  (Note that the arrays aren't really 
        copied anymore, just switched...this is clearly much better than cloning an array 
        or doing a System.arraycopy() each time  through the outer loop.)
        
        Effectively, the difference between the two implementations is this one does not 
        cause an out of memory condition when calculating the LD over two very large strings.  		
         */

        int n = s.length(); // length of s
        int m = t.length(); // length of t

        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }

        int p[] = new int[n + 1]; //'previous' cost array, horizontally
        int d[] = new int[n + 1]; // cost array, horizontally
        int _d[]; //placeholder to assist in swapping p and d

        // indexes into strings s and t
        int i; // iterates through s
        int j; // iterates through t

        char t_j; // jth character of t

        int cost; // cost

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            t_j = t.charAt(j - 1);
            d[0] = j;

            for (i = 1; i <= n; i++) {
                cost = s.charAt(i - 1) == t_j ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost				
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
            }

            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }

        // our last action in the above loop was to switch d and p, so p now 
        // actually has the most recent cost counts
        return p[n];
    }
    
    /**
     * Joins the elements of the provided int array into a single String containing the provided elements.
     * No delimiter is added before or after the list.
     * 
     * @param int_array the int array values to join together
     * @param the separator character to use, do not use null  
     * @return
     */
    public static final String join(int [] int_array, String separator) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < int_array.length; i++) {
            sb.append(int_array[i]);    
            if (i < int_array.length - 1)
                sb.append(separator);            
        }
        return sb.toString();
    }

    /**
     * Hashes a String using the SHA1 algorithm and returns the result as a String of hexadecimal
     * numbers.
     * 
     * @param text
     * @return
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.io.UnsupportedEncodingException
     */
    public static String toSHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-1");
        // The sha1 output will always be 40-bytes lenght.
        byte[] sha1hash = new byte[40];         
        md.update(text.getBytes("UTF-8"), 0, text.length());
        sha1hash = md.digest();
        return toHex(sha1hash);
    }
}
