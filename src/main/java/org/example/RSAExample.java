package org.example;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class RSAExample {
    public static void main(String[] args) {
        String[] texts = {
                "ВАНЧУКОВ АЛЕКСАНДР ВЯЧЕСЛАВОВИЧ",
                "НЕ ПЛАЧЬ ДЕВЧОНКА ПРОЙДУТ ДОЖДИ СОЛДАТ ВЕРНЕТСЯ ТЫ ТОЛЬКО ЖДИ",
                "ОБРАТНАЯ СТОРОНА ЛУНЫ",
                "ЭЛЬ ГАМАЛЬ УСОВЕРШЕНСТВОВАЛ СИСТЕМУ ДИФФИ ХЕЛЛМАНА И ПОЛУЧИЛ ДВА АЛГОРИТМА"
        };

        // Простые числа для примера
        BigInteger p = BigInteger.valueOf(61);
        BigInteger q = BigInteger.valueOf(53);

        // n = p * q — модуль, часть открытого и закрытого ключа
        BigInteger n = p.multiply(q);

        // φ(n) = (p−1)*(q−1)
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        // e — открытая экспонента (должна быть взаимно простой с φ)
        BigInteger e = BigInteger.valueOf(17);

        // d — закрытая экспонента, вычисляется как обратное к e по модулю φ
        BigInteger d = e.modInverse(phi);

        System.out.println("========== RSA ШИФРОВАНИЕ ==========");
        System.out.println("Открытый ключ (e, n): (" + e + ", " + n + ")");
        System.out.println("Закрытый ключ (d, n): (" + d + ", " + n + ")");
        System.out.println("====================================\n");

        for (String text : texts) {
            System.out.println("Исходное сообщение: " + text);

            // Убираем все символы кроме букв и пробелов
            String upper = text.replaceAll("[^А-ЯЁ ]", "");

            // Шифрование
            List<BigInteger> encrypted = new ArrayList<>();
            for (char c : upper.toCharArray()) {
                if (c == ' ') {
                    // Пробел кодируем как 0
                    encrypted.add(BigInteger.ZERO);
                    continue;
                }
                // Код символа в диапазоне А=1, Б=2, ..., Я=32, Ё=33
                int code = (c == 'Ё') ? 33 : (c - 'А' + 1);
                BigInteger m = BigInteger.valueOf(code);

                // c = m^e mod n
                BigInteger ciph = m.modPow(e, n);
                encrypted.add(ciph);
            }

            System.out.println("Зашифрованное сообщение: " + encrypted);

            // Расшифровка
            StringBuilder decrypted = new StringBuilder();
            for (BigInteger ciph : encrypted) {
                if (ciph.equals(BigInteger.ZERO)) {
                    decrypted.append(' ');
                    continue;
                }
                // m = c^d mod n
                BigInteger dec = ciph.modPow(d, n);
                int val = dec.intValue();
                char symbol = (val == 33) ? 'Ё' : (char) ('А' + val - 1);
                decrypted.append(symbol);
            }

            System.out.println("Расшифрованное сообщение: " + decrypted);
            System.out.println("------------------------------------\n");
        }
    }
}
