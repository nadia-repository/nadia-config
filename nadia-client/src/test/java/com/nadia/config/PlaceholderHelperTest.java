package com.nadia.config;

import com.nadia.config.utils.PlaceholderHelper;

import java.util.Set;

public class PlaceholderHelperTest {


    public void testExtractPlaceholderKeys() {
        Set<String> strings = PlaceholderHelper.extractPlaceholderKeys("#{new java.text.SimpleDateFormat('${some.key}').parse('${another.key}')}");
        System.out.println(strings);
    }
}
