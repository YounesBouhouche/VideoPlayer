package com.younesbouh.videoplayer.main.domain.models

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoutes {
    @Serializable
    data object Library : NavRoutes()

    @Serializable
    data object Folders : NavRoutes()

    @Serializable
    data class Folder(val id: Int) : NavRoutes()

    @Serializable
    data object Favorites : NavRoutes()

    @Serializable
    data object History : NavRoutes()
}
