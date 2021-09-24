package org.example;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * pdf tables convert api
 */
public class ConvertToCsv {

    public static InputStream parse(File inputFile) throws Exception {

        final String apiKey = "YOU_API_KEY";
        final String format = "csv";

        // Avoid cookie warning with default cookie configuration
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();

        if (!inputFile.canRead()) {
            System.out.println("Can't read input PDF file");
            System.exit(1);
        }

        try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build()) {
            HttpPost httppost = new HttpPost("https://pdftables.com/api?format=" + format + "&key=" + apiKey);
            FileBody fileBody = new FileBody(inputFile);

            HttpEntity requestBody = MultipartEntityBuilder.create().addPart("f", fileBody).build();
            httppost.setEntity(requestBody);

            System.out.println("Sending request");

            try (CloseableHttpResponse response = httpclient.execute(httppost)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    System.out.println(response.getStatusLine());
                    System.exit(1);
                }
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    return IOUtils.toBufferedInputStream(resEntity.getContent());
                } else {
                    System.out.println("Error: file missing from response");
                    System.exit(1);
                }
            }
            return null;
        }
    }
}