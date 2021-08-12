package app.bestseller.starbux.domain.util;

import app.bestseller.starbux.domain.UserEntity;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

public class SetAuthorityConverter implements AttributeConverter<Set<UserEntity.Authority>, String> {
    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(Set<UserEntity.Authority> stringList) {
        return stringList != null ? String.join(SPLIT_CHAR, stringList.stream().map(Enum::name).collect(Collectors.toSet())) : "";
    }

    @Override
    public Set<UserEntity.Authority> convertToEntityAttribute(String string) {
        return string != null ? Set.of(string.split(SPLIT_CHAR)).stream().map(UserEntity.Authority::valueOf).collect(Collectors.toSet()) : emptySet();
    }
}