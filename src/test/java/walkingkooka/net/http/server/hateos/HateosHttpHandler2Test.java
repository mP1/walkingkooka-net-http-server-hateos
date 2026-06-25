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
import walkingkooka.Cast;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.server.FakeHttpRequest;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.HttpResponses;
import walkingkooka.reflect.JavaVisibility;

public final class HateosHttpHandler2Test implements HateosHttpHandlerTesting<FakeHateosHttpHandler2<FakeHateosResourceHandlerContext>, FakeHateosResourceHandlerContext> {

    @Test
    public void testHandleConnect() {
        this.handleAndCheck(
            new FakeHateosHttpHandler2<>() {
                @Override
                public void handleConnect(final HttpRequest request,
                                          final HttpResponse response,
                                          final FakeHateosResourceHandlerContext context) {
                    HateosHttpHandler2Test.this.handled = true;
                }
            },
            HttpMethod.CONNECT
        );
    }

    @Test
    public void testHandleDelete() {
        this.handleAndCheck(
            new FakeHateosHttpHandler2<>() {
                @Override
                public void handleDelete(final HttpRequest request,
                                         final HttpResponse response,
                                         final FakeHateosResourceHandlerContext context) {
                    HateosHttpHandler2Test.this.handled = true;
                }
            },
            HttpMethod.DELETE
        );
    }

    @Test
    public void testHandleGet() {
        this.handleAndCheck(
            new FakeHateosHttpHandler2<>() {
                @Override
                public void handleGet(final HttpRequest request,
                                      final HttpResponse response,
                                      final FakeHateosResourceHandlerContext context) {
                    HateosHttpHandler2Test.this.handled = true;
                }
            },
            HttpMethod.GET
        );
    }

    @Test
    public void testHandleHead() {
        this.handleAndCheck(
            new FakeHateosHttpHandler2<>() {
                @Override
                public void handleHead(final HttpRequest request,
                                       final HttpResponse response,
                                       final FakeHateosResourceHandlerContext context) {
                    HateosHttpHandler2Test.this.handled = true;
                }
            },
            HttpMethod.HEAD
        );
    }

    @Test
    public void testHandlePatch() {
        this.handleAndCheck(
            new FakeHateosHttpHandler2<>() {
                @Override
                public void handlePatch(final HttpRequest request,
                                        final HttpResponse response,
                                        final FakeHateosResourceHandlerContext context) {
                    HateosHttpHandler2Test.this.handled = true;
                }
            },
            HttpMethod.PATCH
        );
    }

    @Test
    public void testHandlePost() {
        this.handleAndCheck(
            new FakeHateosHttpHandler2<>() {
                @Override
                public void handlePost(final HttpRequest request,
                                       final HttpResponse response,
                                       final FakeHateosResourceHandlerContext context) {
                    HateosHttpHandler2Test.this.handled = true;
                }
            },
            HttpMethod.POST
        );
    }

    @Test
    public void testHandleTrace() {
        this.handleAndCheck(
            new FakeHateosHttpHandler2<>() {
                @Override
                public void handleTrace(final HttpRequest request,
                                        final HttpResponse response,
                                        final FakeHateosResourceHandlerContext context) {
                    HateosHttpHandler2Test.this.handled = true;
                }
            },
            HttpMethod.TRACE
        );
    }

    @Test
    public void testHandleOther() {
        this.handleAndCheck(
            new FakeHateosHttpHandler2<>() {
                @Override
                public void handleOther(final HttpRequest request,
                                        final HttpResponse response,
                                        final FakeHateosResourceHandlerContext context) {
                    HateosHttpHandler2Test.this.handled = true;
                }
            },
            HttpMethod.with("Other")
        );
    }

    private void handleAndCheck(final FakeHateosHttpHandler2<FakeHateosResourceHandlerContext> handler,
                                final HttpMethod method) {
        this.handled = false;

        this.handleAndCheck(
            handler,
            new FakeHttpRequest() {

                @Override
                public HttpMethod method() {
                    return method;
                }
            },
            new FakeHateosResourceHandlerContext(),
            HttpResponses.recording()
        );

        this.checkEquals(
            true,
            this.handled,
            "handled"
        );
    }

    private boolean handled;

    @Override
    public FakeHateosHttpHandler2<FakeHateosResourceHandlerContext> createHateosHttpHandler() {
        return new FakeHateosHttpHandler2<>();
    }

    @Override
    public FakeHateosResourceHandlerContext context() {
        return new FakeHateosResourceHandlerContext();
    }

    // class............................................................................................................

    @Override
    public void testAllConstructorsVisibility() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<FakeHateosHttpHandler2<FakeHateosResourceHandlerContext>> type() {
        return Cast.to(FakeHateosHttpHandler2.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
