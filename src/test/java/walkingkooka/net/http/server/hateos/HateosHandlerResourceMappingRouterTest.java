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
import walkingkooka.Binary;
import walkingkooka.Cast;
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.Range;
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
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpRequestParameterName;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.HttpResponses;
import walkingkooka.net.http.server.RecordingHttpResponse;
import walkingkooka.routing.RouterTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObjectNode;
import walkingkooka.tree.json.marshall.FromJsonNodeContext;
import walkingkooka.tree.json.marshall.FromJsonNodeContexts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HateosHandlerResourceMappingRouterTest extends HateosHandlerResourceMappingTestCase2<HateosHandlerResourceMappingRouter>
        implements RouterTesting<HateosHandlerResourceMappingRouter,
        HttpRequestAttribute<?>,
        BiConsumer<HttpRequest, HttpResponse>> {

    private final static String NO_BODY = null;
    private final static String NO_JSON = null;

    private final static BigInteger ID = BigInteger.valueOf(31);
    private final static BigInteger ID2 = BigInteger.valueOf(127);
    private final static Range<BigInteger> ALL = Range.all();
    private final static Range<BigInteger> RANGE1_2 = Range.greaterThanEquals(ID).and(Range.lessThanEquals(ID2));

    private final static TestHateosResource RESOURCE_IN = TestHateosResource.with(ID);
    private final static TestHateosResource RESOURCE_OUT = TestHateosResource.with(ID2);
    private final static TestHateosResource2 COLLECTION_RESOURCE_IN = TestHateosResource2.with(Range.singleton(ID));
    private final static TestHateosResource2 COLLECTION_RESOURCE_OUT = TestHateosResource2.with(RANGE1_2);

    private final static HttpMethod METHOD = HttpMethod.POST;

    // BAD REQUEST......................................................................................................

    @Test
    public void testBadRequestMissingBase() {
        this.routeAndCheck("/missing/",
                null);
    }

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
                HttpStatusCode.NOT_FOUND.setMessage("resource: unknown123, link relation: self"));
    }

    @Test
    public void testNotFoundUnknownHateosResourceId() {
        this.routeAndCheck("/api/unknown123/456",
                HttpStatusCode.NOT_FOUND.setMessage("resource: unknown123, link relation: self"));
    }

    @Test
    public void testNotFoundUnknownHateosResourceIdRelation() {
        this.routeAndCheck("/api/unknown123/456/contents",
                HttpStatusCode.NOT_FOUND.setMessage("resource: unknown123, link relation: contents"));
    }

    @Test
    public void testBadRequestInvalidRelation() {
        assertThrows(InvalidCharacterException.class, () -> {
            LinkRelation.with("!!");
        });
        this.routeAndCheck("/api/resource1/1/!!",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid link relation \"!!\""));
    }

    @Test
    public void testBadRequestMethodNotSupported() {
        final String method = "WRONGMETHOD";

        this.routeAndCheck(this.createRouter(),
                HttpMethod.with(method),
                "/api/resource1/0x123/contents",
                this.contentType(),
                NO_BODY,
                HttpStatusCode.METHOD_NOT_ALLOWED.setMessage(method + " resource: resource1, link relation: contents"));
    }

    @Test
    public void testBadRequestInvalidResourceId() {
        this.routeAndCheck("/api/resource1/@/contents",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid id \"@\""));
    }

    @Test
    public void testBadRequestMissingRangeBeginResourceId() {
        this.routeAndCheck("/api/resource1/-0x999/contents",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid range begin \"-0x999\""));
    }

    @Test
    public void testBadRequestInvalidRangeBeginResourceId() {
        this.routeAndCheck("/api/resource1/!-0x999/contents",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid range begin \"!-0x999\""));
    }

    @Test
    public void testBadRequestMissingRangeEndResourceId() {
        this.routeAndCheck("/api/resource1/0x999-/contents",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid range end \"0x999-\""));
    }

    @Test
    public void testBadRequestInvalidRangeEndResourceId() {
        this.routeAndCheck("/api/resource1/0x999-!/contents",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid range end \"0x999-!\""));
    }

    @Test
    public void testBadRequestInvalidRange() {
        this.routeAndCheck("/api/resource1/0x1-0x2-0x3/contents",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid character within range \"0x1-0x2-0x3\""));
    }

    @Test
    public void testBadRequestInvalidRange2() {
        this.routeAndCheck("/api/resource1/0x1\\-0x2-0x3-0x4/contents",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid character within range \"0x1\\\\-0x2-0x3-0x4\""));
    }

    @Test
    public void testBadRequestIdAndInvalidJson() {
        this.routeAndCheck("/api/resource1/0x1f/contents",
                "!invalid json",
                HttpStatusCode.BAD_REQUEST.setMessage("Invalid JSON: Unrecognized character '!' at (1,1) \"!invalid json\" expected NULL | BOOLEAN | STRING | NUMBER | ARRAY | OBJECT"));
    }

    @Test
    public void testBadRequestMissingIdAndInvalidJson() {
        this.routeAndCheck("/api/resource1//contents",
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

    // not implement....................................................................................................

    @Test
    public void testNotImplementedId() {
        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestHateosResource> handle(final Optional<BigInteger> id,
                                                                          final Optional<TestHateosResource> resource,
                                                                          final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   throw new UnsupportedOperationException();
                               }
                           },
                "/api/resource1/0x1/contents",
                NO_BODY,
                HttpStatusCode.NOT_IMPLEMENTED.status());
    }

    @Test
    public void testNotImplementedWildcard() {
        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestHateosResource2> handleCollection(final Range<BigInteger> ids,
                                                                                     final Optional<TestHateosResource2> resource,
                                                                                     final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   throw new UnsupportedOperationException();
                               }
                           },
                "/api/resource1/*/contents",
                NO_BODY,
                HttpStatusCode.NOT_IMPLEMENTED.status());
    }

    @Test
    public void testNotImplementedRange() {
        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestHateosResource2> handleCollection(final Range<BigInteger> ids,
                                                                                     final Optional<TestHateosResource2> resource,
                                                                                     final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   throw new UnsupportedOperationException();
                               }
                           },
                "/api/resource1/0x1-0x2/contents",
                NO_BODY,
                HttpStatusCode.NOT_IMPLEMENTED.status());
    }

    @Test
    public void testNotImplementedIdUnsupportedOperationExceptionWithMessage() {
        final String message = "message 123";

        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestHateosResource> handle(final Optional<BigInteger> id,
                                                                          final Optional<TestHateosResource> resource,
                                                                          final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   throw new UnsupportedOperationException(message);
                               }
                           },
                "/api/resource1/0x1/contents",
                NO_BODY,
                HttpStatusCode.NOT_IMPLEMENTED.status().setMessage(message));
    }

    // internal server error............................................................................................

    private final static String INTERNAL_SERVER_ERROR_MESSAGE = "Because 123";

    @Test
    public void testInternalServerErrorId() {
        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestHateosResource> handle(final Optional<BigInteger> id,
                                                                          final Optional<TestHateosResource> resource,
                                                                          final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   throw new RuntimeException(INTERNAL_SERVER_ERROR_MESSAGE);
                               }
                           },
                "/api/resource1/0x1/contents",
                NO_BODY,
                HttpStatusCode.INTERNAL_SERVER_ERROR.status().setMessage(INTERNAL_SERVER_ERROR_MESSAGE));
    }

    @Test
    public void testInternalServerErrorWildcard() {
        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestHateosResource2> handleCollection(final Range<BigInteger> ids,
                                                                                     final Optional<TestHateosResource2> resource,
                                                                                     final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   throw new RuntimeException(INTERNAL_SERVER_ERROR_MESSAGE);
                               }
                           },
                "/api/resource1/*/contents",
                NO_BODY,
                HttpStatusCode.INTERNAL_SERVER_ERROR.status().setMessage(INTERNAL_SERVER_ERROR_MESSAGE));
    }

    @Test
    public void testRangeInternalServerErrorRange() {
        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestHateosResource2> handleCollection(final Range<BigInteger> ids,
                                                                                     final Optional<TestHateosResource2> resource,
                                                                                     final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   throw new RuntimeException(INTERNAL_SERVER_ERROR_MESSAGE);
                               }
                           },
                "/api/resource1/0x1-0x2/contents",
                NO_BODY,
                HttpStatusCode.INTERNAL_SERVER_ERROR.status().setMessage(INTERNAL_SERVER_ERROR_MESSAGE));
    }

    @Test
    public void testInternalServerErrorExceptionWithoutMessageId() {
        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestHateosResource> handle(final Optional<BigInteger> id,
                                                                          final Optional<TestHateosResource> resource,
                                                                          final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   throw new RuntimeException();
                               }
                           },
                "/api/resource1/0x1/contents",
                NO_BODY,
                HttpStatusCode.INTERNAL_SERVER_ERROR.status());
    }

    // id request.......................................................................................................

    private final static String RESOURCE_SUCCESSFUL = METHOD + " resource successful";

    @Test
    public void testRequestResourceBodyAbsentId() {
        this.routeAndCheck(new FakeHateosHandler<>() {
                               @Override
                               public Optional<TestHateosResource> handle(final Optional<BigInteger> id,
                                                                          final Optional<TestHateosResource> resource,
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
                               public Optional<TestHateosResource> handle(final Optional<BigInteger> id,
                                                                          final Optional<TestHateosResource> resource,
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
                               public Optional<TestHateosResource> handle(final Optional<BigInteger> id,
                                                                          final Optional<TestHateosResource> resource,
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
                    public Optional<TestHateosResource> handle(final Optional<BigInteger> id,
                                                               final Optional<TestHateosResource> resource,
                                                               final Map<HttpRequestAttribute<?>, Object> parameters) {
                        checkId(id);
                        checkResource(resource, Optional.of(RESOURCE_IN));

                        return Optional.empty();
                    }
                }),
                METHOD,
                "/api/resource1/0x123/contents",
                this.contentTypeUtf16(),
                this.toJson(RESOURCE_IN),
                HttpStatusCode.NO_CONTENT.status().setMessage(RESOURCE_SUCCESSFUL));
    }

    private void checkId(final Optional<BigInteger> id) {
        checkId(id, Optional.of(BigInteger.valueOf(0x123)));
    }

    private void checkId(final Optional<BigInteger> id, final Optional<BigInteger> expected) {
        assertEquals(expected, id, "id");
    }

    // wildcard request.................................................................................................

    @Test
    public void testRequestResourceBodyAbsentWildcard() {
        this.routeAndCheck(new FakeHateosHandler<>() {

                               @Override
                               public Optional<TestHateosResource2> handleCollection(final Range<BigInteger> id,
                                                                                     final Optional<TestHateosResource2> resource,
                                                                                     final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   checkWildcard(id);
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
                               public Optional<TestHateosResource2> handleCollection(final Range<BigInteger> id,
                                                                                     final Optional<TestHateosResource2> resource,
                                                                                     final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   checkWildcard(id);
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
                               public Optional<TestHateosResource2> handleCollection(final Range<BigInteger> id,
                                                                                     final Optional<TestHateosResource2> resource,
                                                                                     final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   checkWildcard(id);
                                   checkResource(resource, Optional.of(COLLECTION_RESOURCE_IN));

                                   return Optional.empty();
                               }
                           },
                "/api/resource1/*/contents",
                this.toJson(COLLECTION_RESOURCE_IN),
                HttpStatusCode.NO_CONTENT.status().setMessage(RESOURCE_SUCCESSFUL));
    }

    private void checkWildcard(final Range<BigInteger> id) {
        checkId(id, Range.all());
    }

    // range request....................................................................................................

    @Test
    public void testRequestResourceBodyAbsentRange() {
        this.routeAndCheck(new FakeHateosHandler<>() {

                               @Override
                               public Optional<TestHateosResource2> handleCollection(final Range<BigInteger> id,
                                                                                     final Optional<TestHateosResource2> resource,
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
                               public Optional<TestHateosResource2> handleCollection(final Range<BigInteger> id,
                                                                                     final Optional<TestHateosResource2> resource,
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
                               public Optional<TestHateosResource2> handleCollection(final Range<BigInteger> id,
                                                                                     final Optional<TestHateosResource2> resource,
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

    private <H extends HateosResource<?>> void checkResource(final Optional<H> resource, final Optional<H> expected) {
        assertEquals(expected, resource, "resource");
    }

    // response id......................................................................................................

    @Test
    public void testResponseResourceBodyAbsent() {
        this.routeAndCheck(new FakeHateosHandler<>() {

                               @Override
                               public Optional<TestHateosResource> handle(final Optional<BigInteger> id,
                                                                          final Optional<TestHateosResource> resource,
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
                               public Optional<TestHateosResource> handle(final Optional<BigInteger> id,
                                                                          final Optional<TestHateosResource> resource,
                                                                          final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   return Optional.of(RESOURCE_OUT);
                               }
                           },
                "/api/resource1/0x123/contents",
                NO_BODY,
                HttpStatusCode.OK.status().setMessage(RESOURCE_SUCCESSFUL),
                this.httpEntity(RESOURCE_OUT));
    }

    // response collection..............................................................................................

    @Test
    public void testResponseResourceBodyAbsentCollection() {
        this.routeAndCheck(new FakeHateosHandler<>() {

                               @Override
                               public Optional<TestHateosResource2> handleCollection(final Range<BigInteger> id,
                                                                                     final Optional<TestHateosResource2> resource,
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
                               public Optional<TestHateosResource2> handleCollection(final Range<BigInteger> id,
                                                                                     final Optional<TestHateosResource2> resource,
                                                                                     final Map<HttpRequestAttribute<?>, Object> parameters) {
                                   return Optional.of(COLLECTION_RESOURCE_OUT);
                               }
                           },
                "/api/resource1/0x123-0x456/contents",
                NO_BODY,
                HttpStatusCode.OK.status().setMessage(RESOURCE_SUCCESSFUL),
                this.httpEntity(COLLECTION_RESOURCE_OUT));
    }

    // HELPERS .........................................................................................................

    @Override
    public HateosHandlerResourceMappingRouter createRouter() {
        return this.createRouter(new FakeHateosHandler<>());
    }

    private HateosHandlerResourceMappingRouter createRouter(final HateosHandler<BigInteger, TestHateosResource, TestHateosResource2> handler) {
        final HateosHandlerResourceMapping mapping = this.mapping()
                .set(LinkRelation.CONTENTS, METHOD, handler);
        return Cast.to(HateosHandlerResourceMapping.router(this.baseUrl(),
                this.hateosContentType(),
                Sets.of(mapping)));
    }

    private AbsoluteUrl baseUrl() {
        return Url.parseAbsolute("http://www.example.com/api");
    }

    private HateosHandlerResourceMapping mapping() {
        return HateosHandlerResourceMapping.with(HateosResourceName.with("resource1"),
                (s) -> {
                    if (!s.startsWith("0x")) {
                        throw new IllegalArgumentException("Invalid id=" + CharSequences.quoteIfChars(s));
                    }
                    return BigInteger.valueOf(Integer.parseInt(s.substring(2), 16));
                },
                TestHateosResource.class,
                TestHateosResource2.class);
    }

    private MediaType contentType() {
        return this.hateosContentType().contentType();
    }

    private MediaType contentTypeUtf16() {
        return this.contentType().setCharset(CharsetName.UTF_16);
    }

    private HateosContentType<JsonNode> hateosContentType() {
        return HateosContentType.json(this.fromJsonNodeContext(), this.toJsonNodeContext());
    }

    private String toJson(final HateosResource<?> resource) {
        return this.toJsonNodeContext().toJsonNode(resource).toString();
    }

    private HttpEntity[] httpEntity(final HateosResource<?> resource) {
        return this.httpEntity(resource, this.contentType());
    }

    private HttpEntity[] httpEntity(final HateosResource<?> resource,
                                    final MediaType contentType) {
        return this.httpEntity(this.toJson(resource), contentType);
    }

    private HttpEntity[] httpEntity(final String body,
                                    final MediaType contentType) {
        HttpEntity[] entities = new HttpEntity[0];

        if (!CharSequences.isNullOrEmpty(body)) {
            final CharsetName charsetName = MediaTypeParameterName.CHARSET.parameterValue(contentType).orElse(CharsetName.UTF_8);
            final byte[] bytes = this.bytes(body, contentType);

            final Map<HttpHeaderName<?>, Object> headers = Maps.sorted();
            headers.put(HttpHeaderName.CONTENT_TYPE, contentType.setCharset(charsetName));
            headers.put(HttpHeaderName.CONTENT_LENGTH, Long.valueOf(bytes(body, contentType).length));

            entities = new HttpEntity[]{HttpEntity.with(headers, Binary.with(bytes))};
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

    private void routeAndCheck(final HateosHandler<BigInteger, TestHateosResource, TestHateosResource2> handler,
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

    private void routeAndCheck(final HateosHandlerResourceMappingRouter router,
                               final String url,
                               final String body,
                               final HttpStatus status,
                               final HttpEntity... entities) {
        this.routeAndCheck(router,
                METHOD,
                url,
                contentType(),
                body,
                status,
                entities);
    }

    private void routeAndCheck(final HateosHandlerResourceMappingRouter router,
                               final HttpMethod method,
                               final String url,
                               final MediaType contentType,
                               final String body,
                               final HttpStatus status,
                               final HttpEntity... entities) {
        final HttpRequest request = this.request(method,
                url,
                contentType,
                body);
        final RecordingHttpResponse response = HttpResponses.recording();
        final Optional<BiConsumer<HttpRequest, HttpResponse>> handle = router.route(request.routerParameters());
        handle.ifPresent(h -> h.accept(request, response));

        final HttpResponse expected = HttpResponses.recording();

        if (null != status) {
            expected.setStatus(status);
        }

        Arrays.stream(entities)
                .forEach(expected::addEntity);

        assertEquals(expected,
                response,
                () -> request.toString());
    }

    private HttpRequest request(final HttpMethod method,
                                final String url,
                                final MediaType contentType,
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
            public Map<HttpHeaderName<?>, Object> headers() {
                final Map<HttpHeaderName<?>, Object> headers = Maps.sorted();
                headers.put(HttpHeaderName.ACCEPT_CHARSET, AcceptCharset.parse(MediaTypeParameterName.CHARSET.parameterValue(contentType).orElse(CharsetName.UTF_8).toHeaderText()));
                headers.put(HttpHeaderName.CONTENT_TYPE, contentType);
                return headers;
            }

            @Override
            public byte[] body() {
                return bytes(body, contentType);
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
                         final MediaType contentType) {
        return null != body ?
                body.getBytes(MediaTypeParameterName.CHARSET.parameterValue(contentType).orElse(CharsetName.UTF_8).charset().get()) :
                null;
    }

    private FromJsonNodeContext fromJsonNodeContext() {
        return FromJsonNodeContexts.basic();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<HateosHandlerResourceMappingRouter> type() {
        return Cast.to(HateosHandlerResourceMappingRouter.class);
    }
}
