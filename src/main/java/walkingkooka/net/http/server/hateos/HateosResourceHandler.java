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

import walkingkooka.collect.Range;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.http.server.HttpRequestAttribute;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Handles a HATEOS request for one or more {@link HateosResource} handling the unmarshalling of the request body and
 * marshalling of the response to the response body.
 */
public interface HateosResourceHandler<I extends Comparable<I>, V, C, X extends HateosResourceHandlerContext> {

    /**
     * An empty {@link Map} with no parameters.
     */
    Map<HttpRequestAttribute<?>, Object> NO_PARAMETERS = Maps.empty();

    /**
     * Handles a request for a resource
     * <pre>
     * /resource/*
     * </pre>>
     */
    Optional<C> handleAll(final Optional<C> resource,
                          final Map<HttpRequestAttribute<?>, Object> parameters,
                          final X context);

    /**
     * Handles a collection of resources identified by the given Ids
     * <pre>
     * /resource/1,20,300
     * </pre>>
     */
    Optional<C> handleMany(final Set<I> ids,
                           final Optional<C> resource,
                           final Map<HttpRequestAttribute<?>, Object> parameters,
                           final X context);

    /**
     * Handles a resources identified by the given id
     * <pre>
     * /resource/123
     * </pre>>
     */
    Optional<V> handleOne(final I id,
                          final Optional<V> resource,
                          final Map<HttpRequestAttribute<?>, Object> parameters,
                          final X context);

    /**
     * Handles a collection of resources without any Id
     * <pre>
     * /resource
     * </pre>>
     */
    Optional<V> handleNone(final Optional<V> resource,
                           final Map<HttpRequestAttribute<?>, Object> parameters,
                           final X context);

    /**
     * Handles a collection of resources identified by the given Ids
     * <pre>
     * /resource/12-34
     * </pre>>
     */
    Optional<C> handleRange(final Range<I> range,
                            final Optional<C> resource,
                            final Map<HttpRequestAttribute<?>, Object> parameters,
                            final X context);

    // parameter checkers...............................................................................................

    /**
     * Complains if the id is null.
     */
    static <I extends Comparable<I>> I checkId(final I id) {
        return Objects.requireNonNull(id, "id");
    }

    /**
     * Complains if the {@link Set} is null.
     */
    static <I extends Comparable<I>> Set<I> checkManyIds(final Set<I> ids) {
        return Objects.requireNonNull(ids, "ids");
    }
    
    /**
     * Complains if the {@link Range} is null.
     */
    static <I extends Comparable<I>> Range<I> checkIdRange(final Range<I> range) {
        return Objects.requireNonNull(range, "range");
    }

    /**
     * Complains if the resource is null.
     */
    static <T> Optional<T> checkResource(final Optional<T> resource) {
        return Objects.requireNonNull(resource, "resource");
    }

    /**
     * Complains if the resource is null or present.
     */
    static <T> Optional<T> checkResourceEmpty(final Optional<T> resource) {
        checkResource(resource);
        resource.ifPresent((r) -> {
            throw new IllegalArgumentException("Resource not allowed=" + r);
        });
        return resource;
    }

    /**
     * Complains if the resource is absent.
     */
    static <T> T checkResourceNotEmpty(final Optional<T> resource) {
        checkResource(resource);
        return resource.orElseThrow(() -> new IllegalArgumentException("Required resource missing"));
    }

    /**
     * Checks parameters are present.
     */
    static Map<HttpRequestAttribute<?>, Object> checkParameters(final Map<HttpRequestAttribute<?>, Object> parameters) {
        return Objects.requireNonNull(parameters, "parameters");
    }

    /**
     * Requires a {@link HateosResourceHandlerContext} to be present.
     */
    static <X extends HateosResourceHandlerContext> X checkContext(final X context) {
        return Objects.requireNonNull(context, "context");
    }
}
