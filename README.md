# Parse Interactive

A selenium-loading fetched page crawler.

You need SBT to use this. Once you have it just do this:

    make NUTCH_DIR=/path/to/nutch/runtime/local

Or you can do it manually, run

    sbt compile package assembly
    cp conf/plugin.xml /path/to/nutchdir/plugins/parse-interactive/
    cp target/scala-2.12/InteractiveParser-assembly-0.0.2.jar /path/to/nutchdir/plugins/parse-interactive.jar

To enable the plugin, make sure to add it to `parse-plugins.xml` and `nutch-site.xml`

Compiling with plugin dependencies:

    To compile a custom protocol, you most likely are trying to pull from `org.apache.nutch.protocol.http.api.HttpBase` which is not part of the normal nutch JAR. To be able to pull plugin deps in, you need to copy the relevant libs to the `lib/` directory. So, to get the `HttpBase` class, you need to copy `lib-http.jar` into `lib/` from the nutch `plugin/` directory.
