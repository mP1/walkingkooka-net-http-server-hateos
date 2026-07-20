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

import walkingkooka.net.header.MediaType;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.TextContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContextDelegator;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.util.Objects;

final class BasicHateosHandlerContext implements HateosHandlerContext,
    JsonNodeMarshallUnmarshallContextDelegator {

    static BasicHateosHandlerContext with(final JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext,
                                          final TextContext textContext) {
        return new BasicHateosHandlerContext(
            Objects.requireNonNull(jsonNodeMarshallUnmarshallContext, "jsonNodeMarshallUnmarshallContext"),
            Objects.requireNonNull(textContext, "textContext")
        );
    }

    private BasicHateosHandlerContext(final JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext,
                                      final TextContext textContext) {
        super();
        this.jsonNodeMarshallUnmarshallContext = jsonNodeMarshallUnmarshallContext;
        this.textContext = textContext;
    }

    @Override
    public MediaType contentType() {
        return HATEOS_DEFAULT_CONTENT_TYPE;
    }

    @Override
    public Indentation indentation() {
        return this.textContext()
            .indentation();
    }

    @Override
    public LineEnding lineEnding() {
        return this.textContext()
            .lineEnding();
    }

    // JsonNodeMarshallUnmarshallContext................................................................................

    @Override
    public HateosHandlerContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
        final JsonNodeMarshallUnmarshallContext before = this.jsonNodeMarshallUnmarshallContext;
        final JsonNodeMarshallUnmarshallContext after = before.setObjectPostProcessor(processor);

        return before.equals(after) ?
            this :
            new BasicHateosHandlerContext(
                after,
                this.textContext
            );
    }

    @Override
    public HateosHandlerContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        final JsonNodeMarshallUnmarshallContext before = this.jsonNodeMarshallUnmarshallContext;
        final JsonNodeMarshallUnmarshallContext after = before.setPreProcessor(processor);

        return before.equals(after) ?
            this :
            new BasicHateosHandlerContext(
                after,
                this.textContext
            );
    }

    @Override
    public JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext() {
        return this.jsonNodeMarshallUnmarshallContext;
    }

    private final JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext;

    // TextContextDelegator.............................................................................................

    //@Override
    public TextContext textContext() {
        return this.textContext;
    }

    private final TextContext textContext;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.jsonNodeMarshallUnmarshallContext.toString();
    }
}
