package com.example.domain

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object BangladeshDistricts {
    val divisionsBn: List<String> = listOf(
        "সকল",
        "ঢাকা",
        "চট্টগ্রাম",
        "রাজশাহী",
        "খুলনা",
        "বরিশাল",
        "সিলেট",
        "রংপুর",
        "ময়মনসিংহ",
    )

    val allDistricts: List<District> = listOf(
        // ঢাকা বিভাগ (১৩টি)
        District("dhaka", "ঢাকা", "ঢাকা", 23.8103, 90.4125),
        District("gazipur", "গাজীপুর", "ঢাকা", 24.0023, 90.4264),
        District("narayanganj", "নারায়ণগঞ্জ", "ঢাকা", 23.6238, 90.5000),
        District("narsingdi", "নরসিংদী", "ঢাকা", 23.9229, 90.7177),
        District("munshiganj", "মুন্সীগঞ্জ", "ঢাকা", 23.5422, 90.5305),
        District("manikganj", "মানিকগঞ্জ", "ঢাকা", 23.8617, 90.0003),
        District("tangail", "টাঙ্গাইল", "ঢাকা", 24.2513, 89.9167),
        District("kishoreganj", "কিশোরগঞ্জ", "ঢাকা", 24.4449, 90.7766),
        District("faridpur", "ফরিদপুর", "ঢাকা", 23.6071, 89.8429),
        District("madaripur", "মাদারীপুর", "ঢাকা", 23.1641, 90.1897),
        District("shariatpur", "শরীয়তপুর", "ঢাকা", 23.2423, 90.4348),
        District("gopalganj", "গোপালগঞ্জ", "ঢাকা", 23.0051, 89.8266),
        District("rajbari", "রাজবাড়ী", "ঢাকা", 23.7574, 89.6445),

        // চট্টগ্রাম বিভাগ (১১টি)
        District("chattogram", "চট্টগ্রাম", "চট্টগ্রাম", 22.3569, 91.7832),
        District("coxsbazar", "কক্সবাজার", "চট্টগ্রাম", 21.4272, 92.0058),
        District("cumilla", "কুমিল্লা", "চট্টগ্রাম", 23.4607, 91.1809),
        District("brahmanbaria", "ব্রাহ্মণবাড়িয়া", "চট্টগ্রাম", 23.9608, 91.1115),
        District("chandpur", "চাঁদপুর", "চট্টগ্রাম", 23.2333, 90.6713),
        District("noakhali", "নোয়াখালী", "চট্টগ্রাম", 22.8696, 91.0994),
        District("feni", "ফেনী", "চট্টগ্রাম", 23.0159, 91.3976),
        District("lakshmipur", "লক্ষ্মীপুর", "চট্টগ্রাম", 22.9425, 90.8412),
        District("rangamati", "রাঙ্গামাটি", "চট্টগ্রাম", 22.6533, 92.1753),
        District("khagrachhari", "খাগড়াছড়ি", "চট্টগ্রাম", 23.1193, 91.9847),
        District("bandarban", "বান্দরবান", "চট্টগ্রাম", 22.1953, 92.2184),

        // রাজশাহী বিভাগ (৮টি)
        District("rajshahi", "রাজশাহী", "রাজশাহী", 24.3745, 88.6042),
        District("bogura", "বগুড়া", "রাজশাহী", 24.8465, 89.3773),
        District("pabna", "পাবনা", "রাজশাহী", 24.0063, 89.2372),
        District("sirajganj", "সিরাজগঞ্জ", "রাজশাহী", 24.4534, 89.7007),
        District("natore", "নাটোর", "রাজশাহী", 24.4206, 89.0003),
        District("naogaon", "নওগাঁ", "রাজশাহী", 24.7936, 88.9318),
        District("joypurhat", "জয়পুরহাট", "রাজশাহী", 25.0968, 89.0227),
        District("chapainawabganj", "চাঁপাইনবাবগঞ্জ", "রাজশাহী", 24.5965, 88.2775),

        // খুলনা বিভাগ (১০টি)
        District("khulna", "খুলনা", "খুলনা", 22.8456, 89.5403),
        District("jashore", "যশোর", "খুলনা", 23.1634, 89.2182),
        District("satkhira", "সাতক্ষীরা", "খুলনা", 22.7185, 89.0705),
        District("bagerhat", "বাগেরহাট", "খুলনা", 22.6516, 89.7859),
        District("kushtia", "কুষ্টিয়া", "খুলনা", 23.9013, 89.1205),
        District("chuadanga", "চুয়াডাঙ্গা", "খুলনা", 23.6402, 88.8418),
        District("jhenaidah", "ঝিনাইদহ", "খুলনা", 23.5448, 89.1539),
        District("magura", "মাগুরা", "খুলনা", 23.4873, 89.4199),
        District("meherpur", "মেহেরপুর", "খুলনা", 23.7622, 88.6318),
        District("narail", "নড়াইল", "খুলনা", 23.1725, 89.5127),

        // বরিশাল বিভাগ (৬টি)
        District("barishal", "বরিশাল", "বরিশাল", 22.7010, 90.3535),
        District("patuakhali", "পটুয়াখালী", "বরিশাল", 22.3596, 90.3299),
        District("bhola", "ভোলা", "বরিশাল", 22.6859, 90.6482),
        District("jhalokathi", "ঝালকাঠি", "বরিশাল", 22.6406, 90.1987),
        District("pirojpur", "পিরোজপুর", "বরিশাল", 22.5841, 89.9720),
        District("barguna", "বরগুনা", "বরিশাল", 22.1592, 90.1256),

        // সিলেট বিভাগ (৪টি)
        District("sylhet", "সিলেট", "সিলেট", 24.8949, 91.8687),
        District("moulvibazar", "মৌলভীবাজার", "সিলেট", 24.4829, 91.7774),
        District("habiganj", "হবিগঞ্জ", "সিলেট", 24.3749, 91.4155),
        District("sunamganj", "সুনামগঞ্জ", "সিলেট", 25.0658, 91.3950),

        // রংপুর বিভাগ (৮টি)
        District("rangpur", "রংপুর", "রংপুর", 25.7439, 89.2752),
        District("dinajpur", "দিনাজপুর", "রংপুর", 25.6217, 88.6354),
        District("kurigram", "কুড়িগ্রাম", "রংপুর", 25.8054, 89.6362),
        District("gaibandha", "গাইবান্ধা", "রংপুর", 25.3288, 89.5281),
        District("nilphamari", "নীলফামারী", "রংপুর", 25.9318, 88.8560),
        District("lalmonirhat", "লালমনিরহাট", "রংপুর", 25.9923, 89.2847),
        District("thakurgaon", "ঠাকুরগাঁও", "রংপুর", 26.0337, 88.4617),
        District("panchagarh", "পঞ্চগড়", "রংপুর", 26.3411, 88.5542),

        // ময়মনসিংহ বিভাগ (৪টি)
        District("mymensingh", "ময়মনসিংহ", "ময়মনসিংহ", 24.7471, 90.4203),
        District("jamalpur", "জামালপুর", "ময়মনসিংহ", 24.9375, 89.9378),
        District("netrokona", "নেত্রকোণা", "ময়মনসিংহ", 24.8710, 90.7279),
        District("sherpur", "শেরপুর", "ময়মনসিংহ", 25.0205, 90.0153),
    )

    val defaultDistrict: District = allDistricts.first()

    fun findById(id: String): District = allDistricts.firstOrNull { it.id == id } ?: defaultDistrict

    fun findNearest(latitude: Double, longitude: Double): Pair<District, Double> {
        var best = defaultDistrict
        var minDistKm = Double.MAX_VALUE
        for (d in allDistricts) {
            val km = haversineKm(latitude, longitude, d.latitude, d.longitude)
            if (km < minDistKm) {
                minDistKm = km
                best = d
            }
        }
        return best to minDistKm
    }

    private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
