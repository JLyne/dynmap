import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.pluginYmlPaper)
    id("dynmap.java-conventions")
}

description = "dynmap"

dependencies {
    implementation(project(":dynmap-api"))
    implementation(project(":DynmapCore", "shadow"))
    implementation(project(":bukkit-helper"))

    compileOnly(libs.paperApi)
    compileOnly(libs.luckpermsApi)
    compileOnly(libs.vaultApi) { exclude("org.bukkit", "bukkit") }
    compileOnly(libs.groupManager)
    compileOnly(libs.jsonSimple)
}

tasks {
    processResources {
        // replace stuff in mcmod.info, nothing else
        filesMatching("paper-plugin.yml") {
            // replace version and mcversion
            expand(
                    "buildnumber" to project.parent!!.ext.get("buildNumber").toString(),
                    "version" to project.version
            )
        }
    }

    jar {
        archiveClassifier = "unshaded"
    }

    shadowJar {
        dependencies {
            include(dependency(":dynmap-api"))
            include(dependency(":DynmapCore"))
            include(dependency(":bukkit-helper"))
        }

        destinationDirectory = file("../target")
        archiveBaseName = "Dynmap"
        archiveClassifier = "paper"
    }

    artifacts {
        archives(shadowJar)
    }
}

paper {
    name = "dynmap"
    main = "org.dynmap.bukkit.DynmapPlugin"
    version = "${project.version}-${project.parent!!.ext.get("buildNumber")}"
    apiVersion = libs.versions.paper.get().replace(".build.+", "")
    authors = listOf("Jim (AnEnragedPigeon)", "mikeprimm")
    description = "Real time web-based map system"
    website = "https://www.reddit.com/r/Dynmap/"

    serverDependencies {
        register("GroupManager") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("LuckPerms") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("Vault") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }

    permissions {
        register("dynmap.render") {
            description = "Allows /dynmap render command"
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("dynmap.show.self") {
            description = "Allows /dynmap show (on self)"
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("dynmap.show.others") {
            description = "Allows /dynmap show <player>"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.hide.self") {
            description = "Allows /dynmap hide (on self)"
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("dynmap.hide.others") {
            description = "Allows /dynmap hide <player>"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.fullrender") {
            description = "Allows /dynmap fullrender or /dynmap fullrender <world>"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.radiusrender") {
            description = "Allows /dynmap radiusrender"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.updaterender") {
            description = "Allows /dynmap updaterender"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.cancelrender") {
            description = "Allows /dynmap cancelrender <world>"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.reload") {
            description = "Allows /dynmap reload"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.stats") {
            description = "Allows /dynmap stats, /dynmap stats <world>, or /dynmap triggerstats"
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("dynmap.resetstats") {
            description = "Allows /dynmap resetstats or /dynmap resetstats <world>"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.sendtoweb") {
            description = "Allows /dynmap sendtoweb"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.purgequeue") {
            description = "Allows /dynmap purgequeue"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.purgemap") {
            description = "Allows /dynmap purgemap"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.purgeworld") {
            description = "Allows /dynmap purgeworld"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.pause") {
            description = "Allows /dynmap pause"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.quiet") {
            description = "Allows /dynmap quiet"
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("dynmap.dumpmemory") {
            description = "Allows /dynmap dumpmemory"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.url") {
            description = "Allows /dynmap url"
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("dynmap.marker.add") {
            description = "Allows /dmarker add"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.update") {
            description = "Allows /dmarker update"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.movehere") {
            description = "Allows /dmarker movehere"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.delete") {
            description = "Allows /dmarker delete"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.list") {
            description = "Allows /dmarker list"
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("dynmap.marker.icons") {
            description = "Allows /dmarker icons"
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("dynmap.marker.sign") {
            description = "Allows creation of markers using signs"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.addset") {
            description = "Allows /dmarker addset"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.updateset") {
            description = "Allows /dmarker updateset"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.deleteset") {
            description = "Allows /dmarker deleteset"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.listsets") {
            description = "Allows /dmarker listsets"
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("dynmap.marker.addicon") {
            description = "Allows /dmarker addicon"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.updateicon") {
            description = "Allows /dmarker updateicon"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.deleteicon") {
            description = "Allows /dmarker deleteicon"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.addarea") {
            description = "Allows /dmarker addarea, /dmarker addcorner, /dmarker clearcorners"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.updatearea") {
            description = "Allows /dmarker updatearea"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.listareas") {
            description = "Allows /dmarker listareas"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.deletearea") {
            description = "Allows /dmarker deletearea"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.addline") {
            description = "Allows /dmarker addline"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.updateline") {
            description = "Allows /dmarker updateline"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.listlines") {
            description = "Allows /dmarker listlines"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.deleteline") {
            description = "Allows /dmarker deleteline"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.addcircle") {
            description = "Allows /dmarker addcircle"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.updatecircle") {
            description = "Allows /dmarker updatecircle"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.listcircles") {
            description = "Allows /dmarker listcircles"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.deletecircle") {
            description = "Allows /dmarker deletecircle"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.getdesc") {
            description = "Allows /dmarker getdesc"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.resetdesc") {
            description = "Allows /dmarker resetdesc"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.appenddesc") {
            description = "Allows /dmarker appenddesc"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.importdesc") {
            description = "Allows /dmarker importdesc"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.getlabel") {
            description = "Allows /dmarker getlabel"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.marker.importlabel") {
            description = "Allows /dmarker importlabel"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.dmap.worldlist") {
            description = "Allows /dmap worldlist"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.dmap.worldset") {
            description = "Allows /dmap worldset"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.dmap.worldreset") {
            description = "Allows /dmap worldreset"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.dmap.mapdelete") {
            description = "Allows /dmap mapdelete"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.dmap.mapset") {
            description = "Allows /dmap mapset"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.dmap.mapadd") {
            description = "Allows /dmap mapadd"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.dmap.perspectivelist") {
            description = "Allows /dmap perspectivelist"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.dmap.shaderlist") {
            description = "Allows /dmap shaderlist"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.dmap.lightinglist") {
            description = "Allows /dmap lightinglist"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("dynmap.playermarkers.seeall") {
            description = "Allow all players to be seen by user on web UI"
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }
}
