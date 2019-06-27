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
/*******************************************************************************
 * Copyright (c) 2013, 2016 EclipseSource.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.eclipse.ditto.json.experimental;

import java.util.List;

import org.eclipse.ditto.json.JsonParseException;


/**
 * A streaming parser for JSON text. The parser reports all events to a given handler.
 * <p>
 * CAUTION: copied out of minimal JSON with adjustments.
 */
public class ExperimentalParser {

    private final ExperimentalHandler handler;
    private String string;
    private int index;
    private int line;
    private int lineOffset;
    private int current;
    private int captureStart;
    private int start;
    private int end;

    /**
     * Creates a new JsonParser with the given handler. The parser will report all parser events to
     * this handler.
     *
     * @param handler the handler to process parser events
     */
    public ExperimentalParser(ExperimentalHandler handler) {
        this.handler = handler;
    }

    public void parse(String string) {
        this.string = string;
        reset();
        skipWhiteSpace();
        readValue();
        skipWhiteSpace();
        if (!isEndOfText()) {
            throw error("Unexpected character");
        }
    }

    private void reset() {
        index = 0;
        line = 1;
        lineOffset = 0;
        current = 0;
        captureStart = -1;
        read();
    }

    private void readValue() {
        switch (current) {
            case 'n':
                readNull();
                break;
            case 't':
                readTrue();
                break;
            case 'f':
                readFalse();
                break;
            case '"':
                readString();
                break;
            case '[':
                readArray();
                break;
            case '{':
                readObject();
                break;
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                readNumber();
                break;
            default:
                throw expected("value");
        }
    }

    private void readArray() {
        List<ExperimentalValue> array = handler.startArray();
        final int arrayStart = index;
        read();
        skipWhiteSpace();
        if (readChar(']')) {
            start = arrayStart;
            end = index;
            handler.endArray(array, string, start, end);
            return;
        }
        do {
            skipWhiteSpace();
            readValue();
            handler.endArrayValue(array);
            skipWhiteSpace();
        } while (readChar(','));
        if (!readChar(']')) {
            throw expected("',' or ']'");
        }
        start = arrayStart;
        end = index;
        handler.endArray(array, string, start, end);
    }

    private void readObject() {
        List<ExperimentalField> object = handler.startObject();
        final int objectStart = index;
        read();
        skipWhiteSpace();
        if (readChar('}')) {
            start = objectStart;
            end = index;
            handler.endObject(object, string, start, end);
            return;
        }
        do {
            skipWhiteSpace();
            readName();
            final int nameStart = start;
            final int nameEnd = end;
            skipWhiteSpace();
            if (!readChar(':')) {
                throw expected("':'");
            }
            skipWhiteSpace();
            readValue();
            handler.endObjectValue(object, string, nameStart, nameEnd, start, end);
            skipWhiteSpace();
        } while (readChar(','));
        if (!readChar('}')) {
            throw expected("',' or '}'");
        }
        start = objectStart;
        end = index;
        handler.endObject(object, string, start, end);
    }

    private void readName() {
        if (current != '"') {
            throw expected("name");
        }
        readStringInternal();
    }

    private void readNull() {
        startCapture();
        readRequiredChar('u');
        readRequiredChar('l');
        readRequiredChar('l');
        endCapture();
        handler.endNull(string, start, end);
    }

    private void readTrue() {
        startCapture();
        read();
        readRequiredChar('r');
        readRequiredChar('u');
        readRequiredChar('e');
        endCapture();
        handler.endBoolean(true, string, start, end);
    }

    private void readFalse() {
        startCapture();
        read();
        readRequiredChar('a');
        readRequiredChar('l');
        readRequiredChar('s');
        readRequiredChar('e');
        endCapture();
        handler.endBoolean(false, string, start, end);
    }

    private void readRequiredChar(char ch) {
        if (!readChar(ch)) {
            throw expected("'" + ch + "'");
        }
    }

    private void readString() {
        readStringInternal();
        handler.endString(string, start, end);
    }

