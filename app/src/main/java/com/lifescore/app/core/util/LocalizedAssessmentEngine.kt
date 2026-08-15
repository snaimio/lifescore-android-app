package com.lifescore.app.core.util

import com.lifescore.app.domain.model.HeroArchetype

object LocalizedAssessmentEngine {

    fun getLikertLabels(language: AppLanguage = LanguageManager.getCurrentLanguage()): List<String> {
        return when (language) {
            AppLanguage.ENGLISH -> listOf(
                "Strongly Disagree",
                "Disagree",
                "Neutral",
                "Agree",
                "Strongly Agree"
            )
            AppLanguage.SPANISH -> listOf(
                "Totalmente en desacuerdo",
                "En desacuerdo",
                "Neutral",
                "De acuerdo",
                "Totalmente de acuerdo"
            )
            AppLanguage.CHINESE -> listOf(
                "强烈反对",
                "不同意",
                "中立",
                "同意",
                "非常赞同"
            )
            AppLanguage.ARABIC -> listOf(
                "أعارض بشدة",
                "أعارض",
                "محايد",
                "أوافق",
                "أوافق بشدة"
            )
            AppLanguage.HINDI -> listOf(
                "पूरी तरह असहमत",
                "असहमत",
                "तटस्थ",
                "सहमत",
                "पूरी तरह सहमत"
            )
        }
    }

    fun getLocalizedHeroArchetype(
        archetype: HeroArchetype,
        language: AppLanguage = LanguageManager.getCurrentLanguage()
    ): Pair<String, String> {
        return when (language) {
            AppLanguage.SPANISH -> when (archetype) {
                HeroArchetype.WARRIOR -> Pair("El Guerrero", "Fuerza implacable y resistencia, motivado por la victoria y el dominio físico.")
                HeroArchetype.SAGE -> Pair("El Sabio", "Buscador profundo de la verdad, impulsado por el intelecto y el conocimiento.")
                HeroArchetype.EXPLORER -> Pair("El Explorador", "Prospera en la salud, la aventura al aire libre y la vitalidad.")
                HeroArchetype.CREATOR -> Pair("El Creador", "Enfocado en avances profesionales, riqueza y maestría.")
                HeroArchetype.HEALER -> Pair("El Sanador", "Guardián de la empatía, restaurador del bienestar y la armonía.")
            }
            AppLanguage.CHINESE -> when (archetype) {
                HeroArchetype.WARRIOR -> Pair("勇士", "无畏的行动派与征服者，以坚韧体魄和钢铁意志破局。")
                HeroArchetype.SAGE -> Pair("智者", "真理的探索者，以无尽的好奇心与深度思考驱动人生。")
                HeroArchetype.EXPLORER -> Pair("探索者", "拥抱自然与户外探索，以充沛生命力和健康体魄前行。")
                HeroArchetype.CREATOR -> Pair("创造者", "聚焦事业突破、财富自由与卓越创造力。")
                HeroArchetype.HEALER -> Pair("治愈者", "同理心与身心能量的守护者，致力于修复与平衡。")
            }
            AppLanguage.ARABIC -> when (archetype) {
                HeroArchetype.WARRIOR -> Pair("المحارب", "طاقة صلبة وشجاعة لا تلين لتحقيق الأهداف وتجاوز العقبات.")
                HeroArchetype.SAGE -> Pair("الحكيم", "باحث شغوف عن المعرفة والحقيقة والتحليل العميق.")
                HeroArchetype.EXPLORER -> Pair("المستكشف", "يعشق المغامرة والحيوية والنشاط البدني في الهواء الطلق.")
                HeroArchetype.CREATOR -> Pair("المبتكر", "يركز على النجاح المهني وصناعة الثروة والتميز.")
                HeroArchetype.HEALER -> Pair("المعالج", "حارس السلام الداخلي والتعافي والتوازن الإنساني.")
            }
            AppLanguage.HINDI -> when (archetype) {
                HeroArchetype.WARRIOR -> Pair("योद्धा (द वॉरियर)", "अदम्य साहस और अटूट अनुशासन से हर चुनौती को जीतने वाला।")
                HeroArchetype.SAGE -> Pair("ज्ञानी (द सेज)", "सत्य और आत्म-ज्ञान का गहरा खोजी, बुद्धिमान विचारक।")
                HeroArchetype.EXPLORER -> Pair("अन्वेषक (द एक्सप्लोरर)", "प्रकृति, स्वास्थ्य और नई खोजों का उत्साही पथिक।")
                HeroArchetype.CREATOR -> Pair("सर्जक (द क्रिएटर)", "करियर में नई ऊंचाइयां, संपत्ति और महारत हासिल करने वाला।")
                HeroArchetype.HEALER -> Pair("उपचारक (द हीलर)", "सहानुभूति और जीवन में संतुलन लाने वाला रक्षक।")
            }
            AppLanguage.ENGLISH -> Pair(archetype.displayName, archetype.description)
        }
    }

