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
import walkingkooka.net.Url;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequests;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.HttpResponses;
import walkingkooka.net.http.server.hateos.HateosHttpHandlerTestingTest.TestHateosHttpHandler;
import walkingkooka.reflect.JavaVisibility;

import java.util.Objects;

public final class HateosHttpHandlerTestingTest implements HateosHttpHandlerTesting<TestHateosHttpHandler, FakeHateosResourceHandlerContext> {

    @Test
    public void testHandleAndCheck() {
        final String bodyText = "Hello World";

        final HttpResponse response = HttpResponses.recording();
        response.setStatus(HttpStatusCode.OK.status());
        response.setEntity(
            HttpEntity.EMPTY.setBodyText(bodyText)
        );

        this.handleAndCheck(
            this.createHateosHttpHandler(),
            HttpRequests.get(
                HttpTransport.UNSECURED,
                Url.parseRelative("/path1/file2.txt"),
                HttpProtocolVersion.VERSION_1_0,
                HttpEntity.EMPTY.setBodyText(bodyText)
            ),
            new FakeHateosResourceHandlerContext(),
            response
        );
    }

    @Override
    public void testAllConstructorsVisibility() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testIfClassIsFinalIfAllConstructorsArePrivate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestHateosHttpHandler createHateosHttpHandler() {
        return new TestHateosHttpHandler();
    }

    @Override
    public FakeHateosResourceHandlerContext context() {
        return new FakeHateosResourceHandlerContext();
    }

    @Override
    public Class<TestHateosHttpHandler> type() {
        return TestHateosHttpHandler.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    final static class TestHateosHttpHandler implements HateosHttpHandler<FakeHateosResourceHandlerContext> {

        TestHateosHttpHandler() {
            super();
        }

        @Override
        public void handle(final HttpRequest request,
                           final HttpResponse response,
                           final FakeHateosResourceHandlerContext context) {
            Objects.requireNonNull(request, "request");
            Objects.requireNonNull(response, "response");
            Objects.requireNonNull(context, "context");

            response.setStatus(HttpStatusCode.OK.status());
            response.setEntity(
                HttpEntity.EMPTY.setBodyText(request.bodyText())
            );
        }
    }
}
