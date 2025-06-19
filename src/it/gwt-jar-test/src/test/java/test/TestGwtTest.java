package test;

import com.google.gwt.junit.client.GWTTestCase;

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

@walkingkooka.j2cl.locale.LocaleAware
public class TestGwtTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "test.Test";
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
        final HateosResourceMapping<BigInteger, TestResource3, TestResource3, TestHateosResource3, TestHateosResourceHandlerContext> mapping = HateosResourceMapping.with(
                HateosResourceName.with("resource1"),
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
                TestResource3.class,
                TestResource3.class,
                TestHateosResource3.class,
                TestHateosResourceHandlerContext.class
        ).setHateosResourceHandler(
                LinkRelation.CONTENTS,
                HttpMethod.POST,
                new FakeHateosResourceHandler<>() {
                    @Override
                    public Optional<TestResource3> handleOne(final BigInteger id,
                                                             final Optional<TestResource3> resource,
                                                             final Map<HttpRequestAttribute<?>, Object> parameters,
                                                             final UrlPath path,
                                                             final TestHateosResourceHandlerContext context) {
                        return Optional.of(
                                TestResource3.with(
                                        TestHateosResource3.with(
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
                        "  \"type\": \"test-HateosResource3\",\n" +
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

    private Class<TestResource3> valueType() {
        return TestResource3.class;
    }

    private Class<TestResource4> collectionType() {
        return TestResource4.class;
    }

    private Class<TestHateosResource3> resourceType() {
        return TestHateosResource3.class;
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
