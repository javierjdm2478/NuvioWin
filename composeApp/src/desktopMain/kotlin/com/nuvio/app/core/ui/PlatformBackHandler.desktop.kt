package com.nuvio.app.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

internal object DesktopBackDispatcher {
    private val entries = mutableListOf<BackEntry>()

    fun register(entry: BackEntry): () -> Unit = synchronized(entries) {
        entries += entry
        { synchronized(entries) { entries -= entry } }
    }

    fun dispatch(): Boolean {
        val entry = synchronized(entries) {
            entries.asReversed().firstOrNull { it.enabled }
        } ?: return false
        entry.onBack()
        return true
    }
}

internal class BackEntry(
    var enabled: Boolean,
    var onBack: () -> Unit,
)

@Composable
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    val currentOnBack = rememberUpdatedState(onBack)
    val entry = remember {
        BackEntry(enabled = enabled) { currentOnBack.value() }
    }

    SideEffect {
        entry.enabled = enabled
        entry.onBack = { currentOnBack.value() }
    }

    DisposableEffect(Unit) {
        val unregister = DesktopBackDispatcher.register(entry)
        onDispose(unregister)
    }
}
