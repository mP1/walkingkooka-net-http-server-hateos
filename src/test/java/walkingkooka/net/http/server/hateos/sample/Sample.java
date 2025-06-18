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

package walkingkooka.net.http.server.hateos.sample;

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.UrlPath;
import walkingkooka.net.header.AcceptCharset;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.net.http.server.FakeHttpRequest;
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpRequestParameterName;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.HttpResponses;
import walkingkooka.net.http.server.hateos.FakeHateosResource;
import walkingkooka.net.http.server.hateos.FakeHateosResourceHandler;
import walkingkooka.net.http.server.hateos.FakeHateosResourceHandlerContext;
import walkingkooka.net.http.server.hateos.HateosResourceMapping;
import walkingkooka.net.http.server.hateos.HateosResourceName;
import walkingkooka.net.http.server.hateos.HateosResourceSelection;
import walkingkooka.route.Router;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Sample {

    public static void main(final String[] args) {
        final Sample sample = new Sample();
        sample.testAssertEquals();
        sample.testHateosResourceName();
        sample.testNewHateosHandler();
        sample.testNewHateosResource();
        sample.testHateosResourceMapping();
    }

    public void testAssertEquals() {
        checkEquals(
                1,
                1
        );
    }

    public void testHateosResourceName() {
        final String name = "name123";

        checkEquals(
                name,
                HateosResourceName.with(name)
                        .value()
        );
    }

    public void testNewHateosHandler() {
        new FakeHateosResourceHandler<String, String, Collection<String>, TestHateosResourceHandlerContext>() {
        };
    }

    public void testNewHateosResource() {
        new FakeHateosResource<String>() {
        };
    }

    public void testHateosResourceMapping() {
        final HateosResourceMapping<BigInteger, TestResource, TestResource, TestHateosResource, TestHateosResourceHandlerContext> mapping = HateosResourceMapping.with(HateosResourceName.with("resource1"),
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
                new FakeHateosResourceHandler<>() {
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
                UrlPath.ROOT,
                Sets.of(mapping),
                Indentation.SPACES2,
                LineEnding.NL,
                new TestHateosResourceHandlerContext()
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
                return Url.parseRelative("/api/resource1/0x123/contents");
            }

            @Override
            public Map<HttpHeaderName<?>, List<?>> headers() {
                return Maps.of(
                        HttpHeaderName.CONTENT_TYPE, Lists.of(TestHateosResourceHandlerContext.CONTENT_TYPE),
                        HttpHeaderName.ACCEPT, Lists.of(TestHateosResourceHandlerContext.CONTENT_TYPE.accept()),
                        HttpHeaderName.ACCEPT_CHARSET, Lists.of(AcceptCharset.parse("utf-8"))
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
        final HttpHandler httpHandler = router.route(request.routerParameters())
                .orElseThrow(() -> new Error("Unable to route"));

        final HttpResponse response = HttpResponses.recording();
        httpHandler.handle(request, response);
        checkEquals(
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

    private HateosResourceName resourceName() {
        return HateosResourceName.with("abc123");
    }

    private Function<String, BigInteger> stringToId() {
        return BigInteger::new;
    }

    private Class<TestResource> valueType() {
        return TestResource.class;
    }

    private Class<TestResource2> collectionType() {
        return TestResource2.class;
    }

    private Class<TestHateosResource> resourceType() {
        return TestHateosResource.class;
    }

    static class TestHateosResourceHandlerContext extends FakeHateosResourceHandlerContext {

        final static MediaType CONTENT_TYPE = MediaType.parse("application/test-json");

        @Override
        public MediaType contentType() {
            return CONTENT_TYPE;
        }

        @Override
        public JsonNode marshall(final Object value) {
            return JsonNodeMarshallContexts.basic()
                    .marshall(value);
        }
    }

    static void checkEquals(final Object expected,
                            final Object actual) {
        assertEquals(
                expected,
                actual
        );
    }
}
