package org.example;

import java.util.*;

public class Knapsack {
    public static void main(String[] args) {
        String[] texts = {
                "ВАНЧУКОВ АЛЕКСАНДР ВЯЧЕСЛАВОВИЧ",
                "НЕ ПЛАЧЬ ДЕВЧОНКА ПРОЙДУТ ДОЖДИ СОЛДАТ ВЕРНЕТСЯ ТЫ ТОЛЬКО ЖДИ",
                "ОБРАТНАЯ СТОРОНА ЛУНЫ",
                "ЭЛЬ ГАМАЛЬ УСОВЕРШЕНСТВОВАЛ СИСТЕМУ ДИФФИ ХЕЛЛМАНА И ПОЛУЧИЛ ДВА АЛГОРИТМА"
        };

        // Простая супервозрастающая последовательность
        int[] w = {2, 3, 7, 14, 30, 57, 120, 251};
        int q = 491;
        int r = 41;

        // Публичный ключ
        int[] b = new int[w.length];
        for (int i = 0; i < w.length; i++) {
            b[i] = (w[i] * r) % q;
        }

        System.out.println("Knapsack Encryption:");
        System.out.println("Public key: " + Arrays.toString(b));
        System.out.println();

        for (String text : texts) {
            System.out.println("Исходное сообщение: " + text);

            // Преобразуем символы в бинарный код Windows-1251 (упрощенно)
            byte[] bytes = text.replaceAll("[^А-ЯЁ]", "").getBytes();

            // Шифрование
            List<Integer> encrypted = new ArrayList<>();
            for (byte ch : bytes) {
                String bits = String.format("%8s", Integer.toBinaryString(ch & 0xFF)).replace(' ', '0');
                int sum = 0;
                for (int i = 0; i < 8; i++) {
                    if (bits.charAt(i) == '1') sum += b[i];
                }
                encrypted.add(sum);
            }

            System.out.println("Зашифрованное: " + encrypted);

            // Расшифрование
            List<Character> decrypted = new ArrayList<>();
            int r_inv = modInverse(r, q);
            for (int c : encrypted) {
                int cPrime = (c * r_inv) % q;

                // Распаковываем с конца
                StringBuilder bits = new StringBuilder();
                for (int i = w.length - 1; i >= 0; i--) {
                    if (w[i] <= cPrime) {
                        cPrime -= w[i];
                        bits.insert(0, "1");
                    } else bits.insert(0, "0");
                }

                decrypted.add((char) Integer.parseInt(bits.toString(), 2));
            }

            System.out.println("Расшифрованное: " + decrypted.toString());
            System.out.println();
        }
    }

    private static int modInverse(int a, int m) {
        for (int x = 1; x < m; x++) if ((a * x) % m == 1) return x;
        return 1;
    }
}
