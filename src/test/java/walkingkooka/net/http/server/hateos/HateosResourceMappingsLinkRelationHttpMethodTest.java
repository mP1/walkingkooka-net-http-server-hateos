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
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.http.HttpMethod;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HateosResourceMappingsLinkRelationHttpMethodTest extends HateosResourceMappingsTestCase2<HateosResourceMappingsLinkRelationHttpMethod>
        implements ComparableTesting2<HateosResourceMappingsLinkRelationHttpMethod> {

    private final static LinkRelation<?> RELATION = LinkRelation.SELF;

    private final static HttpMethod METHOD = HttpMethod.GET;
    
    @Test
    public void testWithNullRelationFails() {
        assertThrows(
                NullPointerException.class,
                () -> HateosResourceMappingsLinkRelationHttpMethod.with(
                        null,
                        METHOD
                )
        );
    }

    @Test
    public void testWithUrlRelationFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> HateosResourceMappingsLinkRelationHttpMethod.with(
                        LinkRelation.parse("https://example.com")
                                .get(0),
                        HttpMethod.GET
                )
        );
    }

    @Test
    public void testWithNullMethodFails() {
        assertThrows(
                NullPointerException.class,
                () -> HateosResourceMappingsLinkRelationHttpMethod.with(
                        RELATION,
                        null
                )
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentLinkRelation() {
        this.checkNotEquals(
                HateosResourceMappingsLinkRelationHttpMethod.with(
                        LinkRelation.with("different"),
                        METHOD
                )
        );
    }

    @Test
    public void testEqualsDifferentMethod() {
        this.checkNotEquals(
                HateosResourceMappingsLinkRelationHttpMethod.with(
                        RELATION,
                        HttpMethod.with("different")
                )
        );
    }

    @Override
    public HateosResourceMappingsLinkRelationHttpMethod createComparable() {
        return HateosResourceMappingsLinkRelationHttpMethod.with(
                RELATION,
                METHOD
        );
    }

    // Class............................................................................................................

    @Override
    public Class<HateosResourceMappingsLinkRelationHttpMethod> type() {
        return HateosResourceMappingsLinkRelationHttpMethod.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return HateosResourceMappings.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return LinkRelation.class.getSimpleName() +
                HttpMethod.class.getSimpleName();
    }
}
