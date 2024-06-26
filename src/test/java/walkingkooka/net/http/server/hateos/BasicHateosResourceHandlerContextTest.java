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
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicHateosResourceHandlerContextTest implements HateosResourceHandlerContextTesting<BasicHateosResourceHandlerContext>,
        ToStringTesting<BasicHateosResourceHandlerContext> {

    private final static JsonNodeMarshallContext MARSHALL_CONTEXT = JsonNodeMarshallContexts.basic();

    private final static JsonNodeUnmarshallContext UNMARSHALL_CONTEXT = JsonNodeUnmarshallContexts.basic(
            ExpressionNumberKind.BIG_DECIMAL,
            MathContext.DECIMAL32
    );


    @Test
    public void testWithNullMarshallContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicHateosResourceHandlerContext.with(
                        null,
                        UNMARSHALL_CONTEXT
                )
        );
    }

    @Test
    public void testWithNullUnmarshallContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicHateosResourceHandlerContext.with(
                        MARSHALL_CONTEXT,
                        null
                )
        );
    }

    @Override
    public BasicHateosResourceHandlerContext createContext() {
        return BasicHateosResourceHandlerContext.with(
                MARSHALL_CONTEXT,
                UNMARSHALL_CONTEXT
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createContext(),
                        MARSHALL_CONTEXT +
                        " " +
                        UNMARSHALL_CONTEXT
        );
    }

    // class............................................................................................................

    @Override
    public Class<BasicHateosResourceHandlerContext> type() {
        return BasicHateosResourceHandlerContext.class;
    }
}
