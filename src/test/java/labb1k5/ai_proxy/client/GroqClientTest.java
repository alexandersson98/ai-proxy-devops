package labb1k5.ai_proxy.client;


import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;


public class GroqClientTest {



    @Test
    void retriesThreeTimesOnRateLimit(){
        RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GroqClient client = new GroqClient(builder.build());

        server.expect(times(3), requestTo("http://localhost/openai/v1/chat/completions"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));

        String result = client.send(Map.of("model", "test"));

        assertNull(result);
        server.verify();
    }


    @Test
    void throwsExceptionWhenReadTimesOut() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/openai/v1/chat/completions", exchange -> {
            try { Thread.sleep(500); } catch (InterruptedException ignored) { }
            byte[] body = "{}".getBytes();
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        int port = server.getAddress().getPort();

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofMillis(50));
        RestClient restClient = RestClient.builder()
                .requestFactory(factory)
                .baseUrl("http://localhost:" + port)
                .build();
        GroqClient client = new GroqClient(restClient);

        assertThrows(RestClientException.class,
                () -> client.send(Map.of("model", "test")));

        server.stop(0);
    }
}
