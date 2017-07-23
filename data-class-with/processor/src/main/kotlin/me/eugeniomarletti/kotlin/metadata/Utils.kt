package me.eugeniomarletti.kotlin.metadata

import org.jetbrains.kotlin.serialization.ClassData
import org.jetbrains.kotlin.serialization.ProtoBuf

internal fun ProtoBuf.Type.extractFullName(classData: ClassData): String {
    val (nameResolver, classProto) = classData

    val name = when {
        hasTypeParameter() -> classProto.getTypeParameter(typeParameter).name
        hasTypeParameterName() -> typeParameterName
        hasAbbreviatedType() -> abbreviatedType.typeAliasName
        else -> className
    }.let { nameResolver.getString(it).replace('/', '.') }

    val argumentList = if (hasAbbreviatedType()) abbreviatedType.argumentList else argumentList
    val arguments = argumentList
        .takeIf { it.isNotEmpty() }
        ?.joinToString(prefix = "<", postfix = ">") { if (!it.hasType()) "*" else it.type.extractFullName(classData) }
        ?: ""

    val nullability = if (nullable) "?" else ""

    return name + arguments + nullability
}
