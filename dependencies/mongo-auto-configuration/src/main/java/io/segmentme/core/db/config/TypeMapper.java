package io.segmentme.core.db.config;

import io.segmentme.core.domain.DbDomain;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.convert.TypeInformationMapper;
import org.springframework.data.mapping.Alias;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
class TypeMapper implements TypeInformationMapper {

    private final Map<? extends TypeInformation<?>, Alias> typeToAliasMap;
    private final Map<Alias, TypeInformation<?>> aliasToTypeMap;

    TypeMapper(List<String> basePackagesToScan) {
        typeToAliasMap = resolveTypes(basePackagesToScan);
        aliasToTypeMap = typeToAliasMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    private Map<? extends TypeInformation<?>, Alias> resolveTypes(List<String> basePackagesToScan) {
        var scanner = getScanner(new AssignableTypeFilter(DbDomain.class));

        return basePackagesToScan.stream()
                .map(scanner::findCandidateComponents)
                .flatMap(Collection::stream)
                .distinct()
                .map(this::loadClass)
                .distinct()
                .collect(Collectors.toMap(ClassTypeInformation::from, this::getAliasName));
    }

    @Override
    public TypeInformation<?> resolveTypeFrom(Alias alias) {
        return aliasToTypeMap.get(alias);
    }

    @Override
    public Alias createAliasFor(TypeInformation<?> type) {
        return typeToAliasMap.get(type);
    }

    private Alias getAliasName(Class<?> clazz) {
        return Alias.of(Optional.ofNullable(AnnotationUtils.getAnnotation(clazz, TypeAlias.class))
                .map(TypeAlias::value)
                .filter(StringUtils::isNotBlank)
                .orElseGet(clazz::getSimpleName));
    }

    @SneakyThrows
    private Class<?> loadClass(BeanDefinition definition) {
        return Class.forName(definition.getBeanClassName());
    }

    private ClassPathScanningCandidateComponentProvider getScanner(TypeFilter filter) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return super.isCandidateComponent(beanDefinition) || beanDefinition.getMetadata().isAbstract();
            }
        };
        scanner.addIncludeFilter(filter);
        return scanner;
    }
}
