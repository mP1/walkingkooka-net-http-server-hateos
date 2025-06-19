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

final class HateosResourceMappingHandlerHateosHttpEntityHandler extends HateosResourceMappingHandler {

    static HateosResourceMappingHandlerHateosHttpEntityHandler with(final HateosHttpEntityHandler<?, ?> handler) {
        return new HateosResourceMappingHandlerHateosHttpEntityHandler(
                Objects.requireNonNull(handler, "handler")
        );
    }

    private HateosResourceMappingHandlerHateosHttpEntityHandler(final HateosHttpEntityHandler<?, ?> handler) {
        this.handler = handler;
    }

    @Override
    void handle(final HateosResourceMappingRouterHttpHandlerRequest request,
                final HateosResourceMapping<?, ?, ?, ?, ?> mapping,
                final HateosResourceSelection<?> selection,
                final UrlPath path,
                final HateosResourceHandlerContext context) {
        request.handleHateosHttpEntityHandler(
                this.handler,
                selection,
                path,
                context
        );
    }

    private final HateosHttpEntityHandler<?, ?> handler;

    @Override
    public int hashCode() {
        return this.handler.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof HateosResourceMappingHandlerHateosHttpEntityHandler && this.equals0((HateosResourceMappingHandlerHateosHttpEntityHandler) other);
    }

    private boolean equals0(final HateosResourceMappingHandlerHateosHttpEntityHandler other) {
        return this.handler.equals(other.handler);
    }

    @Override
    public String toString() {
        return this.handler.toString();
    }
}
