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
import walkingkooka.collect.map.Maps;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.UrlPathName;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpRequestAttributes;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.route.RouteException;
import walkingkooka.route.Router;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * A {@link Router} that dispatches to the given {@link HateosResourceMapping mappings}.
 * Note that any exceptions that are thrown, will have their stack trace in the response body with content-type=text/plain
 */
final class HateosResourceMappingRouter implements Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> {

    static HateosResourceMappingRouter with(final AbsoluteUrl base,
                                            final HateosContentType contentType,
                                            final Set<HateosResourceMapping<?, ?, ?, ?>> mappings) {
        Objects.requireNonNull(base, "base");
        Objects.requireNonNull(contentType, "contentType");
        Objects.requireNonNull(mappings, "mappings");

        return new HateosResourceMappingRouter(base, contentType, mappings);
    }

    private HateosResourceMappingRouter(final AbsoluteUrl base,
                                        final HateosContentType contentType,
                                        final Set<HateosResourceMapping<?, ?, ?, ?>> mappings) {
        super();
        this.base = base;
        this.contentType = contentType;
        this.resourceNameToMapping = Maps.sorted();

        for (HateosResourceMapping<?, ?, ?, ?> mapping : mappings) {
            this.resourceNameToMapping.put(mapping.resourceName, mapping);
        }
    }

    // Router...........................................................................................................

    @Override
    public Optional<BiConsumer<HttpRequest, HttpResponse>> route(final Map<HttpRequestAttribute<?>, Object> parameters) throws RouteException {
        Objects.requireNonNull(parameters, "parameters");

        return Optional.ofNullable(this.canHandle(parameters) ?
                HateosResourceMappingRouterBiConsumer.with(this) :
                null);
    }

    /**
     * Returns true if the content types are a match and the request path matches the base url.
     */
    private boolean canHandle(final Map<HttpRequestAttribute<?>, Object> parameters) {
        // Optional.stream is not supported in J2cl hence the alternative.
        return HttpHeaderName.CONTENT_TYPE.parameterValue(parameters)
                .map(this::isContentTypeCompatible)
                .orElse(false) &&
                -1 != this.consumeBasePath(parameters);
    }

    /**
     * Only returns true if the hateos content type and the given {@link MediaType} are compatible.
     */
    private boolean isContentTypeCompatible(final MediaType possible) {
        return this.contentType.contentType()
                .test(possible);
    }

    /**
     * Attempts to consume the {@link #base} completely returning the index to the {@link HateosResourceName} component within the path or -1.
     */
    int consumeBasePath(final Map<HttpRequestAttribute<?>, Object> parameters) {
        // base path must be matched..................................................................................
        int pathIndex = 0;
        for (UrlPathName name : this.base.path()) {
            if (!name.equals(parameters.get(HttpRequestAttributes.pathComponent(pathIndex)))) {
                pathIndex = -1;
                break;
            }
            pathIndex++;
        }
        return pathIndex;
    }

    final AbsoluteUrl base;
    final HateosContentType contentType;
    final Map<HateosResourceName, HateosResourceMapping<?, ?, ?, ?>> resourceNameToMapping;

    // toString.........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .value(this.base)
                .value(this.contentType)
                .value(this.resourceNameToMapping.values())
                .build();
    }
}
