package me.eugeniomarletti.kotlin.metadata.jvm

import me.eugeniomarletti.kotlin.metadata.KotlinMetadataUtils
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.NameResolver
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.TypeTable
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.jvm.deserialization.JvmProtoBufUtil
import javax.lang.model.element.ExecutableElement

/**
 * Returns JVM signature in the format: `equals(Ljava/lang/Object;)Z`.
 *
 * See [ExecutableElement.jvmMethodSignature][KotlinMetadataUtils.jvmMethodSignature] for getting the same from an [ExecutableElement].
 */
fun ProtoBuf.Function.getJvmMethodSignature(nameResolver: NameResolver, typeTable: ProtoBuf.TypeTable = this.typeTable) =
    JvmProtoBufUtil.getJvmMethodSignature(this, nameResolver, TypeTable(typeTable))

fun ProtoBuf.Constructor.getJvmConstructorSignature(nameResolver: NameResolver, typeTable: ProtoBuf.TypeTable) =
    JvmProtoBufUtil.getJvmConstructorSignature(this, nameResolver, TypeTable(typeTable))

fun ProtoBuf.Property.getJvmFieldSignature(nameResolver: NameResolver, typeTable: ProtoBuf.TypeTable) =
    JvmProtoBufUtil.getJvmFieldSignature(this, nameResolver, TypeTable(typeTable))
