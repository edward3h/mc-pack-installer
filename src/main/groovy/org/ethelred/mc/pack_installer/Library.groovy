/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack_installer

import org.ethelred.mc.pack.Pack
import org.ethelred.mc.pack.PackId

import com.google.common.graph.GraphBuilder
import com.google.common.graph.Graphs

/**
 * logic for combining multiple found instance of packs and resolving dependencies
 */
class Library {
    Map<PackId, Pack> data = new HashMap()

    def leftShift(Pack pack) {
        log.info "add pack $pack"
        assert pack != null
        data.get(pack, pack) << pack // nice
    }

    List<List<Pack>> getDependencyGroups() {
        def graphbuilder = GraphBuilder.undirected().immutable();
        data.values().each { p ->
            graphbuilder.addNode(p)
            p.dependencies.each { id ->
                def d = data[id]
                if (d) graphbuilder.putEdge(p, d)
                else log.error("Missing dependency $d")
            }
        }
        def graph = graphbuilder.build()
        new HashSet(data.values().collect { Graphs.reachableNodes(graph, it) })
        .collect { d -> d.sort {it.name }}
        .sort { it.first().name }
    }
}
