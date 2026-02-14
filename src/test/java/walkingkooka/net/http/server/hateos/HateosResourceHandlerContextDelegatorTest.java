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
import walkingkooka.net.http.server.hateos.HateosResourceHandlerContextDelegatorTest.TestHateosResourceHandlerContextDelegator;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.MathContext;

public final class HateosResourceHandlerContextDelegatorTest implements HateosResourceHandlerContextTesting<TestHateosResourceHandlerContextDelegator> {

    private final static Indentation INDENTATION = Indentation.SPACES2;
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

    static final class TestHateosResourceHandlerContextDelegator implements HateosResourceHandlerContextDelegator {

        @Override
        public HateosResourceHandlerContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public HateosResourceHandlerContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public HateosResourceHandlerContext hateosResourceHandlerContext() {
            return BasicHateosResourceHandlerContext.with(
                INDENTATION,
                LINE_ENDING,
                JsonNodeMarshallUnmarshallContexts.basic(
                    JsonNodeMarshallContexts.basic(),
                    JsonNodeUnmarshallContexts.basic(
                        (String cc) -> {
                            throw new UnsupportedOperationException();
                        },
                        ExpressionNumberKind.BIG_DECIMAL,
                        MathContext.DECIMAL32
                    )
                )
            );
        }

        @Override
        public String toString() {
            return this.hateosResourceHandlerContext().toString();
        }
    }
}
