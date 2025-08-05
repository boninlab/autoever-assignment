package com.autoever.assignment.api.admin

import com.autoever.assignment.dto.user.UserUpdateRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.delete
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

@SpringBootTest
@AutoConfigureMockMvc
class AdminUserControllerTest @Autowired constructor(
    val mockMvc: MockMvc
) {
    val mapper = jacksonObjectMapper()

    @Test
    fun `회원 목록 조회`() {
        mockMvc.get("/admin/users") {
            header("Authorization", "Basic YWRtaW46MTIxMg==")
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `회원 주소 수정`() {
        val request = UserUpdateRequest(
            password = "newpass123",
            address = "부산광역시 해운대구"
        )


        mockMvc.patch("/admin/users/1") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Basic YWRtaW46MTIxMg==")
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { isNoContent() }
        }
    }

    @Test
    fun `회원 삭제`() {
        mockMvc.delete("/admin/users/1") {
            header("Authorization", "Basic YWRtaW46MTIxMg==")
        }.andExpect {
            status { isNoContent() }
        }
    }
}