    fun getLocalizedPsychometricArchetype(
        archetypeId: String,
        fallbackName: String,
        fallbackTitle: String,
        fallbackDesc: String,
        language: AppLanguage = LanguageManager.getCurrentLanguage()
    ): Triple<String, String, String> {
        if (language == AppLanguage.ENGLISH) return Triple(fallbackName, fallbackTitle, fallbackDesc)

        return when (language) {
            AppLanguage.SPANISH -> when (archetypeId) {
                "architect" -> Triple("El Arquitecto", "Estratega Maestro", "Diseñador de sistemas de vida escalables y disciplina implacable.")
                "sage" -> Triple("El Sabio", "Buscador de Sabiduría", "Pensador reflexivo guiado por el conocimiento y la verdad.")
                "warrior" -> Triple("El Guerrero", "Fuerza Indomable", "Campeón de la resistencia física y el coraje inquebrantable.")
                "healer" -> Triple("El Sanador", "Faro de Armonía", "Guardián de la empatía, el bienestar mental y la conexión humana.")
                "visionary" -> Triple("El Visionario", "Pionero Audaz", "Innovador que reinventa el futuro con pasión creativa.")
                "alchemist" -> Triple("El Alquimista", "Multiplicador de Valor", "Maestro en transformar pequeños recursos en imperios.")
                "mystic" -> Triple("El Místico", "Maestro Interior", "Buscador de la serenidad espiritual y el dominio mental.")
                "diplomat" -> Triple("El Diplomático", "Conector Magnético", "Líder empático y maestro de la inteligencia social.")
                "sovereign" -> Triple("El Soberano", "Líder Supremo", "Comandante natural con visión integradora y autoridad serena.")
                else -> Triple("El Excepcional", "Leyenda 0.01%", "Rompedor de paradigmas que redefine el potencial humano.")
            }
            AppLanguage.CHINESE -> when (archetypeId) {
                "architect" -> Triple("建筑大师", "系统战略家", "专注于构建长期系统与精工细作的人生规划师。")
                "sage" -> Triple("哲人智者", "真理探索者", "以无尽的好奇心与深度认知洞察世界。")
                "warrior" -> Triple("无畏勇士", "破局先锋", "以坚韧体魄和钢铁纪律征服一切困难。")
                "healer" -> Triple("心灵疗愈者", "身心守护者", "关注内在平衡、同理心与和谐人际关系。")
                "visionary" -> Triple("远见先锋", "未来构想家", "打破常规的颠覆者，以宏大视野引领创新。")
                "alchemist" -> Triple("炼金宗师", "价值转化者", "擅长资源聚合与财富呈指数级放大。")
                "mystic" -> Triple("静心求道者", "精神行者", "专注于心性沉淀、情绪自控与内在觉醒。")
                "diplomat" -> Triple("外交领袖", "人脉聚合者", "深谙人性与合作艺术的社交领袖。")
                "sovereign" -> Triple("统帅之王", "全域领袖", "统领全局、以宏大胸怀引领未来的天生领袖。")
                else -> Triple("绝顶传奇", "0.01% 卓越者", "突破常规极限，重新定义人类卓越潜能。")
            }
            AppLanguage.ARABIC -> when (archetypeId) {
                "architect" -> Triple("المهندس المعماري", "مخطط استراتيجي", "يبني أنظمة محكمة وانضباطاً صارماً لحياة متكاملة.")
                "sage" -> Triple("الحكيم", "طالب المعرفة", "مفكر عميق يبحث عن الحقيقة ويسعى للتعلم المستمر.")
                "warrior" -> Triple("المحارب", "طاقة صلبة", "يتفوق في الإرادة والقوة والتحمل البدني العالي.")
                "healer" -> Triple("المعالج", "منارة السلام", "يرعى الصحة النفسية والعلاقات والتعاطف الإنساني.")
                "visionary" -> Triple("صاحب الرؤية", "رائد التغيير", "يعيد ابتكار المستقبل برؤية شجاعة وإبداع لا ينضب.")
                "alchemist" -> Triple("الخيميائي", "صانع الثروة", "يحول الفرص البسيطة إلى نجاحات كبرى.")
                "mystic" -> Triple("العارف", "سيد السكينة", "يبحث عن الوعي العميق والراحة النفسية والصفاء الذهني.")
                "diplomat" -> Triple("الدبلوماسي", "قائد العلاقات", "خبير الذكاء العاطفي وبناء الجسور بين الناس.")
                "sovereign" -> Triple("الحاكم", "قائد ملهم", "يجمع بين السلطة الطبيعية والرؤية المتوازنة.")
                else -> Triple("الأسطورة", "النخبة الفريدة", "يعيد كتابة قواعد الإنجاز البشري المتميز.")
            }
            AppLanguage.HINDI -> when (archetypeId) {
                "architect" -> Triple("वास्तुकार (द आर्किटेक्ट)", "मास्टर रणनीतिकार", "जीवन के सटीक नियम और अटूट अनुशासन गढ़ने वाला।")
                "sage" -> Triple("ज्ञानी (द सेज)", "ज्ञान का खोजी", "गहन चिंतन और सत्य का आराधक।")
                "warrior" -> Triple("योद्धा (द वॉरियर)", "अदम्य साहसी", "शारीरिक शक्ति और दृढ़ इच्छाशक्ति का प्रतीक।")
                "healer" -> Triple("उपचारक (द हीलर)", "शांति का दूत", "मानसिक सुकून और रिश्तों में मिठास घोलने वाला।")
                "visionary" -> Triple("दूरदर्शी (द विजनरी)", "नवाचार का अग्रदूत", "भविष्य की नई संभावनाओं को जन्म देने वाला।")
                "alchemist" -> Triple("कीमियागर (द अल्केमिस्ट)", "सफलता का निर्माता", "साधारण संसाधनों को महान सफलता में बदलने वाला।")
                "mystic" -> Triple("योगी (द मिस्टिक)", "आत्म-साधक", "मानसिक शांति, ध्यान और आंतरिक संतुलन का साधक।")
                "diplomat" -> Triple("राजनयिक (द डिप्लोमैट)", "संबंधों का महारथी", "सामाजिक समझ और सहयोग की शक्ति।")
                "sovereign" -> Triple("सम्राट (द सॉवरेन)", "प्राकृतिक नेता", "दृढ़ संकल्प और संतुलित जीवन का प्रतीक।")
                else -> Triple("असाधारण दिग्गज (द आउटलायर)", "सर्वोच्च 0.01%", "सफलता की नई परिभाषा गढ़ने वाला महानायक।")
            }
            AppLanguage.ENGLISH -> Triple(fallbackName, fallbackTitle, fallbackDesc)
        }
    }

    fun getLocalizedQuestion(
        id: Int,
        englishText: String,
        language: AppLanguage = LanguageManager.getCurrentLanguage()
    ): String {
        if (language == AppLanguage.ENGLISH) return englishText

        return when (language) {
            AppLanguage.SPANISH -> "[$id] ¿En qué medida te identificas con: '$englishText'?"
            AppLanguage.CHINESE -> "[$id] 请评估你对以下陈述的赞同程度：“$englishText”"
            AppLanguage.ARABIC -> "[$id] إلى أي مدى توافق على: \"$englishText\"؟"
            AppLanguage.HINDI -> "[$id] आप इस कथन से कितना सहमत हैं: \"$englishText\"?"
            AppLanguage.ENGLISH -> englishText
        }
    }
}
