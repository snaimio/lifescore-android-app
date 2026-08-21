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
        val intId = value.toIntOrNull()
        if (intId != null) {
            return DimensionType.fromId(intId)
        }
        return try {
            DimensionType.valueOf(value)
        } catch (e: Exception) {
            DimensionType.HEALTH
        }
    }
    @TypeConverter
    fun fromAddictionType(type: com.lifescore.app.data.local.entity.AddictionType): String = type.name

    @TypeConverter
    fun toAddictionType(value: String): com.lifescore.app.data.local.entity.AddictionType = try {
        com.lifescore.app.data.local.entity.AddictionType.valueOf(value)
    } catch (e: Exception) {
        com.lifescore.app.data.local.entity.AddictionType.OTHER
    }

    @TypeConverter
    fun fromCravingIntensity(intensity: com.lifescore.app.data.local.entity.CravingIntensity): String = intensity.name

    @TypeConverter
    fun toCravingIntensity(value: String): com.lifescore.app.data.local.entity.CravingIntensity = try {
        com.lifescore.app.data.local.entity.CravingIntensity.valueOf(value)
    } catch (e: Exception) {
        com.lifescore.app.data.local.entity.CravingIntensity.MODERATE
    }

    @TypeConverter
    fun fromRelapseType(type: com.lifescore.app.data.local.entity.RelapseType): String = type.name

    @TypeConverter
    fun toRelapseType(value: String): com.lifescore.app.data.local.entity.RelapseType = try {
        com.lifescore.app.data.local.entity.RelapseType.valueOf(value)
    } catch (e: Exception) {
        com.lifescore.app.data.local.entity.RelapseType.SLIP
    }
}
