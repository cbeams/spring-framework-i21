package com.interface21.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Utility for matching paths with patterns in an Ant-like way.
 * Examples are provided below. Part of this mapping code has been
 * kindly borrowed from Ant (http://ant.apache.org).
 *
 * <p>The mapping matches urls using the following rules:<br>
 * <ul>
 * <li>'*' matches zero or more characters</li>
 * <li>'?' matches one characters</li>
 * <li>** matches zero or more 'directories' in a path</li>
 * </ul>
 *
 * <p>Some examples:<br>
 * <ul>
 * <li>com/**\/test.jsp - matches all test.jsp path underneath the com path</li>
 * <li>com/interface21/**\/*.jsp - matches all .jsp files underneath the
 * com/interface21 path</li>
 * <li>com/t?st.jsp - matches test.jsp but also tast.jsp or txst.jsp</li>
 * <li>com/**\/servlet/bla.jsp - matches com/interface21/servlet/bla.jsp but
 * also com/interface21/testing/servlet/bla.jsp and com/servlet/bla.jsp</li>
 * </ul>
 *
 * @author Alef Arendsen
 */
public abstract class PathMatcher {

	/**
	 * Matches a strign agains the given pattern
	 * @param pattern the pattern to match against
	 * @param str the string to test
	 * @return <code>true</code> is the arguments matched, <code>false</code>
	 * otherwise
	 */
	public static boolean match(String pattern, String str) {
		if (str.startsWith("/") !=
		    pattern.startsWith("/")) {
			return false;
		}

		List patDirs = tokenizePath(pattern);
		List strDirs = tokenizePath(str);

		int patIdxStart = 0;
		int patIdxEnd = patDirs.size() - 1;
		int strIdxStart = 0;
		int strIdxEnd = strDirs.size() - 1;

		// match all elements up to the first **
		while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
			String patDir = (String) patDirs.get(patIdxStart);
			if (patDir.equals("**")) {
				break;
			}
			if (!matchStrings(patDir, (String) strDirs.get(strIdxStart))) {
				return false;
			}
			patIdxStart++;
			strIdxStart++;
		}

		if (strIdxStart > strIdxEnd) {
			// String is exhausted, only match if rest of pattern is **'s
			for (int i = patIdxStart; i <= patIdxEnd; i++) {
				if (!patDirs.get(i).equals("**")) {
					return false;
				}
			}
			return true;
		}
		else {
			if (patIdxStart > patIdxEnd) {
				// String not exhausted, but pattern is. Failure.
				return false;
			}
		}

		// up to last '**'
		while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
			String patDir = (String) patDirs.get(patIdxEnd);
			if (patDir.equals("**")) {
				break;
			}
			if (!matchStrings(patDir, (String) strDirs.get(strIdxEnd))) {
				return false;
			}
			patIdxEnd--;
			strIdxEnd--;
		}
		if (strIdxStart > strIdxEnd) {
			// String is exhausted
			for (int i = patIdxStart; i <= patIdxEnd; i++) {
				if (!patDirs.get(i).equals("**")) {
					return false;
				}
			}
			return true;
		}

		while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
			int patIdxTmp = -1;
			for (int i = patIdxStart + 1; i <= patIdxEnd; i++) {
				if (patDirs.get(i).equals("**")) {
					patIdxTmp = i;
					break;
				}
			}
			if (patIdxTmp == patIdxStart + 1) {
				// '**/**' situation, so skip one
				patIdxStart++;
				continue;
			}
			// Find the pattern between padIdxStart & padIdxTmp in str between
			// strIdxStart & strIdxEnd
			int patLength = (patIdxTmp - patIdxStart - 1);
			int strLength = (strIdxEnd - strIdxStart + 1);
			int foundIdx = -1;
			strLoop:
			    for (int i = 0; i <= strLength - patLength; i++) {
				    for (int j = 0; j < patLength; j++) {
					    String subPat = (String) patDirs.get(patIdxStart + j + 1);
					    String subStr = (String) strDirs.get(strIdxStart + i + j);
					    if (!matchStrings(subPat, subStr)) {
						    continue strLoop;
					    }
				    }

				    foundIdx = strIdxStart + i;
				    break;
			    }

			if (foundIdx == -1) {
				return false;
			}

			patIdxStart = patIdxTmp;
			strIdxStart = foundIdx + patLength;
		}

		for (int i = patIdxStart; i <= patIdxEnd; i++) {
			if (!patDirs.get(i).equals("**")) {
				return false;
			}
		}

		return true;
	}


	/**
	 * Tests whether or not a string matches against a pattern.
	 * The pattern may contain two special characters:<br>
	 * '*' means zero or more characters<br>
	 * '?' means one and only one character
	 * @param pattern pattern to match against.
	 * Must not be <code>null</code>.
	 * @param str string which must be matched against the pattern.
	 * Must not be <code>null</code>.
	 * @return <code>true</code> if the string matches against the
	 * pattern, or <code>false</code> otherwise.
	 */
	private static boolean matchStrings(String pattern, String str) {
		char[] patArr = pattern.toCharArray();
		char[] strArr = str.toCharArray();
		int patIdxStart = 0;
		int patIdxEnd = patArr.length - 1;
		int strIdxStart = 0;
		int strIdxEnd = strArr.length - 1;
		char ch;

		boolean containsStar = false;
		for (int i = 0; i < patArr.length; i++) {
			if (patArr[i] == '*') {
				containsStar = true;
				break;
			}
		}

		if (!containsStar) {
			// No '*'s, so we make a shortcut
			if (patIdxEnd != strIdxEnd) {
				return false; // Pattern and string do not have the same size
			}
			for (int i = 0; i <= patIdxEnd; i++) {
				ch = patArr[i];
				if (ch != '?') {
					if (ch != strArr[i]) {
						return false;// Character mismatch
					}
				}
			}
			return true; // String matches against pattern
		}


		if (patIdxEnd == 0) {
			return true; // Pattern contains only '*', which matches anything
		}

		// Process characters before first star
		while ((ch = patArr[patIdxStart]) != '*' && strIdxStart <= strIdxEnd) {
			if (ch != '?') {
				if (ch != strArr[strIdxStart]) {
					return false;// Character mismatch
				}
			}
			patIdxStart++;
			strIdxStart++;
		}
		if (strIdxStart > strIdxEnd) {
			// All characters in the string are used. Check if only '*'s are
			// left in the pattern. If so, we succeeded. Otherwise failure.
			for (int i = patIdxStart; i <= patIdxEnd; i++) {
				if (patArr[i] != '*') {
					System.out.println("false14");
					return false;
				}
			}
			return true;
		}

		// Process characters after last star
		while ((ch = patArr[patIdxEnd]) != '*' && strIdxStart <= strIdxEnd) {
			if (ch != '?') {
				if (ch != strArr[strIdxEnd]) {
					return false;// Character mismatch
				}
			}
			patIdxEnd--;
			strIdxEnd--;
		}
		if (strIdxStart > strIdxEnd) {
			// All characters in the string are used. Check if only '*'s are
			// left in the pattern. If so, we succeeded. Otherwise failure.
			for (int i = patIdxStart; i <= patIdxEnd; i++) {
				if (patArr[i] != '*') {
					return false;
				}
			}
			return true;
		}

		// process pattern between stars. padIdxStart and patIdxEnd point
		// always to a '*'.
		while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
			int patIdxTmp = -1;
			for (int i = patIdxStart + 1; i <= patIdxEnd; i++) {
				if (patArr[i] == '*') {
					patIdxTmp = i;
					break;
				}
			}
			if (patIdxTmp == patIdxStart + 1) {
				// Two stars next to each other, skip the first one.
				patIdxStart++;
				continue;
			}
			// Find the pattern between padIdxStart & padIdxTmp in str between
			// strIdxStart & strIdxEnd
			int patLength = (patIdxTmp - patIdxStart - 1);
			int strLength = (strIdxEnd - strIdxStart + 1);
			int foundIdx = -1;
			strLoop:
			for (int i = 0; i <= strLength - patLength; i++) {
				for (int j = 0; j < patLength; j++) {
					ch = patArr[patIdxStart + j + 1];
					if (ch != '?') {
						if (ch != strArr[strIdxStart + i + j]) {
							continue strLoop;
						}
					}
				}

				foundIdx = strIdxStart + i;
				break;
			}

			if (foundIdx == -1) {
				return false;
			}

			patIdxStart = patIdxTmp;
			strIdxStart = foundIdx + patLength;
		}

		// All characters in the string are used. Check if only '*'s are left
		// in the pattern. If so, we succeeded. Otherwise failure.
		for (int i = patIdxStart; i <= patIdxEnd; i++) {
			if (patArr[i] != '*') {
				return false;
			}
		}
		return true;
	}

	/**
	 * Breaks up a given path in a List of elements.
	 * @param path Path to tokenize. Must not be <code>null</code>.
	 * @return a List of path elements from the tokenized path
	 */
	private static List tokenizePath(String path) {
		List ret = new ArrayList();
		StringTokenizer st = new StringTokenizer(path, "/");
		while (st.hasMoreTokens()) {
			ret.add(st.nextToken());
		}
		return ret;
	}

}
