package org.example;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;
import java.util.List;

public class CryptoSystemsGUI extends JFrame {
    private JComboBox<String> algorithmCombo;
    private JComboBox<String> modeCombo;
    private JComboBox<String> phraseCombo;
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JTextArea keysArea;
    private JPanel parametersPanel;

    // RSA параметры
    private JTextField rsaPField, rsaQField, rsaEField;
    private RSAKeys rsaKeys;

    // Knapsack параметры
    private JTextField knapsackSeqField, knapsackMField, knapsackWField;
    private KnapsackKeys knapsackKeys;

    // ElGamal параметры
    private JTextField elgamalPField, elgamalGField, elgamalXField;
    private ElGamalKeys elgamalKeys;

    private String[] presetPhrases = {
            "Выберите фразу...",
            "Ванчуков Александр Вячеславович",
            "не плачь девочка, пройдут дожди. Солдат вернется, ты только жди",
            "обратная сторона луны",
            "Эль-Гамаль усовершенствовал систему Диффи-Хеллмана и получил два алгоримта"
    };

    public CryptoSystemsGUI() {
        setTitle("Криптосистемы с открытым ключом");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        initComponents();

        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Верхняя панель с настройками
        JPanel topPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(60, 60, 100));

        // Режим
        JPanel modePanel = createStyledPanel("Режим");
        modeCombo = new JComboBox<>(new String[]{"Шифрование", "Дешифрование"});
        styleComboBox(modeCombo);
        modePanel.add(modeCombo);
        topPanel.add(modePanel);

        // Алгоритм
        JPanel algoPanel = createStyledPanel("Алгоритм");
        algorithmCombo = new JComboBox<>(new String[]{"RSA", "Укладка ранца", "Эль-Гамаль"});
        styleComboBox(algorithmCombo);
        algorithmCombo.addActionListener(e -> updateParametersPanel());
        algoPanel.add(algorithmCombo);
        topPanel.add(algoPanel);

        // Готовые фразы
        JPanel phrasePanel = createStyledPanel("Готовые фразы");
        phraseCombo = new JComboBox<>(presetPhrases);
        styleComboBox(phraseCombo);
        phraseCombo.addActionListener(e -> {
            if (phraseCombo.getSelectedIndex() > 0) {
                inputArea.setText((String) phraseCombo.getSelectedItem());
            }
        });
        phrasePanel.add(phraseCombo);
        topPanel.add(phrasePanel);

        add(topPanel, BorderLayout.NORTH);

        // Центральная панель
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
        centerPanel.setBackground(new Color(40, 40, 70));

        // Левая панель - параметры
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBackground(new Color(40, 40, 70));

        // Панель параметров
        parametersPanel = new JPanel(new BorderLayout());
        parametersPanel.setBorder(createTitledBorder("Параметры"));
        parametersPanel.setBackground(new Color(50, 50, 90));
        updateParametersPanel();
        leftPanel.add(parametersPanel, BorderLayout.CENTER);

        // Кнопка генерации ключей
        JButton generateButton = new JButton("Сгенерировать ключи");
        styleButton(generateButton, new Color(120, 80, 200));
        generateButton.addActionListener(e -> generateKeys());
        leftPanel.add(generateButton, BorderLayout.SOUTH);

        // Панель ключей
        JPanel keysPanel = new JPanel(new BorderLayout());
        keysPanel.setBorder(createTitledBorder("Сгенерированные ключи"));
        keysPanel.setBackground(new Color(50, 50, 90));
        keysArea = new JTextArea(5, 30);
        styleTextArea(keysArea);
        keysArea.setEditable(false);
        keysPanel.add(new JScrollPane(keysArea), BorderLayout.CENTER);

        JPanel leftContainer = new JPanel(new GridLayout(2, 1, 5, 5));
        leftContainer.setBackground(new Color(40, 40, 70));
        leftContainer.add(leftPanel);
        leftContainer.add(keysPanel);

        centerPanel.add(leftContainer);

        // Правая панель - ввод/вывод
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        rightPanel.setBackground(new Color(40, 40, 70));

