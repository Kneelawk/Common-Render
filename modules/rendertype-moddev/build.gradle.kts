plugins {
    id("com.kneelawk.versioning")
    id("com.kneelawk.kpublish")
    id("net.neoforged.moddev")
}

val maven_group: String by project
group = maven_group
val mod_id: String by project

neoForge {
    val neoform_version: String by project
    neoFormVersion.set(neoform_version)

    validateAccessTransformers.set(true)

    mods {
        create(mod_id) {
            sourceSet(sourceSets.main.get())
        }
    }

    accessTransformers {
        publish(file("src/main/resources/META-INF/accesstransformer.cfg"))
    }
}

tasks {
    processResources.configure {
        val properties = mapOf(
            "version" to project.version,
            "mod_id" to mod_id
        )

        inputs.properties(properties)

        filesMatching("META-INF/neoforge.mods.toml") {
            expand(properties)
        }
    }
}

kpublish {
    createPublication()
}
