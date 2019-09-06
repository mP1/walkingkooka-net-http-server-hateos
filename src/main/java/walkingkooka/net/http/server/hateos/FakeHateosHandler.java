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

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.test.Fake;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link HateosHandler} where all methods throw {@link UnsupportedOperationException}.
 */
public class FakeHateosHandler<I extends Comparable<I>, R extends HateosResource<Optional<I>>, S extends HateosResource<Range<I>>>
        implements HateosHandler<I, R, S>, Fake {

    @Override
    public Optional<R> handle(final Optional<I> id,
                              final Optional<R> resource,
                              final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(resource, "resource");
        Objects.requireNonNull(parameters, "parameters");

        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<S> handleCollection(final Range<I> ids,
                                        final Optional<S> resource,
                                        final Map<HttpRequestAttribute<?>, Object> parameters) {
        Objects.requireNonNull(ids, "ids");
        Objects.requireNonNull(resource, "resource");
        Objects.requireNonNull(parameters, "parameters");

        throw new UnsupportedOperationException();
    }
}
