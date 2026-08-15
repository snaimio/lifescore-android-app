package com.lifescore.app.core.util

enum class PsychometricDimension(val id: String, val displayName: String, val description: String, val baseColorHex: Long) {
    INTELLECTUAL("intellectual", "Intellect & Analytics", "Logical deduction, abstract reasoning, and continuous learning", 0xFF6366F1),
    EXECUTION("execution", "Tactical Execution & Drive", "Physical mastery, endurance, and relentless follow-through", 0xFFEF4444),
    CREATIVE("creative", "Innovation & Synthesis", "Originality, design intuition, and creative problem solving", 0xFFA855F7),
    EMPATHY("empathy", "Empathy & Social Resonance", "Emotional intelligence, deep listening, and relationship depth", 0xFFEC4899),
    STRATEGY("strategy", "Enterprise & Strategic Vision", "Negotiation, leadership, influence, and scale", 0xFFF59E0B),
    SYSTEMS_ORDER("systems_order", "Systems & Wealth Precision", "Structural order, detail rigor, and financial optimization", 0xFF10B981)
}

enum class RiasecType(val code: String, val fullName: String) {
    REALISTIC("R", "Realistic / Physical"),
    INVESTIGATIVE("I", "Investigative / Analytical"),
    ARTISTIC("A", "Artistic / Creative"),
    SOCIAL("S", "Social / Empathic"),
    ENTERPRISING("E", "Enterprising / Leadership"),
    CONVENTIONAL("C", "Conventional / Systematic")
}

data class AssessmentQuestion(
    val id: Int,
    val dimension: PsychometricDimension,
    val riasecType: RiasecType,
    val prompt: String
)

data class PsychometricArchetype(
    val id: String,
    val name: String,
    val title: String,
    val icon: String,
    val description: String,
    val superpower: String,
    val growthArea: String,
    val primaryRiasec: String
)

data class CareerMatch(
    val title: String,
    val riasecCode: String,
    val matchPercentage: Int,
    val salaryRange: String,
    val description: String,
    val topSkills: List<String>
)

data class AssessmentResult(
    val dimensionScores: Map<PsychometricDimension, Int>, // 0-200 range
    val overallScore: Int,                               // 0-1200 range
    val archetype: PsychometricArchetype,
    val topRiasecCode: String,                           // e.g. "IRS"
    val topCareers: List<CareerMatch>
)

object PsychometricAssessmentEngine {

