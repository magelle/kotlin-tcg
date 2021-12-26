package magelle.tcg.customer

import io.ktor.http.*
import io.ktor.server.testing.*
import magelle.tcg.module
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

internal class OrderRouteTests {
    @Test
    fun testPostCustomer() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/customer") {
                setBody(
                    """{
                      "id": "100",
                      "firstName": "Jane",
                      "lastName": "Smith",
                      "email": "jane.smith@company.com"
                    }"""
                )
                addHeader("Content-Type", "application/json")
            }.apply {
                assertEquals(HttpStatusCode.Created, response.status())
            }
        }
    }
}