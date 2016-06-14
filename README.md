# Broken links finder
Quickly check a website for broken links! 

To set it up, first run:

    ./activator clean update

Warning: this will download some stuff. When it's done, you can now simply start the server with:

    ./activator run
    
for default port 9000, or

    ./activator "run [PORT]"
    
for a custom one. In case you're not already familiar with Play, "clean update" part will get the needed dependencies and plugins (defined in build.sbt and plugins.sbt) and it's a one-time-thing (unless you add some new dependencies). Once that's done, you just fire up the server with `activator run`.

Note that this is very much work in progress. One of potential next steps would be parallelization using Spark for speedup. Code is already inside but there's no cluster; if you're willing to play with that, check out my spark-intro repo on how to set this up locally first, then move onto the cloud. Or simply ignore the whole Spark part (possibly even remove it from the project) and prettify the interface / add additional features to the engine / do whatever you want.

Have fun!

