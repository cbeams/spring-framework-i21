package com.interface21.aop.framework;


import com.interface21.aop.framework.DelegatingIntroductionInterceptor;
import com.interface21.core.TimeStamped;

/**
 *
 */
public class TimestampIntroductionInterceptor extends DelegatingIntroductionInterceptor 
	implements TimeStamped {

	private long ts;
	
	
	public TimestampIntroductionInterceptor(long ts) {
		this();
		this.ts = ts;
	}
	
	public void setTime(long ts) {
		this.ts = ts;
	}
	/** 
	 * Constructor for TestAspectInterface.
	 * @param interfacesToPublish
	 */
	public TimestampIntroductionInterceptor() {
	}

	/**
	 * @see com.interface21.core.TimeStamped#getTimeStamp()
	 */
	public long getTimeStamp() {
		System.out.println("Invoked getTimeStamp method");
		return ts;
	}

}
