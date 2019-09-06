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

import walkingkooka.io.printer.IndentingPrinter;
import walkingkooka.io.printer.IndentingPrinters;
import walkingkooka.io.printer.Printers;
import walkingkooka.net.header.MediaType;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.FromJsonNodeContext;
import walkingkooka.tree.json.marshall.ToJsonNodeContext;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The {@link HateosContentType} that handles {@link JsonNode}.
 */
final class HateosContentTypeJsonNode extends HateosContentType {

    final static HateosContentTypeJsonNode with(final FromJsonNodeContext fromJsonNodeContext,
                                                final ToJsonNodeContext toJsonNodeContext) {
        Objects.requireNonNull(fromJsonNodeContext, "fromJsonNodeContext");
        Objects.requireNonNull(toJsonNodeContext, "toJsonNodeContext");

        return new HateosContentTypeJsonNode(fromJsonNodeContext, toJsonNodeContext);
    }

    /**
     * Private ctor use singleton.
     */
    private HateosContentTypeJsonNode(final FromJsonNodeContext fromJsonNodeContext,
                                      final ToJsonNodeContext toJsonNodeContext) {
        super();
        this.fromJsonNodeContext = fromJsonNodeContext;
        this.toJsonNodeContext = toJsonNodeContext;
    }

    @Override
    public MediaType contentType() {
        return CONTENT_TYPE;
    }

    final static MediaType CONTENT_TYPE = MediaType.with("application", "hal+json");

    /**
     * Reads a resource object from its {@link JsonNode} representation.
     */
    @Override
    <R extends HateosResource<?>> R fromNode(final String text,
                                             final Class<R> resourceType) {
        return this.fromJsonNodeContext.fromJsonNode(JsonNode.parse(text),
                resourceType);
    }

    /**
     * Reads a list of resource objects from their {@link JsonNode} representation.
     */
    @Override
    <R extends HateosResource<?>> List<R> fromNodeList(final String text,
                                                       final Class<R> resourceType) {
        return this.fromJsonNodeContext.fromJsonNodeList(JsonNode.parse(text),
                resourceType);
    }

    private final FromJsonNodeContext fromJsonNodeContext;

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
    String toText(final HateosResource<?> resource) {
        return this.toJsonText(this.toJsonNodeContext.toJsonNode(resource));
    }

    @Override
    String toTextList(final List<HateosResource<?>> resources) {
        return this.toJsonText(
                JsonNode.array().setChildren(resources.
                        stream()
                        .map(this.toJsonNodeContext::toJsonNode)
                        .collect(Collectors.toList())));
    }

    private String toJsonText(final JsonNode node) {
        final StringBuilder b = new StringBuilder();

        try(final IndentingPrinter printer = IndentingPrinters.printer(Printers.stringBuilder(b, LineEnding.SYSTEM))) {
            node.printJson(printer);
            printer.flush();
        }
        return b.toString();
    }

    private final ToJsonNodeContext toJsonNodeContext;

    @Override
    public String toString() {
        return "JSON";
    }
}
