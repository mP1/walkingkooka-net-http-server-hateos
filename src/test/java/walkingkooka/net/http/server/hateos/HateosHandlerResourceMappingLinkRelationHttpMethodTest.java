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
import walkingkooka.compare.ComparableTesting;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.http.HttpMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HateosHandlerResourceMappingLinkRelationHttpMethodTest extends HateosHandlerResourceMappingTestCase2<HateosHandlerResourceMappingLinkRelationHttpMethod>
        implements ComparableTesting<HateosHandlerResourceMappingLinkRelationHttpMethod> {

    @Test
    public void testWithNullRelationFails() {
        this.withFails(null, this.method());
    }

    @Test
    public void testWithNullMethodFails() {
        this.withFails(this.relation(), null);
    }

    private void withFails(final LinkRelation<?> relation,
                           final HttpMethod method) {
        assertThrows(NullPointerException.class, () -> {
            HateosHandlerResourceMappingLinkRelationHttpMethod.with(relation, method);
        });
    }

    @Test
    public void testDifferentLinkRelation() {
        this.checkNotEquals(HateosHandlerResourceMappingLinkRelationHttpMethod.with(LinkRelation.with("different"), this.method()));
    }

    @Test
    public void testDifferentMethod() {
        this.checkNotEquals(HateosHandlerResourceMappingLinkRelationHttpMethod.with(this.relation(), HttpMethod.with("different")));
    }

    @Test
    public void testToString() {
        assertEquals("self GET", this.createComparable().toString());
    }

    @Override
    public HateosHandlerResourceMappingLinkRelationHttpMethod createComparable() {
        return HateosHandlerResourceMappingLinkRelationHttpMethod.with(this.relation(), this.method());
    }

    private LinkRelation<?> relation() {
        return LinkRelation.SELF;
    }

    private HttpMethod method() {
        return HttpMethod.GET;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<HateosHandlerResourceMappingLinkRelationHttpMethod> type() {
        return HateosHandlerResourceMappingLinkRelationHttpMethod.class;
    }

    // TypeNameTesting...................................................................................................

    @Override
    public String typeNamePrefix() {
        return HateosHandlerResourceMapping.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return LinkRelation.class.getSimpleName() + HttpMethod.class.getSimpleName();
    }
}
