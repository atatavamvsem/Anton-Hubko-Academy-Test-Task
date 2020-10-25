import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Logger;


public class Intervals {
    private static LinkedHashMap<String, Integer> notes = new LinkedHashMap<>();

    private static final Logger logger = Logger.getLogger(Intervals.class.getName());

    private static final String ORDER_ASC = "asc";
    private static final String ORDER_DSC = "dsc";

    public enum Accidentals {
        SHARP("#"),
        DBLSHARP("##"),
        FLAT("b"),
        DBLFLAT("bb"),
        NATURAL("");

        private String accidental;

        Accidentals(String accidental) {
            this.accidental = accidental;
        }

        public String getAccidental() {
            return accidental;
        }
    }

    public enum Name {
        m2(1, 2),
        M2(2, 2),
        m3(3, 3),
        M3(4, 3),
        P4(5, 4),
        P5(7, 5),
        m6(8, 6),
        M6(9, 6),
        m7(10, 7),
        M7(11, 7),
        P8(12, 8);

        public int semitones;
        public int degrees;

        Name(int semitones, int degrees) {
            this.semitones = semitones;
            this.degrees = degrees;
        }

        public int getSemitones() {
            return semitones;
        }

        public int getDegrees() {
            return degrees;
        }
    }

    private static LinkedHashMap initNotes() {
        notes.put("C", 2);
        notes.put("D", 2);
        notes.put("E", 1);
        notes.put("F", 2);
        notes.put("G", 2);
        notes.put("A", 2);
        notes.put("B", 1);
        return notes;
    }

