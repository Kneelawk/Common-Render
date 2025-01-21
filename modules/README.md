# Modules

This contains the various different published modules that are part of KRender.

## engine-api-(fabric|neoforge|xplat|xplat-mojmap)

These modules contain the KRender Engine API. This is akin to the Fabric Render API, but is designed to be more of an
abstraction over rendering apis instead of its own dedicated rendering api.

## engine-backend-frapi

This module holds the Fabric Render API backend for the KRender Engine.

## engine-backend-neoforge

This module holds the NeoForge-native backend for the KRender engine.

## model-gltf-(fabric|neoforge|xplat|xplat-mojmap)

These modules hold a default loader for loading glTF files as block models.

## model-guard-(fabric|neoforge|xplat|xplat-mojmap)

These modules allow for the configuring of which default model loaders will be used for which resource paths to prevent
model-loading overlap between two different loaders.

## model-loading-(fabric|neoforge|xplat|xplat-mojmap)

These modules contain the model loading API. They provide several hooks into the model-loader mechanisms, allowing you
to register custom models, both top-level (like blockstates) and lower-level (like regular json block models). They also
provide ways for you to request various lower-level models be loaded as top-level models.

## model-obj-(fabric|neoforge|xplat|xplat-mojmap)

These modules hold a default loader for loading `.obj` files as block models.

## reload-listener-(fabric|neoforge|xplat|xplat-mojmap)

These modules hold events for notifying when resource (both datapack and resourcepack) reloads are occurring.

## rendertype-(loom|moddev)

These modules hold transitive accesswideners/accesstransformers for widening access to various `RenderType` methods,
subclasses, and fields.
