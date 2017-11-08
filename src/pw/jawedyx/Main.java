package pw.jawedyx;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static String site;
    private static String title;

    public static void main(String[] args) {
        System.out.println(args[0]);
        System.out.println(args[1]);
        System.out.println(args[2]);

        Parser parser = new Parser();

        site = parser.getSite(args[1]);
        title = parser.getSiteTitle(site);

//        if(site.contains("<article")){
//            System.out.println("Есть тег статьи");
//        }else {
//            System.out.println("Нет тега статьи");
//        }


        System.out.println(title);

        Pattern pattern1 = Pattern.compile("<p\\b(.*?)>(.*?)</p>");
        Matcher matcher1 = pattern1.matcher(site);
        while (matcher1.find())
        {
            String paragraph = matcher1.group(2);
            //System.out.println(paragraph + "\n");

            Pattern hrefPattern = Pattern.compile("<a href=\"(.*?)\"(.*?)>(.*?)</a>"); //Искать ссылки в найденном параграфе
            Matcher hrefMatcher = hrefPattern.matcher(paragraph);

            while (hrefMatcher.find()){
                paragraph = paragraph.replace(hrefMatcher.group(0), hrefMatcher.group(3) + "[" + hrefMatcher.group(1) + "]");
            }
            System.out.println(paragraph + "\n");


        }



    }

}
