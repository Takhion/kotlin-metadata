package me.eugeniomarletti.kotlin.metadata

import me.eugeniomarletti.kotlin.metadata.shadow.load.java.JvmAnnotationNames.BYTECODE_VERSION_FIELD_NAME
import me.eugeniomarletti.kotlin.metadata.shadow.load.java.JvmAnnotationNames.KIND_FIELD_NAME
import me.eugeniomarletti.kotlin.metadata.shadow.load.java.JvmAnnotationNames.METADATA_DATA_FIELD_NAME
import me.eugeniomarletti.kotlin.metadata.shadow.load.java.JvmAnnotationNames.METADATA_EXTRA_INT_FIELD_NAME
import me.eugeniomarletti.kotlin.metadata.shadow.load.java.JvmAnnotationNames.METADATA_EXTRA_STRING_FIELD_NAME
import me.eugeniomarletti.kotlin.metadata.shadow.load.java.JvmAnnotationNames.METADATA_PACKAGE_NAME_FIELD_NAME
import me.eugeniomarletti.kotlin.metadata.shadow.load.java.JvmAnnotationNames.METADATA_STRINGS_FIELD_NAME
import me.eugeniomarletti.kotlin.metadata.shadow.load.java.JvmAnnotationNames.METADATA_VERSION_FIELD_NAME
import me.eugeniomarletti.kotlin.metadata.shadow.load.java.JvmBytecodeBinaryVersion
import me.eugeniomarletti.kotlin.metadata.shadow.load.kotlin.JvmMetadataVersion
import me.eugeniomarletti.kotlin.metadata.shadow.load.kotlin.header.KotlinClassHeader
import me.eugeniomarletti.kotlin.metadata.shadow.load.kotlin.header.KotlinClassHeader.Kind.CLASS
import me.eugeniomarletti.kotlin.metadata.shadow.load.kotlin.header.KotlinClassHeader.Kind.FILE_FACADE
import me.eugeniomarletti.kotlin.metadata.shadow.load.kotlin.header.KotlinClassHeader.Kind.MULTIFILE_CLASS_PART
import me.eugeniomarletti.kotlin.metadata.shadow.load.kotlin.header.ReadKotlinClassHeaderAnnotationVisitor
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

/**
 * @see ReadKotlinClassHeaderAnnotationVisitor
 */
//TODO create a proper visitor
internal val Element.kotlinClassHeader: KotlinClassHeader?
    get() {
        var metadataVersion: JvmMetadataVersion? = null
        var bytecodeVersion: JvmBytecodeBinaryVersion? = null
        var extraString: String? = null
        var extraInt = 0
        var data: Array<String>? = null
        var strings: Array<String>? = null
        var incompatibleData: Array<String>? = null
        var headerKind: KotlinClassHeader.Kind? = null
        var packageName: String? = null

        for (annotation in annotationMirrors) {
            if ((annotation.annotationType.asElement() as TypeElement).qualifiedName.toString() != kotlinMetadataAnnotation) continue

            for ((element, _value) in annotation.elementValues) {
                val name = element.simpleName.toString().takeIf { it.isNotEmpty() } ?: continue
                val value: Any? = unwrapAnnotationValue(_value)
                when {
                    KIND_FIELD_NAME == name && value is Int ->
                        headerKind = KotlinClassHeader.Kind.getById(value)
                    METADATA_VERSION_FIELD_NAME == name ->
                        metadataVersion = JvmMetadataVersion(*@Suppress("UNCHECKED_CAST") (value as List<Int>).toIntArray())
                    BYTECODE_VERSION_FIELD_NAME == name ->
                        bytecodeVersion = JvmBytecodeBinaryVersion(*@Suppress("UNCHECKED_CAST") (value as List<Int>).toIntArray())
                    METADATA_EXTRA_STRING_FIELD_NAME == name && value is String ->
                        extraString = value
                    METADATA_EXTRA_INT_FIELD_NAME == name && value is Int ->
                        extraInt = value
                    METADATA_DATA_FIELD_NAME == name ->
                        data = @Suppress("UNCHECKED_CAST") (value as List<String>).toTypedArray()
                    METADATA_STRINGS_FIELD_NAME == name ->
                        strings = @Suppress("UNCHECKED_CAST") (value as List<String>).toTypedArray()
                    METADATA_PACKAGE_NAME_FIELD_NAME == name && value is String ->
                        packageName = value
                }
            }
        }

        if (headerKind == null) {
            return null
        }

        if (metadataVersion == null || !metadataVersion.isCompatible()) {
            incompatibleData = data
            data = null
        }
        else if ((headerKind == CLASS || headerKind == FILE_FACADE || headerKind == MULTIFILE_CLASS_PART) && data == null) {
            // This means that the annotation is found and its ABI version is compatible, but there's no "data" string array in it.
            // We tell the outside world that there's really no annotation at all
            return null
        }

        return KotlinClassHeader(
            headerKind,
            metadataVersion ?: JvmMetadataVersion.INVALID_VERSION,
            bytecodeVersion ?: JvmBytecodeBinaryVersion.INVALID_VERSION,
            data,
            incompatibleData,
            strings,
            extraString,
            extraInt,
            packageName
        )
    }

private tailrec fun unwrapAnnotationValue(value: Any?): Any? =
    when (value) {
        is AnnotationValue -> unwrapAnnotationValue(value.value)
        is List<*> -> value.map(::unwrapAnnotationValue)
        else -> value
    }
