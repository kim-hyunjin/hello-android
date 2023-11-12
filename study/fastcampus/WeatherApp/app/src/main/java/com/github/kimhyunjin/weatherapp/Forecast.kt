package com.github.kimhyunjin.weatherapp

data class Forecast(
    val date: String,
    val time: String,
    var temperature: Double = 0.0,
    var sky: SkyType? = null,
    var precipitation: Int = 0,
    var precipitationType: PrecipitationType = PrecipitationType.NONE,
)

enum class PrecipitationType(val code: Int) {
    NONE(0),
    RAIN(1),
    RAIN_OR_SNOW(2),
    SNOW(3),
    HEAVY_RAIN(4);

    companion object {
        fun fromCode(code: Int): PrecipitationType {
            return PrecipitationType.values().first { it.code == code }
        }
    }
}

enum class SkyType(val code: Int) {
    SUNNY(1),
    CLOUDY(3),
    RAIN(4);

    companion object {
        fun fromCode(code: Int): SkyType {
            return SkyType.values().first { it.code == code }
        }
    }
}