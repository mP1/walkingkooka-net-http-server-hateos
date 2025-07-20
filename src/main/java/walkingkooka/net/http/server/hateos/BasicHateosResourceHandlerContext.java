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
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContextDelegator;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.util.Objects;

final class BasicHateosResourceHandlerContext implements HateosResourceHandlerContext,
    JsonNodeMarshallUnmarshallContextDelegator {

    static BasicHateosResourceHandlerContext with(final Indentation indentation,
                                                  final LineEnding lineEnding,
                                                  final JsonNodeMarshallUnmarshallContext context) {
        return new BasicHateosResourceHandlerContext(
            Objects.requireNonNull(indentation, "indentation"),
            Objects.requireNonNull(lineEnding, "lineEnding"),
            Objects.requireNonNull(context, "context")
        );
    }

    private BasicHateosResourceHandlerContext(final Indentation indentation,
                                              final LineEnding lineEnding,
                                              final JsonNodeMarshallUnmarshallContext context) {
        this.indentation = indentation;
        this.lineEnding = lineEnding;
        this.context = context;
    }

    @Override
    public MediaType contentType() {
        return HATEOS_DEFAULT_CONTENT_TYPE;
    }

    @Override
    public Indentation indentation() {
        return this.indentation;
    }

    private final Indentation indentation;

    @Override
    public LineEnding lineEnding() {
        return this.lineEnding;
    }

    private final LineEnding lineEnding;

    // JsonNodeMarshallUnmarshallContext................................................................................

    @Override
    public HateosResourceHandlerContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        final JsonNodeMarshallUnmarshallContext before = this.context;
        final JsonNodeMarshallUnmarshallContext after = before.setPreProcessor(processor);

        return before.equals(after) ?
            this :
            new BasicHateosResourceHandlerContext(
                this.indentation,
                this.lineEnding,
                after
            );
    }

    @Override
    public JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext() {
        return this.context;
    }

    private final JsonNodeMarshallUnmarshallContext context;

    @Override
    public String toString() {
        return this.context.toString();
    }
}
