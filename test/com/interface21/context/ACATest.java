
package com.interface21.context;

public class ACATest implements ApplicationContextAware {
	
	private ApplicationContext ac;

	/**
	 * Constructor for ACATest.
	 */
	public ACATest() {
		super();
	}

	/**
	 * @see ApplicationContextAware#setApplicationContext(ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext ctx) throws ApplicationContextException {
		this.ac = ctx;
	}

	/**
	 * @see ApplicationContextAware#getApplicationContext()
	 */
	public ApplicationContext getApplicationContext() {
		return ac;
	}

}
