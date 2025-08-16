package org.ryuu;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class Main {
    public static void main(String[] args) {
        checkFiniteInBinary("0.1");
        checkFiniteInBinary("0.2");
        checkFiniteInBinary("0.3");
        checkFiniteInBinary("0.5");

        float floatResult = .1f + .2f;
        System.out.printf("floatResult = %.64f\n", floatResult);

        double doubleResult = .1 + .2;
        System.out.printf("doubleResult = %.64f\n", doubleResult);

        BigDecimal decimalResult = new BigDecimal("0.1").add(new BigDecimal("0.2"));
        System.out.printf("decimalResult = %.64f\n", decimalResult);

        doubleResult = .2 + .3;
        System.out.printf("doubleResult = %.64f\n", doubleResult);
    }

    private static String toBinary(BigDecimal num, int precision) {
        if (num.signum() < 0) {
            return "-" + toBinary(num.negate(), precision);
        }

        //region 整数部分
        //  原理：除 2 取余法
        BigDecimal integerPartBD = num.setScale(0, RoundingMode.DOWN);
        long integerPart = integerPartBD.longValue();
        BigDecimal fractionalPart = num.subtract(integerPartBD);
        String intStr = Long.toBinaryString(integerPart);
        //endregion

        //region 小数部分
        // 原理：乘 2 取整法
        // 小数部分为f，小数的每一位为bi，其中i表示小数的位
        // f=0.b1b2b3b4...
        // bi∈{0,1} bi∈{0,1}
        // bn的计算规则
        // bn=floor(f*2)
        // 如果f*2>=1,bn=1,f=f-1
        // 如果f*2<1,bn=0,f=f*2
        // 循环计算直至f=0，或者到指定的位停止
        StringBuilder fracStr = new StringBuilder();
        int count = 0;
        while (fractionalPart.signum() > 0 && count < precision) {
            fractionalPart = fractionalPart.multiply(BigDecimal.TWO);
            if (fractionalPart.compareTo(BigDecimal.ONE) >= 0) {
                fracStr.append("1");
                fractionalPart = fractionalPart.subtract(BigDecimal.ONE);
            } else {
                fracStr.append("0");
            }
            count++;
        }
        //endregion

        if (!fracStr.isEmpty()) {
            return intStr + "." + fracStr;
        } else {
            return intStr;
        }
    }

    private static void checkFiniteInBinary(String num) {
        boolean isFinite = isFiniteInBinary(num);
        String inBinary = toBinary(new BigDecimal(num), 64);
        if (isFinite) {
            System.out.println("十进制数" + num + "在二进制数下能有限表示。");
            System.out.println(inBinary);
        } else {
            System.out.println("十进制数" + num + "在二进制数下不能有限表示。");
            System.out.println(inBinary + "... (仅显示64位)");
        }
        System.out.println();
    }

    private static boolean isFiniteInBinary(String decimalStr) {
        BigDecimal decimal = new BigDecimal(decimalStr);
        return isFiniteInBinary(decimal);
    }

    /**
     * 定理：
     * 对于一个最简分数 p/q，它在进制b下能有限表示，当且仅当q的质因子都被b整除
     * <p>
     * 在二进制（b=2）下：分母 q 只包含质因子 2 → 有限二进制表示。
     * 在十进制（b=10）下：分母 q 只包含质因子 2 和 5 → 有限十进制表示。
     */
    private static boolean isFiniteInBinary(BigDecimal decimal) {
        // 转成分数：BigDecimal = unscaledValue / 10^scale
        BigInteger numerator = decimal.unscaledValue();
        BigInteger denominator = BigInteger.TEN.pow(decimal.scale());

        // 约分
        BigInteger gcd = numerator.gcd(denominator);
        denominator = denominator.divide(gcd);

        // 检查分母是否只含 2
        while (denominator.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            denominator = denominator.divide(BigInteger.TWO);
        }

        return denominator.equals(BigInteger.ONE);
    }
}