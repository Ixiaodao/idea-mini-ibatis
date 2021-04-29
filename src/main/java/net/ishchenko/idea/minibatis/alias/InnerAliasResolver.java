package net.ishchenko.idea.minibatis.alias;


import com.google.common.collect.ImmutableSet;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import net.ishchenko.idea.minibatis.util.JavaUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author yanglin
 */
public class InnerAliasResolver extends AliasResolver {

    private final Set<AliasDesc> innerAliasDescList = ImmutableSet.of(
            AliasDesc.create(JavaUtils.findClazz(project, "java.lang.String"), "string"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Byte"), "byte"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Long"), "long"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Short"), "short"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Integer"), "int"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Integer"), "integer"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Double"), "double"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Float"), "float"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Boolean"), "boolean"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.util.Date"), "date"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.math.BigDecimal"), "decimal"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.lang.Object"), "object"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.util.Map"), "map"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.util.HashMap"), "hashmap"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.util.List"), "list"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.util.ArrayList"), "arraylist"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.util.Collection"), "collection"),
            AliasDesc.create(JavaUtils.findClazz(project, "java.util.Iterator"), "iterator")
    );

    public InnerAliasResolver(Project project) {
        super(project);
    }

    @NotNull
    @Override
    public Set<AliasDesc> getClassAliasDescriptions(@Nullable PsiElement element) {
        return innerAliasDescList;
    }

}
