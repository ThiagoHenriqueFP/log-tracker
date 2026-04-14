package br.edu.ufersa.distributed_logging.web

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping(value = ["/off"])
interface WithOutLogController {
    @GetMapping("/simples")
    fun simple(): ResponseEntity<Any>

    @GetMapping("/aninhado")
    fun nested(): ResponseEntity<Unit>

    @GetMapping("/async")
    fun async(): ResponseEntity<Unit>

    @GetMapping("/coroutine")
    fun coroutine(): ResponseEntity<Unit>
}