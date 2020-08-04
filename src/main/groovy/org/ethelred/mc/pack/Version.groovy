package org.ethelred.mc.pack

/**
 * version number
 */
class Version implements Comparable<Version> {
    private List list

    Version(List<Integer> list) {
        this.list = list
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
}
