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
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.Range;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.UrlPath;
import walkingkooka.net.header.Accept;
import walkingkooka.net.header.AcceptCharset;
import walkingkooka.net.header.CharsetName;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.header.MediaTypeParameterName;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.net.http.server.FakeHttpRequest;
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpRequestParameterName;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.HttpResponses;
import walkingkooka.route.Router;
import walkingkooka.route.RouterTesting2;
import walkingkooka.text.CharSequences;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.BigInteger;
import java.math.MathContext;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HateosResourceMappingRouterTest extends HateosResourceMappingTestCase2<HateosResourceMappingRouter>
        implements RouterTesting2<HateosResourceMappingRouter, HttpRequestAttribute<?>, HttpHandler> {

    private final static String NO_BODY = null;

    private final static BigInteger ID = BigInteger.valueOf(31);
    private final static BigInteger ID2 = BigInteger.valueOf(127);

    private final static TestResource RESOURCE_IN = TestResource.with(TestHateosResource.with(ID));
    private final static TestResource RESOURCE_OUT = TestResource.with(TestHateosResource.with(ID2));
    private final static TestResource COLLECTION_RESOURCE_IN = TestResource.with(TestHateosResource.with(ID));
    private final static TestResource COLLECTION_RESOURCE_OUT = TestResource.with(TestHateosResource.with(ID2));

    private final static MediaType CONTENT_TYPE = MediaType.parse("application/test-json");

    private final static String RESOURCE_TYPE_NAME = TestResource.class.getSimpleName();

    private final static CharsetName DEFAULT_CHARSET = CharsetName.UTF_8;

    private final static Set<HateosResourceMapping<?, ?, ?, ?, ?>> MAPPINGS = Sets.empty();
    private final static Indentation INDENTATION = Indentation.SPACES2;
    private final static LineEnding LINE_ENDING = LineEnding.NL;

    private final static TestHateosResourceHandlerContext CONTEXT = new TestHateosResourceHandlerContext();

    static class TestHateosResourceHandlerContext extends FakeHateosResourceHandlerContext {

        @Override
        public MediaType contentType() {
            return CONTENT_TYPE;
        }

        @Override
        public JsonNode marshall(final Object value) {
            return JsonNodeMarshallContexts.basic()
                    .marshall(value);
        }

        @Override
        public <T> T unmarshall(final JsonNode json,
                                final Class<T> type) {
            return JsonNodeUnmarshallContexts.basic(
                    ExpressionNumberKind.BIG_DECIMAL,
                    MathContext.DECIMAL32
            ).unmarshall(json, type);
        }
    }

    private final static UrlPath BASE_PATH = UrlPath.parse("/api");

    @Test
    public void testWithNullBasePathFails() {
        assertThrows(
                NullPointerException.class,
                () -> HateosResourceMappingRouter.with(
                        null,
                        MAPPINGS,
                        INDENTATION,
                        LINE_ENDING,
                        CONTEXT
                )
        );
    }

    @Test
    public void testWithNullMappingsFails() {
        assertThrows(
                NullPointerException.class,
                () -> HateosResourceMappingRouter.with(
                        BASE_PATH,
                        null,
                        INDENTATION,
                        LINE_ENDING,
                        CONTEXT
                )
        );
    }

    @Test
    public void testWithNullIndentationFails() {
        assertThrows(
                NullPointerException.class,
                () -> HateosResourceMappingRouter.with(
                        BASE_PATH,
                        MAPPINGS,
                        null,
                        LINE_ENDING,
                        CONTEXT
                )
        );
    }

    @Test
    public void testWithNullLineEndingFails() {
        assertThrows(
                NullPointerException.class,
                () -> HateosResourceMappingRouter.with(
                        BASE_PATH,
                        MAPPINGS,
                        INDENTATION,
                        null,
                        CONTEXT
                )
        );
    }

    @Test
    public void testWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> HateosResourceMappingRouter.with(
                        BASE_PATH,
                        MAPPINGS,
                        INDENTATION,
                        LINE_ENDING,
                        null
                )
        );
    }

    // route............................................................................................................

    @Test
    public void testMissingBaseUnrouted() {
        this.routeFails(
                this.request(
                        HttpMethod.POST,
                        "/missing-base/",
                        this.contentType(),
                        ""
                )
        );
    }

    private void routeFails(final HttpRequest request) {
        this.checkEquals(
                Optional.empty(),
                this.createRouter()
                        .route(request.routerParameters()),
                () -> "" + request
        );
    }

    // BAD REQUEST......................................................................................................

    @Test
    public void testBadRequestMissingResourceName() {
        this.routeAndCheck(
                "/api/",
                HttpStatusCode.BAD_REQUEST.setMessage("Missing resource name"),
                HttpEntity.EMPTY
        );
    }

    @Test
    public void testBadRequestInvalidResourceName() {
        this.routeAndCheck(
                "/api/999-invalid",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid resource name \"999-invalid\""),
                HttpEntity.EMPTY
        );
    }

    @Test
    public void testNotFoundUnknownHateosResource() {
        this.routeAndCheck(
                "/api/unknown123",
                HttpStatusCode.NOT_FOUND.setMessage("resource: unknown123"),
                HttpEntity.EMPTY
        );
    }

    @Test
    public void testNotFoundUnknownHateosResourceIdRelation() {
        this.routeAndCheck(
                "/api/unknown123/456/contents",
                HttpStatusCode.NOT_FOUND.setMessage("resource: unknown123"),
                HttpEntity.EMPTY
        );
    }

    @Test
    public void testBadRequestInvalidRelation() {
        assertThrows(InvalidCharacterException.class, () -> {
            LinkRelation.with("!!");
        });
        this.routeAndCheck(
                "/api/resource-with-body/0x1/!!",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid link relation \"!!\""),
                HttpEntity.EMPTY
        );
    }

    @Test
    public void testBadRequestUnknownRelation() {
        this.routeAndCheck(
                "/api/resource-with-body/0x1/clear",
                HttpStatusCode.BAD_REQUEST.setMessage("Unknown link relation \"clear\""),
                HttpEntity.EMPTY
        );
    }

    @Test
    public void testMethodNotSupported() {
        this.methodNotSupportedAndCheck("AAA");
    }

    @Test
    public void testMethodNotSupported2() {
        this.methodNotSupportedAndCheck("GHI");
    }

    @Test
    public void testMethodNotSupported3() {
        this.methodNotSupportedAndCheck("Z");
    }

    private void methodNotSupportedAndCheck(final String method) {
        this.routeAndCheck(
                this.createRouter(),
                HttpMethod.with(method),
                "/api/resource-with-body/0x123/contents",
                this.contentType(),
                NO_BODY,
                HttpStatusCode.METHOD_NOT_ALLOWED.setMessage(method + " resource: resource-with-body, link relation: contents"),
                HttpEntity.EMPTY.addHeader(HttpHeaderName.ALLOW, Lists.of(HttpMethod.DELETE, HttpMethod.POST, HttpMethod.PUT))
        );
    }

    @Test
    public void testBadRequestInvalidResourceId() {
        this.routeAndCheck(
                "/api/resource-with-body/@/contents",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid id \"@\""),
                HttpEntity.EMPTY
        );
    }

    @Test
    public void testBadRequestInvalidList() {
        this.routeAndCheck(
                "/api/resource-with-body/0x1,invalid/contents",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid list \"0x1,invalid\""),
                HttpEntity.EMPTY
        );
    }

    @Test
    public void testBadRequestInvalidRange() {
        this.routeAndCheck(
                "/api/resource-with-body/0x1-0x2-0x3/contents",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid range \"0x1-0x2-0x3\""),
                HttpEntity.EMPTY
        );
    }

    @Test
    public void testContentLengthPresentBodyAbsent() {
        this.routeAndCheck(
                this.createRouter(),
                HttpMethod.POST,
                "/api/resource-with-body/0x1f/contents",
                map(HttpHeaderName.CONTENT_TYPE, this.contentType(), HttpHeaderName.CONTENT_LENGTH, 1L),
                null,
                HttpStatusCode.BAD_REQUEST.setMessage("Body absent with Content-Length: 1"),
                HttpEntity.EMPTY
        );
    }

    @Test
    public void testContentLengthPresentBodyEmpty() {
        this.routeAndCheck(
                this.createRouter(),
                HttpMethod.POST,
                "/api/resource-with-body/0x1f/contents",
                map(HttpHeaderName.CONTENT_TYPE, this.contentType(), HttpHeaderName.CONTENT_LENGTH, 2L),
                "",
                HttpStatusCode.BAD_REQUEST.setMessage("Body absent with Content-Length: 2"),
                HttpEntity.EMPTY
        );
    }

    @Test
    public void testContentLengthRequiredBodyPresentWithoutContentLength() {
        this.routeAndCheck(
                this.createRouter(),
                HttpMethod.POST,
                "/api/resource-with-body/0x1f/contents",
                map(HttpHeaderName.CONTENT_TYPE, this.contentType()),
                "{}",
                HttpStatusCode.LENGTH_REQUIRED.status(),
                HttpEntity.EMPTY
        );
    }

    @Test
    public void testContentLengthBodyLengthMismatchFails() {
        this.routeAndCheck(
                this.createRouter(),
                HttpMethod.POST,
                "/api/resource-with-body/0x1f/contents",
                map(HttpHeaderName.CONTENT_TYPE, this.contentType(), HttpHeaderName.CONTENT_LENGTH, 999L),
                "{}",
                HttpStatusCode.BAD_REQUEST.setMessage("Content-Length: 999 != body length=2 mismatch"),
                HttpEntity.EMPTY
        );
    }

    @Test
    public void testBadRequestIdAndInvalidJson() {
        this.routeAndCheck(
                "/api/resource-with-body/0x1f/contents",
                "!invalid json",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid JSON: Invalid character '!' at (1,1) \"!invalid json\" expected NULL | BOOLEAN | STRING | NUMBER | ARRAY | OBJECT"),
                HttpEntity.EMPTY
        );
    }

    @Test
    public void testBadRequestWildcardAndInvalidJson() {
        this.routeAndCheck(
                "/api/resource-with-body/*/contents",
                "!invalid json",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid JSON: Invalid character '!' at (1,1) \"!invalid json\" expected NULL | BOOLEAN | STRING | NUMBER | ARRAY | OBJECT"),
                HttpEntity.EMPTY
        );
    }

    @Test
    public void testBadRequestRangeAndInvalidJson() {
        this.routeAndCheck(
                "/api/resource-with-body/0x1-0x2/contents",
                "!invalid json",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid JSON: Invalid character '!' at (1,1) \"!invalid json\" expected NULL | BOOLEAN | STRING | NUMBER | ARRAY | OBJECT"),
                HttpEntity.EMPTY
        );
    }

    @Test
    public void testGetWithoutContentType() {
        this.routeAndCheck(
                this.createRouter(
                        new FakeHateosResourceHandler<>() {
                            @Override
                            public Optional<TestResource> handleOne(final BigInteger id,
                                                                    final Optional<TestResource> resource,
                                                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                    final TestHateosResourceHandlerContext context) {
                                return Optional.empty();
                            }
                        }),
                HttpMethod.GET,
                "/api/get-resource/0x1f",
                map(
                        HttpHeaderName.ACCEPT,
                        Accept.with(
                                Lists.of(
                                        this.contentType()
                                )
                        )
                ),
                "",
                HttpStatusCode.NO_CONTENT.status(),
                HttpEntity.EMPTY.addHeader(
                        HateosResourceMapping.X_CONTENT_TYPE_NAME,
                        RESOURCE_TYPE_NAME
                )
        );
    }

    @Test
    public void testGetRequiresNormalize() {
        this.routeAndCheck(
                this.createRouter(
                        new FakeHateosResourceHandler<>() {
                            @Override
                            public Optional<TestResource> handleOne(final BigInteger id,
                                                                    final Optional<TestResource> resource,
                                                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                    final TestHateosResourceHandlerContext context) {
                                return Optional.empty();
                            }
                        }),
                HttpMethod.GET,
                "/api/get-resource/./0x1f",
                map(
                        HttpHeaderName.ACCEPT,
                        Accept.with(
                                Lists.of(
                                        this.contentType()
                                )
                        )
                ),
                "",
                HttpStatusCode.NO_CONTENT.status(),
                HttpEntity.EMPTY.addHeader(
                        HateosResourceMapping.X_CONTENT_TYPE_NAME,
                        RESOURCE_TYPE_NAME
                )
        );
    }

    @Test
    public void testPostMissingAccept() {
        this.routeAndCheck(
                this.createRouter(),
                HttpMethod.POST,
                "/api/resource-with-body/0x1f/contents",
                map(HttpHeaderName.CONTENT_TYPE, this.contentType(), HttpHeaderName.CONTENT_LENGTH, 2L),
                "{}",
                HttpStatusCode.BAD_REQUEST.setMessage("Missing Accept@@"),
                HttpEntity.EMPTY
        );
    }

    @Test
    public void testIncompatibleAccept() {
        this.routeAndCheck(
                this.createRouter(),
                HttpMethod.POST,
                "/api/resource-with-body/0x1f/contents",
                map(
                        HttpHeaderName.ACCEPT,
                        MediaType.TEXT_PLAIN,
                        HttpHeaderName.CONTENT_LENGTH,
                        2L,
                        HttpHeaderName.CONTENT_TYPE,
                        this.contentType()
                ),
                "{}",
                HttpStatusCode.BAD_REQUEST.setMessage("Expected"),
                HttpEntity.EMPTY
        );
    }

    // not implement....................................................................................................

    @Test
    public void testNotImplementedId() {
        final String customMessage = "Custom message 123! something something else";

        this.routeThrowsAndCheck(
                new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestResource> handleOne(final BigInteger id,
                                                            final Optional<TestResource> resource,
                                                            final Map<HttpRequestAttribute<?>, Object> parameters,
                                                            final TestHateosResourceHandlerContext context) {
                        throw new UnsupportedOperationException(customMessage);
                    }
                },
                "/api/resource-with-body/0x1/contents",
                NO_BODY,
                UnsupportedOperationException.class,
                customMessage
        );
    }

    @Test
    public void testNotImplementedWildcard() {
        final String customMessage = "Custom message 123!";

        this.routeThrowsAndCheck(
                new FakeHateosResourceHandler<>() {

                    @Override
                    public Optional<TestResource> handleAll(final Optional<TestResource> resource,
                                                            final Map<HttpRequestAttribute<?>, Object> parameters,
                                                            final TestHateosResourceHandlerContext context) {
                        throw new UnsupportedOperationException(customMessage);
                    }
                },
                "/api/resource-with-body/*/contents",
                NO_BODY,
                UnsupportedOperationException.class,
                customMessage
        );
    }

    @Test
    public void testNotImplementedRange() {
        final String customMessage = "Custom message 123!";

        this.routeThrowsAndCheck(
                new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestResource> handleRange(final Range<BigInteger> ids,
                                                              final Optional<TestResource> resource,
                                                              final Map<HttpRequestAttribute<?>, Object> parameters,
                                                              final TestHateosResourceHandlerContext context) {
                        throw new UnsupportedOperationException(customMessage);
                    }
                },
                "/api/resource-with-body/0x1-0x2/contents",
                NO_BODY,
                UnsupportedOperationException.class,
                customMessage
        );
    }

    @Test
    public void testNotImplementedIdUnsupportedOperationExceptionWithMessage() {
        final String message = "message 456";

        this.routeThrowsAndCheck(
                new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestResource> handleOne(final BigInteger id,
                                                            final Optional<TestResource> resource,
                                                            final Map<HttpRequestAttribute<?>, Object> parameters,
                                                            final TestHateosResourceHandlerContext context) {
                        throw new UnsupportedOperationException(message);
                    }
                },
                "/api/resource-with-body/0x1/contents",
                NO_BODY,
                UnsupportedOperationException.class,
                message
        );
    }

    // internal server error............................................................................................

    private final static String INTERNAL_SERVER_ERROR_MESSAGE = "Because 123\nline 2";

    @Test
    public void testInternalServerErrorId() {
        this.routeThrowsAndCheck(
                new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestResource> handleOne(final BigInteger id,
                                                            final Optional<TestResource> resource,
                                                            final Map<HttpRequestAttribute<?>, Object> parameters,
                                                            final TestHateosResourceHandlerContext context) {
                        throw new RuntimeException(INTERNAL_SERVER_ERROR_MESSAGE);
                    }
                },
                "/api/resource-with-body/0x1/contents",
                NO_BODY,
                RuntimeException.class,
                INTERNAL_SERVER_ERROR_MESSAGE
        );
    }

    @Test
    public void testInternalServerErrorWildcard() {
        this.routeThrowsAndCheck(
                new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestResource> handleAll(final Optional<TestResource> resource,
                                                            final Map<HttpRequestAttribute<?>, Object> parameters,
                                                            final TestHateosResourceHandlerContext context) {
                        throw new RuntimeException(INTERNAL_SERVER_ERROR_MESSAGE);
                    }
                },
                "/api/resource-with-body/*/contents",
                NO_BODY,
                RuntimeException.class,
                INTERNAL_SERVER_ERROR_MESSAGE
        );
    }

    @Test
    public void testRangeInternalServerErrorRange() {
        this.routeThrowsAndCheck(
                new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestResource> handleRange(final Range<BigInteger> ids,
                                                              final Optional<TestResource> resource,
                                                              final Map<HttpRequestAttribute<?>, Object> parameters,
                                                              final TestHateosResourceHandlerContext context) {
                        throw new RuntimeException(INTERNAL_SERVER_ERROR_MESSAGE);
                    }
                },
                "/api/resource-with-body/0x1-0x2/contents",
                NO_BODY,
                RuntimeException.class,
                INTERNAL_SERVER_ERROR_MESSAGE
        );
    }

    @Test
    public void testInternalServerErrorExceptionWithoutMessageId() {
        final String message = "Something went wrong";

        this.routeThrowsAndCheck(
                new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestResource> handleOne(final BigInteger id,
                                                            final Optional<TestResource> resource,
                                                            final Map<HttpRequestAttribute<?>, Object> parameters,
                                                            final TestHateosResourceHandlerContext context) {
                        throw new RuntimeException(message);
                    }
                },
                "/api/resource-with-body/0x1/contents",
                NO_BODY,
                RuntimeException.class,
                message
        );
    }

    private void routeThrowsAndCheck(final HateosResourceHandler<BigInteger, TestResource, TestResource, TestHateosResourceHandlerContext> handler,
                                     final String url,
                                     final String body,
                                     final Class<? extends Throwable> thrownType,
                                     final String thrownMessage) {
        final HateosResourceMappingRouter router = this.createRouter(handler);

        final MediaType contentType = this.contentType();
        final Map<HttpHeaderName<?>, List<?>> headers = Maps.sorted();
        headers.put(HttpHeaderName.ACCEPT, Lists.of(contentType.accept()));
        headers.put(HttpHeaderName.ACCEPT_CHARSET, Lists.of(new AcceptCharset[]{AcceptCharset.parse(MediaTypeParameterName.CHARSET.parameterValue(contentType).orElse(DEFAULT_CHARSET).toHeaderText())}));
        headers.put(HttpHeaderName.CONTENT_TYPE, Lists.of(new MediaType[]{contentType}));

        final byte[] bodyBytes = bytes(body, contentType);
        if (null != bodyBytes && bodyBytes.length > 0) {
            headers.put(
                    HttpHeaderName.CONTENT_LENGTH,
                    Lists.of(new Long[]{Long.valueOf(bodyBytes.length)})
            );
        }

        final HttpRequest request = this.request(
                HttpMethod.POST,
                url,
                headers,
                body
        );

        final Throwable thrown = assertThrows(
                thrownType,
                () -> router.route(
                                request.routerParameters()
                        )
                        .get()
                        .handle(
                                request,
                                HttpResponses.fake()
                        )
        );
        this.checkEquals(thrownMessage, thrown.getMessage(), "message");
    }

    // id request.......................................................................................................

    @Test
    public void testRequestResourceBodyAbsentId() {
        this.routeAndCheck(
                new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestResource> handleOne(final BigInteger id,
                                                            final Optional<TestResource> resource,
                                                            final Map<HttpRequestAttribute<?>, Object> parameters,
                                                            final TestHateosResourceHandlerContext context) {
                        checkId(id);
                        checkResource(resource, Optional.empty());

                        return Optional.empty();
                    }
                },
                "/api/resource-with-body/0x123/contents",
                NO_BODY,
                HttpStatusCode.NO_CONTENT.status(),
                HttpEntity.EMPTY.addHeader(
                        HateosResourceMapping.X_CONTENT_TYPE_NAME,
                        RESOURCE_TYPE_NAME
                )
        );
    }

    @Test
    public void testRequestResourceBodyEmptyId() {
        this.routeAndCheck(
                new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestResource> handleOne(final BigInteger id,
                                                            final Optional<TestResource> resource,
                                                            final Map<HttpRequestAttribute<?>, Object> parameters,
                                                            final TestHateosResourceHandlerContext context) {
                        checkId(id);
                        checkResource(resource, Optional.empty());

                        return Optional.empty();
                    }
                },
                "/api/resource-with-body/0x123/contents",
                "",
                HttpStatusCode.NO_CONTENT.status(),
                HttpEntity.EMPTY.addHeader(
                        HateosResourceMapping.X_CONTENT_TYPE_NAME,
                        RESOURCE_TYPE_NAME
                )
        );
    }

    @Test
    public void testRequestResourceBodyJsonId() {
        this.routeAndCheck(
                new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestResource> handleOne(final BigInteger id,
                                                            final Optional<TestResource> resource,
                                                            final Map<HttpRequestAttribute<?>, Object> parameters,
                                                            final TestHateosResourceHandlerContext context) {
                        checkId(id);
                        checkResource(resource, Optional.of(RESOURCE_IN));

                        return Optional.empty();
                    }
                },
                "/api/resource-with-body/0x123/contents",
                this.toJson(RESOURCE_IN),
                HttpStatusCode.NO_CONTENT.status(),
                HttpEntity.EMPTY.addHeader(
                        HateosResourceMapping.X_CONTENT_TYPE_NAME,
                        RESOURCE_TYPE_NAME
                )
        );
    }

    @Test
    public void testRequestResourceBodyJsonIdCharsetUtf16() {
        this.routeAndCheck(
                this.createRouter(new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestResource> handleOne(final BigInteger id,
                                                            final Optional<TestResource> resource,
                                                            final Map<HttpRequestAttribute<?>, Object> parameters,
                                                            final TestHateosResourceHandlerContext context) {
                        checkId(id);
                        checkResource(resource, Optional.of(RESOURCE_IN));

                        return Optional.empty();
                    }
                }),
                HttpMethod.POST,
                "/api/resource-with-body/0x123/contents",
                this.contentTypeUtf16(),
                this.toJson(RESOURCE_IN),
                HttpStatusCode.NO_CONTENT.status(),
                HttpEntity.EMPTY.addHeader(
                        HateosResourceMapping.X_CONTENT_TYPE_NAME,
                        RESOURCE_TYPE_NAME
                )
        );
    }

    private void checkId(final BigInteger id) {
        checkId(id, BigInteger.valueOf(0x123));
    }

    private void checkId(final BigInteger id, final BigInteger expected) {
        this.checkEquals(expected, id, "id");
    }

    // all request.................................................................................................

    @Test
    public void testRequestResourceBodyAbsentWildcard() {
        this.routeAndCheck(
                new FakeHateosResourceHandler<>() {

                    @Override
                    public Optional<TestResource> handleAll(final Optional<TestResource> resource,
                                                            final Map<HttpRequestAttribute<?>, Object> parameters,
                                                            final TestHateosResourceHandlerContext context) {
                        checkResource(resource, Optional.empty());

                        return Optional.empty();
                    }
                },
                "/api/resource-with-body/*/contents",
                NO_BODY,
                HttpStatusCode.NO_CONTENT.status(),
                HttpEntity.EMPTY.addHeader(
                        HateosResourceMapping.X_CONTENT_TYPE_NAME,
                        RESOURCE_TYPE_NAME
                )
        );
    }

    @Test
    public void testRequestResourceBodyEmptyWildcard() {
        this.routeAndCheck(
                new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestResource> handleAll(final Optional<TestResource> resource,
                                                            final Map<HttpRequestAttribute<?>, Object> parameters,
                                                            final TestHateosResourceHandlerContext context) {
                        checkResource(resource, Optional.empty());

                        return Optional.empty();
                    }
                },
                "/api/resource-with-body/*/contents",
                "",
                HttpStatusCode.NO_CONTENT.status(),
                HttpEntity.EMPTY.addHeader(
                        HateosResourceMapping.X_CONTENT_TYPE_NAME,
                        RESOURCE_TYPE_NAME
                )
        );
    }

    @Test
    public void testRequestResourceBodyJsonWildcard() {
        this.routeAndCheck(
                new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestResource> handleAll(final Optional<TestResource> resource,
                                                            final Map<HttpRequestAttribute<?>, Object> parameters,
                                                            final TestHateosResourceHandlerContext context) {
                        checkResource(resource, Optional.of(COLLECTION_RESOURCE_IN));

                        return Optional.empty();
                    }
                },
                "/api/resource-with-body/*/contents",
                this.toJson(COLLECTION_RESOURCE_IN),
                HttpStatusCode.NO_CONTENT.status(),
                HttpEntity.EMPTY.addHeader(
                        HateosResourceMapping.X_CONTENT_TYPE_NAME,
                        RESOURCE_TYPE_NAME
                )
        );
    }

    // range request....................................................................................................

    @Test
    public void testRequestResourceBodyAbsentRange() {
        this.routeAndCheck(
                new FakeHateosResourceHandler<>() {

                    @Override
                    public Optional<TestResource> handleRange(final Range<BigInteger> id,
                                                              final Optional<TestResource> resource,
                                                              final Map<HttpRequestAttribute<?>, Object> parameters,
                                                              final TestHateosResourceHandlerContext context) {
                        checkRange(id);
                        checkResource(resource, Optional.empty());

                        return Optional.empty();
                    }
                },
                "/api/resource-with-body/0x123-0x456/contents",
                NO_BODY,
                HttpStatusCode.NO_CONTENT.status(),
                HttpEntity.EMPTY.addHeader(
                        HateosResourceMapping.X_CONTENT_TYPE_NAME,
                        RESOURCE_TYPE_NAME
                )
        );
    }

    @Test
    public void testRequestResourceBodyEmptyRange() {
        this.routeAndCheck(
                new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestResource> handleRange(final Range<BigInteger> id,
                                                              final Optional<TestResource> resource,
                                                              final Map<HttpRequestAttribute<?>, Object> parameters,
                                                              final TestHateosResourceHandlerContext context) {
                        checkRange(id);
                        checkResource(resource, Optional.empty());

                        return Optional.empty();
                    }
                },
                "/api/resource-with-body/0x123-0x456/contents",
                "",
                HttpStatusCode.NO_CONTENT.status(),
                HttpEntity.EMPTY.addHeader(
                        HateosResourceMapping.X_CONTENT_TYPE_NAME,
                        RESOURCE_TYPE_NAME
                )
        );
    }

    @Test
    public void testRequestResourceBodyJsonRange() {
        this.routeAndCheck(
                new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestResource> handleRange(final Range<BigInteger> id,
                                                              final Optional<TestResource> resource,
                                                              final Map<HttpRequestAttribute<?>, Object> parameters,
                                                              final TestHateosResourceHandlerContext context) {
                        checkRange(id);
                        checkResource(resource, Optional.of(COLLECTION_RESOURCE_IN));

                        return Optional.empty();
                    }
                },
                "/api/resource-with-body/0x123-0x456/contents",
                this.toJson(COLLECTION_RESOURCE_IN),
                HttpStatusCode.NO_CONTENT.status(),
                HttpEntity.EMPTY.addHeader(
                        HateosResourceMapping.X_CONTENT_TYPE_NAME,
                        RESOURCE_TYPE_NAME
                )
        );
    }

    private void checkRange(final Range<BigInteger> id) {
        checkId(id, Range.greaterThanEquals(BigInteger.valueOf(0x123)).and(Range.lessThanEquals(BigInteger.valueOf(0x456))));
    }

    private void checkId(final Range<BigInteger> id, final Range<BigInteger> expected) {
        this.checkEquals(expected, id, "id");
    }

    private <T> void checkResource(final Optional<T> resource, final Optional<T> expected) {
        this.checkEquals(expected, resource, "resource");
    }

    // response none......................................................................................................

    @Test
    public void testResponseNoneIdResourceBodyAbsent() {
        this.routeAndCheck(
                new FakeHateosResourceHandler<>() {

                    @Override
                    public Optional<TestResource> handleNone(final Optional<TestResource> resource,
                                                             final Map<HttpRequestAttribute<?>, Object> parameters,
                                                             final TestHateosResourceHandlerContext context) {
                        return Optional.empty();
                    }
                },
                "/api/resource-with-body",
                NO_BODY,
                HttpStatusCode.NO_CONTENT.status(),
                HttpEntity.EMPTY.addHeader(
                        HateosResourceMapping.X_CONTENT_TYPE_NAME,
                        RESOURCE_TYPE_NAME
                )
        );
    }

    @Test
    public void testResponseNoneIdResourceBodyJson() {
        this.routeAndCheck(
                new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestResource> handleNone(final Optional<TestResource> resource,
                                                             final Map<HttpRequestAttribute<?>, Object> parameters,
                                                             final TestHateosResourceHandlerContext context) {
                        return Optional.of(RESOURCE_OUT);
                    }
                },
                "/api/resource-with-body",
                NO_BODY,
                HttpStatusCode.CREATED.status(),
                this.httpEntity(RESOURCE_OUT)
        );
    }

    // response id......................................................................................................

    @Test
    public void testResponseResourceBodyAbsent() {
        this.routeAndCheck(
                new FakeHateosResourceHandler<>() {

                    @Override
                    public Optional<TestResource> handleOne(final BigInteger id,
                                                            final Optional<TestResource> resource,
                                                            final Map<HttpRequestAttribute<?>, Object> parameters,
                                                            final TestHateosResourceHandlerContext context) {
                        return Optional.empty();
                    }
                },
                "/api/resource-with-body/0x123/contents",
                NO_BODY,
                HttpStatusCode.NO_CONTENT.status(),
                HttpEntity.EMPTY.addHeader(
                        HateosResourceMapping.X_CONTENT_TYPE_NAME,
                        RESOURCE_TYPE_NAME
                )
        );
    }

    @Test
    public void testResponseResourceBodyJson() {
        this.routeAndCheck(
                new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestResource> handleOne(final BigInteger id,
                                                            final Optional<TestResource> resource,
                                                            final Map<HttpRequestAttribute<?>, Object> parameters,
                                                            final TestHateosResourceHandlerContext context) {
                        return Optional.of(RESOURCE_OUT);
                    }
                },
                "/api/resource-with-body/0x123/contents",
                NO_BODY,
                HttpStatusCode.OK.status(),
                this.httpEntity(RESOURCE_OUT)
        );
    }

    // response collection..............................................................................................

    @Test
    public void testResponseResourceBodyAbsentCollection() {
        this.routeAndCheck(
                new FakeHateosResourceHandler<>() {

                    @Override
                    public Optional<TestResource> handleRange(final Range<BigInteger> id,
                                                              final Optional<TestResource> resource,
                                                              final Map<HttpRequestAttribute<?>, Object> parameters,
                                                              final TestHateosResourceHandlerContext context) {
                        return Optional.empty();
                    }
                },
                "/api/resource-with-body/0x123-0x456/contents",
                NO_BODY,
                HttpStatusCode.NO_CONTENT.status(),
                HttpEntity.EMPTY.addHeader(
                        HateosResourceMapping.X_CONTENT_TYPE_NAME,
                        RESOURCE_TYPE_NAME
                )
        );
    }

    @Test
    public void testResponseResourceBodyJsonCollection() {
        this.routeAndCheck(
                new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestResource> handleRange(final Range<BigInteger> id,
                                                              final Optional<TestResource> resource,
                                                              final Map<HttpRequestAttribute<?>, Object> parameters,
                                                              final TestHateosResourceHandlerContext context) {
                        return Optional.of(COLLECTION_RESOURCE_OUT);
                    }
                },
                "/api/resource-with-body/0x123-0x456/contents",
                NO_BODY,
                HttpStatusCode.OK.status(),
                this.httpEntity(COLLECTION_RESOURCE_OUT)
        );
    }

    // this test contains everything in a single method so it can be copied over to JunitTest.
    @Test
    public void testMapRouteAndCheckStandaloneForItJunitTest() {
        final HateosResourceMapping<BigInteger, TestResource, TestResource, TestHateosResource, TestHateosResourceHandlerContext> mapping = HateosResourceMapping.with(
                HateosResourceName.with("resource-with-body"),
                (s, x) -> {
                    return HateosResourceSelection.one(
                            BigInteger.valueOf(
                                    Integer.parseInt(
                                            s.substring(2),
                                            16
                                    )
                            )
                    ); // assumes hex digit in url
                },
                TestResource.class,
                TestResource.class,
                TestHateosResource.class,
                TestHateosResourceHandlerContext.class
        ).setHateosResourceHandler(
                LinkRelation.CONTENTS,
                HttpMethod.POST,
                new FakeHateosResourceHandler<BigInteger, TestResource, TestResource, TestHateosResourceHandlerContext>() {

                    @Override
                    public Optional<TestResource> handleOne(final BigInteger id,
                                                            final Optional<TestResource> resource,
                                                            final Map<HttpRequestAttribute<?>, Object> parameters,
                                                            final TestHateosResourceHandlerContext context) {
                        return Optional.of(
                                TestResource.with(
                                        TestHateosResource.with(
                                                BigInteger.valueOf(31)
                                        )
                                )
                        );
                    }
                }
        );

        final Router<HttpRequestAttribute<?>, HttpHandler> router = HateosResourceMapping.router(
                UrlPath.parse("/api"),
                Sets.of(mapping),
                INDENTATION,
                LINE_ENDING,
                CONTEXT
        );

        final HttpRequest request = new FakeHttpRequest() {

            @Override
            public HttpTransport transport() {
                return HttpTransport.UNSECURED;
            }

            @Override
            public HttpProtocolVersion protocolVersion() {
                return HttpProtocolVersion.VERSION_1_0;
            }

            @Override
            public HttpMethod method() {
                return HttpMethod.POST;
            }

            @Override
            public RelativeUrl url() {
                return Url.parseRelative("/api/resource-with-body/0x123/contents");
            }

            @Override
            public Map<HttpHeaderName<?>, List<?>> headers() {
                return Maps.of(
                        HttpHeaderName.CONTENT_TYPE, Lists.of(CONTENT_TYPE),
                        HttpHeaderName.ACCEPT, Lists.of(CONTENT_TYPE.accept()),
                        HttpHeaderName.ACCEPT_CHARSET, Lists.of(
                                AcceptCharset.parse("utf-8")
                        )
                );
            }

            @Override
            public String bodyText() {
                return "";
            }

            @Override
            public Map<HttpRequestParameterName, List<String>> parameters() {
                return Maps.empty();
            }

            @Override
            public List<String> parameterValues(final HttpRequestParameterName parameterName) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String toString() {
                return this.method() + " " + this.url() + " " + parameters();
            }
        };
        final HttpHandler httpHandler = router.route(
                request.routerParameters()
        ).orElseThrow(
                () -> new Error("Unable to route")
        );

        final HttpResponse response = HttpResponses.recording();
        httpHandler.handle(request, response);
        this.checkEquals(
                "{\n" +
                        "  \"type\": \"test-HateosResource\",\n" +
                        "  \"value\": {\n" +
                        "    \"id\": \"31\"\n" +
                        "  }\n" +
                        "}",
                response.entity()
                        .bodyText()
        );
    }

    @Test
    public void testSetHateosHttpEntityHandlerAndRoute() {
        final MediaType mediaType = MediaType.TEXT_PLAIN;

        final HateosResourceMapping<BigInteger, TestResource, TestResource, TestHateosResource, TestHateosResourceHandlerContext> mapping = HateosResourceMapping.with(
                HateosResourceName.with("resource-with-body"),
                (s, x) -> {
                    return HateosResourceSelection.one(
                            BigInteger.valueOf(
                                    Integer.parseInt(
                                            s.substring(2),
                                            16
                                    )
                            )
                    ); // assumes hex digit in url
                },
                TestResource.class,
                TestResource.class,
                TestHateosResource.class,
                TestHateosResourceHandlerContext.class
        ).setHateosHttpEntityHandler(
                LinkRelation.CONTENTS,
                HttpMethod.POST,
                new FakeHateosHttpEntityHandler<BigInteger, TestHateosResourceHandlerContext>() {
                    @Override
                    public HttpEntity handleOne(final BigInteger id,
                                                final HttpEntity entity,
                                                final Map<HttpRequestAttribute<?>, Object> parameters,
                                                final TestHateosResourceHandlerContext context) {
                        checkEquals(
                                mediaType,
                                HttpHeaderName.CONTENT_TYPE.headerOrFail(entity)
                        );
                        return HttpEntity.EMPTY.setBodyText(
                                id +
                                        "\n" +
                                        entity.bodyText()
                        );
                    }
                }
        );

        final Router<HttpRequestAttribute<?>, HttpHandler> router = HateosResourceMapping.router(
                UrlPath.parse("api"),
                Sets.of(mapping),
                INDENTATION,
                LINE_ENDING,
                CONTEXT
        );

        final HttpRequest request = new FakeHttpRequest() {

            @Override
            public HttpTransport transport() {
                return HttpTransport.UNSECURED;
            }

            @Override
            public HttpProtocolVersion protocolVersion() {
                return HttpProtocolVersion.VERSION_1_0;
            }

            @Override
            public HttpMethod method() {
                return HttpMethod.POST;
            }

            @Override
            public RelativeUrl url() {
                return Url.parseRelative("/api/resource-with-body/0x123/contents");
            }

            @Override
            public Map<HttpHeaderName<?>, List<?>> headers() {
                return Maps.of(
                        HttpHeaderName.CONTENT_TYPE, Lists.of(mediaType),
                        HttpHeaderName.ACCEPT, Lists.of(mediaType.accept()),
                        HttpHeaderName.ACCEPT_CHARSET, Lists.of(AcceptCharset.parse("utf-8"))
                );
            }

            @Override
            public byte[] body() {
                return this.bodyText()
                        .getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String bodyText() {
                return "RequestBodyText123";
            }

            @Override
            public Map<HttpRequestParameterName, List<String>> parameters() {
                return Maps.empty();
            }

            @Override
            public List<String> parameterValues(final HttpRequestParameterName parameterName) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String toString() {
                return this.method() + " " + this.url() + " " + parameters();
            }
        };
        final HttpHandler httpHandler = router.route(
                request.routerParameters()
        ).orElseThrow(
                () -> new Error("Unable to route")
        );

        final HttpResponse response = HttpResponses.recording();
        httpHandler.handle(request, response);
        this.checkEquals(
                "291\n" + // 0x123
                        "RequestBodyText123",
                response.entity()
                        .bodyText()
        );
    }

    // HELPERS .........................................................................................................

    @Override
    public HateosResourceMappingRouter createRouter() {
        return this.createRouter(new FakeHateosResourceHandler<>());
    }

    private HateosResourceMappingRouter createRouter(final HateosResourceHandler<BigInteger, TestResource, TestResource, TestHateosResourceHandlerContext> handler) {
        final HateosResourceMapping<BigInteger, TestResource, TestResource, TestHateosResource, TestHateosResourceHandlerContext> getMapping = this.getMapping()
                .setHateosResourceHandler(LinkRelation.SELF, HttpMethod.GET, handler);

        final HateosResourceMapping<BigInteger, TestResource, TestResource, TestHateosResource, TestHateosResourceHandlerContext> mappingWithBody = this.mappingWithBody()
                .setHateosResourceHandler(LinkRelation.SELF, HttpMethod.POST, handler)
                .setHateosResourceHandler(LinkRelation.with("a1"), HttpMethod.POST, handler)
                .setHateosResourceHandler(LinkRelation.CONTENTS, HttpMethod.PUT, handler)
                .setHateosResourceHandler(LinkRelation.CONTENTS, HttpMethod.DELETE, handler)
                .setHateosResourceHandler(LinkRelation.CONTENTS, HttpMethod.POST, handler)
                .setHateosResourceHandler(LinkRelation.with("z1"), HttpMethod.POST, handler);
        return Cast.to(
                HateosResourceMapping.router(
                        BASE_PATH,
                        Sets.of(
                                getMapping,
                                mappingWithBody
                        ),
                        INDENTATION,
                        LINE_ENDING,
                        CONTEXT
                )
        );
    }

    // assumes a GET get-resource id
    private HateosResourceMapping<BigInteger, TestResource, TestResource, TestHateosResource, TestHateosResourceHandlerContext> getMapping() {
        return HateosResourceMapping.with(
                HateosResourceName.with("get-resource"),
                (s, x) -> HateosResourceSelection.one(
                        parse(s)
                ),
                TestResource.class,
                TestResource.class,
                TestHateosResource.class,
                TestHateosResourceHandlerContext.class
        );
    }

    private HateosResourceMapping<BigInteger, TestResource, TestResource, TestHateosResource, TestHateosResourceHandlerContext> mappingWithBody() {
        return HateosResourceMapping.with(
                HateosResourceName.with("resource-with-body"),
                (s, x) -> {
                    if (s.isEmpty()) {
                        return HateosResourceSelection.none();
                    }
                    if (s.equals("*")) {
                        return HateosResourceSelection.all();
                    }
                    final int range = s.indexOf("-");
                    if (-1 != range) {
                        try {
                            return HateosResourceSelection.range(
                                    Range.greaterThanEquals(
                                            parse(
                                                    s.substring(0, range)
                                            )
                                    ).and(
                                            Range.lessThanEquals(
                                                    parse(
                                                            s.substring(range + 1)
                                                    )
                                            )
                                    )
                            );
                        } catch (final RuntimeException cause) {
                            throw new IllegalArgumentException("Invalid range " + CharSequences.quoteAndEscape(s));
                        }
                    }
                    final int list = s.indexOf(",");
                    if (-1 != list) {
                        try {
                            return HateosResourceSelection.many(
                                    Arrays.stream(s.split(","))
                                            .map(HateosResourceMappingRouterTest::parse)
                                            .collect(Collectors.toCollection(SortedSets::tree))
                            );
                        } catch (final RuntimeException cause) {
                            throw new IllegalArgumentException("Invalid list " + CharSequences.quoteAndEscape(s));
                        }
                    }

                    return HateosResourceSelection.one(
                            parse(s)
                    );
                },
                TestResource.class,
                TestResource.class,
                TestHateosResource.class,
                TestHateosResourceHandlerContext.class
        );
    }

    private static BigInteger parse(final String text) {
        try {
            return BigInteger.valueOf(Integer.parseInt(text.substring(2), 16)); // drop leading 0x
        } catch (final RuntimeException cause) {
            throw new IllegalArgumentException("Invalid id " + CharSequences.quoteAndEscape(text));
        }
    }

    private MediaType contentType() {
        return CONTENT_TYPE.setCharset(DEFAULT_CHARSET);
    }

    private MediaType contentTypeUtf16() {
        return this.contentType()
                .setCharset(CharsetName.UTF_16);
    }

    private String toJson(final Object resource) {
        return this.marshallContext()
                .marshall(resource).toString();
    }

    private HttpEntity httpEntity(final Object resource) {
        return this.httpEntity(
                resource,
                this.contentType()
        );
    }

    private HttpEntity httpEntity(final Object resource,
                                  final MediaType contentType) {
        return this.httpEntity(
                this.toJson(resource),
                resource.getClass()
                        .getSimpleName(),
                contentType
        );
    }

    private HttpEntity httpEntity(final String body,
                                  final String valueType,
                                  final MediaType contentType) {
        return CharSequences.isNullOrEmpty(body) ?
                HttpEntity.EMPTY :
                HttpEntity.EMPTY
                        .setContentType(contentType)
                        .addHeader(HateosResourceMapping.X_CONTENT_TYPE_NAME, valueType)
                        .setBodyText(body)
                        .setContentLength();
    }

    private void routeAndCheck(final String url,
                               final HttpStatus status,
                               final HttpEntity entity) {
        this.routeAndCheck(url,
                NO_BODY,
                status,
                entity);
    }

    private void routeAndCheck(final String url,
                               final String body,
                               final HttpStatus status,
                               final HttpEntity entity) {
        this.routeAndCheck(this.createRouter(),
                url,
                body,
                status,
                entity);
    }

    private void routeAndCheck(final HateosResourceHandler<BigInteger, TestResource, TestResource, TestHateosResourceHandlerContext> handler,
                               final String url,
                               final String body,
                               final HttpStatus status,
                               final HttpEntity entity) {
        this.routeAndCheck(
                this.createRouter(handler),
                url,
                body,
                status,
                entity
        );
    }

    private void routeAndCheck(final HateosResourceMappingRouter router,
                               final String url,
                               final String body,
                               final HttpStatus status,
                               final HttpEntity entity) {
        this.routeAndCheck(
                router,
                HttpMethod.POST,
                url,
                contentType(),
                body,
                status,
                entity
        );
    }

    /**
     * Also computes and adds a content-length header.
     */
    private void routeAndCheck(final HateosResourceMappingRouter router,
                               final HttpMethod method,
                               final String url,
                               final MediaType contentType,
                               final String body,
                               final HttpStatus status,
                               final HttpEntity entity) {
        final Map<HttpHeaderName<?>, List<?>> headers = Maps.sorted();
        headers.put(
                HttpHeaderName.ACCEPT,
                Lists.of(
                        CONTENT_TYPE.accept()
                )
        );
        headers.put(
                HttpHeaderName.ACCEPT_CHARSET,
                Lists.of(
                        new AcceptCharset[]{
                                AcceptCharset.parse(
                                        MediaTypeParameterName.CHARSET.parameterValue(contentType)
                                                .orElse(DEFAULT_CHARSET)
                                                .toHeaderText()
                                )
                        }
                )
        );
        headers.put(
                HttpHeaderName.CONTENT_TYPE,
                Lists.of(
                        new MediaType[]{contentType}
                )
        );

        final byte[] bodyBytes = bytes(body, contentType);
        if (null != bodyBytes && bodyBytes.length > 0) {
            headers.put(
                    HttpHeaderName.CONTENT_LENGTH,
                    Lists.of(
                            new Long[]{
                                    Long.valueOf(bodyBytes.length)
                            }
                    )
            );
        }

        this.routeAndCheck(
                router,
                method,
                url,
                headers,
                body,
                status,
                entity
        );
    }

    private void routeAndCheck(final HateosResourceMappingRouter router,
                               final HttpMethod method,
                               final String url,
                               final Map<HttpHeaderName<?>, List<?>> headers,
                               final String body,
                               final HttpStatus status,
                               final HttpEntity entity) {
        final HttpRequest request = this.request(method,
                url,
                headers,
                body);
        final HttpResponse response = HttpResponses.recording();
        final Optional<HttpHandler> handle = router.route(request.routerParameters());
        handle.ifPresent(
                h -> h.handle(
                        request,
                        response
                )
        );

        final HttpResponse expected = HttpResponses.recording();

        if (null != status) {
            expected.setStatus(status);
        }
        expected.setEntity(entity);

        if (null != status && status.value().equals(HttpStatusCode.BAD_REQUEST) && response.entity().isNotEmpty()) {
            this.checkEquals(status, expected.status().orElse(null), "status");

            final HttpEntity responseEntity = response.entity();
            this.checkEquals(Lists.of(MediaType.TEXT_PLAIN), responseEntity.headers().get(HttpHeaderName.CONTENT_TYPE), () -> "content-type\n" + expected);
            this.checkNotEquals(Lists.empty(), responseEntity.headers().get(HttpHeaderName.CONTENT_TYPE), () -> "content-type\n" + expected);
            this.checkNotEquals("", responseEntity.bodyText(), () -> "body\n" + expected);

        } else {
            this.checkEquals(
                    expected,
                    response,
                    request::toString);
        }
    }

    private HttpRequest request(final HttpMethod method,
                                final String url,
                                final MediaType contentType,
                                final String body) {
        return this.request(method,
                url,
                map(HttpHeaderName.CONTENT_TYPE, contentType),
                body);
    }

    private Map<HttpHeaderName<?>, List<?>> map(final HttpHeaderName<?> header,
                                                final Object value) {
        return Maps.of(
                header,
                Lists.of(
                        new Object[]{value}
                )
        );
    }

    private Map<HttpHeaderName<?>, List<?>> map(final HttpHeaderName<?> header1,
                                                final Object value1,
                                                final HttpHeaderName<?> header2,
                                                final Object value2) {
        return Maps.of(
                header1,
                Lists.of(new Object[]{value1}),
                header2,
                Lists.of(new Object[]{value2})
        );
    }

    private Map<HttpHeaderName<?>, List<?>> map(final HttpHeaderName<?> header1,
                                                final Object value1,
                                                final HttpHeaderName<?> header2,
                                                final Object value2,
                                                final HttpHeaderName<?> header3,
                                                final Object value3) {
        return Maps.of(
                header1,
                Lists.of(new Object[]{value1}),
                header2,
                Lists.of(new Object[]{value2}),
                header3,
                Lists.of(new Object[]{value3})
        );
    }

    private HttpRequest request(final HttpMethod method,
                                final String url,
                                final Map<HttpHeaderName<?>, List<?>> headers,
                                final String body) {
        return new HttpRequest() {

            @Override
            public HttpTransport transport() {
                return HttpTransport.UNSECURED;
            }

            @Override
            public HttpProtocolVersion protocolVersion() {
                return HttpProtocolVersion.VERSION_1_0;
            }

            @Override
            public HttpMethod method() {
                return method;
            }

            @Override
            public RelativeUrl url() {
                return Url.parseRelative(url);
            }

            @Override
            public Map<HttpHeaderName<?>, List<?>> headers() {
                return headers;
            }

            @Override
            public byte[] body() {
                return bytes(body, this);
            }

            @Override
            public String bodyText() {
                return null != body ? body : "";
            }

            @Override
            public long bodyLength() {
                final byte[] body = this.body();
                return null != body ?
                        body.length :
                        0;
            }

            @Override
            public Map<HttpRequestParameterName, List<String>> parameters() {
                final Map<HttpRequestParameterName, List<String>> parameters = Maps.sorted();

                this.url()
                        .query()
                        .parameters()
                        .entrySet()
                        .forEach(e -> parameters.put(
                                        HttpRequestParameterName.with(
                                                e.getKey()
                                                        .value()
                                        ),
                                        e.getValue()
                                )
                        );

                return parameters;
            }

            @Override
            public List<String> parameterValues(final HttpRequestParameterName parameterName) {
                final List<String> values = this.parameters().get(parameterName);
                return null == values ?
                        Lists.empty() :
                        values;
            }

            @Override
            public String toString() {
                return this.method() + " " + this.url() + " " + parameters();
            }
        };
    }

    private byte[] bytes(final String body,
                         final HttpRequest request) {
        return bytes(body,
                HttpHeaderName.CONTENT_TYPE.headerOrFail(request));
    }

    private byte[] bytes(final String body,
                         final MediaType contentType) {
        return null != body ?
                body.getBytes(MediaTypeParameterName.CHARSET.parameterValue(contentType).orElse(CharsetName.UTF_8).charset().get()) :
                null;
    }

    private JsonNodeUnmarshallContext unmarshallContext() {
        return JsonNodeUnmarshallContexts.basic(
                ExpressionNumberKind.DEFAULT,
                MathContext.DECIMAL32
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<HateosResourceMappingRouter> type() {
        return Cast.to(HateosResourceMappingRouter.class);
    }
}
