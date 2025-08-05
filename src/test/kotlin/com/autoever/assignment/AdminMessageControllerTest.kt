package com.autoever.assignment.api.admin

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

@SpringBootTest
@AutoConfigureMockMvc
class AdminMessageControllerTest @Autowired constructor(
    val mockMvc: MockMvc
) {
    val mapper = jacksonObjectMapper()

    @Test
    fun `연령별 메시지 발송 요청`() {
        val body = mapOf(
            10 to "10대를 위한 첫차 혜택!",
            20 to "20대를 위한 모빌리티 할인!",
            30 to "30대를 위한 SUV 혜택!"
        )

        mockMvc.post("/admin/messages/send") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Basic YWRtaW46MTIxMg==")
            content = mapper.writeValueAsString(body)
        }.andExpect {
            status { isOk() }
        }
    }
}
