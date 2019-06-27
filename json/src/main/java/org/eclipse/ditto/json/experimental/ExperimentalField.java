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

import java.util.Optional;

import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonFieldMarker;
import org.eclipse.ditto.json.JsonKey;

final class ExperimentalField implements JsonField {

    private final String source;
    private final int keyStart;
    private final int keyEnd;
    private final ExperimentalValue value;

    ExperimentalField(final String source, final int keyStart, final int keyEnd,
            final ExperimentalValue value) {

        this.source = source;
        this.keyStart = keyStart;
        this.keyEnd = keyEnd;
        this.value = value;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String getKeyName() {
        return new ExperimentalString(source, keyStart, keyEnd).asString();
    }

    @Override
    public ExperimentalValue getValue() {
        return value;
    }

    @Override
    public JsonKey getKey() {
        return JsonKey.of(getKeyName());
    }

    @Override
    public Optional<JsonFieldDefinition> getDefinition() {
        return Optional.empty();
    }

    @Override
    public boolean isMarkedAs(final JsonFieldMarker fieldMarker, final JsonFieldMarker... furtherFieldMarkers) {
        return true;
    }
}
