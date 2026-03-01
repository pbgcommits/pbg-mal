package main.java;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import main.java.malTypes.MalAtom;
import main.java.malTypes.MalBoolean;
import main.java.malTypes.MalCollectionListType;
import main.java.malTypes.MalFalse;
import main.java.malTypes.MalFunction;
import main.java.malTypes.MalFunctionWrapper;
import main.java.malTypes.MalHashMap;
import main.java.malTypes.MalHashMapKey;
import main.java.malTypes.MalInteger;
import main.java.malTypes.MalKeyword;
import main.java.malTypes.MalList;
import main.java.malTypes.MalMacroFunction;
import main.java.malTypes.MalNil;
import main.java.malTypes.MalString;
import main.java.malTypes.MalSymbol;
import main.java.malTypes.MalTrue;
import main.java.malTypes.MalType;
import main.java.malTypes.MalVector;

public class Core {
    private Map<MalSymbol, MalFunction> ns = new HashMap<>();
    private final String LIST_ERROR = "Expected list";
    public Map<MalSymbol, MalFunction> getNameSpace() {
        return this.ns;
    }
    public Core() {
        this.ns.put(new MalSymbol("+"), new MalFunction() {
            @Override
            public MalInteger operate(MalType[] a) {
                int sum = 0;
                for (MalType i : a) {
                    sum += ((MalInteger) i).getNumber();
                }
                return new MalInteger(sum);
            }
        });
        this.ns.put(new MalSymbol("-"), new MalFunction() {
            @Override
            public MalInteger operate(MalType[] a) {
                long diff = 2 * ((MalInteger) a[0]).getNumber();
                for (MalType i : a) {
                    diff -= ((MalInteger) i).getNumber();
                }
                return new MalInteger(diff);
            }
        });
        this.ns.put(new MalSymbol("*"), new MalFunction() {
            @Override
            public MalInteger operate(MalType[] a) {
                int product = 1;
                for (MalType i : a) {
                    product *= ((MalInteger) i).getNumber();
                }
                return new MalInteger(product);
            }
        });
        this.ns.put(new MalSymbol("/"), new MalFunction() {
            @Override
            public MalInteger operate(MalType[] a) {
                long quotient = ((MalInteger) a[0]).getNumber() * ((MalInteger) a[0]).getNumber();
                for (MalType i : a) {
                    quotient /= ((MalInteger) i).getNumber();
                }
                return new MalInteger(quotient);
            }
        });
        this.ns.put(new MalSymbol("list"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                MalList l = new MalList();
                for (MalType x : a) {
                    l.add(x);
                }
                return l;
            }
        });
        this.ns.put(new MalSymbol("list?"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                if (a[0] instanceof MalList) {
                    return new MalTrue();
                } else {
                    return new MalFalse();
                }
            }
        });
        this.ns.put(new MalSymbol("empty?"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                if (!(a[0] instanceof MalCollectionListType)) {
                    throw new Exception(LIST_ERROR);
                }
                return MalBoolean.getBoolean(((MalCollectionListType) a[0]).getCollection().isEmpty());
            }
        });
        this.ns.put(new MalSymbol("count"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                if (a[0] instanceof MalNil) {
                    // Really strange behaviour specified by the tests
                    return new MalInteger(0);
                }
                if (!(a[0] instanceof MalCollectionListType)) {
                    throw new Exception(LIST_ERROR);
                }
                return new MalInteger(((MalCollectionListType) a[0]).getCollection().size());
            }
        });
        this.ns.put(new MalSymbol("="), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 2);
                return MalBoolean.getBoolean(a[0].equals(a[1]));
            }
        });
        this.ns.put(new MalSymbol("<"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 2);
                MalInteger first = (MalInteger) a[0];
                MalInteger second = (MalInteger) a[1];
                return MalBoolean.getBoolean(first.getNumber() < second.getNumber());
            }
        });
        this.ns.put(new MalSymbol(">"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 2);
                MalInteger first = (MalInteger) a[0];
                MalInteger second = (MalInteger) a[1];
                return MalBoolean.getBoolean(first.getNumber() > second.getNumber());
            }
        });
        this.ns.put(new MalSymbol("<="), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 2);
                MalInteger first = (MalInteger) a[0];
                MalInteger second = (MalInteger) a[1];
                return MalBoolean.getBoolean(first.getNumber() <= second.getNumber());
            }
        });
        this.ns.put(new MalSymbol(">="), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 2);
                MalInteger first = (MalInteger) a[0];
                MalInteger second = (MalInteger) a[1];
                return MalBoolean.getBoolean(first.getNumber() >= second.getNumber());
            }
        });
        this.ns.put(new MalSymbol("pr-str"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                if (a.length == 0) {
                    return new MalString("");
                }
                StringBuilder b = new StringBuilder();
                for (int i = 0; i < a.length - 1; i++) {
                    b.append(Printer.pr_str(a[i], true));
                    b.append(" ");
                }
                b.append(Printer.pr_str(a[a.length-1], true));
                return new MalString(MalString.getReadable(b.toString()));
            }
        });
        this.ns.put(new MalSymbol("str"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                if (a.length == 0) {
                    return new MalString("");
                }
                if (a.length == 1) {
                    if (a[0] instanceof MalString) {
                        return new MalString(a[0].toString());
                    }
                    return new MalString(a[0].toString(), false);
                }
                StringBuilder b = new StringBuilder();
                b.append("\"");
                for (int i = 0; i < a.length - 1; i++) {
                    b.append(Printer.pr_str(a[i], false));
                }
                b.append(Printer.pr_str(a[a.length-1], false));
                b.append("\"");
                MalString s = new MalString(b.toString());
                return s;
            }
        });
        this.ns.put(new MalSymbol("prn"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                if (a.length == 0) {
                    System.out.println();
                    return new MalNil();
                }
                StringBuilder b = new StringBuilder();
                for (int i = 0; i < a.length - 1; i++) {
                    b.append(Printer.pr_str(a[i], true));
                    b.append(" ");
                }
                b.append(Printer.pr_str(a[a.length-1], true));
                System.out.println(b.toString());
                return new MalNil();
            }
        });
        this.ns.put(new MalSymbol("println"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                if (a.length == 0) {
                    System.out.println();
                    return new MalNil();
                }
                StringBuilder b = new StringBuilder();
                for (int i = 0; i < a.length - 1; i++) {
                    b.append(Printer.pr_str(a[i], false));
                    b.append(" ");
                }
                b.append(Printer.pr_str(a[a.length-1], false));
                System.out.println(b.toString());
                return new MalNil();
            }
        });
        this.ns.put(new MalSymbol("read-string"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                String read = a[0].toString(false);
                // Remove surrounding quotes
                MalType x = Reader.readStr(read);
                return x;
            }
        });
        this.ns.put(new MalSymbol("slurp"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                Path p = Path.of(a[0].toString(false));
                try {
                    String s = Files.readString(p, StandardCharsets.UTF_8);
                    return new MalString(s, false);
                } catch (IOException e) {
                    throw new Exception("IOException: " + e.getMessage());
                }
            }
        });
        this.ns.put(new MalSymbol("atom"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                return new MalAtom(a[0]);
            }
        });
        this.ns.put(new MalSymbol("atom?"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                return MalBoolean.getBoolean(a[0] instanceof MalAtom);
            }
        });
        this.ns.put(new MalSymbol("deref"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                if (!(a[0] instanceof MalAtom)) {
                    throw new Exception("Expected atom");
                }
                return ((MalAtom) a[0]).getValue();
            }
        });
        this.ns.put(new MalSymbol("reset!"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 2);
                if (!(a[0] instanceof MalAtom)) {
                    throw new Exception("Expected atom");
                }
                MalAtom atom = (MalAtom) a[0];
                atom.setValue(a[1]);
                return a[1];
            }
        });
        this.ns.put(new MalSymbol("swap!"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 2);
                if (!(a[0] instanceof MalAtom)) { 
                    throw new Exception("Expected atom");
                }
                if (!(a[1] instanceof MalFunction) && !(a[1] instanceof MalFunctionWrapper)) {
                    throw new Exception("Expected function");
                }
                MalAtom atom = (MalAtom) a[0];
                MalType[] args = Arrays.copyOfRange(a, 1, a.length);
                args[0] = atom.getValue();
                MalFunction function;
                if (a[1] instanceof MalFunctionWrapper) {
                    function = ((MalFunctionWrapper) a[1]).getFn();
                } else {
                    function = (MalFunction) a[1];
                }
                MalType result = function.operate(args);
                atom.setValue(result);
                return result;
            } 
        });
        this.ns.put(new MalSymbol("cons"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 2);
                if (!(a[1] instanceof MalCollectionListType)) {
                    throw new Exception(LIST_ERROR);
                }
                MalList list = new MalList();
                list.add(a[0]);
                for (MalType x : ((MalCollectionListType) a[1]).getCollection()) {
                    list.add(x);
                }
                return list;
            }
        });
        this.ns.put(new MalSymbol("concat"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                MalList list = new MalList();
                for (MalType t : a) {
                    if (!(t instanceof MalCollectionListType)) {
                        throw new Exception(LIST_ERROR);
                    }
                    for (MalType t1 : ((MalCollectionListType) t).getCollection()) {
                        list.add(t1);
                    }
                }
                return list;
            }
        });
        this.ns.put(new MalSymbol("vec"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                if (a[0] instanceof MalVector)  {
                    return a[0];
                } else if (a[0] instanceof MalList) {
                    MalVector v = new MalVector();
                    for (MalType t : ((MalList) a[0]).getCollection()) {
                        v.add(t);
                    }
                    return v;
                } else {
                    throw new Exception(LIST_ERROR);
                }
            }
        });
        this.ns.put(new MalSymbol("nth"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 2);
                if (!(a[0] instanceof MalCollectionListType)) {
                    throw new Exception("Expected list/vector; got " + a[0].getClass());
                }
                if (!(a[1] instanceof MalInteger)) {
                    throw new Exception("Expected integer; got " + a[1].getClass());
                }
                MalCollectionListType list = (MalCollectionListType) a[0];
                int num = (int) ((MalInteger) a[1]).getNumber();
                if (list.size() < num + 1) {
                    throw new Exception("Index " + num + " out of range");
                }
                return list.get(num);
            }
        });
        this.ns.put(new MalSymbol("first"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                if (a[0] instanceof MalNil) {
                    return new MalNil();
                }
                if (!(a[0] instanceof MalCollectionListType)) {
                    throw new Exception("Expected list/vector; got " + a[0].getClass());
                }
                MalCollectionListType list = (MalCollectionListType) a[0];
                if (list.size() < 1) {
                    return new MalNil();
                }
                return list.get(0);
            }
        });
        this.ns.put(new MalSymbol("rest"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                if (a[0] instanceof MalNil) {
                    return new MalList();
                }
                if (!(a[0] instanceof MalCollectionListType)) {
                    throw new Exception("Expected list/vector; got " + a[0].getClass());
                }
                MalCollectionListType list = (MalCollectionListType) a[0];
                MalList newList = new MalList();
                for (int i = 1; i < list.size(); i++) {
                    newList.add(list.get(i));
                }
                return newList;
            }
        });
        this.ns.put(new MalSymbol("throw"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                throw new Exception(a[0].toString(false));
            }
        });
        this.ns.put(new MalSymbol("apply"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 2);
                MalFunction function;
                if (a[0] instanceof MalFunction) {
                    function = (MalFunction) a[0];
                } else if (a[0] instanceof MalFunctionWrapper) {
                    function = ((MalFunctionWrapper) a[0]).getFn();
                } else if (a[0] instanceof MalMacroFunction) {
                    function = ((MalMacroFunction) a[0]).getFunction();
                } else {
                    throw new Exception("Expected arg 1 to be function, got " + a[0].getClass());
                }
                if (!(a[a.length-1] instanceof MalCollectionListType)) {
                    throw new Exception("Expected arg 2 to be list/vector, got " + a[a.length - 1].getClass());
                }
                MalCollectionListType last = (MalCollectionListType) a[a.length - 1];
                MalList list = new MalList();
                for (int i = 1; i < a.length - 1; i++) {
                    list.add(a[i]);
                }
                for (MalType t : last.getCollection()) {
                    list.add(t);
                }
                return function.operate(list.getCollection().toArray(new MalType[0]));
            }
        });
        this.ns.put(new MalSymbol("map"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 2);
                MalFunction function;
                if (a[0] instanceof MalFunction) {
                    function = (MalFunction) a[0];
                } else if (a[0] instanceof MalFunctionWrapper) {
                    function = ((MalFunctionWrapper) a[0]).getFn();
                } else if (a[0] instanceof MalMacroFunction) {
                    function = ((MalMacroFunction) a[0]).getFunction();
                } else {
                    throw new Exception("Expected arg 1 to be function, got " + a[0].getClass());
                }
                if (!(a[1] instanceof MalCollectionListType)) {
                    throw new Exception("Expected arg 2 to be list/vector, got " + a[a.length - 1].getClass());
                }
                MalList list = new MalList();
                ((MalCollectionListType) a[1]).getCollection().forEach((t) -> { 
                    try {
                        MalType[] x = {t};
                        list.add(function.operate(x)); 
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                });;
                return list;
            }
        });
        this.ns.put(new MalSymbol("nil?"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                return MalBoolean.getBoolean(a[0] instanceof MalNil);
            }
        });
        this.ns.put(new MalSymbol("true?"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                return MalBoolean.getBoolean(a[0] instanceof MalTrue);
            }
        });
        this.ns.put(new MalSymbol("false?"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                return MalBoolean.getBoolean(a[0] instanceof MalFalse);
            }
        });
        this.ns.put(new MalSymbol("symbol?"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                return MalBoolean.getBoolean(a[0] instanceof MalSymbol);
            }
        });
        this.ns.put(new MalSymbol("symbol"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                if (!(a[0] instanceof MalString)) {
                    throw new Exception("Expected string, got " + a[0].getClass());
                }
                return new MalSymbol(((MalString) a[0]).toString(false));
            }
        });
        this.ns.put(new MalSymbol("keyword"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                if (a[0] instanceof MalKeyword) {
                    return a[0];
                }
                if (!(a[0] instanceof MalString)) {
                    throw new Exception("Expected string, got " + a[0].getClass());
                }
                return new MalKeyword(MalKeyword.KEYWORD_START + ((MalString) a[0]).toString(false));
            }
        });
        this.ns.put(new MalSymbol("keyword?"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                return MalBoolean.getBoolean(a[0] instanceof MalKeyword);
            }
        });
        this.ns.put(new MalSymbol("vector"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                MalVector v = new MalVector();
                for (MalType t : a) {
                    v.add(t);
                }
                return v;
            }
        });
        this.ns.put(new MalSymbol("vector?"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                return MalBoolean.getBoolean(a[0] instanceof MalVector);
            }
        });
        this.ns.put(new MalSymbol("sequential?"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                return MalBoolean.getBoolean(a[0] instanceof MalCollectionListType);
            }
        });
        this.ns.put(new MalSymbol("hash-map"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                if (a.length % 2 != 0) {
                    throw new Exception("Expected even num of args");
                }
                MalHashMap map = new MalHashMap();
                for (MalType t : a) {
                    map.add(t);
                }
                return map;
            }
        });
        this.ns.put(new MalSymbol("map?"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                return MalBoolean.getBoolean(a[0] instanceof MalHashMap);
            }
        });
        this.ns.put(new MalSymbol("assoc"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                if (!(a[0] instanceof MalHashMap)) {
                    throw new Exception("Expected hashmap; got " + a[0].getClass());
                }
                MalHashMap original = (MalHashMap) a[0];
                MalHashMap map = new MalHashMap();
                for (MalHashMapKey key : original.getCollection().keySet()) {
                    map.put(key, original.getCollection().get(key));
                }
                for (int i = 1; i < a.length; i++) {
                    map.add(a[i]);
                }
                if (a.length % 2 != 1) {
                    map.add(new MalNil());
                }
                return map;
            }
        });
        this.ns.put(new MalSymbol("dissoc"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                if (!(a[0] instanceof MalHashMap)) {
                    return a[0];
                }
                MalHashMap map = new MalHashMap();
                MalHashMap original = (MalHashMap) a[0];
                for (MalHashMapKey key : original.getCollection().keySet()) {
                    map.put(key, original.getCollection().get(key));
                }
                for (int i = 1; i < a.length; i++) {
                    if (!(a[i] instanceof MalHashMapKey)) {
                        throw new Exception("Expected " + a[i] + " to be HashMapKey");
                    }
                    map.getCollection().remove(a[i]);
                }
                return map;
            }
        });
        this.ns.put(new MalSymbol("get"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                if (a[0] instanceof MalNil) {
                    return new MalNil();
                }
                if (!(a[0] instanceof MalHashMap)) {
                    throw new Exception("Expected hashmap, got " + a[0].getClass());
                }
                if (a.length < 2 || !(a[1] instanceof MalHashMapKey)) {
                    return new MalNil();
                }
                MalType value = ((MalHashMap) a[0]).getCollection().get(a[1]);
                if (value == null) {
                    return new MalNil();
                } else {
                    return value;
                }
            }
        });
        this.ns.put(new MalSymbol("contains?"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                if (!(a[0] instanceof MalHashMap)) {
                    throw new Exception("Expected hashmap, got " + a[0].getClass());
                }
                if (a.length < 2 || !(a[1] instanceof MalHashMapKey)) {
                    return new MalFalse();
                }
                return MalBoolean.getBoolean(((MalHashMap) a[0]).getCollection().containsKey(a[1]));
            }
        });
        this.ns.put(new MalSymbol("keys"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                if (!(a[0] instanceof MalHashMap)) {
                    throw new Exception("Expected hashmap, got " + a[0].getClass());
                }
                MalList list = new MalList();
                for (MalHashMapKey key : ((MalHashMap) a[0]).getCollection().keySet()) {
                    list.add(key);
                }
                return list;
            }
        });
        this.ns.put(new MalSymbol("vals"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                if (!(a[0] instanceof MalHashMap)) {
                    throw new Exception("Expected hashmap, got " + a[0].getClass());
                }
                MalList list = new MalList();
                for (MalType value : ((MalHashMap) a[0]).getCollection().values()) {
                    list.add(value);
                }
                return list;
            }
        });
        this.ns.put(new MalSymbol("meta"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                return a[0].getMetadata();
            }
        });
        this.ns.put(new MalSymbol("with-meta"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                if (a.length < 2) {
                    return a[0];
                }
                a[0].setMetadata(a[1]);
                return a[0];
            }
        });
        this.ns.put(new MalSymbol("time-ms"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                return new MalInteger(Instant.now().toEpochMilli());
            }
        });
        this.ns.put(new MalSymbol("conj"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                if (a[0] instanceof MalList) {
                    MalList list = new MalList();
                    for (int i = a.length - 1; i >= 1; i--) {
                        list.add(a[i]);
                    }
                    MalList og = (MalList) a[0];
                    for (MalType item : og.getCollection()) {
                        list.add(item);
                    }
                    return list;
                } else if (a[0] instanceof MalVector) {
                    MalVector og = (MalVector) a[0];
                    MalVector v = new MalVector();
                    for (MalType item : og.getCollection()) {
                        v.add(item);
                    }
                    for (int i = 1; i < a.length; i++) {
                        v.add(a[i]);
                    }
                    return v;
                } else {
                    throw new Exception("Excepted list/vector, got " + a[0].getClass());
                }
            }
        });
        this.ns.put(new MalSymbol("string?"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                return MalBoolean.getBoolean(a[0] instanceof MalString);
            }
        });
        this.ns.put(new MalSymbol("number?"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                return MalBoolean.getBoolean(a[0] instanceof MalInteger);
            }
        });
        this.ns.put(new MalSymbol("fn?"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                return MalBoolean.getBoolean(a[0] instanceof MalFunction || a[0] instanceof MalFunctionWrapper);
            }
        });
        this.ns.put(new MalSymbol("macro?"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                return MalBoolean.getBoolean(a[0] instanceof MalMacroFunction);
            }
        });
        this.ns.put(new MalSymbol("seq"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                if (a[0] instanceof MalNil) {
                    return new MalNil();
                } else if (a[0] instanceof MalList) {
                    MalList og = (MalList) a[0];
                    MalList list = new MalList();
                    for (MalType item : og.getCollection()) {
                        list.add(item);
                    }
                    return list;
                } else if (a[0] instanceof MalVector) {
                    MalVector og = (MalVector) a[0];
                    MalList list = new MalList();
                    for (MalType item : og.getCollection()) {
                        list.add(item);
                    }
                    return list;
                } else if (a[0] instanceof MalString) {
                    char[] chars = ((MalString) a[0]).toString(false).toCharArray();
                    MalList charList = new MalList();
                    for (char c : chars) {
                        charList.add(new MalString(String.valueOf(c)));
                    }
                    return charList;
                } else {
                    throw new Exception("Expected list, vector, string, or nil");
                }
            }
        });
    }
    private void verifyLengthAtLeast(MalType[] a, int l) throws Exception {
        if (a.length < l) {
            throw new Exception("Expected at least " + l + " parameters; got " + a.length);
        }
    }
}
