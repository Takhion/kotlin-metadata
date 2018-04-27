package me.eugeniomarletti

import com.google.auto.service.AutoService
import me.eugeniomarletti.Generator.Input
import me.eugeniomarletti.Generator.Parameter
import me.eugeniomarletti.Generator.TypeParameter
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.KotlinMetadataUtils
import me.eugeniomarletti.kotlin.metadata.extractFullName
import me.eugeniomarletti.kotlin.metadata.isDataClass
import me.eugeniomarletti.kotlin.metadata.isPrimary
import me.eugeniomarletti.kotlin.metadata.kaptGeneratedOption
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.processing.KotlinAbstractProcessor
import java.io.File
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic.Kind.ERROR

@AutoService(Processor::class)
@Suppress("unused")
class DataClassWithProcessor : KotlinAbstractProcessor(), KotlinMetadataUtils {

    private val annotationName = WithMethods::class.java.canonicalName

    override fun getSupportedAnnotationTypes() = setOf(annotationName)

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val annotationElement = elementUtils.getTypeElement(annotationName)
        @Suppress("LoopToCallChain")
        for (element in roundEnv.getElementsAnnotatedWith(annotationElement)) {
            val input = getInputFrom(element) ?: continue
            if (!input.generateAndWrite()) return true
        }
        return true
    }

    private fun getInputFrom(element: Element): Input? {
        val metadata = element.kotlinMetadata

        if (metadata !is KotlinClassMetadata) {
            errorMustBeDataClass(element)
            return null
        }

        val classData = metadata.data
        val (nameResolver, classProto) = classData

        fun ProtoBuf.Type.extractFullName() = extractFullName(classData)

        if (!classProto.isDataClass) {
            errorMustBeDataClass(element)
            return null
        }

        val fqClassName = nameResolver.getString(classProto.fqName).replace('/', '.')

        val `package` = nameResolver.getString(classProto.fqName).substringBeforeLast('/').replace('/', '.')

        val typeArguments = classProto.typeParameterList
            .map { typeArgument ->
                TypeParameter(
                    name = nameResolver.getString(typeArgument.name),
                    upperBoundsFqClassNames = typeArgument.upperBoundList.map { it.extractFullName() })
            }

        val parameters = classProto.constructorList
            .single { it.isPrimary }
            .valueParameterList
            .map { valueParameter ->
                Parameter(
                    name = nameResolver.getString(valueParameter.name),
                    fqClassName = valueParameter.type.extractFullName())
            }

        val extensionName = element.getAnnotation(WithMethods::class.java).extensionName

        return Input(
            fqClassName = fqClassName,
            `package` = `package`,
            typeArgumentList = typeArguments,
            parameterList = parameters,
            extensionName = extensionName)
    }

    private fun errorMustBeDataClass(element: Element) {
        messager.printMessage(ERROR,
            "@${WithMethods::class.java.simpleName} can't be applied to $element: must be a Kotlin data class", element)
    }

    private fun Input.generateAndWrite(): Boolean {
        val generatedDir = generatedDir ?: run {
            messager.printMessage(ERROR, "Can't find option '$kaptGeneratedOption'")
            return false
        }
        val dirPath = `package`.replace('.', File.separatorChar)
        val filePath = "DataClassWithExtensions_${fqClassName.substringAfter(`package`).replace('.', '_')}.kt"
        val dir = File(generatedDir, dirPath).also { it.mkdirs() }
        val file = File(dir, filePath)
        file.writeText(generate())
        return true
    }
}
