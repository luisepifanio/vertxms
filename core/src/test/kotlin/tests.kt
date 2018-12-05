package demo

import ar.com.phostech.vertx.core.env.ExecutionEnvironment
import kotlin.test.assertEquals
import org.junit.Test as test

class TestSource() {
    @test fun f() {

        ExecutionEnvironment.development()


        val example : KotlinGreetingJoiner = KotlinGreetingJoiner(Greeter("Hi"))
        example.addName("Harry")
        example.addName("Ron")
        example.addName(null)
        example.addName("Hermione")

        assertEquals("Hi Harry and Ron and Hermione" , example.getJoinedGreeting() )
    }
}

