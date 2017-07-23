@file:Suppress("unused")

package me.eugeniomarletti.kotlin.metadata

import org.jetbrains.kotlin.load.kotlin.header.KotlinClassHeader
import org.jetbrains.kotlin.load.kotlin.header.KotlinClassHeader.Kind.CLASS
import org.jetbrains.kotlin.load.kotlin.header.KotlinClassHeader.Kind.FILE_FACADE
import org.jetbrains.kotlin.load.kotlin.header.KotlinClassHeader.Kind.MULTIFILE_CLASS
import org.jetbrains.kotlin.load.kotlin.header.KotlinClassHeader.Kind.MULTIFILE_CLASS_PART
import org.jetbrains.kotlin.load.kotlin.header.KotlinClassHeader.Kind.SYNTHETIC_CLASS
import org.jetbrains.kotlin.load.kotlin.header.KotlinClassHeader.Kind.UNKNOWN
import org.jetbrains.kotlin.serialization.jvm.JvmProtoBufUtil
import javax.lang.model.element.Element

val Element.kotlinMetadata get() = kotlinClassHeader?.let { KotlinMetadata.from(it) }

/**
 * Wrapper around [kotlin.Metadata].
 */
sealed class KotlinMetadata(val header: KotlinClassHeader) {
    companion object {
        internal fun from(header: KotlinClassHeader) = when (header.kind) {
            CLASS -> KotlinClassMetadata(header)
            FILE_FACADE -> KotlinFileMetadata(header)
            MULTIFILE_CLASS_PART -> KotlinMultiFileClassPartMetadata(header)
            MULTIFILE_CLASS -> KotlinMultiFileClassFacadeMetadata(header)
            SYNTHETIC_CLASS -> KotlinSyntheticClassMetadata(header)
            UNKNOWN -> KotlinUnknownMetadata(header)
        }
    }

    val metadataVersion get() = header.metadataVersion
    val bytecodeVersion get() = header.bytecodeVersion

    val isPreRelease get() = header.isPreRelease
    val isScript get() = header.isScript
    val isMultiFile get() = multiFileClassKind != null

    val multiFileClassKind get() = header.multifileClassKind

    override fun equals(other: Any?) = other is KotlinMetadata && other.header == header
    override fun hashCode() = header.hashCode()
    override fun toString() = KotlinMetadata::class.java.simpleName + "." + javaClass.simpleName
}

sealed class KotlinPackageMetadata(header: KotlinClassHeader) : KotlinMetadata(header) {
    val data by lazy { JvmProtoBufUtil.readPackageDataFrom(header.data!!, header.strings!!) }
}

class KotlinClassMetadata internal constructor(header: KotlinClassHeader) : KotlinMetadata(header) {
    val data by lazy { JvmProtoBufUtil.readClassDataFrom(header.data!!, header.strings!!) }
}

class KotlinFileMetadata internal constructor(header: KotlinClassHeader) : KotlinPackageMetadata(header)

class KotlinMultiFileClassPartMetadata internal constructor(header: KotlinClassHeader) : KotlinPackageMetadata(header) {
    val facadeClassName get() = header.multifileClassName
}

class KotlinMultiFileClassFacadeMetadata internal constructor(header: KotlinClassHeader) : KotlinMetadata(header) {
    val partsClassNames get() = header.multifilePartNames
}

class KotlinSyntheticClassMetadata internal constructor(header: KotlinClassHeader) : KotlinMetadata(header)

class KotlinUnknownMetadata internal constructor(header: KotlinClassHeader) : KotlinMetadata(header) {
    override fun toString() = super.toString() + "(${header.incompatibleData})"
}
