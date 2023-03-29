package com.nagpal.shivam.vtucslab.core

sealed class ErrorType {
    object NoActiveInternetConnection : ErrorType()
    object SomeErrorOccurred : ErrorType()
}
