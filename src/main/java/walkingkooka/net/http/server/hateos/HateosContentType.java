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
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

/**
 * Controls the content type of hateos messages.
 */
public abstract class HateosContentType {

    /**
     * A {@link MediaType} for json hateos content.
     */
    public final static MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON;

    /**
     * Selects JSON formatted request and response bodies.
     */
    public static HateosContentType json(final JsonNodeUnmarshallContext unmarshallContext,
                                         final JsonNodeMarshallContext marshallContext) {
        return HateosContentTypeJsonNode.with(unmarshallContext,
                marshallContext);
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
     * Reads a value from its {@link String text} representation into an object.
     */
    abstract <T> T fromText(final String text,
                            final Class<T> type);

    /**
     * Marshalls the value into text. The {@link Indentation} and {@link LineEnding} can be used to pretty print JSON.
     */
    abstract String toText(final Object value,
                           final Indentation indentation,
                           final LineEnding lineEnding);

    abstract public String toString();
}
