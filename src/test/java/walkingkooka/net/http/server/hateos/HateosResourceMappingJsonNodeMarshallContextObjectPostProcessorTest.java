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
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessorTesting;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;

import java.math.BigInteger;
import java.util.Set;
import java.util.function.BiFunction;

public final class HateosResourceMappingJsonNodeMarshallContextObjectPostProcessorTest extends HateosResourceMappingTestCase2<HateosResourceMappingJsonNodeMarshallContextObjectPostProcessor>
        implements JsonNodeMarshallContextObjectPostProcessorTesting<HateosResourceMappingJsonNodeMarshallContextObjectPostProcessor> {

    // apply............................................................................................................

    @Test
    public void testHateosResourceWithoutLinks() {
        final StringName without = Names.string("1");
        this.marshallAndCheck(without, this.marshallContext().marshall(without));
    }

    @Test
    public void testHateosResourceWithLinks() {
        final TestHateosResource resource = TestHateosResource.with(BigInteger.valueOf(123));
        this.marshallAndCheck(resource,
                JsonNode.parse("{\n" +
                        "  \"id\": \"123\",\n" +
                        "  \"_links\": [{\n" +
                        "    \"href\": \"https://example.com/api/resource-1/7b\",\n" +
                        "    \"method\": \"POST\",\n" +
                        "    \"rel\": \"self\",\n" +
                        "    \"type\": \"application/test-json-123\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"https://example.com/api/resource-1/7b/contents\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"contents\",\n" +
                        "    \"type\": \"application/test-json-123\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"https://example.com/api/resource-1/7b/contents\",\n" +
                        "    \"method\": \"POST\",\n" +
                        "    \"rel\": \"contents\",\n" +
                        "    \"type\": \"application/test-json-123\"\n" +
                        "  }]\n" +
                        "}"));
    }

    private void marshallAndCheck(final Object resource,
                                  final JsonNode json) {
        this.checkEquals(json,
                JsonNodeMarshallContexts.basic()
                        .setObjectPostProcessor(this.createBiFunction()).marshall(resource),
                () -> resource.toString());
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createBiFunction(),
                "{walkingkooka.net.http.server.hateos.TestHateosResource=resource-1, self=POST, contents=GET, POST, walkingkooka.net.http.server.hateos.TestHateosResource2=resource-2, about=PUT}");
    }

    // BiFunction.......................................................................................................

    @Override
    public HateosResourceMappingJsonNodeMarshallContextObjectPostProcessor createBiFunction() {
        return HateosResourceMappingJsonNodeMarshallContextObjectPostProcessor.with(
                this.baseUrl(),
                this.mappings(),
                new TestHateosResourceHandlerContext()
        );
    }

    private AbsoluteUrl baseUrl() {
        return Url.parseAbsolute("https://example.com/api");
    }

    private HateosResourceName resourceName1() {
        return HateosResourceName.with("resource-1");
    }

    private HateosResourceName resourceName2() {
        return HateosResourceName.with("resource-2");
    }

    private Set<HateosResourceMapping<?, ?, ?, ?, ?>> mappings() {
        final HateosResourceName resourceName1 = this.resourceName1();
        final HateosResourceName resourceName2 = this.resourceName2();

        final HateosResourceMapping<?, ?, ?, ?, ?> mapping1 = HateosResourceMapping.with(
                        resourceName1,
                        this.selectionParser(),
                        TestResource.class,
                        TestResource2.class,
                        TestHateosResource.class,
                        TestHateosResourceHandlerContext.class
                ).setHateosResourceHandler(LinkRelation.CONTENTS, HttpMethod.GET, new FakeHateosResourceHandler<>())
                .setHateosResourceHandler(LinkRelation.CONTENTS, HttpMethod.POST, new FakeHateosResourceHandler<>())
                .setHateosResourceHandler(LinkRelation.SELF, HttpMethod.POST, new FakeHateosResourceHandler<>());

        final HateosResourceMapping<?, ?, ?, ?, ?> mapping2 = HateosResourceMapping.with(
                        resourceName2,
                        this.selectionParser(),
                        TestResource.class,
                        TestResource2.class,
                        TestHateosResource2.class,
                        TestHateosResourceHandlerContext.class
                )
                .setHateosResourceHandler(LinkRelation.ABOUT, HttpMethod.PUT, new FakeHateosResourceHandler<>());

        return Sets.of(mapping1, mapping2);
    }

    private BiFunction<String, TestHateosResourceHandlerContext, HateosResourceSelection<BigInteger>> selectionParser() {
        return (s, x) -> HateosResourceSelection.one(
                new BigInteger(s)
        );
    }

    static class TestHateosResourceHandlerContext extends FakeHateosResourceHandlerContext {

        @Override
        public MediaType contentType() {
            return MediaType.parse("application/test-json-123");
        }

        @Override
        public JsonNode marshall(final Object value) {
            return JsonNodeMarshallContexts.basic()
                    .marshall(value);
        }
    }

    // ClassTesting......................................................................................................

    @Override
    public Class<HateosResourceMappingJsonNodeMarshallContextObjectPostProcessor> type() {
        return HateosResourceMappingJsonNodeMarshallContextObjectPostProcessor.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return HateosResourceMapping.class.getSimpleName();
    }
}
