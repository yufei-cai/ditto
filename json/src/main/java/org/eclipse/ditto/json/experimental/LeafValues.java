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

import org.eclipse.ditto.json.JsonNumber;

final class ExperimentalString extends AbstractValue {

    ExperimentalString(final String source, final int start, final int end) {
        super(source, start, end);
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public String asString() {
        return new ExperimentalParser(null).unescapeString();
    }

    @Override
    public String toString() {
        return asString();
    }
}

final class ExperimentalNull extends AbstractValue {

    ExperimentalNull(final String source, final int start, final int end) {
        super(source, start, end);
    }

    @Override
    public boolean isNull() {
        return true;
    }
}

final class ExperimentalBoolean extends AbstractValue {

    private final boolean value;

    ExperimentalBoolean(final boolean value, final String source, final int start, final int end) {
        super(source, start, end);
        this.value = value;
    }

    @Override
    public boolean isBoolean() {
        return true;
    }

    @Override
    public boolean asBoolean() {
        return value;
    }
}

final class ExperimentalNumber extends AbstractValue implements JsonNumber {

    private final boolean fractional;

    ExperimentalNumber(final boolean fractional, final String source, final int begin, final int end) {
        super(source, begin, end);
        this.fractional = fractional;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public boolean isInt() {
        return !fractional && start + 11 <= end && isInteger(getSubstring());
    }

    @Override
    public boolean isLong() {
        return !fractional && start + 20 <= end && isLong(getSubstring());
    }

    @Override
    public boolean isDouble() {
        return !isLong();
    }

    @Override
    public int asInt() {
        // TODO: wrap exception
        return parseToInteger(getSubstring());
    }

    @Override
    public long asLong() {
        // TODO: wrap exception
        return parseToLong(getSubstring());
    }

    @Override
    public double asDouble() {
        // TODO: wrap exception
        return parseToDouble(getSubstring());
    }

    private static Double parseToDouble(final String string) {
        return Double.parseDouble(string);
    }

    private static boolean isInteger(final String string) {
        try {
            parseToInteger(string);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    private static boolean isLong(final String string) {
        try {
            parseToLong(string);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    private static Integer parseToInteger(final String string) {
        return Integer.parseInt(string);
    }

    private static Long parseToLong(final String string) {
        return Long.parseLong(string);
    }
}
