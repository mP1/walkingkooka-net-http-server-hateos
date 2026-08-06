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
import walkingkooka.collect.set.Sets;
import walkingkooka.datetime.HasLastModifiedTesting;
import walkingkooka.datetime.HasOptionalLastModifiedTesting;
import walkingkooka.net.Url;
import walkingkooka.net.UrlPath;
import walkingkooka.net.header.Accept;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.net.http.server.HttpHandlerTesting;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpRequests;
import walkingkooka.net.http.server.HttpResponses;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextTesting;

import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;

public final class HateosResourceMappingsRouterHttpHandlerTest extends HateosResourceMappingsTestCase<HateosResourceMappingsRouterHttpHandler<HateosHandlerContext>>
    implements HttpHandlerTesting<HateosResourceMappingsRouterHttpHandler<HateosHandlerContext>, HateosHandlerContext>,
    HasLastModifiedTesting,
    HasOptionalLastModifiedTesting,
    JsonNodeMarshallContextTesting {

    @Test
    public void testHandleHateosResource() {
        this.handleAndCheck(
            HateosResourceMappingsRouterHttpHandler.with(
                HateosResourceMappingsRouter.with(
                    UrlPath.ROOT,
                    Sets.of(
                        HateosResourceMappings.with(
                            HateosResourceName.with("TestResource"),
                            (s, x) -> {
                                return HateosResourceSelection.one(
                                    new BigInteger(s)
                                );
                            },
                            TestResource.class,
                            TestResource.class,
                            TestHateosResource.class,
                            HateosHandlerContext.class
                        ).setHateosResourceHandler(
                            LinkRelation.SELF,
                            HttpMethod.GET,
                            new FakeHateosResourceHandler<>() {

                                @Override
                                public Optional<TestResource> handleOne(final BigInteger id,
                                                                        final Optional<TestResource> resource,
                                                                        final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                        final UrlPath path,
                                                                        final HateosHandlerContext context) {
                                    HateosResourceHandler.checkPathEmpty(path);

                                    return Optional.of(
                                        TestResource.with(
                                            TestHateosResource.with(
                                                BigInteger.valueOf(31)
                                            )
                                        )
                                    );
                                }
                            }
                        )
                    )
                )
            ),
            HttpRequests.get(
                HttpTransport.UNSECURED,
                Url.parseRelative("/TestResource/1"),
                HttpProtocolVersion.VERSION_1_0,
                HttpEntity.EMPTY.addHeader(
                    HttpHeaderName.ACCEPT,
                    Accept.DEFAULT
                )
            ),
            new FakeHateosHandlerContext() {

                @Override
                public Indentation indentation() {
                    return HateosResourceMappingsRouterHttpHandlerTest.INDENTATION;
                }

                @Override
                public LineEnding lineEnding() {
                    return EOL;
                }

                @Override
                public JsonNode marshall(final Object value) {
                    return JSON_NODE_MARSHALL_CONTEXT.marshall(value);
                }
            },
            HttpResponses.parse(
                "HTTP/1.0 200 OK\r\n" +
                    "Content-Length: 68\r\n" +
                    "Content-Type: application/json; charset=UTF-8\r\n" +
                    "X-Content-Type-Name: TestResource\r\n" +
                    "\r\n" +
                    "{\n" +
                    "  \"type\": \"test-HateosResource\",\n" +
                    "  \"value\": {\n" +
                    "    \"id\": \"31\"\n" +
                    "  }\n" +
                    "}"
            )
        );
    }

    @Test
    public void testHandleHateosResourceWithHasLastModified() {
        this.handleAndCheck(
            HateosResourceMappingsRouterHttpHandler.with(
                HateosResourceMappingsRouter.with(
                    UrlPath.ROOT,
                    Sets.of(
                        HateosResourceMappings.with(
                            HateosResourceName.with("TestResource2"),
                            (s, x) -> {
                                return HateosResourceSelection.one(
                                    new BigInteger(s)
                                );
                            },
                            TestResource2.class,
                            TestResource2.class,
                            TestHateosResource.class,
                            HateosHandlerContext.class
                        ).setHateosResourceHandler(
                            LinkRelation.SELF,
                            HttpMethod.GET,
                            new FakeHateosResourceHandler<>() {

                                @Override
                                public Optional<TestResource2> handleOne(final BigInteger id,
                                                                         final Optional<TestResource2> resource,
                                                                         final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                         final UrlPath path,
                                                                         final HateosHandlerContext context) {
                                    HateosResourceHandler.checkPathEmpty(path);

                                    return Optional.of(
                                        TestResource2.with(
                                            TestHateosResource.with(
                                                BigInteger.valueOf(31)
                                            )
                                        )
                                    );
                                }
                            }
                        )
                    )
                )
            ),
            HttpRequests.get(
                HttpTransport.UNSECURED,
                Url.parseRelative("/TestResource2/1"),
                HttpProtocolVersion.VERSION_1_0,
                HttpEntity.EMPTY.addHeader(
                    HttpHeaderName.ACCEPT,
                    Accept.DEFAULT
                )
            ),
            new FakeHateosHandlerContext() {

                @Override
                public Indentation indentation() {
                    return HateosResourceMappingsRouterHttpHandlerTest.INDENTATION;
                }

                @Override
                public LineEnding lineEnding() {
                    return EOL;
                }

                @Override
                public JsonNode marshall(final Object value) {
                    return JSON_NODE_MARSHALL_CONTEXT.marshall(value);
                }
            },
            HttpResponses.parse(
                "HTTP/1.0 200 OK\r\n" +
                    "Content-Length: 68\r\n" +
                    "Content-Type: application/json; charset=UTF-8\r\n" +
                    "Last-Modified: Fri, 31 Dec 1999 12:58:59 GMT\r\n" +
                    "X-Content-Type-Name: TestResource2\r\n" +
                    "\r\n" +
                    "{\n" +
                    "  \"type\": \"test-HateosResource\",\n" +
                    "  \"value\": {\n" +
                    "    \"id\": \"31\"\n" +
                    "  }\n" +
                    "}"
            )
        );
    }

    @Test
    public void testHandleHateosResourceWithHasOptionalLastModified() {
        this.handleAndCheck(
            HateosResourceMappingsRouterHttpHandler.with(
                HateosResourceMappingsRouter.with(
                    UrlPath.ROOT,
                    Sets.of(
                        HateosResourceMappings.with(
                            HateosResourceName.with("TestResource"),
                            (s, x) -> {
                                return HateosResourceSelection.one(
                                    new BigInteger(s)
                                );
                            },
                            TestResource.class,
                            TestResource.class,
                            TestHateosResource.class,
                            HateosHandlerContext.class
                        ).setHateosResourceHandler(
                            LinkRelation.SELF,
                            HttpMethod.GET,
                            new FakeHateosResourceHandler<>() {

                                @Override
                                public Optional<TestResource> handleOne(final BigInteger id,
                                                                        final Optional<TestResource> resource,
                                                                        final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                        final UrlPath path,
                                                                        final HateosHandlerContext context) {
                                    HateosResourceHandler.checkPathEmpty(path);

                                    return Optional.of(
                                        TestResource.with(
                                            TestHateosResource.with(
                                                BigInteger.valueOf(31)
                                            ),
                                            HateosResourceMappingsRouterHttpHandlerTest.NOW
                                        )
                                    );
                                }
                            }
                        )
                    )
                )
            ),
            HttpRequests.get(
                HttpTransport.UNSECURED,
                Url.parseRelative("/TestResource/1"),
                HttpProtocolVersion.VERSION_1_0,
                HttpEntity.EMPTY.addHeader(
                    HttpHeaderName.ACCEPT,
                    Accept.DEFAULT
                )
            ),
            new FakeHateosHandlerContext() {

                @Override
                public Indentation indentation() {
                    return HateosResourceMappingsRouterHttpHandlerTest.INDENTATION;
                }

                @Override
                public LineEnding lineEnding() {
                    return EOL;
                }

                @Override
                public JsonNode marshall(final Object value) {
                    return JSON_NODE_MARSHALL_CONTEXT.marshall(value);
                }
            },
            HttpResponses.parse(
                "HTTP/1.0 200 OK\r\n" +
                    "Content-Length: 68\r\n" +
                    "Content-Type: application/json; charset=UTF-8\r\n" +
                    "Last-Modified: Fri, 31 Dec 1999 12:58:59 GMT\r\n" +
                    "X-Content-Type-Name: TestResource\r\n" +
                    "\r\n" +
                    "{\n" +
                    "  \"type\": \"test-HateosResource\",\n" +
                    "  \"value\": {\n" +
                    "    \"id\": \"31\"\n" +
                    "  }\n" +
                    "}"
            )
        );
    }

    @Override
    public HateosResourceMappingsRouterHttpHandler<HateosHandlerContext> createHttpHandler() {
        return HateosResourceMappingsRouterHttpHandler.with(
            HateosResourceMappingsRouter.with(
                UrlPath.ROOT,
                Sets.empty()
            )
        );
    }

    @Override
    public HateosHandlerContext createContext() {
        return HateosHandlerContexts.fake();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final HateosResourceMappingsRouter<HateosHandlerContext> router = HateosResourceMappingsRouter.with(
            UrlPath.ROOT,
            Sets.empty()
        );

        this.toStringAndCheck(
            HateosResourceMappingsRouterHttpHandler.with(router),
            router.toString()
        );
    }

    // ClassTesting......................................................................................................

    @Override
    public Class<HateosResourceMappingsRouterHttpHandler<HateosHandlerContext>> type() {
        return Cast.to(HateosResourceMappingsRouterHttpHandler.class);
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return HateosResourceMappings.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return HttpHandler.class.getSimpleName();
    }
}
