/* (C) 2020 Edward Harman */
package org.ethelred.util.groovy


import org.codehaus.groovy.runtime.typehandling.GroovyCastException

import groovy.transform.ToString
/**
 * TODO
 */
@ToString
class Table {
    Map data
    Headers columns

    Table(TableBuilder tb) {
        def mc = metaClass = new ExpandoMetaClass(getClass())
        data = tb.build()
        columns = tb.header

        columns.each {col ->
            mc."findBy${col.name.capitalize()}" = { value ->
                def cv = col.coerce(value)
                data.values().find { it[col.name] == cv }
            }
        }

        data.each { rowKey, row ->
            mc."get${rowKey.toString().capitalize()}" = { row }
        }
        metaClass.initialize()
    }

    static Table of(Closure block) {
        def tb = new TableBuilder().tap(block)
        new Table(tb)
    }

    def getRows() {
        data.values()
    }
}

@ToString
class TableBuilder {
    Headers header
    List rows
    def context

    Map build() {
        def cols = header.headers
        def r = [:]
        rows.each { row ->
            if (row.size() != cols.size()) {
                throw new IllegalStateException("row $row does not match number of columns")
            }
            def rowMap = [:]
            cols.eachWithIndex { Header col, int i ->
                rowMap[col.name] = col.coerce(row[i])
            }
            def rowKey = cols[0].coerce(row[0])
            r[rowKey] = rowMap
        }
        r
    }

    def propertyMissing(String name) {
        if (!header) {
            header = new Headers(new Header(name: name))
            return header
        }
        else {
            return new UnresolvedProperty(name: name, tb: this)
        }
    }

    def or(value) {
        if (rows) {
            rows.last() << value
        } else if (header && context instanceof Header) {
            header << context
            context = null
        } else {
            throw new IllegalStateException("Unexpected 'or' $value")
        }
        return this
    }

    def methodMissing(String name, def args) {
        if (args[0] instanceof Class) {
            Header h = new Header(name: name, type: args[0])
            if (!header) {
                header = new Headers(h)
                return header
            } else {
                context = h
                return h
            }
        }
        return this
    }

    def startRow(firstValue, secondValue) {
        if (!rows) {
            rows = []
        }
        def row = new Row(firstValue, secondValue)
        rows << row
        return row
    }
}

@ToString
class Header {
    String name
    Class type = Object
    private Map _coerceMethods = [:]

    def coerce(value) {
        if (value == null) return null
        if (type.isInstance(value)) return value
        def r
        def _coerceMethod = _coerceMethods[value.getClass()]
        switch(_coerceMethod) {
            case 'asType':
                return value.asType(type)
            case 'newInstance':
                return type.newInstance(value)
            case MetaMethod:
                return _coerceMethod.invoke(null, value)
            default:
                try {
                    r = value.asType (type)
                    _coerceMethods[value.getClass()] = 'asType'
                } catch (GroovyCastException ignored) {
                    try {
                        r = type.newInstance(value)
                        _coerceMethods[value.getClass()] = 'newInstance'
                    } catch (GroovyRuntimeException e2) {
                        MetaMethod mm = type.metaClass.methods.find {
                            it.isStatic() && it.returnType == type && it.isValidMethod(value)
                        }
                        if (mm) {
                            r = mm.invoke(null, value)
                            _coerceMethods[value.getClass()] = mm
                        } else {
                            throw e2
                        }
                    }
                }
        }
        return r
    }
}

@ToString
class UnresolvedProperty {
    String name
    TableBuilder tb

    def or(value) {
        tb.startRow(name, value)
    }
}

@ToString
class Headers {
    def headers = []

    Headers(Header h) {
        headers << h
    }

    def or(Header h) {
        headers << h
        return this
    }

    def or(UnresolvedProperty p) {
        headers << new Header(name: p.name)
        return this
    }

    def getAt(int i) {
        headers[i]
    }

    def propertyMissing(String name) {
        headers.find { it.name == name }
    }

    def size() {
        headers.size()
    }

    def each(Closure block) {
        headers.each(block)
    }
}

class Row {
    @Delegate
    List l

    Row(def... values) {
        l = values
    }

    def or(UnresolvedProperty p) {
        l << p.name
        return this
    }

    def or(value) {
        l << value
        return this
    }
}