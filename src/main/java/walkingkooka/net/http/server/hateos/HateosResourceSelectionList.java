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
import walkingkooka.net.http.server.HttpRequestAttribute;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

final class HateosResourceSelectionList<I extends Comparable<I>> extends HateosResourceSelectionValue<I, List<I>> {

    final static <I extends Comparable<I>> HateosResourceSelectionList<I> with(final List<I> value) {
        Objects.requireNonNull(value, "value");
        return new HateosResourceSelectionList<>(value);
    }

    HateosResourceSelectionList(final List<I> value) {
        super(value);
    }

    @Override
    Optional<?> dispatch(final HateosHandler<I, ?, ?> handler,
                         final Optional<?> resource,
                         final Map<HttpRequestAttribute<?>, Object> parameters) {
        return handler.handleList(this.value(), Cast.to(resource), parameters);
    }
}
