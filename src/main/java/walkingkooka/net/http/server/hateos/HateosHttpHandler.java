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

/**
 * A http handler that may be added to a {@link HateosResourceMappings}.
 */
public interface HateosHttpHandler<C extends HateosResourceHandlerContext> {

    void handle(final HttpRequest request,
                final HttpResponse response,
                final C context);
}
