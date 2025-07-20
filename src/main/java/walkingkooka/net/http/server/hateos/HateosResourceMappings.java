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
import walkingkooka.collect.map.Maps;
import walkingkooka.net.UrlPath;
import walkingkooka.net.UrlPathName;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.route.Router;
import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Holds all mappings for a single {@link HateosResource}, where a mapping is a combination of {@link HttpMethod} and
 * {@link LinkRelation} mapped to individual {@link HateosResourceHandler}.
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
public final class HateosResourceMappings<I extends Comparable<I>, V, C, H extends HateosResource<I>, X extends HateosResourceHandlerContext>
    implements TreePrintable {

    /**
     * This header will appear in any successful JSON response and contains the simple java type name (Class#getSimpleName())
     * for the java object converted to JSON.
     */
    public final static HttpHeaderName<String> X_CONTENT_TYPE_NAME = HttpHeaderName.with("X-Content-Type-Name").stringValues();

    /**
     * Creates a new {@link HateosResourceMappings}
     */
    public static <I extends Comparable<I>, V, C, H extends HateosResource<I>, X extends HateosResourceHandlerContext> HateosResourceMappings<I, V, C, H, X> with(
        final HateosResourceName resourceName,
        final BiFunction<String, X, HateosResourceSelection<I>> selection,
        final Class<V> valueType,
        final Class<C> collectionType,
        final Class<H> resourceType,
        final Class<X> contextType) {
        Objects.requireNonNull(resourceName, "resourceName");
        Objects.requireNonNull(selection, "selection");
        Objects.requireNonNull(valueType, "valueType");
        Objects.requireNonNull(collectionType, "collectionType");
        Objects.requireNonNull(contextType, "contextType");

        if (collectionType.isInterface()) {
            throw new IllegalArgumentException("Collection type " + collectionType.getName() + " is an interface expected a concrete class");
        }
        if (collectionType.isArray()) {
            throw new IllegalArgumentException("Collection type " + collectionType.getName() + " is an array expected a concrete class");
        }

        Objects.requireNonNull(resourceType, "resourceType");
        if (resourceType.isInterface()) {
            throw new IllegalArgumentException("Resource type " + resourceType.getName() + " is an interface expected a concrete class");
        }
        if (resourceType.isArray()) {
            throw new IllegalArgumentException("Resource type " + resourceType.getName() + " is an array expected a concrete class");
        }

        return new HateosResourceMappings<>(
            resourceName,
            selection,
            valueType,
            collectionType,
            resourceType,
            Maps.empty()
        );
    }

    /**
     * Private ctor use factory.
     */
    private HateosResourceMappings(final HateosResourceName resourceName,
                                   final BiFunction<String, X, HateosResourceSelection<I>> selection,
                                   final Class<V> valueType,
                                   final Class<C> collectionType,
                                   final Class<H> resourceType,
                                   final Map<UrlPathName, HateosResourceMappingsMapping<I, V, C, H, X>> pathNameToMappings) {
        super();
        this.resourceName = resourceName;
        this.selection = selection;
        this.valueType = valueType;
        this.collectionType = collectionType;
        this.resourceType = resourceType;

        this.pathNameToMappings = pathNameToMappings;
    }

    /**
     * Sets or replaces a {@link LinkRelation} and {@link HttpMethod} with a {@link HateosHttpEntityHandler}.
     */
    public HateosResourceMappings<I, V, C, H, X> setHateosHttpEntityHandler(final LinkRelation<?> relation,
                                                                            final HttpMethod method,
                                                                            final HateosHttpEntityHandler<I, X> handler) {
        checkLinkRelation(relation);
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(handler, "handler");

        final Map<UrlPathName, HateosResourceMappingsMapping<I, V, C, H, X>> pathNameToMappings = Maps.sorted();
        pathNameToMappings.putAll(this.pathNameToMappings);

        final UrlPathName pathName = relation.toUrlPathName()
            .get();

        HateosResourceMappingsMapping<I, V, C, H, X> mappingHandler = pathNameToMappings.get(pathName);
        if (null == mappingHandler) {
            mappingHandler = HateosResourceMappingsMapping.empty(
                relation,
                null
            );
        }
        pathNameToMappings.put(
            pathName,
            mappingHandler.setHateosHttpEntityHandler(
                method,
                handler
            )
        );

        return this.pathNameToMappings.equals(pathNameToMappings) ?
            this :
            new HateosResourceMappings<>(
                this.resourceName,
                this.selection,
                this.valueType,
                this.collectionType,
                this.resourceType,
                pathNameToMappings
            );
    }

    /**
     * Sets or replaces a {@link LinkRelation} and {@link HttpMethod} with a {@link HateosResourceHandler}.
     */
    public HateosResourceMappings<I, V, C, H, X> setHateosResourceHandler(final LinkRelation<?> relation,
                                                                          final HttpMethod method,
                                                                          final HateosResourceHandler<I, V, C, X> handler) {
        checkLinkRelation(relation);
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(handler, "handler");

        final Map<UrlPathName, HateosResourceMappingsMapping<I, V, C, H, X>> pathNameToMappings = Maps.sorted();
        pathNameToMappings.putAll(this.pathNameToMappings);

        final UrlPathName pathName = relation.toUrlPathName()
            .get();

        HateosResourceMappingsMapping<I, V, C, H, X> mappingHandler = pathNameToMappings.get(pathName);
        if (null == mappingHandler) {
            mappingHandler = HateosResourceMappingsMapping.empty(
                relation,
                null
            );
        }
        pathNameToMappings.put(
            pathName,
            mappingHandler.setHateosResourceHandler(
                method,
                handler
            )
        );

        return this.pathNameToMappings.equals(pathNameToMappings) ?
            this :
            new HateosResourceMappings<>(
                this.resourceName,
                this.selection,
                this.valueType,
                this.collectionType,
                this.resourceType,
                pathNameToMappings
            );
    }

    private static LinkRelation<?> checkLinkRelation(final LinkRelation<?> relation) {
        Objects.requireNonNull(relation, "relation");
        if (relation.isUrl()) {
            throw new IllegalArgumentException("Invalid relation, urls are not supported");
        }
        return relation;
    }

    /**
     * Sets a {@link HttpHandler} to handle requests at the given relative {@link UrlPathName}.
     */
    public HateosResourceMappings<I, V, C, H, X> setHateosHttpHandler(final UrlPathName pathName,
                                                                      final HateosHttpHandler<X> handler) {
        Objects.requireNonNull(pathName, "pathName");
        Objects.requireNonNull(handler, "handler");

        final Map<UrlPathName, HateosResourceMappingsMapping<I, V, C, H, X>> pathNameToMappings = Maps.sorted();
        pathNameToMappings.putAll(this.pathNameToMappings);

        HateosResourceMappingsMapping<I, V, C, H, X> mappingHandler = pathNameToMappings.get(pathName);
        if (null == mappingHandler) {
            mappingHandler = HateosResourceMappingsMapping.empty(
                null,
                handler
            );
        }
        pathNameToMappings.put(
            pathName,
            mappingHandler.setHateosHttpHandler(handler)
        );

        return this.pathNameToMappings.equals(pathNameToMappings) ?
            this :
            new HateosResourceMappings<>(
                this.resourceName,
                this.selection,
                this.valueType,
                this.collectionType,
                this.resourceType,
                pathNameToMappings
            );
    }

    // HateosResourceMappingsRouter.....................................................................................

    /**
     * Creates a {@link Router} from the provided {@link HateosResourceMappings mappings}.
     */
    public static <X extends HateosResourceHandlerContext> Router<HttpRequestAttribute<?>, HttpHandler> router(final UrlPath base,
                                                                                                               final Set<HateosResourceMappings<?, ?, ?, ?, X>> mappings,
                                                                                                               final X context) {
        return HateosResourceMappingsRouter.with(
            base,
            mappings,
            context
        );
    }

    final HateosResourceName resourceName;

    /**
     * A parser function that converts a {@link String} from the url path into an {@link Comparable id}.
     */
    final BiFunction<String, X, HateosResourceSelection<I>> selection;

    /**
     * The type used to marshall the resource for
     * <ol>
     * <li>{@link HateosResourceHandler#handleNone(Optional, Map, UrlPath, HateosResourceHandlerContext)},</li>
     * <li>{@link HateosResourceHandler#handleOne(Comparable, Optional, Map, UrlPath, HateosResourceHandlerContext)},</li>
     * </ol>
     */
    final Class<V> valueType;

    /**
     * The type used to marshall the resource for
     * <ol>
     * <li>{@link HateosResourceHandler#handleMany(Set, Optional, Map, UrlPath, HateosResourceHandlerContext)},</li>
     * <li>{@link HateosResourceHandler#handleRange(Range, Optional, Map, UrlPath, HateosResourceHandlerContext)},</li>
     * <li>{@link HateosResourceHandler#handleAll(Optional, Map, UrlPath, HateosResourceHandlerContext)},</li>
     * </ol>
     */
    final Class<C> collectionType;

    /**
     * The {@link HateosResource type}.
     */
    final Class<H> resourceType;

    /**
     * Contains all individual mappings.
     */
    final Map<UrlPathName, HateosResourceMappingsMapping<I, V, C, H, X>> pathNameToMappings;

    // toString.........................................................................................................

    @Override
    public String toString() {
        final ToStringBuilder b = ToStringBuilder.empty();
        b.enable(ToStringBuilderOption.SKIP_IF_DEFAULT_VALUE);
        b.valueSeparator(" ");

        b.value(this.resourceName);
        b.value(this.resourceType.getName());

        b.valueSeparator(",");
        b.value(this.pathNameToMappings);

        return b.build();
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        TreePrintable.printTreeOrToString(
            this.resourceName,
            printer
        );

        printer.indent();
        {
            printer.println("ResourceType:");
            printer.indent();
            {
                printer.println(this.resourceType.getSimpleName());
            }
            printer.outdent();
        }
        printer.outdent();

        printer.indent();
        {
            printer.println("mappings:");
            printer.indent();
            {
                for (final Entry<UrlPathName, HateosResourceMappingsMapping<I, V, C, H, X>> pathNameAndMapping : this.pathNameToMappings.entrySet()) {
                    printer.indent();
                    {
                        printer.println(
                            CharSequences.quoteAndEscape(
                                pathNameAndMapping.getKey()
                                    .value()
                            )
                        );
                        printer.indent();
                        {
                            pathNameAndMapping.getValue()
                                .printTree(printer);
                        }
                        printer.outdent();
                    }
                    printer.outdent();
                }
            }
            printer.println();
        }
        printer.outdent();
    }
}
