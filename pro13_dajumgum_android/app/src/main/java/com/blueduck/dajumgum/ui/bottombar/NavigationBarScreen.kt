package com.blueduck.dajumgum.ui.bottombar

import com.blueduck.dajumgum.R
import com.blueduck.dajumgum.enums.BottomBarScreen



sealed class NavigationBarScreen(val route: String,  val tabTextResourceId: Int, val iconResourceId: Int) {
    object Home : NavigationBarScreen(BottomBarScreen.Home.route, R.string.home, R.drawable.ic_home)
    object Inspection : NavigationBarScreen(BottomBarScreen.Inspection.route, R.string.inspection, R.drawable.ic_search )
    object Schedule : NavigationBarScreen(BottomBarScreen.Schedule.route, R.string.schedule, R.drawable.ic_schedule )
    object Chat : NavigationBarScreen(BottomBarScreen.Chat.route, R.string.chat, R.drawable.ic_chat )
}