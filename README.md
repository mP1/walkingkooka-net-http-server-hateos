[![Coverage Status](https://coveralls.io/repos/github/mP1/walkingkooka-net-http-server-hateos/badge.svg?branch=master)](https://coveralls.io/github/mP1/walkingkooka-net-http-server-hateos?branch=master)

# Basic Project

A Hateos library where resources and relations combinations are defined programmatically using an immutable fluent style.
These mappings are also used by the `Router` to create the links for `HateosResource` as they are marshalled, a sample
from a unit test is shown below:

```json
{
  "a": 1,
  "b": 2,
  "_links": [{
    "href": "http://example.com/api/resource1/7b",
    "method": "GET",
    "rel": "self",
    "type": "application/hal+json"
  }, {
    "href": "http://example.com/api/resource1/7b",
    "method": "POST",
    "rel": "self",
    "type": "application/hal+json"
  }, {
    "href": "http://example.com/api/resource1/7b/about",
    "method": "DELETE",
    "rel": "about",
    "type": "application/hal+json"
  }]
}
```

## Dependencies

- [https://github.com/mP1/walkingkooka-route](https://github.com/mP1/walkingkooka-net)
- junit

No actual releases are available, instead the latest snapshot may be referenced in a Maven POM.xml using
[https://jitpack.io](https://jitpack.io) repository.

```xml
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <dependencies>
       <dependency>
            <groupId>com.github.mP1</groupId>
            <artifactId>walkingkooka-net-http-server-hateos</artifactId>
            <version>master-SNAPSHOT</version>
        </dependency>
    </dependencies>
```



## Getting the source

You can either download the source using the "ZIP" button at the top
of the github page, or you can make a clone using git:

```
git clone git://github.com/mP1/walkingkooka-net-http-server-hateos.git
```
