/*
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
package org.eclipse.ditto.json.experimental;

import org.eclipse.ditto.json.AbstractJsonValue;
import org.eclipse.ditto.json.JsonValue;

interface ExperimentalValue extends JsonValue {

    String getSource();

    int getStart();

    int getEnd();
}

abstract class AbstractValue extends AbstractJsonValue implements ExperimentalValue {

    final String source;
    final int start;
    final int end;

    AbstractValue(final String source, final int start, final int end) {
        this.source = source;
        this.start = start;
        this.end = end;
    }

    protected String getSubstring() {
        return source.substring(start, end);
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return getSubstring();
    }
}
