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

import walkingkooka.tree.xml.XmlNode;

public class FakeHateosResource<I> implements HateosResource<I> {

    @Override
    public String hateosLinkId() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public I id() {
        throw new UnsupportedOperationException();
    }

    @Override
    public XmlNode toXmlNode() {
        throw new UnsupportedOperationException();
    }
}
