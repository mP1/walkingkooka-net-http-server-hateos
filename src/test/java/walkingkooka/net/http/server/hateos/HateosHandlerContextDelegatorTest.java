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
import walkingkooka.currency.CurrencyContexts;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.net.http.server.hateos.HateosHandlerContextDelegatorTest.TestHateosResourceHandlerContextDelegator;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.MathContext;
import java.util.Locale;

public final class HateosHandlerContextDelegatorTest implements HateosHandlerContextTesting<TestHateosResourceHandlerContextDelegator> {

    private final static LineEnding LINE_ENDING = LineEnding.NL;

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetObjectPostProcessorNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetObjectPostProcessor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetObjectPostProcessorSame() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetPreProcessorNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetPreProcessor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetPreProcessorSame() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testIndentation() {
        this.indentationAndCheck(
            this.createContext(),
            INDENTATION
        );
    }

    @Test
    public void testLineEnding() {
        this.lineEndingAndCheck(
            this.createContext(),
            LINE_ENDING
        );
    }

    @Override
    public TestHateosResourceHandlerContextDelegator createContext() {
        return new TestHateosResourceHandlerContextDelegator();
    }

    @Override
    public Class<TestHateosResourceHandlerContextDelegator> type() {
        return TestHateosResourceHandlerContextDelegator.class;
    }

    static final class TestHateosResourceHandlerContextDelegator implements HateosHandlerContextDelegator {

        @Override
        public HateosHandlerContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public HateosHandlerContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public HateosHandlerContext hateosHandlerContext() {
            return BasicHateosHandlerContext.with(
                INDENTATION,
                LINE_ENDING,
                JsonNodeMarshallUnmarshallContexts.basic(
                    JsonNodeMarshallContexts.basic(),
                    JsonNodeUnmarshallContexts.basic(
                        ExpressionNumberKind.BIG_DECIMAL,
                        CurrencyContexts.fake()
                            .setLocaleContext(
                                LocaleContexts.jre(Locale.ENGLISH)
                            ), // CurrencyCodeLanguageTagContext, // CurrencyCodeLanguageTagContext
                        MathContext.DECIMAL32
                    )
                )
            );
        }

        @Override
        public String toString() {
            return this.hateosHandlerContext().toString();
        }
    }
}
