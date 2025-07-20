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
import walkingkooka.net.UrlPath;
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.net.http.server.HttpHandlerTesting;

public final class HateosResourceMappingsRouterHttpHandlerTest extends HateosResourceMappingsTestCase<HateosResourceMappingsRouterHttpHandler>
        implements HttpHandlerTesting<HateosResourceMappingsRouterHttpHandler> {
    @Override
    public HateosResourceMappingsRouterHttpHandler createHttpHandler() {
        return HateosResourceMappingsRouterHttpHandler.with(
                HateosResourceMappingsRouter.with(
                        UrlPath.ROOT,
                        Sets.empty(),
                        HateosResourceHandlerContexts.fake()
                ),
                HateosResourceHandlerContexts.fake()
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final HateosResourceHandlerContext context = HateosResourceHandlerContexts.fake();

        final HateosResourceMappingsRouter router = HateosResourceMappingsRouter.with(
                UrlPath.ROOT,
                Sets.empty(),
                context
        );

        this.toStringAndCheck(
                HateosResourceMappingsRouterHttpHandler.with(
                        router,
                        context
                ),
                router.toString()
        );
    }

    // ClassTesting......................................................................................................

    @Override
    public Class<HateosResourceMappingsRouterHttpHandler> type() {
        return HateosResourceMappingsRouterHttpHandler.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return HateosResourceMappings.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return HttpHandler.class.getSimpleName();
    }
}
