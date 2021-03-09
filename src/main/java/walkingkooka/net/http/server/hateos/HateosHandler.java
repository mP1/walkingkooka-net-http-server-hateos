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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Handles a HATEOS request for a {@link HateosResource} including the marshalling between to and from text from
 * the {@link walkingkooka.net.http.server.HttpRequest#body()}.
 */
public interface HateosHandler<I extends Comparable<I>, V, C> {

    /**
     * An empty {@link Map} with no parameters.
     */
    Map<HttpRequestAttribute<?>, Object> NO_PARAMETERS = Maps.empty();

    /**
     * Handles a all request for the resource.
     */
    Optional<C> handleAll(final Optional<C> resource,
                          final Map<HttpRequestAttribute<?>, Object> parameters);

    /**
     * Handles a resource identified by a range of ids
     */
    Optional<C> handleList(final List<I> list,
                           final Optional<C> resource,
                           final Map<HttpRequestAttribute<?>, Object> parameters);

    /**
     * Handles a resource identified by the ID.
     */
    Optional<V> handleOne(final I id,
                          final Optional<V> resource,
                          final Map<HttpRequestAttribute<?>, Object> parameters);

    /**
     * Handles a resource without an id.
     */
    Optional<V> handleNone(final Optional<V> resource,
                           final Map<HttpRequestAttribute<?>, Object> parameters);

    /**
     * Handles a resource identified by a range of ids
     */
    Optional<C> handleRange(final Range<I> range,
                            final Optional<C> resource,
                            final Map<HttpRequestAttribute<?>, Object> parameters);

    // parameter checkers...............................................................................................

    /**
     * Complains if the id is null.
     */
    static void checkId(final Comparable<?> id) {
        Objects.requireNonNull(id, "id");
    }

    /**
     * Complains if the {@link List} is null.
     */
    static void checkList(final List<?> list) {
        Objects.requireNonNull(list, "list");
    }
    
    /**
     * Complains if the {@link Range} is null.
     */
    static void checkRange(final Range<?> range) {
        Objects.requireNonNull(range, "range");
    }

    /**
     * Complains if the resource is null.
     */
    static void checkResource(final Optional<?> resource) {
        Objects.requireNonNull(resource, "resource");
    }

    /**
     * Complains if the resource is null or present.
     */
    static void checkResourceEmpty(final Optional<?> resource) {
        checkResource(resource);
        resource.ifPresent((r) -> {
            throw new IllegalArgumentException("Resource not allowed=" + r);
        });
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
    static void checkParameters(final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(parameters, "parameters");
    }
}
