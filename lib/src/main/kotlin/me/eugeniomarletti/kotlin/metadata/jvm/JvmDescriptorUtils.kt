package me.eugeniomarletti.kotlin.metadata.jvm

import me.eugeniomarletti.kotlin.processing.KotlinProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.QualifiedNameable
import javax.lang.model.type.ArrayType
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.ErrorType
import javax.lang.model.type.ExecutableType
import javax.lang.model.type.NoType
import javax.lang.model.type.NullType
import javax.lang.model.type.PrimitiveType
import javax.lang.model.type.TypeKind.BOOLEAN
import javax.lang.model.type.TypeKind.BYTE
import javax.lang.model.type.TypeKind.CHAR
import javax.lang.model.type.TypeKind.DOUBLE
import javax.lang.model.type.TypeKind.FLOAT
import javax.lang.model.type.TypeKind.INT
import javax.lang.model.type.TypeKind.LONG
import javax.lang.model.type.TypeKind.SHORT
import javax.lang.model.type.TypeMirror
import javax.lang.model.type.TypeVariable
import javax.lang.model.type.WildcardType
import javax.lang.model.util.AbstractTypeVisitor6
import javax.lang.model.util.Types

interface JvmDescriptorUtils : KotlinProcessingEnvironment {

    /**
     * @see [JvmDescriptorTypeVisitor]
     */
    val TypeMirror.descriptor: String
        get() = descriptor(typeUtils)

    /**
     * Returns the "field descriptor" of this type.
     * @see [JvmDescriptorTypeVisitor]
     */
    val WildcardType.descriptor: String
        get() = descriptor(typeUtils)

    /**
     * Returns the "field descriptor" of this type.
     * @see [JvmDescriptorTypeVisitor]
     */
    val TypeVariable.descriptor: String
        get() = descriptor(typeUtils)

    /**
     * Returns the "field descriptor" of this type.
     * @see [JvmDescriptorTypeVisitor]
     */
    val ArrayType.descriptor: String
        get() = descriptor(typeUtils)

    /**
     * Returns the "method descriptor" of this type.
     * @see [JvmDescriptorTypeVisitor]
     */
    val ExecutableType.descriptor: String
        get() = descriptor(typeUtils)
}

/**
 * Returns the name of this [Element] in its "internal form".
 *
 * For reference, see the [JVM specification, section 4.2](http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.2).
 */
val Element.internalName: String
    get() = when (this) {
        is QualifiedNameable -> qualifiedName.toString().replace('.', '/')
        else -> simpleName.toString()
    }
/**
 * Returns the "field descriptor" of this type.
 * @see [JvmDescriptorTypeVisitor]
 */
@Suppress("unused")
val NoType.descriptor: String
    get() = "V"
/**
 * Returns the "field descriptor" of this type.
 * @see [JvmDescriptorTypeVisitor]
 */
val DeclaredType.descriptor: String
    get() = "L" + asElement().internalName + ";"
/**
 * Returns the "field descriptor" of this type.
 * @see [JvmDescriptorTypeVisitor]
 */
val PrimitiveType.descriptor: String
    get() = when (this.kind) {
        BYTE -> "B"
        CHAR -> "C"
        DOUBLE -> "D"
        FLOAT -> "F"
        INT -> "I"
        LONG -> "J"
        SHORT -> "S"
        BOOLEAN -> "Z"
        else -> error("Unknown primitive type $this")
    }

/**
 * @see [JvmDescriptorTypeVisitor]
 * @see [JvmDescriptorUtils]
 */
fun TypeMirror.descriptor(typeUtils: Types): String =
    accept(JvmDescriptorTypeVisitor, typeUtils)

/**
 * Returns the "field descriptor" of this type.
 * @see [JvmDescriptorTypeVisitor]
 * @see [JvmDescriptorUtils]
 */
fun WildcardType.descriptor(typeUtils: Types): String =
    typeUtils.erasure(this).descriptor(typeUtils)

/**
 * Returns the "field descriptor" of this type.
 * @see [JvmDescriptorTypeVisitor]
 * @see [JvmDescriptorUtils]
 */
fun TypeVariable.descriptor(typeUtils: Types): String =
    typeUtils.erasure(this).descriptor(typeUtils)

/**
 * Returns the "field descriptor" of this type.
 * @see [JvmDescriptorTypeVisitor]
 * @see [JvmDescriptorUtils]
 */
fun ArrayType.descriptor(typeUtils: Types): String =
    "[" + componentType.descriptor(typeUtils)

/**
 * Returns the "method descriptor" of this type.
 * @see [JvmDescriptorTypeVisitor]
 * @see [JvmDescriptorUtils]
 */
fun ExecutableType.descriptor(typeUtils: Types): String {
    val parameterDescriptors = parameterTypes.joinToString(separator = "") { it.descriptor(typeUtils) }
    val returnDescriptor = returnType.descriptor(typeUtils)
    return "($parameterDescriptors)$returnDescriptor"
}

/**
 * When applied over a type, it returns either:
 * + a "field descriptor", for example: `Ljava/lang/Object;`
 * + a "method descriptor", for example: `(Ljava/lang/Object;)Z`
 *
 * The easiest way to use this is through [TypeMirror.descriptor][JvmDescriptorUtils.descriptor] in [JvmDescriptorUtils].
 *
 * For reference, see the [JVM specification, section 4.3](http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3).
 */
object JvmDescriptorTypeVisitor : AbstractTypeVisitor6<String, Types>() {

    override fun visitNoType(t: NoType, typeUtils: Types): String = t.descriptor
    override fun visitDeclared(t: DeclaredType, typeUtils: Types): String = t.descriptor
    override fun visitPrimitive(t: PrimitiveType, typeUtils: Types): String = t.descriptor

    override fun visitArray(t: ArrayType, typeUtils: Types): String = t.descriptor(typeUtils)
    override fun visitWildcard(t: WildcardType, typeUtils: Types): String = t.descriptor(typeUtils)
    override fun visitExecutable(t: ExecutableType, typeUtils: Types): String = t.descriptor(typeUtils)
    override fun visitTypeVariable(t: TypeVariable, typeUtils: Types): String = t.descriptor(typeUtils)

    override fun visitNull(t: NullType, typeUtils: Types): String = visitUnknown(t, typeUtils)
    override fun visitError(t: ErrorType, typeUtils: Types): String = visitUnknown(t, typeUtils)

    override fun visitUnknown(t: TypeMirror, typeUtils: Types): String = error("Unsupported type $t")
}
