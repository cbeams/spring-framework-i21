/**
 * Generic framework code included with
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/1861007841/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002).
 * This code is free to use and modify.
 * Please contact <a href="mailto:rod.johnson@interface21.com">rod.johnson@interface21.com</a>
 * for commercial support.
 */
package com.interface21.util;

/**
 * Miscellaneous utility methods for creating an object array from
 * a bunch of primative args.  This utility class is especially
 * useful for classes implementing the ParameterizableErrorCoded interface.
 * Since there are so many combinations of primitives that can be passed in
 * to create an Object array from there are the following limitations.
 * CURRENT LIMITATIONS:
 * 1) Only implements overload for up to 9 args of SAME PRIMITIVE TYPE.
 * 2) Implements a helper overload for creating an array from up to 9 args
 *    of type object (not a big help, but it saves user from doing "new Object[] {...}"
 *    and it's consistent.
 * 3) Only implements overload for up to 5 args of all combinations
 *    SAME PRIMITIVE TYPE + any Objects sprinkled in between that primitive type
 *    (as it's assumed this will be a frequently used combo).
 *
 * @author  Tony Falabella
 * @since   24 February 2002
 * @version $Id$
 */
public abstract class ObjectArrayUtils {
    //~ Methods ----------------------------------------------------------------

    public static Object[] toArray(int arg1) {
        return new Object[] { new Integer(arg1) };
    }

    public static Object[] toArray(int arg1, int arg2) {
        return new Object[] { new Integer(arg1), new Integer(arg2) };
    }

    public static Object[] toArray(int arg1, int arg2, int arg3) {
        return new Object[] {
            new Integer(arg1), new Integer(arg2), new Integer(arg3)
        };
    }

    public static Object[] toArray(int arg1, int arg2, int arg3, int arg4) {
        return new Object[] {
            new Integer(arg1), new Integer(arg2), new Integer(arg3),
            new Integer(arg4)
        };
    }

    public static Object[] toArray(int arg1, int arg2, int arg3, int arg4,
                                   int arg5) {
        return new Object[] {
            new Integer(arg1), new Integer(arg2), new Integer(arg3),
            new Integer(arg4), new Integer(arg5)
        };
    }

    public static Object[] toArray(int arg1, int arg2, int arg3, int arg4,
                                   int arg5, int arg6) {
        return new Object[] {
            new Integer(arg1), new Integer(arg2), new Integer(arg3),
            new Integer(arg4), new Integer(arg5), new Integer(arg6)
        };
    }

    public static Object[] toArray(int arg1, int arg2, int arg3, int arg4,
                                   int arg5, int arg6, int arg7) {
        return new Object[] {
            new Integer(arg1), new Integer(arg2), new Integer(arg3),
            new Integer(arg4), new Integer(arg5), new Integer(arg6),
            new Integer(arg7)
        };
    }

    public static Object[] toArray(int arg1, int arg2, int arg3, int arg4,
                                   int arg5, int arg6, int arg7, int arg8) {
        return new Object[] {
            new Integer(arg1), new Integer(arg2), new Integer(arg3),
            new Integer(arg4), new Integer(arg5), new Integer(arg6),
            new Integer(arg7), new Integer(arg8)
        };
    }

    public static Object[] toArray(int arg1, int arg2, int arg3, int arg4,
                                   int arg5, int arg6, int arg7, int arg8,
                                   int arg9) {
        return new Object[] {
            new Integer(arg1), new Integer(arg2), new Integer(arg3),
            new Integer(arg4), new Integer(arg5), new Integer(arg6),
            new Integer(arg7), new Integer(arg8), new Integer(arg9)
        };
    }

    public static Object[] toArray(long arg1) {
        return new Object[] { new Long(arg1) };
    }

    public static Object[] toArray(long arg1, long arg2) {
        return new Object[] { new Long(arg1), new Long(arg2) };
    }

    public static Object[] toArray(long arg1, long arg2, long arg3) {
        return new Object[] { new Long(arg1), new Long(arg2), new Long(arg3) };
    }

    public static Object[] toArray(long arg1, long arg2, long arg3, long arg4) {
        return new Object[] {
            new Long(arg1), new Long(arg2), new Long(arg3), new Long(arg4)
        };
    }

    public static Object[] toArray(long arg1, long arg2, long arg3, long arg4,
                                   long arg5) {
        return new Object[] {
            new Long(arg1), new Long(arg2), new Long(arg3), new Long(arg4),
            new Long(arg5)
        };
    }

    public static Object[] toArray(long arg1, long arg2, long arg3, long arg4,
                                   long arg5, long arg6) {
        return new Object[] {
            new Long(arg1), new Long(arg2), new Long(arg3), new Long(arg4),
            new Long(arg5), new Long(arg6)
        };
    }

    public static Object[] toArray(long arg1, long arg2, long arg3, long arg4,
                                   long arg5, long arg6, long arg7) {
        return new Object[] {
            new Long(arg1), new Long(arg2), new Long(arg3), new Long(arg4),
            new Long(arg5), new Long(arg6), new Long(arg7)
        };
    }

    public static Object[] toArray(long arg1, long arg2, long arg3, long arg4,
                                   long arg5, long arg6, long arg7, long arg8) {
        return new Object[] {
            new Long(arg1), new Long(arg2), new Long(arg3), new Long(arg4),
            new Long(arg5), new Long(arg6), new Long(arg7), new Long(arg8)
        };
    }

    public static Object[] toArray(long arg1, long arg2, long arg3, long arg4,
                                   long arg5, long arg6, long arg7, long arg8,
                                   long arg9) {
        return new Object[] {
            new Long(arg1), new Long(arg2), new Long(arg3), new Long(arg4),
            new Long(arg5), new Long(arg6), new Long(arg7), new Long(arg8),
            new Long(arg9)
        };
    }

    public static Object[] toArray(short arg1) {
        return new Object[] { new Short(arg1) };
    }

    public static Object[] toArray(short arg1, short arg2) {
        return new Object[] { new Short(arg1), new Short(arg2) };
    }

    public static Object[] toArray(short arg1, short arg2, short arg3) {
        return new Object[] { new Short(arg1), new Short(arg2), new Short(arg3) };
    }

    public static Object[] toArray(short arg1, short arg2, short arg3,
                                   short arg4) {
        return new Object[] {
            new Short(arg1), new Short(arg2), new Short(arg3), new Short(arg4)
        };
    }

    public static Object[] toArray(short arg1, short arg2, short arg3,
                                   short arg4, short arg5) {
        return new Object[] {
            new Short(arg1), new Short(arg2), new Short(arg3), new Short(arg4),
            new Short(arg5)
        };
    }

    public static Object[] toArray(short arg1, short arg2, short arg3,
                                   short arg4, short arg5, short arg6) {
        return new Object[] {
            new Short(arg1), new Short(arg2), new Short(arg3), new Short(arg4),
            new Short(arg5), new Short(arg6)
        };
    }

    public static Object[] toArray(short arg1, short arg2, short arg3,
                                   short arg4, short arg5, short arg6,
                                   short arg7) {
        return new Object[] {
            new Short(arg1), new Short(arg2), new Short(arg3), new Short(arg4),
            new Short(arg5), new Short(arg6), new Short(arg7)
        };
    }

    public static Object[] toArray(short arg1, short arg2, short arg3,
                                   short arg4, short arg5, short arg6,
                                   short arg7, short arg8) {
        return new Object[] {
            new Short(arg1), new Short(arg2), new Short(arg3), new Short(arg4),
            new Short(arg5), new Short(arg6), new Short(arg7), new Short(arg8)
        };
    }

