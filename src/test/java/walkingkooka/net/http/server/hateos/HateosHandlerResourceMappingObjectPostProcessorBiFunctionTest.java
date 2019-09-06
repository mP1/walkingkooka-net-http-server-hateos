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
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObjectNode;
import walkingkooka.tree.json.marshall.ToJsonNodeContext;
import walkingkooka.tree.json.marshall.ToJsonNodeContexts;
import walkingkooka.util.BiFunctionTesting;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HateosHandlerResourceMappingObjectPostProcessorBiFunctionTest extends HateosHandlerResourceMappingTestCase2<HateosHandlerResourceMappingObjectPostProcessorBiFunction>
        implements BiFunctionTesting<HateosHandlerResourceMappingObjectPostProcessorBiFunction, Object, JsonObjectNode, JsonObjectNode> {

    @Test
    public void testWithNullBaseUrlFails() {
        this.withFails(null,
                this.mappings(),
                this.resourceNameToTypes(),
                this.toJsonNodeContext());
    }

    @Test
    public void testWithNullMappingsFails() {
        this.withFails(this.baseUrl(),
                null,
                this.resourceNameToTypes(),
                this.toJsonNodeContext());
    }

    @Test
    public void testWithNullResourceNameToTypesFails() {
        this.withFails(this.baseUrl(),
                this.mappings(),
                null,
                this.toJsonNodeContext());
    }

    @Test
    public void testWithNullToJsonNodeContextFails() {
        this.withFails(this.baseUrl(),
                this.mappings(),
                this.resourceNameToTypes(),
                null);
    }

    private void withFails(final AbsoluteUrl base,
                           final Set<HateosHandlerResourceMapping<?, ?, ?>> mappings,
                           final Map<HateosResourceName, Class<?>> resourceNameToType,
                           final ToJsonNodeContext context) {
        assertThrows(NullPointerException.class, () -> {
            HateosHandlerResourceMappingObjectPostProcessorBiFunction.with(base,
                    mappings,
                    resourceNameToType,
                    context);
        });
    }

    @Test
    public void testWithMissingResourceNameFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            HateosHandlerResourceMappingObjectPostProcessorBiFunction.with(this.baseUrl(),
                    this.mappings(),
                    Maps.of(this.resourceName1(), TestHateosResource.class),
                    this.toJsonNodeContext());
        });
    }

    // apply............................................................................................................

    @Test
    public void testHateosResourceWithoutLinks() {
        final StringName without = Names.string("1");
        this.toJsonNodeAndCheck(without, this.toJsonNodeContext().toJsonNode(without));
    }

    @Test
    public void testHateosResourceWithLinks() {
        final TestHateosResource resource = TestHateosResource.with(BigInteger.valueOf(123));
        this.toJsonNodeAndCheck(resource,
                JsonNode.parse("{\n" +
                        "  \"id\": \"123\",\n" +
                        "  \"_links\": [{\n" +
                        "    \"href\": \"http://example.com/api/resource-1/7b/contents\",\n" +
                        "    \"method\": \"GET\",\n" +
                        "    \"rel\": \"contents\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api/resource-1/7b/contents\",\n" +
                        "    \"method\": \"POST\",\n" +
                        "    \"rel\": \"contents\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }, {\n" +
                        "    \"href\": \"http://example.com/api/resource-1/7b\",\n" +
                        "    \"method\": \"POST\",\n" +
                        "    \"rel\": \"self\",\n" +
                        "    \"type\": \"application/hal+json\"\n" +
                        "  }]\n" +
                        "}"));
    }

    private void toJsonNodeAndCheck(final Object resource,
                                    final JsonNode json) {
        assertEquals(json,
                ToJsonNodeContexts.basic()
                        .setObjectPostProcessor(this.createBiFunction()).toJsonNode(resource),
                () -> resource.toString());
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createBiFunction(),
                "{walkingkooka.net.http.server.hateos.TestHateosResource=resource-1, contents=GET, POST, self=POST, walkingkooka.net.http.server.hateos.TestHateosResource3=resource-2, about=PUT}");
    }

    // BiFunction.......................................................................................................

    @Override
    public HateosHandlerResourceMappingObjectPostProcessorBiFunction createBiFunction() {
        return HateosHandlerResourceMappingObjectPostProcessorBiFunction.with(this.baseUrl(),
                this.mappings(),
                this.resourceNameToTypes(),
                this.toJsonNodeContext());
    }

    private AbsoluteUrl baseUrl() {
        return Url.parseAbsolute("http://example.com/api");
    }

    private HateosResourceName resourceName1() {
        return HateosResourceName.with("resource-1");
    }

    private HateosResourceName resourceName2() {
        return HateosResourceName.with("resource-2");
    }

    private Set<HateosHandlerResourceMapping<?, ?, ?>> mappings() {
        final HateosResourceName resourceName1 = this.resourceName1();
        final HateosResourceName resourceName2 = this.resourceName2();

        final HateosHandlerResourceMapping mapping1 = HateosHandlerResourceMapping.with(resourceName1,
                BigInteger::new,
                TestHateosResource.class,
                TestHateosResource2.class)
                .set(LinkRelation.CONTENTS, HttpMethod.GET, new FakeHateosHandler<>())
                .set(LinkRelation.CONTENTS, HttpMethod.POST, new FakeHateosHandler<>())
                .set(LinkRelation.SELF, HttpMethod.POST, new FakeHateosHandler<>());

        final HateosHandlerResourceMapping mapping2 = HateosHandlerResourceMapping.with(resourceName2,
                BigInteger::new,
                TestHateosResource3.class,
                TestHateosResource4.class)
                .set(LinkRelation.ABOUT, HttpMethod.PUT, new FakeHateosHandler<>());

        return Sets.of(mapping1, mapping2);
    }

    private Map<HateosResourceName, Class<?>> resourceNameToTypes() {
        return Maps.of(this.resourceName1(), TestHateosResource.class, this.resourceName2(), TestHateosResource3.class);
    }

    // ClassTesting......................................................................................................

    @Override
    public Class<HateosHandlerResourceMappingObjectPostProcessorBiFunction> type() {
        return HateosHandlerResourceMappingObjectPostProcessorBiFunction.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return HateosHandlerResourceMapping.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return BiFunction.class.getSimpleName();
    }
}
