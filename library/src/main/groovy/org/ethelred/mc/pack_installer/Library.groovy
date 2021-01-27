/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack_installer

import org.ethelred.mc.pack.Pack
import org.ethelred.mc.pack.PackId

import com.google.common.graph.GraphBuilder
import com.google.common.graph.Graphs
import org.ethelred.mc.world.World

/**
 * logic for combining multiple found instance of packs and resolving dependencies
 */
class Library {
    Map<PackId, PackInstances> data = new HashMap()
    Set<World> worlds = new HashSet()

    def leftShift(LocationPack pack) {
        log.debug "add $pack"
        assert pack != null
        data.get(pack, new PackInstances()) << pack // nice
    }

    def leftShift(World world) {
        log.debug "add $world"
        assert world != null
        worlds << world
    }

    List<List<Pack>> getDependencyGroups() {
        def graphbuilder = GraphBuilder.undirected().immutable()
        data.values().each { p ->
            graphbuilder.addNode(p)
            p.dependencies.each { id ->
                def d = data[id]
                if (d) graphbuilder.putEdge(p, d)
                else log.error("Missing dependency $id for pack ${p.toStringShort()}")
            }
        }
        def graph = graphbuilder.build()
        new HashSet(data.values().collect { Graphs.reachableNodes(graph, it) })
        .collect { d -> d.sort { PackInstances p -> p.name.strip }}
        .sort { it.first().name.strip }
    }

    List<World> getWorlds() {
        worlds.sort { it.name.strip }
    }
}
