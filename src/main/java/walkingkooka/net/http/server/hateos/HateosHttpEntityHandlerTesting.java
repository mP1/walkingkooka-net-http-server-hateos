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
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Mixin interface for testing {@link HateosHttpEntityHandler}
 */
public interface HateosHttpEntityHandlerTesting<H extends HateosHttpEntityHandler<I, X>,
        I extends Comparable<I>,
        X extends HateosResourceHandlerContext>
        extends ClassTesting2<H>,
        TreePrintableTesting,
        TypeNameTesting<H> {

    // handleAll........................................................................................................

    @Test
    default void testHandleAllWithNullEntityFails() {
        this.handleAllFails(
                null,
                HateosHttpEntityHandler.NO_PARAMETERS,
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleAllWithNullParametersFails() {
        this.handleAllFails(
                this.entity(),
                null,
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleAllWithNullPathFails() {
        this.handleAllFails(
                this.entity(),
                this.parameters(),
                null,
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleAllWithNullContextFails() {
        this.handleAllFails(
                this.entity(),
                this.parameters(),
                this.path(),
                null,
                NullPointerException.class
        );
    }

    default <T extends Throwable> T handleAllFails(final HttpEntity entity,
                                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                                   final UrlPath path,
                                                   final X context,
                                                   final Class<T> thrown) {
        return this.handleAllFails(
                this.createHandler(),
                entity,
                parameters,
                path,
                context,
                thrown
        );
    }

    default <T extends Throwable> T handleAllFails(final HateosHttpEntityHandler<I, X> handler,
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

    default void handleAllAndCheck(final HttpEntity entity,
                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                   final UrlPath path,
                                   final X context,
                                   final HttpEntity expected) {
        this.handleAllAndCheck(
                this.createHandler(),
                entity,
                parameters,
                path,
                context,
                expected
        );
    }

    default void handleAllAndCheck(final HateosHttpEntityHandler<I, X> handler,
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

// handleMany.......................................................................................................

    @Test
    default void testHandleManyWithNullEntityFails() {
        this.handleManyFails(
                this.manyIds(),
                null,
                HateosHttpEntityHandler.NO_PARAMETERS,
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleManyWithNullParametersFails() {
        this.handleManyFails(
                this.manyIds(),
                this.entity(),
                null,
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleManyWithNullPathFails() {
        this.handleManyFails(
                this.manyIds(),
                this.entity(),
                this.parameters(),
                null,
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleManyWithNullContextFails() {
        this.handleManyFails(
                this.manyIds(),
                this.entity(),
                this.parameters(),
                this.path(),
                null,
                NullPointerException.class
        );
    }

    default <T extends Throwable> T handleManyFails(final Set<I> ids,
                                                    final HttpEntity entity,
                                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                                    final UrlPath path,
                                                    final X context,
                                                    final Class<T> thrown) {
        return this.handleManyFails(
                this.createHandler(),
                ids,
                entity,
                parameters,
                path,
                context,
                thrown
        );
    }

    default <T extends Throwable> T handleManyFails(final HateosHttpEntityHandler<I, X> handler,
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

    default void handleManyAndCheck(final Set<I> ids,
                                    final HttpEntity entity,
                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                    final UrlPath path,
                                    final X context,
                                    final HttpEntity expected) {
        this.handleManyAndCheck(
                this.createHandler(),
                ids,
                entity,
                parameters,
                path,
                context,
                expected
        );
    }

    default void handleManyAndCheck(final HateosHttpEntityHandler<I, X> handler,
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

    @Test
    default void testHandleNoneWithNullEntityFails() {
        this.handleNoneFails(
                null,
                this.parameters(),
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleNoneWithNullParametersFails() {
        this.handleNoneFails(
                this.entity(),
                null,
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleNoneWithNullPathFails() {
        this.handleNoneFails(
                this.entity(),
                this.parameters(),
                null,
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleNoneWithNullContextFails() {
        this.handleNoneFails(
                this.entity(),
                this.parameters(),
                this.path(),
                null,
                NullPointerException.class
        );
    }

    default <T extends Throwable> T handleNoneFails(final HttpEntity entity,
                                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                                    final UrlPath path,
                                                    final X context,
                                                    final Class<T> thrown) {
        return this.handleNoneFails(
                this.createHandler(),
                entity,
                parameters,
                path,
                context,
                thrown
        );
    }

    default <T extends Throwable> T handleNoneFails(final HateosHttpEntityHandler<I, X> handler,
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

    default void handleNoneAndCheck(final HttpEntity entity,
                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                    final UrlPath path,
                                    final X context,
                                    final HttpEntity expected) {
        this.handleNoneAndCheck(
                this.createHandler(),
                entity,
                parameters,
                path,
                context,
                expected
        );
    }

    default void handleNoneAndCheck(final HateosHttpEntityHandler<I, X> handler,
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

    @Test
    default void testHandleOneWithNullEntityFails() {
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
    default void testHandleOneWithNullParametersFails() {
        this.handleOneFails(
                this.id(),
                this.entity(),
                null,
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleOneWithNullPathFails() {
        this.handleOneFails(
                this.id(),
                this.entity(),
                this.parameters(),
                null,
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleOneWithNullContextFails() {
        this.handleOneFails(
                this.id(),
                this.entity(),
                this.parameters(),
                this.path(),
                null,
                NullPointerException.class
        );
    }

    default <T extends Throwable> T handleOneFails(final I id,
                                                   final HttpEntity entity,
                                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                                   final UrlPath path,
                                                   final X context,
                                                   final Class<T> thrown) {
        return this.handleOneFails(
                this.createHandler(),
                id,
                entity,
                parameters,
                path,
                context,
                thrown
        );
    }

    default <T extends Throwable> T handleOneFails(final HateosHttpEntityHandler<I, X> handler,
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

    default void handleOneAndCheck(final I id,
                                   final HttpEntity entity,
                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                   final UrlPath path,
                                   final X context,
                                   final HttpEntity expected) {
        this.handleOneAndCheck(
                this.createHandler(),
                id,
                entity,
                parameters,
                path,
                context,
                expected
        );
    }

    default void handleOneAndCheck(final HateosHttpEntityHandler<I, X> handler,
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

    @Test
    default void testHandleRangeWithNullIdsFails() {
        this.handleRangeFails(
                null,
                this.entity(),
                this.parameters(),
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleRangeWithNullEntityFails() {
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
    default void testHandleRangeWithNullParametersFails() {
        this.handleRangeFails(
                this.range(),
                this.entity(),
                null,
                this.path(),
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleRangeWithNullPathFails() {
        this.handleRangeFails(
                this.range(),
                this.entity(),
                this.parameters(),
                null,
                this.context(),
                NullPointerException.class
        );
    }

    @Test
    default void testHandleRangeWithNullContextFails() {
        this.handleRangeFails(
                this.range(),
                this.entity(),
                this.parameters(),
                this.path(),
                null,
                NullPointerException.class
        );
    }

    default <T extends Throwable> T handleRangeFails(final Range<I> ids,
                                                     final HttpEntity entity,
                                                     final Map<HttpRequestAttribute<?>, Object> parameters,
                                                     final UrlPath path,
                                                     final X context,
                                                     final Class<T> thrown) {
        return this.handleRangeFails(
                this.createHandler(),
                ids,
                entity,
                parameters,
                path,
                context,
                thrown
        );
    }

    default <T extends Throwable> T handleRangeFails(final HateosHttpEntityHandler<I, X> handler,
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

    default void handleRangeAndCheck(final Range<I> ids,
                                     final HttpEntity entity,
                                     final Map<HttpRequestAttribute<?>, Object> parameters,
                                     final UrlPath path,
                                     final X context,
                                     final HttpEntity expected) {
        this.handleRangeAndCheck(
                this.createHandler(),
                ids,
                entity,
                parameters,
                path,
                context,
                expected
        );
    }

    default void handleRangeAndCheck(final HateosHttpEntityHandler<I, X> handler,
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

    // helpers..........................................................................................................

    H createHandler();

    I id();

    Set<I> manyIds();

    Range<I> range();

    HttpEntity entity();

    Map<HttpRequestAttribute<?>, Object> parameters();

    UrlPath path();

    X context();
}
