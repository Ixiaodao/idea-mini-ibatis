package net.ishchenko.idea.minibatis.alias;


import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author yanglin
 */
public class AliasFacade {

    private Project project;

    private JavaPsiFacade javaPsiFacade;

    private List<AliasResolver> resolvers;

    public static final AliasFacade getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, AliasFacade.class);
    }

    public AliasFacade(Project project) {
        this.project = project;
        this.resolvers = Lists.newArrayList();
        this.javaPsiFacade = JavaPsiFacade.getInstance(project);
        initResolvers();
    }

    private void initResolvers() {
        this.registerResolver(new AnnotationAliasResolver(project));
        this.registerResolver(new InnerAliasResolver(project));
    }

    public PsiClass findPsiClass(@Nullable PsiElement element, @NotNull String shortName) {
        PsiClass clazz = javaPsiFacade.findClass(shortName, GlobalSearchScope.allScope(project));
        if (null != clazz) {
            return clazz;
        }
        for (AliasResolver resolver : resolvers) {
            for (AliasDesc desc : resolver.getClassAliasDescriptions(element)) {
                if (shortName.equalsIgnoreCase(desc.getAlias())) {
                    return desc.getClazz();
                }
            }
        }
        return null;
    }

    @NotNull
    public Collection<AliasDesc> getAliasDescs(@Nullable PsiElement element) {
        ArrayList<AliasDesc> result = Lists.newArrayList();
        for (AliasResolver resolver : resolvers) {
            result.addAll(resolver.getClassAliasDescriptions(element));
        }
        return result;
    }

    public AliasDesc findAliasDesc(@Nullable PsiClass clazz) {
        if (clazz == null) {
            return null;
        }
        for (AliasResolver resolver : resolvers) {
            for (AliasDesc desc : resolver.getClassAliasDescriptions(clazz)) {
                if (clazz.equals(desc.getClazz())) {
                    return desc;
                }
            }
        }
        return null;
    }

    public void registerResolver(@NotNull AliasResolver resolver) {
        this.resolvers.add(resolver);
    }

}
