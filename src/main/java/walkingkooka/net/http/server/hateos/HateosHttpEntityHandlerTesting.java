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
public interface HateosHttpEntityHandlerTesting<H extends HateosHttpEntityHandler<I>,
        I extends Comparable<I>>
        extends ClassTesting2<H>,
        TreePrintableTesting,
        TypeNameTesting<H> {

    // handleAll.......................................................................................................

    @Test
    default void testHandleAllNullEntityFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createHandler()
                        .handleAll(
                                null,
                                HateosHttpEntityHandler.NO_PARAMETERS
                        )
        );
    }

    @Test
    default void testHandleAllNullParametersFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createHandler()
                        .handleAll(
                                HttpEntity.EMPTY,
                                null
                        )
        );
    }

    default void handleAllAndCheck(final HttpEntity entity,
                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                   final HttpEntity expected) {
        this.handleAllAndCheck(
                this.createHandler(),
                entity,
                parameters,
                expected
        );
    }

    default void handleAllAndCheck(final HateosHttpEntityHandler<I> handler,
                                   final HttpEntity entity,
                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                   final HttpEntity expected) {
        this.checkEquals(
                expected,
                handler.handleAll(
                        entity,
                        parameters
                )
        );
    }

    // handleMany.......................................................................................................

    @Test
    default void testHandleManyNullIdsFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createHandler()
                        .handleMany(
                                null,
                                HttpEntity.EMPTY,
                                HateosHttpEntityHandler.NO_PARAMETERS
                        )
        );
    }

    @Test
    default void testHandleManyNullEntityFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createHandler()
                        .handleMany(
                                this.manyIds(),
                                null,
                                HateosHttpEntityHandler.NO_PARAMETERS
                        )
        );
    }

    @Test
    default void testHandleManyNullParametersFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createHandler()
                        .handleMany(
                                this.manyIds(),
                                HttpEntity.EMPTY,
                                null
                        )
        );
    }

    default void handleManyAndCheck(final Set<I> ids,
                                    final HttpEntity entity,
                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                    final HttpEntity expected) {
        this.handleManyAndCheck(
                this.createHandler(),
                ids,
                entity,
                parameters,
                expected
        );
    }

    default void handleManyAndCheck(final HateosHttpEntityHandler<I> handler,
                                    final Set<I> ids,
                                    final HttpEntity entity,
                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                    final HttpEntity expected) {
        this.checkEquals(
                expected,
                handler.handleMany(
                        ids,
                        entity,
                        parameters
                )
        );
    }

    // handleNone.......................................................................................................

    @Test
    default void testHandleNoneNullEntityFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createHandler()
                        .handleNone(
                                null,
                                HateosHttpEntityHandler.NO_PARAMETERS
                        )
        );
    }

    @Test
    default void testHandleNoneNullParametersFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createHandler()
                        .handleNone(
                                HttpEntity.EMPTY,
                                null
                        )
        );
    }

    default void handleNoneAndCheck(final HttpEntity entity,
                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                    final HttpEntity expected) {
        this.handleNoneAndCheck(
                this.createHandler(),
                entity,
                parameters,
                expected
        );
    }

    default void handleNoneAndCheck(final HateosHttpEntityHandler<I> handler,
                                    final HttpEntity entity,
                                    final Map<HttpRequestAttribute<?>, Object> parameters,
                                    final HttpEntity expected) {
        this.checkEquals(
                expected,
                handler.handleNone(
                        entity,
                        parameters
                )
        );
    }

    // handleOne........................................................................................................

    @Test
    default void testHandleOneNullIdsFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createHandler()
                        .handleOne(
                                null,
                                HttpEntity.EMPTY,
                                HateosHttpEntityHandler.NO_PARAMETERS
                        )
        );
    }

    @Test
    default void testHandleOneNullEntityFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createHandler()
                        .handleOne(
                                this.id(),
                                null,
                                HateosHttpEntityHandler.NO_PARAMETERS
                        )
        );
    }

    @Test
    default void testHandleOneNullParametersFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createHandler()
                        .handleOne(
                                this.id(),
                                HttpEntity.EMPTY,
                                null
                        )
        );
    }

    default void handleOneAndCheck(final I id,
                                   final HttpEntity entity,
                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                   final HttpEntity expected) {
        this.handleOneAndCheck(
                this.createHandler(),
                id,
                entity,
                parameters,
                expected
        );
    }

    default void handleOneAndCheck(final HateosHttpEntityHandler<I> handler,
                                   final I id,
                                   final HttpEntity entity,
                                   final Map<HttpRequestAttribute<?>, Object> parameters,
                                   final HttpEntity expected) {
        this.checkEquals(
                expected,
                handler.handleOne(
                        id,
                        entity,
                        parameters
                )
        );
    }

    // handleRange......................................................................................................

    @Test
    default void testHandleRangeNullIdsFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createHandler()
                        .handleRange(
                                null,
                                HttpEntity.EMPTY,
                                HateosHttpEntityHandler.NO_PARAMETERS
                        )
        );
    }

    @Test
    default void testHandleRangeNullEntityFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createHandler()
                        .handleRange(
                                this.range(),
                                null,
                                HateosHttpEntityHandler.NO_PARAMETERS
                        )
        );
    }

    @Test
    default void testHandleRangeNullParametersFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createHandler()
                        .handleRange(
                                this.range(),
                                HttpEntity.EMPTY,
                                null
                        )
        );
    }

    default void handleRangeAndCheck(final Range<I> ids,
                                     final HttpEntity entity,
                                     final Map<HttpRequestAttribute<?>, Object> parameters,
                                     final HttpEntity expected) {
        this.handleRangeAndCheck(
                this.createHandler(),
                ids,
                entity,
                parameters,
                expected
        );
    }

    default void handleRangeAndCheck(final HateosHttpEntityHandler<I> handler,
                                     final Range<I> ids,
                                     final HttpEntity entity,
                                     final Map<HttpRequestAttribute<?>, Object> parameters,
                                     final HttpEntity expected) {
        this.checkEquals(
                expected,
                handler.handleRange(
                        ids,
                        entity,
                        parameters
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
}