    // 130-Question Bank across 6 dimensions (20+ questions each)
    val questions: List<AssessmentQuestion> by lazy {
        val list = mutableListOf<AssessmentQuestion>()
        var qId = 1

        // 1. INTELLECTUAL & ANALYTICS (22 questions)
        val intellectualPrompts = listOf(
            "I enjoy breaking down complex problems into fundamental first principles.",
            "I regularly read dense technical, scientific, or philosophical literature.",
            "I analyze the underlying assumptions before accepting popular opinions.",
            "I am drawn to abstract mathematical or architectural mental models.",
            "I prefer spending an evening researching a new topic over passive entertainment.",
            "I seek out disconfirming evidence to test the validity of my beliefs.",
            "I enjoy debugging intricate systems to locate root-cause bottlenecks.",
            "I can sustain deep cognitive focus on difficult puzzles for hours.",
            "I prefer data-driven empirical decisions over emotional intuition.",
            "I enjoy constructing theoretical frameworks to explain human behavior.",
            "I am fascinated by emerging technological paradigms like AI and quantum computing.",
            "I quickly identify logical fallacies in debates and presentations.",
            "I take thorough structured notes when synthesizing new knowledge.",
            "I find satisfaction in optimizing algorithms or analytical models.",
            "I am comfortable working with high degrees of cognitive ambiguity.",
            "I frequently ask 'why' until I reach foundational mechanics.",
            "I prefer precision in language and definitions over vague generalities.",
            "I value intellectual honesty above social validation.",
            "I enjoy creating mental taxonomies and classifications.",
            "I actively study historical precedents to predict future trends.",
            "I dissect systemic failures to design preventative guardrails.",
            "I view learning as a lifelong compounding competitive advantage."
        )
        intellectualPrompts.forEach { prompt ->
            list.add(AssessmentQuestion(qId++, PsychometricDimension.INTELLECTUAL, RiasecType.INVESTIGATIVE, prompt))
        }

        // 2. TACTICAL EXECUTION & DRIVE (22 questions)
        val executionPrompts = listOf(
            "I take immediate decisive physical action rather than overthinking.",
            "I maintain a demanding physical training routine regardless of motivation.",
            "I thrive under intense deadline pressure and high-stakes execution.",
            "I prefer building concrete tangible prototypes over endless discussions.",
            "I possess high somatic energy and dislike sitting idle for long periods.",
            "I hold myself accountable to rigorous daily output metrics.",
            "I finish every task I start, even when novelty wears off.",
            "I am resilient in the face of acute physical discomfort or fatigue.",
            "I enjoy mastering tactile tools, machinery, or complex physical skills.",
            "I maintain extreme discipline with my sleep, diet, and recovery.",
            "I push through pain barriers to achieve personal best records.",
            "I convert strategic concepts into tactical daily checklists.",
            "I have a bias for rapid iteration and shipping work publicly.",
            "I stay composed and execute methodically during sudden emergencies.",
            "I track my physiological biomarkers and energy levels daily.",
            "I am energized by physical challenges and outdoor exploration.",
            "I eliminate friction in my physical workspace to maximize speed.",
            "I refuse to make excuses when deliverables miss the standard.",
            "I treat my physical body as a high-performance instrument.",
            "I lead by personal demonstration and relentless work ethic.",
            "I prefer hands-on craftsmanship over abstract delegation.",
            "I pride myself on punctuality, grit, and physical endurance."
        )
        executionPrompts.forEach { prompt ->
            list.add(AssessmentQuestion(qId++, PsychometricDimension.EXECUTION, RiasecType.REALISTIC, prompt))
        }

        // 3. INNOVATION & CREATIVE SYNTHESIS (22 questions)
        val creativePrompts = listOf(
            "I constantly generate novel connections between unrelated disciplines.",
            "I have a distinct aesthetic sensibility in visual and written work.",
            "I am comfortable challenging industry orthodoxies and dogma.",
            "I express complex emotions and ideas through creative mediums.",
            "I enjoy designing user experiences that feel magical and seamless.",
            "I prefer open-ended creative briefs over rigid step-by-step instructions.",
            "I experiment with unconventional solutions when standard methods fail.",
            "I find inspiration in art, music, architecture, and nature.",
            "I daydream productively about future possibilities and alternative realities.",
            "I value authentic personal style over generic conformity.",
            "I enjoy writing, storytelling, and crafting memorable metaphors.",
            "I iterate rapidly on visual typography, layouts, and colors.",
            "I am willing to look foolish in pursuit of a breakthrough innovation.",
            "I see latent patterns where others only see random noise.",
            "I enjoy remixing existing ideas into fresh compelling formats.",
            "I trust my intuitive leaps when evaluating creative concepts.",
            "I believe elegance and simplicity are hallmarks of great design.",
            "I actively seek out avant-garde and cross-cultural perspectives.",
            "I create provocative narratives that inspire and provoke thought.",
            "I find deep joy in bringing an original vision from 0 to 1.",
            "I refine creative assets until every detail feels harmonious.",
            "I treat life as a canvas for continuous creative reinvention."
        )
        creativePrompts.forEach { prompt ->
            list.add(AssessmentQuestion(qId++, PsychometricDimension.CREATIVE, RiasecType.ARTISTIC, prompt))
        }

        // 4. EMPATHY & SOCIAL RESONANCE (22 questions)
        val empathyPrompts = listOf(
            "I accurately sense the emotional undercurrents in a room.",
            "People frequently confide in me because they feel deeply understood.",
            "I prioritize psychological safety and inclusion in team settings.",
            "I am energized by helping others overcome personal and professional hurdles.",
            "I listen actively without interrupting or formulating my reply prematurely.",
            "I tailor my communication style to match the emotional state of others.",
            "I resolve interpersonal conflicts with patience and diplomatic tact.",
            "I invest time in nurturing long-term meaningful friendships.",
            "I notice subtle body language and micro-expressions in conversations.",
            "I am genuinely curious about people's life stories and motivations.",
            "I forgive mistakes and support people through their vulnerabilities.",
            "I celebrate other people's achievements with genuine enthusiasm.",
            "I can de-escalate heated arguments by validating underlying emotions.",
            "I value deep one-on-one heart-to-heart conversations.",
            "I proactively check in on friends and colleagues during tough times.",
            "I build bridges between culturally diverse groups of people.",
            "I advocate for social fairness and ethical responsibility.",
            "I create warm welcoming environments where individuals feel seen.",
            "I practice deep compassion toward myself and others.",
            "I understand the psychological drivers behind human resistance.",
            "I believe high-trust relationships are life's greatest asset.",
            "I mentor and elevate the next generation of builders."
        )
        empathyPrompts.forEach { prompt ->
            list.add(AssessmentQuestion(qId++, PsychometricDimension.EMPATHY, RiasecType.SOCIAL, prompt))
        }

        // 5. STRATEGIC LEADERSHIP & ENTERPRISE (22 questions)
        val strategyPrompts = listOf(
            "I naturally take charge of ambiguous initiatives and mobilize people.",
            "I am skilled at persuasive storytelling that converts skeptics into believers.",
            "I enjoy negotiating high-stakes win-win agreements.",
            "I think 3 to 5 moves ahead in business and competitive dynamics.",
            "I am comfortable taking calculated risks with substantial upside.",
            "I recruit top-tier talent and align them toward an audacious vision.",
            "I excel at pitching ideas to executive stakeholders and investors.",
            "I make tough executive calls swiftly despite incomplete information.",
            "I spot lucrative market asymmetries and commercial opportunities.",
            "I command attention and communicate with executive presence.",
            "I build strategic alliances with key industry power players.",
            "I design incentives that drive organizational momentum.",
            "I am motivated by market dominance and outsized societal impact.",
            "I delegate tactical execution to focus on high-leverage bottlenecks.",
            "I reframe setbacks as tactical data points for strategic adaptation.",
            "I understand unit economics, pricing power, and distribution moats.",
            "I inspire confidence in times of organizational turmoil.",
            "I orchestrate multi-stakeholder campaigns to achieve ambitious goals.",
            "I balance short-term quarterly execution with 10-year visionary roadmaps.",
            "I enjoy public speaking and evangelizing mission-critical movements.",
            "I optimize for velocity, scale, and high-margin capital efficiency.",
            "I build scalable enterprises that outlast my direct involvement."
        )
        strategyPrompts.forEach { prompt ->
            list.add(AssessmentQuestion(qId++, PsychometricDimension.STRATEGY, RiasecType.ENTERPRISING, prompt))
        }

        // 6. SYSTEMS ORDER & WEALTH PRECISION (20 questions)
        val systemsPrompts = listOf(
            "I maintain meticulous financial balance sheets and cash flow tracking.",
            "I automate repetitive workflows using checklists and software tools.",
            "I review terms, conditions, and contracts with sharp precision.",
            "I systematically organize files, directories, and digital workspace assets.",
            "I maintain an emergency fund and adhere to strict asset allocation models.",
            "I audit recurring subscriptions and eliminate discretionary waste.",
            "I value predictability, compliance, and rigorous operational hygiene.",
            "I build SOPs (Standard Operating Procedures) to scale quality control.",
            "I optimize tax strategies and reinvest cash flows into compounding assets.",
            "I respect established protocols when safety and capital are at risk.",
            "I identify edge-case risks that others overlook in operational plans.",
            "I double-check critical numbers and audit trail documentation.",
            "I maintain clean version control and structured archiving.",
            "I prioritize debt elimination and balance sheet fortress security.",
            "I enjoy building automated spreadsheets with dynamic financial formulas.",
            "I enforce quality assurance standards before any product release.",
            "I value long-term compound interest over speculative get-rich schemes.",
            "I design foolproof fallback contingencies for critical system failures.",
            "I manage project timelines with precise milestone dependencies.",
            "I find peace of mind in structural order, clarity, and fiscal sovereignty."
        )
        systemsPrompts.forEach { prompt ->
            list.add(AssessmentQuestion(qId++, PsychometricDimension.SYSTEMS_ORDER, RiasecType.CONVENTIONAL, prompt))
        }

        list
    }

