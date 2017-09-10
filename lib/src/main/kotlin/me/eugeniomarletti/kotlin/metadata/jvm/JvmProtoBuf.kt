package me.eugeniomarletti.kotlin.metadata.jvm

import me.eugeniomarletti.kotlin.metadata.getExtensionOrNull
import org.jetbrains.kotlin.serialization.ProtoBuf
import org.jetbrains.kotlin.serialization.jvm.JvmProtoBuf

/**
 * @see [getJvmMethodSignature]
 */
val ProtoBuf.Function.jvmMethodSignature get() = getExtensionOrNull(JvmProtoBuf.methodSignature)
val ProtoBuf.Constructor.jvmConstructorSignature get() = getExtensionOrNull(JvmProtoBuf.constructorSignature)
val ProtoBuf.Property.jvmPropertySignature get() = getExtensionOrNull(JvmProtoBuf.propertySignature)
val ProtoBuf.Type.jvmTypeAnnotation get() = getExtensionOrNull(JvmProtoBuf.typeAnnotation) as List<ProtoBuf.Annotation>?
val ProtoBuf.Type.jvmIsRaw get() = getExtensionOrNull(JvmProtoBuf.isRaw)
val ProtoBuf.TypeParameter.jvmTypeParameterAnnotation get() = getExtensionOrNull(JvmProtoBuf.typeParameterAnnotation) as List<ProtoBuf.Annotation>?
val ProtoBuf.Class.jvmClassModuleName get() = getExtensionOrNull(JvmProtoBuf.classModuleName)
val ProtoBuf.Package.jvmPackageModuleName get() = getExtensionOrNull(JvmProtoBuf.packageModuleName)
