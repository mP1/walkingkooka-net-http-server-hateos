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

import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;

/**
 * A collection of factory methods to create a {@link HateosResourceHandlerContext}
 */
public final class HateosResourceHandlerContexts implements PublicStaticHelper {

    /**
     * {@see BasicHateosResourceHandlerContext}
     */
    public static HateosResourceHandlerContext basic(final Indentation indentation,
                                                     final LineEnding lineEnding,
                                                     final JsonNodeMarshallUnmarshallContext context) {
        return BasicHateosResourceHandlerContext.with(
            indentation,
            lineEnding,
            context
        );
    }

    /**
     * {@see FakeHateosResourceHandlerContext}
     */
    public static HateosResourceHandlerContext fake() {
        return new FakeHateosResourceHandlerContext();
    }

    /**
     * Stop creation
     */
    private HateosResourceHandlerContexts() {
        throw new UnsupportedOperationException();
    }
}
