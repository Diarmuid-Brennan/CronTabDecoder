import java.lang.annotation.Retention;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CronTabDecoder {
    public static void main(String[] args) {


        if(args.length != 5){
            System.out.println("Invalid number of values sent");
            return;
        }
        String[] globalValidChars = new String[]{"*", ",", "-"};

        String minute = "";
        String hour = "";
        String dayOfMonth = "";
        String month = "";
        String dayOfWeek = "";

        for (int i = 0; i < args.length; i++) {
            String testString = args[i].replaceAll("\\\\", "");
            System.out.println(testString);
            switch (i) {
                case 0:
                    if (validateMinute(testString)) {
                        minute = decodeMinute(testString);
                    } else {
                        System.out.println(testString + " is an invalid value for minute");
                        break;
                    }
                    break;
                case 1:
                    if (validateHour(testString)) {
                        hour = decodeHour(testString);
                    } else {
                        System.out.println(testString + " is an invalid value for hour");
                        break;
                    }
                    break;
                case 2:
                    if (validateDayOfMonth(testString)) {
                        dayOfMonth = decodeDayOfMonth(testString);
                    } else {
                        System.out.println(testString + " is an invalid value for day of Month");
                        break;
                    }
                    break;
                case 3:
                    if (validateMonth(testString)) {
                        month = decodeMonth(testString);
                    } else {
                        System.out.println(testString + " is an invalid value for Month");
                        break;
                    }
                    break;
                case 4:
                    if (validateDayOfWeek(testString)) {
                        dayOfWeek = decodeDayOfWeek(testString);
                    } else {
                        System.out.println(testString + " is an invalid value for day of week");
                        break;
                    }
                    break;
                default:
                    System.out.println("Unknown argument: " + testString);
                    return;
            }

        }
        System.out.println(minute);
        System.out.println(hour);
        System.out.println(dayOfWeek);
        System.out.println(month);
        System.out.println(dayOfMonth);

}

    private static String decodeDayOfWeek(String arg) {

        String dayOfWeekRegex = "^^([0-6]|SUN|MON|TUE|WED|THU|FRI|SAT)(-([0-6]|SUN|MON|TUE|WED|THU|FRI|SAT))?" +
                "(,([0-6]|SUN|MON|TUE|WED|THU|FRI|SAT)(-([0-6]|SUN|MON|TUE|WED|THU|FRI|SAT))?)*$\n";
        String result = "";
        String cronDigitRegex = "^([0-6]|SUN|MON|TUE|WED|THU|FRI|SAT)$";
        Pattern pattern = Pattern.compile(cronDigitRegex);
        Matcher digitmatcher = pattern.matcher(arg);

        String lastDayDigitRegex = "^([0-6]L?)$";
        Pattern lastDaypattern = Pattern.compile(lastDayDigitRegex);
        Matcher lastDaydigitmatcher = lastDaypattern.matcher(arg);

        String cronListRegex = "^([0-6]|SUN|MON|TUE|WED|THU|FRI|SAT)" +
                "(,[0-6]|SUN|MON|TUE|WED|THU|FRI|SAT)*$";
        Pattern listPattern = Pattern.compile(cronListRegex);
        Matcher listmatcher = listPattern.matcher(arg);

        String cronRangeRegex = "^([0-6]|SUN|MON|TUE|WED|THU|FRI|SAT-[0-6]|SUN|MON|TUE|WED|THU|FRI|SAT)" +
                "(,([0-6]|SUN|MON|TUE|WED|THU|FRI|SAT-[0-6]|SUN|MON|TUE|WED|THU|FRI|SAT))*$";
        Pattern rangePattern = Pattern.compile(cronRangeRegex);
        Matcher rangematcher = rangePattern.matcher(arg);

        Map<String, String> dayMap = new HashMap<>();

        // Populate the map with month abbreviations and their corresponding values
        dayMap.put( "0", "MON");
        dayMap.put( "1", "TUE");
        dayMap.put( "2", "WED");
        dayMap.put( "3", "THU");
        dayMap.put( "4", "FRI");
        dayMap.put( "5", "SAT");
        dayMap.put( "6", "SUN");



        if (arg.equals("*")) {
            result = "Every day ";
        }else if(arg.equals("L")){
            result = "The last day of every week";
        }else if(lastDaydigitmatcher.matches()){
            arg = convertDay(Character.toString(arg.charAt(0)), dayMap);
            result = "The last " + arg + " of every week";
        } else if (digitmatcher.matches()) {
            arg = convertDay(arg, dayMap);
            result = "On " + arg;
        } else if (listmatcher.matches()) {
            result = "On days ";
            String[] items = arg.split(",");

            for (String item : items) {
                result += convertDay(arg, dayMap) + ",";
            }
            result = result.substring(0, result.length() - 1);
        } else if (rangematcher.matches()) {
            result = "Every day between the days of ";
            String[] items = arg.split(",");

            for (int j = 0; j < items.length; j++) {
                String[] rangeItems = items[j].split("-");
                for (int i = 0; i < rangeItems.length; i++) {
                    if (i == rangeItems.length -1) {
                        result += convertDay(rangeItems[i], dayMap);
                    } else {
                        result += convertDay(rangeItems[i], dayMap) + " and ";
                    }

                }
                if (j < items.length -1) {
                    result += ", ";
                }
            }
        }
        return result;
    }

    private static String convertDay(String arg, Map<String, String> dayMap) {
        if(dayMap.containsKey(arg)){
            arg = dayMap.get(arg);
        }
        return arg;
    }

    private static String decodeMonth(String arg) {
        String result = "";
        String cronDigitRegex = "^([1-9]|1[0-2]|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)$";
        Pattern pattern = Pattern.compile(cronDigitRegex);
        Matcher digitmatcher = pattern.matcher(arg);

        String cronListRegex = "^([1-9]|1[0-2]|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)" +
                "(,[1-9]|1[0-2]|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)*$";
        Pattern listPattern = Pattern.compile(cronListRegex);
        Matcher listmatcher = listPattern.matcher(arg);

        String cronRangeRegex = "^([1-9]|1[0-2]|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC-[1-9]|1[0-2]|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC?)" +
                "(,([1-9]|1[0-2]|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC-[1-9]|1[0-2]|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))*$";
        Pattern rangePattern = Pattern.compile(cronRangeRegex);
        Matcher rangematcher = rangePattern.matcher(arg);

        String cronStepRegex = "^(\\*|[1-9]|1[0-2]|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)/" +
                "[1-9]|1[0-2]|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC$";
        Pattern stepPattern = Pattern.compile(cronStepRegex);
        Matcher stepmatcher = stepPattern.matcher(arg);

        Map<String, String> monthMap = new HashMap<>();

        // Populate the map with month abbreviations and their corresponding values
        monthMap.put( "1", "JAN");
        monthMap.put( "2", "FEB");
        monthMap.put( "3", "MAR");
        monthMap.put( "4", "APR");
        monthMap.put( "5", "MAY");
        monthMap.put( "6", "JUN");
        monthMap.put( "7", "JUL");
        monthMap.put( "8", "AUG");
        monthMap.put( "9", "SEP");
        monthMap.put( "10", "OCT");
        monthMap.put( "11", "NOV");
        monthMap.put( "12", "DEC");

        if (arg.equals("*")) {
            result = "Every month";
        } else if (digitmatcher.matches()) {
            arg = convertMonth(arg, monthMap);
            result = "In month " + arg;
        } else if (listmatcher.matches()) {
            arg = convertMonthList(arg, monthMap);
            result = "For months " + arg;
        } else if (rangematcher.matches()) {
            result = "Every month between the months of ";
            String[] items = arg.split(",");

            for (int j = 0; j < items.length; j++) {
                String[] rangeItems = items[j].split("-");
                for (int i = 0; i < rangeItems.length; i++) {
                    if (i == rangeItems.length -1) {
                        result += convertMonth(rangeItems[i], monthMap);
                    } else {
                        result += convertMonth(rangeItems[i], monthMap) + " and ";
                    }

                }
                if (j < items.length -1) {
                    result += ", ";
                }
            }
        } else if (stepmatcher.matches()) {
            String[] stepItems = arg.split("/");
            result = "Starting from month " + convertMonth(stepItems[0], monthMap) + " and every following "
                    + convertMonth(stepItems[1], monthMap) + " months after that";;

        }
        return result;
    }

    private static String convertMonth(String arg, Map<String, String> monthMap) {
        if(monthMap.containsKey(arg)){
            arg = monthMap.get(arg);
        }
        return arg;
    }

    private static String convertMonthList(String arg, Map<String, String> monthMap) {
        String[] listItems = arg.split(",");
        String decodeArg = "";
        for(String item : listItems){
            if(monthMap.containsKey(arg)){
                decodeArg += monthMap.get(arg) +", ";
            }else{
                decodeArg += arg + ", ";
            }

        }
        return decodeArg.substring(0, decodeArg.length()-2);
    }

    private static String decodeDayOfMonth(String arg) {
        String result = "";
        String cronDigitRegex = "^([12]?\\d|3[01])$";
        Pattern pattern = Pattern.compile(cronDigitRegex);
        Matcher digitmatcher = pattern.matcher(arg);

        String cronListRegex = "^([12]?\\d|3[01])(,[12]?\\d|3[01])*$";
        Pattern listPattern = Pattern.compile(cronListRegex);
        Matcher listmatcher = listPattern.matcher(arg);

        String cronRangeRegex = "^([12]?\\d|3[01]-[12]?\\d|3[01])(,([12]?\\d|3[01]-[12]?\\d|3[01]))*$";
        Pattern rangePattern = Pattern.compile(cronRangeRegex);
        Matcher rangematcher = rangePattern.matcher(arg);

        String cronStepRegex = "^(\\*|[12]?\\d|3[01]-[12])/[12]?\\d|3[01]-[12]$";
        Pattern stepPattern = Pattern.compile(cronStepRegex);
        Matcher stepmatcher = stepPattern.matcher(arg);

        if (arg.equals("*")) {
            result = "Every day";
        } else if (digitmatcher.matches()) {
            result = "On day " + arg + " of the month";
        } else if (listmatcher.matches()) {
            result = "On days ";
            String[] items = arg.split(",");

            for (String item : items) {
                result += item + ",";
            }
            result = result.substring(0, result.length() - 1);
        } else if (rangematcher.matches()) {
            result = "On the days between day ";
            String[] items = arg.split(",");

            for (int j = 0; j < items.length; j++) {
                String[] rangeItems = items[j].split("-");
                for (int i = 0; i < rangeItems.length; i++) {
                    if (i == rangeItems.length -1) {
                        result += rangeItems[i];
                    } else {
                        result += rangeItems[i] + " and ";
                    }

                }
                if (j < items.length -1) {
                    result += ", ";
                }
            }
        } else if (stepmatcher.matches()) {
            String[] stepItems = arg.split("/");
            result = "Starting from day " + stepItems[0] + "  and every following "
                    + stepItems[1] + " days after that";;

        }
        return result;
    }

    private static String decodeHour(String arg) {
        String result = "";
        String cronDigitRegex = "^([01]?\\d|2[0-3])$";
        Pattern pattern = Pattern.compile(cronDigitRegex);
        Matcher digitmatcher = pattern.matcher(arg);

        String cronListRegex = "^([01]?\\d|2[0-3])(,[01]?\\d|2[0-3])*$";
        Pattern listPattern = Pattern.compile(cronListRegex);
        Matcher listmatcher = listPattern.matcher(arg);

        String cronRangeRegex = "^([01]?\\d|2[0-3])(,([01]?\\d|2[0-3]-[01]?\\d|2[0-3]))*$";
        Pattern rangePattern = Pattern.compile(cronRangeRegex);
        Matcher rangematcher = rangePattern.matcher(arg);

        String cronStepRegex = "^(\\*|[01]?\\d|2[0-3])/[01]?\\d|2[0-3]?\\d$";
        Pattern stepPattern = Pattern.compile(cronStepRegex);
        Matcher stepmatcher = stepPattern.matcher(arg);

        if (arg.equals("*")) {
            result = "Every hour";
        } else if (digitmatcher.matches()) {
            result = "At hour" + arg;
        } else if (listmatcher.matches()) {
            result = "At hours ";
            String[] items = arg.split(",");

            for (String item : items) {
                result += item + ",";
            }
            result = result.substring(0, result.length() - 1);
        } else if (rangematcher.matches()) {
            result = "On the hour between the times of ";
            String[] items = arg.split(",");

            for (int j = 0; j < items.length; j++) {
                String[] rangeItems = items[j].split("-");
                for (int i = 0; i < rangeItems.length; i++) {
                    if (i == rangeItems.length -1) {
                        result += rangeItems[i];
                    } else {
                        result += rangeItems[i] + " and ";
                    }

                }
                if (j < items.length -1) {
                    result += ", ";
                }
            }
        } else if (stepmatcher.matches()) {
            String[] stepItems = arg.split("/");
            result = "Starting from hour " + stepItems[0] + "  and every following "
                    + stepItems[1] + " hours after that";;

        }
        return result;
    }

    private static String decodeMinute(String arg) {
        String result = "";
        String cronDigitRegex = "^(\\d|[1-5]\\d)$";
        Pattern pattern = Pattern.compile(cronDigitRegex);
        Matcher digitmatcher = pattern.matcher(arg);

        String cronListRegex = "^(\\d|[1-5]\\d)(,\\d|[1-5]\\d)*$";
        Pattern listPattern = Pattern.compile(cronListRegex);
        Matcher listmatcher = listPattern.matcher(arg);

        String cronRangeRegex = "^([0-5]?\\d-[0-5]?\\d)(,([0-5]?\\d-[0-5]?\\d))*$";
        Pattern rangePattern = Pattern.compile(cronRangeRegex);
        Matcher rangematcher = rangePattern.matcher(arg);

        String cronStepRegex = "^(\\*|[0-5]?\\d)/[1-5]?\\d$";
        Pattern stepPattern = Pattern.compile(cronStepRegex);
        Matcher stepmatcher = stepPattern.matcher(arg);

        if (arg.equals("*")) {
            result = "Every minute";
        } else if (digitmatcher.matches()) {
            result = "At minute" + arg;
        } else if (listmatcher.matches()) {
            result = "At minutes ";
            String[] items = arg.split(",");

            for (String item : items) {
                result += item + ",";
            }
            result = result.substring(0, result.length() - 1);
        } else if (rangematcher.matches()) {
            result = "Every minute between the times of ";
            String[] items = arg.split(",");

            for (int j = 0; j < items.length; j++) {
                String[] rangeItems = items[j].split("-");
                for (int i = 0; i < rangeItems.length; i++) {
                    if (i == rangeItems.length -1) {
                        result += rangeItems[i];
                    } else {
                        result += rangeItems[i] + " and ";
                    }

                }
                if (j < items.length -1) {
                    result += ", ";
                }
            }
        } else if (stepmatcher.matches()) {
            String[] stepItems = arg.split("/");
            result = "Starting from " + stepItems[0] + " minutes past the hour and every following "
            + stepItems[1] + " minutes after that";;

        }
        return result;
    }


    private static boolean validateMinute(String minute){
        String minuteRegex = "^(\\*|([0-5]?\\d)(/([1-9]|[1-5]\\d))?|([0-5]?\\d(-[0-5]?\\d)?)(,([0-5]?\\d(-[0-5]?\\d)?)*)?)$";
        Pattern pattern = Pattern.compile(minuteRegex);

        Matcher matcher = pattern.matcher(minute);
        return matcher.matches();

    }

    private static boolean validateHour(String hour){
        String hourRegex = "^(\\*|([01]?\\d|2[0-3])(/([01]?\\d|2[0-3]))?|(([01]?\\d|2[0-3])(-([01]?\\d|2[0-3]))?)(,(([01]?\\d|2[0-3])(-([01]?\\d|2[0-3]))?)*)?)$";
        Pattern pattern = Pattern.compile(hourRegex);

        Matcher matcher = pattern.matcher(hour);
        return matcher.matches();
    }

    private static boolean validateDayOfMonth(String dayOfMonth){
        String dayOfMonthRegex = "^(\\*|([12]?\\d|3[01])(/([12]?\\d|3[01]))?|(([12]?\\d|3[01])(-([12]?\\d|3[01]))?)(,(([12]?\\d|3[01])(-([12]?\\d|3[01]))?)*)?)$";
        Pattern pattern = Pattern.compile(dayOfMonthRegex);

        Matcher matcher = pattern.matcher(dayOfMonth);
        return matcher.matches();
    }

    private static boolean validateMonth(String month){
        String monthRegex = "^([1-9]|1[0-2]|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)" +
                "(-([1-9]|1[0-2]|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?" +
                "(,([1-9]|1[0-2]|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)" +
                "(-([1-9]|1[0-2]|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?)*(/([1-9]|1[0-2]))?$";
        Pattern pattern = Pattern.compile(monthRegex);

        Matcher matcher = pattern.matcher(month);
        return matcher.matches();
    }

    private static boolean validateDayOfWeek(String dayOfWeek){
        String dayOfWeekRegex = "^L|\\?|([0-6]L?|SUN|MON|TUE|WED|THU|FRI|SAT)(-([0-6]|SUN|MON|TUE|WED|THU|FRI|SAT))?" +
                "(,([0-6]|SUN|MON|TUE|WED|THU|FRI|SAT)(-([0-6]|SUN|MON|TUE|WED|THU|FRI|SAT))?)*$";
        Pattern pattern = Pattern.compile(dayOfWeekRegex);

        Matcher matcher = pattern.matcher(dayOfWeek);
        return matcher.matches();
    }
}
import java.util.regex.*;
        import java.util.*;

