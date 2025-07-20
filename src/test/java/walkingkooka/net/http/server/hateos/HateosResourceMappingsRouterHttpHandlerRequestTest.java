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
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

public final class HateosResourceMappingsRouterHttpHandlerRequestTest extends HateosResourceMappingsTestCase<HateosResourceMappingsRouterHttpHandlerRequest> {

    @Test
    public void testDispatchSelectionExtractorExceptionWithNullMessage() {
        this.dispatchSelectionExtractorThrowsAndCheck(
                null,
                "Bad request"
        );
    }

    @Test
    public void testDispatchSelectionExtractorExceptionWithEmptyMessage() {
        this.dispatchSelectionExtractorThrowsAndCheck(
                "",
                "Bad request"
        );
    }

    @Test
    public void testDispatchSelectionExtractorExceptionWithNonEmptyMessage() {
        this.dispatchSelectionExtractorThrowsAndCheck(
                "Message123",
                "Message123"
        );
    }

    @Test
    public void testDispatchSelectionExtractorExceptionWithMultilineMessage() {
        this.dispatchSelectionExtractorThrowsAndCheck(
                "Line1\nLine2\nLine3",
                "Line1"
        );
    }

    private void dispatchSelectionExtractorThrowsAndCheck(final String throwMessage,
                                                         final String expected) {
        final HttpResponse response = HttpResponses.recording();

        final HateosResourceHandlerContext context = new FakeHateosResourceHandlerContext() {
            @Override
            public MediaType contentType() {
                return MediaType.APPLICATION_JSON;
            }

            @Override
            public Indentation indentation() {
                return Indentation.SPACES2;
            }

            @Override
            public LineEnding lineEnding() {
                return LineEnding.NL;
            }
        };

        HateosResourceMappingsRouterHttpHandlerRequest.with(
                HttpRequests.get(
                        HttpTransport.SECURED,
                        Url.parseRelative("/TestResource123"),
                        HttpProtocolVersion.VERSION_1_0,
                        HttpEntity.EMPTY.addHeader(HttpHeaderName.ACCEPT, Accept.parse("" + MediaType.APPLICATION_JSON))
                ), // request,
                response,
                HateosResourceMappingsRouter.with(
                        UrlPath.ROOT,
                        Sets.of(
                                HateosResourceMappings.with(
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
                        context
                ),
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
        final HateosResourceHandlerContext context = HateosResourceHandlerContexts.fake();

        final HateosResourceMappingsRouter router = HateosResourceMappingsRouter.with(
                UrlPath.ROOT,
                Sets.empty(),
                context
        );
        final HttpRequest request = HttpRequests.fake();
        final HttpResponse response = HttpResponses.fake();

        this.toStringAndCheck(
                HateosResourceMappingsRouterHttpHandlerRequest.with(
                        request,
                        response,
                        router,
                        context
                ),
            request + " " + response
        );
    }

    // ClassTesting......................................................................................................

    @Override
    public Class<HateosResourceMappingsRouterHttpHandlerRequest> type() {
        return HateosResourceMappingsRouterHttpHandlerRequest.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return HateosResourceMappingsRouterHttpHandler.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return "Request";
    }
}
