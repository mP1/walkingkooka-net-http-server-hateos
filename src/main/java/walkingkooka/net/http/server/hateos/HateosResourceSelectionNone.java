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

import java.util.Map;
import java.util.Optional;

final class HateosResourceSelectionNone<I extends Comparable<I>> extends HateosResourceSelection<I> {

    static <I extends Comparable<I>> HateosResourceSelectionNone<I> instance() {
        return Cast.to(INSTANCE);
    }

    private static final HateosResourceSelectionNone<?> INSTANCE = new HateosResourceSelectionNone<>();

    HateosResourceSelectionNone() {
        super();
    }

    @Override
    Optional<?> dispatch(final HateosHandler<I, ?, ?> handler,
                         final Optional<?> resource,
                         final Map<HttpRequestAttribute<?>, Object> parameters) {
        return handler.handleNone(Cast.to(resource), parameters);
    }

    @Override
    public String toString() {
        return "";
    }
}
