package ua.ukrpost.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

public class MoneyToTextConverter {
    private String[] strBase = {"", "одна", "дві", "три", "чотири", "п'ять", "шість", "сім", "вісім", "дев'ять"};
    private String[] str100 = {"", "сто", "двісті", "триста", "чотириста", "п'ятсот", "шістьсот",
            "сімсот", "вісімсот", "дев'ятсот"};
    private String[] str11 = {"", "десять", "одинадцять", "дванадцять", "тринадцять", "чотирнадцять", "п'ятнадцять",
            "шістнадцять", "сімнадцять", "вісімнадцять", "дев'ятнадцять", "двадцять"};
    private String[] str10 = {"", "десять", "двадцать", "тридцять", "сорок", "п'ятдесят", "шістдесят",
            "сімдесят", "вісімьдесят", "дев'яносто"};
    private String[][] forms = {
            {"копійка", "копійки", "копійок", "1"},
            {"гривня", "гривні", "гривень", "0"},
            {"тысяча", "тысячи", "тысяч", "1"},
            {"мільйон", "мільйона", "мільйонів", "0"},
            {"мильярд", "мільярда", "мільярдів", "0"},
    };

    public String convert(BigDecimal amount, boolean stripKopiyky) {
        // Separating hryvnas and kopiykas
        long hryvnasLong = amount.longValue();
        if (hryvnasLong > 999_999_999_999L) return (amount.toPlainString());
        String[] splittedAmount = amount.toString().split("\\.");
        long kopiykyLong = 0;
        if (splittedAmount.length > 1) {
            kopiykyLong = Long.valueOf(splittedAmount[1]);
            if (!splittedAmount[1].substring(0, 1).equals("0")) { //doesn't start with zero
                if (kopiykyLong < 10)
                    kopiykyLong *= 10;
            }
        }
        String kopiykyString = String.valueOf(kopiykyLong);
        if (kopiykyString.length() == 1)
            kopiykyString = "0" + kopiykyString;
        long hryvnas_temp = hryvnasLong;
        // Split the sum to segments with 3 digits
        ArrayList segments = new ArrayList();
        while (hryvnas_temp > 999) {
            long seg = hryvnas_temp / 1000;
            segments.add(hryvnas_temp - (seg * 1000));
            hryvnas_temp = seg;
        }
        segments.add(hryvnas_temp);
        Collections.reverse(segments);
        // Analyzing segments
        String output = "";
        if (hryvnasLong == 0) { // if zero
            output = "нуль " + morph(0, forms[1][0], forms[1][1], forms[1][2]);
            if (stripKopiyky)
                return output;
            else
                return output + " " + kopiykyLong + " " + morph(kopiykyLong, forms[0][0], forms[0][1], forms[0][2]);
        }
        // More than zero
        int lev = segments.size();
        for (int i = 0; i < segments.size(); i++) { // looking on segments
            int currentSegment = Integer.valueOf(segments.get(i).toString());// current segment
            if (currentSegment == 0 && lev > 1) { // is segment ==0 and not the last level
                lev--;
                continue;
            }
            String rs = String.valueOf(currentSegment); // number in line
            // Normalize
            if (rs.length() == 1) rs = "00" + rs;// two zeros into the prefix
            if (rs.length() == 2) rs = "0" + rs; // or one
            // getting digits for analyzing
            int firstDigit = Integer.valueOf(rs.substring(0, 1)); //first
            int secondDigit = Integer.valueOf(rs.substring(1, 2)); //second
            int thirdDigit = Integer.valueOf(rs.substring(2, 3)); //third
            int secondAndThirdDigits = Integer.valueOf(rs.substring(1, 3)); //second and third
            // Analyze digits
            if (currentSegment > 99) output += str100[firstDigit] + " "; // hundreds
            if (secondAndThirdDigits > 20) { // >20
                output += str10[secondDigit] + " ";
                output += strBase[thirdDigit] + " ";
            } else { // <=20
                if (secondAndThirdDigits > 9) output += str11[secondAndThirdDigits - 9] + " "; // 10-20
                else output += strBase[thirdDigit] + " "; // 0-9
            }
            // Units of measure
            output += morph(currentSegment, forms[lev][0], forms[lev][1], forms[lev][2]) + " ";
            lev--;
        }
        // Kopiyky numerical value
        if (stripKopiyky) {
            output = output.replaceAll(" {2,}", " ");
        } else {
            output = output + "" + kopiykyString + " " + morph(kopiykyLong, forms[0][0], forms[0][1], forms[0][2]);
            output = output.replaceAll(" {2,}", " ");
        }
        return output;
    }

    private static String morph(long n, String f1, String f2, String f5) {
        n = Math.abs(n) % 100;
        long n1 = n % 10;
        if (n > 10 && n < 20) return f5;
        if (n1 > 1 && n1 < 5) return f2;
        if (n1 == 1) return f1;
        return f5;
    }
}
