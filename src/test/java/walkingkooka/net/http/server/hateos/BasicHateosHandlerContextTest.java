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
import walkingkooka.currency.CurrencyContexts;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.MathContext;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicHateosHandlerContextTest implements HateosHandlerContextTesting<BasicHateosHandlerContext>,
    ToStringTesting<BasicHateosHandlerContext> {

    private final static JsonNodeMarshallUnmarshallContext UNMARSHALL_CONTEXT = JsonNodeMarshallUnmarshallContexts.basic(
        JsonNodeMarshallContexts.basic(),
        JsonNodeUnmarshallContexts.basic(
            ExpressionNumberKind.BIG_DECIMAL,
            CurrencyContexts.fake()
                .setLocaleContext(
                    LocaleContexts.jre(Locale.ENGLISH)
                ), // CurrencyCodeLanguageTagContext
            MathContext.DECIMAL32
        )
    );

    @Test
    public void testWithNullJsonNodeUnmarshallContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicHateosHandlerContext.with(
                null,
                TEXT_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullTextContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicHateosHandlerContext.with(
                UNMARSHALL_CONTEXT,
                null
            )
        );
    }

    @Override
    public BasicHateosHandlerContext createContext() {
        return BasicHateosHandlerContext.with(
            UNMARSHALL_CONTEXT,
            TEXT_CONTEXT
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            UNMARSHALL_CONTEXT.toString()
        );
    }

    // class............................................................................................................

    @Override
    public Class<BasicHateosHandlerContext> type() {
        return BasicHateosHandlerContext.class;
    }
}
