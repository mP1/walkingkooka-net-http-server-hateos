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

import walkingkooka.Binary;
import walkingkooka.net.header.ETag;
import walkingkooka.net.header.ETagComputer;
import walkingkooka.text.BinaryTextContext;
import walkingkooka.text.BinaryTextContextDelegator;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContextDelegator;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.util.Objects;
import java.util.Optional;

final class BasicHateosHandlerContext implements HateosHandlerContext,
    BinaryTextContextDelegator,
    JsonNodeMarshallUnmarshallContextDelegator {

    static BasicHateosHandlerContext with(final BinaryTextContext binaryTextContext,
                                          final ETagComputer etagComputer,
                                          final JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext) {
        return new BasicHateosHandlerContext(
            Objects.requireNonNull(binaryTextContext, "binaryTextContext"),
            Objects.requireNonNull(etagComputer, "etagComputer"),
            Objects.requireNonNull(jsonNodeMarshallUnmarshallContext, "jsonNodeMarshallUnmarshallContext")
        );
    }

    private BasicHateosHandlerContext(final BinaryTextContext binaryTextContext,
                                      final ETagComputer etagComputer,
                                      final JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext) {
        super();
        this.jsonNodeMarshallUnmarshallContext = jsonNodeMarshallUnmarshallContext;
        this.etagComputer = etagComputer;
        this.binaryTextContext = binaryTextContext;
    }

    // BinaryTextContextDelegator.......................................................................................

    @Override
    public BinaryTextContext binaryTextContext() {
        return this.binaryTextContext;
    }

    private final BinaryTextContext binaryTextContext;

    // ETagComputer.....................................................................................................

    @Override
    public Optional<ETag> computeETag(final Binary binary) {
        return this.etagComputer.computeETag(binary);
    }

    private final ETagComputer etagComputer;

    // JsonNodeMarshallUnmarshallContext................................................................................

    @Override
    public HateosHandlerContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
        final JsonNodeMarshallUnmarshallContext before = this.jsonNodeMarshallUnmarshallContext;
        final JsonNodeMarshallUnmarshallContext after = before.setObjectPostProcessor(processor);

        return before.equals(after) ?
            this :
            new BasicHateosHandlerContext(
                this.binaryTextContext,
                this.etagComputer,
                after
            );
    }

    @Override
    public HateosHandlerContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        final JsonNodeMarshallUnmarshallContext before = this.jsonNodeMarshallUnmarshallContext;
        final JsonNodeMarshallUnmarshallContext after = before.setPreProcessor(processor);

        return before.equals(after) ?
            this :
            new BasicHateosHandlerContext(
                this.binaryTextContext,
                this.etagComputer,
                after
            );
    }

    @Override
    public JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext() {
        return this.jsonNodeMarshallUnmarshallContext;
    }

    private final JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.jsonNodeMarshallUnmarshallContext.toString();
    }
}
