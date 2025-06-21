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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.UrlPath;
import walkingkooka.net.UrlPathName;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.http.HttpMethod;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A mapping for a single {@link UrlPathName}
 */
final class HateosResourceMappingsMapping<I extends Comparable<I>, V, C, H extends HateosResource<I>, X extends HateosResourceHandlerContext> {

    /**
     * {@see HateosResourceMappingsMappingHandlerHateosHttpEntityHandler}
     */
    static <I extends Comparable<I>, V, C, H extends HateosResource<I>, X extends HateosResourceHandlerContext> HateosResourceMappingsMapping<I, V, C, H, X> empty(final LinkRelation<?> linkRelation) {
        return new HateosResourceMappingsMapping<>(
                linkRelation,
                null
        );
    }

    private HateosResourceMappingsMapping(final LinkRelation<?> linkRelation,
                                          final Map<HttpMethod, HateosResourceMappingsMappingHandler<?>> methodToHandlers) {
        super();
        this.linkRelation = linkRelation;
        this.methodToHandlers = methodToHandlers;
    }

    final LinkRelation<?> linkRelation;

    HateosResourceMappingsMapping<I, V, C, H, X> setHateosHttpEntityHandler(final HttpMethod method,
                                                                            final HateosHttpEntityHandler<I, X> handler) {
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(handler, "handler");

        final Map<HttpMethod, HateosResourceMappingsMappingHandler<?>> methodToHandlers = Maps.sorted();

        Map<HttpMethod, HateosResourceMappingsMappingHandler<?>> previous = this.methodToHandlers;
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
                        methodToHandlers
                );
    }

    /**
     * Sets or replaces a {@link LinkRelation} and {@link HttpMethod} with a {@link HateosResourceHandler}.
     */
    public HateosResourceMappingsMapping<I, V, C, H, X> setHateosResourceHandler(final HttpMethod method,
                                                                                 final HateosResourceHandler<I, V, C, X> handler) {
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(handler, "handler");

        final Map<HttpMethod, HateosResourceMappingsMappingHandler<?>> methodToHandlers = Maps.sorted();

        Map<HttpMethod, HateosResourceMappingsMappingHandler<?>> previous = this.methodToHandlers;
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
                        methodToHandlers
                );
    }

    void handle(final HateosResourceMappingsRouterHttpHandlerRequest request,
                final HateosResourceMappings<?, ?, ?, ?, ?> mappings,
                final HateosResourceSelection<?> selection,
                final UrlPath path,
                final HateosResourceHandlerContext context) {
        final HttpMethod method = request.request.method();
        final HateosResourceMappingsMappingHandler<?> handler = this.methodToHandlers.get(method);
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

    private final Map<HttpMethod, HateosResourceMappingsMappingHandler<?>> methodToHandlers;

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
                        && this.equals0((HateosResourceMappingsMapping<?, ?, ?, ?, ?>) other);
    }

    private boolean equals0(final HateosResourceMappingsMapping<?, ?, ?, ?, ?> other) {
        return Objects.equals(
                this.methodToHandlers,
                other.methodToHandlers
        );
    }

    @Override
    public String toString() {
        return String.valueOf(this.methodToHandlers);
    }
}
