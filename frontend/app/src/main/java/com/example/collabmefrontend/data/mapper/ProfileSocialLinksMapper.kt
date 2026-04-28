package com.example.collabmefrontend.data.mapper

import com.example.collabmefrontend.domain.model.SocialLinkUiModel
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.collections.mapNotNull

fun List<SocialLinkUiModel>.toJsonObjectOrNull(): JsonObject? {
    val cleaned = mapNotNull { item ->
        val platform = item.platform.trim()
        val url = item.url.trim()

        if (platform.isBlank() && url.isBlank()) {
            null
        } else if (platform.isBlank() || url.isBlank()) {
            // одна часть заполнена, другая нет — ошибка формы
            throw IllegalArgumentException("Fill both social network and URL, or leave both empty")
        } else {
            platform to url
        }
    }

    if (cleaned.isEmpty()) return buildJsonObject { }

    val duplicates = cleaned.groupBy { it.first }.filter { it.value.size > 1 }
    if (duplicates.isNotEmpty()) {
        throw IllegalArgumentException("Social network names must be unique")
    }

    return buildJsonObject {
        cleaned.forEach { (platform, url) ->
            put(platform, JsonPrimitive(url))
        }
    }
}

fun JsonObject.toSocialLinksUiModel(): List<SocialLinkUiModel> {
    if (isEmpty()) return listOf(SocialLinkUiModel())

    return entries.map { entry ->
        SocialLinkUiModel(
            platform = entry.key,
            url = entry.value.jsonPrimitive.content
        )
    }
}