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
		while ((idx = s.indexOf(sub, pos)) != -1) {
			++count;
			pos = idx + sub.length();
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
	}


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
			return new String[]{s};

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
	}


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
	}


	/**
	 * Convenience method to return a Collection as a delimited (e.g. CSV)
	 * String. Useful for toString() implementations
	 * @param c Collection to display
	 * @param delim delimiter to use (probably a ",")
	 */
	public static String collectionToDelimitedString(Collection c, String delim) {
		if (c == null)
			return "null";
		return iteratorToDelimitedString(c.iterator(), delim);
	}


	/**
	 * Convenience method to return a Collection as a delimited (e.g. CSV)
	 * String. Useful for toString() implementations
	 * @param itr Iterator of the collection to display
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

}
