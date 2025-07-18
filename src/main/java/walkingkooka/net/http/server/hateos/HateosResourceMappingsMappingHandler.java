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
import walkingkooka.net.http.server.HttpHandler;

/**
 * A simple wrapper to hold either a {@link HateosHttpEntityHandler} or {@link HateosResourceHandler}
 */
abstract class HateosResourceMappingsMappingHandler<I extends Comparable<I>, V, C, H extends HateosResource<I>, X extends HateosResourceHandlerContext> {

    /**
     * {@see HateosResourceMappingsMappingHandlerHateosHttpEntityHandler}
     */
    static <I extends Comparable<I>, V, C, H extends HateosResource<I>, X extends HateosResourceHandlerContext> HateosResourceMappingsMappingHandlerHateosHttpEntityHandler<I, V, C, H, X> hateosHttpEntityHandler(final HateosHttpEntityHandler<I, X> handler) {
        return HateosResourceMappingsMappingHandlerHateosHttpEntityHandler.with(handler);
    }


    /**
     * {see HateosResourceMappingsMappingHandlerHateosResourceHandler}
     */
    static <I extends Comparable<I>, V, C, H extends HateosResource<I>, X extends HateosResourceHandlerContext> HateosResourceMappingsMappingHandlerHateosResourceHandler<I, V, C, H, X> hateosResourceHandler(final HateosResourceHandler<I, V, C, X> handler) {
        return HateosResourceMappingsMappingHandlerHateosResourceHandler.with(handler);
    }

    /**
     * {@see HateosResourceMappingsMappingHandlerHttpHandler}
     */
    static <I extends Comparable<I>, V, C, H extends HateosResource<I>, X extends HateosResourceHandlerContext> HateosResourceMappingsMappingHandlerHttpHandler<I, V, C, H, X> httpHandler(final HttpHandler handler) {
        return HateosResourceMappingsMappingHandlerHttpHandler.with(handler);
    }

    HateosResourceMappingsMappingHandler() {
        super();
    }

    abstract void handle(final HateosResourceMappingsRouterHttpHandlerRequest<X> request,
                         final HateosResourceMappings<I, V, C, H, X> mappings,
                         final HateosResourceSelection<?> selection,
                         final UrlPath path,
                         final HateosResourceHandlerContext context);

    // Object...........................................................................................................

    @Override
    public final int hashCode() {
        return this.handler()
            .hashCode();
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
            null != other && this.getClass() == other.getClass()
                && this.equals0((HateosResourceMappingsMappingHandler<?, ?, ?, ?, ?>) other);
    }

    private boolean equals0(final HateosResourceMappingsMappingHandler<?, ?, ?, ?, ?> other) {
        return this.handler()
            .equals(other.handler());
    }

    @Override
    public final String toString() {
        return this.handler()
            .toString();
    }

    /**
     * Provides the handler (type is not important) which is used in {@link #hashCode()} and {@link #equals(Object)}.
     */
    abstract Object handler();
}
