package main.java;
import java.util.HashMap;
import java.util.Map;

import main.java.malTypes.MalBoolean;
import main.java.malTypes.MalFalse;
import main.java.malTypes.MalFunction;
import main.java.malTypes.MalInteger;
import main.java.malTypes.MalList;
import main.java.malTypes.MalNil;
import main.java.malTypes.MalString;
import main.java.malTypes.MalSymbol;
import main.java.malTypes.MalTrue;
import main.java.malTypes.MalType;

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
                if (!(a[0] instanceof MalList)) {
                    throw new Exception(LIST_ERROR);
                }
                return MalBoolean.getBoolean(((MalList) a[0]).getCollection().isEmpty());
            }
        });
        this.ns.put(new MalSymbol("count"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                if (a[0] instanceof MalNil) {
                    // Really strange behaviour specified by the tests
                    return new MalInteger(0);
                }
                if (!(a[0] instanceof MalList)) {
                    throw new Exception(LIST_ERROR);
                }
                return new MalInteger(((MalList) a[0]).getCollection().size());
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
                    return new MalString(a[0].toString());
                }
                StringBuilder b = new StringBuilder();
                b.append("\"");
                for (int i = 0; i < a.length - 1; i++) {
                    b.append(Printer.pr_str(a[i], false));
                }
                b.append(Printer.pr_str(a[a.length-1], false));
                b.append("\"");
                MalString s = new MalString(b.toString());
                // MalString s = new MalString(MalString.getReadable(b.toString()));
                return s;
            }
        });
        this.ns.put(new MalSymbol("prn"), new MalFunction() {
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
                System.out.println(b.toString());
                return new MalNil();
            }
        });
        this.ns.put(new MalSymbol("println"), new MalFunction() {
            @Override
            public MalType operate(MalType[] a) throws Exception {
                if (a.length == 0) {
                    return new MalString("");
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
    }
    private void verifyLengthAtLeast(MalType[] a, int l) throws Exception {
        if (a.length < l) {
            throw new Exception("Expected at least " + l + " parameters; got " + a.length);
        }
    }
}
