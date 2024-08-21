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
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContextDelegator;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

final class BasicHateosResourceHandlerContext implements HateosResourceHandlerContext,
        JsonNodeMarshallUnmarshallContextDelegator {

    static BasicHateosResourceHandlerContext with(final JsonNodeMarshallContext marshallContext,
                                                  final JsonNodeUnmarshallContext unmarshallContext) {
        return new BasicHateosResourceHandlerContext(
                Objects.requireNonNull(marshallContext, "marshallContext"),
                Objects.requireNonNull(unmarshallContext, "unmarshallContext")
        );
    }

    private BasicHateosResourceHandlerContext(final JsonNodeMarshallContext marshallContext,
                                              final JsonNodeUnmarshallContext unmarshallContext) {
        this.marshallContext = marshallContext;
        this.unmarshallContext = unmarshallContext;
    }

    @Override
    public MediaType contentType() {
        return MediaType.APPLICATION_JSON;
    }

    // JsonNodeMarshallContext..........................................................................................

    @Override
    public JsonNodeMarshallContext jsonNodeMarshallContext() {
        return this.marshallContext;
    }

    private final JsonNodeMarshallContext marshallContext;

    // JsonNodeUnmarshallContext........................................................................................

    @Override
    public JsonNodeUnmarshallContext jsonNodeUnmarshallContext() {
        return this.unmarshallContext;
    }

    private final JsonNodeUnmarshallContext unmarshallContext;

    @Override
    public String toString() {
        return this.marshallContext +
                " " +
                this.unmarshallContext;
    }
}
