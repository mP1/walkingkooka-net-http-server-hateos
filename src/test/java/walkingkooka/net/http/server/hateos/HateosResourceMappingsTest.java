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

    static class TestHateosResourceHandlerContext extends FakeHateosResourceHandlerContext {
    }

    @Test
    public void testWithNullResourceNameFails() {
        this.withFails(
                null,
                this.selection(),
                this.valueType(),
                this.collectionType(),
                this.resourceType(),
                TestHateosResourceHandlerContext.class
        );
    }

    @Test
    public void testWithNullSelectionFails() {
        this.withFails(
                this.resourceName(),
                null,
                this.valueType(),
                this.collectionType(),
                this.resourceType(),
                TestHateosResourceHandlerContext.class
        );
    }

    @Test
    public void testWithNullValueTypeFails() {
        this.withFails(
                this.resourceName(),
                this.selection(),
                null,
                this.collectionType(),
                this.resourceType(),
                TestHateosResourceHandlerContext.class
        );
    }

    @Test
    public void testWithNullCollectionTypeFails() {
        this.withFails(
                this.resourceName(),
                this.selection(),
                this.valueType(),
                null,
                this.resourceType(),
                TestHateosResourceHandlerContext.class
        );
    }

    @Test
    public void testWithInterfaceCollectionTypeFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> HateosResourceMappings.with(
                        this.resourceName(),
                        this.selection(),
                        this.valueType(),
                        Collection.class,
                        this.resourceType(),
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
                        this.resourceName(),
                        this.selection(),
                        this.valueType(),
                        Object[].class,
                        this.resourceType(),
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
                this.resourceName(),
                this.selection(),
                this.valueType(),
                this.collectionType(),
                null,
                TestHateosResourceHandlerContext.class
        );
    }

    @Test
    public void testWithInterfaceResourceTypeFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> HateosResourceMappings.with(
                        this.resourceName(),
                        this.selection(),
                        this.valueType(),
                        this.collectionType(),
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
            final Class<TestResource2> collectionType,
            final Class<TestHateosResource> resourceType,
            final Class<TestHateosResourceHandlerContext> contextType) {
        assertThrows(
                NullPointerException.class,
                () -> HateosResourceMappings.with(
                        resourceName,
                        selection,
                        valueType,
                        collectionType,
                        resourceType,
                        contextType
                )
        );
    }

    // setHateosResourceHandler.........................................................................................

    @Test
    public void testSetHateosResourceHandlerNullLinkRelationFails() {
        this.setHateosResourceHandlerFails(
                null,
                this.method(),
                this.hateosResourceHandler()
        );
    }

    @Test
    public void testSetHateosResourceHandlerNullMethodFails() {
        this.setHateosResourceHandlerFails(
                this.relation(),
                null,
                this.hateosResourceHandler()
        );
    }

    @Test
    public void testSetHateosResourceHandlerNullHandlerFails() {
        this.setHateosResourceHandlerFails(
                this.relation(),
                this.method(),
                null
        );
    }

    private void setHateosResourceHandlerFails(final LinkRelation<?> relation,
                                               final HttpMethod method,
                                               final HateosResourceHandler<BigInteger, TestResource, TestResource2, TestHateosResourceHandlerContext> handler) {

        assertThrows(
                NullPointerException.class,
                () -> this.createMapping()
                        .setHateosResourceHandler(
                                relation,
                                method,
                                handler
                        )
        );
    }

    @Test
    public void testSetHateosResourceHandlerSame() {
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();
        final HateosResourceHandler<BigInteger, TestResource, TestResource2, TestHateosResourceHandlerContext> handler = this.hateosResourceHandler();

        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping = this.createMapping()
                .setHateosResourceHandler(relation, method, handler);
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
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();
        final HateosResourceHandler<BigInteger, TestResource, TestResource2, TestHateosResourceHandlerContext> handler = this.hateosResourceHandler();

        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping = this.createMapping()
                .setHateosResourceHandler(relation, method, handler);

        final LinkRelation<?> differentRelation = LinkRelation.with("different");
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> different = mapping.setHateosResourceHandler(differentRelation, method, handler);
        assertNotSame(mapping, different);

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
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();
        final HateosResourceHandler<BigInteger, TestResource, TestResource2, TestHateosResourceHandlerContext> handler = this.hateosResourceHandler();

        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping = this.createMapping()
                .setHateosResourceHandler(relation, method, handler);

        final HttpMethod differentMethod = HttpMethod.with("different");
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> different = mapping.setHateosResourceHandler(relation, differentMethod, handler);
        assertNotSame(mapping, different);

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
                                relation,
                                differentMethod
                        ),
                        HateosResourceMappingsHandler.hateosResourceHandler(handler)
                )
        );
    }

    @Test
    public void testSetHateosResourceHandlerDifferentHandler() {
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();
        final HateosResourceHandler<BigInteger, TestResource, TestResource2, TestHateosResourceHandlerContext> handler = this.hateosResourceHandler();

        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping = this.createMapping()
                .setHateosResourceHandler(relation, method, handler);

        final HateosResourceHandler<BigInteger, TestResource, TestResource2, TestHateosResourceHandlerContext> differentHandler = new FakeHateosResourceHandler<>();
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> different = mapping.setHateosResourceHandler(relation, method, differentHandler);
        assertNotSame(mapping, different);

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
                        HateosResourceMappingsHandler.hateosResourceHandler(differentHandler)
                )
        );
    }

    @Test
    public void testSetHateosResourceHandlerMultipleHandlers() {
        final LinkRelation<?> relation1 = this.relation();
        final HttpMethod method1 = this.method();
        final HateosResourceHandler<BigInteger, TestResource, TestResource2, TestHateosResourceHandlerContext> handler1 = this.hateosResourceHandler();

        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping = this.createMapping()
                .setHateosResourceHandler(relation1, method1, handler1);

        final LinkRelation<?> relation2 = LinkRelation.with("relation2");
        final HttpMethod method2 = HttpMethod.with("HTTPMETHODB");
        final HateosResourceHandler<BigInteger, TestResource, TestResource2, TestHateosResourceHandlerContext> handler2 = new FakeHateosResourceHandler<>();
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping2 = mapping.setHateosResourceHandler(relation2,
                method2,
                handler2);
        assertNotSame(mapping, mapping2);

        final LinkRelation<?> relation3 = LinkRelation.with("relation3");
        final HttpMethod method3 = HttpMethod.with("HTTPMETHODC");
        final HateosResourceHandler<BigInteger, TestResource, TestResource2, TestHateosResourceHandlerContext> handler3 = new FakeHateosResourceHandler<>();
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping3 = mapping2.setHateosResourceHandler(relation3,
                method3,
                handler3);
        assertNotSame(mapping, mapping3);

        this.check(
                mapping3,
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

    // setHateosHttpEntityHandler.........................................................................................

    private final static HateosHttpEntityHandler<BigInteger, TestHateosResourceHandlerContext> HATEOS_HTTP_ENTITY_HANDLER = HateosHttpEntityHandlers.fake();

    @Test
    public void testSetHateosHttpEntityHandlerNullLinkRelationFails() {
        this.setHateosHttpEntityHandlerFails(
                null,
                this.method(),
                HATEOS_HTTP_ENTITY_HANDLER
        );
    }

    @Test
    public void testSetHateosHttpEntityHandlerNullMethodFails() {
        this.setHateosHttpEntityHandlerFails(
                this.relation(),
                null,
                HATEOS_HTTP_ENTITY_HANDLER
        );
    }

    @Test
    public void testSetHateosHttpEntityHandlerNullHandlerFails() {
        this.setHateosHttpEntityHandlerFails(
                this.relation(),
                this.method(),
                null
        );
    }

    private void setHateosHttpEntityHandlerFails(final LinkRelation<?> relation,
                                                 final HttpMethod method,
                                                 final HateosHttpEntityHandler<BigInteger, TestHateosResourceHandlerContext> handler) {

        assertThrows(
                NullPointerException.class,
                () -> this.createMapping()
                        .setHateosHttpEntityHandler(
                                relation,
                                method,
                                handler
                        )
        );
    }

    @Test
    public void testSetHateosHttpEntityHandlerSame() {
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();

        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping = this.createMapping()
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
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();

        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping = this.createMapping()
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
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();

        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping = this.createMapping()
                .setHateosHttpEntityHandler(
                        relation,
                        method,
                        HATEOS_HTTP_ENTITY_HANDLER
                );

        final HttpMethod differentMethod = HttpMethod.with("different");
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> different = mapping.setHateosHttpEntityHandler(
                relation,
                differentMethod,
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
                                relation,
                                differentMethod
                        ),
                        HateosResourceMappingsHandler.hateosHttpEntityHandler(HATEOS_HTTP_ENTITY_HANDLER)
                )
        );
    }

    @Test
    public void testSetHateosHttpEntityHandlerDifferentHandler() {
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();

        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping = this.createMapping()
                .setHateosHttpEntityHandler(
                        relation,
                        method,
                        HATEOS_HTTP_ENTITY_HANDLER
                );

        final HateosHttpEntityHandler<BigInteger, TestHateosResourceHandlerContext> differentHandler = new FakeHateosHttpEntityHandler<>();
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> different = mapping.setHateosHttpEntityHandler(relation, method, differentHandler);
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
                        HateosResourceMappingsHandler.hateosHttpEntityHandler(differentHandler)
                )
        );
    }

    @Test
    public void testSetHateosHttpEntityHandlerMultipleHandlers() {
        final LinkRelation<?> relation1 = this.relation();
        final HttpMethod method1 = this.method();
        final HateosHttpEntityHandler<BigInteger, TestHateosResourceHandlerContext> handler1 = HateosHttpEntityHandlers.fake();

        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping = this.createMapping()
                .setHateosHttpEntityHandler(relation1, method1, handler1);

        final LinkRelation<?> relation2 = LinkRelation.with("relation2");
        final HttpMethod method2 = HttpMethod.with("HTTPMETHODB");
        final HateosHttpEntityHandler<BigInteger, TestHateosResourceHandlerContext> handler2 = HateosHttpEntityHandlers.fake();
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping2 = mapping.setHateosHttpEntityHandler(relation2,
                method2,
                handler2);
        assertNotSame(mapping, mapping2);

        final LinkRelation<?> relation3 = LinkRelation.with("relation3");
        final HttpMethod method3 = HttpMethod.with("HTTPMETHODC");
        final HateosHttpEntityHandler<BigInteger, TestHateosResourceHandlerContext> handler3 = HateosHttpEntityHandlers.fake();
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping3 = mapping2.setHateosHttpEntityHandler(relation3,
                method3,
                handler3);
        assertNotSame(mapping, mapping3);

        this.check(
                mapping3,
                Maps.of(
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                relation1,
                                method1
                        ),
                        HateosResourceMappingsHandler.hateosHttpEntityHandler(handler1),
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                relation2,
                                method2
                        ),
                        HateosResourceMappingsHandler.hateosHttpEntityHandler(handler2),
                        HateosResourceMappingsLinkRelationHttpMethod.with(
                                relation3,
                                method3
                        ),
                        HateosResourceMappingsHandler.hateosHttpEntityHandler(handler3)
                )
        );
    }

    private void check(final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping,
                       final Map<HateosResourceMappingsLinkRelationHttpMethod, HateosResourceMappingsHandler> relationAndMethodToHandler) {
        this.checkEquals(
                relationAndMethodToHandler,
                mapping.relationAndMethodToHandlers,
                "relationAndMethodToHandlers"
        );
    }

    // helpers..........................................................................................................

    private HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> createMapping() {
        return HateosResourceMappings.with(
                this.resourceName(),
                this.selection(),
                this.valueType(),
                this.collectionType(),
                this.resourceType(),
                TestHateosResourceHandlerContext.class
        );
    }

    private HateosResourceName resourceName() {
        return HateosResourceName.with("abc123");
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
                return HateosResourceSelection.range(Range.greaterThanEquals(new BigInteger(s.substring(0, range))).and(Range.lessThanEquals(new BigInteger(s.substring(range + 1)))));
            }
            final int many = s.indexOf(",");
            if (-1 == many) {
                return HateosResourceSelection.many(
                        Arrays.stream(s.split(","))
                                .map(BigInteger::new)
                                .collect(Collectors.toCollection(SortedSets::tree))
                );
            }
            return HateosResourceSelection.one(new BigInteger(s));
        };
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

    private LinkRelation<?> relation() {
        return LinkRelation.ITEM;
    }

    private HttpMethod method() {
        return HttpMethod.POST;
    }

    private HateosResourceHandler<BigInteger, TestResource, TestResource2, TestHateosResourceHandlerContext> hateosResourceHandler() {
        return new FakeHateosResourceHandler<>();
    }

    // toString..........................................................................................................

    @Test
    public void testToString() {
        final HateosResourceMappings<BigInteger, TestResource, TestResource2, TestHateosResource, TestHateosResourceHandlerContext> mapping = this.createMapping();
        final LinkRelation<?> relation = this.relation();
        final HttpMethod method = this.method();
        final HateosResourceHandler<BigInteger, TestResource, TestResource2, TestHateosResourceHandlerContext> handler = new FakeHateosResourceHandler<>() {

            @Override
            public String toString() {
                return "Handler123";
            }
        };

        this.toStringAndCheck(mapping.setHateosResourceHandler(relation, method, handler),
                "abc123 \"walkingkooka.net.http.server.hateos.TestHateosResource\" item POST=Handler123");
    }

    @Test
    public void testToStringMultiple() {
        this.toStringAndCheck(
                this.createMapping()
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
                                HttpMethod.POST,
                                new FakeHateosResourceHandler<>() {

                                    @Override
                                    public String toString() {
                                        return "Handler222";
                                    }
                                }
                        ),
                "abc123 \"walkingkooka.net.http.server.hateos.TestHateosResource\" self GET=Handler111,self POST=Handler222");
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