    public static void main(String[] args) {
        try {
            Scanner scan = new Scanner(System.in);
            int x = 0;
            String s = "";

            while (!"3".equals(s)) {
                logger.info("\n 1. Interval Construction \n 2. Interval Identification \n 3. Exit ");
                s = scan.next();
                try {
                    x = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    logger.warning("Wrong!");
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String text = reader.readLine();
                String[] param = text.trim().split("\\s*,\\s*");
                try {
                    validationInput(param, x);
                    switch (x) {
                        case 1:
                            logger.info(intervalConstruction(param));
                            break;
                        case 2:
                            logger.info(intervalIdentification(param));
                            break;
                    }
                } catch (IllegalArgumentException ex) {
                    logger.warning("Interval is wrong! Please, can you put another value!");
                } catch (Exception e) {
                    logger.warning(e.getMessage());
                }
            }
            logger.info("Bye!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String intervalConstruction(String[] args) {
        String order = args.length == 2 || args[2].equals(ORDER_ASC) ? ORDER_ASC : args[2];
        Name interval = Name.valueOf(args[0]);
        String note = args[1].substring(0, 1);
        String accidentals = args[1].substring(1);
        int semitone = interval.semitones;
        int degrees = interval.degrees;
        if (order.equals(ORDER_ASC)) {
            return ascIterator(degrees, accidentals, note, semitone, order);
        }
        return dscIterator(degrees, accidentals, note, semitone, order);
    }

    private static String ascIterator(int degrees, String accidentals, String note, int semitone, String order) {
        ListIterator<Map.Entry<String, Integer>> iterator = resetIterator(order);
        Map.Entry<String, Integer> iteratorNext;
        int interval = 0;
        String startNote = null;
        String resultNote = null;
        while (degrees > 0) {
            if (iterator.hasNext()) {
                iteratorNext = iterator.next();
            } else {
                iterator = resetIterator(order);
                iteratorNext = iterator.next();
            }
            if (note.equals(iteratorNext.getKey()) && startNote == null) {
                startNote = iteratorNext.getKey();
                interval += iteratorNext.getValue();
                degrees--;
            } else if (startNote == null) {
                continue;
            } else {
                resultNote = iteratorNext.getKey();
                degrees--;
                if (degrees != 0) interval += iteratorNext.getValue();
            }
        }
        interval = checkSemitone(accidentals, order, interval);
        resultNote = checkNote(interval, semitone, resultNote, order);
        return resultNote;
    }


    private static String dscIterator(int degrees, String accidentals, String note, int semitone, String order) {
        ListIterator<Map.Entry<String, Integer>> iterator = resetIterator(order);
        Map.Entry<String, Integer> iteratorNext;
        int interval = 0;
        String startNote = null;
        String resultNote = null;
        while (degrees > 0) {
            if (iterator.hasPrevious()) {
                iteratorNext = iterator.previous();
            } else {
                iterator = resetIterator(order);
                iteratorNext = iterator.previous();
            }
            if (note.equals(iteratorNext.getKey()) && startNote == null) {
                startNote = iteratorNext.getKey();
                degrees--;
            } else if (startNote == null) {
                continue;
            } else {
                resultNote = iteratorNext.getKey();
                degrees--;
                interval += iteratorNext.getValue();
            }
        }
        interval = checkSemitone(accidentals, order, interval);
        resultNote = checkNote(interval, semitone, resultNote, order);
        return resultNote;
    }

    private static int checkSemitone(String acc, String order, int interval) {
        if (order.equals(ORDER_ASC) && acc.equals(Accidentals.SHARP)) {
            interval--;
        } else if (order.equals(ORDER_ASC) && acc.equals(Accidentals.FLAT)) {
            interval++;
        } else if (order.equals(ORDER_DSC) && acc.equals(Accidentals.SHARP)) {
            interval++;
        } else if (order.equals(ORDER_DSC) && acc.equals(Accidentals.FLAT)) {
            interval--;
        }
        return interval;
    }

    private static String checkNote(int interval, int semitone, String resultNote, String order) {
        if (interval - semitone == 2) {
            if (order.equals(ORDER_ASC)) {
                resultNote += Accidentals.DBLFLAT;
            } else {
                resultNote += Accidentals.DBLSHARP;
            }
        } else if (interval - semitone == 1) {
            if (order.equals(ORDER_ASC)) {
                resultNote += Accidentals.FLAT;
            } else {
                resultNote += Accidentals.SHARP;
            }
        } else if (interval - semitone == -1) {
            if (order.equals(ORDER_ASC)) {
                resultNote += Accidentals.SHARP;
            } else {
                resultNote += Accidentals.FLAT;
            }
        } else if (interval - semitone == -2) {
            if (order.equals(ORDER_ASC)) {
                resultNote += Accidentals.DBLSHARP;
            } else {
                resultNote += Accidentals.DBLFLAT;
            }
        }
        return resultNote;
    }

    private static ListIterator<Map.Entry<String, Integer>> resetIterator(String srt) {
        ListIterator<Map.Entry<String, Integer>> iteratorAsc = new ArrayList<Map.Entry<String, Integer>>(initNotes().entrySet()).listIterator();
        ListIterator<Map.Entry<String, Integer>> iteratorDsc = new ArrayList<Map.Entry<String, Integer>>(initNotes().entrySet()).listIterator(initNotes().size());
        return ORDER_ASC.equals(srt) ? iteratorAsc : iteratorDsc;
    }

    public static String intervalIdentification(String[] args) {
        String order = args.length == 2 || args[2].equals(ORDER_ASC) ? ORDER_ASC : args[2];
        String noteSrart = args[0].substring(0, 1);
        String noteFinish = args[1].substring(0, 1);
        String accidentalsStart = args[0].substring(1);
        String accidentalsFinish = args[1].substring(1);
        if (order.equals(ORDER_ASC)) {
            return ascIteratorInterval(noteSrart, noteFinish, order, accidentalsStart, accidentalsFinish);
        }
        return dscIteratorInterval(noteSrart, noteFinish, order, accidentalsStart, accidentalsFinish);
    }

    private static String dscIteratorInterval(String noteSrart, String noteFinish,
                                              String order, String accidentalsStart, String accidentalsFinish) {
        ListIterator<Map.Entry<String, Integer>> iterator = resetIterator(order);
        Map.Entry<String, Integer> iteratorNext;
        int interval = 0;
        int degrees = 0;
        String startNote = null;
        String resultNote = null;
        while (!noteSrart.equals(noteFinish)) {
            if (iterator.hasPrevious()) {
                iteratorNext = iterator.previous();
            } else {
                iterator = resetIterator(order);
                iteratorNext = iterator.previous();
            }
            if (noteSrart.equals(iteratorNext.getKey()) && interval == 0) {
                degrees += 1;
            } else if (degrees == 0) {
                continue;
            } else {
                noteSrart = iteratorNext.getKey();
                degrees += 1;
                interval += iteratorNext.getValue();
            }
        }
        interval = checkSemitoneDsc(interval, accidentalsStart, accidentalsFinish);
        int finalInterval = interval;
        int finalDegrees = degrees;
        Optional<Intervals.Name> nameInterval = Arrays.stream(Name.values())
                .filter(name -> name.getDegrees() == finalDegrees && name.getSemitones() == finalInterval).findFirst();
        resultNote = nameInterval.isPresent() ? nameInterval.get().toString() : "Cannot identify the interval";
        return resultNote;
    }

    private static String ascIteratorInterval(String noteSrart, String noteFinish,
                                              String order, String accidentalsStart, String accidentalsFinish) {
        ListIterator<Map.Entry<String, Integer>> iterator = resetIterator(order);
        Map.Entry<String, Integer> iteratorNext;
        int interval = 0;
        int degrees = 0;
        String startNote = null;
        String resultNote = null;
        while (!noteSrart.equals(noteFinish)) {
            if (iterator.hasNext()) {
                iteratorNext = iterator.next();
            } else {
                iterator = resetIterator(order);
                iteratorNext = iterator.next();
            }
            if (noteSrart.equals(iteratorNext.getKey()) && interval == 0) {
                degrees += 1;
                interval += iteratorNext.getValue();
            } else if (degrees == 0) {
                continue;
            } else {
                noteSrart = iteratorNext.getKey();
                degrees += 1;
                if (!noteSrart.equals(noteFinish)) interval += iteratorNext.getValue();
            }
        }
        interval = checkSemitoneAsc(interval, accidentalsStart, accidentalsFinish);
        int finalInterval = interval;
        int finalDegrees = degrees;
        Optional<Intervals.Name> nameInterval = Arrays.stream(Name.values())
                .filter(name -> name.getDegrees() == finalDegrees && name.getSemitones() == finalInterval).findFirst();
        resultNote = nameInterval.isPresent() ? nameInterval.get().toString() : "Cannot identify the interval";
        return resultNote;
    }

    private static int checkSemitoneAsc(int interval, String accidentalsStart, String accidentalsFinish) {
        if (accidentalsStart.equals(Accidentals.SHARP) || accidentalsFinish.equals(Accidentals.FLAT)) {
            interval--;
        }
        if (accidentalsStart.equals(Accidentals.FLAT) || accidentalsFinish.equals(Accidentals.SHARP)) {
            interval++;
        }
        if (accidentalsStart.equals(Accidentals.DBLSHARP) || accidentalsFinish.equals(Accidentals.DBLFLAT)) {
            interval = interval - 2;
        }
        if (accidentalsStart.equals(Accidentals.DBLFLAT) || accidentalsFinish.equals(Accidentals.DBLSHARP)) {
            interval = interval + 2;
        }
        return interval;
    }

    private static int checkSemitoneDsc(int interval, String accidentalsStart, String accidentalsFinish) {
        if (accidentalsStart.equals(Accidentals.SHARP) || accidentalsFinish.equals(Accidentals.FLAT)) {
            interval++;
        }
        if (accidentalsStart.equals(Accidentals.FLAT) || accidentalsFinish.equals(Accidentals.SHARP)) {
            interval--;
        }
        if (accidentalsStart.equals(Accidentals.DBLSHARP) || accidentalsFinish.equals(Accidentals.DBLFLAT)) {
            interval = interval + 2;
        }
        if (accidentalsStart.equals(Accidentals.DBLFLAT) || accidentalsFinish.equals(Accidentals.DBLSHARP)) {
            interval = interval - 2;
        }
        return interval;
    }

    private static void validationInput(String[] param, int menuItem) throws Exception {
        switch (menuItem) {
            case 1:
                validate(param, menuItem);
                break;
            case 2:
                validate(param, menuItem);
                break;
        }
    }

    private static void validate(String[] param, int menuItem) throws Exception {
        if (param.length < 2 || param.length > 3) {
            throw new Exception("Illegal number of elements in input array");
        }
        String order = param.length == 2 || param[2].equals(ORDER_ASC) ? ORDER_ASC : param[2];
        if (!order.equals(ORDER_DSC) && !order.equals(ORDER_ASC)) {
            throw new Exception("Order is wrong! Please, can you put another value!");
        }
        if (menuItem == 1) {
            Name interval = Name.valueOf(param[0]);
            String note = param[1].substring(0, 1);
            String accidentals = param[1].substring(1);
            Optional<Accidentals> nameAccidentals = Arrays.stream(Accidentals.values())
                    .filter(name -> name.getAccidental().equals(accidentals)).findFirst();
            if (initNotes().get(note) == null || !nameAccidentals.isPresent()
                    || nameAccidentals.get().equals(Accidentals.DBLSHARP)
                    || nameAccidentals.get().equals(Accidentals.DBLFLAT)) {
                throw new Exception("Note is wrong! Please, can you put another value!");
            }
        }
        String noteStart = param[0].substring(0, 1);
        String accidentalsStart = param[0].substring(1);
        String noteFinish = param[1].substring(0, 1);
        String accidentalsFinish = param[1].substring(1);
        Optional<Accidentals> nameAccidentalsStart = Arrays.stream(Accidentals.values())
                .filter(name -> name.getAccidental().equals(accidentalsStart)).findFirst();
        Optional<Accidentals> nameAccidentalsFinish = Arrays.stream(Accidentals.values()
        ).filter(name -> name.getAccidental().equals(accidentalsFinish)).findFirst();
        if (initNotes().get(noteStart) == null || !nameAccidentalsStart.isPresent()
                || !nameAccidentalsFinish.isPresent() || initNotes().get(noteFinish) == null) {
            throw new Exception("Note is wrong! Please, can you put another value!");
        }
    }

}
