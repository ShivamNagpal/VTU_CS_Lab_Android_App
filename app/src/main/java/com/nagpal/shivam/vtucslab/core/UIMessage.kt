package com.nagpal.shivam.vtucslab.core

data class UIMessage(val messageType: UIMessageType, val args: List<Any> = emptyList())
