package me.eugeniomarletti.kotlin.metadata

import org.jetbrains.kotlin.protobuf.GeneratedMessageLite.ExtendableMessage
import org.jetbrains.kotlin.protobuf.GeneratedMessageLite.GeneratedExtension

fun <MessageType : ExtendableMessage<MessageType>, Type>
    MessageType.getExtensionOrNull(extension: GeneratedExtension<MessageType, Type>): Type?
    = takeIf { hasExtension(extension) }?.getExtension(extension)
