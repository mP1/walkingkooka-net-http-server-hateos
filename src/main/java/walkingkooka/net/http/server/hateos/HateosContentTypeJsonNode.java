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
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.Printers;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

/**
 * The {@link HateosContentType} that handles {@link JsonNode}.
 */
final class HateosContentTypeJsonNode extends HateosContentType {

    final static HateosContentTypeJsonNode with(final JsonNodeUnmarshallContext unmarshallContext,
                                                final JsonNodeMarshallContext marshallContext) {
        Objects.requireNonNull(unmarshallContext, "unmarshallContext");
        Objects.requireNonNull(marshallContext, "marshallContext");

        return new HateosContentTypeJsonNode(unmarshallContext, marshallContext);
    }

    /**
     * Private ctor use singleton.
     */
    private HateosContentTypeJsonNode(final JsonNodeUnmarshallContext unmarshallContext,
                                      final JsonNodeMarshallContext marshallContext) {
        super();
        this.unmarshallContext = unmarshallContext;
        this.marshallContext = marshallContext;
    }

    @Override
    public MediaType contentType() {
        return JSON_CONTENT_TYPE;
    }

    /**
     * Reads a resource object from its {@link JsonNode} representation.
     */
    @Override
    <T> T fromText(final String text,
                   final Class<T> type) {
        return this.unmarshallContext.unmarshall(JsonNode.parse(text), type);
    }

    private final JsonNodeUnmarshallContext unmarshallContext;

    /**
     * The format for hateos urls is base + "/" + resource name + "/" + id + "/" + link relation
     * <a href="https://en.wikipedia.org/wiki/Hypertext_Application_Language"></a>
     * <pre>
     * "_links": {
     *   "self": {
     *     "href": "http://example.com/api/book/hal-cookbook"
     *   }
     * },
     * </pre>
     */
    @Override
    String toText(final Object value) {
        final StringBuilder b = new StringBuilder();

        try (final IndentingPrinter printer = Printers.stringBuilder(b, LineEnding.SYSTEM).indenting(INDENTATION)) {
            this.marshallContext.marshall(value).printJson(printer);
            printer.flush();
        }
        return b.toString();
    }

    private final static Indentation INDENTATION = Indentation.with("  ");

    private final JsonNodeMarshallContext marshallContext;

    @Override
    public String toString() {
        return "JSON";
    }
}