    // 10 Hero Personality Archetypes
    val archetypes = listOf(
        PsychometricArchetype(
            id = "architect",
            name = "The Architect",
            title = "Master of Structural Systems & Order",
            icon = "🏛️",
            description = "You perceive the world as an interconnected web of systems, frameworks, and compounding structures. You excel at turning chaos into elegant operational clarity.",
            superpower = "Scalable Systems Architecture & Structural Foresight",
            growthArea = "Embracing spontaneous emotional emergence without over-engineering",
            primaryRiasec = "CIE"
        ),
        PsychometricArchetype(
            id = "sage",
            name = "The Sage",
            title = "Seeker of Foundational Truth & Wisdom",
            icon = "🧙",
            description = "Driven by relentless intellectual curiosity, you synthesize deep knowledge across domains to distill fundamental truths.",
            superpower = "First-Principles Synthesis & Analytical Rigor",
            growthArea = "Translating theoretical frameworks into immediate physical action",
            primaryRiasec = "IAS"
        ),
        PsychometricArchetype(
            id = "warrior",
            name = "The Warrior",
            title = "Relentless Executor & Tactical Pioneer",
            icon = "⚔️",
            description = "You lead through physical discipline, grit, and unstoppable forward momentum. When others hesitate, you deliver results.",
            superpower = "Unwavering Grit, High Somatic Output & Tactical Speed",
            growthArea = "Allowing adequate restorative recovery and patient listening",
            primaryRiasec = "RIE"
        ),
        PsychometricArchetype(
            id = "healer",
            name = "The Healer",
            title = "Guardian of Emotional Equilibrium & Empathy",
            icon = "💖",
            description = "Endowed with high emotional intelligence, you build high-trust environments, heal friction, and elevate the well-being of your community.",
            superpower = "Deep Empathetic Resonance & Psychological Safety",
            growthArea = "Enforcing firm boundaries and asserting personal commercial interests",
            primaryRiasec = "SAI"
        ),
        PsychometricArchetype(
            id = "visionary",
            name = "The Visionary",
            title = "Strategic Futurist & Enterprise Catalyst",
            icon = "🚀",
            description = "You see 10 years into the future, rally exceptional talent, and navigate high-stakes environments to turn audacious dreams into reality.",
            superpower = "Charismatic Persuasion, Scale Strategy & Audacious Ambition",
            growthArea = "Grounding high-flying vision in disciplined daily operational routines",
            primaryRiasec = "EIA"
        ),
        PsychometricArchetype(
            id = "alchemist",
            name = "The Alchemist",
            title = "Creative Transformer & Aesthetic Pioneer",
            icon = "🔮",
            description = "You transmute raw ideas into captivating art, design, and culture. You live at the bleeding edge of aesthetic innovation.",
            superpower = "Creative Metaphor, Visual Harmony & Novel Synthesis",
            growthArea = "Maintaining financial and procedural consistency over long horizons",
            primaryRiasec = "AIS"
        ),
        PsychometricArchetype(
            id = "strategist",
            name = "The Strategist",
            title = "Calculated Optimizer & Wealth Commander",
            icon = "♟️",
            description = "You play the long game. You analyze asymmetries, optimize capital allocation, and execute high-leverage moves with calm precision.",
            superpower = "Game Theory, Capital Efficiency & Risk-Adjusted Leverage",
            growthArea = "Vulnerability in personal relationships and spontaneous play",
            primaryRiasec = "ICE"
        ),
        PsychometricArchetype(
            id = "guardian",
            name = "The Guardian",
            title = "Protective Anchor & Communal Pillar",
            icon = "🛡️",
            description = "Reliable, steadfast, and deeply loyal, you protect what matters most and provide an unwavering foundation for those around you.",
            superpower = "Communal Loyalty, Moral Integrity & High Reliability",
            growthArea = "Taking bold calculated career risks outside established comfort zones",
            primaryRiasec = "SCR"
        ),
        PsychometricArchetype(
            id = "catalyst",
            name = "The Catalyst",
            title = "Dynamic Energizer & Movement Builder",
            icon = "⚡",
            description = "You bring unstoppable energy, ignite enthusiasm, and mobilize groups of people into coordinated action.",
            superpower = "Social Velocity, Viral Communication & Sparking Change",
            growthArea = "Patience for slow bureaucratic processes and deep solo focus",
            primaryRiasec = "ESR"
        ),
        PsychometricArchetype(
            id = "explorer",
            name = "The Explorer",
            title = "Curiosity Pioneer & Boundary Breaker",
            icon = "🧭",
            description = "Averse to monotony, you thrive on frontiers, uncharted territories, and continuous adaptation.",
            superpower = "Hyper-Adaptability, Experimental Courage & Cultural Agility",
            growthArea = "Committing to one single path for compounding long-term returns",
            primaryRiasec = "RAE"
        )
    )

