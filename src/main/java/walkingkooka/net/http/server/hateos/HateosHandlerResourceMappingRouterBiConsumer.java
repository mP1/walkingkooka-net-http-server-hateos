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

import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.tree.Node;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * A {@link BiConsumer} which examines the request and then dispatches to the selected {@link HateosHandler}.
 */
final class HateosHandlerResourceMappingRouterBiConsumer implements BiConsumer<HttpRequest, HttpResponse> {

    /**
     * Factory called by {@link HateosHandlerResourceMappingRouter#route}
     */
    static <N extends Node<N, ?, ?, ?>> HateosHandlerResourceMappingRouterBiConsumer with(final HateosHandlerResourceMappingRouter router) {
        return new HateosHandlerResourceMappingRouterBiConsumer(router);
    }

    /**
     * Private ctor use factory.
     */
    private HateosHandlerResourceMappingRouterBiConsumer(final HateosHandlerResourceMappingRouter router) {
        super();
        this.router = router;
    }

    /**
     * Dispatches the request to {@link HateosHandlerRouterRequestBiConsumerHttpMethodVisitor}.
     */
    @Override
    public void accept(final HttpRequest request, final HttpResponse response) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(response, "response");

        try {
            HateosHandlerResourceMappingRouterBiConsumerRequest.with(request, response, this.router)
                    .dispatch();
        } catch (final UnsupportedOperationException unsupported) {
            response.setStatus(HttpStatusCode.NOT_IMPLEMENTED.setMessageOrDefault(unsupported.getMessage()));
        } catch (final IllegalArgumentException badRequest) {
            response.setStatus(HttpStatusCode.BAD_REQUEST.setMessageOrDefault(badRequest.getMessage()));
        } catch (final RuntimeException internal) {
            response.setStatus(HttpStatusCode.INTERNAL_SERVER_ERROR.setMessageOrDefault(internal.getMessage()));
        }
    }

    private final HateosHandlerResourceMappingRouter router;

    @Override
    public String toString() {
        return this.router.toString();
    }
}