    public static Object[] toArray(short arg1, short arg2, short arg3,
                                   short arg4, short arg5, short arg6,
                                   short arg7, short arg8, short arg9) {
        return new Object[] {
            new Short(arg1), new Short(arg2), new Short(arg3), new Short(arg4),
            new Short(arg5), new Short(arg6), new Short(arg7), new Short(arg8),
            new Short(arg9)
        };
    }

    public static Object[] toArray(float arg1) {
        return new Object[] { new Float(arg1) };
    }

    public static Object[] toArray(float arg1, float arg2) {
        return new Object[] { new Float(arg1), new Float(arg2) };
    }

    public static Object[] toArray(float arg1, float arg2, float arg3) {
        return new Object[] { new Float(arg1), new Float(arg2), new Float(arg3) };
    }

    public static Object[] toArray(float arg1, float arg2, float arg3,
                                   float arg4) {
        return new Object[] {
            new Float(arg1), new Float(arg2), new Float(arg3), new Float(arg4)
        };
    }

    public static Object[] toArray(float arg1, float arg2, float arg3,
                                   float arg4, float arg5) {
        return new Object[] {
            new Float(arg1), new Float(arg2), new Float(arg3), new Float(arg4),
            new Float(arg5)
        };
    }

    public static Object[] toArray(float arg1, float arg2, float arg3,
                                   float arg4, float arg5, float arg6) {
        return new Object[] {
            new Float(arg1), new Float(arg2), new Float(arg3), new Float(arg4),
            new Float(arg5), new Float(arg6)
        };
    }

    public static Object[] toArray(float arg1, float arg2, float arg3,
                                   float arg4, float arg5, float arg6,
                                   float arg7) {
        return new Object[] {
            new Float(arg1), new Float(arg2), new Float(arg3), new Float(arg4),
            new Float(arg5), new Float(arg6), new Float(arg7)
        };
    }

    public static Object[] toArray(float arg1, float arg2, float arg3,
                                   float arg4, float arg5, float arg6,
                                   float arg7, float arg8) {
        return new Object[] {
            new Float(arg1), new Float(arg2), new Float(arg3), new Float(arg4),
            new Float(arg5), new Float(arg6), new Float(arg7), new Float(arg8)
        };
    }

    public static Object[] toArray(float arg1, float arg2, float arg3,
                                   float arg4, float arg5, float arg6,
                                   float arg7, float arg8, float arg9) {
        return new Object[] {
            new Float(arg1), new Float(arg2), new Float(arg3), new Float(arg4),
            new Float(arg5), new Float(arg6), new Float(arg7), new Float(arg8),
            new Float(arg9)
        };
    }

    public static Object[] toArray(double arg1) {
        return new Object[] { new Double(arg1) };
    }

    public static Object[] toArray(double arg1, double arg2) {
        return new Object[] { new Double(arg1), new Double(arg2) };
    }

    public static Object[] toArray(double arg1, double arg2, double arg3) {
        return new Object[] {
            new Double(arg1), new Double(arg2), new Double(arg3)
        };
    }

    public static Object[] toArray(double arg1, double arg2, double arg3,
                                   double arg4) {
        return new Object[] {
            new Double(arg1), new Double(arg2), new Double(arg3),
            new Double(arg4)
        };
    }

    public static Object[] toArray(double arg1, double arg2, double arg3,
                                   double arg4, double arg5) {
        return new Object[] {
            new Double(arg1), new Double(arg2), new Double(arg3),
            new Double(arg4), new Double(arg5)
        };
    }

    public static Object[] toArray(double arg1, double arg2, double arg3,
                                   double arg4, double arg5, double arg6) {
        return new Object[] {
            new Double(arg1), new Double(arg2), new Double(arg3),
            new Double(arg4), new Double(arg5), new Double(arg6)
        };
    }

    public static Object[] toArray(double arg1, double arg2, double arg3,
                                   double arg4, double arg5, double arg6,
                                   double arg7) {
        return new Object[] {
            new Double(arg1), new Double(arg2), new Double(arg3),
            new Double(arg4), new Double(arg5), new Double(arg6),
            new Double(arg7)
        };
    }

    public static Object[] toArray(double arg1, double arg2, double arg3,
                                   double arg4, double arg5, double arg6,
                                   double arg7, double arg8) {
        return new Object[] {
            new Double(arg1), new Double(arg2), new Double(arg3),
            new Double(arg4), new Double(arg5), new Double(arg6),
            new Double(arg7), new Double(arg8)
        };
    }

    public static Object[] toArray(double arg1, double arg2, double arg3,
                                   double arg4, double arg5, double arg6,
                                   double arg7, double arg8, double arg9) {
        return new Object[] {
            new Double(arg1), new Double(arg2), new Double(arg3),
            new Double(arg4), new Double(arg5), new Double(arg6),
            new Double(arg7), new Double(arg8), new Double(arg9)
        };
    }

    public static Object[] toArray(char arg1) {
        return new Object[] { new Character(arg1) };
    }

    public static Object[] toArray(char arg1, char arg2) {
        return new Object[] { new Character(arg1), new Character(arg2) };
    }

    public static Object[] toArray(char arg1, char arg2, char arg3) {
        return new Object[] {
            new Character(arg1), new Character(arg2), new Character(arg3)
        };
    }

    public static Object[] toArray(char arg1, char arg2, char arg3, char arg4) {
        return new Object[] {
            new Character(arg1), new Character(arg2), new Character(arg3),
            new Character(arg4)
        };
    }

    public static Object[] toArray(char arg1, char arg2, char arg3, char arg4,
                                   char arg5) {
        return new Object[] {
            new Character(arg1), new Character(arg2), new Character(arg3),
            new Character(arg4), new Character(arg5)
        };
    }

    public static Object[] toArray(char arg1, char arg2, char arg3, char arg4,
                                   char arg5, char arg6) {
        return new Object[] {
            new Character(arg1), new Character(arg2), new Character(arg3),
            new Character(arg4), new Character(arg5), new Character(arg6)
        };
    }

    public static Object[] toArray(char arg1, char arg2, char arg3, char arg4,
                                   char arg5, char arg6, char arg7) {
        return new Object[] {
            new Character(arg1), new Character(arg2), new Character(arg3),
            new Character(arg4), new Character(arg5), new Character(arg6),
            new Character(arg7)
        };
    }

    public static Object[] toArray(char arg1, char arg2, char arg3, char arg4,
                                   char arg5, char arg6, char arg7, char arg8) {
        return new Object[] {
            new Character(arg1), new Character(arg2), new Character(arg3),
            new Character(arg4), new Character(arg5), new Character(arg6),
            new Character(arg7), new Character(arg8)
        };
    }

    public static Object[] toArray(char arg1, char arg2, char arg3, char arg4,
                                   char arg5, char arg6, char arg7, char arg8,
                                   char arg9) {
        return new Object[] {
            new Character(arg1), new Character(arg2), new Character(arg3),
            new Character(arg4), new Character(arg5), new Character(arg6),
            new Character(arg7), new Character(arg8), new Character(arg9)
        };
    }

    public static Object[] toArray(boolean arg1) {
        return new Object[] { new Boolean(arg1) };
    }

    public static Object[] toArray(boolean arg1, boolean arg2) {
        return new Object[] { new Boolean(arg1), new Boolean(arg2) };
    }

    public static Object[] toArray(boolean arg1, boolean arg2, boolean arg3) {
        return new Object[] {
            new Boolean(arg1), new Boolean(arg2), new Boolean(arg3)
        };
    }

    public static Object[] toArray(boolean arg1, boolean arg2, boolean arg3,
                                   boolean arg4) {
        return new Object[] {
            new Boolean(arg1), new Boolean(arg2), new Boolean(arg3),
            new Boolean(arg4)
        };
    }

