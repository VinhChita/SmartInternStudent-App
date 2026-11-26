package com.smartintern.student

import okhttp3.MultipartBody

class MockApi : Api {
    override suspend fun login(b: LoginReq): LoginRes = LoginRes(
        token = "mock_temporary_token",
        user = UserDto(1, "Vinh Chita", b.login.ifBlank { "vinhchitaofficial@gmail.com" }, "bsr180105")
    )

    override suspend fun register(b: RegisterReq): MessageRes = MessageRes("ចុះឈ្មោះដោយជោគជ័យ!")

    override suspend fun logout(): MessageRes = MessageRes("ចាកចេញដោយជោគជ័យ")

    override suspend fun dashboard(): DashboardRes = DashboardRes(
        name = "Vinh Chita",
        stats = Stats(applications = 13, approved = 5, rejected = 3, activities = 15),
        next_meeting = MeetingDto(
            id = 1,
            title = "ជួបពិភាក្សាការងារជាមួយ Supervisor",
            topic = "របាយការណ៍សប្តាហ៍ទី ៤",
            day = "15",
            month = "មេសា",
            year = "2025",
            time = "09:00 AM",
            start_time = "09:00:00"
        )
    )

    override suspend fun programs(q: String?, type: String?): ProgramsRes {
        val list = listOf(
            ProgramDto(1, "Mobile App Developer (Kotlin / Compose)", "Smart Axiata", "ពេញម៉ោង", "ភ្នំពេញ", true),
            ProgramDto(2, "Full Stack Web Developer (Laravel & React)", "ABA Bank", "ពេញម៉ោង", "ភ្នំពេញ", true),
            ProgramDto(3, "UI/UX Designer Intern", "Wing Bank", "ក្រៅម៉ោង", "ភ្នំពេញ", false),
            ProgramDto(4, "Data Analyst Intern", "Cellcard", "ពេញម៉ោង", "ភ្នំពេញ", false),
            ProgramDto(5, "DevOps & Cloud Engineer Intern", "Canadia Bank", "ក្រៅម៉ោង", "ភ្នំពេញ", false),
            ProgramDto(6, "Cybersecurity Assistant Intern", "Chip Mong Group", "ពេញម៉ោង", "ភ្នំពេញ", false),
            ProgramDto(7, "iOS App Developer (SwiftUI)", "ACLEDA Bank", "ពេញម៉ោង", "ភ្នំពេញ", false),
            ProgramDto(8, "Frontend Developer (Vue.js / Next.js)", "Sathapana Bank", "ក្រៅម៉ោង", "ភ្នំពេញ", false),
            ProgramDto(9, "AI & Machine Learning Intern", "Vattanac Bank", "ពេញម៉ោង", "ភ្នំពេញ", false),
            ProgramDto(10, "Software Quality Assurance (QA / Tester)", "Prince Bank", "ក្រៅម៉ោង", "ភ្នំពេញ", false),
            ProgramDto(11, "Database Administrator (PostgreSQL)", "Hattha Bank", "ពេញម៉ោង", "ភ្នំពេញ", false),
            ProgramDto(12, "IT Support & Network Specialist", "Telecom Cambodia", "ក្រៅម៉ោង", "ភ្នំពេញ", false),
            ProgramDto(13, "Flutter Cross-Platform Developer", "PassApp Technologies", "ពេញម៉ោង", "ភ្នំពេញ", false),
            ProgramDto(14, "Graphic & Motion Designer", "Foodpanda Cambodia", "ក្រៅម៉ោង", "ភ្នំពេញ", false),
            ProgramDto(15, "Product Management Intern", "Grab Cambodia", "ពេញម៉ោង", "ភ្នំពេញ", false),
            ProgramDto(16, "Cloud Solutions Architect Intern", "AWS Partner Cambodia", "ពេញម៉ោង", "ភ្នំពេញ", false)
        )
        val filtered = list.filter { item ->
            (q.isNullOrBlank() || item.title.contains(q, true) || item.company.contains(q, true)) &&
            (type.isNullOrBlank() || type == "all" || item.type_label.contains(type, true))
        }
        return ProgramsRes(filtered)
    }

    override suspend fun apply(id: Int): MessageRes = MessageRes("ដាក់ពាក្យស្នើសុំបានជោគជ័យ!")

