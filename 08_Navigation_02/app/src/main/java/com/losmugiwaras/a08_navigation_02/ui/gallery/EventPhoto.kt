package com.losmugiwaras.a08_navigation_02.ui.gallery

data class EventPhoto(
    val imageResId: Int = 0, // Para imágenes locales, por defecto 0
    val imageUrl: String? = null, // Para imágenes remotas
    val comment: String = "" // Comentario opcional
) {
    companion object {
        val imageResId: Int
            get() {
                TODO()
            }
    }
}