    public static Object[] toArray(boolean arg1, boolean arg2, boolean arg3,
                                   boolean arg4, boolean arg5) {
        return new Object[] {
            new Boolean(arg1), new Boolean(arg2), new Boolean(arg3),
            new Boolean(arg4), new Boolean(arg5)
        };
    }

    public static Object[] toArray(boolean arg1, boolean arg2, boolean arg3,
                                   boolean arg4, boolean arg5, boolean arg6) {
        return new Object[] {
            new Boolean(arg1), new Boolean(arg2), new Boolean(arg3),
            new Boolean(arg4), new Boolean(arg5), new Boolean(arg6)
        };
    }

    public static Object[] toArray(boolean arg1, boolean arg2, boolean arg3,
                                   boolean arg4, boolean arg5, boolean arg6,
                                   boolean arg7) {
        return new Object[] {
            new Boolean(arg1), new Boolean(arg2), new Boolean(arg3),
            new Boolean(arg4), new Boolean(arg5), new Boolean(arg6),
            new Boolean(arg7)
        };
    }

    public static Object[] toArray(boolean arg1, boolean arg2, boolean arg3,
                                   boolean arg4, boolean arg5, boolean arg6,
                                   boolean arg7, boolean arg8) {
        return new Object[] {
            new Boolean(arg1), new Boolean(arg2), new Boolean(arg3),
            new Boolean(arg4), new Boolean(arg5), new Boolean(arg6),
            new Boolean(arg7), new Boolean(arg8)
        };
    }

    public static Object[] toArray(boolean arg1, boolean arg2, boolean arg3,
                                   boolean arg4, boolean arg5, boolean arg6,
                                   boolean arg7, boolean arg8, boolean arg9) {
        return new Object[] {
            new Boolean(arg1), new Boolean(arg2), new Boolean(arg3),
            new Boolean(arg4), new Boolean(arg5), new Boolean(arg6),
            new Boolean(arg7), new Boolean(arg8), new Boolean(arg9)
        };
    }

    public static Object[] toArray(Object arg1) {
        return new Object[] { arg1 };
    }

    public static Object[] toArray(Object arg1, Object arg2) {
        return new Object[] { arg1, arg2 };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3) {
        return new Object[] { arg1, arg2, arg3 };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   Object arg4) {
        return new Object[] { arg1, arg2, arg3, arg4 };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { arg1, arg2, arg3, arg4, arg5 };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   Object arg4, Object arg5, Object arg6) {
        return new Object[] { arg1, arg2, arg3, arg4, arg5, arg6 };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   Object arg4, Object arg5, Object arg6,
                                   Object arg7) {
        return new Object[] { arg1, arg2, arg3, arg4, arg5, arg6, arg7 };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   Object arg4, Object arg5, Object arg6,
                                   Object arg7, Object arg8) {
        return new Object[] { arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8 };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   Object arg4, Object arg5, Object arg6,
                                   Object arg7, Object arg8, Object arg9) {
        return new Object[] {
            arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9
        };
    }

    public static Object[] toArray(int arg1, Object arg2, Object arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { new Integer(arg1), arg2, arg3, arg4, arg5 };
    }

    public static Object[] toArray(Object arg1, int arg2, Object arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { arg1, new Integer(arg2), arg3, arg4, arg5 };
    }

