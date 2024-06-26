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

import walkingkooka.Cast;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;

import java.util.Map;
import java.util.Set;

/**
 * A {@link JsonNodeMarshallContextObjectPostProcessor} that adds links for types, and can be used by {@link JsonNodeMarshallContexts#basic}
 */
final class HateosResourceMappingJsonNodeMarshallContextObjectPostProcessor implements JsonNodeMarshallContextObjectPostProcessor {

    static HateosResourceMappingJsonNodeMarshallContextObjectPostProcessor with(final AbsoluteUrl base,
                                                                                final Set<HateosResourceMapping<?, ?, ?, ?, ?>> mappings,
                                                                                final HateosResourceHandlerContext context) {
        final Map<String, HateosResourceMappingJsonNodeMarshallContextObjectPostProcessorMapping> typeToMappings = Maps.ordered();

        for (HateosResourceMapping<?, ?, ?, ?, ?> mapping : mappings) {
            final HateosResourceName resourceName = mapping.resourceName;
            final Map<LinkRelation<?>, Set<HttpMethod>> relationToMethods = Maps.ordered();

            for (HateosResourceMappingLinkRelationHttpMethod relationAndMethod : mapping.relationAndMethodToHandlers.keySet()) {
                final LinkRelation<?> relation = relationAndMethod.relation;
                Set<HttpMethod> methods = relationToMethods.get(relation);
                if (null == methods) {
                    methods = Sets.ordered();
                    relationToMethods.put(relation, methods);
                }
                methods.add(relationAndMethod.method);
            }

            typeToMappings.put(mapping.resourceType.getName(),
                    HateosResourceMappingJsonNodeMarshallContextObjectPostProcessorMapping.with(resourceName, relationToMethods));
        }

        return new HateosResourceMappingJsonNodeMarshallContextObjectPostProcessor(base,
                typeToMappings,
                context);
    }

    private HateosResourceMappingJsonNodeMarshallContextObjectPostProcessor(final AbsoluteUrl base,
                                                                            final Map<String, HateosResourceMappingJsonNodeMarshallContextObjectPostProcessorMapping> typeToMappings,
                                                                            final HateosResourceHandlerContext context) {
        super();
        this.base = base;
        this.typeToMappings = typeToMappings;
        this.context = context;
    }

    @Override
    public JsonObject apply(final Object value,
                            final JsonObject object) {
        Class<?> type = value.getClass();
        HateosResourceMappingJsonNodeMarshallContextObjectPostProcessorMapping mapping;

        do {
            mapping = this.typeToMappings.get(type.getName());
            if (null != mapping) {
                break;
            }
            type = type.getSuperclass();
        } while (Object.class != type);

        return null != mapping ?
                mapping.addLinks(Cast.to(value), object, this.base, this.context) :
                object;
    }

    private final Map<String, HateosResourceMappingJsonNodeMarshallContextObjectPostProcessorMapping> typeToMappings;
    private final AbsoluteUrl base;
    private final HateosResourceHandlerContext context;

    @Override
    public String toString() {
        return this.typeToMappings.toString();
    }
}
