/**
 * Generic framework code included with 
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002). 
 * This code is free to use and modify. 
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */


package com.interface21.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.interface21.core.InternalErrorException;

/**
 * Miscellaneous string utility methods. This class delivers some
 * simple functionality that should really be provided by the core
 * Java String and StringBuffer classes, such as the ability to
 * replace all occurrences of a given substring in a target string.
 * It also provides easy-to-use methods to convert between delimited
 * strings, such as CSV strings, and collections and arrays.
 * @author  Rod Johnson
 * @since 16 April 2001
 */
public abstract class StringUtils {
	
	/**
	 * Count the occurrences of the substring in string s
	 * @param s string to search in. Returns 0 if this is null
	 * @param sub string to search for. Return 0 if this is null.
	 */
	public static int countOccurrencesOf(String s, String sub) {
		if (s == null || sub == null || "".equals(sub))
			return 0;
		int count = 0, pos = 0, idx = 0;
		while ( (idx = s.indexOf(sub, pos)) != -1) {
			++count;
			pos = idx + sub.length();
			//System.out.println("Idx="  + idx + " Pos=" + pos);
		}
		return count;
	}
   
    /**
     * Count the occurrences of the character <code>marker</code> in a string <code>str</code>
     * delimited by <code>delimiter</code>;
     * Any two consecutive nested occurrences of <code>delimiter</code> inside the string is 
     * also ignored.
     * The character <code>marker</code> is not counted if it appears withing character <code>delimiter</code>.
     * It is also not counted if it is not followed by whitespace (unless it is just before the closing
     * delimiter)
     * <p>
     * Examples : if the delimiter is the single quote, and the character to count the 
     * occurrences of is the question mark, then
     * <p>
     * <code>'The big ? bad wolf'</code> gives a count of one
     * <code>'The big ?? bad wolf'</code> gives a count of zero
     * <code>'The big  ba''ad ? wolf'</code> gives a count of one
     * <p>
     * The grammar of the string passed in should obey the rules
     * string = (marker | stringPart)*
     * stringPart = character | nestedStringPart
     * nestedStringPart = delimiter (character | nestedDelimiter | marker)*
     * delimiter = the delimiter character (normally the apostrophe for SQL strings)
     * nestedDelimiter = 2 consecutive delimiters
     * marker = the character to count the occurrences of
     * character = all other characters
     * <p>
     * @param str string to search in. Returns 0 if this is null
     * @param marker the character to search for.
     * @param delim the delimiter.
     */
    public static int countParameterPlaceholders(String str, char marker, char delim) {
	int count = 0;
	if (str == null || "".equals(str) || '\0' == marker || '\0' == delim)
	    return count;

	// The states of the finite state machine
	final int stateStart = 0;
	final int stateNormalChar = 1;
	final int stateMarker = 2;
	final int stateInDelim = 3;
	final int stateError = 4;

	int len = str.length();
	int index = 0;
	char ch;
	// Because we have to skip over nested consecutive markers, we need one character of lookahead
	char lookahead = 0;

	// We start in stateStart
	int state = stateStart;
	while (index < len) {
	    ch = 0 == index ? str.charAt(0) : index < len - 1 ? lookahead : str.charAt(index);
	    lookahead = index < len - 1 ? str.charAt(index + 1) : 0;
	    switch(state) {
	    case stateStart:
		if (ch == delim)
		    state = stateInDelim;
		else if (ch == marker && 
			 (index == len - 1 || Character.isWhitespace(str.charAt(index + 1)))) {
		    state = stateMarker;
		}
		else
		    state = stateNormalChar;
		break;
	    case stateNormalChar:
		System.out.println("stateNormalChar");
		if (ch == delim) {
		    state = stateInDelim;
		}
		else if (index < len - 1 && lookahead == marker) {
		    state = stateMarker;
		}
		break;
	    case stateMarker:
		++count;
		if (index < len - 1 && !Character.isWhitespace(lookahead))
		    state = stateError;
		else
		    state = stateNormalChar;
		break;
	    case stateInDelim:
		if (index == len - 1)
		    state = stateError;
		else if (ch == delim) {
		    if (index < len - 1 && delim == lookahead) {
			// Eat delimiters and stay in same state
			if (index > len - 2)
			    throw new IllegalArgumentException("Invalid nested delimiters : " + str);
			else {
			    index += 1;
			}
		    }
		    else // seen end delimiter
			state = stateNormalChar;
		}
		break;
	    case stateError:
		throw new IllegalArgumentException("Invalid string : " + str);		
	    default:
		throw new InternalErrorException("default case reached");
	    }
	    ++index;
	}

	return count;
    }
 
