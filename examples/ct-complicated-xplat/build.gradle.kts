plugins {
    id("com.kneelawk.versioning")
    id("com.kneelawk.submodule")
}

submodule {
    setRefmaps("ct_complicated")
    xplatProjectDependency(":model-loading")
    xplatProjectDependency(":engine-api")
    val kregistry_version: String by project
    xplatExternalDependency { "com.kneelawk.kregistry:kregistry-core-$it:$kregistry_version" }
}
