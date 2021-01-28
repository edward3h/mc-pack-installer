package org.ethelred.mc.pack_installer

import spock.lang.Specification

import java.nio.file.Paths

/**
 * TODO
 *
 * @author edward3h
 * @since 2021-01-06
 */
class ConfigScriptTest extends Specification {
    def root = Paths.get(getClass().getResource('/ConfigScriptTest').toURI())

    def "simple case"() {
        given:
            def path = root.resolve 'simple.groovy'
            def config = new DefaultConfig()

        when:
            config.load path

        then:
            with(config) {
                targets.size() == 1
                targets.first().path.fileName.toString() == 'testtarget'
                sources.size() == 1
                sources.first().path.fileName.toString() == 'testsource'
            }
    }

    def "same directory for target and source"() {
        given:
        def path = root.resolve 'clash.groovy'
        def config = new DefaultConfig()

        when:
        config.load path

        then:
        thrown(IllegalStateException)
    }

    def "target can be converted to web target"() {
        given:
        def path = root.resolve 'webconvert.groovy'
        def config = new DefaultConfig()

        when:
        config.load path

        then:
        with(config) {
            targets.size() == 1
            targets.first() instanceof WebTarget
            targets.first().path.fileName.toString() == 'testtarget'
            targets.first().remote == 'testremote'
        }
    }

    def "closures apply properties"() {
        given:
        def path = root.resolve 'closures.groovy'
        def config = new DefaultConfig()

        when:
        config.load path

        then:
        with(config) {
            targets.size() == 1
            targets.first() instanceof WebTarget
            targets.first().path.fileName.toString() == 'testtarget'
            targets.first().remote == 'testremote'
            sources.size() == 1
            sources.first().path.fileName.toString() == 'testsource'
            sources.first().development == true
        }
    }

    def "can reopen a location and add to properties"() {

        given:
        def path = root.resolve 'reopen.groovy'
        def config = new DefaultConfig()

        when:
        config.load path

        then:
        with(config) {
            targets.size() == 1
            targets.first().path.fileName.toString() == 'foo'
            targets.first().includes.collect {it.toString() } == ['Quack', 'Honk', 'Hiss']
        }
    }
}
