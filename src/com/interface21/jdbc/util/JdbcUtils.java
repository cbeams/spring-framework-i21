/**
 * Utility class for JDBC functionality
 * @author <a href="isabelle@meta-logix.com">Isabelle Muszynski</a>
 * @version $Id$
 */

package com.interface21.jdbc.util;

import java.sql.Types;
import com.interface21.core.InternalErrorException;

public class JdbcUtils {
   
    /**
     * Count the occurrences of the character <code>marker</code> in a SQL string <code>str</code>.
     * Any two consecutive nested occurrences of <code>delimiter</code> inside a substring of the string 
     * delimited by <code>delimiter</code> is ignored. Example :
     * <code>
     * select pet_owner from pet, owner where pet_name = 'bodo''ke' and owner.pet_id = pet.pet_id;
     * </code>
     * In this case the pet's name is "bodo'ke".
     * <p>
     * The character <code>marker</code> is not counted if it appears withing character <code>delimiter</code>.
     * It is also not counted if it is not followed by whitespace (unless it is just before the closing
     * delimiter)
     * <p>
     * Examples : if the delimiter is the single quote, and the character to count the 
     * occurrences of is the question mark, then
     * <p>
     * <code>The big ? 'bad wolf'</code> gives a count of one
     * <code>The big ?? bad wolf</code> gives a count of zero
     * <code>The big  'ba''ad' ? wolf</code> gives a count of one
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
     * Check that a SQL type is numeric
     * @param sqlType the SQL type to be checked
     * @return <code>true</code> if the type is numeric,
     * <code>false</code> otherwise
     */
    public static boolean isNumeric(int sqlType) {
	return Types.BIT == sqlType || Types.BIGINT == sqlType || Types.DECIMAL == sqlType || 
	    Types.DOUBLE == sqlType || Types.FLOAT == sqlType || Types.INTEGER == sqlType || 
	    Types.NUMERIC == sqlType || Types.REAL == sqlType || Types.SMALLINT == sqlType || 
	    Types.TINYINT == sqlType;
    }

    /**
     * Translate a SQL type into one of a few values.
     * All integer types are translated to Integer.
     * All real types are translated to Double.
     * All string types are translated to String.
     * All other types are left untouched.
     * @param sqlType the type to be translated into a simpler type
     * @return the new SQL type
     */
    public static int translateType(int sqlType) {

	int retType = sqlType;
	if (Types.BIT == sqlType || Types.TINYINT == sqlType || Types.SMALLINT == sqlType ||
	         Types.INTEGER == sqlType)
	    retType = Types.INTEGER;
	else if (Types.CHAR == sqlType || Types.VARCHAR == sqlType)
	    retType = Types.VARCHAR;
	else if (Types.DECIMAL == sqlType || Types.DOUBLE == sqlType || Types.FLOAT == sqlType ||
		 Types.NUMERIC == sqlType || Types.REAL == sqlType)
	    retType = Types.NUMERIC;

	return retType;
    }
}
