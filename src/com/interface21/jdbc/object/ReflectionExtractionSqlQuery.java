package com.interface21.jdbc.object;


import com.interface21.dao.InvalidDataAccessApiUsageException;
import com.interface21.jdbc.core.ResultReader;

/**
 * Reusable threadsafe query in which reflection is used to
 * extract each result row.
 * Each object must be of the same type.
	NOTE : THIS CLASS IS A DEMONSTRATION ONLY OF THIS APPROACH.
	IT IS NOT FULLY IMPLEMENTED, DOCUMENTED OR TESTED.
 */
public abstract class ReflectionExtractionSqlQuery extends SqlQuery {

	/** Class of each row of result.
	 * Must be a concrete class, not an interface and must
	 * be a bean with a no-arg constructor.
 	 */
	private Class	resultClass;
	
	/**
	 *  Only subclasses can use this: they must
	 * remember to set the result class later
	 */
	protected ReflectionExtractionSqlQuery() {
	}
	
	
	//-------------------------------------------------------------------------
	// Bean properties
	//-------------------------------------------------------------------------
	public Class getResultClass() {
		return resultClass;
	}
	
	
	public void setResultClassname(String classname) throws InvalidDataAccessApiUsageException {
		try {
			setResultClass(Class.forName(classname));
		}
		catch (ClassNotFoundException ex) {
			// Eventually use fatal
			throw new InvalidDataAccessApiUsageException("Result class [" + classname + "] not found");
		}
	}
	
	protected void setResultClass(Class resultClass) {
		this.resultClass = resultClass;
	}
	
	

	protected void onCompileInternal() throws InvalidDataAccessApiUsageException {
		if (resultClass == null)
			throw new InvalidDataAccessApiUsageException("ResultClass must be set in class " + getClass().getName());	
	}


	//-------------------------------------------------------------------------
	// Implementation of protected abstract methods
	//-------------------------------------------------------------------------
	/**
	 * Use reflection to extract an object from each row of the result.
	 */
	protected ResultReader newResultReader(int rowsExpected) {
		return new ReflectionRowExtractor(getResultClass(), rowsExpected);
	}

}