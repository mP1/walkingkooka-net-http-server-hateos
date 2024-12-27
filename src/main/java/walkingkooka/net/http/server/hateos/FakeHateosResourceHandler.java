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

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link HateosResourceHandler} where all methods throw {@link UnsupportedOperationException}.
 */
public class FakeHateosResourceHandler<I extends Comparable<I>, V, C, X extends HateosResourceHandlerContext> implements HateosResourceHandler<I, V, C, X>, Fake {

    @Override
    public Optional<C> handleAll(final Optional<C> resource,
                                 final Map<HttpRequestAttribute<?>, Object> parameters,
                                 final X context) {
        HateosResourceHandler.checkResource(resource);
        HateosResourceHandler.checkParameters(parameters);
        HateosResourceHandler.checkContext(context);

        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<C> handleMany(final Set<I> ids,
                                  final Optional<C> resource,
                                  final Map<HttpRequestAttribute<?>, Object> parameters,
                                  final X context) {
        HateosResourceHandler.checkManyIds(ids);
        HateosResourceHandler.checkResource(resource);
        HateosResourceHandler.checkParameters(parameters);
        HateosResourceHandler.checkContext(context);

        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<V> handleNone(final Optional<V> resource,
                                  final Map<HttpRequestAttribute<?>, Object> parameters,
                                  final X context) {
        HateosResourceHandler.checkResource(resource);
        HateosResourceHandler.checkParameters(parameters);
        HateosResourceHandler.checkContext(context);

        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<V> handleOne(final I id,
                                 final Optional<V> resource,
                                 final Map<HttpRequestAttribute<?>, Object> parameters,
                                 final X context) {
        HateosResourceHandler.checkId(id);
        HateosResourceHandler.checkResource(resource);
        HateosResourceHandler.checkParameters(parameters);
        HateosResourceHandler.checkContext(context);

        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<C> handleRange(final Range<I> range,
                                   final Optional<C> resource,
                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                   final X context) {
        HateosResourceHandler.checkIdRange(range);
        HateosResourceHandler.checkResource(resource);
        HateosResourceHandler.checkParameters(parameters);
        HateosResourceHandler.checkContext(context);

        throw new UnsupportedOperationException();
    }
}
