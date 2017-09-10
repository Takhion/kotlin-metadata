@file:Suppress("unused")

package me.eugeniomarletti.kotlin.metadata

/**
 * Name of the processor option containing the path to the Kotlin generated src dir.
 */
const val kaptGeneratedOption = "kapt.kotlin.generated"

/**
 * Fully qualified name class name of [kotlin.Metadata] (which is internal).
 */
const val kotlinMetadataAnnotation = "kotlin.Metadata"

/**
 * Postfix of the method name containing the [kotlin.Metadata] annotation for the relative property.
 * @see [getPropertyOrNull]
 */
const val kotlinPropertyAnnotationsFunPostfix = "\$annotations"
