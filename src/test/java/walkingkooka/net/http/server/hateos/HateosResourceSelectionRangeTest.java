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

public final class HateosResourceSelectionRangeTest extends HateosResourceSelectionValueTestCase<HateosResourceSelectionRange<Integer>, Integer, Range<Integer>> {

    @Override
    public HateosResourceSelectionRange<Integer> createHateosResourceSelection(final Range<Integer> range) {
        return HateosResourceSelectionRange.with(range);
    }

    @Override
    Range<Integer> value() {
        return Range.greaterThanEquals(11).and(Range.lessThanEquals(22));
    }

    @Override
    public Class<HateosResourceSelectionRange<Integer>> type() {
        return Cast.to(HateosResourceSelectionRange.class);
    }
}
