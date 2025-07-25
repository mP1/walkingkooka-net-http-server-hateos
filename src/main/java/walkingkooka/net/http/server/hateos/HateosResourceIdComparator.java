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
import walkingkooka.compare.Comparators;

import java.util.Comparator;

/**
 * A {@link Comparator} that may be used to sort {@link HateosResource}
 */
final class HateosResourceIdComparator<H extends HateosResource<I>, I extends Comparable<I>> implements Comparator<H> {

    /**
     * Singleton getter
     */
    static <H extends HateosResource<I>, I extends Comparable<I>> HateosResourceIdComparator<H, I> instance() {
        return Cast.to(INSTANCE);
    }

    /**
     * Singleton
     */
    private final static HateosResourceIdComparator<?, ?> INSTANCE = new HateosResourceIdComparator<>();

    /**
     * Private ctor use instance getter.
     */
    private HateosResourceIdComparator() {
        super();
    }

    @Override
    public int compare(final H left,
                       final H right) {
        final I leftId = left.id()
            .orElse(null);
        final I rightId = right.id()
            .orElse(null);

        return null == leftId && null == rightId ?
            Comparators.EQUAL :
            null == leftId ?
                Comparators.LESS :
                null == rightId ?
                    Comparators.MORE :
                    leftId.compareTo(rightId);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
