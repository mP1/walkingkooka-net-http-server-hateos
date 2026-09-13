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
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Mixin interface for testing {@link HateosResourceHandler}
 */
public interface HateosResourceHandlerTesting extends TreePrintableTesting {

    default <I extends Comparable<I>, V, C, X extends HateosHandlerContext, T extends Throwable> T handleAllFails(final HateosResourceHandler<I, V, C, X> handler,
                                                                                                                  final Optional<C> resource,
                                                                                                                  final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                                                                  final UrlPath path,
                                                                                                                  final X context,
                                                                                                                  final Class<T> thrown) {
        return assertThrows(
            thrown,
            () -> handler.handleAll(
                resource,
                parameters,
                path,
                context
            )
        );
    }

    default <I extends Comparable<I>, V, C, X extends HateosHandlerContext> void handleAllAndCheck(final HateosResourceHandler<I, V, C, X> handler,
                                                                                                   final Optional<C> resource,
                                                                                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                                                   final UrlPath path,
                                                                                                   final X context,
                                                                                                   final Optional<C> expected) {
        this.checkEquals(
            expected,
            handler.handleAll(
                resource,
                parameters,
                path,
                context
            )
        );
    }

    // handleMany.......................................................................................................

    default <I extends Comparable<I>, V, C, X extends HateosHandlerContext, T extends Throwable> T handleManyFails(final HateosResourceHandler<I, V, C, X> handler,
                                                                                                                   final Set<I> ids,
                                                                                                                   final Optional<C> resource,
                                                                                                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                                                                   final UrlPath path,
                                                                                                                   final X context,
                                                                                                                   final Class<T> thrown) {
        return assertThrows(
            thrown,
            () -> handler.handleMany(
                ids,
                resource,
                parameters,
                path,
                context
            )
        );
    }

    default <I extends Comparable<I>, V, C, X extends HateosHandlerContext> void handleManyAndCheck(final HateosResourceHandler<I, V, C, X> handler,
                                                                                                    final Set<I> ids,
                                                                                                    final Optional<C> resource,
                                                                                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                                                    final UrlPath path,
                                                                                                    final X context,
                                                                                                    final Optional<C> expected) {
        this.checkEquals(
            expected,
            handler.handleMany(
                ids,
                resource,
                parameters,
                path,
                context
            )
        );
    }

    // handleNone.......................................................................................................

    default <I extends Comparable<I>, V, C, X extends HateosHandlerContext, T extends Throwable> T handleNoneFails(final HateosResourceHandler<I, V, C, X> handler,
                                                                                                                   final Optional<V> resource,
                                                                                                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                                                                   final UrlPath path,
                                                                                                                   final X context,
                                                                                                                   final Class<T> thrown) {
        return assertThrows(
            thrown,
            () -> handler.handleNone(
                resource,
                parameters,
                path,
                context
            )
        );
    }

    default <I extends Comparable<I>, V, C, X extends HateosHandlerContext> void handleNoneAndCheck(final HateosResourceHandler<I, V, C, X> handler,
                                                                                                    final Optional<V> resource,
                                                                                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                                                    final UrlPath path,
                                                                                                    final X context,
                                                                                                    final Optional<V> expected) {
        this.checkEquals(
            expected,
            handler.handleNone(
                resource,
                parameters,
                path,
                context
            )
        );
    }

    // handleOne.......................................................................................................

    default <I extends Comparable<I>, V, C, X extends HateosHandlerContext, T extends Throwable> T handleOneFails(final HateosResourceHandler<I, V, C, X> handler,
                                                                                                                  final I id,
                                                                                                                  final Optional<V> resource,
                                                                                                                  final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                                                                  final UrlPath path,
                                                                                                                  final X context,
                                                                                                                  final Class<T> thrown) {
        return assertThrows(
            thrown,
            () -> handler.handleOne(
                id,
                resource,
                parameters,
                path,
                context
            )
        );
    }

    default <I extends Comparable<I>, V, C, X extends HateosHandlerContext> void handleOneAndCheck(final HateosResourceHandler<I, V, C, X> handler,
                                                                                                   final I id,
                                                                                                   final Optional<V> resource,
                                                                                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                                                   final UrlPath path,
                                                                                                   final X context,
                                                                                                   final Optional<V> expected) {
        this.checkEquals(
            expected,
            handler.handleOne(
                id,
                resource,
                parameters,
                path,
                context
            )
        );
    }

    // handleRange.......................................................................................................

    default <I extends Comparable<I>, V, C, X extends HateosHandlerContext, T extends Throwable> T handleRangeFails(final HateosResourceHandler<I, V, C, X> handler,
                                                                                                                    final Range<I> range,
                                                                                                                    final Optional<C> resource,
                                                                                                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                                                                    final UrlPath path,
                                                                                                                    final X context,
                                                                                                                    final Class<T> thrown) {
        return assertThrows(
            thrown,
            () -> handler.handleRange(
                range,
                resource,
                parameters,
                path,
                context
            )
        );
    }

    default <I extends Comparable<I>, V, C, X extends HateosHandlerContext> void handleRangeAndCheck(final HateosResourceHandler<I, V, C, X> handler,
                                                                                                     final Range<I> range,
                                                                                                     final Optional<C> resource,
                                                                                                     final Map<HttpRequestAttribute<?>, Object> parameters,
                                                                                                     final UrlPath path,
                                                                                                     final X context,
                                                                                                     final Optional<C> expected) {
        this.checkEquals(
            expected,
            handler.handleRange(
                range,
                resource,
                parameters,
                path,
                context
            )
        );
    }
}
