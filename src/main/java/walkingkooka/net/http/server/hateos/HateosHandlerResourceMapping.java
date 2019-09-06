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
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.Range;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.routing.Router;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonObjectNode;
import walkingkooka.tree.json.marshall.ToJsonNodeContext;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Holds all mappings for a single {@link HateosResource}.
 */
public final class HateosHandlerResourceMapping<I extends Comparable<I>, R extends HateosResource<Optional<I>>, S extends HateosResource<Range<I>>> {

    /**
     * Creates a new {@link HateosHandlerResourceMapping}
     */
    public static <I extends Comparable<I>, R extends HateosResource<Optional<I>>, S extends HateosResource<Range<I>>> HateosHandlerResourceMapping<I, R, S> with(
            final HateosResourceName resourceName,
            final Function<String, I> stringToId,
            final Class<R> resourceType,
            final Class<S> collectionResourceType) {
        Objects.requireNonNull(resourceName, "resourceName");
        Objects.requireNonNull(stringToId, "stringToId");
        Objects.requireNonNull(resourceType, "resourceType");
        Objects.requireNonNull(collectionResourceType, "collectionResourceType");

        return new HateosHandlerResourceMapping<>(resourceName,
                stringToId,
                resourceType,
                collectionResourceType,
                Maps.empty());
    }

    /**
     * Private ctor use factory.
     */
    private HateosHandlerResourceMapping(final HateosResourceName resourceName,
                                         final Function<String, I> stringToId,
                                         final Class<R> resourceType,
                                         final Class<S> collectionResourceType,
                                         final Map<HateosHandlerResourceMappingLinkRelationHttpMethod, HateosHandler<I, R, S>> relationAndMethodToHandlers) {
        super();
        this.resourceName = resourceName;
        this.stringToId = stringToId;
        this.resourceType = resourceType;
        this.collectionResourceType = collectionResourceType;
        this.relationAndMethodToHandlers = relationAndMethodToHandlers;
    }

    /**
     * Sets or replaces a {@link LinkRelation} and {@link HttpMethod} with a {@link HateosHandler}.
     */
    public HateosHandlerResourceMapping<I, R, S> set(final LinkRelation relation,
                                                     final HttpMethod method,
                                                     final HateosHandler<I, R, S> handler) {
        final HateosHandlerResourceMappingLinkRelationHttpMethod key = HateosHandlerResourceMappingLinkRelationHttpMethod.with(relation, method);
        Objects.requireNonNull(handler, "handler");

        final Map<HateosHandlerResourceMappingLinkRelationHttpMethod, HateosHandler<I, R, S>> copy = Maps.sorted();
        copy.putAll(this.relationAndMethodToHandlers);
        final Object replaced = copy.put(key, handler);

        return handler.equals(replaced) ?
                this :
                new HateosHandlerResourceMapping(this.resourceName,
                        this.stringToId,
                        this.resourceType,
                        this.collectionResourceType, copy);
    }

    /**
     * Returns the {@link HateosHandler} if one exists for the given {@link LinkRelation} and {@link HttpMethod} combination.
     */
    public Optional<HateosHandler<I, R, S>> handler(final LinkRelation relation,
                                                    final HttpMethod method) {
        return Optional.ofNullable(this.relationAndMethodToHandlers.get(HateosHandlerResourceMappingLinkRelationHttpMethod.with(relation, method)));
    }

    /**
     * Mapping of all {@link LinkRelation} and {@link HttpMethod} to {@link HateosHandler}.
     */
    final Map<HateosHandlerResourceMappingLinkRelationHttpMethod, HateosHandler<I, R, S>> relationAndMethodToHandlers;

    // HateosHandlerResourceMappingRouter...............................................................................

    /**
     * Creates a {@link Router} from the provided {@link HateosHandlerResourceMapping mappings}.
     */
    public static Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> router(final AbsoluteUrl base,
                                                                                                final HateosContentType<?> contentType,
                                                                                                final Set<HateosHandlerResourceMapping<?, ?, ?>> mappings) {
        return HateosHandlerResourceMappingRouter.with(base, contentType, mappings);
    }

    /**
     * Creates a {@link BiFunction} which will add links to {@link Class hateos values} when they are marshalled to JSON.
     */
    public static BiFunction<Object, JsonObjectNode, JsonObjectNode> objectPostProcessor(final AbsoluteUrl base,
                                                                                         final Set<HateosHandlerResourceMapping<?, ?, ?>> mappings,
                                                                                         final Map<HateosResourceName, Class<?>> resourceNameToTypes,
                                                                                         final ToJsonNodeContext context) {
        return HateosHandlerResourceMappingObjectPostProcessorBiFunction.with(base,
                mappings,
                resourceNameToTypes,
                context);
    }

    final HateosResourceName resourceName;

    // HateosHandlerResourceMappingRouterBiConsumerRequest..............................................................

    /**
     * Handles a request for a single resource with the given parameters.
     */
    void handleId(final String idText,
                  final LinkRelation<?> linkRelation,
                  final HateosHandlerResourceMappingRouterBiConsumerRequest request) {
        final I id = this.idOrBadRequest(idText, request);
        if (null != id) {
            this.handleId0(Optional.of(id), linkRelation, request);
        }
    }

