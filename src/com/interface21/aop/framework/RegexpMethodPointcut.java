/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.aop.framework;

import java.lang.reflect.Method;

import org.aopalliance.intercept.AttributeRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/**
 * Method pointcut that matches a Perl5-style regular expression
 * pattern with "fullyQualifiedClassName.methodName".
 * Example: "java\.lang\.Obj.*" would match "java.lang.Object.equals".
 * @author Rod Johnson
 * @since July 22, 2003
 * @version $Id$
 */
public class RegexpMethodPointcut extends AbstractMethodPointcut implements StaticMethodPointcut {

	private Log logger = LogFactory.getLog(getClass());

	private Pattern compiledPattern;
	
	private PatternMatcher matcher;
	
	public void setPattern(String pattern) throws MalformedPatternException {
		Perl5Compiler compiler = new Perl5Compiler();
		this.compiledPattern = compiler.compile(pattern, Perl5Compiler.READ_ONLY_MASK);
		this.matcher = new Perl5Matcher();
	}

	/**
	 * @see StaticMethodPointcut#applies(java.lang.reflect.Method, org.aopalliance.intercept.AttributeRegistry)
	 */
	public boolean applies(Method m, AttributeRegistry attributeRegistry) {
		String patt = m.getDeclaringClass().getName() + "." + m.getName();
		boolean matched =  this.matcher.matches(patt, this.compiledPattern);
		logger.debug("Candidate is: '" + patt + "'; pattern is " + this.compiledPattern.getPattern() + "; matched=" + matched);
		return matched;
	}

}
