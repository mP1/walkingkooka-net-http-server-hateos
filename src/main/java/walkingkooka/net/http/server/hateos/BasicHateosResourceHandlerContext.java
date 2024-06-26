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
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonString;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.math.MathContext;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

final class BasicHateosResourceHandlerContext implements HateosResourceHandlerContext {

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
    public JsonNodeMarshallContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor context) {
        return this.marshallContext.setObjectPostProcessor(context);
    }

    @Override
    public JsonNode marshall(final Object value) {
        return this.marshallContext.marshall(value);
    }

    @Override
    public JsonNode marshallEnumSet(final Set<? extends Enum<?>> set) {
        return this.marshallContext.marshallEnumSet(set);
    }

    @Override
    public JsonNode marshallWithType(final Object value) {
        return this.marshallContext.marshallWithType(value);
    }

    @Override
    public JsonNode marshallCollection(final Collection<?> collection) {
        return this.marshallContext.marshallCollection(collection);
    }

    @Override
    public JsonNode marshallMap(final Map<?, ?> map) {
        return this.marshallContext.marshallMap(map);
    }

    @Override
    public JsonNode marshallWithTypeCollection(final Collection<?> collection) {
        return this.marshallContext.marshallWithTypeCollection(collection);
    }

    @Override
    public JsonNode marshallWithTypeMap(final Map<?, ?> map) {
        return this.marshallContext.marshallWithTypeMap(map);
    }

    private final JsonNodeMarshallContext marshallContext;

    // JsonNodeUnmarshallContext........................................................................................

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return this.unmarshallContext.expressionNumberKind();
    }

    @Override
    public MathContext mathContext() {
        return this.unmarshallContext.mathContext();
    }

    @Override
    public JsonNodeUnmarshallContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        return this.unmarshallContext.setPreProcessor(processor);
    }

    @Override
    public <T> T unmarshall(final JsonNode json,
                            final Class<T> type) {
        return this.unmarshallContext.unmarshall(
                json,
                type
        );
    }

    @Override
    public <T extends Enum<T>> Set<T> unmarshallEnumSet(final JsonNode json,
                                                        final Class<T> type,
                                                        final Function<String, T> nameToEnum) {
        return this.unmarshallContext.unmarshallEnumSet(
                json,
                type,
                nameToEnum
        );
    }

    @Override
    public <T> List<T> unmarshallList(final JsonNode json,
                                      final Class<T> type) {
        return this.unmarshallContext.unmarshallList(
                json,
                type
        );
    }

    @Override
    public <T> Set<T> unmarshallSet(final JsonNode json,
                                    final Class<T> type) {
        return this.unmarshallContext.unmarshallSet(
                json,
                type
        );
    }

    @Override
    public <K, V> Map<K, V> unmarshallMap(final JsonNode json,
                                          final Class<K> keyType,
                                          final Class<V> valueType) {
        return this.unmarshallContext.unmarshallMap(
                json,
                keyType,
                valueType
        );
    }

    @Override
    public <T> T unmarshallWithType(final JsonNode json) {
        return this.unmarshallContext.unmarshallWithType(json);
    }

    @Override
    public <T> List<T> unmarshallWithTypeList(final JsonNode json) {
        return this.unmarshallContext.unmarshallWithTypeList(json);
    }

    @Override
    public <T> Set<T> unmarshallWithTypeSet(final JsonNode json) {
        return this.unmarshallContext.unmarshallWithTypeSet(json);
    }

    @Override
    public <K, V> Map<K, V> unmarshallWithTypeMap(final JsonNode json) {
        return this.unmarshallContext.unmarshallWithTypeMap(json);
    }

    @Override
    public Optional<Class<?>> registeredType(final JsonString string) {
        return this.unmarshallContext.registeredType(string);
    }

    @Override
    public Optional<JsonString> typeName(final Class<?> type) {
        return this.unmarshallContext.typeName(type);
    }

    private final JsonNodeUnmarshallContext unmarshallContext;

    @Override
    public String toString() {
        return this.marshallContext +
                " " +
                this.unmarshallContext;
    }
}