    override suspend fun applications(): ApplicationsRes = ApplicationsRes(
        counts = Counts(all = 13, pending = 5, approved = 5, rejected = 3),
        items = listOf(
            ApplicationDto(1, "Mobile App Developer", "Smart Axiata", "approved", "10 មករា 2025"),
            ApplicationDto(2, "Full Stack Web Developer", "ABA Bank", "pending", "15 កុម្ភៈ 2025"),
            ApplicationDto(3, "Backend Developer (Java)", "Chip Mong Group", "rejected", "01 ធ្នូ 2024"),
            ApplicationDto(4, "UI/UX Designer Intern", "Wing Bank", "approved", "12 មករា 2025"),
            ApplicationDto(5, "iOS App Developer (SwiftUI)", "ACLEDA Bank", "pending", "18 កុម្ភៈ 2025"),
            ApplicationDto(6, "Data Analyst Intern", "Cellcard", "approved", "05 មករា 2025"),
            ApplicationDto(7, "DevOps Assistant Intern", "Canadia Bank", "pending", "20 កុម្ភៈ 2025"),
            ApplicationDto(8, "Frontend Developer (Vue.js)", "Sathapana Bank", "rejected", "15 វិច្ឆិកា 2024"),
            ApplicationDto(9, "Software QA Tester", "Prince Bank", "approved", "22 មករា 2025"),
            ApplicationDto(10, "Database Administrator", "Hattha Bank", "pending", "22 កុម្ភៈ 2025"),
            ApplicationDto(11, "Network Administrator", "Telecom Cambodia", "approved", "28 មករា 2025"),
            ApplicationDto(12, "Flutter Developer", "PassApp Technologies", "pending", "25 កុម្ភៈ 2025"),
            ApplicationDto(13, "Product Manager Intern", "Grab Cambodia", "rejected", "10 ធ្នូ 2024")
        )
    )

    override suspend fun profile(): ProfileRes = ProfileRes(
        name = "Vinh Chita",
        initials = "TA",
        major = "វិទ្យាសាស្ត្រកុំព្យូទ័រ (Computer Science)",
        email = "vinhchita@gmail.com",
        phone = "0979391971",
        birth_date_label = "27 មករា 2005",
        location = "សៀមរាប, កម្ពុជា",
        cv = CvDto("Vinh_Chita_CV_2025.pdf", "2.4 MB", "12 មករា 2025")
    )

    override suspend fun uploadCv(file: MultipartBody.Part): MessageRes = MessageRes("បានបញ្ចូល CV រួចរាល់!")

    override suspend fun deleteCv(): MessageRes = MessageRes("បានលុប CV រួចរាល់")

    override suspend fun reports(type: String): ReportsRes {
        val list = listOf(
            ReportDto(1, "weekly", "របាយការណ៍សប្តាហ៍ទី ១", "បានសិក្សាពីកូដចាស់ និងកំណត់រចនាសម្ព័ន្ធបណ្តាញ", "01 - 07 មករា 2025", "approved"),
            ReportDto(2, "weekly", "របាយការណ៍សប្តាហ៍ទី ២", "បានអភិវឌ្ឍអេក្រង់ Authentication & API Integration", "08 - 14 មករា 2025", "approved"),
            ReportDto(3, "weekly", "របាយការណ៍សប្តាហ៍ទី ៣", "បានបង្កើតសេវា RESTful API ជាមួយ Laravel Sanctuam", "15 - 21 មករា 2025", "approved"),
            ReportDto(4, "weekly", "របាយការណ៍សប្តាហ៍ទី ៤", "បានធ្វើតេស្តមុខងារសរុប និងកែសម្រួល UI/UX ឲ្យស្អាត", "22 - 28 មករា 2025", "submitted"),
            ReportDto(5, "weekly", "របាយការណ៍សប្តាហ៍ទី ៥", "បានរៀបចំការតភ្ជាប់រវាង Flutter/Kotlin និង Backend", "01 - 07 កុម្ភៈ 2025", "submitted"),
            ReportDto(6, "weekly", "របាយការណ៍សប្តាហ៍ទី ៦", "បានធ្វើការបង្កើនល្បឿន និង Optimize Database Query", "08 - 14 កុម្ភៈ 2025", "draft"),
            ReportDto(7, "monthly", "របាយការណ៍ប្រចាំខែ មករា", "ការងារសរុបប្រចាំខែមករា និងសមិទ្ធផលដែលសម្រេចបាន", "មករា 2025", "approved"),
            ReportDto(8, "monthly", "របាយការណ៍ប្រចាំខែ កុម្ភៈ", "ការងារសរុបប្រចាំខែកុម្ភៈ និងការអភិវឌ្ឍប្រព័ន្ធ", "កុម្ភៈ 2025", "submitted")
        )
        val filtered = if (type.isNotBlank() && type != "all") list.filter { it.type == type } else list
        return ReportsRes(filtered)
    }

