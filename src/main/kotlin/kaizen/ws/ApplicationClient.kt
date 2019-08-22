package kaizen.ws

import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil.UTF_8
import reactor.core.publisher.Flux
import reactor.netty.http.client.HttpClient

fun main() {
    val outgoingMessagesFlux = Flux.just(Unpooled.wrappedBuffer("hello".toByteArray(UTF_8)))

    HttpClient.create()
        .websocket()
        .uri("wss://echo.websocket.org")
        .handle { inbound, outbound ->
            val thenInbound = inbound.receive()
                .asString()
                .doOnNext { println(it) }
                .then()

            val thenOutbound = outbound.send(outgoingMessagesFlux).neverComplete()
            Flux.zip(thenInbound, thenOutbound).then()
        }.blockLast()
}
