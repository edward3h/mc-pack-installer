package org.ethelred.mc.pack_installer

import dev.dirs.ProjectDirectories
import groovy.transform.ToString
import org.codehaus.groovy.control.CompilerConfiguration

import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths

@ToString
class DefaultConfig implements Config {
    GroovyClassLoader classLoader

    DefaultConfig() {
        this.classLoader = new GroovyClassLoader(this.getClass().getClassLoader(),
                new CompilerConfiguration(scriptBaseClass: ConfigScript.name))
    }

    List<Target> targets = []
    List<Source> sources = []

    def loadDefault() {
        def defaultFile = Paths.get(getClass().getResource("/default_config.groovy").toURI())
        load(defaultFile)
    }

    def loadUser() {
        def userFile = FileSystems.getDefault()
                .getPath(ProjectDirectories.from("org", "ethelred", "mc-pack-installer").configDir, "config.groovy")
        load(userFile)
    }

    def load(file) {
        log.warn "$file"
        if (file && Files.isReadable(file)) {
            log.warn "${file.text}"
            //noinspection GrDeprecatedAPIUsage
            def script = classLoader.parseClass(file.text).newInstance()
            script.binding = new Binding(
                    mcpelauncher: ProjectDirectories.from("", "", "mcpelauncher").dataDir,
                    HOME: System.getProperty("user.home")
            )
            log.warn "${this}"
            script.setAppConfig(this)
            log.warn "${script.dump()}"
            script.run()
        } else {
        log.info "No config file with path $configOverride"
    }
    }
}