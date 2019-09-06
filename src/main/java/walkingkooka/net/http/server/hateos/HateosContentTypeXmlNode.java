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
import walkingkooka.net.http.server.HttpServerException;
import walkingkooka.tree.Node;
import walkingkooka.tree.xml.XmlName;
import walkingkooka.tree.xml.XmlNode;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

final class HateosContentTypeXmlNode extends HateosContentType {

    /**
     * Singleton
     */
    final static HateosContentTypeXmlNode with(final DocumentBuilder builder) {
        return new HateosContentTypeXmlNode(builder);
    }


    /**
     * Private ctor use singleton.
     */
    private HateosContentTypeXmlNode(final DocumentBuilder documentBuilder) {
        super();
        this.documentBuilder = documentBuilder;
    }

    @Override
    public MediaType contentType() {
        return CONTENT_TYPE;
    }

    private final static MediaType CONTENT_TYPE = MediaType.with("application", "hal+xml");

    /**
     * Reads a resource object from its {@link XmlNode} representation.
     */
    @Override
    <R extends HateosResource<?>> R fromNode(final String text,
                                             final Class<R> resourceType) {
        return fromXmlNode(parseXml(text), resourceType);
    }

    /**
     * Reads a list of resource objects from their {@link Node} representation.
     */
    @Override
    <R extends HateosResource<?>> List<R> fromNodeList(final String text,
                                                       final Class<R> resourceType) {
        return parseXml(text)
                .children()
                .stream()
                .map(c -> fromXmlNode(c, resourceType))
                .collect(Collectors.toList());
    }

    private XmlNode parseXml(final String text) {
        try {
            return XmlNode.fromXml(this.documentBuilder, new StringReader(text));
        } catch (final Exception cause) {
            throw new HttpServerException(cause.getMessage(), cause);
        }
    }

    private <R extends HateosResource<?>, I extends Comparable<I>> R fromXmlNode(final XmlNode node,
                                                                                 final Class<R> resourceType) {
        throw new UnsupportedOperationException();
    }

    @Override
    String toText(final HateosResource<?> resource) {
        return toXmlText(resource.toXmlNode());
    }

    @Override
    String toTextList(final List<HateosResource<?>> resources) {
        return toXmlText(
                XmlNode.createDocument(this.documentBuilder)
                        .createElement(XmlName.element("list"))
                        .setChildren(resources.stream()
                                .map(HateosResource::toXmlNode)
                                .collect(Collectors.toList())));
    }

    private final DocumentBuilder documentBuilder;

    private String toXmlText(final XmlNode node) {
        try (final StringWriter writer = new StringWriter()) {
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            node.toXml(transformer, writer);
            writer.flush();
            return writer.toString();
        } catch (final IOException | TransformerException cause) {
            throw new HttpServerException(cause.getMessage(), cause);
        }
    }

    @Override
    public String toString() {
        return "XML";
    }
}
