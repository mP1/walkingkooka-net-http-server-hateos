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
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.server.HttpRequestAttribute;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

final class HateosResourceSelectionOne<I extends Comparable<I>> extends HateosResourceSelectionValue<I, I> {

    static <I extends Comparable<I>> HateosResourceSelectionOne<I> with(final I value) {
        Objects.requireNonNull(value, "value");
        return new HateosResourceSelectionOne<>(value);
    }

    HateosResourceSelectionOne(final I value) {
        super(value);
    }

    @Override
    HttpEntity handleHateosHttpEntityHandler(final HateosHttpEntityHandler<I, ?> handler,
                                             final HttpEntity entity,
                                             final Map<HttpRequestAttribute<?>, Object> parameters,
                                             final HateosResourceHandlerContext context) {
        return handler.handleOne(
                this.value(),
                entity,
                parameters,
                Cast.to(context)
        );
    }

    @Override
    Optional<?> handleHateosResourceHandler(final HateosResourceHandler<I, ?, ?, ?> handler,
                                            final Optional<?> resource,
                                            final Map<HttpRequestAttribute<?>, Object> parameters,
                                            final HateosResourceHandlerContext context) {
        return handler.handleOne(
                this.value(),
                Cast.to(resource),
                parameters,
                Cast.to(context)
        );
    }
}