    // 48 Holland RIASEC Career Matches Database
    val careersDatabase = listOf(
        // INVESTIGATIVE & SYSTEMS (I, C, E)
        CareerMatch("AI & Machine Learning Architect", "IEC", 96, "$180k - $350k+", "Designs scalable neural architectures, LLM pipelines, and autonomous AI agents.", listOf("Python", "Deep Learning", "Systems Design")),
        CareerMatch("Quantitative Hedge Fund Strategist", "ICE", 95, "$220k - $500k+", "Builds algorithmic trading models, statistical arbitrage, and risk parity systems.", listOf("Stochastics", "Data Modeling", "Alpha Generation")),
        CareerMatch("Biotechnology Research Director", "IRA", 93, "$160k - $280k", "Leads genomics, CRISPR gene therapies, and longevity cellular interventions.", listOf("Molecular Biology", "Clinical Trials", "Bio-informatics")),
        CareerMatch("Cybersecurity Defense Architect", "RIC", 92, "$150k - $260k", "Protects critical infrastructure, zero-trust protocols, and cryptographic systems.", listOf("Zero-Trust", "Pen Testing", "Threat Modeling")),
        CareerMatch("Chief Technology Officer (CTO)", "EIC", 94, "$200k - $450k+", "Directs holistic engineering strategy, enterprise scalability, and R&D pipelines.", listOf("Tech Strategy", "Engineering Leadership", "Cloud Architecture")),
        CareerMatch("Data Science & Analytics VP", "IEC", 91, "$170k - $300k", "Translates enterprise big data into strategic moat advantages and predictive models.", listOf("BigQuery", "Machine Learning", "Strategic Analytics")),
        CareerMatch("Quantum Computing Researcher", "IRC", 90, "$160k - $290k", "Pioneers superconducting qubits, quantum error correction, and quantum algorithms.", listOf("Quantum Mechanics", "Linear Algebra", "Qiskit")),
        CareerMatch("Neuroscientist & Cognitive Engineer", "IAS", 89, "$140k - $240k", "Studies neural plasticity, brain-computer interfaces, and cognitive enhancement.", listOf("fMRI Analysis", "BCI Protocols", "Cognitive Science")),

        // ENTERPRISING & STRATEGY (E, C, S)
        CareerMatch("Venture Capital General Partner", "ECI", 95, "$250k - $1M+", "Identifies generational founders, structures term sheets, and scales portfolio companies.", listOf("Deal Sourcing", "Due Diligence", "Board Governance")),
        CareerMatch("Product Strategy Vice President", "EIA", 94, "$190k - $340k", "Defines product vision, pricing mechanics, and growth flywheels for millions of users.", listOf("Product Roadmapping", "Unit Economics", "User Research")),
        CareerMatch("Fintech Founder & CEO", "ECR", 93, "$200k - $800k+", "Builds next-generation financial rails, decentralized protocols, and neo-banking suites.", listOf("Capital Raising", "Regulatory Strategy", "Market Execution")),
        CareerMatch("Private Equity M&A Lead", "CEI", 92, "$220k - $600k+", "Executes leveraged buyouts, financial engineering, and operational rollups.", listOf("LBO Modeling", "Valuation", "Due Diligence")),
        CareerMatch("Global Supply Chain Director", "CRE", 91, "$160k - $270k", "Architects resilient multi-continent logistics networks and supplier ecosystems.", listOf("Operations", "Risk Mitigation", "ERP Systems")),
        CareerMatch("Chief Operating Officer (COO)", "CER", 93, "$190k - $380k", "Drives flawless operational delivery, OKRs, and cross-functional company rhythms.", listOf("Process Engineering", "People Ops", "Resource Allocation")),
        CareerMatch("Commercial Real Estate Developer", "ERC", 90, "$180k - $450k+", "Acquires land, structures syndications, and builds mixed-use architectural projects.", listOf("Syndication", "Zoning Law", "Project Finance")),
        CareerMatch("Growth Marketing & Viral Lead", "EAS", 89, "$140k - $260k", "Engineers viral growth loops, performance marketing engines, and conversion funnels.", listOf("CAC/LTV Optimization", "Viral Hooks", "Paid Acquisition")),

        // ARTISTIC & INNOVATION (A, I, E)
        CareerMatch("Executive Creative Director", "AEI", 95, "$170k - $320k", "Directs iconic global brand aesthetics, storytelling, and multi-media campaigns.", listOf("Brand Architecture", "Creative Vision", "Art Direction")),
        CareerMatch("Principal UI/UX Product Designer", "AIR", 94, "$150k - $270k", "Crafts intuitive digital interfaces, design systems, and micro-interaction paradigms.", listOf("Figma", "Design Systems", "Interaction Design")),
        CareerMatch("Film & Narrative Director", "ASE", 92, "$130k - $350k+", "Directs cinematic narratives, visual storytelling, and emotive performance art.", listOf("Cinematography", "Screenwriting", "Creative Leadership")),
        CareerMatch("Generative AI Creative Technologist", "AIE", 93, "$160k - $290k", "Blends visual aesthetics with generative diffusion models and computational graphics.", listOf("Generative Art", "Prompt Engineering", "Shader Programming")),
        CareerMatch("Game Director & World Builder", "AIR", 91, "$150k - $280k", "Designs immersive gaming universes, core combat loops, and player progression economies.", listOf("Unreal Engine", "Game Economy", "Narrative Architecture")),
        CareerMatch("Architectural Master Planner", "ARC", 90, "$140k - $250k", "Envisions sustainable smart cities, biophilic living spaces, and urban monuments.", listOf("3D Parametric CAD", "Urban Planning", "Biophilic Design")),
        CareerMatch("Industrial Hardware Designer", "ARI", 89, "$130k - $240k", "Designs luxury consumer electronics, ergonomic hardware, and tactile materials.", listOf("Industrial CAD", "Materials Science", "DFM Prototyping")),
        CareerMatch("Music Producer & Sound Architect", "AIR", 88, "$100k - $260k+", "Engineers spatial audio, acoustic environments, and iconic audio soundscapes.", listOf("Acoustic Engineering", "Spatial Audio", "Logic Pro / Ableton")),

        // SOCIAL & EMPATHY (S, A, E)
        CareerMatch("Clinical Neuropsychologist", "SIA", 94, "$140k - $250k", "Assesses brain-behavior relationships and administers cognitive rehabilitation therapies.", listOf("Neuropsych Assessment", "CBT", "Trauma Recovery")),
        CareerMatch("Executive Performance Coach", "SEA", 93, "$150k - $350k+", "Coaches high-growth founders and Fortune 500 CEOs on mindset, focus, and energy.", listOf("Cognitive Coaching", "Habit Architecture", "EQ Training")),
        CareerMatch("Pediatric Surgeon & Physician", "SIR", 95, "$280k - $550k", "Performs delicate surgical interventions and oversees clinical patient healing.", listOf("Surgical Precision", "Patient Empathy", "Critical Care")),
        CareerMatch("Chief People Officer (CPO)", "SEC", 91, "$180k - $320k", "Builds company culture, executive compensation plans, and leadership academies.", listOf("Talent Strategy", "Culture Design", "Conflict Mediation")),
        CareerMatch("Philanthropic Foundation Director", "SEI", 90, "$150k - $270k", "Deploys non-profit endowments to solve global healthcare, poverty, and literacy crises.", listOf("Grant Making", "Impact Measurement", "Public Advocacy")),
        CareerMatch("Health & Longevity Medical Lead", "SIR", 92, "$220k - $450k", "Guides patients through preventative medicine, metabolic health, and hormone optimization.", listOf("Metabolic Medicine", "Biomarker Diagnostics", "Preventative Care")),
        CareerMatch("Organizational Psychologist", "SIE", 89, "$130k - $230k", "Analyzes workforce dynamics, burnout mitigation, and high-performance team culture.", listOf("Psychometrics", "Survey Design", "Change Management")),
        CareerMatch("Educational Technology Dean", "SAE", 88, "$130k - $220k", "Transforms higher education through immersive learning platforms and gamified curricula.", listOf("Pedagogy", "EdTech Systems", "Curriculum Design")),

        // REALISTIC & ENGINEERING (R, I, E)
        CareerMatch("Robotics & Autonomous Systems Lead", "RIE", 95, "$170k - $320k", "Builds autonomous humanoid robots, robotic actuators, and computer vision sensors.", listOf("ROS 2", "Kinematics", "C++ / Embedded")),
        CareerMatch("Aerospace Propulsion Engineer", "RIE", 94, "$160k - $290k", "Designs rocket engines, satellite thrusters, and hypersonic atmospheric vehicles.", listOf("Thermal Dynamics", "Fluid Mechanics", "CAD Modeling")),
        CareerMatch("Renewable Energy Grid Architect", "RCE", 91, "$140k - $250k", "Engineers megawatt battery storage, solar microgrids, and clean power distribution.", listOf("Smart Grids", "Battery Chemistry", "High Voltage Power")),
        CareerMatch("Biomedical Prosthetics Engineer", "RIA", 92, "$130k - $240k", "Constructs motorized bionic limbs and neural interface prosthetic devices.", listOf("Biomechanics", "Sensors", "CAD Manufacturing")),
        CareerMatch("Commercial Airline Captain", "RCE", 90, "$180k - $380k", "Commands widebody international jetliners with flawless navigation and safety standards.", listOf("Avionics", "Crew Resource Mgmt", "IFR Navigation")),
        CareerMatch("Precision Fabrication Specialist", "RCI", 88, "$100k - $190k", "Operates 5-axis CNC machines and titanium additive manufacturing for aerospace components.", listOf("5-Axis CNC", "Additive Mfg", "Metallurgy")),
        CareerMatch("Deep Sea Marine Explorer & Engineer", "RIC", 89, "$120k - $230k", "Deploys autonomous underwater submersibles to map abyssal ocean geology.", listOf("Hydrodynamics", "Oceanography", "Submersible Piloting")),
        CareerMatch("Sports Biomechanics Director", "RSE", 91, "$130k - $240k", "Optimizes elite Olympic and NBA athletes' movement efficiency and force production.", listOf("Force Plates", "Motion Capture", "Athletic Conditioning")),

        // CONVENTIONAL & SYSTEMS ORDER (C, E, I)
        CareerMatch("Chief Financial Officer (CFO)", "CEI", 96, "$220k - $600k+", "Stewards corporate capital structure, debt syndications, treasury, and investor relations.", listOf("Corporate Finance", "GAAP / IFRS", "Treasury Mgmt")),
        CareerMatch("Tax & Wealth Preservation Partner", "CIE", 93, "$200k - $450k+", "Structures family offices, offshore trusts, and multi-generational tax minimization.", listOf("Trust Law", "Estate Planning", "Asset Protection")),
        CareerMatch("Enterprise Compliance & Risk Officer", "CES", 91, "$160k - $280k", "Enforces global regulatory compliance, AML, and corporate anti-fraud frameworks.", listOf("Regulatory Audit", "AML Protocols", "Risk Governance")),
        CareerMatch("Actuarial Science Director", "CIE", 94, "$170k - $300k", "Calculates complex mortality, climate catastrophe, and financial systemic liabilities.", listOf("Stochastic Actuarial Models", "R / Python", "Underwriting")),
        CareerMatch("Clinical Operations Director", "CSE", 90, "$150k - $260k", "Streamlines hospital operations, medical records compliance, and surgical ward logistics.", listOf("Healthcare Admin", "Six Sigma", "HIPAA Compliance")),
        CareerMatch("Database & Infrastructure Lead", "CIR", 92, "$160k - $280k", "Maintains distributed databases, high availability replication, and disaster recovery.", listOf("PostgreSQL", "Cassandra", "Disaster Recovery")),
        CareerMatch("Forensic Financial Investigator", "CIE", 89, "$130k - $240k", "Uncovers complex money laundering, embezzlement, and corporate fraud schemes.", listOf("Forensic Accounting", "Subpoena Analysis", "Court Testimony")),
        CareerMatch("Logistics Optimization Engineer", "CRE", 90, "$130k - $220k", "Solves traveling salesperson and route optimization problems for global freight fleets.", listOf("Linear Programming", "Operations Research", "Simulations"))
    )

