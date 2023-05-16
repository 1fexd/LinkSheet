package fe.linksheet.extension

import androidx.compose.ui.Modifier

typealias ChangeModifier = (Modifier) -> Modifier

fun Modifier.runIf(condition: Boolean, run: ChangeModifier) = if (condition) this.let(run) else this

fun Modifier.runIf(condition: Boolean, runIf: ChangeModifier, runElse: ChangeModifier) = if (condition)
    this.let(runIf)
else this.let(runElse)