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
 * This interface represents a JSON number.
 */
public interface JsonNumber extends JsonValue {

    default boolean isNumber() {
        return true;
    }

    boolean isInt();

    int asInt();

    boolean isLong();

    long asLong();

    boolean isDouble();

    double asDouble();

}
