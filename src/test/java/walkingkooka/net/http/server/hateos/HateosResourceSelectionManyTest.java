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
import walkingkooka.collect.set.Sets;

import java.util.Set;

public final class HateosResourceSelectionManyTest extends HateosResourceSelectionValueTestCase<HateosResourceSelectionMany<Integer>, Integer, Set<Integer>> {

    @Override
    public HateosResourceSelectionMany<Integer> createHateosResourceSelection(final Set<Integer> ids) {
        return HateosResourceSelectionMany.with(ids);
    }

    @Override
    Set<Integer> value() {
        return Sets.of(11, 22);
    }

    @Override
    public Class<HateosResourceSelectionMany<Integer>> type() {
        return Cast.to(HateosResourceSelectionMany.class);
    }
}
