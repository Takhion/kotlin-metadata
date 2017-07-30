package me.eugeniomarletti.kotlin.metadata

import org.jetbrains.kotlin.protobuf.GeneratedMessageLite.ExtendableMessage
import org.jetbrains.kotlin.protobuf.GeneratedMessageLite.GeneratedExtension
import org.jetbrains.kotlin.serialization.ProtoBuf
import org.jetbrains.kotlin.serialization.jvm.JvmProtoBuf

fun <MessageType : ExtendableMessage<MessageType>, Type>
    MessageType.getExtensionOrNull(extension: GeneratedExtension<MessageType, Type>)
    = takeIf { hasExtension(extension) }?.let { getExtension(extension) }

val ProtoBuf.Constructor.jvmConstructorSignature get() = getExtensionOrNull(JvmProtoBuf.constructorSignature)
val ProtoBuf.Function.jvmMethodSignature get() = getExtensionOrNull(JvmProtoBuf.methodSignature)
val ProtoBuf.Property.jvmPropertySignature get() = getExtensionOrNull(JvmProtoBuf.propertySignature)
val ProtoBuf.Type.jvmTypeAnnotation get() = getExtensionOrNull(JvmProtoBuf.typeAnnotation) as List<ProtoBuf.Annotation>?
val ProtoBuf.Type.jvmIsRaw get() = getExtensionOrNull(JvmProtoBuf.isRaw)
val ProtoBuf.TypeParameter.jvmTypeParameterAnnotation get() = getExtensionOrNull(JvmProtoBuf.typeParameterAnnotation) as List<ProtoBuf.Annotation>?
val ProtoBuf.Class.jvmClassModuleName get() = getExtensionOrNull(JvmProtoBuf.classModuleName)
val ProtoBuf.Package.jvmPackageModuleName get() = getExtensionOrNull(JvmProtoBuf.packageModuleName)
