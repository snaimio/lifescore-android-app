package com.lifescore.app.core.util

import com.lifescore.app.domain.model.DimensionType

data class DetailedArchetype(
    val id: String,
    val name: String,
    val title: String,
    val icon: String,
    val overview: String,
    val superpower: String,
    val growthRecommendation: String,
    val tendencies: List<String>,
    val blindSpots: List<String>,
    val workStyle: List<String>,
    val relationshipStyle: List<String>,
    val primaryDimensions: List<DimensionType>,
    val themeColorHex: Long = 0xFF6366F1
)

object ArchetypeManager {

    val allArchetypes: List<DetailedArchetype> = listOf(
        DetailedArchetype(
            id = "architect",
            name = "The Architect",
            title = "Master of Structural Systems & Order",
            icon = "🏛️",
            overview = "You perceive the world as an interconnected web of systems, workflows, and compounding structures. You excel at turning chaos into elegant operational clarity.",
            superpower = "Scalable Systems Architecture & Structural Foresight",
            growthRecommendation = "Allow space for spontaneous human emergence without over-engineering every variable.",
            tendencies = listOf(
                "Deconstructs problems into foundational first principles",
                "Automates repetitive processes using checklists and tooling",
                "Maintains clean structural organization across digital and physical workspaces",
                "Plans 3 to 5 steps ahead in complex multi-phase roadmaps"
            ),
            blindSpots = listOf(
                "Can become overly rigid when sudden unpredictable pivots occur",
                "May overlook emotional subtleties in favor of pure logic",
                "Prone to analysis paralysis when edge-case data is missing"
            ),
            workStyle = listOf(
                "Flawless execution when operating within clear structural boundaries",
                "Excels at technical specifications, SOPs, and system design",
                "Prefers asynchronous documentation over unproductive meetings"
            ),
            relationshipStyle = listOf(
                "Expresses care through dependable problem solving and structural support",
                "Values intellectual depth and shared principles in close relationships",
                "Needs explicit communication rather than subtle emotional hinting"
            ),
            primaryDimensions = listOf(DimensionType.CAREER, DimensionType.WEALTH, DimensionType.LEARNING),
            themeColorHex = 0xFF6366F1
        ),
        DetailedArchetype(
            id = "sage",
            name = "The Sage",
            title = "Seeker of Foundational Truth & Wisdom",
            icon = "🧙",
            overview = "Driven by relentless intellectual curiosity, you synthesize deep knowledge across domains to distill fundamental truths and elevate collective understanding.",
            superpower = "First-Principles Synthesis & Multi-Domain Synthesis",
            growthRecommendation = "Ground theoretical mastery into daily tangible execution and physical movement.",
            tendencies = listOf(
                "Consumes dense technical, philosophical, and historical literature",
                "Seeks disconfirming evidence to stress-test core beliefs",
                "Connects disparate ideas into holistic philosophical paradigms",
                "Prioritizes intellectual honesty above social consensus"
            ),
            blindSpots = listOf(
                "Risk of living entirely inside mental abstractions (intellectual armchair)",
                "Impatience with superficial small talk and commercial posturing",
                "Procrastination on physical habits and administrative logistics"
            ),
            workStyle = listOf(
                "Deep solo research blocks with zero ambient distractions",
                "Authoring thought leadership, whitepapers, and strategic manifestos",
                "Advisory and mentoring roles guiding foundational direction"
            ),
            relationshipStyle = listOf(
                "Deep, intellectually stimulating one-on-one dialogues",
                "Appreciates partners who challenge their thinking and share curious minds",
                "Offers wise, non-reactive perspective during friends' crises"
            ),
            primaryDimensions = listOf(DimensionType.LEARNING, DimensionType.MENTAL_HEALTH, DimensionType.CAREER),
            themeColorHex = 0xFF3B82F6
        ),
        DetailedArchetype(
            id = "warrior",
            name = "The Warrior",
            title = "Relentless Tactical Pioneer & Executor",
            icon = "⚔️",
            overview = "You lead through physical discipline, grit, and unstoppable forward momentum. When others hesitate, you step forward and deliver results.",
            superpower = "Unwavering Grit, High Somatic Output & Tactical Speed",
            growthRecommendation = "Incorporate restorative down-regulation, stillness, and deep listening.",
            tendencies = listOf(
                "Biased toward immediate physical action over prolonged deliberation",
                "Maintains rigorous exercise, nutrition, and recovery standards",
                "Thrives under intense deadline pressure and high-stakes resistance",
                "Holds self and team to non-negotiable delivery standards"
            ),
            blindSpots = listOf(
                "Can mistake motion for progress or brute-force nuanced problems",
                "Susceptible to physical burnout if recovery is neglected",
                "Can intimidate more sensitive collaborators with blunt urgency"
            ),
            workStyle = listOf(
                "High-intensity sprint sessions with clear measurable milestones",
                "Leading from the front by personal demonstration",
                "Decisive crisis management and operational turnaround"
            ),
            relationshipStyle = listOf(
                "Fiercely loyal and protective of inner circle",
                "Shows love through tangible acts of service and physical presence",
                "Prefers direct candid feedback over sugar-coated diplomacy"
            ),
            primaryDimensions = listOf(DimensionType.FITNESS, DimensionType.HEALTH, DimensionType.CAREER),
            themeColorHex = 0xFFEF4444
        ),
        DetailedArchetype(
            id = "healer",
            name = "The Healer",
            title = "Guardian of Emotional Equilibrium & Empathy",
            icon = "💖",
            overview = "Endowed with high emotional intelligence, you build psychological safety, heal interpersonal friction, and elevate the well-being of those around you.",
            superpower = "Empathetic Resonance & Psychological Safety",
            growthRecommendation = "Establish firm personal boundaries and assert your own commercial interests.",
            tendencies = listOf(
                "Instinctively senses unspoken emotional currents in groups",
                "Active listener who makes others feel unconditionally seen",
                "Proactively checks in on friends during difficult seasons",
                "Champions holistic wellness, mindfulness, and somatic healing"
            ),
            blindSpots = listOf(
                "Absorbing other people's negative emotions and chronic stress",
                "Difficulty saying 'no' leading to personal depletion",
                "Hesitation to engage in necessary commercial or tactical confrontations"
            ),
            workStyle = listOf(
                "People operations, culture building, counseling, and coaching",
                "Facilitating high-trust collaborative team retrospectives",
                "Human-centered design advocating for vulnerable user cohorts"
            ),
            relationshipStyle = listOf(
                "Unconditional emotional presence, warmth, and validation",
                "Builds lifelong intimate relationships based on deep vulnerability",
                "Patient mediator resolving family and team conflicts"
            ),
            primaryDimensions = listOf(DimensionType.HEALTH, DimensionType.MENTAL_HEALTH, DimensionType.RELATIONSHIPS),
            themeColorHex = 0xFFEC4899
        ),
        DetailedArchetype(
            id = "visionary",
            name = "The Visionary",
            title = "Strategic Futurist & Enterprise Catalyst",
            icon = "🚀",
            overview = "You see 10 years into the future, rally exceptional talent, and navigate high-stakes environments to turn audacious dreams into reality.",
            superpower = "Audacious Ambition, Scale Strategy & Magnetic Persuasion",
            growthRecommendation = "Anchor visionary leaps to disciplined daily micro-habit execution.",
            tendencies = listOf(
                "Naturally commands attention and articulates compelling futures",
                "Takes calculated asymmetric risks with massive upside",
                "Attracts world-class collaborators and aligns them toward missions",
                "Reframes setbacks as temporary tactical iterations"
            ),
            blindSpots = listOf(
                "Impatience with granular operational details and compliance",
                "Overcommitting before foundational infrastructure is built",
                "Moving on to new shiny initiatives before completing existing ones"
            ),
            workStyle = listOf(
                "Executive storytelling, fundraising, and strategic partnerships",
                "Launching 0-to-1 initiatives and evangelizing paradigm shifts",
                "Setting bold North Star OKRs and inspiring autonomy"
            ),
            relationshipStyle = listOf(
                "Inspires and elevates partners to achieve their highest potential",
                "Dynamic social life filled with ambitious visionaries and creators",
                "Needs a grounding partner who provides emotional and operational anchor"
            ),
            primaryDimensions = listOf(DimensionType.CAREER, DimensionType.SOCIAL_LIFE, DimensionType.WEALTH),
            themeColorHex = 0xFFF59E0B
        ),
        DetailedArchetype(
            id = "alchemist",
            name = "The Alchemist",
            title = "Creative Transformer & Aesthetic Pioneer",
            icon = "🔮",
            overview = "You transmute raw ideas into captivating art, design, and culture. You live at the bleeding edge of aesthetic and narrative innovation.",
            superpower = "Novel Synthesis, Creative Metaphor & Visual Elegance",
            growthRecommendation = "Develop long-term financial routines and procedural consistency.",
            tendencies = listOf(
                "Refines visual, written, or musical assets to perfection",
                "Challenges orthodoxies with provocative, original thinking",
                "Finds profound creative inspiration across music, nature, and art",
                "Treats daily life as an expressive canvas for self-invention"
            ),
            blindSpots = listOf(
                "Subject to creative mood swings and perfectionist stalling",
                "Frustration when commercial constraints compromise pure aesthetics",
                "Disorganized bookkeeping and administrative follow-through"
            ),
            workStyle = listOf(
                "Immersive creative studio flow states with ambient soundscapes",
                "Brand architecture, UI/UX design, storytelling, and world-building",
                "Iterative design sprints experimenting with cutting-edge tools"
            ),
            relationshipStyle = listOf(
                "Romantic, emotionally expressive, and highly perceptive",
                "Celebrates uniqueness and aesthetic expression in partners",
                "Thrives in environments that encourage creative play and exploration"
            ),
            primaryDimensions = listOf(DimensionType.LEARNING, DimensionType.CAREER, DimensionType.MENTAL_HEALTH),
            themeColorHex = 0xFFA855F7
        ),
        DetailedArchetype(
            id = "strategist",
            name = "The Strategist",
            title = "Calculated Optimizer & Wealth Commander",
            icon = "♟️",
            overview = "You play the long game. You analyze asymmetries, optimize capital allocation, and execute high-leverage moves with calm precision.",
            superpower = "Game Theory, Capital Efficiency & Risk-Adjusted Leverage",
            growthRecommendation = "Cultivate emotional vulnerability and spontaneous, unstructured play.",
            tendencies = listOf(
                "Thinks in probabilistic trees and second-order consequences",
                "Optimizes cash flow, asset allocation, and tax efficiencies",
                "Remains completely emotionally neutral during market volatility",
                "Identifies leverage points where 10% effort yields 90% results"
            ),
            blindSpots = listOf(
                "Can reduce organic human relationships to transactional chess moves",
                "Over-optimizing minor variables at the expense of joyful living",
                "Skepticism that prevents enjoying spontaneous unmeasured moments"
            ),
            workStyle = listOf(
                "Portfolio management, M&A, unit economics modeling, and negotiation",
                "Designing incentive structures that align organizational interests",
                "High-leverage decision sprints using quantitative metrics"
            ),
            relationshipStyle = listOf(
                "Provides exceptional financial security and long-term stability",
                "Shows devotion through calculated long-term planning for loved ones",
                "Values loyalty, discretion, and emotional maturity"
            ),
            primaryDimensions = listOf(DimensionType.WEALTH, DimensionType.CAREER, DimensionType.LEARNING),
            themeColorHex = 0xFF10B981
        ),
        DetailedArchetype(
            id = "guardian",
            name = "The Guardian",
            title = "Protective Anchor & Communal Pillar",
            icon = "🛡️",
            overview = "Reliable, steadfast, and deeply loyal, you protect what matters most and provide an unwavering foundation for your community.",
            superpower = "Communal Loyalty, Moral Integrity & High Reliability",
            growthRecommendation = "Take bold personal risks outside your established safety zone.",
            tendencies = listOf(
                "Upholds promises, traditions, and ethical commitments without fail",
                "Creates stable, organized, and secure environments for others",
                "Stands up courageously for vulnerable team members",
                "Manages risk and contingency planning with extreme diligence"
            ),
            blindSpots = listOf(
                "Resistance to radical changes or disruptive new technologies",
                "Tendency to carry the burdens of the entire group in silence",
                "Over-caution leading to missed asymmetric career opportunities"
            ),
            workStyle = listOf(
                "Risk governance, compliance, operations, and infrastructure reliability",
                "Mentoring junior talent and institutional knowledge preservation",
                "Dependable anchor in multi-year complex operational programs"
            ),
            relationshipStyle = listOf(
                "The bedrock of family and long-term community circles",
                "Steadfast loyalty through good times and severe adversity",
                "Prefers deep, lifelong bonds over superficial network expansion"
            ),
            primaryDimensions = listOf(DimensionType.RELATIONSHIPS, DimensionType.SOCIAL_LIFE, DimensionType.HEALTH),
            themeColorHex = 0xFF0D9488
        ),
        DetailedArchetype(
            id = "catalyst",
            name = "The Catalyst",
            title = "Dynamic Energizer & Movement Builder",
            icon = "⚡",
            overview = "You bring unstoppable energy, ignite enthusiasm, and mobilize groups of people into coordinated action.",
            superpower = "Social Velocity, Viral Communication & Sparking Change",
            growthRecommendation = "Build patience for slow administrative processes and solitary focus.",
            tendencies = listOf(
                "High social energy that revitalizes sluggish environments",
                "Sparks new collaborations and introduces people across networks",
                "Excels at public speaking, live presentations, and community building",
                "Turns mundane tasks into high-energy team challenges"
            ),
            blindSpots = listOf(
                "Difficulty sustaining momentum after the initial excitement wanes",
                "Overcommitting to too many social and professional engagements",
                "Restlessness during quiet periods of solo administrative work"
            ),
            workStyle = listOf(
                "Community evangelism, growth marketing, public relations, and sales",
                "Hosting masterminds, hackathons, and high-energy launch events",
                "Unblocking team morale and driving social accountability"
            ),
            relationshipStyle = listOf(
                "Vibrant, fun-loving, and intensely generous with social energy",
                "Introduces friends to exciting new adventures and communities",
                "Thrives when surrounded by proactive, enthusiastic partners"
            ),
            primaryDimensions = listOf(DimensionType.SOCIAL_LIFE, DimensionType.RELATIONSHIPS, DimensionType.CAREER),
            themeColorHex = 0xFFF97316
        ),
        DetailedArchetype(
            id = "explorer",
            name = "The Explorer",
            title = "Curiosity Pioneer & Boundary Breaker",
            icon = "🧭",
            overview = "Averse to monotony, you thrive on frontiers, uncharted territories, and continuous adaptation across life's 8 dimensions.",
            superpower = "Hyper-Adaptability, Experimental Courage & Agility",
            growthRecommendation = "Commit to one singular compounding craft for deep multi-year mastery.",
            tendencies = listOf(
                "Constantly experiments with new sports, hobbies, and lifestyles",
                "Comfortable traveling into the unknown with minimal baggage",
                "Pioneers emerging tools and unconventional lifestyle design",
                "Maintains broad cross-disciplinary curiosity and adaptability"
            ),
            blindSpots = listOf(
                "Fear of missing out (FOMO) leading to frequent premature pivots",
                "Struggles with repetitive daily maintenance habits",
                "Reluctance to establish deep roots in one physical or career domain"
            ),
            workStyle = listOf(
                "Remote, location-independent exploratory projects",
                "Rapid prototyping, market scouting, and field research",
                "Cross-functional roles requiring diverse skill integration"
            ),
            relationshipStyle = listOf(
                "Exciting, adventurous partner who turns life into a journey",
                "Values personal freedom, autonomy, and non-possessive trust",
                "Connects effortlessly with people from all global cultures"
            ),
            primaryDimensions = listOf(DimensionType.LEARNING, DimensionType.FITNESS, DimensionType.SOCIAL_LIFE),
            themeColorHex = 0xFF14B8A6
        )
    )