    void readStringInternal() {
        read();
        startCapture();
        while (current != '"') {
            if (current == '\\') {
                readEscape();
            } else if (current < 0x20) {
                throw expected("valid string character");
            } else {
                read();
            }
        }
        endCapture();
        read();
    }

    String unescapeString() {
        reset();
        final StringBuilder builder = new StringBuilder(string.length());
        while (index < string.length()) {
            if (current == '\\') {
                builder.appendCodePoint(readEscape());
            } else if (current < 0x20) {
                throw expected("valid string character");
            } else {
                builder.append((char) current);
                read();
            }
        }
        return builder.toString();
    }

    private int readEscape() {
        read();
        final int result;
        switch (current) {
            case '"':
            case '/':
            case '\\':
                result = (char) current;
                break;
            case 'b':
                result = 'b';
                break;
            case 'f':
                result = '\f';
                break;
            case 'n':
                result = '\n';
                break;
            case 'r':
                result = '\r';
                break;
            case 't':
                result = '\t';
                break;
            case 'u':
                char[] hexChars = new char[4];
                for (int i = 0; i < 4; i++) {
                    read();
                    if (!isHexDigit()) {
                        throw expected("hexadecimal digit");
                    }
                    hexChars[i] = (char) current;
                }
                result = Integer.parseInt(new String(hexChars), 16);
                break;
            default:
                throw expected("valid escape sequence");
        }
        read();
        return result;
    }

    private void readNumber() {
        startCapture();
        readChar('-');
        int firstDigit = current;
        if (!readDigit()) {
            throw expected("digit");
        }
        if (firstDigit != '0') {
            while (readDigit()) {
            }
        }
        final boolean fractional = readFraction();
        readExponent();
        endCapture();
        handler.endNumber(fractional, string, start, end);
    }

    private boolean readFraction() {
        if (!readChar('.')) {
            return false;
        }
        if (!readDigit()) {
            throw expected("digit");
        }
        while (readDigit()) {
        }
        return true;
    }

    private boolean readExponent() {
        if (!readChar('e') && !readChar('E')) {
            return false;
        }
        if (!readChar('+')) {
            readChar('-');
        }
        if (!readDigit()) {
            throw expected("digit");
        }
        while (readDigit()) {
        }
        return true;
    }

    private boolean readChar(char ch) {
        if (current != ch) {
            return false;
        }
        read();
        return true;
    }

    private boolean readDigit() {
        if (!isDigit()) {
            return false;
        }
        read();
        return true;
    }

    private void skipWhiteSpace() {
        while (isWhiteSpace()) {
            read();
        }
    }

    private void read() {
        if (current == '\n') {
            line++;
            lineOffset = index;
        }
        current = string.charAt(index++);
    }

    private void startCapture() {
        captureStart = index - 1;
    }

    private void pauseCapture() {
        end = current == -1 ? index : index - 1;
        captureStart = -1;
    }

    private void endCapture() {
        start = captureStart;
        end = index - 1;
        captureStart = -1;
    }

    Location getLocation() {
        int offset = index - 1;
        int column = offset - lineOffset + 1;
        return new Location(offset, line, column);
    }

    private JsonParseException expected(String expected) {
        if (isEndOfText()) {
            return error("Unexpected end of input");
        }
        return error("Expected " + expected);
    }

    private JsonParseException error(String message) {
        return new JsonParseException(message);
    }

    private boolean isWhiteSpace() {
        return current == ' ' || current == '\t' || current == '\n' || current == '\r';
    }

    private boolean isDigit() {
        return current >= '0' && current <= '9';
    }

    private boolean isHexDigit() {
        return current >= '0' && current <= '9'
                || current >= 'a' && current <= 'f'
                || current >= 'A' && current <= 'F';
    }

    private boolean isEndOfText() {
        return current == -1;
    }

    private static final class Location {

        final int offset;
        final int line;
        final int column;

        private Location(final int offset, final int line, final int column) {
            this.offset = offset;
            this.line = line;
            this.column = column;
        }
    }
}
