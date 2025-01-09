package com.consilium.vcg

import edu.stanford.nlp.pipeline.StanfordCoreNLP
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.*

@SpringBootApplication
@EnableScheduling
class VcgApplication:SpringBootServletInitializer()

fun main(args: Array<String>)  {

	val props: Properties = Properties()
	props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,depparse")


	// 加载中文模型
	props.setProperty("tokenize.language", "zh")

	val pipeline: StanfordCoreNLP = StanfordCoreNLP(props)
	runApplication<VcgApplication>(*args)
}
