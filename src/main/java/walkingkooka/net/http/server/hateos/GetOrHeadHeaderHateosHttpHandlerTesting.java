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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.server.FakeHttpRequest;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.HttpResponses;

/**
 * Mixin interface for testing {@link GetOrHeadHeaderHateosHttpHandler}
 */
public interface GetOrHeadHeaderHateosHttpHandlerTesting<H extends GetOrHeadHeaderHateosHttpHandler<C>, C extends HateosResourceHandlerContext> extends HateosHttpHandlerTesting<H, C> {

    @Test
    default void testHandlePost() {
        final HttpResponse expected = HttpResponses.recording();
        expected.setMethodNotAllowed(
            HttpMethod.POST,
            Lists.of(
                HttpMethod.GET,
                HttpMethod.HEAD
            )
        );

        this.handleAndCheck(
            this.createHateosHttpHandler(),
            new FakeHttpRequest() {
                @Override
                public HttpMethod method() {
                    return HttpMethod.POST;
                }
            },
            this.context(),
            expected
        );
    }
}
