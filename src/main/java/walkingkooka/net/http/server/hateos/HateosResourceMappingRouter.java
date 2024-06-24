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
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpRequestAttributes;
import walkingkooka.route.RouteException;
import walkingkooka.route.Router;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link Router} that dispatches to the given {@link HateosResourceMapping mappings}.
 * Note that any exceptions that are thrown, will have their stack trace in the response body with content-type=text/plain
 */
final class HateosResourceMappingRouter implements Router<HttpRequestAttribute<?>, HttpHandler> {

    static HateosResourceMappingRouter with(final AbsoluteUrl base,
                                            final HateosContentType contentType,
                                            final Set<HateosResourceMapping<?, ?, ?, ?>> mappings,
                                            final Indentation indentation,
                                            final LineEnding lineEnding) {
        Objects.requireNonNull(base, "base");
        Objects.requireNonNull(contentType, "contentType");
        Objects.requireNonNull(mappings, "mappings");
        Objects.requireNonNull(indentation, "indentation");
        Objects.requireNonNull(lineEnding, "lineEnding");

        return new HateosResourceMappingRouter(
                base,
                contentType,
                mappings,
                indentation,
                lineEnding
        );
    }

    private HateosResourceMappingRouter(final AbsoluteUrl base,
                                        final HateosContentType contentType,
                                        final Set<HateosResourceMapping<?, ?, ?, ?>> mappings,
                                        final Indentation indentation,
                                        final LineEnding lineEnding) {
        super();
        this.base = base.normalize();
        this.contentType = contentType;
        this.resourceNameToMapping = Maps.sorted();

        for (final HateosResourceMapping<?, ?, ?, ?> mapping : mappings) {
            this.resourceNameToMapping.put(mapping.resourceName, mapping);
        }

        this.indentation = indentation;
        this.lineEnding = lineEnding;
    }

    // Router...........................................................................................................

    @Override
    public Optional<HttpHandler> route(final Map<HttpRequestAttribute<?>, Object> parameters) throws RouteException {
        Objects.requireNonNull(parameters, "parameters");

        // a handler will be returned if the request path matche the path
        return Optional.ofNullable(
                -1 != this.consumeBasePath(parameters) ?
                        this.httpHandler() :
                        null
        );
    }

//    /**
//     * When the method=GET the content-type must be absent, while for other methods the content-type must match,
//     * along with the base path.
//     */
//    private boolean canHandle(final Map<HttpRequestAttribute<?>, Object> parameters) {
//        final HttpMethod method = (HttpMethod) parameters.get(HttpRequestAttributes.METHOD);
//        final Optional<MediaType> contentType = HttpHeaderName.CONTENT_TYPE.parameterValue(parameters);
//
//        // Optional.stream is not supported in J2cl hence the alternative.
//        return HttpMethod.GET.equals(method) || contentType.map(this::isContentTypeCompatible)
//                .orElse(false) &&
//                -1 != this.consumeBasePath(parameters);
//    }
//
//    /**
//     * Only returns true if the hateos content type and the given {@link MediaType} are compatible.
//     */
//    private boolean isContentTypeCompatible(final MediaType possible) {
//        return this.contentType.contentType()
//                .test(possible);
//    }

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

    private HttpHandler httpHandler() {
        return HateosResourceMappingRouterHttpHandler.with(
                this,
                this.indentation,
                this.lineEnding
        );
    }

    private final Indentation indentation;
    private final LineEnding lineEnding;

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
