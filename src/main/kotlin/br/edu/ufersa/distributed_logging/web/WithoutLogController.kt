package br.edu.ufersa.distributed_logging.web

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
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
}