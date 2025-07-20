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

import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContextTesting;

public interface HateosResourceHandlerContextTesting<C extends HateosResourceHandlerContext> extends JsonNodeMarshallUnmarshallContextTesting<C> {

    default void indentationAndCheck(final C context,
                                     final Indentation expected) {
        this.checkEquals(
            expected,
            context.indentation()
        );
    }

    default void lineEndingAndCheck(final C context,
                                    final LineEnding expected) {
        this.checkEquals(
            expected,
            context.lineEnding()
        );
    }
    
    @Override
    default String typeNameSuffix() {
        return HateosResourceHandlerContext.class.getSimpleName();
    }
}
