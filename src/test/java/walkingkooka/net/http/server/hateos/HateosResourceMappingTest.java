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

public final class HateosResourceMappingTest extends HateosResourceMappingTestCase<HateosResourceMapping<BigInteger, TestResource, TestResource2, TestHateosResource>>
        implements ToStringTesting<HateosResourceMapping<BigInteger, TestResource, TestResource2, TestHateosResource>> {

    @Test
    public void testWithNullResourceNameFails() {
        this.withFails(null,
                this.stringToId(),
                this.valueType(),
                this.collectionType(),
                this.resourceType());
    }

    @Test
    public void testWithNullStringToIdFails() {
        this.withFails(this.resourceName(),
                null,
                this.valueType(),
                this.collectionType(),
                this.resourceType());
    }

    @Test
    public void testWithNullValueTypeFails() {
        this.withFails(this.resourceName(),
                this.stringToId(),
                null,
                this.collectionType(),
                this.resourceType());
    }

    @Test
    public void testWithNullCollectionTypeFails() {
        this.withFails(this.resourceName(),
                this.stringToId(),
                this.valueType(),
                null,
                this.resourceType());
    }

    @Test
    public void testWithNullResourceTypeFails() {
        this.withFails(this.resourceName(),
                this.stringToId(),
                this.valueType(),
                this.collectionType(),
                null);
    }

    private void withFails(
            final HateosResourceName resourceName,
            final Function<String, BigInteger> stringToId,
            final Class<TestResource> valueType,
            final Class<TestResource2> collectionType,
            final Class<TestHateosResource> resourceType) {
        assertThrows(NullPointerException.class, () -> HateosResourceMapping.with(resourceName,
                stringToId,
                valueType,
                collectionType,
                resourceType));
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
                          final HateosHandler<BigInteger, TestResource, TestResource2> handler) {

        assertThrows(NullPointerException.class, () ->
                this.createMapping().set(relation, method, handler));
    }

    @Test
    public void testSetSame() {
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();
        final HateosHandler<BigInteger, TestResource, TestResource2> handler = this.handler();

        final HateosResourceMapping<BigInteger, TestResource, TestResource2, TestHateosResource> mapping = this.createMapping()
                .set(relation, method, handler);
        assertSame(mapping, mapping.set(relation, method, handler));
    }

    @Test
    public void testSetDifferentRelation() {
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();
        final HateosHandler<BigInteger, TestResource, TestResource2> handler = this.handler();

        final HateosResourceMapping<BigInteger, TestResource, TestResource2, TestHateosResource> mapping = this.createMapping()
                .set(relation, method, handler);

        final LinkRelation<?> differentRelation = LinkRelation.with("different");
        final HateosResourceMapping<BigInteger, TestResource, TestResource2, TestHateosResource> different = mapping.set(differentRelation, method, handler);
        assertNotSame(mapping, different);

        this.check(mapping, Maps.of(HateosResourceMappingLinkRelationHttpMethod.with(relation, method), handler));
        this.check(different, Maps.of(HateosResourceMappingLinkRelationHttpMethod.with(relation, method), handler,
                HateosResourceMappingLinkRelationHttpMethod.with(differentRelation, method), handler));
    }

    @Test
    public void testSetDifferentMethod() {
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();
        final HateosHandler<BigInteger, TestResource, TestResource2> handler = this.handler();

        final HateosResourceMapping<BigInteger, TestResource, TestResource2, TestHateosResource> mapping = this.createMapping()
                .set(relation, method, handler);

        final HttpMethod differentMethod = HttpMethod.with("different");
        final HateosResourceMapping<BigInteger, TestResource, TestResource2, TestHateosResource> different = mapping.set(relation, differentMethod, handler);
        assertNotSame(mapping, different);

        this.check(mapping, Maps.of(HateosResourceMappingLinkRelationHttpMethod.with(relation, method), handler));
        this.check(different, Maps.of(HateosResourceMappingLinkRelationHttpMethod.with(relation, method), handler,
                HateosResourceMappingLinkRelationHttpMethod.with(relation, differentMethod), handler));
    }

    @Test
    public void testSetDifferentHandler() {
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();
        final HateosHandler<BigInteger, TestResource, TestResource2> handler = this.handler();

        final HateosResourceMapping<BigInteger, TestResource, TestResource2, TestHateosResource> mapping = this.createMapping()
                .set(relation, method, handler);

        final HateosHandler<BigInteger, TestResource, TestResource2> differentHandler = new FakeHateosHandler<>();
        final HateosResourceMapping<BigInteger, TestResource, TestResource2, TestHateosResource> different = mapping.set(relation, method, differentHandler);
        assertNotSame(mapping, different);

        this.check(mapping, Maps.of(HateosResourceMappingLinkRelationHttpMethod.with(relation, method), handler));
        this.check(different, Maps.of(HateosResourceMappingLinkRelationHttpMethod.with(relation, method), differentHandler));
    }

    @Test
    public void testSetMultipleHandlers() {
        final LinkRelation<?> relation1 = this.relation();
        final HttpMethod method1 = this.method();
        final HateosHandler<BigInteger, TestResource, TestResource2> handler1 = this.handler();

        final HateosResourceMapping<BigInteger, TestResource, TestResource2, TestHateosResource> mapping = this.createMapping()
                .set(relation1, method1, handler1);

        final LinkRelation<?> relation2 = LinkRelation.with("relation2");
        final HttpMethod method2 = HttpMethod.with("HTTPMETHODB");
        final HateosHandler<BigInteger, TestResource, TestResource2> handler2 = new FakeHateosHandler<>();
        final HateosResourceMapping<BigInteger, TestResource, TestResource2, TestHateosResource> mapping2 = mapping.set(relation2,
                method2,
                handler2);
        assertNotSame(mapping, mapping2);

        final LinkRelation<?> relation3 = LinkRelation.with("relation3");
        final HttpMethod method3 = HttpMethod.with("HTTPMETHODC");
        final HateosHandler<BigInteger, TestResource, TestResource2> handler3 = new FakeHateosHandler<>();
        final HateosResourceMapping<BigInteger, TestResource, TestResource2, TestHateosResource> mapping3 = mapping2.set(relation3,
                method3,
                handler3);
        assertNotSame(mapping, mapping3);

        this.check(mapping3, Maps.of(HateosResourceMappingLinkRelationHttpMethod.with(relation1, method1), handler1,
                HateosResourceMappingLinkRelationHttpMethod.with(relation2, method2), handler2,
                HateosResourceMappingLinkRelationHttpMethod.with(relation3, method3), handler3));
    }

    private void check(final HateosResourceMapping<BigInteger, TestResource, TestResource2, TestHateosResource> mapping,
                       final Map<HateosResourceMappingLinkRelationHttpMethod, HateosHandler<BigInteger, TestResource, TestResource2>> relationAndMethodToHandler) {
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
        final HateosResourceMapping<BigInteger, TestResource, TestResource2, TestHateosResource> mapping = this.createMapping();
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();
        final HateosHandler<BigInteger, TestResource, TestResource2> handler = this.handler();

        this.handlerAndCheck(mapping.set(relation, method, handler),
                relation,
                HttpMethod.with("unknown"),
                Optional.empty());
    }

    @Test
    public void testHandlerUnknown2() {
        final HateosResourceMapping<BigInteger, TestResource, TestResource2, TestHateosResource> mapping = this.createMapping();
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();
        final HateosHandler<BigInteger, TestResource, TestResource2> handler = this.handler();

        this.handlerAndCheck(mapping.set(relation, method, handler),
                LinkRelation.with("unknown"),
                method,
                Optional.empty());
    }

    @Test
    public void testHandlerMatched() {
        final HateosResourceMapping<BigInteger, TestResource, TestResource2, TestHateosResource> mapping = this.createMapping();
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();
        final HateosHandler<BigInteger, TestResource, TestResource2> handler = this.handler();

        this.handlerAndCheck(mapping.set(relation, method, handler),
                relation,
                method,
                Optional.of(handler));
    }

    private void handlerAndCheck(final HateosResourceMapping<BigInteger, TestResource, TestResource2, TestHateosResource> mapping,
                                 final LinkRelation relation,
                                 final HttpMethod method,
                                 final Optional<HateosHandler<BigInteger, TestResource, TestResource2>> handler) {
        assertEquals(handler,
                mapping.handler(relation, method),
                () -> "handler " + relation + " " + method);
    }

    // toString..........................................................................................................

    @Test
    public void testToString() {
        final HateosResourceMapping<BigInteger, TestResource, TestResource2, TestHateosResource> mapping = this.createMapping();
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();
        final HateosHandler<BigInteger, TestResource, TestResource2> handler = new FakeHateosHandler<>() {

            @Override
            public String toString() {
                return "Handler123";
            }
        };

        this.toStringAndCheck(mapping.set(relation, method, handler),
                "abc123 \"walkingkooka.net.http.server.hateos.TestHateosResource\" item POST=Handler123");
    }

    // helpers..........................................................................................................

    private HateosResourceMapping<BigInteger, TestResource, TestResource2, TestHateosResource> createMapping() {
        return HateosResourceMapping.with(this.resourceName(),
                this.stringToId(),
                this.valueType(),
                this.collectionType(),
                this.resourceType());
    }

    private HateosResourceName resourceName() {
        return HateosResourceName.with("abc123");
    }

    private Function<String, BigInteger> stringToId() {
        return BigInteger::new;
    }

    private Class<TestResource> valueType() {
        return TestResource.class;
    }

    private Class<TestResource2> collectionType() {
        return TestResource2.class;
    }

    private Class<TestHateosResource> resourceType() {
        return TestHateosResource.class;
    }

    private LinkRelation relation() {
        return LinkRelation.ITEM;
    }

    private HttpMethod method() {
        return HttpMethod.POST;
    }

    private HateosHandler<BigInteger, TestResource, TestResource2> handler() {
        return new FakeHateosHandler<>();
    }

    // ClassVisibility..................................................................................................

    @Override
    public Class<HateosResourceMapping<BigInteger, TestResource, TestResource2, TestHateosResource>> type() {
        return Cast.to(HateosResourceMapping.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return HateosResourceMapping.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return "";
    }
}
