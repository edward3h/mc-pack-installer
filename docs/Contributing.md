# Contributing

## Feedback
You can open feature requests or bug reports as 
[issues](https://github.com/edward3h/mc-pack-installer/issues). Please check 
if there is a relevant existing issue before opening a new one, and
include as much detail as you can in your issue.

## Documentation
If you can improve the documentation, feel free to submit a pull request or
add to the project Wiki.

## Coding
Feel free to submit pull requests for fixes or enhancements.

The code is written in [Groovy](https://groovy-lang.org/index.html), with 
[Gradle](https://gradle.org/) as the build system. The only external dependency 
is `java` 1.8. I decided to stick with 1.8 compatibility for now as it 
is easier to package.

Code must pass formatting and unit tests. `./gradlew test`. This is enforced
by a git hook that is installed by the build.

I recommend using IntelliJ IDEA as IDE since it seems to have the best support
for Groovy, however I sometimes use VS Code too.

_Note:_ Groovy compilation is using a [configuration](groovyc.groovy) that 
automatically adds the `log` property to every class.