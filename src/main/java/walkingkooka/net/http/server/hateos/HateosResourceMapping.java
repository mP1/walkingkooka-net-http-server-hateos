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

import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.collect.Range;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.route.Router;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Holds all mappings for a single {@link HateosResource}, where a mapping is a combination of {@link HttpMethod} and
 * {@link LinkRelation} mapped to individual {@link HateosHandler}.
 * <br>
 * A very brief summary of {@link HttpStatusCode} used follows and required conditions. Different {@link HttpStatusCode}
 * indicate errors in the request or failure in executing the handler.
 * <ul>
 * <li>{@link HttpStatusCode#OK} - The response complete with a non empty response including a {@link HttpHeaderName#CONTENT_LENGTH}</li>
 * <li>{@link HttpStatusCode#NO_CONTENT} - The response complete with an empty response</li>
 * <li>{@link HttpStatusCode#BAD_REQUEST} - The request is invalid or incomplete such as an invalid id, unknown {@link HateosResourceName}</li>
 * <li>{@link HttpStatusCode#NOT_FOUND} - No handler for any method is present</li>
 * <li>{@link HttpStatusCode#METHOD_NOT_ALLOWED} - A handler is present for other {@link HttpMethod methods} but absent for the current method</li>
 * <li>{@link HttpStatusCode#LENGTH_REQUIRED} - The request body is not empty and a {@link HttpHeaderName#CONTENT_LENGTH} is missing</li>
 * <li>{@link HttpStatusCode#INTERNAL_SERVER_ERROR} - The handler throws an {@link RuntimeException} but not {@link UnsupportedOperationException}</li>
 * <li>{@link HttpStatusCode#NOT_IMPLEMENTED} - The handler throws an {@link UnsupportedOperationException}</li>
 * </ul>
 */
public final class HateosResourceMapping<I extends Comparable<I>, V, C, H extends HateosResource<I>> {

    /**
     * This header will appear in any successful JSON response and contains the simple java type name (Class#getSimpleName())
     * for the java object converted to JSON.
     */
    public final static HttpHeaderName<String> X_CONTENT_TYPE_NAME = HttpHeaderName.with("X-Content-Type-Name").stringValues();

    /**
     * Creates a new {@link HateosResourceMapping}
     */
    public static <I extends Comparable<I>, V, C, H extends HateosResource<I>> HateosResourceMapping<I, V, C, H> with(
            final HateosResourceName resourceName,
            final Function<String, HateosResourceSelection<I>> selection,
            final Class<V> valueType,
            final Class<C> collectionType,
            final Class<H> resourceType) {
        Objects.requireNonNull(resourceName, "resourceName");
        Objects.requireNonNull(selection, "selection");
        Objects.requireNonNull(valueType, "valueType");
        Objects.requireNonNull(collectionType, "collectionType");

        if (collectionType.isInterface()) {
            throw new IllegalArgumentException("Collection type " + collectionType.getName() + " is an interface expected concrete class");
        }
        if (collectionType.isArray()) {
            throw new IllegalArgumentException("Collection type " + collectionType.getName() + " is an array expected concrete class");
        }

        Objects.requireNonNull(resourceType, "resourceType");
        if (resourceType.isInterface()) {
            throw new IllegalArgumentException("Resource type " + resourceType.getName() + " is an interface expected a concrete class");
        }
        if (resourceType.isArray()) {
            throw new IllegalArgumentException("Resource type " + resourceType.getName() + " is an array expected a concrete class");
        }

        return new HateosResourceMapping<>(resourceName,
                selection,
                valueType,
                collectionType,
                resourceType,
                Maps.empty());
    }

    /**
     * Private ctor use factory.
     */
    private HateosResourceMapping(final HateosResourceName resourceName,
                                  final Function<String, HateosResourceSelection<I>> selection,
                                  final Class<V> valueType,
                                  final Class<C> collectionType,
                                  final Class<H> resourceType,
                                  final Map<HateosResourceMappingLinkRelationHttpMethod, HateosHandler<I, V, C>> relationAndMethodToHandlers) {
        super();
        this.resourceName = resourceName;
        this.selection = selection;
        this.valueType = valueType;
        this.collectionType = collectionType;
        this.resourceType = resourceType;
        this.relationAndMethodToHandlers = relationAndMethodToHandlers;

        // Builds a map of {@link LinkRelation to all {@link List} of {@link HttpMethod}.
        // Used by {@link #handlerOrMethodNotAllowed(LinkRelation, HateosResourceMappingRouterBiConsumerRequest) for the {@link HttpHeaderName#ALLOW}.
        final Map<LinkRelation<?>, List<HttpMethod>> relationToMethods = Maps.ordered();
        relationAndMethodToHandlers.keySet()
                .forEach(relationAndMethod -> {
                    final LinkRelation<?> r = relationAndMethod.relation;
                    List<HttpMethod> m = relationToMethods.get(r);
                    if (null == m) {
                        m = Lists.array();
                        relationToMethods.put(r, m);
                    }
                    m.add(relationAndMethod.method);
                });
        relationToMethods.values()
                .forEach(methods -> {
                    methods.sort(Comparator.naturalOrder());
                });

        this.relationToMethods = relationToMethods;
    }

    /**
     * Sets or replaces a {@link LinkRelation} and {@link HttpMethod} with a {@link HateosHandler}.
     */
    public HateosResourceMapping<I, V, C, H> set(final LinkRelation<?> relation,
                                                 final HttpMethod method,
                                                 final HateosHandler<I, V, C> handler) {
        final HateosResourceMappingLinkRelationHttpMethod key = HateosResourceMappingLinkRelationHttpMethod.with(relation, method);
        Objects.requireNonNull(handler, "handler");

        final Map<HateosResourceMappingLinkRelationHttpMethod, HateosHandler<I, V, C>> copy = Maps.sorted();
        copy.putAll(this.relationAndMethodToHandlers);
        final Object replaced = copy.put(key, handler);

        return handler.equals(replaced) ?
                this :
                new HateosResourceMapping<>(this.resourceName,
                        this.selection,
                        this.valueType,
                        this.collectionType,
                        this.resourceType,
                        copy);
    }

    /**
     * Mapping of all {@link LinkRelation} and {@link HttpMethod} to {@link HateosHandler}.
     */
    final Map<HateosResourceMappingLinkRelationHttpMethod, HateosHandler<I, V, C>> relationAndMethodToHandlers;

    // HateosResourceMappingRouter...............................................................................

    /**
     * Creates a {@link Router} from the provided {@link HateosResourceMapping mappings}.
     */
    public static Router<HttpRequestAttribute<?>, HttpHandler> router(final AbsoluteUrl base,
                                                                      final HateosContentType contentType,
                                                                      final Set<HateosResourceMapping<?, ?, ?, ?>> mappings,
                                                                      final Indentation indentation,
                                                                      final LineEnding lineEnding) {
        return HateosResourceMappingRouter.with(
                base,
                contentType,
                mappings,
                indentation,
                lineEnding
        );
    }

    final HateosResourceName resourceName;

    /**
     * A parser function that converts a {@link String} from the url path into an {@link Comparable id}.
     */
    final Function<String, HateosResourceSelection<I>> selection;

    /**
     * The type used to marshall the resource for
     * <ol>
     * <li>{@link HateosHandler#handleNone(Optional, Map)},</li>
     * <li>{@link HateosHandler#handleOne(Comparable, Optional, Map)},</li>
     * </ol>
     */
    final Class<V> valueType;

    /**
     * The type used to marshall the resource for
     * <ol>
     * <li>{@link HateosHandler#handleMany(List, Optional, Map)},</li>
     * <li>{@link HateosHandler#handleRange(Range, Optional, Map)},</li>
     * <li>{@link HateosHandler#handleAll(Optional, Map)},</li>
     * </ol>
     */
    final Class<C> collectionType;

    /**
     * The {@link HateosResource type}.
     */
    final Class<H> resourceType;

    /**
     * A lazy {@link Map} that maps a {@link LinkRelation} to all supported {@link HttpMethod}.
     * This is populated and used to fill the {@link HttpHeaderName#ALLOW} when an invalid request method is used.
     */
    final Map<LinkRelation<?>, List<HttpMethod>> relationToMethods;

    // toString.........................................................................................................

    @Override
    public String toString() {
        final ToStringBuilder b = ToStringBuilder.empty();
        b.enable(ToStringBuilderOption.SKIP_IF_DEFAULT_VALUE);
        b.valueSeparator(" ");

        b.value(this.resourceName);
        b.value(this.resourceType.getName());
        b.value(this.relationAndMethodToHandlers);

        return b.build();
    }
}
