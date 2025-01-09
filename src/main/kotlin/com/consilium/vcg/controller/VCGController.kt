package com.consilium.vcg.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
@RequestMapping(value = ["/page","","/"])
class VCGController {


    @RequestMapping(value = [""], method = [RequestMethod.GET])
    fun page():String
    {
        return "page"
    }

}