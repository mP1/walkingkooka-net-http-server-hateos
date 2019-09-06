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
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.type.JavaVisibility;

import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class HateosHandlerTestingTest implements HateosHandlerTesting<FakeHateosHandler<BigInteger, TestHateosResource, TestHateosResource2>,
        BigInteger,
        TestHateosResource,
        TestHateosResource2> {

    @Override
    public final void testTestNaming() {
    }

    @Test
    public void testHandleIdAndCheck() {
        final Optional<BigInteger> id = this.id();
        final Optional<TestHateosResource> in = this.resource();
        final Map<HttpRequestAttribute<?>, Object> parameters = this.parameters();

        final TestHateosResource out = TestHateosResource.with(BigInteger.ONE);

        this.handleAndCheck(new FakeHateosHandler<>() {
                                @Override
                                public Optional<TestHateosResource> handle(final Optional<BigInteger> i,
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

    @Test
    public void testHandleUnsupported() {
        this.handleUnsupported(new FakeHateosHandler<>() {
        });
    }

    @Test
    public void testHandleCollectionAndCheck() {
        final Range<BigInteger> id = this.collection();
        final Optional<TestHateosResource2> in = Optional.of(TestHateosResource2.with(this.collection()));
        final Map<HttpRequestAttribute<?>, Object> parameters = this.parameters();

        final Optional<TestHateosResource2> out = Optional.of(TestHateosResource2.with(Range.singleton(BigInteger.valueOf(222))));

        this.handleCollectionAndCheck(new FakeHateosHandler<>() {
                                          @Override
                                          public Optional<TestHateosResource2> handleCollection(final Range<BigInteger> i,
                                                                                                final Optional<TestHateosResource2> r,
                                                                                                final Map<HttpRequestAttribute<?>, Object> p) {
                                              assertSame(id, i);
                                              assertSame(in, r);
                                              assertSame(parameters, p);

                                              return out;
                                          }
                                      },
                id,
                in,
                parameters,
                out);
    }

    @Test
    public void testHandleCollectionUnsupported() {
        this.handleCollectionUnsupported(new FakeHateosHandler<>() {
        });
    }

    @Override
    public FakeHateosHandler<BigInteger, TestHateosResource, TestHateosResource2> createHandler() {
        return new FakeHateosHandler<>();
    }

    @Override
    public Optional<BigInteger> id() {
        return Optional.of(BigInteger.valueOf(111));
    }

    @Override
    public Range<BigInteger> collection() {
        return Range.greaterThanEquals(BigInteger.valueOf(111))
                .and(Range.lessThanEquals(BigInteger.valueOf(222)));
    }

    @Override
    public Optional<TestHateosResource> resource() {
        return Optional.of(TestHateosResource.with(BigInteger.valueOf(999)));
    }

    @Override
    public Optional<TestHateosResource2> collectionResource() {
        return Optional.of(TestHateosResource2.with(this.collection()));
    }

    @Override
    public Class<FakeHateosHandler<BigInteger, TestHateosResource, TestHateosResource2>> type() {
        return Cast.to(FakeHateosHandler.class);
    }

    @Override
    public final Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public final String typeNamePrefix() {
        return "";
    }
}
