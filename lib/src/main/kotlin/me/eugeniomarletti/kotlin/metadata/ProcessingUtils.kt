package me.eugeniomarletti.kotlin.metadata

import me.eugeniomarletti.kotlin.processing.KotlinProcessingUtils
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.ArrayType
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.ErrorType
import javax.lang.model.type.ExecutableType
import javax.lang.model.type.NoType
import javax.lang.model.type.NullType
import javax.lang.model.type.PrimitiveType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.lang.model.type.TypeVariable
import javax.lang.model.type.WildcardType
import javax.lang.model.util.AbstractTypeVisitor6
import javax.lang.model.util.Types

interface KotlinMetadataUtils : KotlinProcessingUtils {

    val ExecutableElement.jvmMethodSignature: String get() {
        fun TypeMirror.jvmDeclaration(): String = accept(JvmDeclarationTypeVisitor, typeUtils)
        val params = parameters.joinToString(separator = "") { it.asType().jvmDeclaration() }
        val returnType = returnType.jvmDeclaration()
        return "$simpleName($params)$returnType"
    }
}

/**
 * When applied over a method, it returns the JVM signature in the form "name(descriptor)returnValue", for example: "equals(Ljava/lang/Object;)Z".
 *
 * See the [JVM specification, section 4.3](http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3)
 * @see getJvmMethodSignature
 */
private object JvmDeclarationTypeVisitor : AbstractTypeVisitor6<String, Types>() {

    override fun visitTypeVariable(t: TypeVariable, typeUtils: Types): String =
        typeUtils.erasure(t).accept(this, typeUtils)

    override fun visitArray(t: ArrayType, typeUtils: Types): String =
        "[" + t.componentType.accept(this, typeUtils)

    override fun visitNoType(t: NoType, typeUtils: Types): String =
        "V"

    override fun visitDeclared(t: DeclaredType, typeUtils: Types): String {
        val internalName = (t.asElement() as TypeElement).qualifiedName.toString().replace('.', '/')
        return "L$internalName;"
    }

    override fun visitPrimitive(t: PrimitiveType, typeUtils: Types): String =
        when (t.kind) {
            TypeKind.BYTE -> "B"
            TypeKind.CHAR -> "C"
            TypeKind.DOUBLE -> "D"
            TypeKind.FLOAT -> "F"
            TypeKind.INT -> "I"
            TypeKind.LONG -> "J"
            TypeKind.SHORT -> "S"
            TypeKind.BOOLEAN -> "Z"
            else -> error("Unknown primitive type $t")
        }

    override fun visitExecutable(t: ExecutableType, typeUtils: Types): String = visitUnknown(t, typeUtils)
    override fun visitWildcard(t: WildcardType, typeUtils: Types): String = visitUnknown(t, typeUtils)
    override fun visitError(t: ErrorType, typeUtils: Types): String = visitUnknown(t, typeUtils)
    override fun visitNull(t: NullType, typeUtils: Types): String = visitUnknown(t, typeUtils)

    override fun visitUnknown(t: TypeMirror, typeUtils: Types): String = error("Unsupported type $t")
}
