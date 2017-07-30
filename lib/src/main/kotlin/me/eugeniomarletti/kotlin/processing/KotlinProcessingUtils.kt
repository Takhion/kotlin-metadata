package me.eugeniomarletti.kotlin.processing

import java.util.Locale
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.lang.model.SourceVersion
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

interface KotlinProcessingUtils {
    val options: Map<String, String>
    val messager: Messager
    val filer: Filer
    val elementUtils: Elements
    val typeUtils: Types
    val sourceVersion: SourceVersion
    val locale: Locale
}