public class CronTabDecoder {

    private static final Pattern minutePattern = Pattern.compile("^(\\*|([0-5]?\\d)(/([1-9]|[1-5]\\d))?|([0-5]?\\d(-[0-5]?\\d)?)(,([0-5]?\\d(-[0-5]?\\d)?)*)?)$");
    private static final Pattern hourPattern = Pattern.compile("^(\\*|([01]?\\d|2[0-3])(/([01]?\\d|2[0-3]))?|(([01]?\\d|2[0-3])(-([01]?\\d|2[0-3]))?)(,(([01]?\\d|2[0-3])(-([01]?\\d|2[0-3]))?)*)?)$");
    private static final Pattern dayOfMonthPattern = Pattern.compile("^(\\*|([12]?\\d|3[01])(/([12]?\\d|3[01]))?|(([12]?\\d|3[01])(-([12]?\\d|3[01]))?)(,(([12]?\\d|3[01])(-([12]?\\d|3[01]))?)*)?)$");
    private static final Pattern monthPattern = Pattern.compile("^([1-9]|1[0-2]|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(-([1-9]|1[0-2]|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?(,([1-9]|1[0-2]|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(-([1-9]|1[0-2]|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?)*(/([1-9]|1[0-2]))?$");
    private static final Pattern dayOfWeekPattern = Pattern.compile("^L|\\?|([0-6]L?|SUN|MON|TUE|WED|THU|FRI|SAT)(-([0-6]|SUN|MON|TUE|WED|THU|FRI|SAT))?(,([0-6]|SUN|MON|TUE|WED|THU|FRI|SAT)(-([0-6]|SUN|MON|TUE|WED|THU|FRI|SAT))?)*$");

    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("Invalid number of values sent");
            return;
        }

        String[] cronFields = {"Minute", "Hour", "Day of Month", "Month", "Day of Week"};
        String[] decodedFields = new String[5];

        for (int i = 0; i < args.length; i++) {
            String cronValue = args[i].replaceAll("\\\\", "");
            decodedFields[i] = processCronField(i, cronValue);
        }

        for (int i = 0; i < decodedFields.length; i++) {
            System.out.println(cronFields[i] + ": " + decodedFields[i]);
        }
    }

    private static String processCronField(int fieldIndex, String cronValue) {
        switch (fieldIndex) {
            case 0: return validateAndDecode(cronValue, minutePattern, CronField.MINUTE);
            case 1: return validateAndDecode(cronValue, hourPattern, CronField.HOUR);
            case 2: return validateAndDecode(cronValue, dayOfMonthPattern, CronField.DAY_OF_MONTH);
            case 3: return validateAndDecode(cronValue, monthPattern, CronField.MONTH);
            case 4: return validateAndDecode(cronValue, dayOfWeekPattern, CronField.DAY_OF_WEEK);
            default: return "Unknown field";
        }
    }

    private static String validateAndDecode(String cronValue, Pattern pattern, CronField cronField) {
        if (!pattern.matcher(cronValue).matches()) {
            return "Invalid " + cronField.fieldName;
        }
        return decodeCronField(cronValue, cronField);
    }

    private static String decodeCronField(String cronValue, CronField cronField) {
        if (cronValue.equals("*")) {
            return "Every " + cronField.fieldName.toLowerCase();
        }

        if (cronValue.contains(",")) {
            return "On " + cronField.fieldName.toLowerCase() + "s " + String.join(", ", cronValue.split(","));
        } else if (cronValue.contains("-")) {
            return "Between " + cronValue.replace("-", " and ");
        } else if (cronValue.contains("/")) {
            return "Starting from " + cronValue.split("/")[0] + " every " + cronValue.split("/")[1] + " " + cronField.fieldName.toLowerCase();
        } else {
            return "At " + cronValue;
        }
    }

    enum CronField {
        MINUTE("Minute"), HOUR("Hour"), DAY_OF_MONTH("Day of Month"), MONTH("Month"), DAY_OF_WEEK("Day of Week");

        private final String fieldName;
        CronField(String fieldName) {
            this.fieldName = fieldName;
        }
    }
}



