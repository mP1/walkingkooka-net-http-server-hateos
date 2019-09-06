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
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.header.MediaType;
import walkingkooka.tree.xml.XmlNode;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HateosContentTypeXmlNodeTest extends HateosContentTypeTestCase<HateosContentTypeXmlNode> {

    @Test
    public void testFromNodeFails() {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.hateosContentType().fromNode("<test1/>", TestHateosResource.class);
        });
    }

    @Test
    public void testFromNodeListFails() {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.hateosContentType().fromNodeList("<test1/>", TestHateosResource.class);
        });
    }

    @Test
    public void testToText() {
        this.toTextAndCheck(TestHateosResource.with(BigInteger.valueOf(123)),
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test-hateos-resource-1><id>123</id></test-hateos-resource-1>");
    }

    @Test
    public void testToTextList() {
        this.toTextListAndCheck(Lists.of(TestHateosResource.with(BigInteger.valueOf(111)), TestHateosResource.with(BigInteger.valueOf(222))),
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><list><test-hateos-resource-1><id>111</id></test-hateos-resource-1><test-hateos-resource-1><id>222</id></test-hateos-resource-1></list>");
    }

    @Override
    HateosContentTypeXmlNode hateosContentType() {
        return HateosContentTypeXmlNode.with(documentBuilder());
    }

    @Override
    MediaType contentType() {
        return MediaType.parse("application/hal+xml");
    }

    @Override
    String expectedToString() {
        return "XML";
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<HateosContentTypeXmlNode> type() {
        return HateosContentTypeXmlNode.class;
    }
}
