package main.java;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import main.java.malTypes.MalAtom;
import main.java.malTypes.MalBoolean;
import main.java.malTypes.MalCollectionListType;
import main.java.malTypes.MalFalse;
import main.java.malTypes.MalFunction;
import main.java.malTypes.MalFunctionWrapper;
import main.java.malTypes.MalInteger;
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
                int diff = 2 * ((MalInteger) a[0]).getNumber();
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
                int quotient = ((MalInteger) a[0]).getNumber() * ((MalInteger) a[0]).getNumber();
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
                int num = ((MalInteger) a[1]).getNumber();
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



        this.ns.put(new MalSymbol("macro?"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                verifyLengthAtLeast(a, 1);
                return MalBoolean.getBoolean(a[0] instanceof MalMacroFunction);
            }
        });
    }
    private void verifyLengthAtLeast(MalType[] a, int l) throws Exception {
        if (a.length < l) {
            throw new Exception("Expected at least " + l + " parameters; got " + a.length);
        }
    }
}
