package com.interface21.web.bind;

import junit.framework.TestCase;

import com.interface21.validation.Errors;
import com.interface21.validation.BindException;
import com.interface21.validation.ObjectError;
import com.interface21.validation.FieldError;
import com.interface21.beans.TestBean;

/**
 * @author Juergen Hoeller
 * @since 02.05.2003
 */
public class EscapedErrorsTestSuite extends TestCase {

	public EscapedErrorsTestSuite(String location) {
		super(location);
	}

	public void testEscapedErrors() {
		TestBean tb = new TestBean();
		tb.setName("empty &");

		Errors errors = new EscapedErrors(new BindException(tb, "tb"));
		errors.rejectValue("name", "NAME_EMPTY &", null, "message: &");
		errors.rejectValue("age", "AGE_NOT_SET <tag>", null, "message: <tag>");
		errors.rejectValue("age", "AGE_NOT_32 <tag>", null, "message: <tag>");
		errors.reject("GENERAL_ERROR \" '", null, "message: \" '");

		assertTrue("Correct errors flag", errors.hasErrors());
		assertTrue("Correct number of errors", errors.getErrorCount() == 4);
		assertTrue("Correct object name", "tb".equals(errors.getObjectName()));

		assertTrue("Correct global errors flag", errors.hasGlobalErrors());
		assertTrue("Correct number of global errors", errors.getGlobalErrorCount() == 1);
		ObjectError globalError = errors.getGlobalError();
		assertTrue("Global error message escaped", "message: &quot; &#39;".equals(globalError.getDefaultMessage()));
		assertTrue("Global error code not escaped", "GENERAL_ERROR \" '".equals(globalError.getCode()));
		ObjectError globalErrorInList = (ObjectError) errors.getGlobalErrors().get(0);
		assertTrue("Same global error in list", globalError.getDefaultMessage().equals(globalErrorInList.getDefaultMessage()));
		ObjectError globalErrorInAllList = (ObjectError) errors.getAllErrors().get(3);
		assertTrue("Same global error in list", globalError.getDefaultMessage().equals(globalErrorInAllList.getDefaultMessage()));

		assertTrue("Correct name errors flag", errors.hasFieldErrors("name"));
		assertTrue("Correct number of name errors", errors.getFieldErrorCount("name") == 1);
		FieldError nameError = errors.getFieldError("name");
		assertTrue("Name error message escaped", "message: &amp;".equals(nameError.getDefaultMessage()));
		assertTrue("Name error code not escaped", "NAME_EMPTY &".equals(nameError.getCode()));
		assertTrue("Name value escaped", "empty &amp;".equals(errors.getFieldValue("name")));
		FieldError nameErrorInList = (FieldError) errors.getFieldErrors("name").get(0);
		assertTrue("Same name error in list", nameError.getDefaultMessage().equals(nameErrorInList.getDefaultMessage()));

		assertTrue("Correct age errors flag", errors.hasFieldErrors("age"));
		assertTrue("Correct number of age errors", errors.getFieldErrorCount("age") == 2);
		FieldError ageError = errors.getFieldError("age");
		assertTrue("Age error message escaped", "message: &lt;tag&gt;".equals(ageError.getDefaultMessage()));
		assertTrue("Age error code not escaped", "AGE_NOT_SET <tag>".equals(ageError.getCode()));
		assertTrue("Age value not escaped", (new Integer(0)).equals(errors.getFieldValue("age")));
		FieldError ageErrorInList = (FieldError) errors.getFieldErrors("age").get(0);
		assertTrue("Same name error in list", ageError.getDefaultMessage().equals(ageErrorInList.getDefaultMessage()));
		FieldError ageError2 = (FieldError) errors.getFieldErrors("age").get(1);
		assertTrue("Age error 2 message escaped", "message: &lt;tag&gt;".equals(ageError2.getDefaultMessage()));
		assertTrue("Age error 2 code not escaped", "AGE_NOT_32 <tag>".equals(ageError2.getCode()));
	}

}
