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
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.test.ToStringTesting;
import walkingkooka.type.JavaVisibility;

import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HateosHandlerResourceMappingTest extends HateosHandlerResourceMappingTestCase<HateosHandlerResourceMapping<BigInteger, TestHateosResource, TestHateosResource2>>
        implements ToStringTesting<HateosHandlerResourceMapping<BigInteger, TestHateosResource, TestHateosResource2>> {

    @Test
    public void testWithNullResourceNameFails() {
        this.withFails(null,
                this.stringToId(),
                this.resourceType(),
                this.collectionResourceType());
    }

    @Test
    public void testWithNullStringToIdFails() {
        this.withFails(this.resourceName(),
                null,
                this.resourceType(),
                this.collectionResourceType());
    }

    @Test
    public void testWithNullResourceTypeFails() {
        this.withFails(this.resourceName(),
                this.stringToId(),
                null,
                this.collectionResourceType());
    }

    @Test
    public void testWithNullCollectionResourceTypeFails() {
        this.withFails(this.resourceName(),
                this.stringToId(),
                this.resourceType(),
                null);
    }

    private void withFails(
            final HateosResourceName resourceName,
            final Function<String, BigInteger> stringToId,
            final Class<TestHateosResource> resourceType,
            final Class<TestHateosResource2> collectionResourceType) {
        assertThrows(NullPointerException.class, () -> HateosHandlerResourceMapping.with(resourceName,
                stringToId,
                resourceType,
                collectionResourceType));
    }

    // set..............................................................................................................

    @Test
    public void testSetNullLinkRelationFails() {
        this.setFails(null,
                this.method(),
                this.handler());
    }

    @Test
    public void testSetNullMethodFails() {
        this.setFails(this.relation(),
                null,
                this.handler());
    }

    @Test
    public void testSetNullHandlerFails() {
        this.setFails(this.relation(),
                this.method(),
                null);
    }

    private void setFails(final LinkRelation relation,
                          final HttpMethod method,
                          final HateosHandler<BigInteger, TestHateosResource, TestHateosResource2> handler) {

        assertThrows(NullPointerException.class, () ->
                this.createMapping().set(relation, method, handler));
    }

    @Test
    public void testSetSame() {
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();
        final HateosHandler<BigInteger, TestHateosResource, TestHateosResource2> handler = this.handler();

        final HateosHandlerResourceMapping<BigInteger, TestHateosResource, TestHateosResource2> mapping = this.createMapping()
                .set(relation, method, handler);
        assertSame(mapping, mapping.set(relation, method, handler));
    }

    @Test
    public void testSetDifferentRelation() {
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();
        final HateosHandler<BigInteger, TestHateosResource, TestHateosResource2> handler = this.handler();

        final HateosHandlerResourceMapping<BigInteger, TestHateosResource, TestHateosResource2> mapping = this.createMapping()
                .set(relation, method, handler);

        final LinkRelation<?> differentRelation = LinkRelation.with("different");
        final HateosHandlerResourceMapping<BigInteger, TestHateosResource, TestHateosResource2> different = mapping.set(differentRelation, method, handler);
        assertNotSame(mapping, different);

        this.check(mapping, Maps.of(HateosHandlerResourceMappingLinkRelationHttpMethod.with(relation, method), handler));
        this.check(different, Maps.of(HateosHandlerResourceMappingLinkRelationHttpMethod.with(relation, method), handler,
                HateosHandlerResourceMappingLinkRelationHttpMethod.with(differentRelation, method), handler));
    }

    @Test
    public void testSetDifferentMethod() {
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();
        final HateosHandler<BigInteger, TestHateosResource, TestHateosResource2> handler = this.handler();

        final HateosHandlerResourceMapping<BigInteger, TestHateosResource, TestHateosResource2> mapping = this.createMapping()
                .set(relation, method, handler);

        final HttpMethod differentMethod = HttpMethod.with("different");
        final HateosHandlerResourceMapping<BigInteger, TestHateosResource, TestHateosResource2> different = mapping.set(relation, differentMethod, handler);
        assertNotSame(mapping, different);

        this.check(mapping, Maps.of(HateosHandlerResourceMappingLinkRelationHttpMethod.with(relation, method), handler));
        this.check(different, Maps.of(HateosHandlerResourceMappingLinkRelationHttpMethod.with(relation, method), handler,
                HateosHandlerResourceMappingLinkRelationHttpMethod.with(relation, differentMethod), handler));
    }

    @Test
    public void testSetDifferentHandler() {
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();
        final HateosHandler<BigInteger, TestHateosResource, TestHateosResource2> handler = this.handler();

        final HateosHandlerResourceMapping<BigInteger, TestHateosResource, TestHateosResource2> mapping = this.createMapping()
                .set(relation, method, handler);

        final HateosHandler<BigInteger, TestHateosResource, TestHateosResource2> differentHandler = new FakeHateosHandler<>();
        final HateosHandlerResourceMapping<BigInteger, TestHateosResource, TestHateosResource2> different = mapping.set(relation, method, differentHandler);
        assertNotSame(mapping, different);

        this.check(mapping, Maps.of(HateosHandlerResourceMappingLinkRelationHttpMethod.with(relation, method), handler));
        this.check(different, Maps.of(HateosHandlerResourceMappingLinkRelationHttpMethod.with(relation, method), differentHandler));
    }

    @Test
    public void testSetMultipleHandlers() {
        final LinkRelation<?> relation1 = this.relation();
        final HttpMethod method1 = this.method();
        final HateosHandler<BigInteger, TestHateosResource, TestHateosResource2> handler1 = this.handler();

        final HateosHandlerResourceMapping<BigInteger, TestHateosResource, TestHateosResource2> mapping = this.createMapping()
                .set(relation1, method1, handler1);

        final LinkRelation<?> relation2 = LinkRelation.with("relation2");
        final HttpMethod method2 = HttpMethod.with("HTTPMETHODB");
        final HateosHandler<BigInteger, TestHateosResource, TestHateosResource2> handler2 = new FakeHateosHandler<>();
        final HateosHandlerResourceMapping<BigInteger, TestHateosResource, TestHateosResource2> mapping2 = mapping.set(relation2,
                method2,
                handler2);
        assertNotSame(mapping, mapping2);

        final LinkRelation<?> relation3 = LinkRelation.with("relation3");
        final HttpMethod method3 = HttpMethod.with("HTTPMETHODC");
        final HateosHandler<BigInteger, TestHateosResource, TestHateosResource2> handler3 = new FakeHateosHandler<>();
        final HateosHandlerResourceMapping<BigInteger, TestHateosResource, TestHateosResource2> mapping3 = mapping2.set(relation3,
                method3,
                handler3);
        assertNotSame(mapping, mapping3);

        this.check(mapping3, Maps.of(HateosHandlerResourceMappingLinkRelationHttpMethod.with(relation1, method1), handler1,
                HateosHandlerResourceMappingLinkRelationHttpMethod.with(relation2, method2), handler2,
                HateosHandlerResourceMappingLinkRelationHttpMethod.with(relation3, method3), handler3));
    }

    private void check(final HateosHandlerResourceMapping<BigInteger, TestHateosResource, TestHateosResource2> mapping,
                       final Map<HateosHandlerResourceMappingLinkRelationHttpMethod, HateosHandler<BigInteger, TestHateosResource, TestHateosResource2>> relationAndMethodToHandler) {
        assertEquals(relationAndMethodToHandler,
                mapping.relationAndMethodToHandlers,
                "relationAndMethodToHandlers");
    }

    // handler..........................................................................................................

    @Test
    public void testHandlerNullLinkRelationFails() {
        this.handlerFails(null,
                this.method());
    }

    @Test
    public void testHandlerNullMethodFails() {
        this.handlerFails(this.relation(),
                null);
    }

    private void handlerFails(final LinkRelation relation,
                              final HttpMethod method) {

        assertThrows(NullPointerException.class, () ->
                this.createMapping().handler(relation, method));
    }

    @Test
    public void testHandlerUnknown() {
        final HateosHandlerResourceMapping<BigInteger, TestHateosResource, TestHateosResource2> mapping = this.createMapping();
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();
        final HateosHandler<BigInteger, TestHateosResource, TestHateosResource2> handler = this.handler();

        this.handlerAndCheck(mapping.set(relation, method, handler),
                relation,
                HttpMethod.with("unknown"),
                Optional.empty());
    }

    @Test
    public void testHandlerUnknown2() {
        final HateosHandlerResourceMapping<BigInteger, TestHateosResource, TestHateosResource2> mapping = this.createMapping();
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();
        final HateosHandler<BigInteger, TestHateosResource, TestHateosResource2> handler = this.handler();

        this.handlerAndCheck(mapping.set(relation, method, handler),
                LinkRelation.with("unknown"),
                method,
                Optional.empty());
    }

    @Test
    public void testHandlerMatched() {
        final HateosHandlerResourceMapping<BigInteger, TestHateosResource, TestHateosResource2> mapping = this.createMapping();
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();
        final HateosHandler<BigInteger, TestHateosResource, TestHateosResource2> handler = this.handler();

        this.handlerAndCheck(mapping.set(relation, method, handler),
                relation,
                method,
                Optional.of(handler));
    }

    private void handlerAndCheck(final HateosHandlerResourceMapping<BigInteger, TestHateosResource, TestHateosResource2> mapping,
                                 final LinkRelation relation,
                                 final HttpMethod method,
                                 final Optional<HateosHandler<BigInteger, TestHateosResource, TestHateosResource2>> handler) {
        assertEquals(handler,
                mapping.handler(relation, method),
                () -> "handler " + relation + " " + method);
    }

    // toString..........................................................................................................

    @Test
    public void testToString() {
        final HateosHandlerResourceMapping<BigInteger, TestHateosResource, TestHateosResource2> mapping = this.createMapping();
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();
        final HateosHandler<BigInteger, TestHateosResource, TestHateosResource2> handler = new FakeHateosHandler<>() {

            @Override
            public String toString() {
                return "Handler123";
            }
        };

        this.toStringAndCheck(mapping.set(relation, method, handler),
                "abc123 \"walkingkooka.net.http.server.hateos.TestHateosResource\" \"walkingkooka.net.http.server.hateos.TestHateosResource2\" item POST=Handler123");
    }

    // helpers..........................................................................................................

    private HateosHandlerResourceMapping<BigInteger, TestHateosResource, TestHateosResource2> createMapping() {
        return HateosHandlerResourceMapping.with(this.resourceName(),
                this.stringToId(),
                this.resourceType(),
                this.collectionResourceType());
    }

    private HateosResourceName resourceName() {
        return HateosResourceName.with("abc123");
    }

    private Function<String, BigInteger> stringToId() {
        return BigInteger::new;
    }

    private Class<TestHateosResource> resourceType() {
        return TestHateosResource.class;
    }

    private Class<TestHateosResource2> collectionResourceType() {
        return TestHateosResource2.class;
    }

    private LinkRelation relation() {
        return LinkRelation.ITEM;
    }

    private HttpMethod method() {
        return HttpMethod.POST;
    }

    private HateosHandler<BigInteger, TestHateosResource, TestHateosResource2> handler() {
        return new FakeHateosHandler<>();
    }

    // ClassVisibility..................................................................................................

    @Override
    public Class<HateosHandlerResourceMapping<BigInteger, TestHateosResource, TestHateosResource2>> type() {
        return Cast.to(HateosHandlerResourceMapping.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return HateosHandlerResourceMapping.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return "";
    }
}
