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

import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.tree.Node;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * A {@link BiConsumer} which examines the request and then dispatches to the selected {@link HateosHandler}.
 */
final class HateosResourceMappingRouterBiConsumer implements BiConsumer<HttpRequest, HttpResponse> {

    /**
     * Factory called by {@link HateosResourceMappingRouter#route}
     */
    static <N extends Node<N, ?, ?, ?>> HateosResourceMappingRouterBiConsumer with(final HateosResourceMappingRouter router) {
        return new HateosResourceMappingRouterBiConsumer(router);
    }

    /**
     * Private ctor use factory.
     */
    private HateosResourceMappingRouterBiConsumer(final HateosResourceMappingRouter router) {
        super();
        this.router = router;
    }

    @Override
    public void accept(final HttpRequest request, final HttpResponse response) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(response, "response");

        try {
            HateosResourceMappingRouterBiConsumerRequest.with(request, response, this.router)
                    .dispatch();
        } catch (final UnsupportedOperationException unsupported) {
            handleFailure(response, HttpStatusCode.NOT_IMPLEMENTED, unsupported);
        } catch (final IllegalArgumentException badRequest) {
            handleFailure(response, HttpStatusCode.BAD_REQUEST, badRequest);
        } catch (final RuntimeException internal) {
            handleFailure(response, HttpStatusCode.INTERNAL_SERVER_ERROR, internal);
        }
    }

    /**
     * Updates the response with the given code and the message from the {@link Throwable}. The body of the response is set to the stacktrace.
     */
    private static void handleFailure(final HttpResponse response,
                                      final HttpStatusCode code,
                                      final Throwable cause) {
        final String message = cause.getMessage();
        response.setStatus(
                code.setMessageOrDefault(
                        null != message ?
                        HttpStatus.firstLineOfText(cause.getMessage()) :
                        message
                )
        );
        response.addEntity(HttpEntity.dumpStackTrace(cause));
    }

    private final HateosResourceMappingRouter router;

    @Override
    public String toString() {
        return this.router.toString();
    }
}