    /**
     * Replaces all occurences of a substring within a string with another string.
     * @param inString String to examine
     * @param oldPattern String to replace
     * @param newPattern String to insert
     * @return a String with the replacements
     */
    public static String replace(String inString, String oldPattern, String newPattern) {
        // Pick up error conditions
        if (inString == null)
            return null;
        if (oldPattern == null || newPattern == null)
            return inString;
        
        StringBuffer sbuf = new StringBuffer();      // Output StringBuffer we'll build up
        int pos = 0;                        // Our position in the old string
        int index = inString.indexOf(oldPattern); // The index of an occurrence we've found, or -1
        int patLen = oldPattern.length();
        while (index >= 0) {
            sbuf.append(inString.substring(pos, index));
            sbuf.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }
        sbuf.append(inString.substring(pos));     // Remember to append any characters to the right of a match
        return sbuf.toString();
    }  // replace
    
    
    /**
     * Delete all occurrences of the given substring.
     * @param pattern pattern to delete all occurrences of
     */
    public static String delete(String inString, String pattern) {
    	return replace(inString, pattern, "");
    }
    
    /**
     * Delete any character in a given string.
     * @param chars characters to delete e.g. az\n will delete as, zs and new lines
     */
    public static String deleteAny(String inString, String chars) {
    	if (inString == null || chars == null)
    		return inString;
   		StringBuffer out = new StringBuffer();
    	for (int i = 0; i < inString.length(); i++) {
    		char c = inString.charAt(i);
    		if (chars.indexOf(c) == -1) {
    			out.append(c);
    		}
    	}
    	return out.toString();
    }
    
	
   /** 
    * Take a String which is a delimited list and convert it to a String array
    * @param s String
    * @param delimiter delimiter. This will not be returned
    * @return an array of the tokens in the list
    */
    public static String[] delimitedListToStringArray(String s, String delimiter) {
    	if (s == null)
    		return new String[0];
   		if (delimiter == null)
   			return new String[] { s };
    	
    	/*
        StringTokenizer st = new StringTokenizer(s, delimiter);
        String[] tokens = new String[st.countTokens()];
        System.out.println("length is  " +tokens.length);
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = st.nextToken();
        }
        return tokens;
      */

        List l = new LinkedList();
        int delimCount = 0;
        int pos = 0;
        int delpos = 0;
       	while ((delpos = s.indexOf(delimiter, pos)) != -1) {
        	l.add(s.substring(pos, delpos));
        	pos = delpos + delimiter.length();
        }
        if (pos <= s.length()) {
        	// Add rest of String
        	l.add(s.substring(pos));
        }        	
          
        return (String[]) l.toArray(new String[l.size()]);

    }	// delimitedListToStringArray
    
    
   /** 
    * Convert a CSV list into an array of Strings
    * @param s CSV list
    * @return an array of Strings. Returns the empty array if
    * s is null.
    */
    public static String[] commaDelimitedListToStringArray(String s) {
        return delimitedListToStringArray(s, ",");
    }
    
   /** 
    * Convenience method to convert a CSV string list to a set. Note that
    * this will suppress duplicates.
    * @param s CSV String
    * @return a Set of String entries in the list
    */
    public static Set commaDelimitedListToSet(String s) {
        Set set = new TreeSet();
        String[] tokens = commaDelimitedListToStringArray(s);
        for (int i = 0; i < tokens.length; i++)
            set.add(tokens[i]);
        return set;
    }
    
    
   /** 
    * Convenience method to return a String array as a delimited (e.g. CSV)
    * String. Useful for toString() implementations
    * @param arr array to display. Elements may be of any type (toString() will be
    * called on each element).
    * @param delim delimiter to use (probably a ,)
    */
    public static String arrayToDelimitedString(Object[] arr, String delim) {
        if (arr == null)
            return "null";
        else {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < arr.length; i++) {
                if (i > 0)
                    sb.append(delim);
                sb.append(arr[i]);
            }
            return sb.toString();
        }
    }    // arrayToDelimitedString
    
	
   /** Convenience method to return a Collection as a delimited (e.g. CSV)
    * String. Useful for toString() implementations
    * @param c Collection to display
    * @param delim delimiter to use (probably a ,)
    */
    public static String collectionToDelimitedString(Collection c, String delim) {
    	 if (c == null)
            return "null";
        return iteratorToDelimitedString(c.iterator(), delim);
    }    // collectionToDelimitedString
    
     
    /** 
     * Convenience method to return a Collection as a delimited (e.g. CSV)
    * String. Useful for toString() implementations
    * @param c Collection to display
    * @param delim delimiter to use (probably a ,)
    */
    public static String iteratorToDelimitedString(Iterator itr, String delim) {
        if (itr == null)
            return "null";
        else {
            StringBuffer sb = new StringBuffer();
            int i = 0;
            while (itr.hasNext()) {
                if (i++ > 0)
                    sb.append(delim);
                sb.append(itr.next());
            }
            return sb.toString();
        }
    }
    
}	// class StringUtils
