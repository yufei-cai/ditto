/*
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.ditto.json;

/**
 * Common base implementation for parsing JSON string representations to ditto-json types.
 *
 * @param <V> the type of the value this handler returns.
 */
public interface DittoJsonHandler<V> {

    /**
     * Returns the value of this handler or {@code null}.
     *
     * @return the value.
     */
    V getValue();

}
