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
import walkingkooka.ToStringTesting;
import walkingkooka.collect.Range;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.server.hateos.HateosResourceMappingsTest.TestHateosResourceHandlerContext;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HateosResourceMappingsTest implements ClassTesting2<HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext>>,
        ToStringTesting<HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext>> {

    private final static HateosResourceName RESOURCE_NAME = HateosResourceName.with("abc123");

    private final static Class<TestResource> VALUE_TYPE = TestResource.class;

    private final static Class<TestResource2> COLLECTION_TYPE = TestResource2.class;

    private final static Class<TestHateosResource> RESOURCE_TYPE = TestHateosResource.class;

    private final static HateosResourceHandler<BigInteger, TestResource, TestResource2, TestHateosResourceHandlerContext> HATEOS_RESOURCE_HANDLER = new FakeHateosResourceHandler<>();

    private static final LinkRelation<String> LINK_RELATION = LinkRelation.ITEM;

    private static final HttpMethod METHOD = HttpMethod.POST;

    static class TestHateosResourceHandlerContext extends FakeHateosResourceHandlerContext {
    }

    @Test
    public void testWithNullResourceNameFails() {
        this.withFails(
                null,
                this.selection(),
                VALUE_TYPE,
                COLLECTION_TYPE,
                RESOURCE_TYPE,
                TestHateosResourceHandlerContext.class
        );
    }

    @Test
    public void testWithNullSelectionFails() {
        this.withFails(
                RESOURCE_NAME,
                null,
                VALUE_TYPE,
                COLLECTION_TYPE,
                RESOURCE_TYPE,
                TestHateosResourceHandlerContext.class
        );
    }

    @Test
    public void testWithNullValueTypeFails() {
        this.withFails(
                RESOURCE_NAME,
                this.selection(),
                null,
                COLLECTION_TYPE,
                RESOURCE_TYPE,
                TestHateosResourceHandlerContext.class
        );
    }

    @Test
    public void testWithNullCollectionTypeFails() {
        this.withFails(
                RESOURCE_NAME,
                this.selection(),
                VALUE_TYPE,
                null,
                RESOURCE_TYPE,
                TestHateosResourceHandlerContext.class
        );
    }

    @Test
    public void testWithInterfaceCollectionTypeFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> HateosResourceMappings.with(
                        RESOURCE_NAME,
                        this.selection(),
                        VALUE_TYPE,
                        Collection.class,
                        RESOURCE_TYPE,
                        TestHateosResourceHandlerContext.class
                )
        );
        this.checkEquals(
                "Collection type java.util.Collection is an interface expected a concrete class",
                thrown.getMessage()
        );
    }

    @Test
    public void testWithArrayCollectionTypeFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> HateosResourceMappings.with(
                        RESOURCE_NAME,
                        this.selection(),
                        VALUE_TYPE,
                        Object[].class,
                        RESOURCE_TYPE,
                        TestHateosResourceHandlerContext.class
                )
        );
        this.checkEquals(
                "Collection type [Ljava.lang.Object; is an array expected a concrete class",
                thrown.getMessage()
        );
    }

    @Test
    public void testWithNullResourceTypeFails() {
        this.withFails(
                RESOURCE_NAME,
                this.selection(),
                VALUE_TYPE,
                COLLECTION_TYPE,
                null,
                TestHateosResourceHandlerContext.class
        );
    }

    @Test
    public void testWithInterfaceResourceTypeFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> HateosResourceMappings.with(
                        RESOURCE_NAME,
                        this.selection(),
                        VALUE_TYPE,
                        COLLECTION_TYPE,
                        TestHateosResourceInterface.class,
                        TestHateosResourceHandlerContext.class
                )
        );
        this.checkEquals(
                "Resource type walkingkooka.net.http.server.hateos.HateosResourceMappingsTest$TestHateosResourceInterface is an interface expected a concrete class",
                thrown.getMessage()
        );
    }

    interface TestHateosResourceInterface extends HateosResource<BigInteger> {

    }

    private void withFails(
            final HateosResourceName resourceName,
            final BiFunction<String, TestHateosResourceHandlerContext, HateosResourceSelection<BigInteger>> selection,
            final Class<TestResource> valueType,
            final Class<TestResource2> COLLECTION_TYPE,
            final Class<TestHateosResource> resourceType,
            final Class<TestHateosResourceHandlerContext> contextType) {
        assertThrows(
                NullPointerException.class,
                () -> HateosResourceMappings.with(
                        resourceName,
                        selection,
                        valueType,
                        COLLECTION_TYPE,
                        resourceType,
                        contextType
                )
        );
    }

    // setHateosResourceHandler.........................................................................................

    @Test
    public void testSetHateosResourceHandlerWithNullLinkRelationFails() {
        this.setHateosResourceHandlerFails(
                null,
                METHOD,
                HATEOS_RESOURCE_HANDLER
        );
    }

    @Test
    public void testSetHateosResourceHandlerWithNullMethodFails() {
        this.setHateosResourceHandlerFails(
                LINK_RELATION,
                null,
                HATEOS_RESOURCE_HANDLER
        );
    }

    @Test
    public void testSetHateosResourceHandlerWithNullHandlerFails() {
        this.setHateosResourceHandlerFails(
                LINK_RELATION,
                METHOD,
                null
        );
    }

    private void setHateosResourceHandlerFails(final LinkRelation<?> relation,
                                               final HttpMethod method,
                                               final HateosResourceHandler<BigInteger, TestResource, TestResource2, TestHateosResourceHandlerContext> handler) {

        assertThrows(
                NullPointerException.class,
                () -> this.createMappings()
                        .setHateosResourceHandler(
                                relation,
                                method,
                                handler
                        )
        );
    }

    @Test
    public void testSetHateosResourceHandlerSame() {
        final LinkRelation<?> relation = LINK_RELATION;
        final HttpMethod method = METHOD;
        final HateosResourceHandler<BigInteger, TestResource, TestResource2, TestHateosResourceHandlerContext> handler = HATEOS_RESOURCE_HANDLER;

        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping = this.createMappings()
                .setHateosResourceHandler(
                        relation,
                        method,
                        handler
                );
        assertSame(
                mapping,
                mapping.setHateosResourceHandler(
                        relation,
                        method,
                        handler
                )
        );
    }

    @Test
    public void testSetHateosResourceHandlerDifferentRelation() {
        final LinkRelation<?> relation = LINK_RELATION;
        final HttpMethod method = METHOD;
        final HateosResourceHandler<BigInteger, TestResource, TestResource2, TestHateosResourceHandlerContext> handler = HATEOS_RESOURCE_HANDLER;

        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping = this.createMappings()
                .setHateosResourceHandler(
                        relation,
                        method,
                        handler
                );

        final LinkRelation<?> differentRelation = LinkRelation.with("different");
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> different = mapping.setHateosResourceHandler(
                differentRelation,
                method,
                handler
        );
        assertNotSame(
                mapping,
                different
        );

        this.check(
                mapping,
                Maps.of(
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                relation,
                                method
                        ),
                        HateosResourceMappingsHandler.hateosResourceHandler(handler)
                )
        );
        this.check(
                different,
                Maps.of(
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                relation,
                                method
                        ),
                        HateosResourceMappingsHandler.hateosResourceHandler(handler),
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                differentRelation,
                                method
                        ),
                        HateosResourceMappingsHandler.hateosResourceHandler(handler)
                )
        );
    }

    @Test
    public void testSetHateosResourceHandlerDifferentMethod() {
        final LinkRelation<?> relation = LINK_RELATION;
        final HttpMethod method = METHOD;
        final HateosResourceHandler<BigInteger, TestResource, TestResource2, TestHateosResourceHandlerContext> handler = HATEOS_RESOURCE_HANDLER;

        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mappings = this.createMappings()
                .setHateosResourceHandler(
                        relation,
                        method,
                        handler
                );

        final HttpMethod differentMethod = HttpMethod.with("different");
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> different = mappings.setHateosResourceHandler(
                relation,
                differentMethod,
                handler
        );
        assertNotSame(
                mappings,
                different
        );

        this.check(
                mappings,
                Maps.of(
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                relation,
                                method
                        ),
                        HateosResourceMappingsHandler.hateosResourceHandler(handler)
                )
        );
        this.check(
                different,
                Maps.of(
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                relation,
                                method
                        ),
                        HateosResourceMappingsHandler.hateosResourceHandler(handler),
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                relation,
                                differentMethod
                        ),
                        HateosResourceMappingsHandler.hateosResourceHandler(handler)
                )
        );
    }

    @Test
    public void testSetHateosResourceHandlerDifferentHandler() {
        final LinkRelation<?> relation = LINK_RELATION;
        final HttpMethod method = METHOD;
        final HateosResourceHandler<BigInteger, TestResource, TestResource2, TestHateosResourceHandlerContext> handler = HATEOS_RESOURCE_HANDLER;

        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mappings = this.createMappings()
                .setHateosResourceHandler(
                        relation,
                        method,
                        handler
                );

        final HateosResourceHandler<BigInteger, TestResource, TestResource2, TestHateosResourceHandlerContext> differentHandler = new FakeHateosResourceHandler<>();
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> different = mappings.setHateosResourceHandler(
                relation,
                method,
                differentHandler
        );
        assertNotSame(
                mappings,
                different
        );

        this.check(
                mappings,
                Maps.of(
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                relation,
                                method
                        ),
                        HateosResourceMappingsHandler.hateosResourceHandler(handler)
                )
        );
        this.check(
                different,
                Maps.of(
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                relation,
                                method
                        ),
                        HateosResourceMappingsHandler.hateosResourceHandler(differentHandler)
                )
        );
    }

    @Test
    public void testSetHateosResourceHandlerMultipleHandlers() {
        final LinkRelation<?> relation1 = LINK_RELATION;
        final HttpMethod method1 = METHOD;
        final HateosResourceHandler<BigInteger, TestResource, TestResource2, TestHateosResourceHandlerContext> handler1 = HATEOS_RESOURCE_HANDLER;

        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping = this.createMappings()
                .setHateosResourceHandler(relation1, method1, handler1);

        final LinkRelation<?> relation2 = LinkRelation.with("relation2");
        final HttpMethod method2 = HttpMethod.with("HTTPMETHODB");
        final HateosResourceHandler<BigInteger, TestResource, TestResource2, TestHateosResourceHandlerContext> handler2 = new FakeHateosResourceHandler<>();
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mappings2 = mapping.setHateosResourceHandler(relation2,
                method2,
                handler2);
        assertNotSame(mapping, mappings2);

        final LinkRelation<?> relation3 = LinkRelation.with("relation3");
        final HttpMethod method3 = HttpMethod.with("HTTPMETHODC");
        final HateosResourceHandler<BigInteger, TestResource, TestResource2, TestHateosResourceHandlerContext> handler3 = new FakeHateosResourceHandler<>();
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mappings3 = mappings2.setHateosResourceHandler(relation3,
                method3,
                handler3);
        assertNotSame(mapping, mappings3);

        this.check(
                mappings3,
                Maps.of(
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                relation1,
                                method1
                        ),
                        HateosResourceMappingsHandler.hateosResourceHandler(handler1),
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                relation2,
                                method2
                        ),
                        HateosResourceMappingsHandler.hateosResourceHandler(handler2),
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                relation3,
                                method3
                        ),
                        HateosResourceMappingsHandler.hateosResourceHandler(handler3)
                )
        );
    }

    // setHateosHttpEntityHandler.......................................................................................

    private final static HateosHttpEntityHandler<BigInteger, TestHateosResourceHandlerContext> HATEOS_HTTP_ENTITY_HANDLER = HateosHttpEntityHandlers.fake();

    @Test
    public void testSetHateosHttpEntityHandlerWithNullLinkRelationFails() {
        this.setHateosHttpEntityHandlerFails(
                null,
                METHOD,
                HATEOS_HTTP_ENTITY_HANDLER
        );
    }

    @Test
    public void testSetHateosHttpEntityHandlerWithNullMethodFails() {
        this.setHateosHttpEntityHandlerFails(
                LINK_RELATION,
                null,
                HATEOS_HTTP_ENTITY_HANDLER
        );
    }

    @Test
    public void testSetHateosHttpEntityHandlerWithNullHandlerFails() {
        this.setHateosHttpEntityHandlerFails(
                LINK_RELATION,
                METHOD,
                null
        );
    }

    private void setHateosHttpEntityHandlerFails(final LinkRelation<?> relation,
                                                 final HttpMethod method,
                                                 final HateosHttpEntityHandler<BigInteger, TestHateosResourceHandlerContext> handler) {

        assertThrows(
                NullPointerException.class,
                () -> this.createMappings()
                        .setHateosHttpEntityHandler(
                                relation,
                                method,
                                handler
                        )
        );
    }

    @Test
    public void testSetHateosHttpEntityHandlerSame() {
        final LinkRelation<?> relation = LINK_RELATION;
        final HttpMethod method = METHOD;

        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping = this.createMappings()
                .setHateosHttpEntityHandler(
                        relation,
                        method,
                        HATEOS_HTTP_ENTITY_HANDLER
                );
        assertSame(
                mapping,
                mapping.setHateosHttpEntityHandler(
                        relation,
                        method,
                        HATEOS_HTTP_ENTITY_HANDLER
                )
        );
    }

    @Test
    public void testSetHateosHttpEntityHandlerDifferentRelation() {
        final LinkRelation<?> relation = LINK_RELATION;
        final HttpMethod method = METHOD;

        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping = this.createMappings()
                .setHateosHttpEntityHandler(
                        relation,
                        method,
                        HATEOS_HTTP_ENTITY_HANDLER
                );

        final LinkRelation<?> differentRelation = LinkRelation.with("different");
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> different = mapping.setHateosHttpEntityHandler(
                differentRelation,
                method,
                HATEOS_HTTP_ENTITY_HANDLER
        );
        assertNotSame(mapping, different);

        this.check(
                mapping,
                Maps.of(
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                relation,
                                method
                        ),
                        HateosResourceMappingsHandler.hateosHttpEntityHandler(HATEOS_HTTP_ENTITY_HANDLER)
                )
        );
        this.check(
                different,
                Maps.of(
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                relation,
                                method
                        ),
                        HateosResourceMappingsHandler.hateosHttpEntityHandler(HATEOS_HTTP_ENTITY_HANDLER),
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                differentRelation,
                                method
                        ),
                        HateosResourceMappingsHandler.hateosHttpEntityHandler(HATEOS_HTTP_ENTITY_HANDLER)
                )
        );
    }

    @Test
    public void testSetHateosHttpEntityHandlerDifferentMethod() {
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping = this.createMappings()
                .setHateosHttpEntityHandler(
                        LINK_RELATION,
                        METHOD,
                        HATEOS_HTTP_ENTITY_HANDLER
                );

        final HttpMethod differentMethod = HttpMethod.with("different");
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> different = mapping.setHateosHttpEntityHandler(
                LINK_RELATION,
                differentMethod,
                HATEOS_HTTP_ENTITY_HANDLER
        );
        assertNotSame(mapping, different);

        this.check(
                mapping,
                Maps.of(
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                LINK_RELATION,
                                METHOD
                        ),
                        HateosResourceMappingsHandler.hateosHttpEntityHandler(HATEOS_HTTP_ENTITY_HANDLER)
                )
        );
        this.check(
                different,
                Maps.of(
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                LINK_RELATION,
                                METHOD
                        ),
                        HateosResourceMappingsHandler.hateosHttpEntityHandler(HATEOS_HTTP_ENTITY_HANDLER),
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                LINK_RELATION,
                                differentMethod
                        ),
                        HateosResourceMappingsHandler.hateosHttpEntityHandler(HATEOS_HTTP_ENTITY_HANDLER)
                )
        );
    }

    @Test
    public void testSetHateosHttpEntityHandlerDifferentHandler() {
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mappings = this.createMappings()
                .setHateosHttpEntityHandler(
                        LINK_RELATION,
                        METHOD,
                        HATEOS_HTTP_ENTITY_HANDLER
                );

        final HateosHttpEntityHandler<BigInteger, TestHateosResourceHandlerContext> differentHandler = new FakeHateosHttpEntityHandler<>();
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> different = mappings.setHateosHttpEntityHandler(
                LINK_RELATION,
                METHOD,
                differentHandler
        );
        assertNotSame(mappings, different);

        this.check(
                mappings,
                Maps.of(
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                LINK_RELATION,
                                METHOD
                        ),
                        HateosResourceMappingsHandler.hateosHttpEntityHandler(HATEOS_HTTP_ENTITY_HANDLER)
                )
        );
        this.check(
                different,
                Maps.of(
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                LINK_RELATION,
                                METHOD
                        ),
                        HateosResourceMappingsHandler.hateosHttpEntityHandler(differentHandler)
                )
        );
    }

    @Test
    public void testSetHateosHttpEntityHandlerMultipleHandlers() {
        final LinkRelation<?> linkRelation1 = LINK_RELATION;
        final HttpMethod method1 = METHOD;
        final HateosHttpEntityHandler<BigInteger, TestHateosResourceHandlerContext> handler1 = HateosHttpEntityHandlers.fake();

        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mappings1 = this.createMappings()
                .setHateosHttpEntityHandler(linkRelation1, method1, handler1);

        final LinkRelation<?> linkRelation2 = LinkRelation.with("relation2");
        final HttpMethod method2 = HttpMethod.with("HTTPMETHODB");
        final HateosHttpEntityHandler<BigInteger, TestHateosResourceHandlerContext> handler2 = HateosHttpEntityHandlers.fake();
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mappings2 = mappings1.setHateosHttpEntityHandler(
                linkRelation2,
                method2,
                handler2
        );
        assertNotSame(
                mappings1,
                mappings2
        );

        final LinkRelation<?> linkRelation3 = LinkRelation.with("relation3");
        final HttpMethod method3 = HttpMethod.with("HTTPMETHODC");
        final HateosHttpEntityHandler<BigInteger, TestHateosResourceHandlerContext> handler3 = HateosHttpEntityHandlers.fake();
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mappings3 = mappings2.setHateosHttpEntityHandler(linkRelation3,
                method3,
                handler3);
        assertNotSame(
                mappings1,
                mappings3
        );

        this.check(
                mappings3,
                Maps.of(
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                linkRelation1,
                                method1
                        ),
                        HateosResourceMappingsHandler.hateosHttpEntityHandler(handler1),
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                linkRelation2,
                                method2
                        ),
                        HateosResourceMappingsHandler.hateosHttpEntityHandler(handler2),
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                linkRelation3,
                                method3
                        ),
                        HateosResourceMappingsHandler.hateosHttpEntityHandler(handler3)
                )
        );
    }

    private void check(final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mappings,
                       final Map<HateosResourceMappingsLinkRelationHttpMethod, HateosResourceMappingsHandler<?>> linkRelationAndMethodToHandler) {
        this.checkEquals(
                linkRelationAndMethodToHandler,
                mappings.relationAndMethodToHandlers,
                "relationAndMethodToHandlers"
        );
    }

    // helpers..........................................................................................................

    private HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> createMappings() {
        return HateosResourceMappings.with(
                RESOURCE_NAME,
                this.selection(),
                VALUE_TYPE,
                COLLECTION_TYPE,
                RESOURCE_TYPE,
                TestHateosResourceHandlerContext.class
        );
    }

    private BiFunction<String, TestHateosResourceHandlerContext, HateosResourceSelection<BigInteger>> selection() {
        return (s, x) -> {
            if (s.isEmpty()) {
                return HateosResourceSelection.none();
            }
            if ("*".equals(s)) {
                return HateosResourceSelection.all();
            }
            final int range = s.indexOf("-");
            if (-1 != range) {
                return HateosResourceSelection.range(
                        Range.greaterThanEquals(
                                new BigInteger(
                                        s.substring(0, range)
                                )
                        ).and(
                                Range.lessThanEquals(
                                        new BigInteger(
                                                s.substring(range + 1)
                                        )
                                )
                        )
                );
            }
            final int many = s.indexOf(",");
            if (-1 == many) {
                return HateosResourceSelection.many(
                        Arrays.stream(s.split(","))
                                .map(BigInteger::new)
                                .collect(
                                        Collectors.toCollection(SortedSets::tree)
                                )
                );
            }
            return HateosResourceSelection.one(new BigInteger(s));
        };
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createMappings()
                        .setHateosResourceHandler(
                                LINK_RELATION,
                                METHOD,
                                new FakeHateosResourceHandler<>() {

                                    @Override
                                    public String toString() {
                                        return "Handler123";
                                    }
                                }
                        ),
                "abc123 \"walkingkooka.net.http.server.hateos.TestHateosResource\" item POST=Handler123"
        );
    }

    @Test
    public void testToStringMultiple() {
        this.toStringAndCheck(
                this.createMappings()
                        .setHateosResourceHandler(
                                LinkRelation.SELF,
                                HttpMethod.GET,
                                new FakeHateosResourceHandler<>() {

                                    @Override
                                    public String toString() {
                                        return "Handler111";
                                    }
                                }
                        ).setHateosResourceHandler(
                                LinkRelation.SELF,
                                METHOD,
                                new FakeHateosResourceHandler<>() {

                                    @Override
                                    public String toString() {
                                        return "Handler222";
                                    }
                                }
                        ),
                "abc123 \"walkingkooka.net.http.server.hateos.TestHateosResource\" self GET=Handler111,self POST=Handler222"
        );
    }

    // class............................................................................................................

    @Override
    public Class<HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext>> type() {
        return Cast.to(HateosResourceMappings.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
