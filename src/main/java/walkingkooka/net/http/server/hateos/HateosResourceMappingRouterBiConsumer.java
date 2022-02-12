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

import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
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
    static <N extends Node<N, ?, ?, ?>> HateosResourceMappingRouterBiConsumer with(final HateosResourceMappingRouter router,
                                                                                   final Indentation indentation,
                                                                                   final LineEnding lineEnding) {
        return new HateosResourceMappingRouterBiConsumer(
                router,
                indentation,
                lineEnding
        );
    }

    /**
     * Private ctor use factory.
     */
    private HateosResourceMappingRouterBiConsumer(final HateosResourceMappingRouter router,
                                                  final Indentation indentation,
                                                  final LineEnding lineEnding) {
        super();
        this.router = router;
        this.indentation = indentation;
        this.lineEnding = lineEnding;
    }

    @Override
    public void accept(final HttpRequest request, final HttpResponse response) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(response, "response");

        HateosResourceMappingRouterBiConsumerRequest.with(
                request,
                response,
                this.router,
                this.indentation,
                this.lineEnding
        ).dispatch();
    }

    private final HateosResourceMappingRouter router;
    private final Indentation indentation;
    private final LineEnding lineEnding;

    @Override
    public String toString() {
        return this.router.toString();
    }
}
