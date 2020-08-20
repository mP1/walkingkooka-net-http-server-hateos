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

import walkingkooka.Binary;
import walkingkooka.ToStringBuilder;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.UrlPathName;
import walkingkooka.net.header.AcceptCharset;
import walkingkooka.net.header.CharsetName;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.header.MediaTypeParameterName;
import walkingkooka.net.header.NotAcceptableHeaderException;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpRequestAttributes;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.Node;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Handles dispatching a request, after extracting ids and parsing request bodies.
 */
final class HateosResourceMappingRouterBiConsumerRequest {

    static HateosResourceMappingRouterBiConsumerRequest with(final HttpRequest request,
                                                             final HttpResponse response,
                                                             final HateosResourceMappingRouter router) {
        return new HateosResourceMappingRouterBiConsumerRequest(request,
                response,
                router);
    }

    private HateosResourceMappingRouterBiConsumerRequest(final HttpRequest request,
                                                         final HttpResponse response,
                                                         final HateosResourceMappingRouter router) {
        super();
        this.request = request;
        this.response = response;
        this.router = router;
    }

    final void dispatch() {
        this.parameters = this.request.routerParameters();

        Loop:

        do {
            // verify correctly dispatched...
            int pathIndex = this.router.consumeBasePath(this.parameters);
            if (-1 == pathIndex) {
                this.badRequest("Bad routing");
                break;
            }

            // extract resource name....................................................................................
            final String resourceNameString = this.pathComponentOrNull(pathIndex);
            if (CharSequences.isNullOrEmpty(resourceNameString)) {
                this.badRequest("Missing resource name");
                break;
            }

            HateosResourceName resourceName;
            try {
                resourceName = HateosResourceName.with(resourceNameString);
            } catch (final RuntimeException invalid) {
                this.badRequest("Invalid resource name " + CharSequences.quoteAndEscape(resourceNameString));
                break;
            }

            // id or range .............................................................................................
            final String idOrRange = this.pathComponentOrNull(pathIndex + 1);
            if (CharSequences.isNullOrEmpty(idOrRange)) {
                this.idMissing(resourceName, pathIndex);
                break;
            }
            if ("*".equals(idOrRange)) {
                this.wildcard(resourceName, pathIndex);
                break;
            }

            boolean escaped = false;
            final StringBuilder component = new StringBuilder();
            String begin = null;

            for (char c : idOrRange.toCharArray()) {
                if (escaped) {
                    escaped = false;
                    component.append(c);
                    continue;
                }
                if ('\\' == c) {
                    escaped = true;
                    continue;
                }
                if (HateosResource.HATEOS_LINK_RANGE_SEPARATOR == c) {
                    if (null == begin) {
                        begin = component.toString();
                        component.setLength(0);
                        continue;
                    }
                    // second dash found error!!!
                    this.badRequest("Invalid character within range " + CharSequences.quoteAndEscape(idOrRange));
                    break Loop;
                }
                component.append(c);
            }

            if (null == begin) {
                this.id(resourceName, component.toString(), pathIndex);
                break;
            }
            this.collection(resourceName,
                    begin,
                    component.toString(),
                    idOrRange,
                    pathIndex);
        } while (false);
    }

    /**
     * Fetches the path component at the path index or returns null.
     */
    private String pathComponentOrNull(final int pathIndex) {
        return HttpRequestAttributes.pathComponent(pathIndex).parameterValue(this.parameters)
                .map(v -> v.value())
                .orElse(null);
    }

    // ID MISSING.......................................................................................................

    private void idMissing(final HateosResourceName resourceName,
                           final int pathIndex) {
        final LinkRelation<?> linkRelation = this.linkRelationOrDefaultOrResponseBadRequest(pathIndex + 2);
        if (null != linkRelation) {
            final HateosResourceMapping<?, ?, ?, ?> mapping = this.handlersOrResponseNotFound(resourceName, linkRelation);
            if (null != mapping) {
                mapping.handleId0(Optional.empty(), linkRelation, this);
            }
        }
    }

    // ID...............................................................................................................

    private void id(final HateosResourceName resourceName,
                    final String id,
                    final int pathIndex) {
        final LinkRelation<?> linkRelation = this.linkRelationOrDefaultOrResponseBadRequest(pathIndex + 2);
        if (null != linkRelation) {
            this.id0(resourceName, id, linkRelation);
        }
    }

