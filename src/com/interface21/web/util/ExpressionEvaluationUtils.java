package com.interface21.web.util;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

/**
 * Convenience methods for easy access to the JSP Expression Language
 * evaluator of Jakarta's JSTL implementation.
 *
 * <p>The evaluation methods check if the value starts with "${"
 * before invoking the EL evaluator, treating the value as "normal"
 * expression (i.e. conventional String) else.
 *
 * <p>Note: The evaluation methods do not have a runtime dependency
 * on Jakarta's JSTL implementation, as long as they don't receive
 * actual EL expressions.
 *
 * @author Juergen Hoeller
 * @since 11.07.2003
 */
public abstract class ExpressionEvaluationUtils {

	/**
	 * Check if the given expression value is an EL expression.
	 */
	public static boolean isExpressionLanguage(String value) {
		return value.startsWith("${");
	}

	/**
	 * Evaluate the given expression to an Object, be it EL or a conventional String.
	 * @param attrName name of the attribute (typically a JSP tag attribute)
	 * @param attrValue value of the attribute
	 * @param resultClass class that the result should have (String, Integer, Boolean)
	 * @param pageContext current JSP PageContext
	 * @return the result of the evaluation
	 * @throws JspException in case of parsing errors
	 */
	public static Object evaluate(String attrName, String attrValue, Class resultClass, PageContext pageContext)
	    throws JspException {
		if (isExpressionLanguage(attrValue)) {
			return ExpressionEvaluationHelper.evaluate(attrName, attrValue, resultClass, pageContext);
		}
		else {
			return attrValue;
		}
	}

	/**
	 * Evaluate the given expression to a String, be it EL or a conventional String.
	 * @param attrName name of the attribute (typically a JSP tag attribute)
	 * @param attrValue value of the attribute
	 * @param pageContext current JSP PageContext
	 * @return the result of the evaluation
	 * @throws JspException in case of parsing errors
	 */
	public static String evaluateString(String attrName, String attrValue, PageContext pageContext)
	    throws JspException {
		if (isExpressionLanguage(attrValue)) {
			return (String) ExpressionEvaluationHelper.evaluate(attrName, attrValue, String.class, pageContext);
		}
		else {
			return attrValue;
		}
	}

	/**
	 * Evaluate the given expression to an integer, be it EL or a conventional String.
	 * @param attrName name of the attribute (typically a JSP tag attribute)
	 * @param attrValue value of the attribute
	 * @param pageContext current JSP PageContext
	 * @return the result of the evaluation
	 * @throws JspException in case of parsing errors
	 */
	public static int evaluateInteger(String attrName, String attrValue, PageContext pageContext)
	    throws JspException {
		if (isExpressionLanguage(attrValue)) {
			return ((Integer) ExpressionEvaluationHelper.evaluate(attrName, attrValue, Integer.class, pageContext)).intValue();
		}
		else {
			return Integer.parseInt(attrValue);
		}
	}

	/**
	 * Evaluate the given expression to a boolean, be it EL or a conventional String.
	 * @param attrName name of the attribute (typically a JSP tag attribute)
	 * @param attrValue value of the attribute
	 * @param pageContext current JSP PageContext
	 * @return the result of the evaluation
	 * @throws JspException in case of parsing errors
	 */
	public static boolean evaluateBoolean(String attrName, String attrValue, PageContext pageContext)
	    throws JspException {
		if (isExpressionLanguage(attrValue)) {
			return ((Boolean) ExpressionEvaluationHelper.evaluate(attrName, attrValue, Boolean.class, pageContext)).booleanValue();
		}
		else {
			return Boolean.valueOf(attrValue).booleanValue();
		}
	}


	/**
	 * Actual invocation of the Jakarta ExpressionEvaluatorManager.
	 * In separate inner class to avoid runtime dependency on Jakarta's
	 * JSTL implementation, for evaluation of non-EL expressions.
	 */
	private static class ExpressionEvaluationHelper {

		private static Object evaluate(String attrName, String attrValue, Class resultClass, PageContext pageContext)
		    throws JspException {
			return ExpressionEvaluatorManager.evaluate(attrName, attrValue, resultClass, pageContext);
		}
	}

}
