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

public final class HateosResourceMappingRouterHttpHandlerRequestTest extends HateosResourceMappingTestCase2<HateosResourceMappingRouterHttpHandlerRequest> {

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final Indentation indentation = Indentation.with("   ");
        final LineEnding lineEnding = LineEnding.NL;
        final HateosResourceHandlerContext context = HateosResourceHandlerContexts.fake();

        final HateosResourceMappingRouter router = HateosResourceMappingRouter.with(
                Url.parseAbsolute("https://example.com"),
                Sets.empty(),
                indentation,
                lineEnding,
                context
        );
        final HttpRequest request = HttpRequests.fake();
        final HttpResponse response = HttpResponses.fake();

        this.toStringAndCheck(
                HateosResourceMappingRouterHttpHandlerRequest.with(request, response, router, indentation, lineEnding, context),
                router + " " + request + " " + response + " indentation=\"   \" lineEndings=\"\\n\""
        );
    }

    // ClassTesting......................................................................................................

    @Override
    public Class<HateosResourceMappingRouterHttpHandlerRequest> type() {
        return HateosResourceMappingRouterHttpHandlerRequest.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return HateosResourceMappingRouterHttpHandler.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return "Request";
    }
}
