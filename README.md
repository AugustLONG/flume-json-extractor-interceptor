# Flume JSON Extractor Interceptor

This project provides a [Flume NG](http://flume.apache.org/) interceptor for extracting properties from the incoming event message and setting the flume event body to its value.

## Installation

To get started, clone the repository and build the package (Requires [Maven](http://maven.apache.org/))

```
git clone https://github.com/onedeadear/flume-json-extractor-interceptor.git
cd flume-json-extractor-interceptor
mvn package
```

Copy the resulting jar to a plugin specific lib directory:
```
sudo mkdir -p /usr/lib/flume-ng/plugins.d/flume-json-extractor-interceptor/lib
sudo cp target/flume-json-extractor-interceptor-1.0.jar /usr/lib/flume-ng/plugins.d/flume-json-extractor-interceptor/lib/
```

Copy the external [json library](https://github.com/douglascrockford/JSON-java)
```
mvn org.apache.maven.plugins:maven-dependency-plugin:2.8:get \
  -DgroupId=org.json \
  -DartifactId=json \
  -Dversion=20140107 \
  -Dtransitive=false

sudo mkdir -p /usr/lib/flume-ng/plugins.d/flume-json-extractor-interceptor/libext
sudo cp json-20140107.jar /usr/lib/flume-ng/plugins.d/flume-json-extractor-interceptor/libext/
```

## Configuration

The following configuration properties are supported by the plugin. Required properties are in **bold**

 Property Name         | Default | Description
-----------------------|---------|---------------------------------------------
 **type**              | -       | The component type name, needs to be *org.onedeadear.flume.interceptors.JSONExtractorInterceptor$Builder*
 jsonProperty          | -       | The property to extract

#### Example:

The following example will extract the Result property from a source supplying json events
```
# Name the components on this agent
a1.sources = r1
a1.sinks = k1
a1.channels = c1

# Describe/configure the source
a1.sources.r1.type = netcat
a1.sources.r1.bind = localhost
a1.sources.r1.port = 44444

**a1.sources.r1.interceptors = i1**
**a1.sources.r1.interceptors.i1.type = org.onedeadear.flume.interceptors.JSONExtractorInterceptor$Builder**
**a1.sources.r1.interceptors.i1.jsonProperty = Result**

# Describe the sink
a1.sinks.k1.type = logger

# Use a channel which buffers events in memory
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100

# Bind the source and sink to the channel
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1

```
