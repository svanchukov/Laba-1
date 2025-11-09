package org.example;

import java.math.BigInteger;
import java.util.*;

public class ElGamal {
    public static void main(String[] args) {
        String[] texts = {
                "ВАНЧУКОВ АЛЕКСАНДР ВЯЧЕСЛАВОВИЧ",
                "НЕ ПЛАЧЬ ДЕВЧОНКА ПРОЙДУТ ДОЖДИ СОЛДАТ ВЕРНЕТСЯ ТЫ ТОЛЬКО ЖДИ",
                "ОБРАТНАЯ СТОРОНА ЛУНЫ",
                "ЭЛЬ ГАМАЛЬ УСОВЕРШЕНСТВОВАЛ СИСТЕМУ ДИФФИ ХЕЛЛМАНА И ПОЛУЧИЛ ДВА АЛГОРИТМА"
        };

        BigInteger p = new BigInteger("467");
        BigInteger g = new BigInteger("2");
        BigInteger x = new BigInteger("127"); // приватный ключ
        BigInteger y = g.modPow(x, p);        // публичный ключ

        System.out.println("ElGamal Encryption:");
        System.out.println("Public key: (p=" + p + ", g=" + g + ", y=" + y + ")");
        System.out.println("Private key: x=" + x);
        System.out.println();

        for (String text : texts) {
            System.out.println("Исходное сообщение: " + text);
            String upper = text.replaceAll("[^А-ЯЁ]", "");

            List<BigInteger[]> encrypted = new ArrayList<>();
            for (char c : upper.toCharArray()) {
                BigInteger m = BigInteger.valueOf(c - 'А' + 1);
                BigInteger k = BigInteger.valueOf(43);
                BigInteger a = g.modPow(k, p);
                BigInteger b = (y.modPow(k, p).multiply(m)).mod(p);
                encrypted.add(new BigInteger[]{a, b});
            }

            System.out.print("Зашифрованное: ");
            for (BigInteger[] pair : encrypted) System.out.print(Arrays.toString(pair) + " ");
            System.out.println();

            StringBuilder decrypted = new StringBuilder();
            for (BigInteger[] pair : encrypted) {
                BigInteger a = pair[0];
                BigInteger b = pair[1];
                BigInteger s = a.modPow(x, p);
                BigInteger s_inv = s.modInverse(p);
                BigInteger m = (b.multiply(s_inv)).mod(p);
                decrypted.append((char) ('А' + m.intValue() - 1));
            }

            System.out.println("Расшифрованное: " + decrypted);
            System.out.println();
        }
    }
}
