package com.gems.toplan.navigation

sealed class NavigationItems(val route: String) {
    data object DoScreen : NavigationItems("do_screen")
    data object ItemScreen : NavigationItems("item_screen/{id}")
}

