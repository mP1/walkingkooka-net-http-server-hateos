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

import walkingkooka.Cast;
import walkingkooka.ToStringBuilder;
import walkingkooka.net.UrlPathName;
import walkingkooka.net.header.Accept;
import walkingkooka.net.header.AcceptCharset;
import walkingkooka.net.header.CharsetName;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.LinkRelation;
import walkingkooka.net.header.MediaType;
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

        this.parameters = this.request.routerParameters();
    }

    /**
     * Disassembles the path into components.
     * <ol>
     * <li>{@link HateosResourceName} required</li>
     * <li>{@link HateosResourceSelection} required or empty</li>
     * <li>{@link LinkRelation} option defaults to {@link LinkRelation#SELF}</li>
     * </ol>
     */
    void dispatch() {
        final int pathIndex = this.router.consumeBasePath(this.parameters);
        if (-1 == pathIndex) {
            this.badRequest("Bad routing");
        } else {
            this.extractResourceNameOrBadRequest(pathIndex);
        }
    }

    private void extractResourceNameOrBadRequest(final int pathIndex) {
        final String resourceNameString = this.pathComponent(pathIndex, null);

        if (CharSequences.isNullOrEmpty(resourceNameString)) {
            this.badRequest("Missing resource name");
        } else {
            HateosResourceName resourceName = null;
            try {
                resourceName = HateosResourceName.with(resourceNameString);
            } catch (final RuntimeException invalid) {
                this.badRequest("Invalid resource name " + CharSequences.quoteAndEscape(resourceNameString), invalid);
            }
            if (null != resourceName) {
                this.handleResourceNameOrNotFound(resourceName, pathIndex + 1);
            }
        }
    }

    private void handleResourceNameOrNotFound(final HateosResourceName resourceName,
                                              final int pathIndex) {
        final HateosResourceMapping<?, ?, ?, ?> mapping = this.router.resourceNameToMapping.get(resourceName);
        if (null == mapping) {
            this.notFound(resourceName);
        } else {
            this.parseSelectionOrBadRequest(mapping, pathIndex);
        }
    }

    private void notFound(final HateosResourceName resourceName) {
        this.setStatus(HttpStatusCode.NOT_FOUND, message(resourceName));
    }

    /**
     * Attempts to parse the selection which may be missing, id, range, list or all.
     */
    private void parseSelectionOrBadRequest(final HateosResourceMapping<?, ?, ?, ?> mapping,
                                            final int pathIndex) {
        final String selectionString = this.pathComponent(pathIndex, "");

        HateosResourceSelection<?> selection;
        try {
            selection = mapping.selection.apply(null == selectionString ? "" : selectionString);
        } catch (final RuntimeException invalid) {
            selection = null;
            this.badRequest(invalid.getMessage(), invalid);
        }

        if (null != selection) {
            this.extractLinkRelation(
                    mapping,
                    selection,
                    pathIndex + 1
            );
        }
    }

    /**
     * Extracts the link relation or defaults to {@link LinkRelation#SELF}.
     */
    private void extractLinkRelation(final HateosResourceMapping<?, ?, ?, ?> mapping,
                                     final HateosResourceSelection<?> selection,
                                     final int pathIndex) {
        final LinkRelation<?> relation = this.linkRelationOrDefaultOrResponseBadRequest(pathIndex);
        if (null != relation) {
            this.methodSupportedTest(mapping, selection, relation);
        }
    }

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
                    this.badRequest("Invalid link relation " + CharSequences.quoteAndEscape(relationString), invalid);
                }
            }
        }

        return relation;
    }

    /**
     * Validates that the request method is supported for the given {@link HateosResourceMapping}
     */
    private void methodSupportedTest(final HateosResourceMapping<?, ?, ?, ?> mapping,
                                     final HateosResourceSelection<?> selection,
                                     final LinkRelation<?> relation) {
        final HttpMethod method = this.request.method();

        final List<HttpMethod> supportedMethods = mapping.relationToMethods.get(relation);
        if (null != supportedMethods && supportedMethods.contains(method)) {
            this.locateHandlerParseRequestBodyAndDispatch(mapping, selection, relation, method);
        } else {
            this.methodNotAllowed(mapping.resourceName, relation, supportedMethods);
        }
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
    void methodNotAllowed(final HateosResourceName resourceName,
                          final LinkRelation<?> relation,
                          final List<HttpMethod> allowed) {
        this.setStatus(
                HttpStatusCode.METHOD_NOT_ALLOWED,
                this.request.method() + " " + message(resourceName, relation)
        );
        this.response.addEntity(
                HttpEntity.EMPTY.addHeader(HttpHeaderName.ALLOW, allowed)
        );
    }

    /**
     * Locatest the locateHandlerParseRequestBodyAndDispatch, attempts to parse the request body into a resource.
     */
    private void locateHandlerParseRequestBodyAndDispatch(final HateosResourceMapping<?, ?, ?, ?> mapping,
                                                          final HateosResourceSelection<?> selection,
                                                          final LinkRelation<?> relation,
                                                          final HttpMethod method) {
        final HateosHandler<?, ?, ?> handler = this.handlerOrNotFound(mapping, relation, method);
        if (null != handler) {
            final Optional<?> resource = this.parseBodyOrBadRequest(mapping, selection);
            if(null != resource) {
                final Accept accept = this.acceptCompatibleOrBadRequest();
                if(null != accept) {
                    final Optional<?> maybeResponseResource = selection.dispatch(Cast.to(handler), resource, this.parameters);
                    String responseText = null;

                    if (maybeResponseResource.isPresent()) {
                        final Object responseResource = maybeResponseResource.get();
                        responseText = this.hateosContentType()
                                .toText(responseResource);
                    }

                    this.setStatusAndBody(
                            responseText,
                            selection.resourceType(mapping)
                    );
                }
            }
        }
    }

    /**
     * Attempts to locate the locateHandlerParseRequestBodyAndDispatch for the given criteria or sets the response with not found.
     */
    private HateosHandler<?, ?, ?> handlerOrNotFound(final HateosResourceMapping<?, ?, ?, ?> mapping,
                                                     final LinkRelation<?> relation,
                                                     final HttpMethod method) {
        final HateosHandler<?, ?, ?> handler = mapping.relationAndMethodToHandlers.get(HateosResourceMappingLinkRelationHttpMethod.with(relation, method));
        if (null == handler) {
            this.notFound(mapping.resourceName, relation);
        }
        return handler;
    }

    private void notFound(final HateosResourceName resourceName,
                          final LinkRelation<?> linkRelation) {
        this.setStatus(
                HttpStatusCode.NOT_FOUND,
                message(resourceName, linkRelation)
        );
    }

    /**
     * Parses the request body and its JSON into a resource and then dispatches the locateHandlerParseRequestBodyAndDispatch.
     */
    private Optional<?> parseBodyOrBadRequest(final HateosResourceMapping<?, ?, ?, ?> mapping,
                                              final HateosResourceSelection<?> selection) {
        Optional<?> resource = null;

        final String bodyText = this.resourceTextOrBadRequest();
        if (null != bodyText) {
            resource = this.resourceOrBadRequest(bodyText, mapping, selection);
        }
        return resource;
    }

    /**
     * Reads and returns the body as text, with null signifying an error occured and a bad request response set.
     */
    private String resourceTextOrBadRequest() {
        final HttpRequest request = this.request;

        String bodyText;
        try {
            bodyText = request.bodyText();
        } catch (final RuntimeException cause) {
            this.badRequest("Invalid content: " + cause.getMessage(), cause);
            bodyText = null;
        }

        if (null != bodyText) {
            final Long contentLength = HttpHeaderName.CONTENT_LENGTH.header(request).orElse(null);
            if (bodyText.isEmpty()) {
                if (null != contentLength && contentLength.longValue() != request.bodyLength()) {
                    this.badRequest("Body absent with " + HttpHeaderName.CONTENT_LENGTH + ": " + contentLength);
                    bodyText = null;
                } else {
                    bodyText = "";
                }

            } else {
                if (null == contentLength) {
                    this.setStatus(HttpStatusCode.LENGTH_REQUIRED.status());
                    bodyText = null;
                } else {
                    final long bodyLength = request.bodyLength();
                    final long contentLengthLong = contentLength.longValue();
                    if (bodyLength != contentLengthLong) {
                        this.badRequest(HttpHeaderName.CONTENT_LENGTH + ": " + contentLengthLong + " != body length=" + bodyLength + " mismatch");
                        bodyText = null;
                    }
                }
            }
        }

        return bodyText;
    }

    /**
     * Using the given request resource text (request body) read that into an {@link Optional optional} {@link HateosResource resource}.
     */
    private Optional<?> resourceOrBadRequest(final String requestText,
                                             final HateosResourceMapping<?, ?, ?, ?> mapping,
                                             final HateosResourceSelection<?> selection) {
        Optional<?> resource;

        if (requestText.isEmpty()) {
            resource = Optional.empty();
        } else {
            final HateosContentType hateosContentType = this.router.contentType;
            final Class<?> type = selection.resourceType(mapping);
            try {
                resource = Optional.of(
                        hateosContentType.fromText(requestText, type)
                );
            } catch (final Exception cause) {
                this.badRequest("Invalid " + hateosContentType + ": " + cause.getMessage(), cause);
                resource = null;
            }
        }
        return resource;
    }

    private Accept acceptCompatibleOrBadRequest() {
        final HttpHeaderName<Accept> header = HttpHeaderName.ACCEPT;

        Accept accept = header.header(this.request)
                .orElse(null);
        if (null == accept) {
            this.badRequest("Missing " + HttpHeaderName.ACCEPT);
        } else {
            final MediaType contentType = this.hateosContentType().contentType();
            if (!accept.test(contentType)) {
                this.badRequest("Header " + header + " expected " + contentType + " got " + accept);
                accept = null;
            }
        }

        return accept;
    }

    /**
     * Fetches the path component at the path index or returns null.
     */
    private String pathComponent(final int pathIndex, final String missing) {
        return HttpRequestAttributes.pathComponent(pathIndex).parameterValue(this.parameters)
                .map(v -> v.value())
                .orElse(missing);
    }

    // error reporting..................................................................................................

    void badRequest(final String message) {
        this.setStatus(HttpStatusCode.BAD_REQUEST, message);
    }

    private static String message(final HateosResourceName resourceName) {
        return "resource: " + resourceName;
    }

    private static String message(final HateosResourceName resourceName,
                           final LinkRelation<?> linkRelation) {
        return message(resourceName) + ", link relation: " + linkRelation;
    }

    /**
     * Reports a bad request with the body filled with the stack trace of the provided {@link Throwable}.
     */
    void badRequest(final String message,
                    final Throwable cause) {
        this.badRequest(message);
        this.response.addEntity(HttpEntity.dumpStackTrace(cause));
    }

    /**
     * Sets the status and message to match the content.
     */
    void setStatusAndBody(final String content,
                          final Class<?> contentValueType) {

        final HttpStatusCode statusCode;

        final HttpEntity entity;
        if (null != content) {
            statusCode = HttpStatusCode.OK;

            final CharsetName charsetName = this.selectCharsetName();
            final HateosContentType hateosContentType = this.hateosContentType();
            final MediaType contentType = hateosContentType.contentType();

            entity = HttpEntity.EMPTY
                    .addHeader(HttpHeaderName.CONTENT_TYPE, contentType.setCharset(charsetName))
                    .addHeader(HateosResourceMapping.X_CONTENT_TYPE_NAME, contentValueType.getSimpleName()) // this header is used a hint about the response.
                    .setBodyText(content)
                    .setContentLength();
        } else {
            statusCode = HttpStatusCode.NO_CONTENT;
            entity = HttpEntity.EMPTY;
        }

        this.setStatus(statusCode.status());

        if(!entity.isEmpty()) {
            this.response.addEntity(entity);
        }
    }

    private CharsetName selectCharsetName() {
        final AcceptCharset acceptCharset = HttpHeaderName.ACCEPT_CHARSET.header(this.request)
                .orElse(UTF8);
        final Optional<Charset> charset = acceptCharset.charset();
        if (!charset.isPresent()) {
            throw new NotAcceptableHeaderException("AcceptCharset " + acceptCharset + " contain unsupported charset");
        }
        return CharsetName.with(charset.get().name());
    }

    private final static AcceptCharset UTF8 = AcceptCharset.parse("utf-8");

    private void setStatus(final HttpStatusCode statusCode,
                           final String message) {
        this.setStatus(
                statusCode.setMessageOrDefault(
                        HttpStatus.firstLineOfText(message)
                )
        ); // message could be null if Exception#getMessage
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
    final Map<HttpRequestAttribute<?>, Object> parameters;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .value(this.router)
                .value(this.request)
                .value(this.response)
                .build();
    }
}
