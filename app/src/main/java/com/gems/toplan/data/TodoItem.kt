package com.gems.toplan.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodoItem(
    @SerialName("status") val status: String,
    @SerialName("list") val list: List<Task>,
    @SerialName("revision") val revision: Int
) {
    @Serializable
    data class Task(
        @SerialName("id")
        val id: String = "",
        @SerialName("text")
        val text: String = "",
        @SerialName("importance")
        val importance: Int = 0,
        @SerialName("deadline")
        val deadline: String? = null,
        @SerialName("done")
        var done: Boolean = false,
        @SerialName("color")
        val color: String? = null,
        @SerialName("created_at")
        val createdAt: Long = 0L,
        @SerialName("changed_at")
        val changedAt: Long = 0L,
        @SerialName("last_updated_by")
        val lastUpdatedBy: String = ""
    )

}

