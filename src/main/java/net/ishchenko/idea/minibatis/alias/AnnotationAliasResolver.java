package net.ishchenko.idea.minibatis.alias;


import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import net.ishchenko.idea.minibatis.annotation.Annotation;
import net.ishchenko.idea.minibatis.util.JavaUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yanglin
 */
public class AnnotationAliasResolver extends AliasResolver {

    public AnnotationAliasResolver(Project project) {
        super(project);
    }

    @NotNull
    @Override
    public Set<AliasDesc> getClassAliasDescriptions(@Nullable PsiElement element) {
        Optional<PsiClass> clazz = Annotation.ALIAS.toPsiClass(project);
        if (clazz.isPresent()) {
            Collection<PsiClass> res = AnnotatedElementsSearch.searchPsiClasses(clazz.get(), GlobalSearchScope.allScope(project)).findAll();
            return res.stream().map(psiClass -> {
                Optional<String> annotationValueText = JavaUtils.getAnnotationValueText(psiClass, Annotation.ALIAS);
                if (!annotationValueText.isPresent()) {
                    return null;
                }
                AliasDesc ad = new AliasDesc();
                ad.setAlias(annotationValueText.get());
                ad.setClazz(psiClass);
                return ad;
            }).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

}
