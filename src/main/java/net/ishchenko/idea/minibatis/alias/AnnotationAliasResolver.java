package net.ishchenko.idea.minibatis.alias;


import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import net.ishchenko.idea.minibatis.util.JavaUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author yanglin
 */
public class AnnotationAliasResolver extends AliasResolver {

    public static final String IBATIS_ALIAS_CONFIG = "com.zuche.framework.annotation.IbatisAliasConfig";

    public AnnotationAliasResolver(Project project) {
        super(project);
    }

    @NotNull
    @Override
    public Set<AliasDesc> getClassAliasDescriptions(@Nullable PsiElement element) {
        GlobalSearchScope globalSearchScope = GlobalSearchScope.allScope(project);
        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(IBATIS_ALIAS_CONFIG, globalSearchScope);
        if (psiClass == null) {
            return Collections.emptySet();
        }
        Collection<PsiClass> list = AnnotatedElementsSearch.searchPsiClasses(psiClass, globalSearchScope).findAll();
        Set<AliasDesc> set = new HashSet<>();
        for (PsiClass clazz : list) {
            String valueText = JavaUtils.getAnnotationValueText(clazz, IBATIS_ALIAS_CONFIG);
            if (StringUtils.isNotEmpty(valueText)) {
                AliasDesc ad = new AliasDesc();
                ad.setAlias(valueText);
                ad.setClazz(clazz);
                set.add(ad);
            }
        }
        return set;
    }

}
