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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ditto.json.DittoJsonHandler;
import org.eclipse.ditto.json.JsonValue;

public final class ExperimentalHandler implements DittoJsonHandler<JsonValue> {

    private ExperimentalValue value;

    @Override
    public JsonValue getValue() {
        return value;
    }

    List<ExperimentalValue> startArray() {
        return new ArrayList<>();
    }

    List<ExperimentalField> startObject() {
        return new ArrayList<>();
    }

    void endNull(final String source, final int start, final int end) {
        value = new ExperimentalNull(source, start, end);
    }

    void endBoolean(final boolean value, final String source, final int start, final int end) {
        this.value = new ExperimentalBoolean(value, source, start, end);
    }

    void endString(final String source, final int start, final int end) {
        value = new ExperimentalString(source, start, end);
    }

    void endNumber(final boolean fractional, final String source, final int start, final int end) {
        value = new ExperimentalNumber(fractional, source, start, end);
    }

    void endArrayValue(final List<ExperimentalValue> jsonValues) {
        jsonValues.add(value);
    }

    void endArray(final List<ExperimentalValue> jsonValues, final String source, final int start, final int end) {
        value = new ExperimentalArray(jsonValues, source, start, end);
    }

    void endObjectValue(final List<ExperimentalField> jsonFields, final String source, final int nameStart,
            final int nameEnd, final int start, final int end) {
        jsonFields.add(new ExperimentalField(source, nameStart, nameEnd, value));
    }

    void endObject(final List<ExperimentalField> jsonFields, final String source, final int start, final int end) {

    }
}
