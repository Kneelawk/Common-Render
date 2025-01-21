plugins {
    id("com.kneelawk.versioning")
    id("com.kneelawk.kpublish")
    id("fabric-loom")
}

val maven_group: String by project
group = maven_group
val mod_id: String by project

loom {
    accessWidenerPath.set(file("src/main/resources/${mod_id}.accesswidener"))
}

repositories {
    maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
}

dependencies {
    val minecraft_version: String by project
    minecraft("com.mojang:minecraft:$minecraft_version")
    val parchment_mc_version: String by project
    val parchment_version: String by project
    mappings(loom.layered {
        officialMojangMappings {
            nameSyntheticMembers = true
        }
        parchment("org.parchmentmc.data:parchment-$parchment_mc_version:$parchment_version@zip")
    })

    val fabric_loader_version: String by project
    modCompileOnly("net.fabricmc:fabric-loader:$fabric_loader_version")
    modLocalRuntime("net.fabricmc:fabric-loader:$fabric_loader_version")
}

tasks {
    processResources.configure {
        val properties = mapOf(
            "version" to project.version,
            "mod_id" to mod_id
        )

        inputs.properties(properties)

        filesMatching("fabric.mod.json") {
            expand(properties)
        }
    }
}

kpublish {
    createPublication()
}
