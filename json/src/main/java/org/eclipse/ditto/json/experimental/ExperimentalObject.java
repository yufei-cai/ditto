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
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonFieldSelector;
import org.eclipse.ditto.json.JsonKey;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.json.JsonValue;

public class ExperimentalObject extends AbstractValue implements JsonObject {

    @Override
    public JsonObject setValue(final CharSequence key, final int value) {
        return error();
    }

    @Override
    public JsonObject setValue(final CharSequence key, final long value) {
        return error();
    }

    @Override
    public JsonObject setValue(final CharSequence key, final double value) {
        return error();
    }

    @Override
    public JsonObject setValue(final CharSequence key, final boolean value) {
        return error();
    }

    @Override
    public JsonObject setValue(final CharSequence key, final String value) {
        return error();
    }

    @Override
    public JsonObject setValue(final CharSequence key, final JsonValue value) {
        return error();
    }

    @Override
    public <T> JsonObject set(final JsonFieldDefinition<T> fieldDefinition, @Nullable final T value) {
        return error();
    }

    @Override
    public JsonObject set(final JsonField field) {
        return null;
    }

    @Override
    public JsonObject setAll(final Iterable<JsonField> jsonFields) {
        return null;
    }

    @Override
    public boolean contains(final CharSequence key) {
        return false;
    }

    @Override
    public JsonObject get(final JsonPointer pointer) {
        return null;
    }

    @Override
    public JsonObject get(final JsonFieldDefinition fieldDefinition) {
        return null;
    }

    @Override
    public JsonObject get(final JsonFieldSelector fieldSelector) {
        return null;
    }

    @Override
    public Optional<JsonValue> getValue(final CharSequence key) {
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> getValue(final JsonFieldDefinition<T> fieldDefinition) {
        return Optional.empty();
    }

    @Override
    public <T> T getValueOrThrow(final JsonFieldDefinition<T> fieldDefinition) {
        return null;
    }

    @Override
    public JsonObject remove(final CharSequence key) {
        return null;
    }

    @Override
    public List<JsonKey> getKeys() {
        return null;
    }

    @Override
    public Optional<JsonField> getField(final CharSequence key) {
        return Optional.empty();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public Stream<JsonField> stream() {
        return null;
    }

    @Override
    public Iterator<JsonField> iterator() {
        return null;
    }

    private static <T> T error() {
        throw new IllegalStateException("not implemented");
    }
}
