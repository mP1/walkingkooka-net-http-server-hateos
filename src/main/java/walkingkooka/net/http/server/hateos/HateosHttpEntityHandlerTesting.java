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

import walkingkooka.collect.Range;
import walkingkooka.net.UrlPath;
import walkingkooka.net.header.HasHateosContentType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Mixin interface for testing {@link HateosHttpEntityHandler}
 */
public interface HateosHttpEntityHandlerTesting extends HasHateosContentType,
    TreePrintableTesting {

    // handleAll........................................................................................................


    default <I extends Comparable<I>, X extends HateosHandlerContext, T extends Throwable> T handleAllFails(final HateosHttpEntityHandler<I, X> handler,
                                                                                                            final HttpEntity entity,
                                                                                                            final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                                                            final UrlPath path,
                                                                                                            final X context,
                                                                                                            final Class<T> thrown) {
        return assertThrows(
            thrown,
            () -> handler.handleAll(
                entity,
                parameters,
                path,
                context
            )
        );
    }

    default <I extends Comparable<I>, X extends HateosHandlerContext> void handleAllAndCheck(final HateosHttpEntityHandler<I, X> handler,
                                                                                             final HttpEntity entity,
                                                                                             final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                                             final UrlPath path,
                                                                                             final X context,
                                                                                             final HttpEntity expected) {
        this.checkEquals(
            expected,
            handler.handleAll(
                entity,
                parameters,
                path,
                context
            )
        );
    }

// handleMany............................................................................................................

    default <I extends Comparable<I>, X extends HateosHandlerContext, T extends Throwable> T handleManyFails(final HateosHttpEntityHandler<I, X> handler,
                                                                                                             final Set<I> ids,
                                                                                                             final HttpEntity entity,
                                                                                                             final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                                                             final UrlPath path,
                                                                                                             final X context,
                                                                                                             final Class<T> thrown) {
        return assertThrows(
            thrown,
            () -> handler.handleMany(
                ids,
                entity,
                parameters,
                path,
                context
            )
        );
    }

    default <I extends Comparable<I>, X extends HateosHandlerContext> void handleManyAndCheck(final HateosHttpEntityHandler<I, X> handler,
                                                                                              final Set<I> ids,
                                                                                              final HttpEntity entity,
                                                                                              final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                                              final UrlPath path,
                                                                                              final X context,
                                                                                              final HttpEntity expected) {
        this.checkEquals(
            expected,
            handler.handleMany(
                ids,
                entity,
                parameters,
                path,
                context
            )
        );
    }

    // handleNone.......................................................................................................

    default <I extends Comparable<I>, X extends HateosHandlerContext, T extends Throwable> T handleNoneFails(final HateosHttpEntityHandler<I, X> handler,
                                                                                                             final HttpEntity entity,
                                                                                                             final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                                                             final UrlPath path,
                                                                                                             final X context,
                                                                                                             final Class<T> thrown) {
        return assertThrows(
            thrown,
            () -> handler.handleNone(
                entity,
                parameters,
                path,
                context
            )
        );
    }

    default <I extends Comparable<I>, X extends HateosHandlerContext> void handleNoneAndCheck(final HateosHttpEntityHandler<I, X> handler,
                                                                                              final HttpEntity entity,
                                                                                              final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                                              final UrlPath path,
                                                                                              final X context,
                                                                                              final HttpEntity expected) {
        this.checkEquals(
            expected,
            handler.handleNone(
                entity,
                parameters,
                path,
                context
            )
        );
    }

    // handleOne........................................................................................................

    default <I extends Comparable<I>, X extends HateosHandlerContext, T extends Throwable> T handleOneFails(final HateosHttpEntityHandler<I, X> handler,
                                                                                                            final I id,
                                                                                                            final HttpEntity entity,
                                                                                                            final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                                                            final UrlPath path,
                                                                                                            final X context,
                                                                                                            final Class<T> thrown) {
        return assertThrows(
            thrown,
            () -> handler.handleOne(
                id,
                entity,
                parameters,
                path,
                context
            )
        );
    }

    default <I extends Comparable<I>, X extends HateosHandlerContext> void handleOneAndCheck(final HateosHttpEntityHandler<I, X> handler,
                                                                                             final I id,
                                                                                             final HttpEntity entity,
                                                                                             final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                                             final UrlPath path,
                                                                                             final X context,
                                                                                             final HttpEntity expected) {
        this.checkEquals(
            expected,
            handler.handleOne(
                id,
                entity,
                parameters,
                path,
                context
            )
        );
    }

    // handleRange......................................................................................................

    default <I extends Comparable<I>, X extends HateosHandlerContext, T extends Throwable> T handleRangeFails(final HateosHttpEntityHandler<I, X> handler,
                                                                                                              final Range<I> ids,
                                                                                                              final HttpEntity entity,
                                                                                                              final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                                                              final UrlPath path,
                                                                                                              final X context,
                                                                                                              final Class<T> thrown) {
        return assertThrows(
            thrown,
            () -> handler.handleRange(
                ids,
                entity,
                parameters,
                path,
                context
            )
        );
    }

    default <I extends Comparable<I>, X extends HateosHandlerContext> void handleRangeAndCheck(final HateosHttpEntityHandler<I, X> handler,
                                                                                               final Range<I> ids,
                                                                                               final HttpEntity entity,
                                                                                               final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                                               final UrlPath path,
                                                                                               final X context,
                                                                                               final HttpEntity expected) {
        this.checkEquals(
            expected,
            handler.handleRange(
                ids,
                entity,
                parameters,
                path,
                context
            )
        );
    }
}
