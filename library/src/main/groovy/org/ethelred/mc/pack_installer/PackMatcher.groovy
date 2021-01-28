package org.ethelred.mc.pack_installer

import org.ethelred.mc.pack.Pack

import java.util.regex.Pattern

/**
 * TODO
 *
 * @author edward3h
 * @since 2021-01-05
 */
class PackMatcher {
    static Set<Pattern> DEFAULT = [~/.*/]
    Set<Pattern> includes = []
    Set<Pattern> excludes = []

    boolean matches(pack) {
        def name = pack.name as String
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Pack $pack has no name")
        }

        if (includes.empty && excludes.empty) {
            return true // short circuit
        }

        def _includes = includes;
        def _excludes = excludes ?: DEFAULT;

        _includes.any { _match(it, name)} || _excludes.every { !_match(it, name)}

    }

    private static boolean _match(pattern, value) {
        def m = pattern.matcher(value)
        m.matches()
    }

    void include(value) {
        _handle(value, includes)
    }

    void exclude(value) {
        _handle(value, excludes)
    }

    private void _handle(value, set) {
        switch (value) {
            case null:
                break // ignore
            case Pattern:
                set << value
                break
            case String:
                _handle(Pattern.compile(value), set)
                break
            case Collection:
            case Object[]:
                value.each {_handle(it, set)}
                break
            default:
                throw new IllegalArgumentException("Unhandled value $value")
        }
    }
}
