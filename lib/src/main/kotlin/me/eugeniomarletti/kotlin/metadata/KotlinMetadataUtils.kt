package me.eugeniomarletti.kotlin.metadata

import me.eugeniomarletti.kotlin.metadata.jvm.JvmDescriptorUtils
import me.eugeniomarletti.kotlin.metadata.jvm.getJvmMethodSignature
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.NameResolver
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement

data class ClassData(
    val nameResolver: NameResolver,
    val classProto: ProtoBuf.Class)

data class PackageData(
    val nameResolver: NameResolver,
    val packageProto: ProtoBuf.Package)

/**
 * Main repository for extensions that need to access stuff inside [ProcessingEnvironment].
 */
interface KotlinMetadataUtils : JvmDescriptorUtils {

    /**
     * Returns the JVM signature in the form "$Name$MethodDescriptor", for example: `equals(Ljava/lang/Object;)Z`.
     *
     * Useful for comparing with [ProtoBuf.Function.getJvmMethodSignature][getJvmMethodSignature].
     *
     * For reference, see the [JVM specification, section 4.3](http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3).
     */
    val ExecutableElement.jvmMethodSignature: String
        get() = "$simpleName${asType().descriptor}"

    /**
     * If possible, returns the [ProtoBuf.Function] inside [functionList] represented by [methodElement].
     */
    fun getFunctionOrNull(
        methodElement: ExecutableElement,
        nameResolver: NameResolver,
        functionList: List<ProtoBuf.Function>
    ): ProtoBuf.Function? =
        methodElement.jvmMethodSignature.let { methodSignature ->
            functionList.firstOrNull { methodSignature == it.getJvmMethodSignature(nameResolver) }
        }

    /** @see [getFunctionOrNull] */
    fun ClassData.getFunctionOrNull(methodElement: ExecutableElement) =
        getFunctionOrNull(methodElement, nameResolver, proto.functionList)

    /** @see [getFunctionOrNull] */
    fun PackageData.getFunctionOrNull(methodElement: ExecutableElement) =
        getFunctionOrNull(methodElement, nameResolver, proto.functionList)
}

/**
 * If this [isNotBlank] then it adds the optional [prefix] and [postfix].
 */
fun String.plusIfNotBlank(
    prefix: String = "",
    postfix: String = ""
) =
    if (isNotBlank()) "$prefix${this}$postfix" else this

/**
 * Returns the escaped "readable" version of the internal Kotlin class name.
 */
val String.escapedClassName
    get() = split('/', '.').joinToString("`.`").plusIfNotBlank(prefix = "`", postfix = "`")

/**
 * Same as [ClassData.classProto], useful while copy/pasting when duplicating extensions for [PackageData].
 */
inline val ClassData.proto get() = classProto

/**
 * Same as [PackageData.packageProto], useful while copy/pasting when duplicating extensions for [ClassData].
 */
inline val PackageData.proto get() = packageProto

/**
 * If possible, returns the [ProtoBuf.Property] inside [propertyList] represented by [methodElement].
 */
inline fun getPropertyOrNull(
    methodElement: ExecutableElement,
    nameResolver: NameResolver,
    propertyList: () -> List<ProtoBuf.Property>
): ProtoBuf.Property? =
    methodElement.simpleName.toString()
        .takeIf { it.endsWith(kotlinPropertyAnnotationsFunPostfix) }
        ?.substringBefore(kotlinPropertyAnnotationsFunPostfix)
        ?.let { propertyName -> propertyList().firstOrNull { propertyName == nameResolver.getString(it.name) } }

/** @see [getPropertyOrNull] */
fun ClassData.getPropertyOrNull(methodElement: ExecutableElement) =
    getPropertyOrNull(methodElement, nameResolver, proto::getPropertyList)

/** @see [getPropertyOrNull] */
fun PackageData.getPropertyOrNull(methodElement: ExecutableElement) =
    getPropertyOrNull(methodElement, nameResolver, proto::getPropertyList)

/**
 * If possible, returns the [ProtoBuf.ValueParameter] inside [function] represented by [parameterElement].
 */
fun getValueParameterOrNull(
    nameResolver: NameResolver,
    function: ProtoBuf.Function,
    parameterElement: VariableElement
): ProtoBuf.ValueParameter? =
    parameterElement.simpleName.toString().let { parameterName ->
        function.valueParameterList.firstOrNull { parameterName == nameResolver.getString(it.name) }
    }

/** @see [getValueParameterOrNull] */
fun ClassData.getValueParameterOrNull(function: ProtoBuf.Function, parameterElement: VariableElement) =
    getValueParameterOrNull(nameResolver, function, parameterElement)

/** @see [getValueParameterOrNull] */
fun PackageData.getValueParameterOrNull(function: ProtoBuf.Function, parameterElement: VariableElement) =
    getValueParameterOrNull(nameResolver, function, parameterElement)

/**
 * Returns the fully qualified name of this type as it would be seen in the source code, including nullability and generic type parameters.
 *
 * Package and class names are escaped with backticks through [escapedClassName].
 *
 * @param [getTypeParameter]
 * A function that returns the type parameter for the given index.
 * **Only called if [ProtoBuf.Type.hasTypeParameter] is `true`!**
 *
 * @param [outputTypeAlias]
 * If `true` type aliases will be used, otherwise they will be replaced by the concrete type.
 *
 * @param [throwOnGeneric]
 * If not `null` it will be thrown if this type contains generic information.
 */
fun ProtoBuf.Type.extractFullName(
    nameResolver: NameResolver,
    getTypeParameter: (index: Int) -> ProtoBuf.TypeParameter,
    outputTypeAlias: Boolean = true,
    throwOnGeneric: Throwable? = null
): String {

    if (!hasClassName() && throwOnGeneric != null) throw throwOnGeneric

    val name = when {
        hasTypeParameter() -> getTypeParameter(typeParameter).name
        hasTypeParameterName() -> typeParameterName
        outputTypeAlias && hasAbbreviatedType() -> abbreviatedType.typeAliasName
        else -> className
    }.let { nameResolver.getString(it).escapedClassName }

    val argumentList = when {
        outputTypeAlias && hasAbbreviatedType() -> abbreviatedType.argumentList
        else -> argumentList
    }
    val arguments = argumentList
        .takeIf { it.isNotEmpty() }
        ?.joinToString(prefix = "<", postfix = ">") {
            when {
                it.hasType() -> it.type.extractFullName(nameResolver, getTypeParameter, outputTypeAlias, throwOnGeneric)
                throwOnGeneric != null -> throw throwOnGeneric
                else -> "*"
            }
        }
        ?: ""

    val nullability = if (nullable) "?" else ""

    return name + arguments + nullability
}

/** @see [extractFullName] */
fun ProtoBuf.Type.extractFullName(
    data: ClassData,
    outputTypeAlias: Boolean = true,
    throwOnGeneric: Throwable? = null
) =
    extractFullName(data.nameResolver, data.proto::getTypeParameter, outputTypeAlias, throwOnGeneric)

/** @see [extractFullName] */
fun ProtoBuf.Type.extractFullName(
    data: PackageData,
    outputTypeAlias: Boolean = true,
    throwOnGeneric: Throwable? = null
) =
    extractFullName(data.nameResolver, { throw IllegalStateException() }, outputTypeAlias, throwOnGeneric)
