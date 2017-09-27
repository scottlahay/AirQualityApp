package com.scott.airquality

data class AirQuality(var locationName: String, var AQHI: String, var riskLevel: String, var moreInfo: String, var generalMessage: String, var atRiskMessage: String, var calculatedAt: String) {
    constructor() : this("LocationName", "AQHI", "riskLevel", "moreInfo", "general", "atrisk", "calculatedAt")

    // this is for the spinner adapter
    override fun toString(): String {
        return locationName
    }

    companion object {
        val dbKey = "locations"
        val modRisk = HealthMessage.moderateRisk()
        val highRisk = HealthMessage.highRisk()
        fun centralOk() = AirQuality("Central Okanagan", "5", modRisk.risk, "https://weather.gc.ca/airquality/pages/trends/bcaq-008_e.html",
                                     modRisk.generalMessage, modRisk.atRiskMessage,
                                     "7:00pm Sept 12")

        fun kamloops() = AirQuality("Kamloops", "7", highRisk.risk, "https://weather.gc.ca/airquality/pages/trends/bcaq-008_e.html",
                                    highRisk.generalMessage, highRisk.atRiskMessage,
                                    "7:00pm Sept 12")
    }

}

data class HealthMessage(var risk: String, var index: String, var atRiskMessage: String, var generalMessage: String) {
    constructor() : this("risk", "index", "atRiskMessage", "generalMessage")

    companion object {
        val dbKey = "health_message"
        fun lowRisk() = HealthMessage("Low Risk", "3", "Enjoy your usual outdoor activities.", "Ideal air quality for outdoor activities.")
        fun moderateRisk() = HealthMessage("Moderate Risk", "6", "Consider reducing or rescheduling strenuous activities outdoors if you are experiencing symptoms.", "No need to modify your usual outdoor activities unless you experience symptoms such as coughing and throat irritation.")
        fun highRisk() = HealthMessage("High Risk", "10", "Reduce or reschedule strenuous activities outdoors. Children and the elderly should also take it easy.", "Consider reducing or rescheduling strenuous activities outdoors if you experience symptoms such as coughing and throat irritation.")
        fun veryHighRisk() = HealthMessage("Very High Risk", "", "Avoid strenuous activities outdoors. Children and the elderly should also avoid outdoor physical exertion.", "Reduce or reschedule strenuous activities outdoors, especially if you experience symptoms such as coughing and throat irritation.")
    }

}
