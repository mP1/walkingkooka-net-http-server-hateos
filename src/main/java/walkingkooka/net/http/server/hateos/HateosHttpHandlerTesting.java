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
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequests;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.net.http.server.HttpResponses;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.text.printer.TreePrintableTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Mixin interface for testing {@link HateosResourceHandler}
 */
public interface HateosHttpHandlerTesting<H extends HateosHttpHandler<C>,
        C extends HateosResourceHandlerContext>
        extends ClassTesting2<H>,
        TreePrintableTesting,
        TypeNameTesting<H> {

    // handleAll........................................................................................................

    @Test
    default void testHandleWithNullRequestFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createHandler()
                        .handle(
                                null,
                                HttpResponses.fake(),
                                this.context()
                        )
        );
    }

    @Test
    default void testHandleWithNullResponseFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createHandler()
                        .handle(
                                HttpRequests.fake(),
                                null,
                                this.context()
                        )
        );
    }

    @Test
    default void testHandleWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createHandler()
                        .handle(
                                HttpRequests.fake(),
                                HttpResponses.fake(),
                                null
                        )
        );
    }

    default void handleAndCheck(final H handler,
                                final HttpRequest request,
                                final C context,
                                final HttpResponse expected) {
        final HttpResponse response = HttpResponses.recording();

        handler.handle(
                request,
                response,
                context
        );

        this.checkEquals(
                expected,
                response,
                request::toString
        );
    }

    // helpers..........................................................................................................

    H createHandler();

    C context();

    @Override
    default String typeNamePrefix() {
        return "";
    }

    @Override
    default String typeNameSuffix() {
        return HateosHttpHandler.class.getSimpleName();
    }
}