        // Панель ввода
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(createTitledBorder("Входной текст"));
        inputPanel.setBackground(new Color(50, 50, 90));
        inputArea = new JTextArea();
        styleTextArea(inputArea);
        inputPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);

        JButton processButton = new JButton("Обработать");
        styleButton(processButton, new Color(80, 160, 80));
        processButton.addActionListener(e -> processText());
        inputPanel.add(processButton, BorderLayout.SOUTH);

        rightPanel.add(inputPanel);

        // Панель вывода
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(createTitledBorder("Результат"));
        outputPanel.setBackground(new Color(50, 50, 90));
        outputArea = new JTextArea();
        styleTextArea(outputArea);
        outputArea.setEditable(false);
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JButton copyButton = new JButton("Копировать");
        styleButton(copyButton, new Color(80, 120, 200));
        copyButton.addActionListener(e -> {
            outputArea.selectAll();
            outputArea.copy();
            JOptionPane.showMessageDialog(this, "Скопировано!");
        });
        outputPanel.add(copyButton, BorderLayout.SOUTH);

        rightPanel.add(outputPanel);

        centerPanel.add(rightPanel);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void updateParametersPanel() {
        parametersPanel.removeAll();
        JPanel params = new JPanel(new GridBagLayout());
        params.setBackground(new Color(50, 50, 90));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        String algo = (String) algorithmCombo.getSelectedItem();

        if ("RSA".equals(algo)) {
            gbc.gridx = 0; gbc.gridy = 0;
            params.add(createLabel("Простое число P:"), gbc);
            gbc.gridx = 1;
            rsaPField = createTextField("61");
            params.add(rsaPField, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            params.add(createLabel("Простое число Q:"), gbc);
            gbc.gridx = 1;
            rsaQField = createTextField("53");
            params.add(rsaQField, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            params.add(createLabel("Экспонента E:"), gbc);
            gbc.gridx = 1;
            rsaEField = createTextField("17");
            params.add(rsaEField, gbc);

        } else if ("Укладка ранца".equals(algo)) {
            gbc.gridx = 0; gbc.gridy = 0;
            params.add(createLabel("Последовательность:"), gbc);
            gbc.gridx = 1;
            knapsackSeqField = createTextField("2,3,7,14,30,57,120,251");
            params.add(knapsackSeqField, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            params.add(createLabel("Модуль M:"), gbc);
            gbc.gridx = 1;
            knapsackMField = createTextField("41");
            params.add(knapsackMField, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            params.add(createLabel("Множитель W:"), gbc);
            gbc.gridx = 1;
            knapsackWField = createTextField("491");
            params.add(knapsackWField, gbc);

        } else if ("Эль-Гамаль".equals(algo)) {
            gbc.gridx = 0; gbc.gridy = 0;
            params.add(createLabel("Простое число P:"), gbc);
            gbc.gridx = 1;
            elgamalPField = createTextField("467");
            params.add(elgamalPField, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            params.add(createLabel("Образующий G:"), gbc);
            gbc.gridx = 1;
            elgamalGField = createTextField("2");
            params.add(elgamalGField, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            params.add(createLabel("Секретный ключ X:"), gbc);
            gbc.gridx = 1;
            elgamalXField = createTextField("153");
            params.add(elgamalXField, gbc);
        }

        parametersPanel.add(params, BorderLayout.CENTER);
        parametersPanel.revalidate();
        parametersPanel.repaint();
    }

    private void generateKeys() {
        try {
            String algo = (String) algorithmCombo.getSelectedItem();

            if ("RSA".equals(algo)) {
                long p = Long.parseLong(rsaPField.getText());
                long q = Long.parseLong(rsaQField.getText());
                long e = Long.parseLong(rsaEField.getText());

                if (!isPrime(p) || !isPrime(q)) {
                    throw new Exception("P и Q должны быть простыми числами!");
                }

                rsaKeys = new RSAKeys(p, q, e);
                keysArea.setText(rsaKeys.toString());

            } else if ("Укладка ранца".equals(algo)) {
                String[] parts = knapsackSeqField.getText().split(",");
                int[] seq = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    seq[i] = Integer.parseInt(parts[i].trim());
                }
                int m = Integer.parseInt(knapsackMField.getText());
                int w = Integer.parseInt(knapsackWField.getText());

                knapsackKeys = new KnapsackKeys(seq, m, w);
                keysArea.setText(knapsackKeys.toString());

            } else if ("Эль-Гамаль".equals(algo)) {
                long p = Long.parseLong(elgamalPField.getText());
                long g = Long.parseLong(elgamalGField.getText());
                long x = Long.parseLong(elgamalXField.getText());

                if (!isPrime(p)) {
                    throw new Exception("P должно быть простым числом!");
                }

                elgamalKeys = new ElGamalKeys(p, g, x);
                keysArea.setText(elgamalKeys.toString());
            }

            JOptionPane.showMessageDialog(this, "Ключи успешно сгенерированы!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processText() {
        try {
            String input = inputArea.getText();
            if (input.isEmpty()) {
                throw new Exception("Введите текст!");
            }

            String algo = (String) algorithmCombo.getSelectedItem();
            boolean encrypt = modeCombo.getSelectedIndex() == 0;
            String result = "";

            if ("RSA".equals(algo)) {
                if (rsaKeys == null) throw new Exception("Сначала сгенерируйте ключи!");
                result = encrypt ? rsaKeys.encrypt(input) : rsaKeys.decrypt(input);

            } else if ("Укладка ранца".equals(algo)) {
                if (knapsackKeys == null) throw new Exception("Сначала сгенерируйте ключи!");
                result = encrypt ? knapsackKeys.encrypt(input) : knapsackKeys.decrypt(input);

            } else if ("Эль-Гамаль".equals(algo)) {
                if (elgamalKeys == null) throw new Exception("Сначала сгенерируйте ключи!");
                result = encrypt ? elgamalKeys.encrypt(input) : elgamalKeys.decrypt(input);
            }

            outputArea.setText(result);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Вспомогательные методы UI
    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(createTitledBorder(title));
        panel.setBackground(new Color(50, 50, 90));
        return panel;
    }

    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 200), 2),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                Color.WHITE
        );
        return border;
    }

    private void styleComboBox(JComboBox<?> combo) {
        combo.setBackground(new Color(70, 70, 110));
        combo.setForeground(Color.WHITE);
        combo.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private void styleTextArea(JTextArea area) {
        area.setBackground(new Color(30, 30, 50));
        area.setForeground(Color.WHITE);
        area.setCaretColor(Color.WHITE);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        return label;
    }

    private JTextField createTextField(String defaultValue) {
        JTextField field = new JTextField(defaultValue, 15);
        field.setBackground(new Color(70, 70, 110));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        return field;
    }

    // Математические функции
    private static boolean isPrime(long n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (long i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }

    private static long gcd(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    private static long modInverse(long a, long m) {
        a = ((a % m) + m) % m;
        for (long x = 1; x < m; x++) {
            if ((a * x) % m == 1) return x;
        }
        return 1;
    }

    private static long modPow(long base, long exp, long mod) {
        long result = 1;
        base = base % mod;
        while (exp > 0) {
            if (exp % 2 == 1) result = (result * base) % mod;
            exp = exp >> 1;
            base = (base * base) % mod;
        }
        return result;
    }

    // Классы для хранения ключей
    static class RSAKeys {
        long p, q, n, phi, e, d;

        RSAKeys(long p, long q, long e) throws Exception {
            this.p = p;
            this.q = q;
            this.n = p * q;
            this.phi = (p - 1) * (q - 1);
            this.e = e;

            if (gcd(e, phi) != 1) {
                throw new Exception("E и φ(n) должны быть взаимно простыми!");
            }

            this.d = modInverse(e, phi);
        }

        String encrypt(String text) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                long m = text.charAt(i);
                long c = modPow(m, e, n);
                result.append(c);
                if (i < text.length() - 1) result.append(",");
            }
            return result.toString();
        }

        String decrypt(String text) {
            StringBuilder result = new StringBuilder();
            String[] parts = text.split(",");
            for (String part : parts) {
                long c = Long.parseLong(part.trim());
                long m = modPow(c, d, n);
                result.append((char) m);
            }
            return result.toString();
        }

        public String toString() {
            return String.format("Открытый ключ: (e=%d, n=%d)\nЗакрытый ключ: (d=%d, n=%d)\nφ(n)=%d",
                    e, n, d, n, phi);
        }
    }

    static class KnapsackKeys {
        int[] privateSeq, publicSeq;
        int m, w, wInv;

        KnapsackKeys(int[] seq, int m, int w) throws Exception {
            this.privateSeq = seq;
            this.m = m;
            this.w = w;

            // Проверка супервозрастающей последовательности
            int sum = 0;
            for (int i = 0; i < seq.length; i++) {
                if (i > 0 && seq[i] <= sum) {
                    throw new Exception("Последовательность должна быть супервозрастающей!");
                }
                sum += seq[i];
            }

            if (m <= sum) {
                throw new Exception("M должен быть больше суммы!");
            }

            if (gcd(w, m) != 1) {
                throw new Exception("W и M должны быть взаимно простыми!");
            }

            this.publicSeq = new int[seq.length];
            for (int i = 0; i < seq.length; i++) {
                publicSeq[i] = (int) ((seq[i] * w) % m);
            }

            this.wInv = (int) modInverse(w, m);
        }

        String encrypt(String text) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                int charCode = text.charAt(i);
                String binary = String.format("%8s", Integer.toBinaryString(charCode)).replace(' ', '0');

                int sum = 0;
                for (int j = 0; j < 8 && j < publicSeq.length; j++) {
                    if (binary.charAt(j) == '1') {
                        sum += publicSeq[j];
                    }
                }
                result.append(sum);
                if (i < text.length() - 1) result.append(",");
            }
            return result.toString();
        }

        String decrypt(String text) {
            StringBuilder result = new StringBuilder();
            String[] parts = text.split(",");

            for (String part : parts) {
                int encValue = Integer.parseInt(part.trim());
                int c = (int) ((encValue * wInv) % m);

                StringBuilder binary = new StringBuilder();
                for (int i = privateSeq.length - 1; i >= 0; i--) {
                    if (c >= privateSeq[i]) {
                        binary.insert(0, '1');
                        c -= privateSeq[i];
                    } else {
                        binary.insert(0, '0');
                    }
                }

                String bin = binary.toString();
                if (bin.length() < 8) {
                    bin = String.format("%8s", bin).replace(' ', '0');
                }
                bin = bin.substring(0, 8);

                int charCode = Integer.parseInt(bin, 2);
                result.append((char) charCode);
            }
            return result.toString();
        }

        public String toString() {
            return String.format("Открытый ключ: %s\nЗакрытый ключ: %s\nm=%d, w=%d",
                    Arrays.toString(publicSeq), Arrays.toString(privateSeq), m, w);
        }
    }

    static class ElGamalKeys {
        long p, g, x, y;

        ElGamalKeys(long p, long g, long x) throws Exception {
            this.p = p;
            this.g = g;
            this.x = x;

            if (x >= p) {
                throw new Exception("X должен быть меньше P!");
            }

            this.y = modPow(g, x, p);
        }

        String encrypt(String text) {
            StringBuilder result = new StringBuilder();
            Random random = new Random();

            for (int i = 0; i < text.length(); i++) {
                long m = text.charAt(i);
                long k = random.nextInt((int) (p - 2)) + 1;

                long a = modPow(g, k, p);
                long b = (m * modPow(y, k, p)) % p;

                result.append(a).append(":").append(b);
                if (i < text.length() - 1) result.append(",");
            }
            return result.toString();
        }

        String decrypt(String text) {
            StringBuilder result = new StringBuilder();
            String[] parts = text.split(",");

            for (String part : parts) {
                String[] pair = part.split(":");
                long a = Long.parseLong(pair[0].trim());
                long b = Long.parseLong(pair[1].trim());

                long s = modPow(a, x, p);
                long sInv = modInverse(s, p);
                long m = (b * sInv) % p;

                result.append((char) m);
            }
            return result.toString();
        }

        public String toString() {
            return String.format("Открытый ключ: (p=%d, g=%d, y=%d)\nЗакрытый ключ: (x=%d)",
                    p, g, y, x);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new CryptoSystemsGUI();
        });
    }
}