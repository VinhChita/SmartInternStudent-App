package com.smartintern.student

data class LoginReq(val login: String, val password: String, val device_name: String = "android")
data class RegisterReq(val name: String, val email: String, val student_id: String?, val password: String, val password_confirmation: String)
data class UserDto(val id: Int, val name: String, val email: String, val student_id: String?)
data class LoginRes(val token: String, val user: UserDto)
data class MessageRes(val message: String?)

data class Stats(val applications: Int, val approved: Int, val rejected: Int, val activities: Int)
data class MeetingDto(val id: Int, val title: String, val topic: String?, val day: String, val month: String, val year: String, val time: String, val start_time: String)
data class DashboardRes(val name: String, val stats: Stats, val next_meeting: MeetingDto?)

data class ProgramDto(val id: Int, val title: String, val company: String, val type_label: String, val location: String, val has_applied: Boolean)
data class ProgramsRes(val items: List<ProgramDto>)

data class ApplicationDto(val id: Int, val title: String, val company: String, val status: String, val applied_label: String)
data class Counts(val all: Int, val pending: Int, val approved: Int, val rejected: Int)
data class ApplicationsRes(val counts: Counts, val items: List<ApplicationDto>)

data class CvDto(val name: String, val size: String, val uploaded: String)
data class ProfileRes(val name: String, val initials: String, val major: String?, val email: String, val phone: String?, val birth_date_label: String?, val location: String?, val cv: CvDto?)

data class ReportDto(val id: Int, val type: String, val title: String, val notes: String?, val period_label: String, val status: String)
data class ReportsRes(val items: List<ReportDto>)
data class ReportReq(val type: String, val notes: String?, val submit: Boolean)

data class EvaluationDto(val professionalism: Double, val communication: Double)
data class EvaluationRes(val evaluation: EvaluationDto?, val overall_label: String?, val meetings: List<MeetingDto>)
data class SendMessageReq(val body: String)

data class NotificationDto(val id: Int, val type: String, val title: String, val body: String?, val ago: String, val read: Boolean)
data class NotificationsRes(val items: List<NotificationDto>)
