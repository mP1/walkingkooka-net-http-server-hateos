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
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A value class that holds numerous components to build links for a {@link HateosResourceName}.
 */
final class HateosResourceMappingsJsonNodeMarshallContextObjectPostProcessorMapping {

    static HateosResourceMappingsJsonNodeMarshallContextObjectPostProcessorMapping with(final HateosResourceName name,
                                                                                        final Map<LinkRelation<?>, Collection<HttpMethod>> linkRelationToMethods) {
        return new HateosResourceMappingsJsonNodeMarshallContextObjectPostProcessorMapping(name, linkRelationToMethods);
    }

    private HateosResourceMappingsJsonNodeMarshallContextObjectPostProcessorMapping(final HateosResourceName name,
                                                                                    final Map<LinkRelation<?>, Collection<HttpMethod>> linkRelationToMethods) {
        super();
        this.name = UrlPathName.with(name.value());
        this.linkRelationToMethods = linkRelationToMethods;
    }

    JsonObject addLinks(final HateosResource<?> resource,
                        final JsonObject object,
                        final AbsoluteUrl base,
                        final HateosResourceHandlerContext context) {
        // base + resource name.
        final UrlPath pathAndResourceNameAndId = base.path()
                .append(this.name)
                .append(UrlPathName.with(resource.hateosLinkId()));
        final List<JsonNode> links = Lists.array();

        for (Entry<LinkRelation<?>, Collection<HttpMethod>> linkRelationToMethods : this.linkRelationToMethods.entrySet()) {
            final LinkRelation<?> relation = linkRelationToMethods.getKey();

            for (HttpMethod method : linkRelationToMethods.getValue()) {
                // TODO add support for title/title* and hreflang
                final Map<LinkParameterName<?>, Object> parameters = Maps.of(
                        LinkParameterName.METHOD, method,
                        LinkParameterName.REL, Lists.of(relation),
                        LinkParameterName.TYPE, context.contentType()
                );

                links.add(
                        context.marshall(Link.with(base.setPath(LinkRelation.SELF == relation ?
                                                        pathAndResourceNameAndId :
                                                        pathAndResourceNameAndId.append(
                                                                UrlPathName.with(
                                                                        relation.value()
                                                                                .toString()
                                                                )
                                                        )
                                                )
                                        )
                                        .setParameters(parameters)
                        )
                );
            }

        }

        return object.set(
                LINKS,
                JsonNode.array()
                        .setChildren(links)
        );
    }

    /**
     * The property that receives the actual links.
     */
    private final static JsonPropertyName LINKS = JsonPropertyName.with("_links");

    private final UrlPathName name;
    private final Map<LinkRelation<?>, Collection<HttpMethod>> linkRelationToMethods;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .valueSeparator(", ")
                .separator(", ")
                .value(this.name)
                .value(this.linkRelationToMethods)
                .build();
    }
}
