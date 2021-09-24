package org.example;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Xiaobo Bi (869384236@qq.com)
 */
public class DownloadPdf {

    private static final String url = "https://www.dhl.de/en/geschaeftskunden/express/infos-knowhow/hilfe-zollabwicklung/plt.html";
    private static final String host = "https://www.dhl.de";

    public static List<PltCountriesAvailable> downloadAndParse() throws Exception {
        Document doc = Jsoup.connect(url).get();

        Elements elements = doc.select("a.documentDownload");
        String pdfUrl = null;
        for (Element element : elements) {
            if (element.text().contains("PLT")) {
                pdfUrl = host + element.attr("href");
            }
        }
        if (pdfUrl == null) {
            throw new NullPointerException();
        }
        File tempFile = File.createTempFile("tmp", "pdf");
        HttpGet get = new HttpGet(pdfUrl);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try (CloseableHttpResponse response = httpClient.execute(get)) {
            InputStream content = response.getEntity().getContent();
            FileUtils.copyToFile(content, tempFile);
        }

        InputStream parse = ConvertToCsv.parse(tempFile);
        String s = IOUtils.toString(parse, StandardCharsets.UTF_8);
        String[] lines = s.split("\\n");
        List<PltCountriesAvailable> results = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith("Country,IB")) {
                continue;
            }
            System.out.println(line);
            List<String> split = split(line).stream()
                    .filter(str -> !StringUtil.isBlank(str))
                    .collect(Collectors.toList());
            if (split.size() % 3 != 0) {
                continue;
            }
            for (int i = 0; i < split.size(); i += 3) {
                results.add(new PltCountriesAvailable(
                        split.get(i),
                        "Y".equalsIgnoreCase(split.get(i + 1)),
                        "Y".equalsIgnoreCase(split.get(i + 2))
                ));
            }
        }
        return results;
    }

    public static List<String> split(String str) {
        char[] chars = str.toCharArray();
        boolean openClose = false;
        StringBuilder sb = new StringBuilder();
        List<String> result = new ArrayList<>();
        for (char aChar : chars) {
            if (aChar == '"') {
                if (openClose) {
                    openClose = false;
                    result.add(sb.toString());
                    sb = new StringBuilder();
                } else {
                    openClose = true;
                }
                continue;
            }
            if (!openClose && aChar == ',') {
                result.add(sb.toString());
                sb = new StringBuilder();
                continue;
            }
            sb.append(aChar);
        }
        result.add(sb.toString());

        return result;
    }
}
