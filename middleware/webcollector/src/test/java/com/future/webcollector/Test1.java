package com.future.webcollector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Test1 {

    public void testFor(){
        ArrayList stringList = new ArrayList<>(Arrays.asList("abc", "abc", "b", "ccc", "ccc", "abc", "ddd", "ddd"));
        LinkedHashSet set =  new LinkedHashSet(stringList);
        List collect = (List) stringList.stream().distinct().collect(Collectors.toList());

    }
}
