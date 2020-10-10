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
import walkingkooka.net.http.HttpMethod;
import walkingkooka.tree.json.JsonNode;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class HateosResourceMappingObjectPostProcessorBiFunctionMappingTest extends HateosResourceMappingTestCase2<HateosResourceMappingObjectPostProcessorBiFunctionMapping>
        implements ToStringTesting<HateosResourceMappingObjectPostProcessorBiFunctionMapping> {

    @Test
    public void testSelfGetAddLinks() {
        final String withLinks = "{\n" +
                "  \"a\": 1,\n" +
                "  \"b\": 2,\n" +
                "  \"_links\": [{\n" +
                "    \"href\": \"http://example.com/api/resource1/7b\",\n" +
                "    \"method\": \"GET\",\n" +
                "    \"rel\": \"self\",\n" +
                "    \"type\": \"application/json\"\n" +
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
                "    \"href\": \"http://example.com/api/resource1/7b\",\n" +
                "    \"method\": \"GET\",\n" +
                "    \"rel\": \"self\",\n" +
                "    \"type\": \"application/json\"\n" +
                "  }, {\n" +
                "    \"href\": \"http://example.com/api/resource1/7b\",\n" +
                "    \"method\": \"POST\",\n" +
                "    \"rel\": \"self\",\n" +
                "    \"type\": \"application/json\"\n" +
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
                "    \"href\": \"http://example.com/api/resource1/7b/contents\",\n" +
                "    \"method\": \"GET\",\n" +
                "    \"rel\": \"contents\",\n" +
                "    \"type\": \"application/json\"\n" +
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
                "    \"href\": \"http://example.com/api/resource1/7b\",\n" +
                "    \"method\": \"GET\",\n" +
                "    \"rel\": \"self\",\n" +
                "    \"type\": \"application/json\"\n" +
                "  }, {\n" +
                "    \"href\": \"http://example.com/api/resource1/7b\",\n" +
                "    \"method\": \"POST\",\n" +
                "    \"rel\": \"self\",\n" +
                "    \"type\": \"application/json\"\n" +
                "  }, {\n" +
                "    \"href\": \"http://example.com/api/resource1/7b/about\",\n" +
                "    \"method\": \"DELETE\",\n" +
                "    \"rel\": \"about\",\n" +
                "    \"type\": \"application/json\"\n" +
                "  }]\n" +
                "}";
        this.addLinksAndCheck(this.createMapping(Maps.of(LinkRelation.SELF, Sets.of(HttpMethod.GET, HttpMethod.POST),
                LinkRelation.ABOUT, Sets.of(HttpMethod.DELETE))),
                withLinks);
    }

    private void addLinksAndCheck(final HateosResourceMappingObjectPostProcessorBiFunctionMapping mapping,
                                  final String withLinks) {
        final String before = "{\"a\": 1, \"b\": 2}";
        assertEquals(JsonNode.parse(withLinks),
                mapping.addLinks(TestHateosResource.with(BigInteger.valueOf(123)),
                        JsonNode.parse(before).objectOrFail(),
                        Url.parseAbsolute("http://example.com/api"),
                        this.marshallContext()),
                () -> mapping.toString());
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createMapping(), "resource1, about=GET, contents=GET, POST");
    }

    private HateosResourceMappingObjectPostProcessorBiFunctionMapping createMapping() {
        return this.createMapping(Maps.of(LinkRelation.ABOUT, Sets.of(HttpMethod.GET), LinkRelation.CONTENTS, Sets.of(HttpMethod.GET, HttpMethod.POST)));
    }

    private HateosResourceMappingObjectPostProcessorBiFunctionMapping createMapping(final Map<LinkRelation<?>, Set<HttpMethod>> relationToMethods) {
        return HateosResourceMappingObjectPostProcessorBiFunctionMapping.with(HateosResourceName.with("resource1"), relationToMethods);
    }

    // TypeTesting......................................................................................................

    @Override
    public Class<HateosResourceMappingObjectPostProcessorBiFunctionMapping> type() {
        return HateosResourceMappingObjectPostProcessorBiFunctionMapping.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return HateosResourceMappingObjectPostProcessorBiFunction.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return "Mapping";
    }
}
