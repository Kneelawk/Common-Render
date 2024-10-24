plugins {
    id("com.kneelawk.versioning")
    id("com.kneelawk.submodule")
    id("com.kneelawk.kpublish")
}

submodule {
    setRefmaps("krender_model_guard")
    val kregistry_version: String by project
    xplatExternalDependency { "com.kneelawk.kregistry:kregistry-lite-$it:$kregistry_version" }
    setupJavadoc()
}

kpublish {
    createPublication("intermediary")
}