    fun calculatePsychometricScores(answers: Map<Int, Int>): Map<PsychometricDimension, Int> {
        val result = mutableMapOf<PsychometricDimension, Int>()
        
        PsychometricDimension.values().forEach { dim ->
            val dimQuestions = questions.filter { it.dimension == dim }
            val count = dimQuestions.size
            if (count > 0) {
                val totalRating = dimQuestions.sumOf { q -> answers[q.id] ?: 3 } // default 3 (neutral)
                val maxPossible = count * 5.0
                val normalizedScore = ((totalRating / maxPossible) * 200.0).toInt().coerceIn(0, 200)
                result[dim] = normalizedScore
            } else {
                result[dim] = 100
            }
        }
        return result
    }

    fun calculateRiasecCode(answers: Map<Int, Int>): String {
        val riasecTotals = mutableMapOf<RiasecType, Int>()
        RiasecType.values().forEach { riasecTotals[it] = 0 }

        questions.forEach { q ->
            val rating = answers[q.id] ?: 3
            riasecTotals[q.riasecType] = (riasecTotals[q.riasecType] ?: 0) + rating
        }

        val sorted = riasecTotals.entries.sortedByDescending { it.value }
        return sorted.take(3).joinToString("") { it.key.code }
    }

    fun determineArchetype(scores: Map<PsychometricDimension, Int>, riasecCode: String): PsychometricArchetype {
        val intellect = scores[PsychometricDimension.INTELLECTUAL] ?: 100
        val execution = scores[PsychometricDimension.EXECUTION] ?: 100
        val creative = scores[PsychometricDimension.CREATIVE] ?: 100
        val empathy = scores[PsychometricDimension.EMPATHY] ?: 100
        val strategy = scores[PsychometricDimension.STRATEGY] ?: 100
        val systems = scores[PsychometricDimension.SYSTEMS_ORDER] ?: 100

        return when {
            systems >= 150 && intellect >= 140 -> archetypes.find { it.id == "architect" }!!
            intellect >= 160 -> archetypes.find { it.id == "sage" }!!
            execution >= 160 -> archetypes.find { it.id == "warrior" }!!
            empathy >= 160 -> archetypes.find { it.id == "healer" }!!
            strategy >= 160 -> archetypes.find { it.id == "visionary" }!!
            creative >= 160 -> archetypes.find { it.id == "alchemist" }!!
            systems >= 150 && strategy >= 140 -> archetypes.find { it.id == "strategist" }!!
            empathy >= 140 && systems >= 130 -> archetypes.find { it.id == "guardian" }!!
            strategy >= 140 && empathy >= 130 -> archetypes.find { it.id == "catalyst" }!!
            else -> archetypes.find { it.id == "explorer" }!!
        }
    }

    fun findTopCareerMatches(riasecCode: String, limit: Int = 10): List<CareerMatch> {
        val primaryLetter = riasecCode.firstOrNull() ?: 'I'
        val secondaryLetter = riasecCode.getOrNull(1) ?: 'E'
        val tertiaryLetter = riasecCode.getOrNull(2) ?: 'C'

        return careersDatabase
            .sortedByDescending { career ->
                var score = career.matchPercentage
                if (career.riasecCode.contains(primaryLetter)) score += 15
                if (career.riasecCode.contains(secondaryLetter)) score += 8
                if (career.riasecCode.contains(tertiaryLetter)) score += 4
                score
            }
            .take(limit)
    }

    fun evaluateAssessment(answers: Map<Int, Int>): AssessmentResult {
        val scores = calculatePsychometricScores(answers)
        val overall = scores.values.sum() // 0 - 1200
        val riasecCode = calculateRiasecCode(answers)
        val archetype = determineArchetype(scores, riasecCode)
        val careers = findTopCareerMatches(riasecCode, limit = 12)

        return AssessmentResult(
            dimensionScores = scores,
            overallScore = overall,
            archetype = archetype,
            topRiasecCode = riasecCode,
            topCareers = careers
        )
    }
}
