package com.rino.visualdestortion.utils
object Constants {
    const val BASE_URL1 = "https://13fb-102-42-181-124.ngrok.io/"
    const val BASE_URL = "https://amanat-jeddah-staging.azurewebsites.net/"

    fun convertNumsToArabic(value: String): String {
        return (value.replace("1", "١").replace("2", "٢")
            .replace("3", "٣").replace("4", "٤")
            .replace("5", "٥").replace("6", "٦")
            .replace("7", "٧").replace("8", "٨")
            .replace("9", "٩").replace("0", "٠")
            .replace("AM", "ص").replace("PM", "م"))
    }
    fun convertNumsToEnglish(value: String): String {
        return (value.replace("١","1").replace("٢","2")
            .replace( "٣","3").replace( "٤","4")
            .replace("٥","5").replace("٦", "6")
            .replace( "٧", "7").replace("٨", "8")
            .replace("٩", "9").replace( "٠","0"))
    }
}