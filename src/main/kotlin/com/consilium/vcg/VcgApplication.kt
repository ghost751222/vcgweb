package com.consilium.vcg

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class VcgApplication:SpringBootServletInitializer()

fun main(args: Array<String>)  {
	runApplication<VcgApplication>(*args)
}
