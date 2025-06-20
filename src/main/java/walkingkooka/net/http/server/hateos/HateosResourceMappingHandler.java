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

/**
 * A simple wrapper to hold either a {@link HateosHttpEntityHandler} or {@link HateosResourceHandler}
 */
abstract class HateosResourceMappingHandler<T> {

    /**
     * {@see HateosResourceMappingHandlerHateosHttpEntityHandler}
     */
    static HateosResourceMappingHandlerHateosHttpEntityHandler hateosHttpEntityHandler(final HateosHttpEntityHandler<?, ?> handler) {
        return HateosResourceMappingHandlerHateosHttpEntityHandler.with(handler);
    }


    /**
     * {see HateosResourceMappingHandlerHateosResourceHandler}
     */
    static HateosResourceMappingHandlerHateosResourceHandler hateosResourceHandler(final HateosResourceHandler<?, ?, ?, ?> handler) {
        return HateosResourceMappingHandlerHateosResourceHandler.with(handler);
    }

    HateosResourceMappingHandler(final T handler) {
        super();
        this.handler = handler;
    }

    abstract void handle(final HateosResourceMappingRouterHttpHandlerRequest request,
                         final HateosResourceMapping<?, ?, ?, ?, ?> mapping,
                         final HateosResourceSelection<?> selection,
                         final UrlPath path,
                         final HateosResourceHandlerContext context);

    final T handler;

    @Override
    public final int hashCode() {
        return this.handler.hashCode();
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
                null != other && this.getClass() == other.getClass()
                        && this.equals0((HateosResourceMappingHandler) other);
    }

    private boolean equals0(final HateosResourceMappingHandler<?> other) {
        return this.handler.equals(other.handler);
    }

    @Override
    public final String toString() {
        return this.handler.toString();
    }
}
