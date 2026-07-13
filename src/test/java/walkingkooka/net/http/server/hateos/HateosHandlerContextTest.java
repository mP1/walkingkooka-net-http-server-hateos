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
import walkingkooka.HasCharset;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.BinaryTextContext;
import walkingkooka.text.TextContext;

public final class HateosHandlerContextTest implements ClassTesting<HateosHandlerContext> {

    @Test
    public void testDoesNotImplementsHasCharset() {
        this.checkEquals(
            false,
            new FakeHateosHandlerContext() instanceof HasCharset
        );
    }

    @Test
    public void testDoesNotImplementsBinaryTextContext() {
        this.checkEquals(
            false,
            new FakeHateosHandlerContext() instanceof BinaryTextContext
        );
    }

    @Test
    public void testImplementsTextContext() {
        this.checkEquals(
            true,
            new FakeHateosHandlerContext() instanceof TextContext
        );
    }

    // class............................................................................................................

    @Override
    public Class<HateosHandlerContext> type() {
        return HateosHandlerContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }
}
