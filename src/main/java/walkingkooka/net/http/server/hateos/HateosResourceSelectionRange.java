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

import walkingkooka.Cast;
import walkingkooka.collect.Range;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.server.HttpRequestAttribute;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

final class HateosResourceSelectionRange<I extends Comparable<I>> extends HateosResourceSelectionValue<I, Range<I>> {

    static <I extends Comparable<I>> HateosResourceSelectionRange<I> with(final Range<I> value) {
        Objects.requireNonNull(value, "value");
        return new HateosResourceSelectionRange<>(value);
    }

    HateosResourceSelectionRange(final Range<I> value) {
        super(value);
    }

    @Override
    HttpEntity handleHateosHttpEntityHandler(final HateosHttpEntityHandler<I> handler,
                                             final HttpEntity entity) {
        return handler.handleRange(
                this.value(),
                entity
        );
    }

    @Override
    Optional<?> handleHateosResourceHandler(final HateosResourceHandler<I, ?, ?> handler,
                                            final Optional<?> resource,
                                            final Map<HttpRequestAttribute<?>, Object> parameters) {
        return handler.handleRange(
                this.value(),
                Cast.to(resource),
                parameters
        );
    }
}