    private void id0(final HateosResourceName resourceName,
                     final String idText,
                     final LinkRelation<?> linkRelation) {
        final HateosResourceMapping<?, ?, ?, ?> mapping = this.handlersOrResponseNotFound(resourceName, linkRelation);
        if (null != mapping) {
            mapping.handleId(idText, linkRelation, this);
        }
    }

    // WILDCARD.........................................................................................................

    private void wildcard(final HateosResourceName resourceName,
                          final int pathIndex) {
        final LinkRelation<?> linkRelation = this.linkRelationOrDefaultOrResponseBadRequest(pathIndex + 2);
        if (null != linkRelation) {
            final HateosResourceMapping<?, ?, ?, ?> mapping = this.handlersOrResponseNotFound(resourceName, linkRelation);
            if (null != mapping) {
                mapping.handleIdRange(linkRelation, this);
            }
        }
    }

    // COLLECTION.......................................................................................................

    /**
     * Dispatches a collection resource request, but with the range outstanding and unparsed.
     */
    private void collection(final HateosResourceName resourceName,
                            final String begin,
                            final String end,
                            final String rangeText,
                            final int pathIndex) {
        final LinkRelation<?> linkRelation = this.linkRelationOrDefaultOrResponseBadRequest(pathIndex + 2);
        if (null != linkRelation) {
            final HateosResourceMapping<?, ?, ?, ?> mapping = this.handlersOrResponseNotFound(resourceName, linkRelation);
            if (null != mapping) {
                mapping.handleIdRange(begin,
                        end,
                        rangeText,
                        linkRelation,
                        this);
            }
        }
    }

    // HELPERS .........................................................................................................

    /**
     * If not empty parse the relation otherwise return a default of {@link LinkRelation#SELF}, a null indicates an invalid relation.
     */
    private LinkRelation<?> linkRelationOrDefaultOrResponseBadRequest(final int pathIndex) {
        LinkRelation<?> relation = LinkRelation.SELF;

        final Optional<UrlPathName> relationPath = HttpRequestAttributes.pathComponent(pathIndex)
                .parameterValue(this.parameters);

        if (relationPath.isPresent()) {
            final String relationString = relationPath.get().value();
            if (!CharSequences.isNullOrEmpty(relationString)) {
                try {
                    relation = LinkRelation.with(relationString);
                } catch (final RuntimeException invalid) {
                    relation = null;
                    this.badRequest("Invalid link relation " + CharSequences.quoteAndEscape(relationString));
                }
            }
        }

        return relation;
    }

    /**
     * Reads and returns the body as text, with null signifying an error occured and a bad request response set.
     */
    String resourceTextOrBadRequest() {
        String text = ""; // null == bad request

        final HttpRequest request = this.request;
        final Map<HttpHeaderName<?>, Object> headers = request.headers();
        final Optional<Long> contentLength = HttpHeaderName.CONTENT_LENGTH.headerValue(headers);
        final byte[] body = request.body();
        if (null == body || body.length == 0) {
            // no body present any content-length != 0 then bad request.
            final Long contentLengthValue = contentLength.orElse(0L);
            if(contentLengthValue.longValue() != 0) {
                this.badRequest("Body absent with " + HttpHeaderName.CONTENT_LENGTH + ": " + contentLength.get());
                text = null;
            } else {
                text = "";
            }
        } else {
            // body present content-length must match
            if(false == contentLength.isPresent()) {
                this.setStatus(HttpStatusCode.LENGTH_REQUIRED.status());
                text = null;
            } else {
                Charset charset = Charset.defaultCharset();

                final Optional<MediaType> contentType = HttpHeaderName.CONTENT_TYPE.headerValue(headers);
                if (contentType.isPresent()) {
                    final Optional<CharsetName> possible = MediaTypeParameterName.CHARSET.parameterValue(contentType.get());
                    if (possible.isPresent()) {
                        charset = possible.get().charset().orElse(charset);
                    }
                }

                try (final StringReader reader = new StringReader(new String(body, charset))) {
                    final StringBuilder b = new StringBuilder();
                    final char[] buffer = new char[contentLength.get().intValue()];

                    for (; ; ) {
                        final int fill = reader.read(buffer);
                        if (-1 == fill) {
                            break;
                        }
                        b.append(buffer, 0, fill);
                    }

                    text = b.toString();
                } catch (final IOException cause) {
                    this.badRequest("Invalid content: " + cause.getMessage());
                    text = null;
                }
            }
        }

        return text;
    }