    fun mapScoresToArchetype(dimensionScores: Map<DimensionType, Int>): DetailedArchetype {
        val health = dimensionScores[DimensionType.HEALTH] ?: 60
        val wealth = dimensionScores[DimensionType.WEALTH] ?: 60
        val relationships = dimensionScores[DimensionType.RELATIONSHIPS] ?: 60
        val career = dimensionScores[DimensionType.CAREER] ?: 60
        val learning = dimensionScores[DimensionType.LEARNING] ?: 60
        val fitness = dimensionScores[DimensionType.FITNESS] ?: 60
        val mental = dimensionScores[DimensionType.MENTAL_HEALTH] ?: 60
        val social = dimensionScores[DimensionType.SOCIAL_LIFE] ?: 60

        return when {
            career >= 80 && wealth >= 75 -> allArchetypes.find { it.id == "architect" }!!
            learning >= 85 && mental >= 75 -> allArchetypes.find { it.id == "sage" }!!
            fitness >= 85 && health >= 75 -> allArchetypes.find { it.id == "warrior" }!!
            health >= 80 && relationships >= 80 -> allArchetypes.find { it.id == "healer" }!!
            career >= 85 && social >= 75 -> allArchetypes.find { it.id == "visionary" }!!
            learning >= 80 && career >= 75 && mental >= 70 -> allArchetypes.find { it.id == "alchemist" }!!
            wealth >= 85 && career >= 75 -> allArchetypes.find { it.id == "strategist" }!!
            relationships >= 85 && social >= 75 -> allArchetypes.find { it.id == "guardian" }!!
            social >= 85 && career >= 75 -> allArchetypes.find { it.id == "catalyst" }!!
            else -> allArchetypes.find { it.id == "explorer" }!!
        }
    }

    fun getArchetypeById(id: String): DetailedArchetype {
        return allArchetypes.find { it.id.equals(id, ignoreCase = true) } ?: allArchetypes.first()
    }

    fun generateArchetypeShareCaption(archetype: DetailedArchetype, score: Int, level: Int): String {
        return "👑 I am ${archetype.name} on LifeScore! ${archetype.icon}\n⚡ Superpower: ${archetype.superpower}\n🔥 Level $level Achiever • $score LifeScore. Discover your hero archetype: https://lifescore.app/archetype #LifeScore #Archetype #PersonalityProfile #GamifyYourLife"
    }
}
