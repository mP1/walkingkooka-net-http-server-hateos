/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.net.http.server.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HateosHandlerContextBasicTest implements HateosHandlerContextTesting2<HateosHandlerContextBasic>,
    ToStringTesting<HateosHandlerContextBasic> {

    @Test
    public void testWithNullBinaryTextContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> HateosHandlerContextBasic.with(
                null,
                ETAG_COMPUTER,
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullETagComputerFails() {
        assertThrows(
            NullPointerException.class,
            () -> HateosHandlerContextBasic.with(
                BINARY_TEXT_CONTEXT,
                null,
                JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullJsonNodeUnmarshallContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> HateosHandlerContextBasic.with(
                BINARY_TEXT_CONTEXT,
                ETAG_COMPUTER,
                null
            )
        );
    }

    @Override
    public HateosHandlerContextBasic createContext() {
        return HateosHandlerContextBasic.with(
            BINARY_TEXT_CONTEXT,
            ETAG_COMPUTER,
            JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT.toString()
        );
    }

    // class............................................................................................................

    @Override
    public Class<HateosHandlerContextBasic> type() {
        return HateosHandlerContextBasic.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
