package pw.jawedyx;


public class Main {

    public static void main(String[] args) {

        if(args.length >= 1){

            System.out.println("Program'll take first argument.");
            System.out.println("Your link: " + args[0] + ".");
            Parser parser = new Parser();
            String site = parser.getSite(args[0]);
            parser.formatAndWriteSite(site);
            System.out.println("Enjoy the article: " + parser.getFilePath());

        }else {
            System.out.println("You need to give only one arg like Web-link with article.");
        }

    }

}
