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
import walkingkooka.net.UrlPath;
import walkingkooka.net.UrlPathName;
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpRequestAttributes;
import walkingkooka.route.Router;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link Router} that dispatches to the given {@link HateosResourceMappings mappings}.
 * Note that any exceptions that are thrown, will have their stack trace in the response body with content-type=text/plain
 */
final class HateosResourceMappingsRouter<C extends HateosHandlerContext> implements Router<HttpRequestAttribute<?>, HttpHandler<C>> {

    static <C extends HateosHandlerContext> HateosResourceMappingsRouter<C> with(final UrlPath base,
                                                                                 final Set<HateosResourceMappings<?, ?, ?, ?, C>> mappings) {
        Objects.requireNonNull(base, "base");
        Objects.requireNonNull(mappings, "mappings");

        return new HateosResourceMappingsRouter<>(
            base,
            mappings
        );
    }

    private HateosResourceMappingsRouter(final UrlPath base,
                                         final Set<HateosResourceMappings<?, ?, ?, ?, C>> mappings) {
        super();
        this.base = base.normalize();
        Map<HateosResourceName, HateosResourceMappings<?, ?, ?, ?, C>> resourceNameToMapping = Maps.sorted();

        for (final HateosResourceMappings<?, ?, ?, ?, C> mappingsMappings : mappings) {
            resourceNameToMapping.put(
                mappingsMappings.resourceName,
                mappingsMappings
            );
        }

        this.resourceNameToMapping = resourceNameToMapping;
    }

    final Map<HateosResourceName, HateosResourceMappings<?, ?, ?, ?, C>> resourceNameToMapping;

    // Router...........................................................................................................

    @Override
    public Optional<HttpHandler<C>> route(final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(parameters, "parameters");

        // a handler will be returned if the request path matches the #base path
        return Optional.ofNullable(
            -1 != this.consumeBasePath(parameters) ?
                HateosResourceMappingsRouterHttpHandler.with(
                    this
                ) :
                null
        );
    }

    /**
     * Attempts to consume the {@link #base} completely returning the index to the {@link HateosResourceName} component within the path or -1.
     */
    int consumeBasePath(final Map<HttpRequestAttribute<?>, Object> parameters) {
        int pathIndex = 0;
        for (final UrlPathName name : this.base) {
            if (false == name.equals(parameters.get(HttpRequestAttributes.pathComponent(pathIndex)))) {
                pathIndex = -1;
                break;
            }
            pathIndex++;
        }
        return pathIndex;
    }

    private final UrlPath base;

    // toString.........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .enable(ToStringBuilderOption.SKIP_IF_DEFAULT_VALUE)
            .value(this.base)
            .value(this.resourceNameToMapping.values())
            .build();
    }
}