    /**
     * Handles a request for a single resource with the given parameters, assumes the ID has already been parsed.
     */
    void handleId0(final Optional<I> id,
                   final LinkRelation<?> linkRelation,
                   final HateosHandlerResourceMappingRouterBiConsumerRequest request) {
        final HateosHandler<I, R, S> handler = this.handlerOrResponseMethodNotAllowed(linkRelation,
                request);
        if (null != handler) {
            final String requestText = request.resourceTextOrBadRequest();
            if (null != request) {
                final HateosContentType<?> hateosContentType = request.hateosContentType();
                final Optional<R> requestResource = request.resourceOrBadRequest(requestText,
                        hateosContentType,
                        this.resourceType);

                if (null != requestResource) {
                    final HttpRequest httpRequest = request.request;
                    final HttpMethod method = httpRequest.method();
                    String responseText = null;
                    Optional<R> maybeResponseResource = handler.handle(id,
                            requestResource,
                            request.parameters);
                    if (maybeResponseResource.isPresent()) {
                        final R responseResource = maybeResponseResource.get();
                        responseText = hateosContentType.toText(responseResource);
                    }

                    request.setStatusAndBody(method + " resource successful", responseText);
                }
            }
        }
    }

    /**
     * Handles a request for ALL of a collection.
     */
    void handleIdRange(final LinkRelation<?> linkRelation,
                       final HateosHandlerResourceMappingRouterBiConsumerRequest request) {
        final HateosHandler<I, R, S> handler = this.handlerOrResponseMethodNotAllowed(linkRelation, request);
        if (null != handler) {
            this.handleIdRange0(Range.all(), handler, request);
        }
    }

    /**
     * Handles a request for a collection but with the range ends still requiring to be parsed.
     */
    void handleIdRange(final String begin,
                       final String end,
                       final String rangeText,
                       final LinkRelation<?> linkRelation,
                       final HateosHandlerResourceMappingRouterBiConsumerRequest request) {
        final Range<I> range = this.rangeOrBadRequest(begin, end, rangeText, request);
        if (null != range) {
            final HateosHandler<I, R, S> handler = this.handlerOrResponseMethodNotAllowed(linkRelation, request);
            if (null != handler) {
                this.handleIdRange0(range, handler, request);
            }
        }
    }

    private void handleIdRange0(final Range<I> ids,
                                final HateosHandler<I, R, S> handler,
                                final HateosHandlerResourceMappingRouterBiConsumerRequest request) {
        final String requestText = request.resourceTextOrBadRequest();
        if (null != requestText) {
            final HateosContentType<?> hateosContentType = request.hateosContentType();
            final Optional<S> requestResource = request.resourceOrBadRequest(requestText,
                    hateosContentType,
                    this.collectionResourceType);

            if (null != requestResource) {
                final HttpRequest httpRequest = request.request;
                final HttpMethod method = httpRequest.method();
                String responseText = null;
                Optional<S> maybeResponseResource = handler.handleCollection(ids,
                        requestResource,
                        request.parameters);
                if (maybeResponseResource.isPresent()) {
                    final S responseResource = maybeResponseResource.get();
                    responseText = hateosContentType.toText(responseResource);
                }

                request.setStatusAndBody(method + " resource successful", responseText);
            }
        }
    }

    /**
     * Parses or converts the id text, reporting a bad request if this fails.
     */
    private I idOrBadRequest(final String id,
                             final HateosHandlerResourceMappingRouterBiConsumerRequest request) {
        return this.idOrBadRequest(id, "id", id, request);
    }

    /**
     * Parses or converts the id text, reporting a bad request if this fails.
     */
    private I idOrBadRequest(final String id,
                             final String label, // id, range begin, range end
                             final String text,
                             final HateosHandlerResourceMappingRouterBiConsumerRequest request) {
        I parsed = null;
        try {
            parsed = this.stringToId.apply(id);
        } catch (final RuntimeException failed) {
            request.badRequest("Invalid " + label + " " + CharSequences.quote(text));
        }
        return parsed;
    }

    /**
     * Parses the range if any portion of that fails a bad request is set to the response.
     */
    private Range<I> rangeOrBadRequest(final String begin,
                                       final String end,
                                       final String rangeText,
                                       final HateosHandlerResourceMappingRouterBiConsumerRequest request) {
        Range<I> range = null;

        final I beginComparable = this.idOrBadRequest(begin, "range begin", rangeText, request);
        if (null != beginComparable) {
            final I endComparable = this.idOrBadRequest(end, "range end", rangeText, request);
            if (null != endComparable) {
                range = Range.greaterThanEquals(beginComparable)
                        .and(Range.lessThanEquals(endComparable));
            }
        }

        return range;
    }

    /**
     * Locates the {@link HateosHandler} for the given request method or sets the response to method not allowed.
     */
    private HateosHandler<I, R, S> handlerOrResponseMethodNotAllowed(final LinkRelation<?> linkRelation,
                                                                     final HateosHandlerResourceMappingRouterBiConsumerRequest request) {
        final Optional<HateosHandler<I, R, S>> maybe = this.handler(linkRelation, request.request.method());

        HateosHandler<I, R, S> handler = null;
        if (maybe.isPresent()) {
            handler = maybe.get();
        } else {
            request.methodNotAllowed(resourceName, linkRelation);
        }
        return handler;
    }

    /**
     * A parser function that converts a {@link String} from the url path into an {@link Comparable id}.
     */
    final Function<String, I> stringToId;
    final Class<R> resourceType;
    final Class<S> collectionResourceType;

    // toString.........................................................................................................

    @Override
    public String toString() {
        final ToStringBuilder b = ToStringBuilder.empty();
        b.enable(ToStringBuilderOption.SKIP_IF_DEFAULT_VALUE);
        b.valueSeparator(" ");

        b.value(this.resourceName);
        b.value(this.resourceType.getName());
        b.value(this.collectionResourceType.getName());
        b.value(this.relationAndMethodToHandlers);

        return b.build();
    }
}
