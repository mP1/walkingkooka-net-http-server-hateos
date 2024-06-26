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
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

public final class HateosResourceMappingRouterHttpHandlerTest extends HateosResourceMappingTestCase2<HateosResourceMappingRouterHttpHandler> {

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

        this.toStringAndCheck(
                HateosResourceMappingRouterHttpHandler.with(
                        router,
                        indentation,
                        lineEnding,
                        context
                ),
                router.toString()
        );
    }

    // ClassTesting......................................................................................................

    @Override
    public Class<HateosResourceMappingRouterHttpHandler> type() {
        return HateosResourceMappingRouterHttpHandler.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return HateosResourceMapping.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return HttpHandler.class.getSimpleName();
    }
}
