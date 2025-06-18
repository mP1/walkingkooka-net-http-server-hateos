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
import walkingkooka.net.http.server.HttpRequestAttribute;

import java.util.Map;
import java.util.Optional;

/**
 * A {@link HateosResourceHandler#handleAll(Optional, Map, UrlPath, HateosResourceHandlerContext)} that throws {@link UnsupportedOperationException}.
 */
public interface UnsupportedHateosResourceHandlerHandleAll<I extends Comparable<I>, V, C, X extends HateosResourceHandlerContext> extends HateosResourceHandler<I, V, C, X> {

    @Override
    default Optional<C> handleAll(final Optional<C> resource,
                                  final Map<HttpRequestAttribute<?>, Object> parameters,
                                  final UrlPath path,
                                  final X context) {
        HateosResourceHandler.checkResource(resource);
        HateosResourceHandler.checkParameters(parameters);
        HateosResourceHandler.checkPath(path);
        HateosResourceHandler.checkContext(context);

        throw new UnsupportedOperationException();
    }
}
