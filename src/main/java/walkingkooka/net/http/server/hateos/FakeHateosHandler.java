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
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.test.Fake;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link HateosHandler} where all methods throw {@link UnsupportedOperationException}.
 */
public class FakeHateosHandler<I extends Comparable<I>, V, C> implements HateosHandler<I, V, C>, Fake {

    public Optional<C> handleAll(final Optional<C> resource,
                                 final Map<HttpRequestAttribute<?>, Object> parameters) {
        check(resource, parameters);

        throw new UnsupportedOperationException();
    }

    public Optional<C> handleList(final List<I> list,
                                  final Optional<C> resource,
                                  final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(list, "list");
        check(resource, parameters);

        throw new UnsupportedOperationException();
    }

    public Optional<V> handleNone(final Optional<V> resource,
                                  final Map<HttpRequestAttribute<?>, Object> parameters) {
        check(resource, parameters);

        throw new UnsupportedOperationException();
    }

    public Optional<V> handleOne(final I id,
                                 final Optional<V> resource,
                                 final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(id, "id");
        check(resource, parameters);

        throw new UnsupportedOperationException();
    }

    public Optional<C> handleRange(final Range<I> range,
                                   final Optional<C> resource,
                                   final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(range, "range");
        check(resource, parameters);

        throw new UnsupportedOperationException();
    }

    private static void check(final Optional<?> resource,
                              final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(resource, "resource");
        Objects.requireNonNull(parameters, "parameters");
    }
}
