package org.example;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

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
        setTitle("Криптосистемы с открытым ключом — исправлено");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1250, 820);
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

        // Левая панель - параметры и ключи
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBackground(new Color(40, 40, 70));

        parametersPanel = new JPanel(new BorderLayout());
        parametersPanel.setBorder(createTitledBorder("Параметры"));
        parametersPanel.setBackground(new Color(50, 50, 90));
        updateParametersPanel();
        leftPanel.add(parametersPanel, BorderLayout.CENTER);

        // Кнопки генерации
        JPanel genButtons = new JPanel(new GridLayout(1, 2, 10, 10));
        genButtons.setBorder(new EmptyBorder(8, 8, 8, 8));
        genButtons.setBackground(new Color(40, 40, 70));

        JButton generateButton = new JButton("Сгенерировать ключи");
        styleButton(generateButton, new Color(120, 80, 200));
        generateButton.addActionListener(e -> generateKeys());
        genButtons.add(generateButton);

        JButton autoRSAButton = new JButton("Auto RSA (1024)");
        styleButton(autoRSAButton, new Color(200, 100, 80));
        autoRSAButton.addActionListener(e -> autoGenerateRSA());
        genButtons.add(autoRSAButton);

        leftPanel.add(genButtons, BorderLayout.SOUTH);

        // Панель ключей
        JPanel keysPanel = new JPanel(new BorderLayout());
        keysPanel.setBorder(createTitledBorder("Сгенерированные ключи"));
        keysPanel.setBackground(new Color(50, 50, 90));
        keysArea = new JTextArea(7, 32);
        styleTextArea(keysArea);
        keysArea.setEditable(false);
        keysPanel.add(new JScrollPane(keysArea), BorderLayout.CENTER);

        JPanel leftContainer = new JPanel(new GridLayout(2, 1, 8, 8));
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
            params.add(createLabel("Простое число P (вручную, или Auto RSA):"), gbc);
            gbc.gridx = 1;
            rsaPField = createTextField("");
            params.add(rsaPField, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            params.add(createLabel("Простое число Q:"), gbc);
            gbc.gridx = 1;
            rsaQField = createTextField("");
            params.add(rsaQField, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            params.add(createLabel("Экспонента E (обычно 65537):"), gbc);
            gbc.gridx = 1;
            rsaEField = createTextField("65537");
            params.add(rsaEField, gbc);

            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
            JLabel note = createLabel("<html><i>Важно: n = p*q должен быть больше 65535 (для Unicode). Если p/q маленькие, нажмите <b>Auto RSA (1024)</b>.</i></html>");
            note.setFont(new Font("Arial", Font.ITALIC, 11));
            params.add(note, gbc);
            gbc.gridwidth = 1;

        } else if ("Укладка ранца".equals(algo)) {
            gbc.gridx = 0; gbc.gridy = 0;
            params.add(createLabel("Последовательность (16 чисел, через запятую):"), gbc);
            gbc.gridx = 1;
            // По умолчанию даём супер-возрастающую последовательность длины 16
            knapsackSeqField = createTextField("1,2,4,8,16,32,64,128,256,512,1024,2048,4096,8192,16384,32768");
            params.add(knapsackSeqField, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            params.add(createLabel("Модуль M (> сумма последовательности):"), gbc);
            gbc.gridx = 1;
            knapsackMField = createTextField("131072"); // > сумма
            params.add(knapsackMField, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            params.add(createLabel("Множитель W (взаимно прост с M):"), gbc);
            gbc.gridx = 1;
            knapsackWField = createTextField("65537");
            params.add(knapsackWField, gbc);

        } else if ("Эль-Гамаль".equals(algo)) {
            gbc.gridx = 0; gbc.gridy = 0;
            params.add(createLabel("Простое число P (должно быть >65535):"), gbc);
            gbc.gridx = 1;
            elgamalPField = createTextField("65537");
            params.add(elgamalPField, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            params.add(createLabel("Образующий G:"), gbc);
            gbc.gridx = 1;
            elgamalGField = createTextField("3");
            params.add(elgamalGField, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            params.add(createLabel("Секретный ключ X (1..p-1):"), gbc);
            gbc.gridx = 1;
            elgamalXField = createTextField("153");
            params.add(elgamalXField, gbc);

            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
            JLabel noteE = createLabel("<html><i>Если P маленькое (<65535), будет сгенерировано большое P автоматически при генерации ключей.</i></html>");
            noteE.setFont(new Font("Arial", Font.ITALIC, 11));
            params.add(noteE, gbc);
            gbc.gridwidth = 1;
        }

        parametersPanel.add(params, BorderLayout.CENTER);
        parametersPanel.revalidate();
        parametersPanel.repaint();
    }

    private void generateKeys() {
        try {
            String algo = (String) algorithmCombo.getSelectedItem();

            if ("RSA".equals(algo)) {
                String ptxt = rsaPField.getText().trim();
                String qtxt = rsaQField.getText().trim();
                String etxt = rsaEField.getText().trim();

                if (ptxt.isEmpty() || qtxt.isEmpty()) {
                    // Если не введены p/q — автоматически генерируем безопасные
                    autoGenerateRSA();
                    return;
                }

                BigInteger p = new BigInteger(ptxt);
                BigInteger q = new BigInteger(qtxt);
                BigInteger e = new BigInteger(etxt.isEmpty() ? "65537" : etxt);

                if (!isProbablePrime(p) || !isProbablePrime(q)) {
                    throw new Exception("P и Q должны быть простыми числами!");
                }

                RSAKeys keys = new RSAKeys(p, q, e);
                // Проверяем, что n > 65535 (нужно для корректного восстановления UTF-16 char)
                if (keys.n.compareTo(BigInteger.valueOf(65535)) <= 0) {
                    // автогенерируем большие
                    int opt = JOptionPane.showConfirmDialog(this,
                            "p*q слишком мало для Unicode. Сгенерировать безопасные RSA-ключи (1024 бит)?",
                            "Малый модуль", JOptionPane.YES_NO_OPTION);
                    if (opt == JOptionPane.YES_OPTION) {
                        autoGenerateRSA();
                        return;
                    } else {
                        throw new Exception("n слишком мало для корректной работы с Unicode. Нажмите Auto RSA (1024).");
                    }
                }

                rsaKeys = keys;
                keysArea.setText(rsaKeys.toString());

            } else if ("Укладка ранца".equals(algo)) {
                String[] parts = knapsackSeqField.getText().split(",");
                int[] seq = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    seq[i] = Integer.parseInt(parts[i].trim());
                }
                int m = Integer.parseInt(knapsackMField.getText().trim());
                int w = Integer.parseInt(knapsackWField.getText().trim());

                knapsackKeys = new KnapsackKeys(seq, m, w);
                keysArea.setText(knapsackKeys.toString());

            } else if ("Эль-Гамаль".equals(algo)) {
                String ptxt = elgamalPField.getText().trim();
                BigInteger p;
                BigInteger g = new BigInteger(elgamalGField.getText().trim());
                BigInteger x = new BigInteger(elgamalXField.getText().trim());

                if (ptxt.isEmpty() || new BigInteger(ptxt).compareTo(BigInteger.valueOf(65535)) <= 0) {
                    // генерируем большое p
                    SecureRandom rnd = new SecureRandom();
                    p = BigInteger.probablePrime(512, rnd);
                    JOptionPane.showMessageDialog(this, "Сгенерировано большое простое p для ElGamal (512 бит).");
                } else {
                    p = new BigInteger(ptxt);
                }

                if (!isProbablePrime(p)) {
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

    private void autoGenerateRSA() {
        try {
            SecureRandom rnd = new SecureRandom();
            // 1024-bit RSA (p и q ~512 бит)
            BigInteger p = BigInteger.probablePrime(512, rnd);
            BigInteger q = BigInteger.probablePrime(512, rnd);
            BigInteger e = BigInteger.valueOf(65537);

            rsaKeys = new RSAKeys(p, q, e);
            rsaPField.setText(p.toString());
            rsaQField.setText(q.toString());
            rsaEField.setText(e.toString());
            keysArea.setText(rsaKeys.toString());
            JOptionPane.showMessageDialog(this, "Автоматически сгенерированы RSA-ключи (1024 бита).");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка генерации RSA: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
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
                if (encrypt) {
                    result = rsaKeys.encrypt(input);
                } else {
                    result = rsaKeys.decrypt(input);
                }

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

    // UI helpers
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
        JTextField field = new JTextField(defaultValue, 25);
        field.setBackground(new Color(70, 70, 110));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        return field;
    }

    // ---- Utilities ----
    private static boolean isProbablePrime(BigInteger n) {
        return n.isProbablePrime(25);
    }

    private static BigInteger modPow(BigInteger base, BigInteger exp, BigInteger mod) {
        return base.modPow(exp, mod);
    }

    private static BigInteger modInverse(BigInteger a, BigInteger m) throws Exception {
        try {
            return a.modInverse(m);
        } catch (ArithmeticException ex) {
            throw new Exception("Обратный элемент не существует (a и m должны быть взаимно простыми).");
        }
    }

    // ---- RSA class (BigInteger) ----
    static class RSAKeys {
        BigInteger p, q, n, phi, e, d;

        RSAKeys(BigInteger p, BigInteger q, BigInteger e) throws Exception {
            this.p = p;
            this.q = q;
            this.n = p.multiply(q);
            this.phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
            this.e = e;

            if (!this.e.gcd(phi).equals(BigInteger.ONE)) {
                throw new Exception("E и φ(n) должны быть взаимно простыми!");
            }

            this.d = this.e.modInverse(phi);
        }

        // encrypt: возвращает строку с числами, разделёнными запятыми (BigInteger)
        String encrypt(String text) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                int code = text.charAt(i);
                BigInteger m = BigInteger.valueOf(code);
                BigInteger c = modPow(m, e, n);
                result.append(c.toString());
                if (i < text.length() - 1) result.append(",");
            }
            return result.toString();
        }

        // decrypt: получает ту же строку, возвращает исходный текст
        String decrypt(String text) throws Exception {
            StringBuilder result = new StringBuilder();
            String[] parts = text.split(",");
            for (String part : parts) {
                part = part.trim();
                if (part.isEmpty()) continue;
                BigInteger c = new BigInteger(part);
                BigInteger m = modPow(c, d, n);
                int charCode = m.intValue();
                result.append((char) charCode);
            }
            return result.toString();
        }

        public String toString() {
            return String.format("RSA:\nОткрытый ключ (e,n):\n e=%s\n n=%s\n\nЗакрытый ключ (d,n):\n d=%s\n n=%s\n\nφ(n)=%s",
                    e.toString(), n.toString(), d.toString(), n.toString(), phi.toString());
        }
    }

    // ---- Knapsack (16-bit) ----
    static class KnapsackKeys {
        int[] privateSeq, publicSeq;
        int m, w, wInv;

        KnapsackKeys(int[] seq, int m, int w) throws Exception {
            if (seq.length != 16) {
                throw new Exception("Для корректной поддержки Unicode последовательность должна содержать ровно 16 элементов!");
            }
            this.privateSeq = Arrays.copyOf(seq, seq.length);
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
                throw new Exception("M должен быть больше суммы всех элементов последовательности! Сумма = " + sum + ", M = " + m);
            }

            if (BigInteger.valueOf(w).gcd(BigInteger.valueOf(m)).intValue() != 1) {
                throw new Exception("W и M должны быть взаимно простыми!");
            }

            this.publicSeq = new int[seq.length];
            for (int i = 0; i < seq.length; i++) {
                this.publicSeq[i] = (int) (((long) seq[i] * (long) w) % m);
            }

            this.wInv = modInverseInt(w, m);
        }

        private int modInverseInt(int a, int mod) throws Exception {
            BigInteger inv = modInverse(BigInteger.valueOf(a), BigInteger.valueOf(mod));
            return inv.intValue();
        }

        // encrypt: каждому символу (char, 0..65535) соответствует сумма publicSeq по 16 битам
        String encrypt(String text) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                int charCode = text.charAt(i); // 0..65535
                String binary = String.format("%16s", Integer.toBinaryString(charCode & 0xFFFF)).replace(' ', '0');

                long sum = 0;
                for (int j = 0; j < 16; j++) {
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
                part = part.trim();
                if (part.isEmpty()) continue;
                int encValue = Integer.parseInt(part);

                // S = C * wInv mod m
                int s = (int) (((long) encValue * (long) wInv) % m);
                if (s < 0) s += m;

                char[] bits = new char[16];
                int temp = s;
                for (int i = privateSeq.length - 1; i >= 0; i--) {
                    if (temp >= privateSeq[i]) {
                        bits[i] = '1';
                        temp -= privateSeq[i];
                    } else {
                        bits[i] = '0';
                    }
                }

                String binStr = new String(bits); // 16 бит
                int charCode = Integer.parseInt(binStr, 2);
                result.append((char) charCode);
            }
            return result.toString();
        }

        public String toString() {
            return String.format("Knapsack:\nОткрытый ключ: %s\nЗакрытый ключ: %s\nm=%d, w=%d, w⁻¹=%d",
                    Arrays.toString(publicSeq), Arrays.toString(privateSeq), m, w, wInv);
        }
    }

    // ---- ElGamal (BigInteger) ----
    static class ElGamalKeys {
        BigInteger p, g, x, y;
        SecureRandom random = new SecureRandom();

        ElGamalKeys(BigInteger p, BigInteger g, BigInteger x) throws Exception {
            this.p = p;
            this.g = g;
            this.x = x;

            if (x.compareTo(p) >= 0 || x.compareTo(BigInteger.ZERO) <= 0) {
                throw new Exception("X должен быть в диапазоне 1..p-1!");
            }

            this.y = modPow(g, x, p);
        }

        // encrypt returns string: a:b,a:b,...
        String encrypt(String text) {
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < text.length(); i++) {
                int charCode = text.charAt(i);
                BigInteger m = BigInteger.valueOf(charCode);

                BigInteger k;
                do {
                    k = new BigInteger(p.bitLength(), random);
                } while (k.compareTo(BigInteger.ONE) < 0 || k.compareTo(p.subtract(BigInteger.TWO)) > 0);

                BigInteger a = modPow(g, k, p);
                BigInteger b = (m.multiply(modPow(y, k, p))).mod(p);

                result.append(a.toString()).append(":").append(b.toString());
                if (i < text.length() - 1) result.append(",");
            }
            return result.toString();
        }

        String decrypt(String text) throws Exception {
            StringBuilder result = new StringBuilder();
            String[] parts = text.split(",");

            for (String part : parts) {
                part = part.trim();
                if (part.isEmpty()) continue;
                String[] pair = part.split(":");
                if (pair.length != 2) throw new Exception("Неверный формат шифротекста Эль-Гамаль.");

                BigInteger a = new BigInteger(pair[0].trim());
                BigInteger b = new BigInteger(pair[1].trim());

                BigInteger s = modPow(a, x, p);
                BigInteger sInv = modInverse(s, p);
                BigInteger m = (b.multiply(sInv)).mod(p);

                int charCode = m.intValue();
                result.append((char) charCode);
            }
            return result.toString();
        }

        public String toString() {
            return String.format("ElGamal:\nОткрытый ключ: (p=%s, g=%s, y=%s)\nЗакрытый ключ: (x=%s)",
                    p.toString(), g.toString(), y.toString(), x.toString());
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
