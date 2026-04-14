package br.edu.ufersa.distributed_logging

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DistributedLoggingApplication

fun main(args: Array<String>) {
	runApplication<DistributedLoggingApplication>(*args)
}
