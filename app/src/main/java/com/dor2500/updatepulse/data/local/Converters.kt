package com.dor2500.updatepulse.data.local

import androidx.room.TypeConverter
import com.dor2500.updatepulse.data.model.ServiceCategory
import com.dor2500.updatepulse.data.model.SourceType

class Converters {
    @TypeConverter
    fun fromCategory(value: ServiceCategory): String = value.name

    @TypeConverter
    fun toCategory(value: String): ServiceCategory =
        runCatching { ServiceCategory.valueOf(value) }.getOrDefault(ServiceCategory.CUSTOM)

    @TypeConverter
    fun fromSourceType(value: SourceType): String = value.name

    @TypeConverter
    fun toSourceType(value: String): SourceType =
        runCatching { SourceType.valueOf(value) }.getOrDefault(SourceType.RSS_ATOM)
}
