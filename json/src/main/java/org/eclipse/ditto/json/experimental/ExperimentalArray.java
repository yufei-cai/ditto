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

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.ditto.json.JsonArray;
import org.eclipse.ditto.json.JsonParseException;
import org.eclipse.ditto.json.JsonValue;

final class ExperimentalArray extends AbstractValue implements JsonArray {

    private final List<ExperimentalValue> values;

    ExperimentalArray(final List<ExperimentalValue> values, final String source, final int start, final int end) {
        super(source, start, end);
        this.values = values;
    }

    @Override
    public JsonArray add(final int value, final int... furtherValues) {
        // TODO: add exception
        throw new JsonParseException(null);
    }

    @Override
    public JsonArray add(final long value, final long... furtherValues) {
        // TODO: add exception
        throw new JsonParseException(null);
    }

    @Override
    public JsonArray add(final double value, final double... furtherValues) {
        // TODO: add exception
        throw new JsonParseException(null);
    }

    @Override
    // TODO: add exception
    public JsonArray add(final boolean value, final boolean... furtherValues) {
        // TODO: add exception
        throw new JsonParseException(null);
    }

    @Override
    public JsonArray add(final String value, final String... furtherValues) {
        // TODO: add exception
        throw new JsonParseException(null);
    }

    @Override
    public JsonArray add(final JsonValue value, final JsonValue... furtherValues) {
        // TODO: add exception
        throw new JsonParseException(null);
    }

    @Override
    public Optional<JsonValue> get(final int index) {
        // TODO: add exception
        throw new JsonParseException(null);
    }

    @Override
    public boolean contains(final JsonValue value) {
        // TODO: implement
        throw new NoSuchElementException();
    }

    @Override
    public int indexOf(final JsonValue value) {
        throw new NoSuchElementException();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public int getSize() {
        return values.size();
    }

    // immutable list is covariant in element type. this is safe.
    @SuppressWarnings("unchecked")
    @Override
    public Stream<JsonValue> stream() {
        return (Stream<JsonValue>) (Object) values.stream();
    }

    // immutable list is covariant in element type. this is safe.
    @SuppressWarnings("unchecked")
    @Override
    public Iterator<JsonValue> iterator() {
        return (Iterator<JsonValue>) (Object) values.iterator();
    }
}