    override suspend fun createReport(b: ReportReq): ReportDto = ReportDto(
        id = 10,
        type = b.type,
        title = if (b.type == "monthly") "របាយការណ៍ប្រចាំខែថ្មី" else "របាយការណ៍សប្តាហ៍ថ្មី",
        notes = b.notes,
        period_label = "បច្ចុប្បន្ន",
        status = if (b.submit) "submitted" else "draft"
    )

    override suspend fun updateReport(id: Int, b: ReportReq): ReportDto = ReportDto(
        id = id,
        type = b.type,
        title = "របាយការណ៍ត្រូវបានធ្វើបច្ចុប្បន្នភាព",
        notes = b.notes,
        period_label = "បច្ចុប្បន្ន",
        status = if (b.submit) "submitted" else "draft"
    )

    override suspend fun evaluation(): EvaluationRes = EvaluationRes(
        evaluation = EvaluationDto(professionalism = 4.8, communication = 4.6),
        overall_label = "ល្អប្រសើរខ្លាំង (Excellent)",
        meetings = listOf(
            MeetingDto(1, "ការវាយតម្លៃពាក់កណ្តាលអាណត្តិ", "Mid-term Evaluation", "20", "កុម្ភៈ", "2025", "10:00 AM", "10:00:00"),
            MeetingDto(2, "ការវាយតម្លៃចុងក្រោយ", "Final Evaluation", "25", "មីនា", "2025", "02:00 PM", "14:00:00"),
            MeetingDto(3, "ការប្រជុំពិភាក្សាលទ្ធផលការងារ", "Performance Review", "10", "មេសា", "2025", "03:30 PM", "15:30:00")
        )
    )

    override suspend fun sendMessage(b: SendMessageReq): MessageRes = MessageRes("ផ្ញើសារបានជោគជ័យ!")

    override suspend fun notifications(): NotificationsRes = NotificationsRes(
        items = listOf(
            NotificationDto(1, "info", "ពាក្យស្នើសុំត្រូវបានអនុម័ត", "ក្រុមហ៊ុន Smart Axiata បានអនុម័តពាក្យស្នើសុំកម្មសិក្សារបស់អ្នក", "2 ម៉ោងមុន", false),
            NotificationDto(2, "meeting", "កាលវិភាគប្រជុំថ្មី", "អ្នកមានការប្រជុំជាមួយអ្នកសម្របសម្រួលនៅថ្ងៃស្អែក", "1 ថ្ងៃមុន", false),
            NotificationDto(3, "report", "របាយការណ៍បានពិនិត្យរួចរាល់", "របាយការណ៍សប្តាហ៍ទី ២ ត្រូវបានទទួលស្គាល់", "3 ថ្ងៃមុន", true),
            NotificationDto(4, "info", " ABA Bank បានផ្ញើសារអញ្ជើញ", " ABA Bank បានអញ្ជើញអ្នកមកសម្ភាសន៍ផ្ទាល់", "4 ថ្ងៃមុន", true),
            NotificationDto(5, "info", "ការដាក់ពាក្យថ្មី", "ពាក្យស្នើសុំនៅ Cellcard ត្រូវបានទទួលរួចរាល់", "5 ថ្ងៃមុន", true),
            NotificationDto(6, "meeting", "រំលឹកកាលវិភាគប្រជុំ", "ការប្រជុំវាយតម្លៃពាក់កណ្តាលអាណត្តិនឹងចាប់ផ្តើមនៅម៉ោង 10:00 AM", "1 សប្តាហ៍មុន", true),
            NotificationDto(7, "report", "របាយការណ៍ត្រូវបានអនុម័ត", "របាយការណ៍ប្រចាំខែមករា ត្រូវបានអនុម័តដោយជោគជ័យ", "2 សប្តាហ៍មុន", true),
            NotificationDto(8, "info", "ព័ត៌មានកម្មវិធីថ្មី", "មានកម្មវិធីកម្មសិក្សាថ្មីនៅ Canadia Bank", "3 សប្តាហ៍មុន", true),
            NotificationDto(9, "info", "ការអាប់ដេតប្រព័ន្ធ", "ប្រព័ន្ធ Smart Internship ត្រូវធ្វើបច្ចុប្បន្នភាព", "1 ខែមុន", true),
            NotificationDto(10, "info", "ស្វាគមន៍មកកាន់ Smart Internship", "គណនីរបស់អ្នកត្រូវបានបង្កើតដោយជោគជ័យ", "1 ខែមុន", true)
        )
    )
}
