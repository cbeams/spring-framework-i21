/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.beans.factory.xml;

import java.util.Collection;

/**
 * Bean that exposes a simple property that can be set
 * to a mix of references and individual values
 * @author Rod Johnson
 * @since 27-May-2003
 * @version $Id$
 */
public class MixedCollectionBean {
	
	private Collection jumble;

	/**
	 * @return Collection
	 */
	public Collection getJumble() {
		return jumble;
	}

	/**
	 * Sets the jumble.
	 * @param jumble The jumble to set
	 */
	public void setJumble(Collection jumble) {
		this.jumble = jumble;
	}

}
