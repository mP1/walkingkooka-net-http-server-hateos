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
import walkingkooka.tree.Node;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.FromJsonNodeContext;
import walkingkooka.tree.json.marshall.ToJsonNodeContext;
import walkingkooka.tree.xml.XmlNode;

import javax.xml.parsers.DocumentBuilder;
import java.util.List;

/**
 * Controls the content type of hateos messages. Ideally this should have been an enum but currently enums do not
 * support type parameters.
 */
public abstract class HateosContentType {

    /**
     * Selects JSON formatted request and response bodies.
     */
    public static HateosContentType json(final FromJsonNodeContext fromJsonNodeContext,
                                                   final ToJsonNodeContext toJsonNodeContext) {
        return HateosContentTypeJsonNode.with(fromJsonNodeContext,
                toJsonNodeContext);
    }

    /**
     * Selects XML formatted request and response bodies.
     */
    public static HateosContentType xml(final DocumentBuilder builder) {
        return HateosContentTypeXmlNode.with(builder);
    }

    /**
     * Package private use constants.
     */
    HateosContentType() {
        super();
    }

    /**
     * Returns the {@link MediaType content type}.
     */
    public abstract MediaType contentType();

    /**
     * Reads a resource object from its {@link String text} representation.
     */
    abstract <R extends HateosResource<?>> R fromNode(final String text,
                                                      final Class<R> resourceType);

    /**
     * Reads a list of resource objects from their {@link String text} representation.
     */
    abstract <R extends HateosResource<?>> List<R> fromNodeList(final String text,
                                                                final Class<R> resourceType);

    /**
     * Converts it to a text.
     */
    abstract String toText(final HateosResource<?> resource);

    /**
     * Converts the resources to a text.
     */
    abstract String toTextList(final List<HateosResource<?>> resources);

    abstract public String toString();
}
