/* (C) 2020 Edward Harman */
package org.ethelred.mc

import org.codehaus.groovy.control.CompilerConfiguration
import org.ethelred.mc.pack_installer.Config
import org.ethelred.mc.pack_installer.Flow
import org.ethelred.mc.pack_installer.Source
import org.ethelred.mc.pack_installer.Target
import org.ethelred.mc.pack_installer.UI
import org.ethelred.mc.pack_installer.WebTarget

import picocli.CommandLine
import dev.dirs.ProjectDirectories
import groovy.transform.ToString

import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Callable

import net.java.truevfs.access.TPath

@CommandLine.Command(name = "mc-pack-installer", mixinStandardHelpOptions = true, version = "1.0")
class App implements UI, Callable<Integer> {

    static void main(String... args) {
        new CommandLine(new App()).execute(args)
    }

    @CommandLine.Option(names = ["--config", "-c"], description = "Use config at this path instead of finding it in user directory")
    Path configOverride

    Config config = new AppConfig()

    @CommandLine.Option(names = ["--target", "-t"], description = "Add a target path")
    def addTarget(String path) {
        config.targets << new Target(path: path)
    }

    @CommandLine.Option(names = ["--source", "-s"], description = "Add a source path")
    def addSource(String path) {
        config.sources << new Source(path: new TPath(path))
    }

    def loadConfig() {
        // read defaults from resources. This is expected to exist
        def defaultFile = getClass().getResource("/default_config.groovy")
        config.load(defaultFile)

        // include override path, or user config path
        if (!configOverride) {
            configOverride = FileSystems.getDefault().getPath(ProjectDirectories.from("org", "ethelred", "mc-pack-installer").configDir, "config.groovy")
        }
        if (configOverride && Files.isReadable(configOverride)) {
            log.info "Trying to load config from $configOverride"
            config.load(configOverride)
        }
        log.info("Read config " + config)
    }

    @Override
    Integer call() throws Exception {
        loadConfig()
        def flow = new Flow(ui: this)
        flow.run()
        0
    }

    @Override
    List<Target> confirmTarget(List<Target> targets) {
        targets.each { println it }
    }

    @Override
    List<Source> confirmSource(List<Source> sources) {
        sources.each { println it }
    }

    @Override
    void listPacks(library) {
        //        library.dependencyGroups.each { g ->
        //            g.each { println it.toStringShort() }
        //            println '---'
        //        }
    }
}

@ToString
class AppConfig implements Config {
    GroovyClassLoader classLoader

    AppConfig() {
        this.classLoader = new GroovyClassLoader(this.getClass().getClassLoader(),
                new CompilerConfiguration(scriptBaseClass: 'org.ethelred.mc.AppConfigScript'))
    }

    List<Target> targets = []
    List<Source> sources = []

    def load(file) {
        //noinspection GrDeprecatedAPIUsage
        def script = classLoader.parseClass(file.text).newInstance()
        script.binding = new Binding(
                mcpelauncher: ProjectDirectories.from("", "", "mcpelauncher").dataDir,
                HOME: System.getProperty("user.home")
                )
        script.appConfig = this
        script.run()
    }
}

abstract class AppConfigScript extends Script {
    def target(String path) {
        appConfig.targets << new Target(path: path)
    }

    def source(String path) {
        appConfig.sources << new Source(path: new TPath(path))
    }

    def target(Closure block) {
        def t = new TargetBuilder().tap(block).build()
        if (t) {
            appConfig.targets << t
        }
    }
}

class TargetBuilder {
    String path
    String type

    Target build() {
        if (path) {
            if (type && "web".equalsIgnoreCase(type)) {
                return new WebTarget(path: path)
            } else {
                return new Target(path: path)
            }
        } else {
            log.error("target closure did not set path")
        }
    }

    def path(v) {
        this.path = v
    }

    def type(v) {
        this.type = v
    }
}