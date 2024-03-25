package eu.chrost.rxweb.web.sse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/sse/upper-case")
@RequiredArgsConstructor
public class UpperCaseWebController {
    private final Map<String, Sinks.Many<UpperCaseProcessingEvent>> sinks = new HashMap<>();

    public interface UpperCaseProcessingEvent {
        @JsonIgnore
        String getEventName();
    }

    private record CompletionRateEvent(int rate) implements UpperCaseProcessingEvent {
        @Override
        public String getEventName() {
            return "COMPLETION_RATE";
        }
    }

    private record ResultEvent(String result) implements UpperCaseProcessingEvent {
        @Override
        public String getEventName() {
            return "RESULT";
        }
    }


    @RequiredArgsConstructor
    private static class UpperCaseProcessingTask implements Runnable {
        private final Sinks.Many<UpperCaseProcessingEvent> sink;
        private final String input;

        @Override
        @SneakyThrows
        public void run() {
            for (int i  = 0; i < 10; ++i) {
                sink.tryEmitNext(new CompletionRateEvent(i * 10));
                Thread.sleep(10000);
            }
            sink.tryEmitNext(new ResultEvent(input.toUpperCase()));
        }
    }

    @PostMapping
    public String startUpperCaseProcessing(@RequestParam String input) {
        var uuid = UUID.randomUUID().toString();
        var sink = Sinks.many().replay().<UpperCaseProcessingEvent>limit(1);
        sinks.put(uuid, sink);

        new Thread(new UpperCaseProcessingTask(sink, input)).start();

        return uuid;
    }

    @GetMapping("/{uuid}")
    public Flux<ServerSentEvent<UpperCaseProcessingEvent>> getUpperCaseProcessingResults(@PathVariable String uuid) {
        var sink = sinks.get(uuid);
        var flux = Optional.ofNullable(sink).map(Sinks.Many::asFlux).orElse(Flux.empty());

        return flux.map(e -> ServerSentEvent.<UpperCaseProcessingEvent>builder()
                .event(e.getEventName()).data(e).build());
    }
}
