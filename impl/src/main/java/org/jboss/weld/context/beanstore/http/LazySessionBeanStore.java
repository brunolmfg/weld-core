/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.context.beanstore.http;

import static org.jboss.weld.logging.Category.CONTEXT;
import static org.jboss.weld.logging.LoggerFactory.loggerFactory;
import static org.jboss.weld.logging.messages.ContextMessage.LOADING_BEAN_STORE_MAP_FROM_SESSION;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.weld.context.beanstore.NamingScheme;
import org.jboss.weld.servlet.SessionHolder;
import org.slf4j.cal10n.LocLogger;

/**
 * <p>
 * A BeanStore that uses a HTTP session as backing storage.
 * </p>
 * <p/>
 * <p>
 * Unlike {@link EagerSessionBeanStore}, this bean store is backed by an
 * HttpRequest, and only requires the session to be created when it needs to
 * write an instance to it.
 * </p>
 * <p/>
 * <p>
 * This class is not threadsafe
 * </p>
 *
 * @author Nicklas Karlsson
 * @author David Allen
 * @author Pete Muir
 * @author Ales Justin
 * @see EagerSessionBeanStore
 */
public class LazySessionBeanStore extends AbstractSessionBeanStore {

    private static final LocLogger log = loggerFactory().getLogger(CONTEXT);

    private final HttpServletRequest request;

    public LazySessionBeanStore(HttpServletRequest request, NamingScheme namingScheme) {
        super(namingScheme);
        this.request = request;
        log.trace(LOADING_BEAN_STORE_MAP_FROM_SESSION, this, getSession(false));
    }

    /**
     * Get the session, create equals false;
     * @return http session or null if no such session exists
     */
    protected HttpSession getSessionIfExists() {
        return SessionHolder.getSessionIfExists();
    }

    @Override
    protected HttpSession getSession(boolean create) {
        try {
            return SessionHolder.getSession(request, create);
        } catch (IllegalStateException e) {
            // If container can't create an underlying session, invalidate the
            // current one
            detach();
            return null;
        }
    }

}
