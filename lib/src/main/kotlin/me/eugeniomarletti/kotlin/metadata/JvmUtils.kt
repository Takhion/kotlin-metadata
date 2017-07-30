package me.eugeniomarletti.kotlin.metadata

import org.jetbrains.kotlin.serialization.ProtoBuf
import org.jetbrains.kotlin.serialization.deserialization.NameResolver
import org.jetbrains.kotlin.serialization.deserialization.TypeTable
import org.jetbrains.kotlin.serialization.jvm.JvmProtoBufUtil

/**
 * Returns JVM signature in the format: "equals(Ljava/lang/Object;)Z"
 */
fun ProtoBuf.Function.getJvmMethodSignature(nameResolver: NameResolver, typeTable: ProtoBuf.TypeTable = this.typeTable) =
    JvmProtoBufUtil.getJvmMethodSignature(this, nameResolver, TypeTable(typeTable))

fun ProtoBuf.Constructor.getJvmConstructorSignature(nameResolver: NameResolver, typeTable: ProtoBuf.TypeTable) =
    JvmProtoBufUtil.getJvmConstructorSignature(this, nameResolver, TypeTable(typeTable))

fun ProtoBuf.Property.getJvmFieldSignature(nameResolver: NameResolver, typeTable: ProtoBuf.TypeTable) =
    JvmProtoBufUtil.getJvmFieldSignature(this, nameResolver, TypeTable(typeTable))
