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

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.compare.ComparatorTesting2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Set;

public final class HateosResourceIdComparatorTest implements ComparatorTesting2<HateosResourceIdComparator<TestHateosResource, BigInteger>, TestHateosResource> {

    @Test
    public void testSort() {
        final TestHateosResource none = TestHateosResource.with(null);
        final TestHateosResource a = TestHateosResource.with(BigInteger.ZERO);
        final TestHateosResource b = TestHateosResource.with(BigInteger.ONE);
        final TestHateosResource c = TestHateosResource.with(BigInteger.TEN);

        this.comparatorArraySortAndCheck(
                none,
                a,
                b,
                c,
                none,
                a,
                b,
                c
        );
    }

    @Test
    public void testSortMultipleNullIds() {
        final TestHateosResource none = TestHateosResource.with(null);
        final TestHateosResource a = TestHateosResource.with(BigInteger.ZERO);
        final TestHateosResource b = TestHateosResource.with(BigInteger.ONE);
        final TestHateosResource c = TestHateosResource.with(BigInteger.TEN);

        this.comparatorArraySortAndCheck(
                none,
                none,
                none,
                a,
                b,
                c,
                none,
                none,
                none,
                a,
                b,
                c
        );
    }

    @Test
    public void testSortReverse() {
        final TestHateosResource none = TestHateosResource.with(null);
        final TestHateosResource a = TestHateosResource.with(BigInteger.ZERO);
        final TestHateosResource b = TestHateosResource.with(BigInteger.ONE);
        final TestHateosResource c = TestHateosResource.with(BigInteger.TEN);

        this.comparatorArraySortAndCheck(
                c,
                b,
                a,
                none,
                none,
                a,
                b,
                c
        );
    }

    @Test
    public void testSetWithMultipleNullIds() {
        final TestHateosResource none = TestHateosResource.with(null);
        final TestHateosResource a = TestHateosResource.with(BigInteger.ZERO);
        final TestHateosResource b = TestHateosResource.with(BigInteger.ONE);
        final TestHateosResource c = TestHateosResource.with(BigInteger.TEN);

        final Set<TestHateosResource> sorted = SortedSets.tree(HateosResourceIdComparator.instance());
        sorted.add(none);
        sorted.add(a);
        sorted.add(none);
        sorted.add(b);
        sorted.add(none);
        sorted.add(c);

        this.checkEquals(
                Lists.of(
                        none,
                        a,
                        b,
                        c
                ),
                new ArrayList<>(sorted)
        );
    }

    @Override
    public HateosResourceIdComparator<TestHateosResource, BigInteger> createComparator() {
        return HateosResourceIdComparator.instance();
    }

    @Override
    public Class<HateosResourceIdComparator<TestHateosResource, BigInteger>> type() {
        return Cast.to(HateosResourceIdComparator.class);
    }
}
