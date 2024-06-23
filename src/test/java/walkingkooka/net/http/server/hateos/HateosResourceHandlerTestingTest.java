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
import walkingkooka.collect.set.Sets;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.reflect.JavaVisibility;

import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class HateosResourceHandlerTestingTest implements HateosResourceHandlerTesting<FakeHateosResourceHandler<BigInteger, TestHateosResource, TestHateosResource2>,
        BigInteger,
        TestHateosResource,
        TestHateosResource2> {

    @Override
    public void testTestNaming() {
    }

    // handleAll....................................................................................................

    @Test
    public void testHandleAllAndCheck() {
        final Optional<TestHateosResource2> in = this.collectionResource();
        final Map<HttpRequestAttribute<?>, Object> parameters = this.parameters();

        final TestHateosResource2 out = TestHateosResource2.with(BigInteger.ONE);

        this.handleAllAndCheck(
                new FakeHateosResourceHandler<>() {

                    @Override
                    public Optional<TestHateosResource2> handleAll(final Optional<TestHateosResource2> r,
                                                                   final Map<HttpRequestAttribute<?>, Object> p) {
                        assertSame(in, r);
                        assertSame(parameters, p);

                        return Optional.of(out);
                    }
                },
                in,
                parameters,
                Optional.of(out));
    }

    // handleMany.......................................................................................................

    @Test
    public void testHandleManyAndCheck() {
        final Set<BigInteger> ids = this.manyIds();
        final Optional<TestHateosResource2> in = this.collectionResource();
        final Map<HttpRequestAttribute<?>, Object> parameters = this.parameters();

        final TestHateosResource2 out = TestHateosResource2.with(BigInteger.ONE);

        this.handleManyAndCheck(
                new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestHateosResource2> handleMany(final Set<BigInteger> i,
                                                                    final Optional<TestHateosResource2> r,
                                                                    final Map<HttpRequestAttribute<?>, Object> p) {
                        assertSame(ids, i);
                        assertSame(in, r);
                        assertSame(parameters, p);

                        return Optional.of(out);
                    }
                },
                ids,
                in,
                parameters,
                Optional.of(out));
    }

    // handleNone....................................................................................................

    @Test
    public void testHandleNoneAndCheck() {
        final Optional<TestHateosResource> in = this.resource();
        final Map<HttpRequestAttribute<?>, Object> parameters = this.parameters();

        final TestHateosResource out = TestHateosResource.with(BigInteger.ONE);

        this.handleNoneAndCheck(
                new FakeHateosResourceHandler<>() {

                    @Override
                    public Optional<TestHateosResource> handleNone(final Optional<TestHateosResource> r,
                                                                   final Map<HttpRequestAttribute<?>, Object> p) {
                        assertSame(in, r);
                        assertSame(parameters, p);

                        return Optional.of(out);
                    }
                },
                in,
                parameters,
                Optional.of(out));
    }

    // handleOne.......................................................................................................

    @Test
    public void testHandleOneAndCheck() {
        final BigInteger id = this.id();
        final Optional<TestHateosResource> in = this.resource();
        final Map<HttpRequestAttribute<?>, Object> parameters = this.parameters();

        final TestHateosResource out = TestHateosResource.with(BigInteger.ONE);

        this.handleOneAndCheck(
                new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestHateosResource> handleOne(final BigInteger i,
                                                                  final Optional<TestHateosResource> r,
                                                                  final Map<HttpRequestAttribute<?>, Object> p) {
                        assertSame(id, i);
                        assertSame(in, r);
                        assertSame(parameters, p);

                        return Optional.of(out);
                    }
                },
                id,
                in,
                parameters,
                Optional.of(out));
    }

    // handleRange.......................................................................................................

    @Test
    public void testHandleRangeAndCheck() {
        final Range<BigInteger> range = this.range();
        final Optional<TestHateosResource2> in = this.collectionResource();
        final Map<HttpRequestAttribute<?>, Object> parameters = this.parameters();

        final TestHateosResource2 out = TestHateosResource2.with(BigInteger.ONE);

        this.handleRangeAndCheck(
                new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestHateosResource2> handleRange(final Range<BigInteger> rr,
                                                                     final Optional<TestHateosResource2> r,
                                                                     final Map<HttpRequestAttribute<?>, Object> p) {
                        assertSame(range, rr);
                        assertSame(in, r);
                        assertSame(parameters, p);

                        return Optional.of(out);
                    }
                },
                range,
                in,
                parameters,
                Optional.of(out));
    }

    // helpers..........................................................................................................

    @Override
    public FakeHateosResourceHandler<BigInteger, TestHateosResource, TestHateosResource2> createHandler() {
        return new FakeHateosResourceHandler<>();
    }

    @Override
    public BigInteger id() {
        return BigInteger.valueOf(111);
    }

    @Override
    public Set<BigInteger> manyIds() {
        return Sets.of(
                BigInteger.ONE,
                BigInteger.valueOf(22)
        );
    }

    @Override
    public Range<BigInteger> range() {
        return Range.greaterThanEquals(BigInteger.valueOf(111))
                .and(Range.lessThanEquals(BigInteger.valueOf(222)));
    }

    @Override
    public Optional<TestHateosResource> resource() {
        return Optional.of(TestHateosResource.with(BigInteger.valueOf(999)));
    }

    @Override
    public Optional<TestHateosResource2> collectionResource() {
        return Optional.of(TestHateosResource2.with(BigInteger.valueOf(999)));
    }

    @Override
    public Class<FakeHateosResourceHandler<BigInteger, TestHateosResource, TestHateosResource2>> type() {
        return Cast.to(FakeHateosResourceHandler.class);
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosResourceHandler.NO_PARAMETERS;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public String typeNamePrefix() {
        return "";
    }
}