    /**
     * Locates the {@link HateosResourceMapping} or writes {@link HttpStatusCode#NOT_FOUND} or {@link HttpStatusCode#METHOD_NOT_ALLOWED}
     */
    private HateosResourceMapping<?, ?, ?, ?> handlersOrResponseNotFound(final HateosResourceName resourceName,
                                                                         final LinkRelation<?> linkRelation) {
        final HateosResourceMapping<?, ?, ?, ?> mapping = this.router.resourceNameToMapping.get(resourceName);
        if (null == mapping) {
            this.notFound(resourceName, linkRelation);
        }
        return mapping;
    }

    /**
     * Using the given request resource text (request body) read that into an {@link Optional optional} {@link HateosResource resource}.
     */
    <T> Optional<T> resourceOrBadRequest(final String requestText,
                                         final HateosContentType hateosContentType,
                                         final Class<T> type) {
        Optional<T> resource;

        if (requestText.isEmpty()) {
            resource = Optional.empty();
        } else {
            try {
                resource = Optional.of(hateosContentType.fromNode(requestText, type));
            } catch (final Exception cause) {
                this.badRequest("Invalid " + hateosContentType + ": " + cause.getMessage());
                resource = null;
            }
        }
        return resource;
    }

    // error reporting..................................................................................................

    final void badRequest(final String message) {
        this.setStatus(HttpStatusCode.BAD_REQUEST, message);
    }

    /**
     * <a href="https://restfulapi.net/http-status-codes/"></a>
     * <pre>
     * 405 (Method Not Allowed)
     * The API responds with a 405 error to indicate that the client tried to use an HTTP method that the resource does not allow. For instance, a read-only resource could support only GET and HEAD, while a controller resource might allow GET and POST, but not PUT or DELETE.
     *
     * A 405 response must include the Allow header, which lists the HTTP methods that the resource supports. For example:
     *
     * Allow: GET, POST
     * </pre>>
     */
    final void methodNotAllowed(final HateosResourceName resourceName,
                                final LinkRelation<?> relation,
                                final List<HttpMethod> allowed) {
        this.setStatus(HttpStatusCode.METHOD_NOT_ALLOWED,
                this.request.method() + " " + message(resourceName, relation));
        this.response.addEntity(HttpEntity.EMPTY.addHeader(HttpHeaderName.ALLOW, allowed));
    }

    private void notFound(final HateosResourceName resourceName,
                          final LinkRelation<?> linkRelation) {
        this.setStatus(HttpStatusCode.NOT_FOUND,
                this.message(resourceName, linkRelation));
    }

    private String message(final HateosResourceName resourceName,
                           final LinkRelation<?> linkRelation) {
        return "resource: " + resourceName + ", link relation: " + linkRelation;
    }

    /**
     * Sets the status to successful and body to the bytes of the encoded text of {@link Node}.
     */
    void setStatusAndBody(final String message, final String content) {
        if (null != content) {
            this.setStatusAndBody0(message, content);
        } else {
            this.setStatus(HttpStatusCode.NO_CONTENT, message);
        }
    }

    private void setStatusAndBody0(final String message, final String content) {
        this.setStatus(HttpStatusCode.OK, message);

        final AcceptCharset acceptCharset = HttpHeaderName.ACCEPT_CHARSET.headerValueOrFail(this.request.headers());
        final Optional<Charset> charset = acceptCharset.charset();
        if (!charset.isPresent()) {
            throw new NotAcceptableHeaderException("AcceptCharset " + acceptCharset + " doesnt contain supported charset");
        }

        final HateosContentType hateosContentType = this.hateosContentType();
        final MediaType contentType = hateosContentType.contentType();

        this.response.addEntity(HttpEntity.EMPTY
                .addHeader(HttpHeaderName.CONTENT_TYPE, contentType.setCharset(CharsetName.with(charset.get().name())))
                .setBodyText(content)
                .setContentLength());
    }

    private void setStatus(final HttpStatusCode statusCode, final String message) {
        this.setStatus(statusCode.setMessage(message));
    }

    private void setStatus(final HttpStatus status) {
        this.response.setStatus(status);
    }

    HateosContentType hateosContentType() {
        return this.router.contentType;
    }

    final HttpRequest request;
    final HttpResponse response;
    final HateosResourceMappingRouter router;

    /**
     * Only set when a valid request is dispatched.
     */
    Map<HttpRequestAttribute<?>, Object> parameters;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .value(this.router)
                .value(this.request)
                .value(this.response)
                .build();
    }
}
