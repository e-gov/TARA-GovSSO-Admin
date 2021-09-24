package ee.ria.tara.service.helper;

import java.time.DateTimeException;
import java.time.LocalDate;

public class NationalIdCodeValidator {

    private static int[] MULTIPLIERS1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 1};
    private static int[] MULTIPLIERS2 = {3, 4, 5, 6, 7, 8, 9, 1, 2, 3};

    public static boolean isValid(String idCode) {
        if (idCode == null || idCode.length() != 11) {
            return false;
        }
        try {
            int controlDigit = Integer.parseInt(idCode.substring(10));

            if (controlDigit != calculateControlDigit(idCode)) {
                return false;
            }
        }
        catch (NumberFormatException numberFormatException) {
            return false;
        }

        try {
            getBirthDate(idCode);
        } catch (DateTimeException ex) {
            return false;
        }
        return true;
    }

    public static int calculateControlDigit(String idCode) {

        int mod = multiplyDigits(idCode, MULTIPLIERS1);
        if (mod == 10) {
            mod = multiplyDigits(idCode, MULTIPLIERS2);
        }
        return mod%10;
    }

    private static int multiplyDigits(String code, int[] multipliers) {
        int total = 0;

        for (int i = 0; i < 10; i++) {
            total += Integer.parseInt(code.charAt(i)+"") * multipliers[i];
        }
        return total % 11;
    }


    public static LocalDate getBirthDate(String idCode) {
        int year = Integer.parseInt(idCode.substring(1, 3));
        int month = Integer.parseInt(idCode.substring(3, 5));
        int dayOfMonth = Integer.parseInt(idCode.substring(5, 7));

        int firstNumber = Integer.parseInt(idCode.substring(0, 1));

        switch (firstNumber) {
            case 5:
            case 6:
                year += 2000;
                break;
            case 3:
            case 4:
                year += 1900;
                break;
            default:
                year += 1800;
        }
        return LocalDate.of(year, month, dayOfMonth);
    }

}
