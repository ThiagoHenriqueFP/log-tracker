package br.edu.ufersa.distributed_logging.web

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping(value = ["/off"])
interface WithoutLogController {
    @GetMapping("/simples")
    fun simple(): ResponseEntity<Any>

    @GetMapping("/aninhado")
    fun nested(): ResponseEntity<Any>

    @GetMapping("/async")
    fun async(): ResponseEntity<Any>

    @GetMapping("/coroutine")
    fun coroutine(): ResponseEntity<String>

    @GetMapping("/simples/{value}")
    fun simple(@PathVariable(name = "value") value: String): ResponseEntity<Any>

    @PostMapping("/simples/{value}")
    fun external(@PathVariable(name = "value") value: String): ResponseEntity<Any>
}