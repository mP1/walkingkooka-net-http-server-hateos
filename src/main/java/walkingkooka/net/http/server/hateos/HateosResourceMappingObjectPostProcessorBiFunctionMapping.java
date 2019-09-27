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

import walkingkooka.ToStringBuilder;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.UrlPath;
import walkingkooka.net.UrlPathName;
import walkingkooka.net.header.Link;
import walkingkooka.net.header.LinkParameterName;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeName;
import walkingkooka.tree.json.JsonObjectNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A value class that holds numerous components to build links for a {@link HateosResourceName}.
 */
final class HateosResourceMappingObjectPostProcessorBiFunctionMapping {

    static HateosResourceMappingObjectPostProcessorBiFunctionMapping with(final HateosResourceName name,
                                                                          final Map<LinkRelation<?>, Set<HttpMethod>> relationToMethods) {
        return new HateosResourceMappingObjectPostProcessorBiFunctionMapping(name, relationToMethods);
    }

    private HateosResourceMappingObjectPostProcessorBiFunctionMapping(final HateosResourceName name,
                                                                      final Map<LinkRelation<?>, Set<HttpMethod>> relationToMethods) {
        super();
        this.name = UrlPathName.with(name.value());
        this.relationToMethods = relationToMethods;
    }

    JsonObjectNode addLinks(final HateosResource<?> resource,
                            final JsonObjectNode object,
                            final AbsoluteUrl base,
                            final JsonNodeMarshallContext context) {
        // base + resource name.
        final UrlPath pathAndResourceNameAndId = base.path()
                .append(this.name)
                .append(UrlPathName.with(resource.hateosLinkId()));
        final List<JsonNode> links = Lists.array();

        for (Entry<LinkRelation<?>, Set<HttpMethod>> relationToMethods : this.relationToMethods.entrySet()) {
            final LinkRelation<?> relation = relationToMethods.getKey();

            for (HttpMethod method : relationToMethods.getValue()) {
                // TODO add support for title/title* and hreflang
                final Map<LinkParameterName<?>, Object> parameters = Maps.of(LinkParameterName.METHOD, method,
                        LinkParameterName.REL, Lists.of(relation),
                        LinkParameterName.TYPE, HateosContentTypeJsonNode.JSON_CONTENT_TYPE);

                links.add(context.marshall(Link.with(base.setPath(LinkRelation.SELF == relation ?
                        pathAndResourceNameAndId :
                        pathAndResourceNameAndId.append(UrlPathName.with(relation.value().toString()))))
                        .setParameters(parameters)));
            }

        }

        return object.set(LINKS, JsonNode.array().setChildren(links));
    }

    /**
     * The property that receives the actual links.
     */
    private final static JsonNodeName LINKS = JsonNodeName.with("_links");

    private final UrlPathName name;
    private final Map<LinkRelation<?>, Set<HttpMethod>> relationToMethods;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .valueSeparator(", ")
                .separator(", ")
                .value(this.name)
                .value(this.relationToMethods)
                .build();
    }
}
