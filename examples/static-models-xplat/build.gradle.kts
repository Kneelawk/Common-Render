plugins {
    id("com.kneelawk.versioning")
    id("com.kneelawk.submodule")
}

submodule {
    setRefmaps("static_models")
    xplatProjectDependency(":model-gltf")
    xplatProjectDependency(":engine-api")
    val kregistry_version: String by project
    xplatExternalDependency { "com.kneelawk.kregistry:kregistry-core-$it:$kregistry_version" }
}
