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
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * A {@link BiConsumer} which examines the request and then dispatches to the selected {@link HateosResourceHandler}.
 */
final class HateosResourceMappingRouterHttpHandler implements HttpHandler {

    /**
     * Factory called by {@link HateosResourceMappingRouter#route}
     */
    static HateosResourceMappingRouterHttpHandler with(final HateosResourceMappingRouter router,
                                                       final Indentation indentation,
                                                       final LineEnding lineEnding,
                                                       final HateosResourceHandlerContext context) {
        return new HateosResourceMappingRouterHttpHandler(
                router,
                indentation,
                lineEnding,
                context
        );
    }

    /**
     * Private ctor use factory.
     */
    private HateosResourceMappingRouterHttpHandler(final HateosResourceMappingRouter router,
                                                   final Indentation indentation,
                                                   final LineEnding lineEnding,
                                                   final HateosResourceHandlerContext context) {
        super();
        this.router = router;
        this.indentation = indentation;
        this.lineEnding = lineEnding;
        this.context = context;
    }

    @Override
    public void handle(final HttpRequest request,
                       final HttpResponse response) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(response, "response");

        HateosResourceMappingRouterHttpHandlerRequest.with(
                request,
                response,
                this.router,
                this.indentation,
                this.lineEnding,
                this.context
        ).dispatch();
    }

    private final HateosResourceMappingRouter router;
    private final Indentation indentation;
    private final LineEnding lineEnding;

    private final HateosResourceHandlerContext context;

    @Override
    public String toString() {
        return this.router.toString();
    }
}
