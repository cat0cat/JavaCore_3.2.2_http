import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Main {

    public static final String REMOTE_SERVICE_URI =
            "https://api.nasa.gov/planetary/apod?api_key=2lnOoYlL1Z235HZz1hvWVJodhtjqdbiE27sj3Rhx";
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();
        // создание объекта запроса с произвольными заголовками
        HttpGet request = new HttpGet(REMOTE_SERVICE_URI);
        CloseableHttpResponse response = httpClient.execute(request);
        NASA nasa = mapper.readValue(response.getEntity().getContent(),NASA.class);
        System.out.println(nasa);

        HttpGet imageRequest = new HttpGet(String.valueOf(nasa.getUrl()));
        CloseableHttpResponse imageResponse = httpClient.execute(imageRequest);
        byte[] bytes = imageResponse.getEntity().getContent().readAllBytes();
        FileOutputStream output = new FileOutputStream(new File(nasa.getUrl().getPath()).getName());
        output.write(bytes);
        output.flush();
        output.close();
    }
}
