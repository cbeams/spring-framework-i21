/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.aop.framework;

import java.lang.reflect.Method;

import org.aopalliance.intercept.AttributeRegistry;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/**
 * 
 * @author Rod Johnson
 * @since July 22, 2003
 * @version $Id$
 */
public class RegexpMethodPointcut extends AbstractMethodPointcut implements StaticMethodPointcut {
	
	private String pattern;
	
	private Pattern compiledPattern;
	
	private PatternMatcher matcher;
	

	/**
	 * @see com.interface21.aop.framework.StaticMethodPointcut#applies(java.lang.reflect.Method, org.aopalliance.intercept.AttributeRegistry)
	 */
	public boolean applies(Method m, AttributeRegistry attributeRegistry) {
		String patt = m.getDeclaringClass().getName() + "." + m.getName();
		boolean matched =  this.matcher.matches(patt, this.compiledPattern);
		System.err.println("Candidate is: '" + patt + "'; pattern is " + this.compiledPattern.getPattern() + "; matched=" + matched);
		return matched;
	}

	/**
	 * @return
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * @param string
	 */
	public void setPattern(String pattern) throws MalformedPatternException {
		this.pattern = pattern;
		Perl5Compiler compiler = new Perl5Compiler();
		this.compiledPattern = compiler.compile(pattern, Perl5Compiler.READ_ONLY_MASK);
		this.matcher = new Perl5Matcher();
	}

}
