package test;

import com.google.gwt.junit.client.GWTTestCase;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.header.AcceptCharset;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.LinkRelation;
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
import walkingkooka.net.http.server.hateos.FakeHateosResourceHandler;
import walkingkooka.net.http.server.hateos.FakeHateosResource;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.net.http.server.hateos.HateosResourceMapping;
import walkingkooka.net.http.server.hateos.HateosResourceName;
import walkingkooka.net.http.server.hateos.HateosResourceSelection;
import walkingkooka.route.Router;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.BigInteger;
import java.math.MathContext;
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
        assertEquals(
                1,
                1
        );
    }

    public void testHateosResourceName()  {
        final String name = "name123";

        assertEquals(
                name,
                HateosResourceName.with(name)
                        .value()
        );
    }

    public void testNewHateosHandler()  {
        new FakeHateosResourceHandler<String, String, Collection<String>>() {
        };
    }

    public void testNewHateosResource()  {
        new FakeHateosResource<String>() {
        };
    }

    public void testHateosResourceMapping()  {
        final HateosResourceMapping<BigInteger, TestResource, TestResource, TestHateosResource> mapping = HateosResourceMapping.with(HateosResourceName.with("resource1"),
                        (s) -> {
                            return HateosResourceSelection.one(BigInteger.valueOf(Integer.parseInt(s.substring(2), 16))); // assumes hex digit in url
                        },
                        TestResource.class,
                        TestResource.class,
                        TestHateosResource.class)
                .setHateosResourceHandler(
                        LinkRelation.CONTENTS,
                        HttpMethod.POST,
                        new FakeHateosResourceHandler<>() {
                            @Override
                            public Optional<TestResource> handleOne(final BigInteger id,
                                                                    final Optional<TestResource> resource,
                                                                    final Map<HttpRequestAttribute<?>, Object> parameters) {
                                return Optional.of(TestResource.with(TestHateosResource.with(BigInteger.valueOf(31))));
                            }
                        }
                );

        final Router<HttpRequestAttribute<?>, HttpHandler> router = HateosResourceMapping.router(
                AbsoluteUrl.parseAbsolute("http://www.example.com/api"),
                HateosContentType.json(
                        JsonNodeUnmarshallContexts.basic(
                                ExpressionNumberKind.DEFAULT,
                                MathContext.DECIMAL32
                        ),
                        JsonNodeMarshallContexts.basic()
                ),
                Sets.of(mapping),
                Indentation.SPACES2,
                LineEnding.NL
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
                        HttpHeaderName.CONTENT_TYPE, Lists.of(HateosContentType.JSON_CONTENT_TYPE),
                        HttpHeaderName.ACCEPT, Lists.of(HateosContentType.JSON_CONTENT_TYPE.accept()),
                        HttpHeaderName.ACCEPT_CHARSET, Lists.of(AcceptCharset.parse("utf-8"))
                );
            }

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
        assertEquals(
                "{\n" +
                "  \"type\": \"test-HateosResource\",\n" +
                "  \"value\": {\n" +
                "    \"id\": \"31\"\n" +
                "  }\n" +
                "}",
                response.entities()
                        .get(0)
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
}
