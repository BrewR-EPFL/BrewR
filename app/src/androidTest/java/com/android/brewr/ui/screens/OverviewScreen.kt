package com.android.brewr.ui.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class OverviewScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<OverviewScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("overviewScreen") }) {

  val appTitle: KNode = child { hasTestTag("appTitle") }
  val addButton: KNode = child { hasTestTag("addButton") }
  val accountButton: KNode = child { hasTestTag("accountButton") }
}
