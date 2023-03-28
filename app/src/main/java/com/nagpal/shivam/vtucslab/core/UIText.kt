package com.nagpal.shivam.vtucslab.core

import androidx.annotation.StringRes

sealed class UIText {
    class StringResource(@field:StringRes val resourceId: Int) : UIText()
}
