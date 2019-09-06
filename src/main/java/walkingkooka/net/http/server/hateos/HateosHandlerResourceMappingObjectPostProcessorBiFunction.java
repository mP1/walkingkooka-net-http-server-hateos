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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.tree.json.JsonObjectNode;
import walkingkooka.tree.json.marshall.ToJsonNodeContext;
import walkingkooka.tree.json.marshall.ToJsonNodeContexts;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * A {@link BiFunction} that adds links for types, and can be used by {@link ToJsonNodeContexts#basic}
 */
final class HateosHandlerResourceMappingObjectPostProcessorBiFunction implements BiFunction<Object, JsonObjectNode, JsonObjectNode> {

    static HateosHandlerResourceMappingObjectPostProcessorBiFunction with(final AbsoluteUrl base,
                                                                          final Set<HateosHandlerResourceMapping<?, ?, ?>> mappings,
                                                                          final Map<HateosResourceName, Class<?>> resourceNameToTypes,
                                                                          final ToJsonNodeContext context) {
        Objects.requireNonNull(base, "base");
        Objects.requireNonNull(mappings, "mappings");
        Objects.requireNonNull(resourceNameToTypes, "resourceNameToTypes");
        Objects.requireNonNull(context, "context");

        final Map<String, HateosHandlerResourceMappingObjectPostProcessorBiFunctionMapping> typeToMappings = Maps.ordered();
        final List<HateosResourceName> missing = Lists.array();

        for (HateosHandlerResourceMapping<?, ?, ?> mapping : mappings) {
            final HateosResourceName resourceName = mapping.resourceName;
            final Class<?> type = resourceNameToTypes.get(resourceName);
            if (null == type) {
                missing.add(resourceName);
                continue;
            }

            final Map<LinkRelation<?>, Set<HttpMethod>> relationToMethods = Maps.ordered();

            for (HateosHandlerResourceMappingLinkRelationHttpMethod relationAndMethod : mapping.relationAndMethodToHandlers.keySet()) {
                final LinkRelation<?> relation = relationAndMethod.relation;
                Set<HttpMethod> methods = relationToMethods.get(relation);
                if (null == methods) {
                    methods = Sets.ordered();
                    relationToMethods.put(relation, methods);
                }
                methods.add(relationAndMethod.method);
            }

            typeToMappings.put(type.getName(),
                    HateosHandlerResourceMappingObjectPostProcessorBiFunctionMapping.with(resourceName, relationToMethods));
        }

        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Resources missing " + HateosResourceName.class.getSimpleName() +
                    " type mapping: " +
                    missing.stream()
                            .map(HateosResourceName::value)
                            .collect(Collectors.joining(",")));
        }

        return new HateosHandlerResourceMappingObjectPostProcessorBiFunction(base,
                typeToMappings,
                context);
    }

    private HateosHandlerResourceMappingObjectPostProcessorBiFunction(final AbsoluteUrl base,
                                                                      final Map<String, HateosHandlerResourceMappingObjectPostProcessorBiFunctionMapping> typeToMappings,
                                                                      final ToJsonNodeContext context) {
        super();
        this.base = base;
        this.typeToMappings = typeToMappings;
        this.context = context;
    }

    @Override
    public JsonObjectNode apply(final Object value,
                                final JsonObjectNode object) {
        Class<?> type = value.getClass();
        HateosHandlerResourceMappingObjectPostProcessorBiFunctionMapping mapping;

        do {
            mapping = this.typeToMappings.get(type.getName());
            if (null != mapping) {
                break;
            }
            type = type.getSuperclass();
        } while (Object.class != type);

        return null != mapping ?
                mapping.addLinks((HateosResource) value, object, this.base, this.context) :
                object;
    }

    private final Map<String, HateosHandlerResourceMappingObjectPostProcessorBiFunctionMapping> typeToMappings;
    private final AbsoluteUrl base;
    private final ToJsonNodeContext context;

    @Override
    public String toString() {
        return this.typeToMappings.toString();
    }
}
