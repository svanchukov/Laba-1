package org.example;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.*;

public class Knapsack {
    public static void main(String[] args) throws Exception {
        String[] texts = {
                "ВАНЧУКОВ АЛЕКСАНДР ВЯЧЕСЛАВОВИЧ",
                "НЕ ПЛАЧЬ ДЕВЧОНКА ПРОЙДУТ ДОЖДИ СОЛДАТ ВЕРНЕТСЯ ТЫ ТОЛЬКО ЖДИ",
                "ОБРАТНАЯ СТОРОНА ЛУНЫ",
                "ЭЛЬ ГАМАЛЬ УСОВЕРШЕНСТВОВАЛ СИСТЕМУ ДИФФИ ХЕЛЛМАНА И ПОЛУЧИЛ ДВА АЛГОРИТМА"
        };

        // 1. Приватный ключ (супервозрастающая последовательность)
        int[] a = {2, 3, 7, 14, 30, 57, 120, 251}; // каждый > суммы предыдущих

        // 2. Выбираем m и n
        int m = 491; // > суммы a[]
        int n = 41;  // взаимно простое с m
        int nInverse = BigInteger.valueOf(n).modInverse(BigInteger.valueOf(m)).intValue();

        // === 3. Публичный ключ ===
        int[] b = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            b[i] = (a[i] * n) % m;
        }

        System.out.println("Приватный ключ a: " + Arrays.toString(a));
        System.out.println("Модуль m = " + m + ", множитель n = " + n);
        System.out.println("Открытый ключ b: " + Arrays.toString(b));
        System.out.println("Обратное n⁻¹ mod m = " + nInverse);
        System.out.println("=====================================================\n");

        Charset charset = Charset.forName("Windows-1251");

        // Таблица шифрования
        for (String text : texts) {
            System.out.println("Исходное сообщение: " + text);

            // переводим текст в байты Windows-1251
            byte[] bytes = text.getBytes(charset);

            // Шифрование
            List<Integer> cipherList = new ArrayList<>();
            for (byte bChar : bytes) {
                String bits = String.format("%8s", Integer.toBinaryString(bChar & 0xFF)).replace(' ', '0');
                int sum = 0;
                for (int i = 0; i < 8; i++) {
                    if (bits.charAt(i) == '1') sum += b[i];
                }
                cipherList.add(sum);
            }

            System.out.println("Зашифрованное: " + cipherList);

            // Расшифрование
            List<Byte> decryptedBytes = new ArrayList<>();
            for (int S : cipherList) {
                int S1 = (S * nInverse) % m;

                StringBuilder bits = new StringBuilder();
                for (int i = a.length - 1; i >= 0; i--) {
                    if (a[i] <= S1) {
                        bits.insert(0, "1");
                        S1 -= a[i];
                    } else {
                        bits.insert(0, "0");
                    }
                }

                int symbolCode = Integer.parseInt(bits.toString(), 2);
                decryptedBytes.add((byte) symbolCode);
            }

            byte[] decryptedArray = new byte[decryptedBytes.size()];
            for (int i = 0; i < decryptedBytes.size(); i++) decryptedArray[i] = decryptedBytes.get(i);

            String decryptedText = new String(decryptedArray, charset);
            System.out.println("Расшифрованное сообщение: " + decryptedText);
            System.out.println("-----------------------------------------------------\n");
        }
    }
}
