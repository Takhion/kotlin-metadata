@file:Suppress("unused")

package me.eugeniomarletti.kotlin.metadata

import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf.Class.Kind
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf.MemberKind
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf.Modality
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf.Visibility
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.Flags
import me.eugeniomarletti.kotlin.metadata.shadow.serialization.deserialization.MemberDeserializer

val ProtoBuf.Type.isSuspendType: Boolean get() = Flags.SUSPEND_TYPE[flags]

val ProtoBuf.Class.hasAnnotations: Boolean get() = Flags.HAS_ANNOTATIONS[flags]
val ProtoBuf.Class.visibility: Visibility? get() = Flags.VISIBILITY[flags]
val ProtoBuf.Class.modality: Modality? get() = Flags.MODALITY[flags]
val ProtoBuf.Class.classKind: Kind get() = Flags.CLASS_KIND[flags] ?: ProtoBuf.Class.Kind.CLASS
val ProtoBuf.Class.isInnerClass: Boolean get() = Flags.IS_INNER[flags]
val ProtoBuf.Class.isDataClass: Boolean get() = Flags.IS_DATA[flags]
val ProtoBuf.Class.isExternalClass: Boolean get() = Flags.IS_EXTERNAL_CLASS[flags]
val ProtoBuf.Class.isExpectClass: Boolean get() = Flags.IS_EXPECT_CLASS[flags]

val ProtoBuf.TypeAlias.hasAnnotations: Boolean get() = Flags.HAS_ANNOTATIONS[flags]
val ProtoBuf.TypeAlias.visibility: Visibility? get() = Flags.VISIBILITY[flags]

val ProtoBuf.Constructor.hasAnnotations: Boolean get() = Flags.HAS_ANNOTATIONS[flags]
val ProtoBuf.Constructor.visibility: Visibility? get() = Flags.VISIBILITY[flags]
val ProtoBuf.Constructor.isSecondary: Boolean get() = Flags.IS_SECONDARY[flags]
val ProtoBuf.Constructor.isPrimary: Boolean get() = !isSecondary

val ProtoBuf.ValueParameter.hasAnnotations: Boolean get() = Flags.HAS_ANNOTATIONS[flags]
val ProtoBuf.ValueParameter.declaresDefaultValue: Boolean get() = Flags.DECLARES_DEFAULT_VALUE[flags]
val ProtoBuf.ValueParameter.isCrossInline: Boolean get() = Flags.IS_CROSSINLINE[flags]
val ProtoBuf.ValueParameter.isNoInline: Boolean get() = Flags.IS_NOINLINE[flags]

val ProtoBuf.Function.hasAnnotations: Boolean get() = Flags.HAS_ANNOTATIONS[flagsOrOld]
val ProtoBuf.Function.visibility: Visibility? get() = Flags.VISIBILITY[flagsOrOld]
val ProtoBuf.Function.modality: Modality? get() = Flags.MODALITY[flagsOrOld]
val ProtoBuf.Function.memberKind: MemberKind? get() = Flags.MEMBER_KIND[flagsOrOld]
val ProtoBuf.Function.isOperator: Boolean get() = Flags.IS_OPERATOR[flagsOrOld]
val ProtoBuf.Function.isInfix: Boolean get() = Flags.IS_INFIX[flagsOrOld]
val ProtoBuf.Function.isInline: Boolean get() = Flags.IS_INLINE[flagsOrOld]
val ProtoBuf.Function.isTailRec: Boolean get() = Flags.IS_TAILREC[flagsOrOld]
val ProtoBuf.Function.isExternalFunction: Boolean get() = Flags.IS_EXTERNAL_FUNCTION[flagsOrOld]
val ProtoBuf.Function.isSuspend: Boolean get() = Flags.IS_SUSPEND[flagsOrOld]
val ProtoBuf.Function.isExpectFunction: Boolean get() = Flags.IS_EXPECT_FUNCTION[flagsOrOld]

