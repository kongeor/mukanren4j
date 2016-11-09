package io.github.kongeor.mukanren4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

public class Core {

    public static boolean isLvar(Object o) {
        return o instanceof Lvar;
    }

    public static Object walk(Map<Object, Object> s, Object u) {
        Object pr = s.get(u);
        if (pr != null) {
            if (isLvar(pr)) {
                return walk(s, pr);
            }
            return pr;
        }
        return u;
    }

    public static Map<Object, Object> unify(Map<Object, Object> s, Object u, Object v) {
        Object u1 = walk(s, u);
        Object v1 = walk(s, v);

        if (isLvar(u1) && isLvar(v1) && u1.equals(v1)) {
            return s;
        } else if (isLvar(u1)) {
            s.put(u1, v1);
            return s;
        } else if (isLvar(v1)) {
            s.put(v1, u1);
            return s;
        } else if (u1.equals(v1)) {
            return s;
        }
        return null;
    }
//
//    public static Function<Map<Object, Object>, Stream<Map<Object, Object>>> conj(Object a) {
//        return s -> Stream.of(a);
//    }
//
//    public static Function<Map<Object, Object>, Stream<Map<Object, Object>>> conj(Object... objs) {
//        return s -> {
//            Arrays.stream(objs)
//                    .reduce()
//                    .reduce(s, (u, v) ->
//                            u.get(v));
//        }
//    }

    public static Function<Map<Object, Object>, Stream<Map<Object, Object>>> equal(Object a, Object b) {
        return s -> {
            Map<Object, Object> v = unify(s, a, b);
            if (v != null) {
                return Stream.of(v);
            } else {
                return Stream.empty();
            }
        };
    }

    public static void main(String[] args) {

        Lvar s = new Lvar("s");
        Lvar u = new Lvar("s");

        Map<Object, Object> sm = mapOf(s, u, u, 42);
        Map<Object, Object> es = new HashMap<>();

        System.out.println(equal(s, 2).apply(es).collect(toList()));
//        System.out.println(walk(sm, s));

        System.out.println(unify(sm, s, u));
//        System.out.println(unify(Collections.emptyMap(), 1, 1));
    }

    public static Map<Object, Object> mapOf(Object... objs) {
        assert objs.length % 2 == 0;

        Map<Object, Object> s = new HashMap<>();

        for (int i=0; i<objs.length; i+=2) {
            s.put(objs[i], objs[i+1]);
        }

        return s;
    }

    public static AtomicLong id = new AtomicLong(1000);

    public static class Lvar {

        private final String name;

        public Lvar() {
            this("");
        }

        public Lvar(String name) {
            this.name = name + "_" + id.getAndIncrement();
        }

        @Override
        public String toString() {
            return name;
        }
    }


}
