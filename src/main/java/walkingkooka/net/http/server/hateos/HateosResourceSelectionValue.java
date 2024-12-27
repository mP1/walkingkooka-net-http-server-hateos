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

import walkingkooka.Value;
import walkingkooka.collect.Range;

import java.util.List;

/**
 * Base class for several {@link HateosResourceSelection} that hold a single value such as an id, {@link Range} or {@link List}.
 */
abstract class HateosResourceSelectionValue<I extends Comparable<I>, V> extends HateosResourceSelection<I> implements Value<V> {

    HateosResourceSelectionValue(final V value) {
        super();
        this.value = value;
    }

    @Override
    public final V value() {
        return this.value;
    }

    private final V value;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    public final boolean equals(final Object other) {
        return this == other || other instanceof HateosResourceSelectionValue && this.equals0((HateosResourceSelectionValue<?, ?>) other);
    }

    private boolean equals0(final HateosResourceSelectionValue<?, ?> other) {
        return this.value.equals(other.value);
    }

    @Override
    public final String toString() {
        return this.value.toString();
    }
}
