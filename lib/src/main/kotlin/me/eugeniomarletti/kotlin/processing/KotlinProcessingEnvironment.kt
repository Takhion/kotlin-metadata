package me.eugeniomarletti.kotlin.processing

import java.util.Locale
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Wraps [processingEnv] and exposes it at the top level.
 * Useful for creating extensions with a receiver that also need to access stuff inside [ProcessingEnvironment].
 */
interface KotlinProcessingEnvironment {
    val processingEnv: ProcessingEnvironment

    val options: Map<String, String> get() = processingEnv.options
    val messager: Messager get() = processingEnv.messager
    val filer: Filer get() = processingEnv.filer
    val elementUtils: Elements get() = processingEnv.elementUtils
    val typeUtils: Types get() = processingEnv.typeUtils
    val sourceVersion: SourceVersion get() = processingEnv.sourceVersion
    val locale: Locale get() = processingEnv.locale
}
