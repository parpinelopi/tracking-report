import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.*;


public class Main {

    public Main() {
    }


    public static void main(String[] args) throws Exception {

        Scanner rangeInput = new Scanner(System.in);
        System.out.println("Select the date range for your report");


        String startDtTm;
        LocalDateTime dateTimeStart = LocalDateTime.now();
        LocalDateTime dateTimeEnd = LocalDateTime.now();
        LocalDateTime.now();
        LocalDateTime dateTimeParsed;
        boolean retry;
        do {
            System.out.println("input the start date and time in the following format yyyy-MM-dd HH:mm:ss");
            startDtTm = rangeInput.nextLine();

            try {
                DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.LENIENT);
                dateTimeStart = LocalDateTime.parse(startDtTm, dateTimeFormat);

                retry = false;
            } catch (DateTimeParseException e) {
                e.printStackTrace();
                retry = true;
                System.out.println("please enter a valid input");
            }
        } while (retry);

        String endDtTm;

        do {
            System.out.println("input the end date and time in the following format yyyy-MM-dd HH:mm:ss");
            endDtTm = rangeInput.nextLine();

            try {
                DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.LENIENT);
                dateTimeEnd = LocalDateTime.parse(endDtTm, dateTimeFormat);
                if (dateTimeEnd.isBefore(dateTimeStart)) {

                    System.out.println("End date cannot be before start date, please enter a valid end date");
                    retry = true;

                } else retry = false;
            } catch (DateTimeParseException e) {
                e.printStackTrace();
                retry = true;
                System.out.println("please enter a valid input");
            }
        } while (retry);


        File log = new File("./src/main/resources/log.txt");

        BufferedReader br = new BufferedReader(new FileReader(log));


        String string;
        br.readLine();
        Map<String, List<String>> trackingResultsPageViews = new HashMap<>();


        while ((string = br.readLine()) != null) {

            String[] stringParts = string.split("\\|");
            String dateTimePart = stringParts[1].replace("UTC", "").trim();
            String urlPart = stringParts[2].replace("|", "").trim();
            String userIDPart = stringParts[3].replace("|", "").trim();


            try {
                DateTimeFormatter dateTimeFormatParsed = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.LENIENT);
                dateTimeParsed = LocalDateTime.parse(dateTimePart, dateTimeFormatParsed);

                if ((dateTimeStart.isBefore(dateTimeParsed) || dateTimeStart.isEqual(dateTimeParsed)) && (dateTimeEnd.isAfter(dateTimeParsed) || dateTimeEnd.isEqual(dateTimeParsed))) {

                    List<String> checkListWithUrl;
                    checkListWithUrl = trackingResultsPageViews.get(urlPart);

                    if (checkListWithUrl == null) {
                        List<String> singleVisitor = new ArrayList<>();
                        singleVisitor.add(userIDPart);
                        trackingResultsPageViews.put(urlPart, singleVisitor);

                    } else {
                        List<String> addUserToRelatedUrl = new ArrayList<>();
                        addUserToRelatedUrl.addAll(checkListWithUrl);
                        addUserToRelatedUrl.add(userIDPart);
                        trackingResultsPageViews.put(urlPart, addUserToRelatedUrl);
                    }
                }

            } catch (DateTimeParseException e) {
                e.printStackTrace();
            }
        }

        String titleTemplate = "%-20s %10s %9s%n";
        String template = "%-20s %10s %9s%n";
        System.out.printf(titleTemplate, "|url", "|page views", "|visitors|");

        for (Map.Entry<String, List<String>> entry :
                trackingResultsPageViews.entrySet()) {
            int pageViews = entry.getValue().size();
            Set<String> set = new HashSet<>();
            for (String userID : entry.getValue())
                set.add(userID);
            System.out.printf(template, entry.getKey(), pageViews, set.size());

        }

    }

}
