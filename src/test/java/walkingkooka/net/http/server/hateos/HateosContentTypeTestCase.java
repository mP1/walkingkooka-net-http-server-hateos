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
import walkingkooka.net.header.MediaType;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.ToStringTesting;
import walkingkooka.test.TypeNameTesting;
import walkingkooka.type.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class HateosContentTypeTestCase<C extends HateosContentType> implements ClassTesting2<C>,
        ToStringTesting<C>,
        TypeNameTesting<C> {

    HateosContentTypeTestCase() {
        super();
    }

    @Test
    public final void testContentType() {
        assertEquals(this.contentType(), this.hateosContentType().contentType());
    }

    abstract MediaType contentType();

    @Test
    public final void testToString() {
        this.toStringAndCheck(this.hateosContentType(), this.expectedToString());
    }

    abstract String expectedToString();

    // helpers..........................................................................................................

    final void fromNodeAndCheck(final String text,
                                final Class<TestHateosResource> resourceType,
                                final TestHateosResource resource) {
        assertEquals(resource,
                this.hateosContentType()
                        .fromNode(text, resourceType),
                () -> "fromNode failed: " + text);
    }

    final void toTextAndCheck(final HateosResource<?> resource,
                              final String text) {
        assertEquals(text,
                this.hateosContentType().toText(resource),
                () -> "toText failed: " + resource);
    }

    abstract C hateosContentType();

    // ClassTesting.....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // TypeNameTesting .................................................................................................

    @Override
    public final String typeNamePrefix() {
        return HateosContentType.class.getSimpleName();
    }

    @Override
    public final String typeNameSuffix() {
        return "";
    }
}
