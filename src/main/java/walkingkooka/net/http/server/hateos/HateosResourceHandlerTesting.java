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
import walkingkooka.collect.Range;
import walkingkooka.net.UrlPath;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Mixin interface for testing {@link HateosResourceHandler}
 */
public interface HateosResourceHandlerTesting<H extends HateosResourceHandler<I, V, C, X>,
        I extends Comparable<I>,
        V,
        C,
        X extends HateosResourceHandlerContext>
        extends ClassTesting2<H>,
        TreePrintableTesting,
        TypeNameTesting<H> {

    // handleAll........................................................................................................

    @Test
    default void testHandleAllNullResourceFails() {
        this.handleAllFails(
                null,
                this.parameters(),
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleAllNullParametersFails() {
        this.handleAllFails(
                this.collectionResource(),
                null,
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleAllNullPathFails() {
        this.handleAllFails(
                this.collectionResource(),
                this.parameters(),
                null,
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleAllNullContextFails() {
        this.handleAllFails(
                this.collectionResource(),
                this.parameters(),
                this.path(),
                null,
                NullPointerException.class
        );
    }

    default <T extends Throwable> T handleAllFails(final Optional<C> resource,
                                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                                   final UrlPath path,
                                                   final X context,
                                                   final Class<T> thrown) {
        return this.handleAllFails(
                this.createHandler(),
                resource,
                parameters,
                path,
                context,
                thrown
        );
    }

    default <T extends Throwable> T handleAllFails(final HateosResourceHandler<I, V, C, X> handler,
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

    default void handleAllAndCheck(final Optional<C> resource,
                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                   final UrlPath path,
                                   final X context,
                                   final Optional<C> expected) {
        this.handleAllAndCheck(
                this.createHandler(),
                resource,
                parameters,
                path,
                context,
                expected
        );
    }

    default void handleAllAndCheck(final HateosResourceHandler<I, V, C, X> handler,
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

    @Test
    default void testHandleManyNullIdsFails() {
        this.handleManyFails(
                null,
                this.collectionResource(),
                this.parameters(),
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleManyNullResourceFails() {
        this.handleManyFails(
                this.manyIds(),
                null,
                this.parameters(),
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleManyNullParametersFails() {
        this.handleManyFails(
                this.manyIds(),
                this.collectionResource(),
                null,
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleManyNullPathFails() {
        this.handleManyFails(
                this.manyIds(),
                this.collectionResource(),
                this.parameters(),
                null,
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleManyNullContextFails() {
        this.handleManyFails(
                this.manyIds(),
                this.collectionResource(),
                this.parameters(),
                this.path(),
                null,
                NullPointerException.class
        );
    }

    default <T extends Throwable> T handleManyFails(final Set<I> ids,
                                                    final Optional<C> resource,
                                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                                    final UrlPath path,
                                                    final X context,
                                                    final Class<T> thrown) {
        return this.handleManyFails(
                this.createHandler(),
                ids,
                resource,
                parameters,
                path,
                context,
                thrown
        );
    }

    default <T extends Throwable> T handleManyFails(final HateosResourceHandler<I, V, C, X> handler,
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

    default void handleManyAndCheck(final Set<I> ids,
                                    final Optional<C> resource,
                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                    final UrlPath path,
                                    final X context,
                                    final Optional<C> expected) {
        this.handleManyAndCheck(
                this.createHandler(),
                ids,
                resource,
                parameters,
                path,
                context,
                expected
        );
    }

    default void handleManyAndCheck(final HateosResourceHandler<I, V, C, X> handler,
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

    @Test
    default void testHandleNoneNullResourceFails() {
        this.handleNoneFails(
                null,
                this.parameters(),
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleNoneNullParametersFails() {
        this.handleNoneFails(
                this.resource(),
                null,
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleNoneNullPathFails() {
        this.handleNoneFails(
                this.resource(),
                this.parameters(),
                null,
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleNoneNullContextFails() {
        this.handleNoneFails(
                this.resource(),
                this.parameters(),
                this.path(),
                null,
                NullPointerException.class
        );
    }

    default <T extends Throwable> T handleNoneFails(final Optional<V> resource,
                                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                                    final UrlPath path,
                                                    final X context,
                                                    final Class<T> thrown) {
        return this.handleNoneFails(
                this.createHandler(),
                resource,
                parameters,
                path,
                context,
                thrown
        );
    }

    default <T extends Throwable> T handleNoneFails(final HateosResourceHandler<I, V, C, X> handler,
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

    default void handleNoneAndCheck(final Optional<V> resource,
                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                    final UrlPath path,
                                    final X context,
                                    final Optional<V> expected) {
        this.handleNoneAndCheck(
                this.createHandler(),
                resource,
                parameters,
                path,
                context,
                expected
        );
    }

    default void handleNoneAndCheck(final HateosResourceHandler<I, V, C, X> handler,
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

    @Test
    default void testHandleOneNullIdFails() {
        this.handleOneFails(
                null,
                this.resource(),
                this.parameters(),
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleOneNullResourceFails() {
        this.handleOneFails(
                this.id(),
                null,
                this.parameters(),
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleOneNullParametersFails() {
        this.handleOneFails(
                this.id(),
                this.resource(),
                null,
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleOneNullPathFails() {
        this.handleOneFails(
                this.id(),
                this.resource(),
                this.parameters(),
                null,
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleOneNullContextFails() {
        this.handleOneFails(
                this.id(),
                this.resource(),
                this.parameters(),
                this.path(),
                null,
                NullPointerException.class
        );
    }

    default <T extends Throwable> T handleOneFails(final I id,
                                                   final Optional<V> resource,
                                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                                   final UrlPath path,
                                                   final X context,
                                                   final Class<T> thrown) {
        return this.handleOneFails(
                this.createHandler(),
                id,
                resource,
                parameters,
                path,
                context,
                thrown
        );
    }

    default <T extends Throwable> T handleOneFails(final HateosResourceHandler<I, V, C, X> handler,
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

    default void handleOneAndCheck(final I id,
                                   final Optional<V> resource,
                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                   final UrlPath path,
                                   final X context,
                                   final Optional<V> expected) {
        this.handleOneAndCheck(
                this.createHandler(),
                id,
                resource,
                parameters,
                path,
                context,
                expected
        );
    }

    default void handleOneAndCheck(final HateosResourceHandler<I, V, C, X> handler,
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

    @Test
    default void testHandleRangeNullRangeFails() {
        this.handleRangeFails(
                null,
                this.collectionResource(),
                this.parameters(),
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleRangeNullResourceFails() {
        this.handleRangeFails(
                this.range(),
                null,
                this.parameters(),
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleRangeNullParametersFails() {
        this.handleRangeFails(
                this.range(),
                this.collectionResource(),
                null,
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleRangeNullPathFails() {
        this.handleRangeFails(
                this.range(),
                this.collectionResource(),
                this.parameters(),
                null,
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleRangeNullContextFails() {
        this.handleRangeFails(
                this.range(),
                this.collectionResource(),
                this.parameters(),
                this.path(),
                null,
                NullPointerException.class
        );
    }

    default <T extends Throwable> T handleRangeFails(final Range<I> range,
                                                     final Optional<C> resource,
                                                     final Map<HttpRequestAttribute<?>, Object> parameters,
                                                     final UrlPath path,
                                                     final X context,
                                                     final Class<T> thrown) {
        return this.handleRangeFails(
                this.createHandler(),
                range,
                resource,
                parameters,
                path,
                context,
                thrown
        );
    }

    default <T extends Throwable> T handleRangeFails(final HateosResourceHandler<I, V, C, X> handler,
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

    default void handleRangeAndCheck(final Range<I> range,
                                     final Optional<C> resource,
                                     final Map<HttpRequestAttribute<?>, Object> parameters,
                                     final UrlPath path,
                                     final X context,
                                     final Optional<C> expected) {
        this.handleRangeAndCheck(
                this.createHandler(),
                range,
                resource,
                parameters,
                path,
                context,
                expected
        );
    }

    default void handleRangeAndCheck(final HateosResourceHandler<I, V, C, X> handler,
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

    // helpers..........................................................................................................

    H createHandler();

    I id();

    Set<I> manyIds();

    Range<I> range();

    Optional<V> resource();

    Optional<C> collectionResource();

    Map<HttpRequestAttribute<?>, Object> parameters();

    UrlPath path();

    X context();

    @Override
    default String typeNameSuffix() {
        return HateosResourceHandler.class.getSimpleName();
    }
}