    public static Object[] toArray(int arg1, int arg2, Object arg3, Object arg4,
                                   Object arg5) {
        return new Object[] {
            new Integer(arg1), new Integer(arg2), arg3, arg4, arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, int arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { arg1, arg2, new Integer(arg3), arg4, arg5 };
    }

    public static Object[] toArray(int arg1, Object arg2, int arg3, Object arg4,
                                   Object arg5) {
        return new Object[] {
            new Integer(arg1), arg2, new Integer(arg3), arg4, arg5
        };
    }

    public static Object[] toArray(Object arg1, int arg2, int arg3, Object arg4,
                                   Object arg5) {
        return new Object[] {
            arg1, new Integer(arg2), new Integer(arg3), arg4, arg5
        };
    }

    public static Object[] toArray(int arg1, int arg2, int arg3, Object arg4,
                                   Object arg5) {
        return new Object[] {
            new Integer(arg1), new Integer(arg2), new Integer(arg3), arg4, arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   int arg4, Object arg5) {
        return new Object[] { arg1, arg2, arg3, new Integer(arg4), arg5 };
    }

    public static Object[] toArray(int arg1, Object arg2, Object arg3, int arg4,
                                   Object arg5) {
        return new Object[] {
            new Integer(arg1), arg2, arg3, new Integer(arg4), arg5
        };
    }

    public static Object[] toArray(Object arg1, int arg2, Object arg3, int arg4,
                                   Object arg5) {
        return new Object[] {
            arg1, new Integer(arg2), arg3, new Integer(arg4), arg5
        };
    }

    public static Object[] toArray(int arg1, int arg2, Object arg3, int arg4,
                                   Object arg5) {
        return new Object[] {
            new Integer(arg1), new Integer(arg2), arg3, new Integer(arg4), arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, int arg3, int arg4,
                                   Object arg5) {
        return new Object[] {
            arg1, arg2, new Integer(arg3), new Integer(arg4), arg5
        };
    }

    public static Object[] toArray(int arg1, Object arg2, int arg3, int arg4,
                                   Object arg5) {
        return new Object[] {
            new Integer(arg1), arg2, new Integer(arg3), new Integer(arg4), arg5
        };
    }

    public static Object[] toArray(Object arg1, int arg2, int arg3, int arg4,
                                   Object arg5) {
        return new Object[] {
            arg1, new Integer(arg2), new Integer(arg3), new Integer(arg4), arg5
        };
    }

    public static Object[] toArray(int arg1, int arg2, int arg3, int arg4,
                                   Object arg5) {
        return new Object[] {
            new Integer(arg1), new Integer(arg2), new Integer(arg3),
            new Integer(arg4), arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   Object arg4, int arg5) {
        return new Object[] { arg1, arg2, arg3, arg4, new Integer(arg5) };
    }

    public static Object[] toArray(int arg1, Object arg2, Object arg3,
                                   Object arg4, int arg5) {
        return new Object[] {
            new Integer(arg1), arg2, arg3, arg4, new Integer(arg5)
        };
    }

    public static Object[] toArray(Object arg1, int arg2, Object arg3,
                                   Object arg4, int arg5) {
        return new Object[] {
            arg1, new Integer(arg2), arg3, arg4, new Integer(arg5)
        };
    }

    public static Object[] toArray(int arg1, int arg2, Object arg3, Object arg4,
                                   int arg5) {
        return new Object[] {
            new Integer(arg1), new Integer(arg2), arg3, arg4, new Integer(arg5)
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, int arg3,
                                   Object arg4, int arg5) {
        return new Object[] {
            arg1, arg2, new Integer(arg3), arg4, new Integer(arg5)
        };
    }

    public static Object[] toArray(int arg1, Object arg2, int arg3, Object arg4,
                                   int arg5) {
        return new Object[] {
            new Integer(arg1), arg2, new Integer(arg3), arg4, new Integer(arg5)
        };
    }

    public static Object[] toArray(Object arg1, int arg2, int arg3, Object arg4,
                                   int arg5) {
        return new Object[] {
            arg1, new Integer(arg2), new Integer(arg3), arg4, new Integer(arg5)
        };
    }

    public static Object[] toArray(int arg1, int arg2, int arg3, Object arg4,
                                   int arg5) {
        return new Object[] {
            new Integer(arg1), new Integer(arg2), new Integer(arg3), arg4,
            new Integer(arg5)
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   int arg4, int arg5) {
        return new Object[] {
            arg1, arg2, arg3, new Integer(arg4), new Integer(arg5)
        };
    }

    public static Object[] toArray(int arg1, Object arg2, Object arg3, int arg4,
                                   int arg5) {
        return new Object[] {
            new Integer(arg1), arg2, arg3, new Integer(arg4), new Integer(arg5)
        };
    }

    public static Object[] toArray(Object arg1, int arg2, Object arg3, int arg4,
                                   int arg5) {
        return new Object[] {
            arg1, new Integer(arg2), arg3, new Integer(arg4), new Integer(arg5)
        };
    }

    public static Object[] toArray(int arg1, int arg2, Object arg3, int arg4,
                                   int arg5) {
        return new Object[] {
            new Integer(arg1), new Integer(arg2), arg3, new Integer(arg4),
            new Integer(arg5)
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, int arg3, int arg4,
                                   int arg5) {
        return new Object[] {
            arg1, arg2, new Integer(arg3), new Integer(arg4), new Integer(arg5)
        };
    }

    public static Object[] toArray(int arg1, Object arg2, int arg3, int arg4,
                                   int arg5) {
        return new Object[] {
            new Integer(arg1), arg2, new Integer(arg3), new Integer(arg4),
            new Integer(arg5)
        };
    }

    public static Object[] toArray(Object arg1, int arg2, int arg3, int arg4,
                                   int arg5) {
        return new Object[] {
            arg1, new Integer(arg2), new Integer(arg3), new Integer(arg4),
            new Integer(arg5)
        };
    }

    public static Object[] toArray(boolean arg1, Object arg2, Object arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { new Boolean(arg1), arg2, arg3, arg4, arg5 };
    }

    public static Object[] toArray(Object arg1, boolean arg2, Object arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { arg1, new Boolean(arg2), arg3, arg4, arg5 };
    }

    public static Object[] toArray(boolean arg1, boolean arg2, Object arg3,
                                   Object arg4, Object arg5) {
        return new Object[] {
            new Boolean(arg1), new Boolean(arg2), arg3, arg4, arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, boolean arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { arg1, arg2, new Boolean(arg3), arg4, arg5 };
    }

    public static Object[] toArray(boolean arg1, Object arg2, boolean arg3,
                                   Object arg4, Object arg5) {
        return new Object[] {
            new Boolean(arg1), arg2, new Boolean(arg3), arg4, arg5
        };
    }

    public static Object[] toArray(Object arg1, boolean arg2, boolean arg3,
                                   Object arg4, Object arg5) {
        return new Object[] {
            arg1, new Boolean(arg2), new Boolean(arg3), arg4, arg5
        };
    }

    public static Object[] toArray(boolean arg1, boolean arg2, boolean arg3,
                                   Object arg4, Object arg5) {
        return new Object[] {
            new Boolean(arg1), new Boolean(arg2), new Boolean(arg3), arg4, arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   boolean arg4, Object arg5) {
        return new Object[] { arg1, arg2, arg3, new Boolean(arg4), arg5 };
    }

    public static Object[] toArray(boolean arg1, Object arg2, Object arg3,
                                   boolean arg4, Object arg5) {
        return new Object[] {
            new Boolean(arg1), arg2, arg3, new Boolean(arg4), arg5
        };
    }

    public static Object[] toArray(Object arg1, boolean arg2, Object arg3,
                                   boolean arg4, Object arg5) {
        return new Object[] {
            arg1, new Boolean(arg2), arg3, new Boolean(arg4), arg5
        };
    }

    public static Object[] toArray(boolean arg1, boolean arg2, Object arg3,
                                   boolean arg4, Object arg5) {
        return new Object[] {
            new Boolean(arg1), new Boolean(arg2), arg3, new Boolean(arg4), arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, boolean arg3,
                                   boolean arg4, Object arg5) {
        return new Object[] {
            arg1, arg2, new Boolean(arg3), new Boolean(arg4), arg5
        };
    }

    public static Object[] toArray(boolean arg1, Object arg2, boolean arg3,
                                   boolean arg4, Object arg5) {
        return new Object[] {
            new Boolean(arg1), arg2, new Boolean(arg3), new Boolean(arg4), arg5
        };
    }

    public static Object[] toArray(Object arg1, boolean arg2, boolean arg3,
                                   boolean arg4, Object arg5) {
        return new Object[] {
            arg1, new Boolean(arg2), new Boolean(arg3), new Boolean(arg4), arg5
        };
    }

    public static Object[] toArray(boolean arg1, boolean arg2, boolean arg3,
                                   boolean arg4, Object arg5) {
        return new Object[] {
            new Boolean(arg1), new Boolean(arg2), new Boolean(arg3),
            new Boolean(arg4), arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   Object arg4, boolean arg5) {
        return new Object[] { arg1, arg2, arg3, arg4, new Boolean(arg5) };
    }

    public static Object[] toArray(boolean arg1, Object arg2, Object arg3,
                                   Object arg4, boolean arg5) {
        return new Object[] {
            new Boolean(arg1), arg2, arg3, arg4, new Boolean(arg5)
        };
    }

    public static Object[] toArray(Object arg1, boolean arg2, Object arg3,
                                   Object arg4, boolean arg5) {
        return new Object[] {
            arg1, new Boolean(arg2), arg3, arg4, new Boolean(arg5)
        };
    }

    public static Object[] toArray(boolean arg1, boolean arg2, Object arg3,
                                   Object arg4, boolean arg5) {
        return new Object[] {
            new Boolean(arg1), new Boolean(arg2), arg3, arg4, new Boolean(arg5)
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, boolean arg3,
                                   Object arg4, boolean arg5) {
        return new Object[] {
            arg1, arg2, new Boolean(arg3), arg4, new Boolean(arg5)
        };
    }

    public static Object[] toArray(boolean arg1, Object arg2, boolean arg3,
                                   Object arg4, boolean arg5) {
        return new Object[] {
            new Boolean(arg1), arg2, new Boolean(arg3), arg4, new Boolean(arg5)
        };
    }

    public static Object[] toArray(Object arg1, boolean arg2, boolean arg3,
                                   Object arg4, boolean arg5) {
        return new Object[] {
            arg1, new Boolean(arg2), new Boolean(arg3), arg4, new Boolean(arg5)
        };
    }

    public static Object[] toArray(boolean arg1, boolean arg2, boolean arg3,
                                   Object arg4, boolean arg5) {
        return new Object[] {
            new Boolean(arg1), new Boolean(arg2), new Boolean(arg3), arg4,
            new Boolean(arg5)
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   boolean arg4, boolean arg5) {
        return new Object[] {
            arg1, arg2, arg3, new Boolean(arg4), new Boolean(arg5)
        };
    }

    public static Object[] toArray(boolean arg1, Object arg2, Object arg3,
                                   boolean arg4, boolean arg5) {
        return new Object[] {
            new Boolean(arg1), arg2, arg3, new Boolean(arg4), new Boolean(arg5)
        };
    }

    public static Object[] toArray(Object arg1, boolean arg2, Object arg3,
                                   boolean arg4, boolean arg5) {
        return new Object[] {
            arg1, new Boolean(arg2), arg3, new Boolean(arg4), new Boolean(arg5)
        };
    }

    public static Object[] toArray(boolean arg1, boolean arg2, Object arg3,
                                   boolean arg4, boolean arg5) {
        return new Object[] {
            new Boolean(arg1), new Boolean(arg2), arg3, new Boolean(arg4),
            new Boolean(arg5)
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, boolean arg3,
                                   boolean arg4, boolean arg5) {
        return new Object[] {
            arg1, arg2, new Boolean(arg3), new Boolean(arg4), new Boolean(arg5)
        };
    }

    public static Object[] toArray(boolean arg1, Object arg2, boolean arg3,
                                   boolean arg4, boolean arg5) {
        return new Object[] {
            new Boolean(arg1), arg2, new Boolean(arg3), new Boolean(arg4),
            new Boolean(arg5)
        };
    }

    public static Object[] toArray(Object arg1, boolean arg2, boolean arg3,
                                   boolean arg4, boolean arg5) {
        return new Object[] {
            arg1, new Boolean(arg2), new Boolean(arg3), new Boolean(arg4),
            new Boolean(arg5)
        };
    }

    public static Object[] toArray(char arg1, Object arg2, Object arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { new Character(arg1), arg2, arg3, arg4, arg5 };
    }

    public static Object[] toArray(Object arg1, char arg2, Object arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { arg1, new Character(arg2), arg3, arg4, arg5 };
    }

    public static Object[] toArray(char arg1, char arg2, Object arg3,
                                   Object arg4, Object arg5) {
        return new Object[] {
            new Character(arg1), new Character(arg2), arg3, arg4, arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, char arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { arg1, arg2, new Character(arg3), arg4, arg5 };
    }

    public static Object[] toArray(char arg1, Object arg2, char arg3,
                                   Object arg4, Object arg5) {
        return new Object[] {
            new Character(arg1), arg2, new Character(arg3), arg4, arg5
        };
    }

    public static Object[] toArray(Object arg1, char arg2, char arg3,
                                   Object arg4, Object arg5) {
        return new Object[] {
            arg1, new Character(arg2), new Character(arg3), arg4, arg5
        };
    }

    public static Object[] toArray(char arg1, char arg2, char arg3, Object arg4,
                                   Object arg5) {
        return new Object[] {
            new Character(arg1), new Character(arg2), new Character(arg3), arg4,
            arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   char arg4, Object arg5) {
        return new Object[] { arg1, arg2, arg3, new Character(arg4), arg5 };
    }

    public static Object[] toArray(char arg1, Object arg2, Object arg3,
                                   char arg4, Object arg5) {
        return new Object[] {
            new Character(arg1), arg2, arg3, new Character(arg4), arg5
        };
    }

    public static Object[] toArray(Object arg1, char arg2, Object arg3,
                                   char arg4, Object arg5) {
        return new Object[] {
            arg1, new Character(arg2), arg3, new Character(arg4), arg5
        };
    }

    public static Object[] toArray(char arg1, char arg2, Object arg3, char arg4,
                                   Object arg5) {
        return new Object[] {
            new Character(arg1), new Character(arg2), arg3, new Character(arg4),
            arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, char arg3,
                                   char arg4, Object arg5) {
        return new Object[] {
            arg1, arg2, new Character(arg3), new Character(arg4), arg5
        };
    }

    public static Object[] toArray(char arg1, Object arg2, char arg3, char arg4,
                                   Object arg5) {
        return new Object[] {
            new Character(arg1), arg2, new Character(arg3), new Character(arg4),
            arg5
        };
    }

    public static Object[] toArray(Object arg1, char arg2, char arg3, char arg4,
                                   Object arg5) {
        return new Object[] {
            arg1, new Character(arg2), new Character(arg3), new Character(arg4),
            arg5
        };
    }

    public static Object[] toArray(char arg1, char arg2, char arg3, char arg4,
                                   Object arg5) {
        return new Object[] {
            new Character(arg1), new Character(arg2), new Character(arg3),
            new Character(arg4), arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   Object arg4, char arg5) {
        return new Object[] { arg1, arg2, arg3, arg4, new Character(arg5) };
    }

    public static Object[] toArray(char arg1, Object arg2, Object arg3,
                                   Object arg4, char arg5) {
        return new Object[] {
            new Character(arg1), arg2, arg3, arg4, new Character(arg5)
        };
    }

    public static Object[] toArray(Object arg1, char arg2, Object arg3,
                                   Object arg4, char arg5) {
        return new Object[] {
            arg1, new Character(arg2), arg3, arg4, new Character(arg5)
        };
    }

    public static Object[] toArray(char arg1, char arg2, Object arg3,
                                   Object arg4, char arg5) {
        return new Object[] {
            new Character(arg1), new Character(arg2), arg3, arg4,
            new Character(arg5)
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, char arg3,
                                   Object arg4, char arg5) {
        return new Object[] {
            arg1, arg2, new Character(arg3), arg4, new Character(arg5)
        };
    }

    public static Object[] toArray(char arg1, Object arg2, char arg3,
                                   Object arg4, char arg5) {
        return new Object[] {
            new Character(arg1), arg2, new Character(arg3), arg4,
            new Character(arg5)
        };
    }

    public static Object[] toArray(Object arg1, char arg2, char arg3,
                                   Object arg4, char arg5) {
        return new Object[] {
            arg1, new Character(arg2), new Character(arg3), arg4,
            new Character(arg5)
        };
    }

    public static Object[] toArray(char arg1, char arg2, char arg3, Object arg4,
                                   char arg5) {
        return new Object[] {
            new Character(arg1), new Character(arg2), new Character(arg3), arg4,
            new Character(arg5)
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   char arg4, char arg5) {
        return new Object[] {
            arg1, arg2, arg3, new Character(arg4), new Character(arg5)
        };
    }

    public static Object[] toArray(char arg1, Object arg2, Object arg3,
                                   char arg4, char arg5) {
        return new Object[] {
            new Character(arg1), arg2, arg3, new Character(arg4),
            new Character(arg5)
        };
    }

    public static Object[] toArray(Object arg1, char arg2, Object arg3,
                                   char arg4, char arg5) {
        return new Object[] {
            arg1, new Character(arg2), arg3, new Character(arg4),
            new Character(arg5)
        };
    }

    public static Object[] toArray(char arg1, char arg2, Object arg3, char arg4,
                                   char arg5) {
        return new Object[] {
            new Character(arg1), new Character(arg2), arg3, new Character(arg4),
            new Character(arg5)
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, char arg3,
                                   char arg4, char arg5) {
        return new Object[] {
            arg1, arg2, new Character(arg3), new Character(arg4),
            new Character(arg5)
        };
    }

    public static Object[] toArray(char arg1, Object arg2, char arg3, char arg4,
                                   char arg5) {
        return new Object[] {
            new Character(arg1), arg2, new Character(arg3), new Character(arg4),
            new Character(arg5)
        };
    }

    public static Object[] toArray(Object arg1, char arg2, char arg3, char arg4,
                                   char arg5) {
        return new Object[] {
            arg1, new Character(arg2), new Character(arg3), new Character(arg4),
            new Character(arg5)
        };
    }

    public static Object[] toArray(double arg1, Object arg2, Object arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { new Double(arg1), arg2, arg3, arg4, arg5 };
    }

    public static Object[] toArray(Object arg1, double arg2, Object arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { arg1, new Double(arg2), arg3, arg4, arg5 };
    }

    public static Object[] toArray(double arg1, double arg2, Object arg3,
                                   Object arg4, Object arg5) {
        return new Object[] {
            new Double(arg1), new Double(arg2), arg3, arg4, arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, double arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { arg1, arg2, new Double(arg3), arg4, arg5 };
    }

    public static Object[] toArray(double arg1, Object arg2, double arg3,
                                   Object arg4, Object arg5) {
        return new Object[] {
            new Double(arg1), arg2, new Double(arg3), arg4, arg5
        };
    }

    public static Object[] toArray(Object arg1, double arg2, double arg3,
                                   Object arg4, Object arg5) {
        return new Object[] {
            arg1, new Double(arg2), new Double(arg3), arg4, arg5
        };
    }

    public static Object[] toArray(double arg1, double arg2, double arg3,
                                   Object arg4, Object arg5) {
        return new Object[] {
            new Double(arg1), new Double(arg2), new Double(arg3), arg4, arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   double arg4, Object arg5) {
        return new Object[] { arg1, arg2, arg3, new Double(arg4), arg5 };
    }

    public static Object[] toArray(double arg1, Object arg2, Object arg3,
                                   double arg4, Object arg5) {
        return new Object[] {
            new Double(arg1), arg2, arg3, new Double(arg4), arg5
        };
    }

    public static Object[] toArray(Object arg1, double arg2, Object arg3,
                                   double arg4, Object arg5) {
        return new Object[] {
            arg1, new Double(arg2), arg3, new Double(arg4), arg5
        };
    }

    public static Object[] toArray(double arg1, double arg2, Object arg3,
                                   double arg4, Object arg5) {
        return new Object[] {
            new Double(arg1), new Double(arg2), arg3, new Double(arg4), arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, double arg3,
                                   double arg4, Object arg5) {
        return new Object[] {
            arg1, arg2, new Double(arg3), new Double(arg4), arg5
        };
    }

    public static Object[] toArray(double arg1, Object arg2, double arg3,
                                   double arg4, Object arg5) {
        return new Object[] {
            new Double(arg1), arg2, new Double(arg3), new Double(arg4), arg5
        };
    }

    public static Object[] toArray(Object arg1, double arg2, double arg3,
                                   double arg4, Object arg5) {
        return new Object[] {
            arg1, new Double(arg2), new Double(arg3), new Double(arg4), arg5
        };
    }

    public static Object[] toArray(double arg1, double arg2, double arg3,
                                   double arg4, Object arg5) {
        return new Object[] {
            new Double(arg1), new Double(arg2), new Double(arg3),
            new Double(arg4), arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   Object arg4, double arg5) {
        return new Object[] { arg1, arg2, arg3, arg4, new Double(arg5) };
    }

    public static Object[] toArray(double arg1, Object arg2, Object arg3,
                                   Object arg4, double arg5) {
        return new Object[] {
            new Double(arg1), arg2, arg3, arg4, new Double(arg5)
        };
    }

    public static Object[] toArray(Object arg1, double arg2, Object arg3,
                                   Object arg4, double arg5) {
        return new Object[] {
            arg1, new Double(arg2), arg3, arg4, new Double(arg5)
        };
    }

    public static Object[] toArray(double arg1, double arg2, Object arg3,
                                   Object arg4, double arg5) {
        return new Object[] {
            new Double(arg1), new Double(arg2), arg3, arg4, new Double(arg5)
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, double arg3,
                                   Object arg4, double arg5) {
        return new Object[] {
            arg1, arg2, new Double(arg3), arg4, new Double(arg5)
        };
    }

    public static Object[] toArray(double arg1, Object arg2, double arg3,
                                   Object arg4, double arg5) {
        return new Object[] {
            new Double(arg1), arg2, new Double(arg3), arg4, new Double(arg5)
        };
    }

    public static Object[] toArray(Object arg1, double arg2, double arg3,
                                   Object arg4, double arg5) {
        return new Object[] {
            arg1, new Double(arg2), new Double(arg3), arg4, new Double(arg5)
        };
    }

    public static Object[] toArray(double arg1, double arg2, double arg3,
                                   Object arg4, double arg5) {
        return new Object[] {
            new Double(arg1), new Double(arg2), new Double(arg3), arg4,
            new Double(arg5)
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   double arg4, double arg5) {
        return new Object[] {
            arg1, arg2, arg3, new Double(arg4), new Double(arg5)
        };
    }

    public static Object[] toArray(double arg1, Object arg2, Object arg3,
                                   double arg4, double arg5) {
        return new Object[] {
            new Double(arg1), arg2, arg3, new Double(arg4), new Double(arg5)
        };
    }

    public static Object[] toArray(Object arg1, double arg2, Object arg3,
                                   double arg4, double arg5) {
        return new Object[] {
            arg1, new Double(arg2), arg3, new Double(arg4), new Double(arg5)
        };
    }

    public static Object[] toArray(double arg1, double arg2, Object arg3,
                                   double arg4, double arg5) {
        return new Object[] {
            new Double(arg1), new Double(arg2), arg3, new Double(arg4),
            new Double(arg5)
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, double arg3,
                                   double arg4, double arg5) {
        return new Object[] {
            arg1, arg2, new Double(arg3), new Double(arg4), new Double(arg5)
        };
    }

    public static Object[] toArray(double arg1, Object arg2, double arg3,
                                   double arg4, double arg5) {
        return new Object[] {
            new Double(arg1), arg2, new Double(arg3), new Double(arg4),
            new Double(arg5)
        };
    }

    public static Object[] toArray(Object arg1, double arg2, double arg3,
                                   double arg4, double arg5) {
        return new Object[] {
            arg1, new Double(arg2), new Double(arg3), new Double(arg4),
            new Double(arg5)
        };
    }

    public static Object[] toArray(float arg1, Object arg2, Object arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { new Float(arg1), arg2, arg3, arg4, arg5 };
    }

    public static Object[] toArray(Object arg1, float arg2, Object arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { arg1, new Float(arg2), arg3, arg4, arg5 };
    }

    public static Object[] toArray(float arg1, float arg2, Object arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { new Float(arg1), new Float(arg2), arg3, arg4, arg5 };
    }

    public static Object[] toArray(Object arg1, Object arg2, float arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { arg1, arg2, new Float(arg3), arg4, arg5 };
    }

    public static Object[] toArray(float arg1, Object arg2, float arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { new Float(arg1), arg2, new Float(arg3), arg4, arg5 };
    }

    public static Object[] toArray(Object arg1, float arg2, float arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { arg1, new Float(arg2), new Float(arg3), arg4, arg5 };
    }

    public static Object[] toArray(float arg1, float arg2, float arg3,
                                   Object arg4, Object arg5) {
        return new Object[] {
            new Float(arg1), new Float(arg2), new Float(arg3), arg4, arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   float arg4, Object arg5) {
        return new Object[] { arg1, arg2, arg3, new Float(arg4), arg5 };
    }

    public static Object[] toArray(float arg1, Object arg2, Object arg3,
                                   float arg4, Object arg5) {
        return new Object[] { new Float(arg1), arg2, arg3, new Float(arg4), arg5 };
    }

    public static Object[] toArray(Object arg1, float arg2, Object arg3,
                                   float arg4, Object arg5) {
        return new Object[] { arg1, new Float(arg2), arg3, new Float(arg4), arg5 };
    }

    public static Object[] toArray(float arg1, float arg2, Object arg3,
                                   float arg4, Object arg5) {
        return new Object[] {
            new Float(arg1), new Float(arg2), arg3, new Float(arg4), arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, float arg3,
                                   float arg4, Object arg5) {
        return new Object[] { arg1, arg2, new Float(arg3), new Float(arg4), arg5 };
    }

    public static Object[] toArray(float arg1, Object arg2, float arg3,
                                   float arg4, Object arg5) {
        return new Object[] {
            new Float(arg1), arg2, new Float(arg3), new Float(arg4), arg5
        };
    }

    public static Object[] toArray(Object arg1, float arg2, float arg3,
                                   float arg4, Object arg5) {
        return new Object[] {
            arg1, new Float(arg2), new Float(arg3), new Float(arg4), arg5
        };
    }

    public static Object[] toArray(float arg1, float arg2, float arg3,
                                   float arg4, Object arg5) {
        return new Object[] {
            new Float(arg1), new Float(arg2), new Float(arg3), new Float(arg4),
            arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   Object arg4, float arg5) {
        return new Object[] { arg1, arg2, arg3, arg4, new Float(arg5) };
    }

    public static Object[] toArray(float arg1, Object arg2, Object arg3,
                                   Object arg4, float arg5) {
        return new Object[] { new Float(arg1), arg2, arg3, arg4, new Float(arg5) };
    }

    public static Object[] toArray(Object arg1, float arg2, Object arg3,
                                   Object arg4, float arg5) {
        return new Object[] { arg1, new Float(arg2), arg3, arg4, new Float(arg5) };
    }

    public static Object[] toArray(float arg1, float arg2, Object arg3,
                                   Object arg4, float arg5) {
        return new Object[] {
            new Float(arg1), new Float(arg2), arg3, arg4, new Float(arg5)
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, float arg3,
                                   Object arg4, float arg5) {
        return new Object[] { arg1, arg2, new Float(arg3), arg4, new Float(arg5) };
    }

    public static Object[] toArray(float arg1, Object arg2, float arg3,
                                   Object arg4, float arg5) {
        return new Object[] {
            new Float(arg1), arg2, new Float(arg3), arg4, new Float(arg5)
        };
    }

    public static Object[] toArray(Object arg1, float arg2, float arg3,
                                   Object arg4, float arg5) {
        return new Object[] {
            arg1, new Float(arg2), new Float(arg3), arg4, new Float(arg5)
        };
    }

    public static Object[] toArray(float arg1, float arg2, float arg3,
                                   Object arg4, float arg5) {
        return new Object[] {
            new Float(arg1), new Float(arg2), new Float(arg3), arg4,
            new Float(arg5)
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   float arg4, float arg5) {
        return new Object[] { arg1, arg2, arg3, new Float(arg4), new Float(arg5) };
    }

    public static Object[] toArray(float arg1, Object arg2, Object arg3,
                                   float arg4, float arg5) {
        return new Object[] {
            new Float(arg1), arg2, arg3, new Float(arg4), new Float(arg5)
        };
    }

    public static Object[] toArray(Object arg1, float arg2, Object arg3,
                                   float arg4, float arg5) {
        return new Object[] {
            arg1, new Float(arg2), arg3, new Float(arg4), new Float(arg5)
        };
    }

    public static Object[] toArray(float arg1, float arg2, Object arg3,
                                   float arg4, float arg5) {
        return new Object[] {
            new Float(arg1), new Float(arg2), arg3, new Float(arg4),
            new Float(arg5)
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, float arg3,
                                   float arg4, float arg5) {
        return new Object[] {
            arg1, arg2, new Float(arg3), new Float(arg4), new Float(arg5)
        };
    }

    public static Object[] toArray(float arg1, Object arg2, float arg3,
                                   float arg4, float arg5) {
        return new Object[] {
            new Float(arg1), arg2, new Float(arg3), new Float(arg4),
            new Float(arg5)
        };
    }

    public static Object[] toArray(Object arg1, float arg2, float arg3,
                                   float arg4, float arg5) {
        return new Object[] {
            arg1, new Float(arg2), new Float(arg3), new Float(arg4),
            new Float(arg5)
        };
    }

    public static Object[] toArray(long arg1, Object arg2, Object arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { new Long(arg1), arg2, arg3, arg4, arg5 };
    }

    public static Object[] toArray(Object arg1, long arg2, Object arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { arg1, new Long(arg2), arg3, arg4, arg5 };
    }

    public static Object[] toArray(long arg1, long arg2, Object arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { new Long(arg1), new Long(arg2), arg3, arg4, arg5 };
    }

    public static Object[] toArray(Object arg1, Object arg2, long arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { arg1, arg2, new Long(arg3), arg4, arg5 };
    }

    public static Object[] toArray(long arg1, Object arg2, long arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { new Long(arg1), arg2, new Long(arg3), arg4, arg5 };
    }

    public static Object[] toArray(Object arg1, long arg2, long arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { arg1, new Long(arg2), new Long(arg3), arg4, arg5 };
    }

    public static Object[] toArray(long arg1, long arg2, long arg3, Object arg4,
                                   Object arg5) {
        return new Object[] {
            new Long(arg1), new Long(arg2), new Long(arg3), arg4, arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   long arg4, Object arg5) {
        return new Object[] { arg1, arg2, arg3, new Long(arg4), arg5 };
    }

    public static Object[] toArray(long arg1, Object arg2, Object arg3,
                                   long arg4, Object arg5) {
        return new Object[] { new Long(arg1), arg2, arg3, new Long(arg4), arg5 };
    }

    public static Object[] toArray(Object arg1, long arg2, Object arg3,
                                   long arg4, Object arg5) {
        return new Object[] { arg1, new Long(arg2), arg3, new Long(arg4), arg5 };
    }

    public static Object[] toArray(long arg1, long arg2, Object arg3, long arg4,
                                   Object arg5) {
        return new Object[] {
            new Long(arg1), new Long(arg2), arg3, new Long(arg4), arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, long arg3,
                                   long arg4, Object arg5) {
        return new Object[] { arg1, arg2, new Long(arg3), new Long(arg4), arg5 };
    }

    public static Object[] toArray(long arg1, Object arg2, long arg3, long arg4,
                                   Object arg5) {
        return new Object[] {
            new Long(arg1), arg2, new Long(arg3), new Long(arg4), arg5
        };
    }

    public static Object[] toArray(Object arg1, long arg2, long arg3, long arg4,
                                   Object arg5) {
        return new Object[] {
            arg1, new Long(arg2), new Long(arg3), new Long(arg4), arg5
        };
    }

    public static Object[] toArray(long arg1, long arg2, long arg3, long arg4,
                                   Object arg5) {
        return new Object[] {
            new Long(arg1), new Long(arg2), new Long(arg3), new Long(arg4),
            arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   Object arg4, long arg5) {
        return new Object[] { arg1, arg2, arg3, arg4, new Long(arg5) };
    }

    public static Object[] toArray(long arg1, Object arg2, Object arg3,
                                   Object arg4, long arg5) {
        return new Object[] { new Long(arg1), arg2, arg3, arg4, new Long(arg5) };
    }

    public static Object[] toArray(Object arg1, long arg2, Object arg3,
                                   Object arg4, long arg5) {
        return new Object[] { arg1, new Long(arg2), arg3, arg4, new Long(arg5) };
    }

    public static Object[] toArray(long arg1, long arg2, Object arg3,
                                   Object arg4, long arg5) {
        return new Object[] {
            new Long(arg1), new Long(arg2), arg3, arg4, new Long(arg5)
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, long arg3,
                                   Object arg4, long arg5) {
        return new Object[] { arg1, arg2, new Long(arg3), arg4, new Long(arg5) };
    }

    public static Object[] toArray(long arg1, Object arg2, long arg3,
                                   Object arg4, long arg5) {
        return new Object[] {
            new Long(arg1), arg2, new Long(arg3), arg4, new Long(arg5)
        };
    }

    public static Object[] toArray(Object arg1, long arg2, long arg3,
                                   Object arg4, long arg5) {
        return new Object[] {
            arg1, new Long(arg2), new Long(arg3), arg4, new Long(arg5)
        };
    }

    public static Object[] toArray(long arg1, long arg2, long arg3, Object arg4,
                                   long arg5) {
        return new Object[] {
            new Long(arg1), new Long(arg2), new Long(arg3), arg4,
            new Long(arg5)
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   long arg4, long arg5) {
        return new Object[] { arg1, arg2, arg3, new Long(arg4), new Long(arg5) };
    }

    public static Object[] toArray(long arg1, Object arg2, Object arg3,
                                   long arg4, long arg5) {
        return new Object[] {
            new Long(arg1), arg2, arg3, new Long(arg4), new Long(arg5)
        };
    }

    public static Object[] toArray(Object arg1, long arg2, Object arg3,
                                   long arg4, long arg5) {
        return new Object[] {
            arg1, new Long(arg2), arg3, new Long(arg4), new Long(arg5)
        };
    }

    public static Object[] toArray(long arg1, long arg2, Object arg3, long arg4,
                                   long arg5) {
        return new Object[] {
            new Long(arg1), new Long(arg2), arg3, new Long(arg4),
            new Long(arg5)
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, long arg3,
                                   long arg4, long arg5) {
        return new Object[] {
            arg1, arg2, new Long(arg3), new Long(arg4), new Long(arg5)
        };
    }

    public static Object[] toArray(long arg1, Object arg2, long arg3, long arg4,
                                   long arg5) {
        return new Object[] {
            new Long(arg1), arg2, new Long(arg3), new Long(arg4),
            new Long(arg5)
        };
    }

    public static Object[] toArray(Object arg1, long arg2, long arg3, long arg4,
                                   long arg5) {
        return new Object[] {
            arg1, new Long(arg2), new Long(arg3), new Long(arg4),
            new Long(arg5)
        };
    }

    public static Object[] toArray(short arg1, Object arg2, Object arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { new Short(arg1), arg2, arg3, arg4, arg5 };
    }

    public static Object[] toArray(Object arg1, short arg2, Object arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { arg1, new Short(arg2), arg3, arg4, arg5 };
    }

    public static Object[] toArray(short arg1, short arg2, Object arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { new Short(arg1), new Short(arg2), arg3, arg4, arg5 };
    }

    public static Object[] toArray(Object arg1, Object arg2, short arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { arg1, arg2, new Short(arg3), arg4, arg5 };
    }

    public static Object[] toArray(short arg1, Object arg2, short arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { new Short(arg1), arg2, new Short(arg3), arg4, arg5 };
    }

    public static Object[] toArray(Object arg1, short arg2, short arg3,
                                   Object arg4, Object arg5) {
        return new Object[] { arg1, new Short(arg2), new Short(arg3), arg4, arg5 };
    }

    public static Object[] toArray(short arg1, short arg2, short arg3,
                                   Object arg4, Object arg5) {
        return new Object[] {
            new Short(arg1), new Short(arg2), new Short(arg3), arg4, arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   short arg4, Object arg5) {
        return new Object[] { arg1, arg2, arg3, new Short(arg4), arg5 };
    }

    public static Object[] toArray(short arg1, Object arg2, Object arg3,
                                   short arg4, Object arg5) {
        return new Object[] { new Short(arg1), arg2, arg3, new Short(arg4), arg5 };
    }

    public static Object[] toArray(Object arg1, short arg2, Object arg3,
                                   short arg4, Object arg5) {
        return new Object[] { arg1, new Short(arg2), arg3, new Short(arg4), arg5 };
    }

    public static Object[] toArray(short arg1, short arg2, Object arg3,
                                   short arg4, Object arg5) {
        return new Object[] {
            new Short(arg1), new Short(arg2), arg3, new Short(arg4), arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, short arg3,
                                   short arg4, Object arg5) {
        return new Object[] { arg1, arg2, new Short(arg3), new Short(arg4), arg5 };
    }

    public static Object[] toArray(short arg1, Object arg2, short arg3,
                                   short arg4, Object arg5) {
        return new Object[] {
            new Short(arg1), arg2, new Short(arg3), new Short(arg4), arg5
        };
    }

    public static Object[] toArray(Object arg1, short arg2, short arg3,
                                   short arg4, Object arg5) {
        return new Object[] {
            arg1, new Short(arg2), new Short(arg3), new Short(arg4), arg5
        };
    }

    public static Object[] toArray(short arg1, short arg2, short arg3,
                                   short arg4, Object arg5) {
        return new Object[] {
            new Short(arg1), new Short(arg2), new Short(arg3), new Short(arg4),
            arg5
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   Object arg4, short arg5) {
        return new Object[] { arg1, arg2, arg3, arg4, new Short(arg5) };
    }

    public static Object[] toArray(short arg1, Object arg2, Object arg3,
                                   Object arg4, short arg5) {
        return new Object[] { new Short(arg1), arg2, arg3, arg4, new Short(arg5) };
    }

    public static Object[] toArray(Object arg1, short arg2, Object arg3,
                                   Object arg4, short arg5) {
        return new Object[] { arg1, new Short(arg2), arg3, arg4, new Short(arg5) };
    }

    public static Object[] toArray(short arg1, short arg2, Object arg3,
                                   Object arg4, short arg5) {
        return new Object[] {
            new Short(arg1), new Short(arg2), arg3, arg4, new Short(arg5)
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, short arg3,
                                   Object arg4, short arg5) {
        return new Object[] { arg1, arg2, new Short(arg3), arg4, new Short(arg5) };
    }

    public static Object[] toArray(short arg1, Object arg2, short arg3,
                                   Object arg4, short arg5) {
        return new Object[] {
            new Short(arg1), arg2, new Short(arg3), arg4, new Short(arg5)
        };
    }

    public static Object[] toArray(Object arg1, short arg2, short arg3,
                                   Object arg4, short arg5) {
        return new Object[] {
            arg1, new Short(arg2), new Short(arg3), arg4, new Short(arg5)
        };
    }

    public static Object[] toArray(short arg1, short arg2, short arg3,
                                   Object arg4, short arg5) {
        return new Object[] {
            new Short(arg1), new Short(arg2), new Short(arg3), arg4,
            new Short(arg5)
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, Object arg3,
                                   short arg4, short arg5) {
        return new Object[] { arg1, arg2, arg3, new Short(arg4), new Short(arg5) };
    }

    public static Object[] toArray(short arg1, Object arg2, Object arg3,
                                   short arg4, short arg5) {
        return new Object[] {
            new Short(arg1), arg2, arg3, new Short(arg4), new Short(arg5)
        };
    }

    public static Object[] toArray(Object arg1, short arg2, Object arg3,
                                   short arg4, short arg5) {
        return new Object[] {
            arg1, new Short(arg2), arg3, new Short(arg4), new Short(arg5)
        };
    }

    public static Object[] toArray(short arg1, short arg2, Object arg3,
                                   short arg4, short arg5) {
        return new Object[] {
            new Short(arg1), new Short(arg2), arg3, new Short(arg4),
            new Short(arg5)
        };
    }

    public static Object[] toArray(Object arg1, Object arg2, short arg3,
                                   short arg4, short arg5) {
        return new Object[] {
            arg1, arg2, new Short(arg3), new Short(arg4), new Short(arg5)
        };
    }

    public static Object[] toArray(short arg1, Object arg2, short arg3,
                                   short arg4, short arg5) {
        return new Object[] {
            new Short(arg1), arg2, new Short(arg3), new Short(arg4),
            new Short(arg5)
        };
    }

    public static Object[] toArray(Object arg1, short arg2, short arg3,
                                   short arg4, short arg5) {
        return new Object[] {
            arg1, new Short(arg2), new Short(arg3), new Short(arg4),
            new Short(arg5)
        };
    }
}