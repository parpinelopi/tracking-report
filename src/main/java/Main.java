import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.*;


public class Main {

    static boolean retry;

    public static void main(String[] args) throws Exception {

        Scanner rangeInput = new Scanner(System.in);
        System.out.println("Select the date range for your report");


        String startDtTm;
        String endDtTm;
        LocalDateTime dateTimeStart;
        LocalDateTime dateTimeEnd;
        LocalDateTime.now();
        LocalDateTime dateTimeParsed = LocalDateTime.now();

        do {
            System.out.println("input the start date and time in the following format yyyy-MM-dd HH:mm:ss");
            startDtTm = rangeInput.nextLine();

            dateTimeStart = parseDateTimeByUser(startDtTm);

        } while (retry);


        do {
            System.out.println("input the end date and time in the following format yyyy-MM-dd HH:mm:ss");
            endDtTm = rangeInput.nextLine();
            dateTimeEnd = parseDateTimeByUser(endDtTm);
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

            createResults(dateTimePart, userIDPart, urlPart, dateTimeStart, dateTimeEnd, dateTimeParsed, trackingResultsPageViews);

        }

        printResults(trackingResultsPageViews);

    }

    static LocalDateTime parseDateTimeByUser(String dateTimeInput) {
        LocalDateTime dateTimeFormatted = LocalDateTime.now();

        try {
            DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.LENIENT);
            dateTimeFormatted = LocalDateTime.parse(dateTimeInput, dateTimeFormat);

            retry = false;
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            retry = true;
            System.out.println("please enter a valid input");
        }

        return dateTimeFormatted;
    }

    static LocalDateTime createResults(String dateTimeString, String userString, String urlString, LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDateTime parsedDateTime, Map<String, List<String>> trackPageViews) {

        try {
            DateTimeFormatter dateTimeFormatParsed = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.LENIENT);
            parsedDateTime = LocalDateTime.parse(dateTimeString, dateTimeFormatParsed);

            if ((startDateTime.isBefore(parsedDateTime) || startDateTime.isEqual(parsedDateTime)) && (endDateTime.isAfter(parsedDateTime) || endDateTime.isEqual(parsedDateTime))) {

                List<String> checkListWithUrl;
                checkListWithUrl = trackPageViews.get(urlString);

                if (checkListWithUrl == null) {
                    List<String> singleVisitor = new ArrayList<>();
                    singleVisitor.add(userString);
                    trackPageViews.put(urlString, singleVisitor);

                } else {
                    List<String> addUserToRelatedUrl = new ArrayList<>();
                    addUserToRelatedUrl.addAll(checkListWithUrl);
                    addUserToRelatedUrl.add(userString);
                    trackPageViews.put(urlString, addUserToRelatedUrl);
                }
            }

        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }

        return parsedDateTime;
    }

    static Map printResults(Map<String, List<String>> trackPageViewMap) {

        String titleTemplate = "%-20s %10s %9s%n";
        String template = "%-20s %10s %9s%n";
        System.out.printf(titleTemplate, "|url", "|page views", "|visitors|");

        for (Map.Entry<String, List<String>> entry :
                trackPageViewMap.entrySet()) {
            int pageViews = entry.getValue().size();
            Set<String> set = new HashSet<>();
            for (String userID : entry.getValue())
                set.add(userID);
            System.out.printf(template, entry.getKey(), pageViews, set.size());

        }
        return trackPageViewMap;
    }
}
