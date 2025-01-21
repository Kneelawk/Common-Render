pluginManagement {
    repositories {
        maven("https://maven.quiltmc.org/repository/release") {
            name = "Quilt"
        }
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        maven("https://maven.architectury.dev/") {
            name = "Architectury"
        }
        maven("https://maven.neoforged.net/releases/") {
            name = "NeoForged"
        }
        maven("https://maven.kneelawk.com/releases/") {
            name = "Kneelawk"
        }
        gradlePluginPortal()
    }
    plugins {
        val loom_version: String by settings
        id("fabric-loom") version loom_version
        val moddev_version: String by settings
        id("net.neoforged.moddev") version moddev_version
        val remapcheck_version: String by settings
        id("com.kneelawk.remapcheck") version remapcheck_version
        val versioning_version: String by settings
        id("com.kneelawk.versioning") version versioning_version
        val kpublish_version: String by settings
        id("com.kneelawk.kpublish") version kpublish_version
        val submodule_version: String by settings
        id("com.kneelawk.submodule") version submodule_version
    }
}

rootProject.name = "krender"

fun module(enabled: Boolean, name: String) {
    if (!enabled) return
    include(name)
    project(":$name").projectDir = File(rootDir, "modules/${name.replace(':', '/')}")
}

fun module(name: String, vararg submodules: Pair<Boolean, String>) {
    include(name)
    project(":$name").projectDir = File(rootDir, "modules/$name")

    for ((enabled, submodule) in submodules) {
        if (!enabled) continue
        include("$name:$submodule")
        project(":$name:$submodule").projectDir = File(rootDir, "modules/$name/${submodule.replace(':', '/')}")
    }
}

fun example(enabled: Boolean, name: String) {
    if (!enabled) return
    include(name)
    project(":$name").projectDir = File(rootDir, "examples/${name.replace(':', '/')}")
}

fun example(name: String, vararg submodules: Pair<Boolean, String>) {
    include(name)
    project(":$name").projectDir = File(rootDir, "examples/$name")

    for ((enabled, submodule) in submodules) {
        if (!enabled) continue
        include("$name:$submodule")
        project(":$name:$submodule").projectDir = File(rootDir, "examples/$name/${submodule.replace(':', '/')}")
    }
}

fun javadoc(enabled: Boolean, name: String) {
    if (!enabled) return
    include("javadoc-$name")
    project(":javadoc-$name").projectDir = File(rootDir, "javadoc/$name")
}

val xplat = true
val mojmap = true
val fabric = true
val neoforge = true
val moddev = true

module(xplat, "engine-api-xplat")
module(mojmap, "engine-api-xplat-mojmap")
module(fabric, "engine-api-fabric")
module(neoforge, "engine-api-neoforge")

module(fabric, "engine-backend-frapi")
module(neoforge, "engine-backend-neoforge")

module(xplat, "model-loading-xplat")
module(mojmap, "model-loading-xplat-mojmap")
module(fabric, "model-loading-fabric")
module(neoforge, "model-loading-neoforge")

module(xplat, "reload-listener-xplat")
module(mojmap, "reload-listener-xplat-mojmap")
module(fabric, "reload-listener-fabric")
module(neoforge, "reload-listener-neoforge")

module(xplat, "model-guard-xplat")
module(mojmap, "model-guard-xplat-mojmap")
module(fabric, "model-guard-fabric")
module(neoforge, "model-guard-neoforge")

module(xplat, "model-obj-xplat")
module(mojmap, "model-obj-xplat-mojmap")
module(fabric, "model-obj-fabric")
module(neoforge, "model-obj-neoforge")

module(xplat, "model-gltf-xplat")
module(mojmap, "model-gltf-xplat-mojmap")
module(fabric, "model-gltf-fabric")
module(neoforge, "model-gltf-neoforge")

module(fabric, "rendertype-loom")
module(moddev, "rendertype-moddev")

example(xplat, "ct-complicated-xplat")
example(fabric, "ct-complicated-fabric")
example(neoforge, "ct-complicated-neoforge")

example(xplat, "static-models-xplat")
example(fabric, "static-models-fabric")
example(neoforge, "static-models-neoforge")
