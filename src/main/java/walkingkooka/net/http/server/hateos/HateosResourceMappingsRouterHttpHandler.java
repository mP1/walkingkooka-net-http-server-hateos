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

import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpResponse;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * A {@link BiConsumer} which examines the request and then dispatches to the selected {@link HateosResourceHandler}.
 */
final class HateosResourceMappingsRouterHttpHandler<C extends HateosResourceHandlerContext> implements HttpHandler {

    /**
     * Factory called by {@link HateosResourceMappingsRouter#route}
     */
    static <C extends HateosResourceHandlerContext> HateosResourceMappingsRouterHttpHandler<C> with(final HateosResourceMappingsRouter<C> router,
                                                                                                    final C context) {
        return new HateosResourceMappingsRouterHttpHandler<>(
            router,
            context
        );
    }

    /**
     * Private ctor use factory.
     */
    private HateosResourceMappingsRouterHttpHandler(final HateosResourceMappingsRouter<C> router,
                                                    final C context) {
        super();
        this.router = router;
        this.context = context;
    }

    @Override
    public void handle(final HttpRequest request,
                       final HttpResponse response) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(response, "response");

        HateosResourceMappingsRouterHttpHandlerRequest.with(
            request,
            response,
            this.router,
            this.context
        ).dispatch();
    }

    private final HateosResourceMappingsRouter<C> router;

    private final C context;

    @Override
    public String toString() {
        return this.router.toString();
    }
}
