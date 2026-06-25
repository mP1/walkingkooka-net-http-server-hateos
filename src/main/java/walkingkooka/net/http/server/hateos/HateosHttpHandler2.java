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

import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpResponse;

import java.util.Objects;

/**
 * A {@link walkingkooka.net.http.server.HttpHandler} that examines the {@link HttpMethod} and dispatches to the matching
 * method.
 */
public interface HateosHttpHandler2<C extends HateosResourceHandlerContext> extends HateosHttpHandler<C> {

    @Override
    default void handle(final HttpRequest request,
                        final HttpResponse response,
                        final C context) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(response, "response");
        Objects.requireNonNull(context, "context");

        final HttpMethod method = request.method();
        switch (method.value().toLowerCase()) {
            case HttpMethod.CONNECT_STRING:
                this.handleConnect(
                    request,
                    response,
                    context
                );
                break;
            case HttpMethod.DELETE_STRING:
                this.handleDelete(
                    request,
                    response,
                    context
                );
                break;
            case HttpMethod.GET_STRING:
                this.handleGet(
                    request,
                    response,
                    context
                );
                break;
            case HttpMethod.HEAD_STRING:
                this.handleHead(
                    request,
                    response,
                    context
                );
                break;
            case HttpMethod.PATCH_STRING:
                this.handlePatch(
                    request,
                    response,
                    context
                );
                break;
            case HttpMethod.POST_STRING:
                this.handlePost(
                    request,
                    response,
                    context
                );
                break;
            default:
                this.handleOther(
                    request,
                    response,
                    context
                );
                break;
        }
    }

    void handleConnect(final HttpRequest request,
                       final HttpResponse response,
                       final C context);

    void handleDelete(final HttpRequest request,
                      final HttpResponse response,
                      final C context);

    void handleGet(final HttpRequest request,
                   final HttpResponse response,
                   final C context);

    void handleHead(final HttpRequest request,
                    final HttpResponse response,
                    final C context);

    void handleOther(final HttpRequest request,
                     final HttpResponse response,
                     final C context);

    void handlePatch(final HttpRequest request,
                     final HttpResponse response,
                     final C context);

    void handlePost(final HttpRequest request,
                    final HttpResponse response,
                    final C context);
}
