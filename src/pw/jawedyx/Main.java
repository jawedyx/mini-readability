package pw.jawedyx;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static String site;

    public static void main(String[] args) {
        System.out.println(args[0]);
        System.out.println(args[1]);
        System.out.println(args[2]);

        Parser parser = new Parser();

        site = parser.getSite(args[2]);

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

                //Удаление лишнего мусора
                String trimBeforeParagraphEnds = foundFragment.substring(0, foundFragment.length()-4).trim();

                if(foundFragment.startsWith("<h1")  || ( foundFragment.startsWith("<p") && trimBeforeParagraphEnds.endsWith("."))){
                    System.out.println(foundFragment.split("<.*?>")[1] + "\n");
                }

            }

        }

    }

}
