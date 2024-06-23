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
public interface HateosResourceHandlerTesting<H extends HateosResourceHandler<I, V, C>,
        I extends Comparable<I>,
        V,
        C>
        extends ClassTesting2<H>,
        TreePrintableTesting,
        TypeNameTesting<H> {
    
    // handleAll.......................................................................................................

    @Test
    default void testHandleAllNullResourceFails() {
        this.handleAllFails(null,
                this.parameters(),
                NullPointerException.class);
    }

    @Test
    default void testHandleAllNullParametersFails() {
        this.handleAllFails(this.collectionResource(),
                null,
                NullPointerException.class);
    }

    default <T extends Throwable> T handleAllFails(final Optional<C> resource,
                                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                                   final Class<T> thrown) {
        return this.handleAllFails(this.createHandler(),
                resource,
                parameters,
                thrown);
    }

    default <T extends Throwable> T handleAllFails(final HateosResourceHandler<I, V, C> handler,
                                                   final Optional<C> resource,
                                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                                   final Class<T> thrown) {
        return assertThrows(thrown, () -> {
            handler.handleAll(resource, parameters);
        });
    }

    default void handleAllAndCheck(final Optional<C> resource,
                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                   final Optional<C> expected) {
        this.handleAllAndCheck(this.createHandler(),
                resource,
                parameters,
                expected);
    }

    default void handleAllAndCheck(final HateosResourceHandler<I, V, C> handler,
                                   final Optional<C> resource,
                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                   final Optional<C> expected) {
        this.checkEquals(expected, handler.handleAll(resource, parameters));
    }

    // handleMany.......................................................................................................

    @Test
    default void testHandleManyNullIdsFails() {
        this.handleManyFails(
                null,
                this.collectionResource(),
                this.parameters(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleManyNullResourceFails() {
        this.handleManyFails(
                this.manyIds(),
                null,
                this.parameters(),
                NullPointerException.class);
    }

    @Test
    default void testHandleManyNullParametersFails() {
        this.handleManyFails(
                this.manyIds(),
                this.collectionResource(),
                null,
                NullPointerException.class);
    }

    default <T extends Throwable> T handleManyFails(final Set<I> ids,
                                                    final Optional<C> resource,
                                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                                    final Class<T> thrown) {
        return this.handleManyFails(this.createHandler(),
                ids,
                resource,
                parameters,
                thrown);
    }

    default <T extends Throwable> T handleManyFails(final HateosResourceHandler<I, V, C> handler,
                                                    final Set<I> ids,
                                                    final Optional<C> resource,
                                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                                    final Class<T> thrown) {
        return assertThrows(thrown, () -> {
            handler.handleMany(ids, resource, parameters);
        });
    }

    default void handleManyAndCheck(final Set<I> ids,
                                    final Optional<C> resource,
                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                    final Optional<C> expected) {
        this.handleManyAndCheck(this.createHandler(),
                ids,
                resource,
                parameters,
                expected);
    }

    default void handleManyAndCheck(final HateosResourceHandler<I, V, C> handler,
                                    final Set<I> ids,
                                    final Optional<C> resource,
                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                    final Optional<C> expected) {
        this.checkEquals(expected, handler.handleMany(ids, resource, parameters));
    }

    // handleNone.......................................................................................................

    @Test
    default void testHandleNoneNullResourceFails() {
        this.handleNoneFails(null,
                this.parameters(),
                NullPointerException.class);
    }

    @Test
    default void testHandleNoneNullParametersFails() {
        this.handleNoneFails(this.resource(),
                null,
                NullPointerException.class);
    }

    default <T extends Throwable> T handleNoneFails(final Optional<V> resource,
                                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                                    final Class<T> thrown) {
        return this.handleNoneFails(this.createHandler(),
                resource,
                parameters,
                thrown);
    }

    default <T extends Throwable> T handleNoneFails(final HateosResourceHandler<I, V, C> handler,
                                                    final Optional<V> resource,
                                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                                    final Class<T> thrown) {
        return assertThrows(thrown, () -> {
            handler.handleNone(resource, parameters);
        });
    }

    default void handleNoneAndCheck(final Optional<V> resource,
                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                    final Optional<V> expected) {
        this.handleNoneAndCheck(this.createHandler(),
                resource,
                parameters,
                expected);
    }

    default void handleNoneAndCheck(final HateosResourceHandler<I, V, C> handler,
                                    final Optional<V> resource,
                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                    final Optional<V> expected) {
        this.checkEquals(expected, handler.handleNone(resource, parameters));
    }

    // handleOne.......................................................................................................

    @Test
    default void testHandleOneNullIdFails() {
        this.handleOneFails(null,
                this.resource(),
                this.parameters(),
                NullPointerException.class);
    }

    @Test
    default void testHandleOneNullResourceFails() {
        this.handleOneFails(this.id(),
                null,
                this.parameters(),
                NullPointerException.class);
    }

    @Test
    default void testHandleOneNullParametersFails() {
        this.handleOneFails(this.id(),
                this.resource(),
                null,
                NullPointerException.class);
    }

    default <T extends Throwable> T handleOneFails(final I id,
                                                   final Optional<V> resource,
                                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                                   final Class<T> thrown) {
        return this.handleOneFails(this.createHandler(),
                id,
                resource,
                parameters,
                thrown);
    }

    default <T extends Throwable> T handleOneFails(final HateosResourceHandler<I, V, C> handler,
                                                   final I id,
                                                   final Optional<V> resource,
                                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                                   final Class<T> thrown) {
        return assertThrows(thrown, () -> {
            handler.handleOne(id, resource, parameters);
        });
    }

    default void handleOneAndCheck(final I id,
                                   final Optional<V> resource,
                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                   final Optional<V> expected) {
        this.handleOneAndCheck(this.createHandler(),
                id,
                resource,
                parameters,
                expected);
    }

    default void handleOneAndCheck(final HateosResourceHandler<I, V, C> handler,
                                   final I id,
                                   final Optional<V> resource,
                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                   final Optional<V> expected) {
        this.checkEquals(expected, handler.handleOne(id, resource, parameters));
    }

    // handleRange.......................................................................................................

    @Test
    default void testHandleRangeNullRangeFails() {
        this.handleRangeFails(null,
                this.collectionResource(),
                this.parameters(),
                NullPointerException.class);
    }

    @Test
    default void testHandleRangeNullResourceFails() {
        this.handleRangeFails(this.range(),
                null,
                this.parameters(),
                NullPointerException.class);
    }

    @Test
    default void testHandleRangeNullParametersFails() {
        this.handleRangeFails(this.range(),
                this.collectionResource(),
                null,
                NullPointerException.class);
    }

    default <T extends Throwable> T handleRangeFails(final Range<I> range,
                                                     final Optional<C> resource,
                                                     final Map<HttpRequestAttribute<?>, Object> parameters,
                                                     final Class<T> thrown) {
        return this.handleRangeFails(this.createHandler(),
                range,
                resource,
                parameters,
                thrown);
    }

    default <T extends Throwable> T handleRangeFails(final HateosResourceHandler<I, V, C> handler,
                                                     final Range<I> range,
                                                     final Optional<C> resource,
                                                     final Map<HttpRequestAttribute<?>, Object> parameters,
                                                     final Class<T> thrown) {
        return assertThrows(thrown, () -> {
            handler.handleRange(range, resource, parameters);
        });
    }

    default void handleRangeAndCheck(final Range<I> range,
                                     final Optional<C> resource,
                                     final Map<HttpRequestAttribute<?>, Object> parameters,
                                     final Optional<C> expected) {
        this.handleRangeAndCheck(this.createHandler(),
                range,
                resource,
                parameters,
                expected);
    }

    default void handleRangeAndCheck(final HateosResourceHandler<I, V, C> handler,
                                     final Range<I> range,
                                     final Optional<C> resource,
                                     final Map<HttpRequestAttribute<?>, Object> parameters,
                                     final Optional<C> expected) {
        this.checkEquals(expected, handler.handleRange(range, resource, parameters));
    }

    // helpers..........................................................................................................

    H createHandler();

    I id();

    Set<I> manyIds();

    Range<I> range();

    Optional<V> resource();

    Optional<C> collectionResource();

    Map<HttpRequestAttribute<?>, Object> parameters();

    @Override
    default String typeNameSuffix() {
        return HateosResourceHandler.class.getSimpleName();
    }
}
