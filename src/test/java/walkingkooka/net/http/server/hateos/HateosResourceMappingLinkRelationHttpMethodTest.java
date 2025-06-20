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

public final class HateosResourceMappingLinkRelationHttpMethodTest extends HateosResourceMappingTestCase2<HateosResourceMappingLinkRelationHttpMethod>
        implements ComparableTesting2<HateosResourceMappingLinkRelationHttpMethod> {

    private final static LinkRelation<?> RELATION = LinkRelation.SELF;

    private final static HttpMethod METHOD = HttpMethod.GET;
    
    @Test
    public void testWithNullRelationFails() {
        assertThrows(
                NullPointerException.class,
                () -> HateosResourceMappingLinkRelationHttpMethod.with(
                        null,
                        METHOD
                )
        );
    }

    @Test
    public void testWithUrlRelationFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> HateosResourceMappingLinkRelationHttpMethod.with(
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
                () -> HateosResourceMappingLinkRelationHttpMethod.with(
                        RELATION,
                        null
                )
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentLinkRelation() {
        this.checkNotEquals(
                HateosResourceMappingLinkRelationHttpMethod.with(
                        LinkRelation.with("different"),
                        METHOD
                )
        );
    }

    @Test
    public void testEqualsDifferentMethod() {
        this.checkNotEquals(
                HateosResourceMappingLinkRelationHttpMethod.with(
                        RELATION,
                        HttpMethod.with("different")
                )
        );
    }

    @Override
    public HateosResourceMappingLinkRelationHttpMethod createComparable() {
        return HateosResourceMappingLinkRelationHttpMethod.with(
                RELATION,
                METHOD
        );
    }

    // Class............................................................................................................

    @Override
    public Class<HateosResourceMappingLinkRelationHttpMethod> type() {
        return HateosResourceMappingLinkRelationHttpMethod.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return HateosResourceMapping.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return LinkRelation.class.getSimpleName() +
                HttpMethod.class.getSimpleName();
    }
}
