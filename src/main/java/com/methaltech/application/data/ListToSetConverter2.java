
package com.methaltech.application.data;


import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
@Component
public class ListToSetConverter2<T> implements Converter<Set<T>, List<T>> {

    @Override
    public Result<List<T>> convertToModel(Set<T> set, ValueContext valueContext) {
        return Result.ok(Collections.unmodifiableList(List.copyOf(set)));
    }

    @Override
    public Set<T> convertToPresentation(List<T> list, ValueContext valueContext) {
        return new HashSet<>(list);
    }
}
