//$Id$
package org.hibernate.search.backend;

import java.util.Properties;

import org.hibernate.event.EventSource;
import org.hibernate.search.engine.SearchFactoryImplementor;

/**
 * Perform work for a given session. This implementation has to be multi threaded
 * @author Emmanuel Bernard
 */
public interface Worker {
	//Use of EventSource since it's the common subinterface for Session and SessionImplementor
	//the alternative would have been to do a subcasting or to retrieve 2 parameters :(
	void performWork(Work work, EventSource session);

	void initialize(Properties props, SearchFactoryImplementor searchFactoryImplementor);
}
