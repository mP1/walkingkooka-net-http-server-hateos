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

import walkingkooka.net.UrlPath;

import java.util.Objects;

final class HateosResourceMappingsMappingHandlerHateosResourceHandler<I extends Comparable<I>, V, C, H extends HateosResource<I>, X extends HateosResourceHandlerContext> extends HateosResourceMappingsMappingHandler<I, V, C, H, X> {

    static <I extends Comparable<I>, V, C, H extends HateosResource<I>, X extends HateosResourceHandlerContext> HateosResourceMappingsMappingHandlerHateosResourceHandler<I, V, C, H, X> with(final HateosResourceHandler<I, V, C, X> handler) {
        return new HateosResourceMappingsMappingHandlerHateosResourceHandler<>(
            Objects.requireNonNull(handler, "handler")
        );
    }

    private HateosResourceMappingsMappingHandlerHateosResourceHandler(final HateosResourceHandler<I, V, C, X> handler) {
        super();
        this.handler = handler;
    }

    @Override
    void handle(final HateosResourceMappingsRouterHttpHandlerRequest<X> request,
                final HateosResourceMappings<I, V, C, H, X> mappings,
                final HateosResourceSelection<?> selection,
                final UrlPath path,
                final HateosResourceHandlerContext context) {
        request.handleHateosResourceHandler(
            this.handler,
            mappings,
            selection,
            path,
            context
        );
    }

    @Override
    HateosResourceHandler<I, V, C, X> handler() {
        return this.handler;
    }

    private final HateosResourceHandler<I, V, C, X> handler;
}
