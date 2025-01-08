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
import walkingkooka.ToStringTesting;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.Url;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

public final class HateosResourceMappingJsonNodeMarshallContextObjectPostProcessorMappingTest extends HateosResourceMappingTestCase2<HateosResourceMappingJsonNodeMarshallContextObjectPostProcessorMapping>
        implements ToStringTesting<HateosResourceMappingJsonNodeMarshallContextObjectPostProcessorMapping> {

    @Test
    public void testSelfGetAddLinks() {
        final String withLinks = "{\n" +
                "  \"a\": 1,\n" +
                "  \"b\": 2,\n" +
                "  \"_links\": [{\n" +
                "    \"href\": \"https://example.com/api/resource1/7b\",\n" +
                "    \"method\": \"GET\",\n" +
                "    \"rel\": \"self\",\n" +
                "    \"type\": \"application/test-json\"\n" +
                "  }]\n" +
                "}";
        this.addLinksAndCheck(this.createMapping(Maps.of(LinkRelation.SELF, Sets.of(HttpMethod.GET))),
                withLinks);
    }

    @Test
    public void testSelfGetPostAddLinks() {
        final String withLinks = "{\n" +
                "  \"a\": 1,\n" +
                "  \"b\": 2,\n" +
                "  \"_links\": [{\n" +
                "    \"href\": \"https://example.com/api/resource1/7b\",\n" +
                "    \"method\": \"GET\",\n" +
                "    \"rel\": \"self\",\n" +
                "    \"type\": \"application/test-json\"\n" +
                "  }, {\n" +
                "    \"href\": \"https://example.com/api/resource1/7b\",\n" +
                "    \"method\": \"POST\",\n" +
                "    \"rel\": \"self\",\n" +
                "    \"type\": \"application/test-json\"\n" +
                "  }]\n" +
                "}";
        this.addLinksAndCheck(this.createMapping(Maps.of(LinkRelation.SELF, Sets.of(HttpMethod.GET, HttpMethod.POST))),
                withLinks);
    }

    @Test
    public void testContentsGetAddLinks() {
        final String withLinks = "{\n" +
                "  \"a\": 1,\n" +
                "  \"b\": 2,\n" +
                "  \"_links\": [{\n" +
                "    \"href\": \"https://example.com/api/resource1/7b/contents\",\n" +
                "    \"method\": \"GET\",\n" +
                "    \"rel\": \"contents\",\n" +
                "    \"type\": \"application/test-json\"\n" +
                "  }]\n" +
                "}";
        this.addLinksAndCheck(this.createMapping(Maps.of(LinkRelation.CONTENTS, Sets.of(HttpMethod.GET))), withLinks);
    }

    @Test
    public void testManyRelationsAddLinks() {
        final String withLinks = "{\n" +
                "  \"a\": 1,\n" +
                "  \"b\": 2,\n" +
                "  \"_links\": [{\n" +
                "    \"href\": \"https://example.com/api/resource1/7b\",\n" +
                "    \"method\": \"GET\",\n" +
                "    \"rel\": \"self\",\n" +
                "    \"type\": \"application/test-json\"\n" +
                "  }, {\n" +
                "    \"href\": \"https://example.com/api/resource1/7b\",\n" +
                "    \"method\": \"POST\",\n" +
                "    \"rel\": \"self\",\n" +
                "    \"type\": \"application/test-json\"\n" +
                "  }, {\n" +
                "    \"href\": \"https://example.com/api/resource1/7b/about\",\n" +
                "    \"method\": \"DELETE\",\n" +
                "    \"rel\": \"about\",\n" +
                "    \"type\": \"application/test-json\"\n" +
                "  }]\n" +
                "}";
        this.addLinksAndCheck(this.createMapping(Maps.of(LinkRelation.SELF, Sets.of(HttpMethod.GET, HttpMethod.POST),
                        LinkRelation.ABOUT, Sets.of(HttpMethod.DELETE))),
                withLinks);
    }

    private void addLinksAndCheck(final HateosResourceMappingJsonNodeMarshallContextObjectPostProcessorMapping mapping,
                                  final String withLinks) {
        final String before = "{\"a\": 1, \"b\": 2}";
        this.checkEquals(JsonNode.parse(withLinks),
                mapping.addLinks(TestHateosResource.with(BigInteger.valueOf(123)),
                        JsonNode.parse(before).objectOrFail(),
                        Url.parseAbsolute("https://example.com/api"),
                        new FakeHateosResourceHandlerContext() {

                            @Override
                            public MediaType contentType() {
                                return MediaType.parse("application/test-json");
                            }

                            @Override
                            public JsonNode marshall(final Object value) {
                                return JsonNodeMarshallContexts.basic()
                                        .marshall(value);
                            }
                        }
                ),
                mapping::toString);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createMapping(), "resource1, about=GET, contents=GET, POST");
    }

    private HateosResourceMappingJsonNodeMarshallContextObjectPostProcessorMapping createMapping() {
        return this.createMapping(Maps.of(LinkRelation.ABOUT, Sets.of(HttpMethod.GET), LinkRelation.CONTENTS, Sets.of(HttpMethod.GET, HttpMethod.POST)));
    }

    private HateosResourceMappingJsonNodeMarshallContextObjectPostProcessorMapping createMapping(final Map<LinkRelation<?>, Set<HttpMethod>> relationToMethods) {
        return HateosResourceMappingJsonNodeMarshallContextObjectPostProcessorMapping.with(HateosResourceName.with("resource1"), relationToMethods);
    }

    // TypeTesting......................................................................................................

    @Override
    public Class<HateosResourceMappingJsonNodeMarshallContextObjectPostProcessorMapping> type() {
        return HateosResourceMappingJsonNodeMarshallContextObjectPostProcessorMapping.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return HateosResourceMappingJsonNodeMarshallContextObjectPostProcessor.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return "Mapping";
    }
}
