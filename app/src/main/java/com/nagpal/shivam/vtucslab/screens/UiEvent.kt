package com.nagpal.shivam.vtucslab.screens

sealed class UiEvent {
    class LoadContent(val url: String) : UiEvent()
    class RefreshContent(val url: String) : UiEvent()
}
