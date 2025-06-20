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
import walkingkooka.Cast;
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.net.UrlPath;
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
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.Printers;
import walkingkooka.tree.json.JsonNode;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Handles dispatching a request, after extracting ids and parsing request bodies.
 */
final class HateosResourceMappingsRouterHttpHandlerRequest {

    static HateosResourceMappingsRouterHttpHandlerRequest with(final HttpRequest request,
                                                               final HttpResponse response,
                                                               final HateosResourceMappingsRouter router,
                                                               final Indentation indentation,
                                                               final LineEnding lineEnding,
                                                               final HateosResourceHandlerContext context) {
        return new HateosResourceMappingsRouterHttpHandlerRequest(
                request,
                response,
                router,
                indentation,
                lineEnding,
                context
        );
    }

    private HateosResourceMappingsRouterHttpHandlerRequest(final HttpRequest request,
                                                           final HttpResponse response,
                                                           final HateosResourceMappingsRouter router,
                                                           final Indentation indentation,
                                                           final LineEnding lineEnding,
                                                           final HateosResourceHandlerContext context) {
        super();
        this.request = request;
        this.response = response;
        this.router = router;
        this.indentation = indentation;
        this.lineEnding = lineEnding;
        this.context = context;

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
                // Invalid resource name "InvalidResourceHere"
                this.badRequest("Invalid resource name " + CharSequences.quoteAndEscape(resourceNameString), invalid);
            }
            if (null != resourceName) {
                this.handleResourceNameOrNotFound(resourceName, pathIndex + 1);
            }
        }
    }

    private void handleResourceNameOrNotFound(final HateosResourceName resourceName,
                                              final int pathIndex) {
        final HateosResourceMappings<?, ?, ?, ?, ?> mapping = this.router.resourceNameToMapping.get(resourceName);
        if (null == mapping) {
            this.notFound(resourceName);
        } else {
            this.parseSelectionOrBadRequest(
                    mapping,
                    pathIndex
            );
        }
    }

    private void notFound(final HateosResourceName resourceName) {
        this.setStatus(HttpStatusCode.NOT_FOUND, message(resourceName));
    }

    /**
     * Attempts to parse the selection which may be missing, id, range, list or all.
     */
    private void parseSelectionOrBadRequest(final HateosResourceMappings<?, ?, ?, ?, ?> mappings,
                                            final int pathIndex) {
        final String selectionString = this.pathComponent(pathIndex, "");

        HateosResourceSelection<?> selection;
        try {
            selection = mappings.selection.apply(
                    null == selectionString ?
                            "" :
                            selectionString,
                    Cast.to(this.context)
            );
        } catch (final RuntimeException invalid) {
            selection = null;
            this.badRequest(invalid.getMessage(), invalid);
        }

        if (null != selection) {
            this.linkRelationOrDefaultOrResponseBadRequestOrMethodNotSupported(
                    mappings,
                    selection,
                    pathIndex + 1
            );
        }
    }

    /**
     * Extracts the link relation or defaults to {@link LinkRelation#SELF}.
     */
    private void linkRelationOrDefaultOrResponseBadRequestOrMethodNotSupported(final HateosResourceMappings<?, ?, ?, ?, ?> mappings,
                                                                               final HateosResourceSelection<?> selection,
                                                                               final int pathIndex) {
        LinkRelation<?> relation = LinkRelation.SELF;
        boolean defaultLinkRelation = true;

        final Optional<UrlPathName> relationPath = HttpRequestAttributes.pathComponent(pathIndex)
                .parameterValue(this.parameters);

        if (relationPath.isPresent()) {
            final String relationString = relationPath.get().value();
            if (!CharSequences.isNullOrEmpty(relationString)) {
                try {
                    relation = LinkRelation.with(relationString);
                    defaultLinkRelation = false;
                } catch (final RuntimeException invalid) {
                    relation = null;

                    // Invalid link relation \"UnknownLinkRelation"
                    this.badRequest(
                            "Invalid link relation " +
                                    CharSequences.quoteAndEscape(relationString),
                            invalid
                    );
                }
            }
        }

        if (null != relation) {
            this.methodSupportedChallengeAndDispatch(
                    mappings,
                    selection,
                    relation,
                    defaultLinkRelation ?
                            pathIndex - 1 :
                            pathIndex
            );
        }
    }

    /**
     * Validates that the request method and {@link LinkRelation} is supported for the given {@link HateosResourceMappings}
     */
    private void methodSupportedChallengeAndDispatch(final HateosResourceMappings<?, ?, ?, ?, ?> mappings,
                                                     final HateosResourceSelection<?> selection,
                                                     final LinkRelation<?> relation,
                                                     final int pathIndex) {
        final HttpMethod method = this.request.method();

        final List<HttpMethod> supportedMethods = mappings.relationToMethods.get(relation);
        if (null == supportedMethods) {
            this.badRequest(
                    "Unknown link relation " +
                            CharSequences.quoteAndEscape(
                                    relation.toHeaderText()
                            )
            );
        } else {
            if (supportedMethods.contains(method)) {
                this.locateHandlerAndHandle(
                        mappings,
                        selection,
                        relation,
                        method,
                        pathIndex
                );
            } else {
                this.methodNotAllowed(
                        mappings.resourceName,
                        relation,
                        supportedMethods
                );
            }
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
    private void methodNotAllowed(final HateosResourceName resourceName,
                                  final LinkRelation<?> relation,
                                  final List<HttpMethod> allowed) {
        this.setStatus(
                HttpStatusCode.METHOD_NOT_ALLOWED,
                this.request.method() +
                        " " +
                        message(
                                resourceName,
                                relation
                        )
        );
        this.response.setEntity(
                HttpEntity.EMPTY.addHeader(HttpHeaderName.ALLOW, allowed)
        );
    }

    /**
     * Using the mapping and relation attempts to locate a matching {@link HateosResourceHandler}, followed by parsing the
     * request body into a {@link HateosResource} and then writes the response and sets the status code.
     */
    private void locateHandlerAndHandle(final HateosResourceMappings<?, ?, ?, ?, ?> mappings,
                                        final HateosResourceSelection<?> selection,
                                        final LinkRelation<?> relation,
                                        final HttpMethod method,
                                        final int pathIndex) {
        final HateosResourceMappingsHandler handler = this.handlerOrNotFound(
                mappings,
                relation,
                method
        );
        if (null != handler) {
            UrlPath extraPath = null;

            int i = 0;
            for (final UrlPathName path : this.request.url().path().normalize()) {
                if (i > pathIndex) {
                    extraPath = (
                            null == extraPath ?
                                    UrlPath.ROOT :
                                    extraPath
                    ).append(path);
                }
                i++;
            }

            handler.handle(
                    this,
                    mappings,
                    selection,
                    null != extraPath ?
                            extraPath.normalize() :
                            UrlPath.EMPTY,
                    this.context
            );
        }
    }

    private final HateosResourceHandlerContext context;

    /**
     * Attempts to locate the {@link HateosResourceMappingsHandler} for the given criteria or sets the response with not found.
     */
    private HateosResourceMappingsHandler handlerOrNotFound(final HateosResourceMappings<?, ?, ?, ?, ?> mappings,
                                                            final LinkRelation<?> relation,
                                                            final HttpMethod method) {
        final HateosResourceMappingsHandler handler = mappings.relationAndMethodToHandlers.get(
                HateosResourceMappingsLinkRelationHttpMethod.with(
                        relation,
                        method
                )
        );
        if (null == handler) {
            this.notFound(
                    mappings.resourceName,
                    relation
            );
        }
        return handler;
    }

    private void notFound(final HateosResourceName resourceName,
                          final LinkRelation<?> linkRelation) {
        this.setStatus(
                HttpStatusCode.NOT_FOUND,
                message(
                        resourceName,
                        linkRelation
                )
        );
    }

    // HateosHttpEntityHandler..........................................................................................

    void handleHateosHttpEntityHandler(final HateosHttpEntityHandler<?, ?> handler,
                                       final HateosResourceSelection<?> selection,
                                       final UrlPath path,
                                       final HateosResourceHandlerContext context) {
        final HttpEntity responseHttpEntity = selection.handleHateosHttpEntityHandler(
                Cast.to(handler),
                this.httpEntity(),
                this.parameters,
                path,
                context
        );

        final HttpResponse response = this.response;

        response.setStatus(
                responseHttpEntity.isEmpty() ?
                        HttpStatusCode.NO_CONTENT.status() :
                        HttpStatusCode.OK.status()
        );
        response.setEntity(responseHttpEntity);
    }

    private HttpEntity httpEntity() {
        final HttpRequest request = this.request;

        return HttpEntity.EMPTY.setHeaders(
                request.headers()
        ).setBody(
                Binary.with(request.body())
        );
    }

    // HateosResourceHandler............................................................................................

    void handleHateosResourceHandler(final HateosResourceHandler<?, ?, ?, ?> handler,
                                     final HateosResourceMappings<?, ?, ?, ?, ?> mappings,
                                     final HateosResourceSelection<?> selection,
                                     final UrlPath path,
                                     final HateosResourceHandlerContext context) {
        final Optional<?> resource = this.parseBodyOrBadRequest(mappings, selection);
        if (null != resource) {
            final Accept accept = this.acceptCompatibleOrBadRequest();
            if (null != accept) {
                final Optional<?> maybeResponseResource = selection.handleHateosResourceHandler(
                        Cast.to(handler),
                        resource,
                        this.parameters,
                        path,
                        context
                );
                String responseText = null;

                if (maybeResponseResource.isPresent()) {
                    final Object responseResource = maybeResponseResource.get();
                    responseText = this.toText(responseResource);
                }

                this.setStatusAndBody(
                        selection,
                        responseText,
                        selection.resourceType(mappings)
                );
            }
        }
    }

    /**
     * Parses the request body and its JSON into a resource and then dispatches the locateHandlerAndHandle.
     */
    private Optional<?> parseBodyOrBadRequest(final HateosResourceMappings<?, ?, ?, ?, ?> mappings,
                                              final HateosResourceSelection<?> selection) {
        Optional<?> resource = null;

        final String bodyText = this.resourceTextOrBadRequest();
        if (null != bodyText) {
            resource = this.resourceOrBadRequest(
                    bodyText,
                    mappings,
                    selection
            );
        }

        return resource;
    }

    /**
     * Reads and returns the body as text, with null signifying an error occurred and a bad request response.
     */
    private String resourceTextOrBadRequest() {
        final HttpRequest request = this.request;

        String bodyText;
        try {
            bodyText = request.bodyText();
        } catch (final RuntimeException cause) {
            this.badRequest(
                    "Invalid content: " +
                            cause.getMessage(),
                    cause
            );
            bodyText = null;
        }

        if (null != bodyText) {
            final Long contentLength = HttpHeaderName.CONTENT_LENGTH.header(request).orElse(null);
            if (bodyText.isEmpty()) {
                if (null != contentLength && contentLength.longValue() != request.bodyLength()) {
                    // Body absent with ContentLength: 123
                    this.badRequest(
                            "Body absent with " +
                                    HttpHeaderName.CONTENT_LENGTH +
                                    ": " +
                                    contentLength
                    );
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
                        // ContentLength: 123 != body length 456 mismatch
                        this.badRequest(
                                HttpHeaderName.CONTENT_LENGTH +
                                        ": " +
                                        contentLengthLong +
                                        " != body length=" +
                                        bodyLength +
                                        " mismatch"
                        );
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
                                             final HateosResourceMappings<?, ?, ?, ?, ?> mappings,
                                             final HateosResourceSelection<?> selection) {
        Optional<?> resource;

        if (requestText.isEmpty()) {
            resource = Optional.empty();
        } else {
            final Class<?> type = selection.resourceType(mappings);
            final HateosResourceHandlerContext context = this.context;
            try {
                resource = Optional.of(
                        context.unmarshall(
                                JsonNode.parse(requestText),
                                type
                        )
                );
            } catch (final Exception cause) {
                // Invalid bad/type: Message here...
                this.badRequest(
                        "Invalid " +
                                context.contentType() +
                                ": " +
                                cause.getMessage(),
                        cause
                );
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
            final MediaType contentType = this.context.contentType();
            if (false == accept.test(contentType)) {
                this.badRequest(
                        accept.requireIncompatibleMessage(contentType)
                );
                accept = null;
            }
        }

        return accept;
    }

    /**
     * Fetches the path component at the path index or returns null.
     */
    private String pathComponent(final int pathIndex,
                                 final String missing) {
        return HttpRequestAttributes.pathComponent(pathIndex)
                .parameterValue(this.parameters)
                .map(UrlPathName::value)
                .orElse(missing);
    }

    /**
     * Marshals the given response to a String which will become the response body text.
     */
    private String toText(final Object body) {
        final StringBuilder b = new StringBuilder();

        try (final IndentingPrinter printer = Printers.stringBuilder(
                b,
                this.lineEnding
        ).indenting(this.indentation)) {
            this.context.marshall(body)
                    .printJson(printer);
            printer.flush();
        }
        return b.toString();
    }

    // these two properties will be removed when a Converter is convert request text -> values and values -> response text.

    private final Indentation indentation;
    private final LineEnding lineEnding;

    // error reporting..................................................................................................

    void badRequest(final String message) {
        this.setStatus(
                HttpStatusCode.BAD_REQUEST,
                message
        );
    }

    private static String message(final HateosResourceName resourceName) {
        return "resource: " + resourceName;
    }

    private static String message(final HateosResourceName resourceName,
                                  final LinkRelation<?> linkRelation) {
        // ResourceName, link relation: SAVE
        return message(
                resourceName) +
                ", link relation: " +
                linkRelation;
    }

    /**
     * Reports a bad request with the body filled with the stack trace of the provided {@link Throwable}.
     */
    void badRequest(final String message,
                    final Throwable cause) {
        this.badRequest(message);
        this.response.setEntity(
                HttpEntity.dumpStackTrace(cause)
        );
    }

    /**
     * Sets the status and message to match the content.
     */
    void setStatusAndBody(final HateosResourceSelection<?> selection,
                          final String content,
                          final Class<?> contentValueType) {

        final HttpStatusCode statusCode;

        final HttpEntity entity;
        if (null != content) {
            // CREATED if HateosResourceSuccess.none and OK for others
            statusCode = selection.successStatusCode();

            final CharsetName charsetName = this.selectCharsetName();
            final MediaType contentType = this.context.contentType();

            entity = HttpEntity.EMPTY
                    .setContentType(contentType.setCharset(charsetName))
                    .setBodyText(content)
                    .setContentLength();
        } else {
            statusCode = HttpStatusCode.NO_CONTENT;
            entity = HttpEntity.EMPTY;
        }

        this.setStatus(statusCode.status());

        // This header is used to dispatch FetcherWatcher#onXXX.
        // Even NO_CONTENT responses require this header so the web app will be aware of successful DELETEs(which reply with NO_CONTENT).
        this.response.setEntity(
                entity.addHeader(
                        HateosResourceMappings.X_CONTENT_TYPE_NAME,
                        contentValueType.getSimpleName()
                )
        );
    }

    private CharsetName selectCharsetName() {
        final AcceptCharset acceptCharset = HttpHeaderName.ACCEPT_CHARSET.header(this.request)
                .orElse(AcceptCharset.UTF_8);
        final Optional<Charset> charset = acceptCharset.charset();
        if (!charset.isPresent()) {
            // AcceptCharset Hello contains unsupported charset
            throw new NotAcceptableHeaderException("AcceptCharset " + acceptCharset + " contain unsupported charset");
        }
        return CharsetName.with(charset.get().name());
    }

    private void setStatus(final HttpStatusCode statusCode,
                           final String message) {
        this.setStatus(
                statusCode.setMessageOrDefault(
                        CharSequences.isNullOrEmpty(message) ?
                                null :
                                HttpStatus.firstLineOfText(message)
                )
        ); // message could be null if Exception#getMessage
    }

    private void setStatus(final HttpStatus status) {
        this.response.setStatus(status);
    }

    final HttpRequest request;
    final HttpResponse response;
    final HateosResourceMappingsRouter router;

    /**
     * Only setHateosResourceHandler when a valid request is dispatched.
     */
    final Map<HttpRequestAttribute<?>, Object> parameters;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .enable(ToStringBuilderOption.SKIP_IF_DEFAULT_VALUE)
                .value(this.router)
                .value(this.request)
                .value(this.response)
                .enable(ToStringBuilderOption.ESCAPE)
                .label("indentation").value(this.indentation)
                .label("lineEndings").value(this.lineEnding)
                .build();
    }
}
