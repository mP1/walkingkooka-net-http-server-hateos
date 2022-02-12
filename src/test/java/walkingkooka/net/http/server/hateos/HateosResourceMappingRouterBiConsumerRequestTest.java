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
import walkingkooka.net.Url;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequests;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.HttpResponses;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

public final class HateosResourceMappingRouterBiConsumerRequestTest extends HateosResourceMappingTestCase2<HateosResourceMappingRouterBiConsumerRequest> {

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final Indentation indentation = Indentation.with("   ");
        final LineEnding lineEnding = LineEnding.NL;

        final HateosResourceMappingRouter router = HateosResourceMappingRouter.with(
                Url.parseAbsolute("http://example.com"),
                HateosContentType.json(JsonNodeUnmarshallContexts.fake(), JsonNodeMarshallContexts.fake()),
                Sets.empty(),
                indentation,
                lineEnding
        );
        final HttpRequest request = HttpRequests.fake();
        final HttpResponse response = HttpResponses.fake();

        this.toStringAndCheck(
                HateosResourceMappingRouterBiConsumerRequest.with(request, response, router, indentation, lineEnding),
                router + " " + request + " " + response + " indentation=\"   \" lineEndings=\"\\n\""
        );
    }

    // ClassTesting......................................................................................................

    @Override
    public Class<HateosResourceMappingRouterBiConsumerRequest> type() {
        return HateosResourceMappingRouterBiConsumerRequest.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return HateosResourceMappingRouterBiConsumer.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return "Request";
    }
}
