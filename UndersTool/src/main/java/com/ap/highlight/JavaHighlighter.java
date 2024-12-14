package com.ap.highlight;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaHighlighter {

    public void highlight(JTextPane textArea) {
        StyledDocument doc = textArea.getStyledDocument();

        // Define a default style
        Style defaultStyle = doc.addStyle("Default Style", null);
        StyleConstants.setForeground(defaultStyle, Color.BLACK);

        // Define a keyword style
        Style keywordStyle = doc.addStyle("Keyword Style", null);
        StyleConstants.setForeground(keywordStyle, Color.BLUE);

        // Define a comment style
        Style commentStyle = doc.addStyle("Comment Style", null);
        StyleConstants.setForeground(commentStyle, Color.GRAY);

        // Define a comment style
        Style javaSEClassStyle = doc.addStyle("Class style ", null);
        StyleConstants.setForeground(javaSEClassStyle, Color.MAGENTA);


        // Apply the default style to the entire text
        doc.setCharacterAttributes(0, doc.getLength(), defaultStyle, false);

        // Apply the keyword style to each keyword
        String[] words = textArea.getText().split("\\W");
        for (String word : words) {
            if (isKeyword(word)) {
                applyStyle(doc, word, keywordStyle);
            }
            if (isJavaStandardClass(word)) {
                applyStyle(doc, word, javaSEClassStyle);
            }
        }

        // Apply the comment style to each comment
        // This is a very basic check. You might want to improve this to handle different types of comments.
        Pattern commentPattern = Pattern.compile("//.*");
        Matcher matcher = commentPattern.matcher(textArea.getText());
        while (matcher.find()) {
            doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), commentStyle, false);
        }
    }

    private boolean isKeyword(String word) {
        // List of all Java keywords
        List<String> keywords = Arrays.asList(
                "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
                "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
                "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
                "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super",
                "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"
        );

        return keywords.contains(word);
    }

    private boolean isComment(String word) {
        // This is a very basic check. You might want to improve this to handle different types of comments.
        return word.startsWith("//");
    }

    private void applyStyle(StyledDocument doc, String word, Style style) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    Pattern wordPattern = Pattern.compile("\\b" + word + "\\b");
                    Matcher matcher = wordPattern.matcher(doc.getText(0, doc.getLength()));
                    while (matcher.find()) {
                        doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), style, false);
                    }
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };


    }

    private boolean isJavaStandardClass(String word) {
        // List of most used Java SE classes
        List<String> javaSEClasses = Arrays.asList(
                "int", "double", "var", "char", "boolean", "long", "String", "Integer", "Double", "Float", "Character", "Boolean", "Byte", "Short", "Long", "Math",
                "System", "Thread", "Runnable", "Date", "Calendar", "GregorianCalendar", "TimeZone", "SimpleDateFormat",
                "Locale", "Random", "Properties", "Runtime", "SecurityManager", "Class", "ClassLoader", "Process",
                "ProcessBuilder", "Enum", "Throwable", "Exception", "Error", "RuntimeException", "ThreadGroup",
                "Package", "Compiler", "InheritableThreadLocal", "ThreadLocal", "Void", "ProcessEnvironment",
                "Shutdown", "ApplicationShutdownHooks", "SystemClassLoaderAction", "File", "InputStream", "OutputStream",
                "Reader", "Writer", "PrintStream", "PrintWriter", "Serializable", "Cloneable", "Comparator", "Iterator",
                "Collection", "Set", "List", "Map", "HashMap", "HashSet", "ArrayList", "LinkedList", "TreeMap",
                "TreeSet", "Vector", "Stack", "Dictionary", "Hashtable", "Enumeration", "BitSet", "UUID", "RandomAccess",
                "Currency", "Observer", "Observable", "Timer", "TimerTask", "ResourceBundle", "Formatter", "Scanner",
                "Console", "Queue", "Deque", "AbstractCollection", "AbstractSet", "AbstractList", "AbstractSequentialList",
                "AbstractMap", "Arrays", "Collections", "AbstractQueue", "PriorityQueue", "Locale", "LocalDate", "LocalTime",
                "LocalDateTime", "ZonedDateTime", "Instant", "Period", "Duration", "Clock", "Year", "YearMonth", "MonthDay",
                "OffsetTime", "OffsetDateTime", "ZoneOffset", "ZoneId", "ChronoUnit", "ChronoField", "ChronoLocalDate",
                "ChronoLocalDateTime", "ChronoZonedDateTime", "Temporal", "TemporalAccessor", "TemporalAdjuster",
                "TemporalAmount", "TemporalField", "TemporalQuery", "TemporalUnit", "HijrahDate", "HijrahEra", "IsoChronology",
                "IsoEra", "JapaneseDate", "JapaneseEra", "MinguoDate", "MinguoEra", "ThaiBuddhistDate", "ThaiBuddhistEra",
                "ValueRange", "DateTimeFormatter", "DateTimeFormatterBuilder", "SignStyle", "ResolverStyle", "FormatStyle",
                "TextStyle", "DecimalStyle", "ResolverStyle", "Chronology", "Era", "DayOfWeek", "Month", "AmPm", "JulianFields",
                "WeekFields", "ArabicShaping", "Bidi", "BreakIterator", "Collator", "RuleBasedCollator", "DateFormat",
                "DateFormatSymbols", "DecimalFormat", "DecimalFormatSymbols", "Format", "NumberFormat", "MessageFormat",
                "FieldPosition", "ParsePosition", "Normalizer", "AttributeEntry", "AttributedCharacterIterator", "Field");
        return javaSEClasses.contains(word);
    }

}