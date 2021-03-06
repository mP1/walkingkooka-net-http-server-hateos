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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
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
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpRequestParameterName;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.HttpResponses;
import walkingkooka.route.Router;
import walkingkooka.route.RouterTesting2;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HateosResourceMappingRouterTest extends HateosResourceMappingTestCase2<HateosResourceMappingRouter>
        implements RouterTesting2<HateosResourceMappingRouter,
                HttpRequestAttribute<?>,
                BiConsumer<HttpRequest, HttpResponse>> {

    private final static String NO_BODY = null;

    private final static BigInteger ID = BigInteger.valueOf(31);
    private final static BigInteger ID2 = BigInteger.valueOf(127);

    private final static TestResource RESOURCE_IN = TestResource.with(TestHateosResource.with(ID));
    private final static TestResource RESOURCE_OUT = TestResource.with(TestHateosResource.with(ID2));
    private final static TestResource COLLECTION_RESOURCE_IN = TestResource.with(TestHateosResource.with(ID));
    private final static TestResource COLLECTION_RESOURCE_OUT = TestResource.with(TestHateosResource.with(ID2));

    private final static CharsetName DEFAULT_CHARSET = CharsetName.UTF_8;

    @Test
    public void testMissingBaseUnrouted() {
        this.routeFails(this.request(HttpMethod.GET,
                "/missing-base/",
                this.contentType(),
                ""));
    }

    @Test
    public void testWrongContentTypeUnrouted() {
        this.routeFails(this.request(HttpMethod.GET,
                "/api/",
                MediaType.parse("text/plain;q=1"),
                ""));
    }

    @Test
    public void testWrongContentTypeUnrouted2() {
        this.routeFails(this.request(HttpMethod.GET,
                "/api/resource1/1",
                MediaType.parse("text/plain;q=1"),
                ""));
    }

    private void routeFails(final HttpRequest request) {
        assertEquals(Optional.empty(),
                this.createRouter()
                        .route(request.routerParameters()),
                () -> "" + request);
    }

    // BAD REQUEST......................................................................................................

    @Test
    public void testBadRequestMissingResourceName() {
        this.routeAndCheck("/api/",
                HttpStatusCode.BAD_REQUEST.setMessage("Missing resource name"));
    }

    @Test
    public void testBadRequestInvalidResourceName() {
        this.routeAndCheck("/api/999-invalid",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid resource name \"999-invalid\""));
    }

    @Test
    public void testNotFoundUnknownHateosResource() {
        this.routeAndCheck("/api/unknown123",
                HttpStatusCode.NOT_FOUND.setMessage("resource: unknown123"));
    }

    @Test
    public void testNotFoundUnknownHateosResourceIdRelation() {
        this.routeAndCheck("/api/unknown123/456/contents",
                HttpStatusCode.NOT_FOUND.setMessage("resource: unknown123"));
    }

    @Test
    public void testBadRequestInvalidRelation() {
        assertThrows(InvalidCharacterException.class, () -> {
            LinkRelation.with("!!");
        });
        this.routeAndCheck("/api/resource1/0x1/!!",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid link relation \"!!\""));
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
        this.routeAndCheck(this.createRouter(),
                HttpMethod.with(method),
                "/api/resource1/0x123/contents",
                this.contentType(),
                NO_BODY,
                HttpStatusCode.METHOD_NOT_ALLOWED.setMessage(method + " resource: resource1, link relation: contents"),
                HttpEntity.EMPTY.addHeader(HttpHeaderName.ALLOW, list(HttpMethod.DELETE, HttpMethod.POST, HttpMethod.PUT)));
    }

    @Test
    public void testBadRequestInvalidResourceId() {
        this.routeAndCheck("/api/resource1/@/contents",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid id \"@\""));
    }

    @Test
    public void testBadRequestInvalidList() {
        this.routeAndCheck("/api/resource1/0x1,invalid/contents",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid list \"0x1,invalid\""));
    }

    @Test
    public void testBadRequestInvalidRange() {
        this.routeAndCheck("/api/resource1/0x1-0x2-0x3/contents",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid range \"0x1-0x2-0x3\""));
    }

    @Test
    public void testContentLengthPresentBodyAbsent() {
        this.routeAndCheck(this.createRouter(),
                HttpMethod.POST,
                "/api/resource1/0x1f/contents",
                map(HttpHeaderName.CONTENT_TYPE, this.contentType(), HttpHeaderName.CONTENT_LENGTH, 1L),
                null,
                HttpStatusCode.BAD_REQUEST.setMessage("Body absent with Content-Length: 1"));
    }

    @Test
    public void testContentLengthPresentBodyEmpty() {
        this.routeAndCheck(this.createRouter(),
                HttpMethod.POST,
                "/api/resource1/0x1f/contents",
                map(HttpHeaderName.CONTENT_TYPE, this.contentType(), HttpHeaderName.CONTENT_LENGTH, 2L),
                "",
                HttpStatusCode.BAD_REQUEST.setMessage("Body absent with Content-Length: 2"));
    }

    @Test
    public void testContentLengthRequiredBodyPresentWithoutContentLength() {
        this.routeAndCheck(this.createRouter(),
                HttpMethod.POST,
                "/api/resource1/0x1f/contents",
                map(HttpHeaderName.CONTENT_TYPE, this.contentType()),
                "{}",
                HttpStatusCode.LENGTH_REQUIRED.status());
    }

    @Test
    public void testContentLengthBodyLengthMismatchFails() {
        this.routeAndCheck(this.createRouter(),
                HttpMethod.POST,
                "/api/resource1/0x1f/contents",
                map(HttpHeaderName.CONTENT_TYPE, this.contentType(), HttpHeaderName.CONTENT_LENGTH, 999L),
                "{}",
                HttpStatusCode.BAD_REQUEST.setMessage("Content-Length: 999 != body length=2 mismatch"));
    }

    @Test
    public void testBadRequestIdAndInvalidJson() {
        this.routeAndCheck("/api/resource1/0x1f/contents",
                "!invalid json",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid JSON: Unrecognized character '!' at (1,1) \"!invalid json\" expected NULL | BOOLEAN | STRING | NUMBER | ARRAY | OBJECT"));
    }

    @Test
    public void testBadRequestWildcardAndInvalidJson() {
        this.routeAndCheck("/api/resource1/*/contents",
                "!invalid json",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid JSON: Unrecognized character '!' at (1,1) \"!invalid json\" expected NULL | BOOLEAN | STRING | NUMBER | ARRAY | OBJECT"));
    }

    @Test
    public void testBadRequestRangeAndInvalidJson() {
        this.routeAndCheck("/api/resource1/0x1-0x2/contents",
                "!invalid json",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid JSON: Unrecognized character '!' at (1,1) \"!invalid json\" expected NULL | BOOLEAN | STRING | NUMBER | ARRAY | OBJECT"));
    }

    @Test
    public void testMissingAccept() {
        this.routeAndCheck(this.createRouter(),
                HttpMethod.POST,
                "/api/resource1/0x1f/contents",
                map(HttpHeaderName.CONTENT_TYPE, this.contentType(), HttpHeaderName.CONTENT_LENGTH, 2L),
                "{}",
                HttpStatusCode.BAD_REQUEST.setMessage("Missing Accept@@"));
    }

    @Test
    public void testIncompatibleAccept() {
        this.routeAndCheck(this.createRouter(),
                HttpMethod.POST,
                "/api/resource1/0x1f/contents",
                map(HttpHeaderName.ACCEPT, MediaType.TEXT_PLAIN, HttpHeaderName.CONTENT_LENGTH, 2L, HttpHeaderName.CONTENT_TYPE, this.contentType()),
                "{}",
                HttpStatusCode.BAD_REQUEST.setMessage("Expected"));
    }

    // not implement....................................................................................................

    @Test
    public void testNotImplementedId() {
        final String customMessage = "Custom message 123! something something else";

        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestResource> handleOne(final BigInteger id,
                                                                       final Optional<TestResource> resource,
                                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   throw new UnsupportedOperationException();
                               }
                           },
                "/api/resource1/0x1/contents",
                NO_BODY,
                HttpStatusCode.NOT_IMPLEMENTED.status(),
                UnsupportedOperationException.class,
                customMessage);
    }

    @Test
    public void testNotImplementedWildcard() {
        final String customMessage = "Custom message 123!";

        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestResource> handleRange(final Range<BigInteger> ids,
                                                                         final Optional<TestResource> resource,
                                                                         final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   throw new UnsupportedOperationException();
                               }
                           },
                "/api/resource1/*/contents",
                NO_BODY,
                HttpStatusCode.NOT_IMPLEMENTED.status(),
                UnsupportedOperationException.class,
                customMessage);
    }

    @Test
    public void testNotImplementedRange() {
        final String customMessage = "Custom message 123!";

        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestResource> handleRange(final Range<BigInteger> ids,
                                                                         final Optional<TestResource> resource,
                                                                         final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   throw new UnsupportedOperationException();
                               }
                           },
                "/api/resource1/0x1-0x2/contents",
                NO_BODY,
                HttpStatusCode.NOT_IMPLEMENTED.status(),
                UnsupportedOperationException.class,
                customMessage);
    }

    @Test
    public void testNotImplementedIdUnsupportedOperationExceptionWithMessage() {
        final String message = "message 456";

        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestResource> handleOne(final BigInteger id,
                                                                       final Optional<TestResource> resource,
                                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   throw new UnsupportedOperationException(message);
                               }
                           },
                "/api/resource1/0x1/contents",
                NO_BODY,
                HttpStatusCode.NOT_IMPLEMENTED.status().setMessage(message),
                UnsupportedOperationException.class,
                message);
    }

    // internal server error............................................................................................

    private final static String INTERNAL_SERVER_ERROR_MESSAGE = "Because 123\nline 2";
    private final static String INTERNAL_SERVER_ERROR_MESSAGE_FIRST_LINE = HttpStatus.firstLineOfText(INTERNAL_SERVER_ERROR_MESSAGE);

    @Test
    public void testInternalServerErrorId() {
        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestResource> handleOne(final BigInteger id,
                                                                       final Optional<TestResource> resource,
                                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   throw new RuntimeException(INTERNAL_SERVER_ERROR_MESSAGE);
                               }
                           },
                "/api/resource1/0x1/contents",
                NO_BODY,
                HttpStatusCode.INTERNAL_SERVER_ERROR.status().setMessage(INTERNAL_SERVER_ERROR_MESSAGE_FIRST_LINE),
                RuntimeException.class,
                INTERNAL_SERVER_ERROR_MESSAGE);
    }

    @Test
    public void testInternalServerErrorWildcard() {
        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestResource> handleAll(final Optional<TestResource> resource,
                                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   throw new RuntimeException(INTERNAL_SERVER_ERROR_MESSAGE);
                               }
                           },
                "/api/resource1/*/contents",
                NO_BODY,
                HttpStatusCode.INTERNAL_SERVER_ERROR.status().setMessage(INTERNAL_SERVER_ERROR_MESSAGE_FIRST_LINE),
                RuntimeException.class,
                INTERNAL_SERVER_ERROR_MESSAGE);
    }

    @Test
    public void testRangeInternalServerErrorRange() {
        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestResource> handleRange(final Range<BigInteger> ids,
                                                                         final Optional<TestResource> resource,
                                                                         final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   throw new RuntimeException(INTERNAL_SERVER_ERROR_MESSAGE);
                               }
                           },
                "/api/resource1/0x1-0x2/contents",
                NO_BODY,
                HttpStatusCode.INTERNAL_SERVER_ERROR.status().setMessage(INTERNAL_SERVER_ERROR_MESSAGE_FIRST_LINE),
                RuntimeException.class,
                INTERNAL_SERVER_ERROR_MESSAGE);
    }

    @Test
    public void testInternalServerErrorExceptionWithoutMessageId() {
        final String message = "Something went wrong";

        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestResource> handleOne(final BigInteger id,
                                                                       final Optional<TestResource> resource,
                                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   throw new RuntimeException();
                               }
                           },
                "/api/resource1/0x1/contents",
                NO_BODY,
                HttpStatusCode.INTERNAL_SERVER_ERROR.status(),
                RuntimeException.class,
                message);
    }

    private void routeAndCheck(final HateosHandler<BigInteger, TestResource, TestResource> handler,
                               final String url,
                               final String body,
                               final HttpStatusCode statusCode,
                               final Class<?> thrown,
                               final String thrownMessage) {
        this.routeAndCheck(handler,
                url,
                body,
                statusCode.setMessageOrDefault(null),
                thrown,
                thrownMessage);
    }

    private void routeAndCheck(final HateosHandler<BigInteger, TestResource, TestResource> handler,
                               final String url,
                               final String body,
                               final HttpStatus status,
                               final Class<?> thrown,
                               final String thrownMessage) {
        final HateosResourceMappingRouter router = this.createRouter(handler);

        final MediaType contentType = this.contentType();
        final Map<HttpHeaderName<?>, List<?>> headers = Maps.sorted();
        headers.put(HttpHeaderName.ACCEPT, Lists.of(contentType.accept()));
        headers.put(HttpHeaderName.ACCEPT_CHARSET, list(AcceptCharset.parse(MediaTypeParameterName.CHARSET.parameterValue(contentType).orElse(DEFAULT_CHARSET).toHeaderText())));
        headers.put(HttpHeaderName.CONTENT_TYPE, list(contentType));

        final byte[] bodyBytes = bytes(body, contentType);
        if (null != bodyBytes && bodyBytes.length > 0) {
            headers.put(HttpHeaderName.CONTENT_LENGTH, list(Long.valueOf(bodyBytes.length)));
        }


        final HttpRequest request = this.request(HttpMethod.POST,
                url,
                headers,
                body);
        final HttpResponse response = HttpResponses.recording();
        final Optional<BiConsumer<HttpRequest, HttpResponse>> handle = router.route(request.routerParameters());
        handle.ifPresent(h -> h.accept(request, response));

        assertEquals(Optional.ofNullable(status), response.status(), () -> "status\n" + request);

        final HttpEntity responseEntity = response.entities().get(0);
        assertEquals(Lists.of(MediaType.TEXT_PLAIN), responseEntity.headers().get(HttpHeaderName.CONTENT_TYPE), () -> "Content-Type incorrect\n" + request + "\n" + response);
        assertNotEquals(null, responseEntity.headers().get(HttpHeaderName.CONTENT_LENGTH), () -> "Content-Length missing\n" + request + "\n" + response);

        final String responseBody = responseEntity.bodyText();
        assertEquals(true, responseBody.contains(thrown.getSimpleName()), () -> "response body contains stacktrace\n" + request + "\n" + response);
        assertEquals(true, responseBody.contains("at " + this.getClass().getName()), () -> "response body contains stacktrace\n" + request + "\n" + response);
    }

    // id request.......................................................................................................

    private final static String RESOURCE_SUCCESSFUL = HttpMethod.POST + " " + TestResource.class.getSimpleName() + " No content";

    @Test
    public void testRequestResourceBodyAbsentId() {
        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestResource> handleOne(final BigInteger id,
                                                                       final Optional<TestResource> resource,
                                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   checkId(id);
                                   checkResource(resource, Optional.empty());

                                   return Optional.empty();
                               }
                           },
                "/api/resource1/0x123/contents",
                NO_BODY,
                HttpStatusCode.NO_CONTENT.status().setMessage(RESOURCE_SUCCESSFUL));
    }

    @Test
    public void testRequestResourceBodyEmptyId() {
        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestResource> handleOne(final BigInteger id,
                                                                       final Optional<TestResource> resource,
                                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   checkId(id);
                                   checkResource(resource, Optional.empty());

                                   return Optional.empty();
                               }
                           },
                "/api/resource1/0x123/contents",
                "",
                HttpStatusCode.NO_CONTENT.status().setMessage(RESOURCE_SUCCESSFUL));
    }

    @Test
    public void testRequestResourceBodyJsonId() {
        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestResource> handleOne(final BigInteger id,
                                                                       final Optional<TestResource> resource,
                                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   checkId(id);
                                   checkResource(resource, Optional.of(RESOURCE_IN));

                                   return Optional.empty();
                               }
                           },
                "/api/resource1/0x123/contents",
                this.toJson(RESOURCE_IN),
                HttpStatusCode.NO_CONTENT.status().setMessage(RESOURCE_SUCCESSFUL));
    }

    @Test
    public void testRequestResourceBodyJsonIdCharsetUtf16() {
        this.routeAndCheck(this.createRouter(new FakeHateosHandler<>() {
                    @Override
                    public Optional<TestResource> handleOne(final BigInteger id,
                                                            final Optional<TestResource> resource,
                                                            final Map<HttpRequestAttribute<?>, Object> parameters) {
                        checkId(id);
                        checkResource(resource, Optional.of(RESOURCE_IN));

                        return Optional.empty();
                    }
                }),
                HttpMethod.POST,
                "/api/resource1/0x123/contents",
                this.contentTypeUtf16(),
                this.toJson(RESOURCE_IN),
                HttpStatusCode.NO_CONTENT.status().setMessage(RESOURCE_SUCCESSFUL));
    }

    private void checkId(final BigInteger id) {
        checkId(id, BigInteger.valueOf(0x123));
    }

    private void checkId(final BigInteger id, final BigInteger expected) {
        assertEquals(expected, id, "id");
    }

    // all request.................................................................................................

    @Test
    public void testRequestResourceBodyAbsentWildcard() {
        this.routeAndCheck(new FakeHateosHandler<>() {

                               @Override
                               public Optional<TestResource> handleAll(final Optional<TestResource> resource,
                                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   checkResource(resource, Optional.empty());

                                   return Optional.empty();
                               }
                           },
                "/api/resource1/*/contents",
                NO_BODY,
                HttpStatusCode.NO_CONTENT.status().setMessage(RESOURCE_SUCCESSFUL));
    }

    @Test
    public void testRequestResourceBodyEmptyWildcard() {
        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestResource> handleAll(final Optional<TestResource> resource,
                                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   checkResource(resource, Optional.empty());

                                   return Optional.empty();
                               }
                           },
                "/api/resource1/*/contents",
                "",
                HttpStatusCode.NO_CONTENT.status().setMessage(RESOURCE_SUCCESSFUL));
    }

    @Test
    public void testRequestResourceBodyJsonWildcard() {
        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestResource> handleAll(final Optional<TestResource> resource,
                                                                         final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   checkResource(resource, Optional.of(COLLECTION_RESOURCE_IN));

                                   return Optional.empty();
                               }
                           },
                "/api/resource1/*/contents",
                this.toJson(COLLECTION_RESOURCE_IN),
                HttpStatusCode.NO_CONTENT.status().setMessage(RESOURCE_SUCCESSFUL));
    }

    // range request....................................................................................................

    @Test
    public void testRequestResourceBodyAbsentRange() {
        this.routeAndCheck(new FakeHateosHandler<>() {

                               @Override
                               public Optional<TestResource> handleRange(final Range<BigInteger> id,
                                                                         final Optional<TestResource> resource,
                                                                         final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   checkRange(id);
                                   checkResource(resource, Optional.empty());

                                   return Optional.empty();
                               }
                           },
                "/api/resource1/0x123-0x456/contents",
                NO_BODY,
                HttpStatusCode.NO_CONTENT.status().setMessage(RESOURCE_SUCCESSFUL));
    }

    @Test
    public void testRequestResourceBodyEmptyRange() {
        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestResource> handleRange(final Range<BigInteger> id,
                                                                         final Optional<TestResource> resource,
                                                                         final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   checkRange(id);
                                   checkResource(resource, Optional.empty());

                                   return Optional.empty();
                               }
                           },
                "/api/resource1/0x123-0x456/contents",
                "",
                HttpStatusCode.NO_CONTENT.status().setMessage(RESOURCE_SUCCESSFUL));
    }

    @Test
    public void testRequestResourceBodyJsonRange() {
        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestResource> handleRange(final Range<BigInteger> id,
                                                                         final Optional<TestResource> resource,
                                                                         final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   checkRange(id);
                                   checkResource(resource, Optional.of(COLLECTION_RESOURCE_IN));

                                   return Optional.empty();
                               }
                           },
                "/api/resource1/0x123-0x456/contents",
                this.toJson(COLLECTION_RESOURCE_IN),
                HttpStatusCode.NO_CONTENT.status().setMessage(RESOURCE_SUCCESSFUL));
    }

    private void checkRange(final Range<BigInteger> id) {
        checkId(id, Range.greaterThanEquals(BigInteger.valueOf(0x123)).and(Range.lessThanEquals(BigInteger.valueOf(0x456))));
    }

    private void checkId(final Range<BigInteger> id, final Range<BigInteger> expected) {
        assertEquals(expected, id, "id");
    }

    private <T> void checkResource(final Optional<T> resource, final Optional<T> expected) {
        assertEquals(expected, resource, "resource");
    }

    // response id......................................................................................................

    @Test
    public void testResponseResourceBodyAbsent() {
        this.routeAndCheck(new FakeHateosHandler<>() {

                               @Override
                               public Optional<TestResource> handleOne(final BigInteger id,
                                                                       final Optional<TestResource> resource,
                                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   return Optional.empty();
                               }
                           },
                "/api/resource1/0x123/contents",
                NO_BODY,
                HttpStatusCode.NO_CONTENT.status().setMessage(RESOURCE_SUCCESSFUL));
    }

    @Test
    public void testResponseResourceBodyJson() {
        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestResource> handleOne(final BigInteger id,
                                                                       final Optional<TestResource> resource,
                                                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   return Optional.of(RESOURCE_OUT);
                               }
                           },
                "/api/resource1/0x123/contents",
                NO_BODY,
                HttpStatusCode.OK.status().setMessage("POST " + TestResource.class.getSimpleName() + " OK"),
                this.httpEntity(RESOURCE_OUT));
    }

    // response collection..............................................................................................

    @Test
    public void testResponseResourceBodyAbsentCollection() {
        this.routeAndCheck(new FakeHateosHandler<>() {

                               @Override
                               public Optional<TestResource> handleRange(final Range<BigInteger> id,
                                                                         final Optional<TestResource> resource,
                                                                         final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   return Optional.empty();
                               }
                           },
                "/api/resource1/0x123-0x456/contents",
                NO_BODY,
                HttpStatusCode.NO_CONTENT.status().setMessage(RESOURCE_SUCCESSFUL));
    }

    @Test
    public void testResponseResourceBodyJsonCollection() {
        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestResource> handleRange(final Range<BigInteger> id,
                                                                         final Optional<TestResource> resource,
                                                                         final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   return Optional.of(COLLECTION_RESOURCE_OUT);
                               }
                           },
                "/api/resource1/0x123-0x456/contents",
                NO_BODY,
                HttpStatusCode.OK.status().setMessage("POST " + TestResource.class.getSimpleName() + " OK"),
                this.httpEntity(COLLECTION_RESOURCE_OUT));
    }

    // this test contains everything in a single method so it can be copied over to JunitTest.
    @Test
    public void testMapRouteAndCheckStandaloneForItJunitTest() {
        final HateosResourceMapping<BigInteger, TestResource, TestResource, TestHateosResource> mapping = HateosResourceMapping.with(HateosResourceName.with("resource1"),
                (s) -> {
                    return HateosResourceSelection.one(BigInteger.valueOf(Integer.parseInt(s.substring(2), 16))); // assumes hex digit in url
                },
                TestResource.class,
                TestResource.class,
                TestHateosResource.class)
                .set(LinkRelation.CONTENTS, HttpMethod.GET, new FakeHateosHandler<>() {
                    @Override
                    public Optional<TestResource> handleOne(final BigInteger id,
                                                            final Optional<TestResource> resource,
                                                            final Map<HttpRequestAttribute<?>, Object> parameters) {
                        return Optional.of(TestResource.with(TestHateosResource.with(BigInteger.valueOf(31))));
                    }
                });

        final Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> router = HateosResourceMapping.router(AbsoluteUrl.parseAbsolute("http://www.example.com/api"),
                HateosContentType.json(this.unmarshallContext(), JsonNodeMarshallContexts.basic()),
                Sets.of(mapping));

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
                return HttpMethod.GET;
            }

            @Override
            public RelativeUrl url() {
                return Url.parseRelative("/api/resource1/0x123/contents");
            }

            @Override
            public Map<HttpHeaderName<?>, List<?>> headers() {
                return Maps.of(HttpHeaderName.CONTENT_TYPE, Lists.of(HateosContentType.JSON_CONTENT_TYPE),
                        HttpHeaderName.ACCEPT, Lists.of(HateosContentType.JSON_CONTENT_TYPE.accept()),
                        HttpHeaderName.ACCEPT_CHARSET, Lists.of(AcceptCharset.parse("utf-8")));
            }

            @Test
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
        final BiConsumer<HttpRequest, HttpResponse> target = router.route(request.routerParameters()).orElseThrow(() -> new Error("Unable to route"));

        final HttpResponse response = HttpResponses.recording();
        target.accept(request, response);
        assertEquals("{\n" +
                "  \"type\": \"test-HateosResource\",\n" +
                "  \"value\": {\n" +
                "    \"id\": \"31\"\n" +
                "  }\n" +
                "}", response.entities().get(0).bodyText());
    }

    // HELPERS .........................................................................................................

    @Override
    public HateosResourceMappingRouter createRouter() {
        return this.createRouter(new FakeHateosHandler<>());
    }

    private HateosResourceMappingRouter createRouter(final HateosHandler<BigInteger, TestResource, TestResource> handler) {
        final HateosHandler<BigInteger, TestResource, TestResource> invalid = new FakeHateosHandler(){};

        final HateosResourceMapping<BigInteger, TestResource, TestResource, TestHateosResource> mapping = this.mapping()
                .set(LinkRelation.with("a1"), HttpMethod.POST, invalid)
                .set(LinkRelation.CONTENTS, HttpMethod.PUT, invalid)
                .set(LinkRelation.CONTENTS, HttpMethod.DELETE, invalid)
                .set(LinkRelation.CONTENTS, HttpMethod.POST, handler)
                .set(LinkRelation.with("z1"), HttpMethod.POST, invalid);
        return Cast.to(HateosResourceMapping.router(this.baseUrl(),
                this.hateosContentType(),
                Sets.of(mapping)));
    }

    private AbsoluteUrl baseUrl() {
        return Url.parseAbsolute("http://www.example.com/api");
    }

    private HateosResourceMapping<BigInteger, TestResource, TestResource, TestHateosResource> mapping() {
        return HateosResourceMapping.with(HateosResourceName.with("resource1"),
                (s) -> {
                    if (s.isEmpty()) {
                        return HateosResourceSelection.none();
                    }
                    if (s.equals("*")) {
                        return HateosResourceSelection.all();
                    }
                    final int range = s.indexOf("-");
                    if (-1 != range) {
                        try {
                            return HateosResourceSelection.range(Range.greaterThanEquals(parse(s.substring(0, range))).and(Range.lessThanEquals(parse(s.substring(range + 1)))));
                        } catch (final RuntimeException cause) {
                            throw new IllegalArgumentException("Invalid range " + CharSequences.quoteAndEscape(s));
                        }
                    }
                    final int list = s.indexOf(",");
                    if (-1 != list) {
                        try {
                            return HateosResourceSelection.list(Arrays.stream(s.split(",")).map(HateosResourceMappingRouterTest::parse).collect(Collectors.toList()));
                        } catch (final RuntimeException cause) {
                            throw new IllegalArgumentException("Invalid list " + CharSequences.quoteAndEscape(s));
                        }
                    }

                    return HateosResourceSelection.one(parse(s));
                },
                TestResource.class,
                TestResource.class,
                TestHateosResource.class);
    }

    private static BigInteger parse(final String text) {
        try {
            return BigInteger.valueOf(Integer.parseInt(text.substring(2), 16)); // drop leading 0x
        } catch (final RuntimeException cause) {
            throw new IllegalArgumentException("Invalid id " + CharSequences.quoteAndEscape(text));
        }
    }

    private MediaType contentType() {
        return this.hateosContentType()
                .contentType()
                .setCharset(DEFAULT_CHARSET);
    }

    private MediaType contentTypeUtf16() {
        return this.contentType().setCharset(CharsetName.UTF_16);
    }

    private HateosContentType hateosContentType() {
        return HateosContentType.json(this.unmarshallContext(), this.marshallContext());
    }

    private String toJson(final Object resource) {
        return this.marshallContext().marshall(resource).toString();
    }

    private HttpEntity[] httpEntity(final Object resource) {
        return this.httpEntity(resource, this.contentType());
    }

    private HttpEntity[] httpEntity(final Object resource,
                                    final MediaType contentType) {
        return this.httpEntity(this.toJson(resource),
                resource.getClass().getSimpleName(),
                contentType);
    }

    private HttpEntity[] httpEntity(final String body,
                                    final String valueType,
                                    final MediaType contentType) {
        HttpEntity[] entities = new HttpEntity[0];

        if (!CharSequences.isNullOrEmpty(body)) {
            entities = new HttpEntity[]{HttpEntity.EMPTY
                    .addHeader(HttpHeaderName.CONTENT_TYPE, contentType)
                    .addHeader(HateosResourceMapping.X_CONTENT_TYPE_NAME, valueType)
                    .setBodyText(body)
                    .setContentLength()};
        }
        return entities;
    }

    private void routeAndCheck(final String url,
                               final HttpStatus status,
                               final HttpEntity... entities) {
        this.routeAndCheck(url,
                NO_BODY,
                status,
                entities);
    }

    private void routeAndCheck(final String url,
                               final String body,
                               final HttpStatus status,
                               final HttpEntity... entities) {
        this.routeAndCheck(this.createRouter(),
                url,
                body,
                status,
                entities);
    }

    private void routeAndCheck(final HateosHandler<BigInteger, TestResource, TestResource> handler,
                               final String url,
                               final String body,
                               final HttpStatus status,
                               final HttpEntity... entities) {
        this.routeAndCheck(this.createRouter(handler),
                url,
                body,
                status,
                entities);
    }

    private void routeAndCheck(final HateosResourceMappingRouter router,
                               final String url,
                               final String body,
                               final HttpStatus status,
                               final HttpEntity... entities) {
        this.routeAndCheck(router,
                HttpMethod.POST,
                url,
                contentType(),
                body,
                status,
                entities);
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
                               final HttpEntity... entities) {
        final Map<HttpHeaderName<?>, List<?>> headers = Maps.sorted();
        headers.put(HttpHeaderName.ACCEPT, Lists.of(HateosContentType.JSON_CONTENT_TYPE.accept()));
        headers.put(HttpHeaderName.ACCEPT_CHARSET, list(AcceptCharset.parse(MediaTypeParameterName.CHARSET.parameterValue(contentType).orElse(DEFAULT_CHARSET).toHeaderText())));
        headers.put(HttpHeaderName.CONTENT_TYPE, list(contentType));

        final byte[] bodyBytes = bytes(body, contentType);
        if (null != bodyBytes && bodyBytes.length > 0) {
            headers.put(HttpHeaderName.CONTENT_LENGTH, list(Long.valueOf(bodyBytes.length)));
        }

        this.routeAndCheck(router,
                method,
                url,
                headers,
                body,
                status,
                entities);
    }

    private void routeAndCheck(final HateosResourceMappingRouter router,
                               final HttpMethod method,
                               final String url,
                               final Map<HttpHeaderName<?>, List<?>> headers,
                               final String body,
                               final HttpStatus status,
                               final HttpEntity... entities) {
        final HttpRequest request = this.request(method,
                url,
                headers,
                body);
        final HttpResponse response = HttpResponses.recording();
        final Optional<BiConsumer<HttpRequest, HttpResponse>> handle = router.route(request.routerParameters());
        handle.ifPresent(h -> h.accept(request, response));

        final HttpResponse expected = HttpResponses.recording();

        if (null != status) {
            expected.setStatus(status);
        }

        if (status.value().equals(HttpStatusCode.BAD_REQUEST) && response.entities().size() == 1) {
            assertEquals(status, expected.status().orElse(null), "status");

            final HttpEntity entity = response.entities().get(0);
            assertEquals(Lists.of(MediaType.TEXT_PLAIN), entity.headers().get(HttpHeaderName.CONTENT_TYPE), () -> "content-type\n" + expected);
            assertNotEquals(Lists.empty(), entity.headers().get(HttpHeaderName.CONTENT_TYPE), () -> "content-type\n" + expected);
            assertNotEquals("", entity.bodyText(), () -> "body\n" + expected);

        } else {
            Arrays.stream(entities)
                    .forEach(expected::addEntity);

            assertEquals(expected,
                    response,
                    () -> request.toString());
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

    private Map<HttpHeaderName<?>, List<?>> map(final HttpHeaderName<?> header, final Object value) {
        return Maps.of(header, list(value));
    }

    private Map<HttpHeaderName<?>, List<?>> map(final HttpHeaderName<?> header1, final Object value1,
                                                final HttpHeaderName<?> header2, final Object value2) {
        return Maps.of(header1, list(value1), header2, list(value2));
    }

    private Map<HttpHeaderName<?>, List<?>> map(final HttpHeaderName<?> header1, final Object value1,
                                                final HttpHeaderName<?> header2, final Object value2,
                                                final HttpHeaderName<?> header3, final Object value3) {
        return Maps.of(header1, list(value1), header2, list(value2), header3, list(value3));
    }

    private <T> List<T> list(final T...values) {
        return Lists.of(values);
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
                        .forEach(e -> parameters.put(HttpRequestParameterName.with(e.getKey().value()), e.getValue()));

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
        return JsonNodeUnmarshallContexts.basic(ExpressionNumberContexts.fake());
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<HateosResourceMappingRouter> type() {
        return Cast.to(HateosResourceMappingRouter.class);
    }
}
