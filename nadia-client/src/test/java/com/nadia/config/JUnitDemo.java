package com.nadia.config;

import com.nadia.config.listener.enumerate.EventType;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JUnitDemo extends A {

    private String name;
    private int age;

//    @Test
    public void test() {
        final List<Field> res = new LinkedList<>();
        ReflectionUtils.doWithFields(new JUnitDemo().getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                res.add(field);
            }
        });
        System.out.println(res);
    }

//    @Test
    public void test1() {
        String s = "#{new java.text.SimpleDateFormat('${some.key}').parse('${another.key}')}";
        String r = ".*[\\$|#]\\{(.*)[\\}|:].*";
        Pattern compile = Pattern.compile(r);
        Matcher matcher = compile.matcher(s);
        System.out.println(matcher.group(1));
    }


//    @Test
    public void test2() {
        String s = "${a.b},${d.e}";
        String r = ".*\\$\\{(.*)\\}.*";
        Pattern compile = Pattern.compile(r);
        Matcher matcher = compile.matcher(s);
        System.out.println(matcher.group(0));
    }

//    @Test
    public void test3() {
        Class switch_instance = EventType.getClass("SWITCH_INSTANCE");
        System.out.println(switch_instance);
    }

}
