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
import walkingkooka.collect.Range;
import walkingkooka.collect.RangeVisitorTesting;
import walkingkooka.reflect.JavaVisibility;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HateosResourceIdRangeVisitorTest implements RangeVisitorTesting<HateosResourceIdRangeVisitor<BigInteger>, BigInteger> {

    @Test
    public void testNullRangeFails() {
        assertThrows(NullPointerException.class, () -> {
            HateosResourceIdRangeVisitor.hateosLinkId(null, HateosResourceIdRangeVisitorTest::idForHateosLink);
        });
    }

    @Test
    public void testNullIdForHateosLinkFunctionFails() {
        assertThrows(NullPointerException.class, () -> {
            HateosResourceIdRangeVisitor.hateosLinkId(Range.singleton(BigInteger.valueOf(123)), null);
        });
    }

    @Test
    public void testAll() {
        this.idForHateosLinkAndCheck(Range.all(), "*");
    }

    @Test
    public void testSingleton() {
        this.idForHateosLinkAndCheck(Range.singleton(BigInteger.valueOf(0x1f)), "1f");
    }

    @Test
    public void testGreaterThanFails() {
        this.idForHateosLinkFails(Range.greaterThan(BigInteger.valueOf(0x1f)));
    }

    @Test
    public void testGreaterThanEquals() {
        this.idForHateosLinkAndCheck(Range.greaterThanEquals(BigInteger.valueOf(0x1f)), "1f-");
    }

    @Test
    public void testLessThanFails() {
        this.idForHateosLinkFails(Range.lessThan(BigInteger.valueOf(0x1f)));
    }

    @Test
    public void testLessThanEquals() {
        this.idForHateosLinkAndCheck(Range.lessThanEquals(BigInteger.valueOf(0x1f)), "-1f");
    }

    @Test
    public void testBetweenInclusive() {
        this.idForHateosLinkAndCheck(Range.greaterThanEquals(BigInteger.valueOf(0x1f)).and(Range.lessThanEquals(BigInteger.valueOf(0x2f))), "1f-2f");
    }

    private void idForHateosLinkAndCheck(final Range<BigInteger> range, final String text) {
        this.checkEquals(text,
                HateosResourceIdRangeVisitor.hateosLinkId(range, HateosResourceIdRangeVisitorTest::idForHateosLink),
                () -> "" + range);
    }

    private void idForHateosLinkFails(final Range<BigInteger> range) {
        assertThrows(UnsupportedOperationException.class, () -> {
            HateosResourceIdRangeVisitor.hateosLinkId(range, HateosResourceIdRangeVisitorTest::idForHateosLink);
        });
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createVisitor(), "");
    }

    @Test
    public void testToString2() {
        final HateosResourceIdRangeVisitor<BigInteger> visitor = this.createVisitor();
        visitor.accept(Range.greaterThanEquals(BigInteger.valueOf(0x12f)));
        this.toStringAndCheck(visitor, "\"12f-\"");
    }

    @Override
    public HateosResourceIdRangeVisitor<BigInteger> createVisitor() {
        return new HateosResourceIdRangeVisitor<>(HateosResourceIdRangeVisitorTest::idForHateosLink);
    }

    private static String idForHateosLink(final BigInteger value) {
        return value.toString(16);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public Class<HateosResourceIdRangeVisitor<BigInteger>> type() {
        return Cast.to(HateosResourceIdRangeVisitor.class);
    }

    @Override
    public String typeNamePrefix() {
        return HateosResource.class.getSimpleName();
    }
}
