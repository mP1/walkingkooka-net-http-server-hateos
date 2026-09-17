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
import walkingkooka.reflect.PublicClassTesting;
import walkingkooka.test.ParseStringTesting;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HateosResourceSelectionTest implements ParseStringTesting<HateosResourceSelection<BigInteger>>,
    PublicClassTesting<HateosResourceSelection<BigInteger>> {

    // parse............................................................................................................

    @Test
    public void testParseWithNullNameFactoryFails() {
        assertThrows(
            NullPointerException.class,
            () -> HateosResourceSelection.parseNoneOneOrAll(
                "",
                null
            )
        );
    }

    @Test
    public void testParseWithInvalidFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.parseString("abc")
        );
    }

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseWithNone() {
        this.parseStringAndCheck(
            "",
            HateosResourceSelection.none()
        );
    }

    @Test
    public void testParseWithOne() {
        this.parseStringAndCheck(
            "123",
            HateosResourceSelection.one(
                new BigInteger("123")
            )
        );
    }

    @Test
    public void testParseWithAll() {
        this.parseStringAndCheck(
            "*",
            HateosResourceSelection.all()
        );
    }

    @Override
    public HateosResourceSelection<BigInteger> parseString(final String text) {
        return HateosResourceSelection.parseNoneOneOrAll(
            text,
            BigInteger::new
        );
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        return expected;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return expected;
    }

    // class............................................................................................................

    @Override
    public Class<HateosResourceSelection<BigInteger>> type() {
        return Cast.to(HateosResourceSelection.class);
    }
}
