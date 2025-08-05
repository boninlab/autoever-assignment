package com.autoever.assignment.api.user

import com.autoever.assignment.dto.user.LoginRequest
import com.autoever.assignment.dto.user.UserSignUpRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest @Autowired constructor(
    val mockMvc: MockMvc
) {
    val mapper = jacksonObjectMapper()

    @Test
    fun `회원가입 테스트`() {
        val request = UserSignUpRequest(
            account = "test002",
            password = "pw1235678",
            name = "박유저",
            rrn = "9701011234567",
            phone = "01088887777",
            address = "부산광역시 수영구"
        )

        mockMvc.post("/api/users") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `로그인 테스트`() {
        val request = LoginRequest("test002", "pw1235678")

        mockMvc.post("/api/users/login") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `내 정보 조회`() {
        mockMvc.get("/api/users/me") {
            header("X-Account", "test002")
        }.andExpect {
            status { isOk() }
        }
    }
}
