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
import walkingkooka.collect.set.Sets;
import walkingkooka.net.Url;
import walkingkooka.net.UrlPath;
import walkingkooka.net.header.Accept;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequests;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.HttpResponses;
import walkingkooka.net.http.server.hateos.sample.TestResource;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

public final class HateosResourceMappingRouterHttpHandlerRequestTest extends HateosResourceMappingTestCase2<HateosResourceMappingRouterHttpHandlerRequest> {

    @Test
    public void testDispatchSelectionExtractorExceptionWithNullMessage() {
        this.dispatchSelectionExtratorThrowsAndCheck(
                null,
                "Bad request"
        );
    }

    @Test
    public void testDispatchSelectionExtractorExceptionWithEmptyMessage() {
        this.dispatchSelectionExtratorThrowsAndCheck(
                "",
                "Bad request"
        );
    }

    @Test
    public void testDispatchSelectionExtractorExceptionWithNonEmptyMessage() {
        this.dispatchSelectionExtratorThrowsAndCheck(
                "Message123",
                "Message123"
        );
    }

    @Test
    public void testDispatchSelectionExtractorExceptionWithMultilineMessage() {
        this.dispatchSelectionExtratorThrowsAndCheck(
                "Line1\nLine2\nLine3",
                "Line1"
        );
    }

    private void dispatchSelectionExtratorThrowsAndCheck(final String throwMessage,
                                                         final String expected) {
        final HttpResponse response = HttpResponses.recording();

        final HateosResourceHandlerContext context = new FakeHateosResourceHandlerContext() {
            @Override
            public MediaType contentType() {
                return MediaType.APPLICATION_JSON;
            }
        };

        HateosResourceMappingRouterHttpHandlerRequest.with(
                HttpRequests.get(
                        HttpTransport.SECURED,
                        Url.parseRelative("/TestResource123"),
                        HttpProtocolVersion.VERSION_1_0,
                        HttpEntity.EMPTY.addHeader(HttpHeaderName.ACCEPT, Accept.parse("" + MediaType.APPLICATION_JSON))
                ), // request,
                response,
                HateosResourceMappingRouter.with(
                        UrlPath.ROOT,
                        Sets.of(
                                HateosResourceMapping.with(
                                        HateosResourceName.with("TestResource123"),
                                        (final String s, final HateosResourceHandlerContext x) -> {
                                            throw new IllegalArgumentException(throwMessage);
                                        },
                                        TestResource.class,
                                        TestResource.class,
                                        TestHateosResource.class,
                                        HateosResourceHandlerContext.class
                                ).setHateosResourceHandler(
                                        LinkRelation.SELF,
                                        HttpMethod.GET,
                                        HateosResourceHandlers.fake()
                                )
                        ),
                        Indentation.SPACES2,
                        LineEnding.NL,
                        context
                ),
                Indentation.SPACES2,
                LineEnding.NL,
                context
        ).dispatch();

        this.checkEquals(
                expected,
                response.status()
                        .map(HttpStatus::message)
                        .orElse(null)
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final Indentation indentation = Indentation.with("   ");
        final LineEnding lineEnding = LineEnding.NL;
        final HateosResourceHandlerContext context = HateosResourceHandlerContexts.fake();

        final HateosResourceMappingRouter router = HateosResourceMappingRouter.with(
                UrlPath.ROOT,
                Sets.empty(),
                indentation,
                lineEnding,
                context
        );
        final HttpRequest request = HttpRequests.fake();
        final HttpResponse response = HttpResponses.fake();

        this.toStringAndCheck(
                HateosResourceMappingRouterHttpHandlerRequest.with(
                        request,
                        response,
                        router,
                        indentation,
                        lineEnding,
                        context
                ),
                request + " " + response + " indentation=\"   \" lineEndings=\"\\n\""
        );
    }

    // ClassTesting......................................................................................................

    @Override
    public Class<HateosResourceMappingRouterHttpHandlerRequest> type() {
        return HateosResourceMappingRouterHttpHandlerRequest.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return HateosResourceMappingRouterHttpHandler.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return "Request";
    }
}
