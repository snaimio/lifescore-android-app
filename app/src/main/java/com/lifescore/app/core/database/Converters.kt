package com.lifescore.app.core.database

import androidx.room.TypeConverter
import com.lifescore.app.domain.model.DimensionType

class Converters {
    @TypeConverter
    fun fromDimensionType(dimension: DimensionType): String {
        return dimension.name
    }

    @TypeConverter
    fun toDimensionType(value: String): DimensionType {
        return try {
            DimensionType.valueOf(value)
        } catch (e: Exception) {
            DimensionType.HEALTH
        }
    }
}
