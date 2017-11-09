package pw.jawedyx;

import org.apache.commons.lang3.text.WordUtils;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Parser {

    private static TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
            public void checkServerTrusted(X509Certificate[] certs, String authType) { }
        }
    };

    private File file;
    private FileWriter writer;
    private String urlArgument;

    public String getSite(String urlAddress){

        urlArgument = urlAddress;

        try {

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HostnameVerifier allHostsValid = (hostname, session) -> true;

            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            URL url = new URL(urlAddress);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            String charset = urlConnection.getContentType().split("charset=")[1]; //Кодировка страницы

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            return buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    public String getSiteTitle(String site){

        Pattern pattern = Pattern.compile("<title>(.*)</title>");
        Matcher matcher = pattern.matcher(site);
        if (matcher.find())
        {
            return matcher.group(1);
        }

        return "";
    }


    public void formatAndWriteSite(String site) {

        file = new File(createDirs());
        file.getParentFile().mkdirs();

        try{
            writer = new FileWriter(file.getAbsolutePath(), false);
        }catch (IOException e){
            e.printStackTrace();
        }

        Pattern pattern1 = Pattern.compile("<div.*>(.*)</div>");
        Matcher matcher1 = pattern1.matcher(site);
        while (matcher1.find())
        {
            String div = matcher1.group(0);

            /*Поиск заголовков h1 или абзацев, заканчивающихся точкой.
            В заголовках, как правило, точка не ставится.
            Прочие заголовки пропускаются.
            */
            Pattern paragraphPattern = Pattern.compile("(<h1.*?>[^>]+</h1>)|(<p\\b.*?>.*?\\.?</p>)");
            Matcher paragraphMatcher = paragraphPattern.matcher(div);

            while (paragraphMatcher.find()){

                String foundFragment = paragraphMatcher.group();

                //Поиск и обработка ссылок
                Pattern hrefPattern = Pattern.compile("<a href=\"(.*?)\"(.*?)>(.*?)</a>");
                Matcher hrefMatcher = hrefPattern.matcher(foundFragment);

                while (hrefMatcher.find()){
                    foundFragment = foundFragment.replace(hrefMatcher.group(0), hrefMatcher.group(3) + "[" + hrefMatcher.group(1) + "]");
                }

                //Форматирование html-сущностей
                foundFragment = foundFragment.replace("&raquo;", "»");
                foundFragment = foundFragment.replace("&laquo;", "«");
                foundFragment = foundFragment.replace("&ndash;", "–");
                foundFragment = foundFragment.replace("&mdash;", "—");

                //Удаление лишнего мусора
                String trimBeforeParagraphEnds = foundFragment.substring(0, foundFragment.length()-4).trim();

                if(foundFragment.startsWith("<h1")  || ( foundFragment.startsWith("<p") && trimBeforeParagraphEnds.endsWith("."))){

                    //Запись в файл
                    try {
                        writer.write(WordUtils.wrap(foundFragment.split("<.*?>")[1], Config.WORD_WRAP));
                        writer.append("\n\n");
                        writer.flush();
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                }

            }

        }

    }

    public String getFilePath(){
        return (file != null)? file.getAbsolutePath() : "Invalid file";
    }

    //Усложнение 1
    private String createDirs() {

        String[] urls = urlArgument.split("/");
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < urls.length; i++){
            sb.append(urls[i] + "\\");
        }

        if(!urls[urls.length-1].contains(".")){
            sb.append(Config.DEFAULT_FILE_NAME);
        }else{
            int lastDot = sb.lastIndexOf(".");
            sb.replace(lastDot, sb.length(), ".txt");
        }

        return sb.substring(1, sb.length());
    }


}
