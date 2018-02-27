package ar.com.phostech.demo.consumers

import ar.com.phostech.vertx.EventBusConsumer
import io.vertx.core.eventbus.EventBus
import java.util.concurrent.TimeUnit


class DemoEventBusConsumer :EventBusConsumer{

    override fun mount(eventBus: EventBus) {
        eventBus.consumer<String>(BusPaths.HOTJAR_LOGIN, { message ->
            try {
                TimeUnit.MILLISECONDS.sleep(300)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            message.reply("response to " + message.body())
        })


    }
}