val ProtoBuf.Property.hasAnnotations: Boolean get() = Flags.HAS_ANNOTATIONS[flagsOrOld]
val ProtoBuf.Property.visibility: Visibility? get() = Flags.VISIBILITY[flagsOrOld]
val ProtoBuf.Property.modality: Modality? get() = Flags.MODALITY[flagsOrOld]
val ProtoBuf.Property.memberKind: MemberKind? get() = Flags.MEMBER_KIND[flagsOrOld]
val ProtoBuf.Property.isVar: Boolean get() = Flags.IS_VAR[flagsOrOld]
val ProtoBuf.Property.isVal: Boolean get() = !isVar
val ProtoBuf.Property.hasGetter: Boolean get() = Flags.HAS_GETTER[flagsOrOld]
val ProtoBuf.Property.hasSetter: Boolean get() = Flags.HAS_SETTER[flagsOrOld]
val ProtoBuf.Property.isConst: Boolean get() = Flags.IS_CONST[flagsOrOld]
val ProtoBuf.Property.isLateInit: Boolean get() = Flags.IS_LATEINIT[flagsOrOld]
val ProtoBuf.Property.hasConstant: Boolean get() = Flags.HAS_CONSTANT[flagsOrOld]
val ProtoBuf.Property.isExternalProperty: Boolean get() = Flags.IS_EXTERNAL_PROPERTY[flagsOrOld]
val ProtoBuf.Property.isDelegated: Boolean get() = Flags.IS_DELEGATED[flagsOrOld]
val ProtoBuf.Property.isExpectProperty: Boolean get() = Flags.IS_EXPECT_PROPERTY[flagsOrOld]

val ProtoBuf.Property.getterHasAnnotations: Boolean get() = Flags.HAS_ANNOTATIONS.get(getterFlags)
val ProtoBuf.Property.getterVisibility: Visibility? get() = Flags.VISIBILITY.get(getterFlags)
val ProtoBuf.Property.getterModality: Modality? get() = Flags.MODALITY.get(getterFlags)
val ProtoBuf.Property.isGetterNotDefault: Boolean get() = hasGetterFlags() && Flags.IS_NOT_DEFAULT.get(getterFlags)
val ProtoBuf.Property.isGetterDefault: Boolean get() = !isGetterNotDefault
val ProtoBuf.Property.isGetterExternal: Boolean get() = hasGetterFlags() && Flags.IS_EXTERNAL_ACCESSOR.get(getterFlags)
val ProtoBuf.Property.isGetterInline: Boolean get() = hasGetterFlags() && Flags.IS_INLINE_ACCESSOR.get(getterFlags)

val ProtoBuf.Property.setterHasAnnotations: Boolean get() = Flags.HAS_ANNOTATIONS.get(setterFlags)
val ProtoBuf.Property.setterVisibility: Visibility? get() = Flags.VISIBILITY.get(setterFlags)
val ProtoBuf.Property.setterModality: Modality? get() = Flags.MODALITY.get(setterFlags)
val ProtoBuf.Property.isSetterNotDefault: Boolean get() = hasSetterFlags() && Flags.IS_NOT_DEFAULT.get(setterFlags)
val ProtoBuf.Property.isSetterDefault: Boolean get() = !isSetterNotDefault
val ProtoBuf.Property.isSetterExternal: Boolean get() = hasSetterFlags() && Flags.IS_EXTERNAL_ACCESSOR.get(setterFlags)
val ProtoBuf.Property.isSetterInline: Boolean get() = hasSetterFlags() && Flags.IS_INLINE_ACCESSOR.get(setterFlags)

@Deprecated("The keyword 'header' was renamed to 'expect'", ReplaceWith("isExpectClass"))
val ProtoBuf.Class.isHeaderClass: Boolean
    get() = isExpectClass

@Deprecated("The keyword 'header' was renamed to 'expect'", ReplaceWith("isExpectFunction"))
val ProtoBuf.Function.isHeaderFunction: Boolean
    get() = isExpectFunction

@Deprecated("The keyword 'header' was renamed to 'expect'", ReplaceWith("isExpectProperty"))
val ProtoBuf.Property.isHeaderProperty: Boolean
    get() = isExpectProperty

private val ProtoBuf.Function.flagsOrOld: Int get() = if (hasFlags()) flags else loadOldFlags(oldFlags)
private val ProtoBuf.Property.flagsOrOld: Int get() = if (hasFlags()) flags else loadOldFlags(oldFlags)

/**
 * @see [MemberDeserializer.loadOldFlags]
 */
private fun loadOldFlags(oldFlags: Int): Int {
    val lowSixBits = oldFlags and 0x3f
    val rest = (oldFlags shr 8) shl 6
    return lowSixBits + rest
}
