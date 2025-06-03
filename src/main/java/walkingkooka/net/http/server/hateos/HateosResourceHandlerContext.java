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

import walkingkooka.Context;
import walkingkooka.net.header.CharsetName;
import walkingkooka.net.header.MediaType;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

/**
 * {@link Context} that accompanies all {@link HateosResourceHandler methods}.
 */
public interface HateosResourceHandlerContext extends JsonNodeMarshallUnmarshallContext {

    /**
     * The default {@link MediaType}.
     * Currently only json is supported, with an assumed charset of UTF8
     */
    MediaType HATEOS_DEFAULT_CONTENT_TYPE = MediaType.APPLICATION_JSON.setCharset(CharsetName.UTF_8);

    MediaType contentType();

    @Override
    HateosResourceHandlerContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor);
}
