/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack

/**
 * version number
 */
class Version implements Comparable<Version> {
    private List list

    Version(List<Integer> list) {
        this.list = list.reverse().dropWhile { it == 0 }.reverse()
    }

    @Override
    int compareTo(Version o) {
        def digits = [list.size(), o.list.size()].max()
        for (i in 0..<digits) {
            def a = list[i] ?: 0
            def b = o.list[i] ?: 0
            def r = a <=> b
            if (r!=0) return r
        }
        0
    }

    @Override
    String toString() {
        list?.join('.') ?: '0'
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Version version = (Version) o
        return compareTo(version) == 0
    }

    int hashCode() {
        return list.inject(1) { acc, val -> acc * 17 + val}
    }
}
