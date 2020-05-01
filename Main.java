package flashcards;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class Main {
    public static void addCard(Map<String, String> cards, List<String> log, Map<String, Integer> mistakes) {
        output("The card: ", log);
        String term = input(log);
        if (!cards.containsKey(term)) {
            output("The definition of the card: ", log);
            String definition = input(log);
            if (!cards.containsValue(definition)) {
                cards.put(term.strip(), definition.strip());
                mistakes.put(term.strip(), 0);
                output(String.format("The pair (\"%s\":\"%s\") has been added", term, definition), log);
            } else {
                output(String.format("The definition %s already exists.", definition), log);
            }
        } else {
            output(String.format("The card %s already exists.", term), log);
        }
    }
    public static void addCard(Map<String, String> cards, String[] line, Map<String, Integer> mistakes) {
        String term = line[0].strip();
        String definition = line[1].strip();
        Integer numberOfMistakes = Integer.parseInt(line[2].strip());
        if (!cards.containsKey(term)) {
            cards.put(term, definition);
            mistakes.put(term, numberOfMistakes);
        } else {
            cards.replace(term, definition);
            mistakes.replace(term, numberOfMistakes);
        }
    }
    public static void removeCard(Map<String, String> cards, List<String> log, Map<String, Integer> mistakes) {
        output("The card: ", log);
        String cardToDelete = input(log).strip();
        if (cards.containsKey(cardToDelete)) {
            cards.remove(cardToDelete);
            mistakes.remove(cardToDelete);
            output("The card has been removed.", log);
        } else {
            output(String.format("Can't remove \"%s\": there is no such card.", cardToDelete), log);
        }
    }
    public static void importFromFile(Map<String, String> cards, List<String> log, Map<String, Integer> mistakes) {
        int counter = 0;
        output("File name: ", log);
        String fileName = input(log);
        File file = new File(fileName);
        try (Scanner readingFile = new Scanner(file)) {
            while (readingFile.hasNext()) {
                String[] line = readingFile.nextLine().split(":", 3);
                addCard(cards, line, mistakes);
                counter++;
            }
        } catch (FileNotFoundException e) {
            output("File not found " + e.getMessage(), log);
        }
        if (counter > 0) {
            output(counter + " cards have been loaded.", log);
        }
    }
    public static void importFromFile(Map<String, String> cards, List<String> log, Map<String, Integer> mistakes, String path) {
        int counter = 0;
        File file = new File(path);
        try (Scanner readingFile = new Scanner(file)) {
            while (readingFile.hasNext()) {
                String[] line = readingFile.nextLine().split(":", 3);
                addCard(cards, line, mistakes);
                counter++;
            }
        } catch (FileNotFoundException e) {
            output("File not found " + e.getMessage(), log);
        }
        if (counter > 0) {
            output(counter + " cards have been loaded.", log);
        }
    }
    public static void exportToFile(Map<String, String> cards, List<String> log, Map<String, Integer> mistakes) {
        int counter = 0;
        output("File name: ", log);
        String fileName = input(log);
        File file = new File(fileName);
        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (String key:cards.keySet()) {
                printWriter.printf("%s : %s : %s%n", key, cards.get(key), mistakes.get(key));
                counter++;
            }
        } catch (Exception e) {
            output("File not found", log);
        }
        output(counter + " cards have been saved.", log);
    }
    public static void exportToFile(Map<String, String> cards, List<String> log, Map<String, Integer> mistakes, String path) {
        int counter = 0;
        File file = new File(path);
        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (String key:cards.keySet()) {
                printWriter.printf("%s : %s : %s%n", key, cards.get(key), mistakes.get(key));
                counter++;
            }
        } catch (Exception e) {
            output("File not found", log);
        }
        output(counter + " cards have been saved.", log);
    }
    public static void ask(Map<String, String> cards, List<String> log, Map<String, Integer> mistakes) {
        Random random = new Random();
        Object[] keys = cards.keySet().toArray();
        output("How many times to ask?", log);
        int howManyAsk = Integer.parseInt(input(log));
        for (int i = 0; i < howManyAsk; i++) {
            String randomKey = (String) keys[random.nextInt(keys.length)];
            output("Print the definition of \"" + randomKey + "\":", log);
            String answer = input(log);
            if (cards.get(randomKey).equals(answer)) {
                output("Correct answer.", log);
            } else if (cards.containsValue(answer)) {
                for (String key : cards.keySet()) {
                    if (cards.get(key).equals(answer)) {
                        output("Wrong answer. The correct one is \""
                                + cards.get(randomKey) + "\", you've just written the definition of \""
                                + key + "\".", log);
                        mistakes.replace(randomKey, mistakes.get(randomKey) + 1);
                    }
                }
            } else {
                output("Wrong answer. The correct one is \"" + cards.get(randomKey) + "\".", log);
                mistakes.replace(randomKey, mistakes.get(randomKey) + 1);
            }
        }

    }

    public static void createLog(String line, List<String> log) {
        log.add(line);
    }

    public static void output(String line, List<String> log) {
        System.out.println(line);
        createLog(line, log);
    }

    public static String input(List<String> log) {
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();
        createLog(userInput, log);
        return userInput;
    }

    public static void log(List<String> log) {
        output("File name: ", log);
        String fileName = input(log);
        File file = new File(fileName);
        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (String line : log) {
                printWriter.printf("%s%n", line);
            }
        } catch (Exception e) {
            output("File not found", log);
        }
    }

    public static void hardestCard(Map<String, Integer> mistakes, List<String> log) {
        List<String> hardest = new ArrayList<>();
        int maxNumOfMistakes = 0;
        for (String key : mistakes.keySet()) {
            if (mistakes.get(key) > maxNumOfMistakes) {
                maxNumOfMistakes = mistakes.get(key);
                hardest.clear();
                hardest.add(String.format("%s", key));
            } else if (mistakes.get(key) == maxNumOfMistakes) {
                hardest.add(String.format("%s", key));
            }
        }
        if (hardest.isEmpty() || maxNumOfMistakes == 0) {
            output("There are no cards with errors", log);
        } else if (hardest.size() == 1) {
            output(String.format("The hardest card is \"%s\". You have %d errors answering it.",
                    hardest.get(0), maxNumOfMistakes),log);
        } else {
            output("The hardest cards are "
                    + hardest.toString().replace("[", "\"").replace("]", "\"")
                    .replace(", ", "\", \"") + ". You have "
                    + maxNumOfMistakes + " errors answering them. ", log);
        }
    }

    public static void resetStats(Map<String, Integer> mistakes) {
        for (String key : mistakes.keySet()) {
            mistakes.replace(key, 0);
        }
    }

    public static void main(String[] args) {
        Map<String, String> cards = new LinkedHashMap<>();
        Map<String, Integer> mistakes = new LinkedHashMap<>();
        List<String> log = new LinkedList<>();
        String importPath = "";
        String exportPath = "";
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals("-import")) {
                importPath = args[i + 1];
            } else if (args[i].equals("-export")) {
                exportPath = args[i + 1];
            }
        }
        if (!importPath.equals("")) {
            importFromFile(cards, log, mistakes, importPath);
        }
        while (true) {
            output("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats): ", log);
            String menu = input(log);
            switch (menu) {
                case "add":
                    addCard(cards, log, mistakes);
                    break;
                case "remove":
                    removeCard(cards, log, mistakes);
                    break;
                case "import":
                    importFromFile(cards, log, mistakes);
                    break;
                case "export":
                    exportToFile(cards, log, mistakes);
                    break;
                case "ask":
                    ask(cards, log, mistakes);
                    break;
                case "log":
                    log(log);
                    output("The log has been saved.", log);
                    break;
                case "hardest card":
                    hardestCard(mistakes, log);
                    break;
                case "reset stats":
                    resetStats(mistakes);
                    output("Card statistics has been reset.", log);
                    break;
                case "exit":
                    output("Bye bye!", log);
                    if (!exportPath.equals("")) {
                        exportToFile(cards, log, mistakes, exportPath);
                    }
                    return;
            }
        }
    }
}
