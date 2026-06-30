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
import walkingkooka.collect.list.Lists;
import walkingkooka.net.Url;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequests;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.HttpResponses;
import walkingkooka.reflect.JavaVisibility;

public final class GetOrHeadHeaderHateosHttpHandlerTest implements GetOrHeadHeaderHateosHttpHandlerTesting<GetOrHeadHeaderHateosHttpHandler<FakeHateosResourceHandlerContext>, FakeHateosResourceHandlerContext> {

    @Test
    public void testHandlePostMethodNotAllowed() {
        final HttpResponse response = HttpResponses.recording();
        response.setStatus(HttpStatusCode.METHOD_NOT_ALLOWED.setMessage("Method POST not allowed"));
        response.setEntity(
            HttpEntity.EMPTY.addHeader(
                HttpHeaderName.ALLOW,
                Lists.of(
                    HttpMethod.GET,
                    HttpMethod.HEAD
                )
            )
        );

        this.handleAndCheck(
            HttpRequests.post(
                HttpTransport.UNSECURED,
                Url.parseRelative("/api/decimalNumberSymbols/*/localeStartsWith/English?offset=0&count=2"),
                HttpProtocolVersion.VERSION_1_0,
                HttpEntity.EMPTY.addHeader(
                    HttpHeaderName.ALLOW,
                    Lists.of(
                        HttpMethod.HEAD,
                        HttpMethod.GET
                    )
                )
            ),
            this.createContext(),
            response
        );
    }


    @Override
    public GetOrHeadHeaderHateosHttpHandler<FakeHateosResourceHandlerContext> createHttpHandler() {
        return new GetOrHeadHeaderHateosHttpHandler<>() {

            @Override
            public void handleGetOrHead(final HttpRequest request,
                                        final HttpResponse response,
                                        final FakeHateosResourceHandlerContext context) {
                response.setStatus(
                    HttpStatusCode.OK.setMessage("Hello")
                );
                response.setEntity(
                    HttpEntity.EMPTY.setBodyText("World")
                );
            }
        };
    }

    @Override
    public FakeHateosResourceHandlerContext createContext() {
        return new FakeHateosResourceHandlerContext();
    }

    @Override
    public Class<GetOrHeadHeaderHateosHttpHandler<FakeHateosResourceHandlerContext>> type() {
        return Cast.to(GetOrHeadHeaderHateosHttpHandler.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
