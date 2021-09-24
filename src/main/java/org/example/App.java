package org.example;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

/**
 * @author Xiaobo Bi (869384236@qq.com)
 */
public class App {

    public static void main(String[] args) throws Exception {
        List<PltCountriesAvailable> list = DownloadPdf.downloadAndParse();
        List<PltCountriesAvailable> collect = list.stream()
                .sorted(Comparator.comparing(PltCountriesAvailable::getCountry))
                .collect(Collectors.toList());

        File file = new File("data.json");
        FileUtils.write(file, ConvertJson.toJson(collect), StandardCharsets.UTF_8);
    }
}
