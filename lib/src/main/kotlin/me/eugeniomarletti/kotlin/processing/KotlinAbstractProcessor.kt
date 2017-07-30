package me.eugeniomarletti.kotlin.processing

import java.util.Locale
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Completion
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

abstract class KotlinAbstractProcessor : AbstractProcessor(), KotlinProcessingUtils {

    override fun getSupportedOptions(): Set<String> = super.getSupportedOptions()
    override fun getSupportedSourceVersion(): SourceVersion = super.getSupportedSourceVersion()
    override fun getSupportedAnnotationTypes(): Set<String> = super.getSupportedAnnotationTypes()
    override fun init(processingEnv: ProcessingEnvironment) = super.init(processingEnv)

    override fun getCompletions(
        element: Element?,
        annotation: AnnotationMirror?,
        member: ExecutableElement?,
        userText: String?
    ): Iterable<Completion>
        = super.getCompletions(element, annotation, member, userText)

    override abstract fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean

    override val options: Map<String, String> get() = processingEnv.options
    override val messager: Messager get() = processingEnv.messager
    override val filer: Filer get() = processingEnv.filer
    override val elementUtils: Elements get() = processingEnv.elementUtils
    override val typeUtils: Types get() = processingEnv.typeUtils
    override val sourceVersion: SourceVersion get() = processingEnv.sourceVersion
    override val locale: Locale get() = processingEnv.locale
}
