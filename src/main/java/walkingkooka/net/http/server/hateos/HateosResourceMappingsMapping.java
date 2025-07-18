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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.UrlPath;
import walkingkooka.net.UrlPathName;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * A mapping for a single {@link UrlPathName}
 */
final class HateosResourceMappingsMapping<I extends Comparable<I>, V, C, H extends HateosResource<I>, X extends HateosResourceHandlerContext>
    implements TreePrintable {

    /**
     * {@see HateosResourceMappingsMappingHandlerHateosHttpEntityHandler}
     */
    static <I extends Comparable<I>, V, C, H extends HateosResource<I>, X extends HateosResourceHandlerContext> HateosResourceMappingsMapping<I, V, C, H, X> empty(final LinkRelation<?> linkRelation,
                                                                                                                                                                   final HttpHandler httpHandler) {
        return new HateosResourceMappingsMapping<>(
            linkRelation,
            null, // Map<HttpMethod, HateosResourceMappingsMappingHandler<?>>
            httpHandler
        );
    }

    private HateosResourceMappingsMapping(final LinkRelation<?> linkRelation,
                                          final Map<HttpMethod, HateosResourceMappingsMappingHandler<I, V, C, H, X>> methodToHandlers,
                                          final HttpHandler httpHandler) {
        super();
        this.linkRelation = linkRelation;
        this.methodToHandlers = methodToHandlers;
        this.httpHandler = httpHandler;
    }

    final LinkRelation<?> linkRelation;

    HateosResourceMappingsMapping<I, V, C, H, X> setHateosHttpEntityHandler(final HttpMethod method,
                                                                            final HateosHttpEntityHandler<I, X> handler) {
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(handler, "handler");

        this.httpHandlerCheck();

        final Map<HttpMethod, HateosResourceMappingsMappingHandler<I, V, C, H, X>> methodToHandlers = Maps.sorted();

        Map<HttpMethod, HateosResourceMappingsMappingHandler<I, V, C, H, X>> previous = this.methodToHandlers;
        if (null != previous) {
            methodToHandlers.putAll(previous);
        }
        methodToHandlers.put(
            method,
            HateosResourceMappingsMappingHandler.hateosHttpEntityHandler(handler)
        );

        return methodToHandlers.equals(previous) ?
            this :
            new HateosResourceMappingsMapping<>(
                this.linkRelation,
                methodToHandlers,
                null // HttpHandler
            );
    }

    /**
     * Sets or replaces a {@link LinkRelation} and {@link HttpMethod} with a {@link HateosResourceHandler}.
     */
    HateosResourceMappingsMapping<I, V, C, H, X> setHateosResourceHandler(final HttpMethod method,
                                                                          final HateosResourceHandler<I, V, C, X> handler) {
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(handler, "handler");

        this.httpHandlerCheck();

        final Map<HttpMethod, HateosResourceMappingsMappingHandler<I, V, C, H, X>> methodToHandlers = Maps.sorted();

        Map<HttpMethod, HateosResourceMappingsMappingHandler<I, V, C, H, X>> previous = this.methodToHandlers;
        if (null != previous) {
            methodToHandlers.putAll(previous);
        }
        methodToHandlers.put(
            method,
            HateosResourceMappingsMappingHandler.hateosResourceHandler(handler)
        );

        return methodToHandlers.equals(previous) ?
            this :
            new HateosResourceMappingsMapping<>(
                this.linkRelation,
                methodToHandlers,
                null // HttpHandler
            );
    }

    /**
     * Sets a {@link HttpHandler}
     */
    HateosResourceMappingsMapping<I, V, C, H, X> setHttpHandler(final HttpHandler handler) {
        Objects.requireNonNull(handler, "handler");

        if (null != this.methodToHandlers) {
            throw new IllegalStateException("Clash with existing " + HateosHttpEntityHandler.class.getSimpleName() + " / " + HateosResourceHandler.class.getSimpleName());
        }

        return new HateosResourceMappingsMapping<>(
            this.linkRelation,
            this.methodToHandlers,
            handler
        );
    }

    private void httpHandlerCheck() {
        if (null != this.httpHandler) {
            throw new IllegalStateException("Clash with existing " + HttpHandler.class.getSimpleName());
        }
    }

    void handle(final HateosResourceMappingsRouterHttpHandlerRequest<X> request,
                final HateosResourceMappings<I, V, C, H, X> mappings,
                final HateosResourceSelection<?> selection,
                final UrlPath path,
                final HateosResourceHandlerContext context) {
        final HttpHandler httpHandler = this.httpHandler;
        if (null != httpHandler) {
            httpHandler.handle(
                request.request,
                request.response
            );
        } else {

            final HttpMethod method = request.request.method();
            final HateosResourceMappingsMappingHandler<I, V, C, H, X> handler = this.methodToHandlers.get(method);
            if (null != handler) {
                handler.handle(
                    request,
                    mappings,
                    selection,
                    path,
                    context
                );
            } else {
                request.methodNotAllowed(
                    mappings.resourceName,
                    this.linkRelation,
                    this.allowedMethods() // allowed HttpMethods
                );
            }
        }
    }

    private final Map<HttpMethod, HateosResourceMappingsMappingHandler<I, V, C, H, X>> methodToHandlers;

    private final HttpHandler httpHandler;

    List<HttpMethod> allowedMethods() {
        if (null == this.allowedMethods) {
            final List<HttpMethod> methods = Lists.array();
            methods.addAll(this.methodToHandlers.keySet());
            this.allowedMethods = methods;
        }
        return this.allowedMethods;
    }

    private List<HttpMethod> allowedMethods;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.methodToHandlers);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            null != other && this.getClass() == other.getClass()
                && this.equals0((HateosResourceMappingsMapping<I, V, C, H, X>) other);
    }

    private boolean equals0(final HateosResourceMappingsMapping<I, V, C, H, X> other) {
        return Objects.equals(
            this.methodToHandlers,
            other.methodToHandlers
        ) && Objects.equals(
            this.httpHandler,
            other.httpHandler
        );
    }

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .value(this.methodToHandlers)
            .value(this.httpHandler)
            .build();
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {

        {
            final Map<HttpMethod, HateosResourceMappingsMappingHandler<I, V, C, H, X>> methodToHandlers = this.methodToHandlers;
            if (null != methodToHandlers) {
                for (final Entry<HttpMethod, HateosResourceMappingsMappingHandler<I, V, C, H, X>> methodAndHandler : this.methodToHandlers.entrySet()) {
                    printer.println(methodAndHandler.getKey().value());

                    printer.indent();

                    TreePrintable.printTreeOrToString(
                        methodAndHandler.getValue(),
                        printer
                    );

                    printer.outdent();
                }
            }
        }

        {
            final HttpHandler httpHandler = this.httpHandler;
            if (null != httpHandler) {
                TreePrintable.printTreeOrToString(
                    httpHandler,
                    printer
                );
            }
        }
    }
}
