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

import walkingkooka.collect.list.Lists;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpResponse;

import java.util.Objects;

/**
 * A {@link HateosHttpHandler} that only allows {@link HttpMethod#GET} and {@link HttpMethod#HEAD} methods, returning
 * {@link walkingkooka.net.http.HttpStatusCode#METHOD_NOT_ALLOWED} for all other {@link HttpMethod}
 */
public interface GetOrHeadHeaderHateosHttpHandler<C extends HateosResourceHandlerContext> extends HateosHttpHandler<C> {

    @Override
    default void handle(final HttpRequest request,
                        final HttpResponse response,
                        final C context) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(response, "response");
        Objects.requireNonNull(context, "context");

        final HttpMethod httpMethod = request.method();
        if (httpMethod.isGetOrHead()) {
            this.handleGetOrHead(
                request,
                response,
                context
            );
        } else {
            response.setMethodNotAllowed(
                httpMethod,
                Lists.of(
                    HttpMethod.GET,
                    HttpMethod.HEAD
                )
            );
        }
    }

    /**
     * This method is invoked if the method is a {@link HttpMethod#GET} or {@link HttpMethod#HEAD}
     */
    void handleGetOrHead(final HttpRequest request,
                         final HttpResponse response,
                         final C context);
}
