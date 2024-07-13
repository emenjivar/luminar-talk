package com.emenjivar.luminar.screen.camera

/**
 * Represent a message in the chat history.
 *
 * @param text Message displayed in the bubble.
 * @param isFromCurrentUser Indicates whether the message was sent by the current user.
 *  True means the message was sent by me, false means the message was received.
 * @param createdAt Date and time of creation.
 */
data class MessageModel(
    val text: String,
    val isFromCurrentUser: Boolean,
    val createdAt: Long = 0L
)
