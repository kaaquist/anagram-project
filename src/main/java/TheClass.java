import java.io.File;

import com.google.common.base.CharMatcher;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.text.StrBuilder;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by kasper on 28/11/2016.
 */
public class TheClass {
    public static Log logger = LogFactory.getLog("TheClass.Logger");

    /**
     * Read the wordlist from file and return a list of strings containing
     * them.
     * @return List of Strings
     */
    public List<String> loadFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            List theLines = FileUtils.readLines(new File(classLoader.getResource("wordlist").getFile()), StandardCharsets.UTF_8);
            return theLines;
        } catch (IOException ioe){
            logger.error( "Exception while reading file ioe: ", ioe);
            return null;
        }
    }

    /**
     * Method to check if the words from a word list contains
     * same letters as the letters used in the initial string.
     * @param a : String
     * @param b : String
     * @return boolean
     */
    public boolean madeOutOfSameLetters(String a, String b) {
        if (a == null) {
            return b == null;
        } else if (b == null) {
            return false;
        }
        char[] left = a.toCharArray();
        char[] right = b.toCharArray();
        Arrays.sort(left);
        Arrays.sort(right);
        if (left.length == right.length){
            logger.debug(" == ");
            return Arrays.equals(left, right);}
        else if(left.length > right.length){
            logger.debug(" left > ");
            for(Character dd : right){
                if (new String(left).indexOf(dd) == -1){
                    return false;
                }
            }
            return true;
        }
        else {
            logger.debug(" right > ");
            for(Character xx: left){
                if (new String(right).indexOf(xx) == -1){
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Will return MD5 Hash of the input string.
     * @param s : string for which to calculate the MD5 hash.
     * @return String with hash.
     */
    public String getMD5Val(String s) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            return new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException nsae){
            return null;
        }
    }

    /**
     * Recursive method to find the anagram.
     * @param startTime : When the app was started.
     * @param tsaindex  : start index of the String[].
     * @param theStringArray : the initial string split in a String array.
     * @param theLinesFiltered : word list after the filtering.
     * @param wordsInString : number of words in the initial string.
     * @param theString : anagram we are looking for.
     */
    public void recursiveSearch(String hash, long startTime, int tsaindex, String[] theStringArray, List<String> theLinesFiltered, int wordsInString, String theString ) {
        if( wordsInString > 0 ) {
            for(String ts : theLinesFiltered) {
                if(ts.length() != theStringArray[tsaindex].length()) continue;
                //System.out.println(wordsInString + " | " + ts );
                recursiveSearch(hash, startTime, tsaindex + 1, theStringArray, theLinesFiltered,wordsInString - 1, theString + " " + ts);
            }
        } else {
            if(getMD5Val(theString.trim()).equals(hash)) {
                logger.info("This is it: " + theString);
                final long duration = System.nanoTime() - startTime;
                logger.info("Time spent in sec : " + (duration / 1000000000));
                System.exit(0);
            }
        }
    }


    public static void main(String[] args) throws Exception {
        //Start time
        final long startTime = System.nanoTime();
        TheClass theClass = new TheClass();
        // Anagram
        String theInitialString = "poultry outwits ants";
        // md5 Hash to find
        final String hash = "4624d200580677270a54ccff86b9610e";

        /* Filtering the wordlist to reduce the amount checks needed to be done
           before we start the search. */
        final String theString = theInitialString.replaceAll("\\s","");
        logger.debug(theString);
        char[] theStringArray = theString.toCharArray();
        Arrays.sort(theStringArray);
        logger.debug(new String(theStringArray));
        Character[] charObjectArray = ArrayUtils.toObject(theStringArray);
        List<Character> charList = Arrays.asList(charObjectArray);
        SortedSet<Character> sortedSet = new TreeSet<>();
        charList.forEach(sortedSet::add);
        StrBuilder sb = new StrBuilder();
        sortedSet.forEach(sb::append);
        List<String> theLines = theClass.loadFile();
        List<String> theLinesFiltered = new ArrayList<>();
        theLines.forEach((string)->{if(theClass.madeOutOfSameLetters(sb.toString(),string.toString())){theLinesFiltered.add(string.toString());}});
        List<String> linesToBeRemoved = new ArrayList<>();
        theLinesFiltered.forEach((string)->{
            for (char tt : string.toCharArray()){
                if(CharMatcher.is(tt).countIn(string) > CharMatcher.is(tt).countIn(theString) ){
                    linesToBeRemoved.add(string);
                    break;
                }
            }
        });
        linesToBeRemoved.forEach(theLinesFiltered::remove);
        // Start the search
        theClass.recursiveSearch(hash, startTime, 0,theInitialString.split(" "),theLinesFiltered, theInitialString.split(" ").length, "");
    }
}
