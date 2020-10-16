/* (C) 2020 Edward Harman */
package org.ethelred.util.groovy

import spock.lang.Specification
import groovy.transform.Canonical

import java.time.Year

/**
 * TODO
 *
 */
class TableTest extends Specification {
    def "simple table example"() {
        when:
        def table = Table.of {
            col1 | col2 | col3
            a    | 1    | "foo"
            b    | 2    | "bar"
        }

        then:
        table.a.col2 == 1
        table.rows.size() == 2
        table.columns.size() == 3
    }

    def "simple table example with types"() {
        when:
        def table = Table.of {
            col1(Character) | col2(Integer) | col3(String)
            a    | 1    | "foo"
            b    | 2    | "bar"
        }

        then:
        table.a.col2 == 1
        table.rows.size() == 2
        table.columns.size() == 3
        table.columns.col2.type == Integer

        when:
        def row2 = table.findByCol2(2)
        def rowFoo = table.findByCol3("foo")

        then:
        row2.col1 == 'b'
        rowFoo.col2 == 1
    }

    def "custom type coercion 1"() {
        when:
        def table = Table.of {
            col1(Character) | col2(Integer) | col3(Foobar)
            a    | 1    | "foo"
            b    | 2    | "bar"
        }

        then:
        table.a.col2 == 1
        table.rows.size() == 2
        table.columns.size() == 3
        table.columns.col2.type == Integer
    }

    def "custom type coercion 2"() {
        when:
        def table = Table.of {
            col1(Character) | col2(Integer) | col3(Banana)
            a    | 1    | "foo"
            b    | 2    | "bar"
        }

        then:
        table.a.col2 == 1
        table.b.col3 == Banana.eatThis("bar")
        table.rows.size() == 2
        table.columns.size() == 3
        table.columns.col2.type == Integer
    }

    def "custom type coercion 3"() {
        when:
        def table = Table.of {
            col1(Character) | col2(Integer) | col3(Banana)
            a    | 1    | "foo"
            b    | 2    | new Banana(v: "bar")
        }

        then:
        table.a.col2 == 1
        table.b.col3 == Banana.eatThis("bar")
        table.rows.size() == 2
        table.columns.size() == 3
        table.columns.col2.type == Integer
    }

    def "null columns"() {
        when:
        def table = Table.of {
            col1(Character) | col2(Integer) | col3(Foobar) | col4(Banana)
            a    | 1    | null | null
            b    | null    | "bar" | "yellow"
        }

        then:
        table.a.col2 == 1
        table.a.col3 == null
        table.a.col4 == null
        table.b.col2 == null
        table.b.col3 == new Foobar("bar")
        table.b.col4 == Banana.eatThis("yellow")
        table.rows.size() == 2
        table.columns.size() == 4
        table.columns.col2.type == Integer
    }

    def "bare words as values"() {
        when:
        def table = Table.of {
            col1(Character) | col2(Integer) | col3(What) | col4
            a | 1 | YES | 0.3
            b | 2 | MAYBE | 0.7
        }

        then:
        table.a.col3 == What.YES
        table.b.col3 == What.MAYBE
    }

    def "check some Groovy Map behavior"() {
        when:
        def map = [foo: "bar", size: "baz"]

        then:
        map.foo == "bar"
        map.size == "baz"
        map.size() == 2
    }
}

@Canonical
class Foobar {
    String v

    Foobar(String v) {
        this.v = v
    }
}

@Canonical
class Banana {
    String v

    static Banana eatThis(String v) {
        new Banana(v: v)
    }
}

enum What {
    YES,
    NO,
    MAYBE,
    FILENOTFOUND
}